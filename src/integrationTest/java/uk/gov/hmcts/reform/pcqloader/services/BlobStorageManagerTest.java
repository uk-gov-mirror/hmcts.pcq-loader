package uk.gov.hmcts.reform.pcqloader.services;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.DockerComposeContainer;
import uk.gov.hmcts.reform.pcqloader.config.BlobStorageProperties;
import uk.gov.hmcts.reform.pcqloader.exceptions.BlobProcessingException;

import java.io.File;
import java.util.List;

@Slf4j
@TestPropertySource(locations = "/application.properties")
public class BlobStorageManagerTest {

    protected static final String CONTAINER_NAME = "pcq";
    private static final String BLOB_FILENAME_1 = "1579002492_31-08-2020-11-35-10.zip";
    private static final String BLOB_FILENAME_2 = "1579002493_31-08-2020-11-48-42.zip";
    private static final String BLOB_FILENAME_DOES_NOT_EXIST = "NOT_FOOUND.zip";

    private static DockerComposeContainer dockerComposeContainer;

    protected BlobContainerClient testContainer;

    protected BlobServiceClient blobServiceClient;

    protected BlobStorageManager blobStorageManager;

    protected File blobFile1;

    protected File blobFile2;

    @BeforeEach
    public void setUp() throws Exception {
        blobServiceClient = new BlobServiceClientBuilder()
                .connectionString("UseDevelopmentStorage=true")
                .buildClient();

        testContainer = blobServiceClient.createBlobContainer(CONTAINER_NAME);
        BlobStorageProperties blobStorageProperties = new BlobStorageProperties();
        blobStorageProperties.setBlobPcqContainer(CONTAINER_NAME);

        File blobFile1 = ResourceUtils.getFile("classpath:blobTestFiles/" + BLOB_FILENAME_1);
        File blobFile2 = ResourceUtils.getFile("classpath:blobTestFiles/" + BLOB_FILENAME_2);

        blobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
        blobStorageManager.uploadFileToBlobStorage(testContainer, blobFile1.getPath());
        blobStorageManager.uploadFileToBlobStorage(testContainer, blobFile2.getPath());
    }

    @AfterEach
    public void cleanUp() throws Exception {
        testContainer.delete();
    }

    @BeforeAll
    public static void initialize() {
        log.info("Starting Azure-Storage container");
        dockerComposeContainer =
            new DockerComposeContainer(new File("src/integrationTest/resources/docker-compose.yml"))
                .withExposedService("azurite", 10_000);

        dockerComposeContainer.start();
    }

    @AfterAll
    public static void tearDownContainer() {
        log.info("Stopping Azure-Storage container");
        dockerComposeContainer.stop();
    }

    @Test
    public void testFetchPcqStorageContainerSuccess() {
        BlobContainerClient container = blobStorageManager.getPcqContainer();
        Assertions.assertNotNull(container);
    }

    @Test
    public void testCollectBlobFileNames() {
        List<String> response =
            blobStorageManager.collectBlobFileNamesFromContainer(blobStorageManager.getPcqContainer());
        Assertions.assertEquals(2, response.size(), "Correct number of blob names");
        Assertions.assertTrue(response.contains(BLOB_FILENAME_1), "Correct filename 1");
        Assertions.assertTrue(response.contains(BLOB_FILENAME_2), "Correct filename 2");
    }

    @Test
    public void testDownloadFileFromBlobStorage() {
        File fileResponse =
            blobStorageManager.downloadFileFromBlobStorage(blobStorageManager.getPcqContainer(), BLOB_FILENAME_1);
        Assertions.assertNotNull(fileResponse,"File returned is not null");
        Assertions.assertTrue(fileResponse.getPath().contains(BLOB_FILENAME_1), "File has correct path");
    }

    @Test
    public void testDownloadFileFromBlobStorageNotFoundError() {
        try {
            File fileResponse = blobStorageManager
                .downloadFileFromBlobStorage(blobStorageManager.getPcqContainer(), BLOB_FILENAME_DOES_NOT_EXIST);
            Assertions.fail("Should generate BlobProcessingException for missing file");
        } catch (BlobProcessingException bpe) {
            Assertions.assertNotNull(bpe, "Successfully generated exception for missing file.");
        }
    }
}
