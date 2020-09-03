package uk.gov.hmcts.reform.pcqloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.hmcts.reform.pcqloader.services.BlobStorageManager;
import uk.gov.hmcts.reform.pcqloader.utils.PcqLoaderUtils;

import java.util.List;

@Component
@Slf4j
public class PcqLoaderComponent {

    @Autowired
    private BlobStorageManager blobStorageManager;

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void execute() {
        log.info("PcqLoaderComponent started.");

        // Step 1. Connect and Authenticate with the PCQ Azure Blob Storage Account.
        Assert.isTrue(blobStorageManager.getPcqContainer().exists(), "Can connect to Blob Storage.");

        // Step 2. Check for zip files in the Pcq container.
        List<String> blobZipNamesList = blobStorageManager.collectPcqContainerBlobFileNames();
        for (String tmpZipFileName : blobZipNamesList) {
            // Step 4. Download the zip file to local storage.

            // Step 5. Retrieve the DCN Number from the Zip File Name. Pass the actual zip file name in the
            // method call below
            final String dcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(tmpZipFileName);
            log.info("DCN Number extracted is " + dcnNumber);

            // Step 6. Unzip the zip file

            // Step 7. Read the file and generate the mapping to the PcqAnswers object.

        }

        log.info("PcqLoaderComponent finished.");
    }
}
