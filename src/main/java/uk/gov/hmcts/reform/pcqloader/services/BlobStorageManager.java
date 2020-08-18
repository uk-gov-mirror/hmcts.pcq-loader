package uk.gov.hmcts.reform.pcqloader.services;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pcqloader.config.BlobStorageProperties;

@Slf4j
@Service
public class BlobStorageManager {

    private final BlobServiceClient blobServiceClient;

    private final BlobStorageProperties blobStorageProperties;

    private final BlobContainerClient pcqContainer;

    public BlobStorageManager(
        BlobStorageProperties blobStorageProperties,
        BlobServiceClient blobServiceClient
    ) {
        this.blobStorageProperties = blobStorageProperties;
        this.blobServiceClient = blobServiceClient;
        this.pcqContainer = blobServiceClient.getBlobContainerClient(blobStorageProperties.getBlobPcqContainer());
    }

    public BlobContainerClient fetchPcqStorageContainer() {
        log.info("Retrieving container {} with URL {} from account {}",
                 blobStorageProperties.getBlobPcqContainer(),
                 pcqContainer.getBlobContainerUrl(),
                 blobServiceClient.getAccountName());

        log.info("DefaultEncryptionScope {}",
            pcqContainer.getProperties().getDefaultEncryptionScope()
        );

        return this.pcqContainer;
    }
}
