package uk.gov.hmcts.reform.pcqloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PcqLoaderComponent {

    public void execute() {
        log.info("PcqLoaderComponent started.");

        // Step 1. Connect and Authenticate with the PCQ Azure Blob Storage Account.

        // Step 2. Check for zip files in the Pcq container.

        // Step 3. Loop if the files are found. (Replace the clause below with the actual condition)
        int fileCount = 2;
        for (int i = 0; i < fileCount; i++) {
            // Step 4. Download the zip file to local storage.

            // Step 5. Retrieve the DCN Number from the Zip File Name.

            // Step 6. Unzip the zip file and extract the JSON Payload.
        }

        log.info("PcqLoaderComponent finished.");
    }
}
