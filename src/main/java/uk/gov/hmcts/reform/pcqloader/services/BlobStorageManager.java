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
import uk.gov.hmcts.reform.pcqloader.exceptions.BlobProcessingException;

import java.io.File;
import java.io.IOException;
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

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public File downloadFileFromBlobStorage(BlobContainerClient blobContainerClient, String blobName) {
        File localFile = null;
        log.debug("Downloading blob name {} to {} path",
                  blobName, blobStorageProperties.getBlobStorageDownloadPath());
        String filePath = blobStorageProperties.getBlobStorageDownloadPath() + File.separator + blobName;

        try {
            if (confirmEmptyFileCanBeCreated(filePath)) {
                blobContainerClient.getBlobClient(blobName).downloadToFile(filePath, true);
                localFile = new File(filePath);
                if (localFile.exists()) {
                    log.info("Succeessfully downloaded blob file to path: {}", localFile.getPath());
                }
            }
        } catch (Exception exp) {
            log.error("Error downloading {} from Blob Storage", blobName);
            throw new BlobProcessingException("Unable to download blob file.", exp);
        }

        return localFile;
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

    private boolean confirmEmptyFileCanBeCreated(String filePath) throws IOException {
        File dirPath = new File(filePath);
        if (dirPath.exists() || dirPath.mkdirs()) {
            File tempFile = new File(filePath);
            if (tempFile.exists() || tempFile.createNewFile()) {
                tempFile.delete();
                return true;
            } else {
                throw new BlobProcessingException("Failed to create temp blob file.");
            }
        } else {
            throw new BlobProcessingException("Failed to create temp blob dir.");
        }
    }
}
