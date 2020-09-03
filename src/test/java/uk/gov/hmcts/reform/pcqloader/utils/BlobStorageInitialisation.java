package uk.gov.hmcts.reform.pcqloader.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SuppressWarnings("HideUtilityClassConstructor")
public class BlobStorageInitialisation {

    private static final String DEV_CONNECTION_STRING = "UseDevelopmentStorage=true";

    private static final String DEV_PCQ_CONTAINER_NAME = "pcq";
    private static final String DEV_PCQ_CONTAINER_REJECTED_NAME = "pcq-rejected";

    private static final String SAMPLE_DOCUMENT_PATH_1
        = "src/integrationTest/resources/blobTestFiles/1579002492_31-08-2020-11-35-10.zip";
    private static final String SAMPLE_DOCUMENT_PATH_2
        = "src/integrationTest/resources/blobTestFiles/1579002493_31-08-2020-11-48-42.zip";

    private static List<String> availableContaimers = new ArrayList<>();

    public static void main(String[] args) {
        log.info("Connecting to Azurite Blob Storage");

        // Connect to development auzrite blob storage
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(DEV_CONNECTION_STRING)
            .buildClient();

        // Clear out available containers
        blobServiceClient.listBlobContainers()
            .forEach(container -> availableContaimers.add(container.getName()));
        availableContaimers.forEach(containerName -> blobServiceClient.deleteBlobContainer(containerName));
        log.info("Creating {} container", DEV_PCQ_CONTAINER_NAME);
        blobServiceClient.createBlobContainer(DEV_PCQ_CONTAINER_NAME);
        log.info("Creating {} container", DEV_PCQ_CONTAINER_REJECTED_NAME);
        blobServiceClient.createBlobContainer(DEV_PCQ_CONTAINER_REJECTED_NAME);

        // Add sample documents to pcq container
        BlobContainerClient blobContainerClient =  blobServiceClient.getBlobContainerClient(DEV_PCQ_CONTAINER_NAME);
        uploadFileToBlobStorage(blobContainerClient, SAMPLE_DOCUMENT_PATH_1);
        uploadFileToBlobStorage(blobContainerClient, SAMPLE_DOCUMENT_PATH_2);
    }

    private static void uploadFileToBlobStorage(BlobContainerClient blobContainerClient, String filePath) {
        File localFileUpload = new File(filePath);
        log.info("Uploading file {} to {} container",
                 localFileUpload.getName(), blobContainerClient.getBlobContainerName());
        BlobClient blobClient = blobContainerClient.getBlobClient(localFileUpload.getName());
        log.info("Uploading to Blob storage as blob: {}", blobClient.getBlobUrl());
        blobClient.uploadFromFile(filePath);
    }
}
