package uk.gov.hmcts.reform.pcqloader;

import com.azure.storage.blob.BlobContainerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.hmcts.reform.pcq.commons.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcqloader.exceptions.ExternalApiException;
import uk.gov.hmcts.reform.pcqloader.helper.PayloadMappingHelper;
import uk.gov.hmcts.reform.pcqloader.services.BlobStorageManager;
import uk.gov.hmcts.reform.pcqloader.services.PcqBackendService;
import uk.gov.hmcts.reform.pcqloader.utils.ZipFileUtils;
import uk.gov.hmcts.reform.pcqloader.utils.PcqLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class PcqLoaderComponent {

    private static final int MAX_RETRIES = 3;

    @Value("${apiExecutionThreadDelay:1000}")
    private int threadDelay;

    @Autowired
    private BlobStorageManager blobStorageManager;

    @Autowired
    private PayloadMappingHelper payloadMappingHelper;

    @Autowired
    private PcqBackendService pcqBackendService;

    @Autowired
    private ZipFileUtils fileUtil;


    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis"})
    public void execute() {
        log.info("PcqLoaderComponent started.");

        // Step 1. Connect and Authenticate with the PCQ Azure Blob Storage Account.
        BlobContainerClient blobContainerClient = blobStorageManager.getPcqContainer();
        Assert.isTrue(blobContainerClient.exists(), "Can't connect to Blob Storage.");

        // Step 2. Check for zip files in the Pcq container.
        List<String> blobZipNamesList = blobStorageManager.collectBlobFileNamesFromContainer(blobContainerClient);
        for (String tmpZipFileName : blobZipNamesList) {

            File blobZipDirectory = null;
            File unzippedFiles = null;
            try {
                // Step 4. Download the zip file to local storage.
                blobZipDirectory = blobStorageManager.downloadFileFromBlobStorage(blobContainerClient, tmpZipFileName);

                // Step 5. Retrieve the DCN Number from the Zip File Name. Pass the actual zip file name in the
                // method call below
                final String fileDcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(tmpZipFileName);
                log.info("Starting to process file {}", fileDcnNumber);

                // Step 6. Unzip the zip file
                unzippedFiles = fileUtil.unzipBlobDownloadZipFile(blobZipDirectory);

                // Step 7. Read the file and generate the mapping to the PcqAnswers object.
                File metaDataFile = fileUtil.getMetaDataFile(Objects.requireNonNull(unzippedFiles.listFiles()));
                if (metaDataFile == null) {
                    log.error("metadata.json file not found, moving the zip file to Rejected container");
                    blobStorageManager.moveFileToRejectedContainer(tmpZipFileName, blobContainerClient);
                } else {
                    String jsonMetaData = jsonStringFromFile(metaDataFile);
                    PcqAnswerRequest mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(
                        jsonMetaData);

                    if (mappedAnswers == null) {
                        log.error("Mapping failed, moving the zip file to Rejected container");
                        blobStorageManager.moveFileToRejectedContainer(tmpZipFileName, blobContainerClient);
                    } else {
                        //Step 8. Invoke the back-end API
                        invokeSubmitAnswers(mappedAnswers, tmpZipFileName, blobContainerClient);
                    }
                }


            } catch (Exception ioe) {
                log.error("Error during processing " + ioe.getMessage(), ioe);
            } finally {
                log.info("File processing completed. Deleting file from local storage");
                fileUtil.deleteFilesFromLocalStorage(blobZipDirectory, unzippedFiles);
            }
        }

        log.info("PcqLoaderComponent finished.");
    }


    @SuppressWarnings("unchecked")
    private void invokeSubmitAnswers(PcqAnswerRequest mappedAnswers, String tmpZipFileName,
                                     BlobContainerClient sourceContainer) throws InterruptedException {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                ResponseEntity<Map<String, String>> responseEntity = pcqBackendService
                    .submitAnswers(mappedAnswers);

                if (responseEntity.getStatusCode() == HttpStatus.CREATED
                    || responseEntity.getStatusCode() == HttpStatus.CONFLICT) {
                    log.info(
                        "File {} processed successfully, moving file to processed folder.",
                        tmpZipFileName
                    );
                    blobStorageManager.moveFileToProcessedFolder(tmpZipFileName, sourceContainer);
                } else {
                    log.error("Back-end API call returned invalid response, moving file to rejected "
                                  + "container - Error code {} for DCN {}",
                              responseEntity.getStatusCode(), mappedAnswers.getDcnNumber());
                    blobStorageManager.moveFileToRejectedContainer(tmpZipFileName, sourceContainer);
                }
                retryCount = MAX_RETRIES;

            } catch (ExternalApiException apiException) {
                log.error(
                    "API Exception occurred during execution {} ", apiException.getMessage(),
                    apiException
                );
                if (retryCount < MAX_RETRIES - 1) {
                    Thread.sleep(threadDelay);
                    log.info("Re-trying to process file {}", tmpZipFileName);
                }
                retryCount++;
            }
        }
    }

    private String jsonStringFromFile(File file) throws IOException {
        return fileUtil.readAllBytesFromFile(file);
    }
}
