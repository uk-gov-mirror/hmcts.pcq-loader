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
import org.testcontainers.containers.DockerComposeContainer;
import uk.gov.hmcts.reform.pcqloader.config.BlobStorageProperties;

import java.io.File;

@Slf4j
@TestPropertySource(locations = "/application.properties")
public class BlobStorageManagerTest {

    private BlobStorageProperties blobStorageProperties;

    private static DockerComposeContainer dockerComposeContainer;

    protected BlobContainerClient testContainer;

    protected static final String CONTAINER_NAME = "pcq";

    protected BlobServiceClient blobServiceClient;

    @BeforeEach
    public void setUp() throws Exception {
        blobServiceClient = new BlobServiceClientBuilder()
                .connectionString("UseDevelopmentStorage=true")
                .buildClient();

        testContainer = blobServiceClient.createBlobContainer(CONTAINER_NAME);
        blobStorageProperties = new BlobStorageProperties();
        blobStorageProperties.setBlobPcqContainer(CONTAINER_NAME);
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
        BlobStorageManager blobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
        BlobContainerClient container = blobStorageManager.fetchPcqStorageContainer();
        Assertions.assertNotNull(container);
    }

}
