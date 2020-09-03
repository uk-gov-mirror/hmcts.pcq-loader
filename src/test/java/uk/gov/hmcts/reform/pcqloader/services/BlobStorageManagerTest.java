package uk.gov.hmcts.reform.pcqloader.services;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pcqloader.config.BlobStorageProperties;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BlobStorageManagerTest {

    @Mock
    private BlobServiceClient blobServiceClient;

    @Mock
    private BlobContainerClient pcqContainer;

    @Mock
    private PagedIterable<BlobItem> pageIterableBlobs;

    @Mock
    private BlobClient blobClient;

    private BlobStorageProperties blobStorageProperties;

    public BlobStorageManager testBlobStorageManager;

    private static final String TEST_PCQ_CONTAINER_NAME = "PCQ_1";
    private static final String TEST_BLOB_FILENAME1 = "1579002492_31-08-2020-11-35-10.zip";
    private static final String TEST_BLOB_FILENAME2 = "1579002493_31-08-2020-11-48-42.zip";
    private static final String TEST_PCQ_BLOB_PATH = "http://0.0.0.0:10000/" + TEST_PCQ_CONTAINER_NAME;

    @BeforeEach
    public void setUp() {
        blobStorageProperties = new BlobStorageProperties();
        blobStorageProperties.setBlobPcqContainer(TEST_PCQ_CONTAINER_NAME);
        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
    }

    @Test
    public void initialisationSuccess() {
        blobServiceClient.getBlobContainerClient(TEST_PCQ_CONTAINER_NAME);
        verify(blobServiceClient, times(1)).getBlobContainerClient(TEST_PCQ_CONTAINER_NAME);
    }

    @Test
    public void testCreateNewContainerSuccess() {
        when(blobServiceClient.createBlobContainer(TEST_PCQ_CONTAINER_NAME)).thenReturn(pcqContainer);
        when(blobServiceClient.getBlobContainerClient(TEST_PCQ_CONTAINER_NAME)).thenReturn(pcqContainer);
        when(pcqContainer.exists()).thenReturn(false);
        BlobContainerClient newContainer = testBlobStorageManager.createContainer(TEST_PCQ_CONTAINER_NAME);
        Assertions.assertNotNull(newContainer, "Container created");
        verify(blobServiceClient, times(1)).createBlobContainer(TEST_PCQ_CONTAINER_NAME);
    }

    @Test
    public void testCreateNewContainerWhenExistingSuccess() {
        when(blobServiceClient.getBlobContainerClient(TEST_PCQ_CONTAINER_NAME)).thenReturn(pcqContainer);
        when(pcqContainer.exists()).thenReturn(true);
        BlobContainerClient newContainer = testBlobStorageManager.createContainer(TEST_PCQ_CONTAINER_NAME);
        Assertions.assertNotNull(newContainer, "Container created");
        verify(blobServiceClient, times(2)).getBlobContainerClient(TEST_PCQ_CONTAINER_NAME);
    }

    @Test
    public void testUploadFilesToBlobStorageSuccess() {
        when(pcqContainer.getBlobClient(TEST_BLOB_FILENAME1)).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn(TEST_PCQ_BLOB_PATH + "/" + TEST_BLOB_FILENAME1);
        testBlobStorageManager.uploadFileToBlobStorage(pcqContainer, TEST_BLOB_FILENAME1);
        verify(blobClient, times(1)).uploadFromFile(TEST_BLOB_FILENAME1);
    }

    @Test
    public void testDeleteContainersSuccess() {
        testBlobStorageManager.deleteContainer(TEST_PCQ_CONTAINER_NAME);
        verify(blobServiceClient, times(1)).deleteBlobContainer(TEST_PCQ_CONTAINER_NAME);
    }

    @Test
    public void testCollectBlobFileNames() {
        BlobItem blobItem1 = new BlobItem().setName(TEST_BLOB_FILENAME1);
        BlobItem blobItem2 = new BlobItem().setName(TEST_BLOB_FILENAME2);
        var blobs = Arrays.asList(blobItem1, blobItem2);

        when(testBlobStorageManager.getPcqContainer()).thenReturn(pcqContainer);
        when(pcqContainer.listBlobs()).thenReturn(pageIterableBlobs);
        when(pageIterableBlobs.iterator()).thenReturn(blobs.iterator());

        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
        List<String> response = testBlobStorageManager.collectPcqContainerBlobFileNames();

        verify(blobServiceClient, times(1)).getBlobContainerClient(TEST_PCQ_CONTAINER_NAME);
        verify(pageIterableBlobs, times(1)).iterator();
        Assertions.assertEquals(2, response.size(), "Correct number of blob names");
        Assertions.assertTrue(response.contains(TEST_BLOB_FILENAME1), "Correct filename 1");
        Assertions.assertTrue(response.contains(TEST_BLOB_FILENAME2), "Correct filename 2");
    }
}
