package uk.gov.hmcts.reform.pcqloader.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pcqloader.config.BlobStorageProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class BlobStorageManager {

    private final BlobServiceClient blobServiceClient;

    private final BlobStorageProperties blobStorageProperties;

    public BlobStorageManager(
        BlobStorageProperties blobStorageProperties,
        BlobServiceClient blobServiceClient
    ) {
        this.blobStorageProperties = blobStorageProperties;
        this.blobServiceClient = blobServiceClient;
    }

    public BlobContainerClient getContainer(String containerName) {
        return blobServiceClient.getBlobContainerClient(containerName);
    }

    public BlobContainerClient getPcqContainer() {
        return getContainer(blobStorageProperties.getBlobPcqContainer());
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<String> collectBlobFileNamesFromContainer(BlobContainerClient blobContainerClient) {
        List<String> zipFilenames = new ArrayList<>();
        for (BlobItem blob : blobContainerClient.listBlobs()) {
            String fileName = FilenameUtils.getName(blob.getName());
            if (Strings.isNullOrEmpty(fileName)) {
                log.error("Unable to retrieve blob filename from container: {}", blob.getName());
            } else {
                zipFilenames.add(fileName);
            }
        }

        Collections.shuffle(zipFilenames);
        return zipFilenames;
    }

    public void uploadFileToBlobStorage(BlobContainerClient blobContainerClient, String filePath) {
        File localFileUpload = new File(filePath);
        log.debug("Uploading file {} to {} container",
                 localFileUpload.getName(), blobContainerClient.getBlobContainerName());
        BlobClient blobClient = blobContainerClient.getBlobClient(localFileUpload.getName());
        log.info("Uploading to Blob storage as blob: {}", blobClient.getBlobUrl());
        blobClient.uploadFromFile(filePath);
    }

    public BlobContainerClient createContainer(String containerName) {
        if (blobServiceClient.getBlobContainerClient(containerName).exists()) {
            return blobServiceClient.getBlobContainerClient(containerName);
        }
        return blobServiceClient.createBlobContainer(containerName);
    }

    public void deleteContainer(String containerName) {
        blobServiceClient.deleteBlobContainer(containerName);
    }
}
