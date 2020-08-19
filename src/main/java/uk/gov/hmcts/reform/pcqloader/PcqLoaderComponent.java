package uk.gov.hmcts.reform.pcqloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pcqloader.services.BlobStorageManager;
import uk.gov.hmcts.reform.pcqloader.utils.PcqLoaderUtils;

@Component
@Slf4j
public class PcqLoaderComponent {

    @Autowired
    private BlobStorageManager blobStorageManager;

    public void execute() {
        log.info("PcqLoaderComponent started.");

        // Step 1. Connect and Authenticate with the PCQ Azure Blob Storage Account.

        // TODO: PCQ-572 Fetch PCQ container connection validation - to be removed once actual code is in place.
        blobStorageManager.fetchPcqStorageContainer();

        // Step 2. Check for zip files in the Pcq container.

        // Step 3. Loop if the files are found. (Replace the clause below with the actual condition)
        int fileCount = 2;
        for (int i = 0; i < fileCount; i++) {
            // Step 4. Download the zip file to local storage.

            // Step 5. Retrieve the DCN Number from the Zip File Name. Pass the actual zip file name in the
            // method call below
            String tmpZipFileName = "1834704010002" + i + "_14-12-2018-11-44-16.zip";
            final String dcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(tmpZipFileName);
            log.info("DCN Number extracted is " + dcnNumber);

            // Step 6. Unzip the zip file and extract the JSON Payload.
        }

        log.info("PcqLoaderComponent finished.");
    }
}
