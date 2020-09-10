package uk.gov.hmcts.reform.pcqloader;

import com.azure.storage.blob.BlobContainerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.hmcts.reform.pcqloader.exception.ExternalApiException;
import uk.gov.hmcts.reform.pcqloader.helper.PayloadMappingHelper;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcqloader.services.BlobStorageManager;
import uk.gov.hmcts.reform.pcqloader.services.PcqBackendService;
import uk.gov.hmcts.reform.pcqloader.utils.ZipFileUtils;
import uk.gov.hmcts.reform.pcqloader.utils.PcqLoaderUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PcqLoaderComponent {

    private static final String DUPLICATE_DCN_MSG = "Record already exists for Dcn Number";
    private static final String RESPONSE_KEY_3 = "responseStatus";
    private static final int THREAD_DELAY = 15000;

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
        Assert.isTrue(blobContainerClient.exists(), "Can connect to Blob Storage.");

        // Step 2. Check for zip files in the Pcq container.
        List<String> blobZipNamesList = blobStorageManager.collectBlobFileNamesFromContainer(blobContainerClient);
        for (String tmpZipFileName : blobZipNamesList) {

            try {
                // Step 4. Download the zip file to local storage.
                File blobZipDirectory =
                    blobStorageManager.downloadFileFromBlobStorage(blobContainerClient, tmpZipFileName);

                // Step 5. Retrieve the DCN Number from the Zip File Name. Pass the actual zip file name in the
                // method call below
                final String dcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(tmpZipFileName);
                log.info("DCN Number extracted is " + dcnNumber);

                // Step 6. Unzip the zip file
                fileUtil.unzipBlobDownloadZipFile(blobZipDirectory);

                // Step 7. Read the file and generate the mapping to the PcqAnswers object.
                String jsonMetaData = PcqLoaderUtils.jsonStringFromFile("File_Name_From_Above_Step");
                PcqAnswerRequest mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, jsonMetaData);
                if (mappedAnswers == null) {
                    log.error("Mapping failed, moving the zip file to Rejected container");
                    moveFileToRejectedContainer(tmpZipFileName, blobContainerClient);
                } else {
                    //Step 8. Invoke the back-end API
                    invokeSubmitAnswers(mappedAnswers, tmpZipFileName, blobContainerClient);
                }

            } catch (Exception ioe) {
                log.error("Error during processing "+ ioe.getMessage(), ioe);
            } finally {
                log.info("File processing completed. Deleting file from local storage");
                deleteFileFromLocalStorage(tmpZipFileName);
            }
        }

        log.info("PcqLoaderComponent finished.");
    }

    private void moveFileToRejectedContainer(String fileName, BlobContainerClient sourceContainer) {

        blobStorageManager.moveFileToRejectedContainer(fileName, sourceContainer);
        log.info("Moved file {} to the Rejected Container", fileName);
    }

    private void moveFileToProcessedFolder(String fileName, BlobContainerClient sourceContainer) {
        blobStorageManager.moveFileToProcessedFolder(fileName, sourceContainer);
        log.info("Moved file {} to the Processed Folder", fileName);
    }

    private void deleteFileFromLocalStorage(String fileName) {
        //Add code here to remove file from local storage.
    }

    @SuppressWarnings("unchecked")
    private void invokeSubmitAnswers(PcqAnswerRequest mappedAnswers, String tmpZipFileName,
                                     BlobContainerClient sourceContainer) {
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                ResponseEntity<Map<String, String>> responseEntity = pcqBackendService
                    .submitAnswers(mappedAnswers);

                if (responseEntity.getStatusCode() == HttpStatus.CREATED ||
                    responseEntity.getBody().get(RESPONSE_KEY_3).contains(DUPLICATE_DCN_MSG)) {
                    log.info(
                        "File {} processed successfully, moving file to processed folder.",
                        tmpZipFileName
                    );
                    moveFileToProcessedFolder(tmpZipFileName, sourceContainer);
                    retryCount = 3;
                } else {
                    log.error("Back-end API call returned invalid response, moving file to rejected "
                                  + "container - Error code {} for DCN {}",
                              responseEntity.getStatusCode(), mappedAnswers.getDcnNumber());
                    moveFileToRejectedContainer(tmpZipFileName, sourceContainer);
                    retryCount = 3;
                }

            } catch (ExternalApiException apiException) {
                log.error(
                    "API Exception occurred during execution {} ", apiException.getMessage(),
                    apiException
                );
                try {
                    Thread.sleep(THREAD_DELAY);
                } catch (InterruptedException e) {
                    log.warn("InterruptedException received, carrying on");
                }
                if (retryCount < 2) {
                    log.info("Re-trying to process file {}", tmpZipFileName);
                }
                retryCount ++;
            }
        }
    }
}
