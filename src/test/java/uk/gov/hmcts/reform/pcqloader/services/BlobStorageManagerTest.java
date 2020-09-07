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
import uk.gov.hmcts.reform.pcqloader.exceptions.BlobProcessingException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("PMD.TooManyMethods")
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
    private static final String TEST_CUSTOM_CONTAINER_NAME = "PCQ_2";
    private static final String TEST_BLOB_FILENAME1 = "1579002492_31-08-2020-11-35-10.zip";
    private static final String TEST_BLOB_FILENAME2 = "1579002493_31-08-2020-11-48-42.zip";
    private static final String TEST_PCQ_BLOB_PATH = "http://0.0.0.0:10000/" + TEST_PCQ_CONTAINER_NAME;
    private static final String TEST_PCQ_FILE_PATH = "/var/tmp";

    @BeforeEach
    public void setUp() {
        blobStorageProperties = new BlobStorageProperties();
        blobStorageProperties.setBlobPcqContainer(TEST_PCQ_CONTAINER_NAME);
        blobStorageProperties.setBlobStorageDownloadPath(TEST_PCQ_FILE_PATH);
        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
    }

    @Test
    public void initialisationSuccess() {
        blobServiceClient.getBlobContainerClient(TEST_PCQ_CONTAINER_NAME);
        verify(blobServiceClient, times(1)).getBlobContainerClient(TEST_PCQ_CONTAINER_NAME);
    }

    @Test
    public void testGetPcqContainer() {
        when(blobServiceClient.getBlobContainerClient(TEST_PCQ_CONTAINER_NAME)).thenReturn(pcqContainer);
        BlobContainerClient blobContainerClient = testBlobStorageManager.getPcqContainer();
        Assertions.assertNotNull(blobContainerClient, "Container collected successfully");
        verify(blobServiceClient, times(1)).getBlobContainerClient(TEST_PCQ_CONTAINER_NAME);
    }

    @Test
    public void testGetContainerWithContainerName() {
        when(blobServiceClient.getBlobContainerClient(TEST_CUSTOM_CONTAINER_NAME)).thenReturn(pcqContainer);
        BlobContainerClient blobContainerClient = testBlobStorageManager.getContainer(TEST_CUSTOM_CONTAINER_NAME);
        Assertions.assertNotNull(blobContainerClient, "Named container collected successfully");
        verify(blobServiceClient, times(1)).getBlobContainerClient(TEST_CUSTOM_CONTAINER_NAME);
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
    public void testCollectBlobFileNamesSuccess() {
        BlobItem blobItem1 = new BlobItem().setName(TEST_BLOB_FILENAME1);
        BlobItem blobItem2 = new BlobItem().setName(TEST_BLOB_FILENAME2);
        List<BlobItem> blobs = Arrays.asList(blobItem1, blobItem2);

        when(pcqContainer.listBlobs()).thenReturn(pageIterableBlobs);
        when(pageIterableBlobs.iterator()).thenReturn(blobs.iterator());

        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
        List<String> response = testBlobStorageManager.collectBlobFileNamesFromContainer(pcqContainer);

        verify(pageIterableBlobs, times(1)).iterator();
        Assertions.assertEquals(2, response.size(), "Correct number of blob names");
        Assertions.assertTrue(response.contains(TEST_BLOB_FILENAME1), "Correct filename 1");
        Assertions.assertTrue(response.contains(TEST_BLOB_FILENAME2), "Correct filename 2");
    }

    @Test
    public void testCollectBlobFileNamesEmptyListSuccess() {
        List<BlobItem> blobs = new ArrayList<>();

        when(pcqContainer.listBlobs()).thenReturn(pageIterableBlobs);
        when(pageIterableBlobs.iterator()).thenReturn(blobs.iterator());

        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
        List<String> response = testBlobStorageManager.collectBlobFileNamesFromContainer(pcqContainer);

        verify(pageIterableBlobs, times(1)).iterator();
        Assertions.assertEquals(0, response.size(), "Correct number of blob names");
    }

    @Test
    public void testCollectBlobFileNamesMissingName() {
        BlobItem blobItem1 = new BlobItem();
        var blobs = Arrays.asList(blobItem1);

        when(pcqContainer.listBlobs()).thenReturn(pageIterableBlobs);
        when(pageIterableBlobs.iterator()).thenReturn(blobs.iterator());

        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
        List<String> response = testBlobStorageManager.collectBlobFileNamesFromContainer(pcqContainer);

        verify(pageIterableBlobs, times(1)).iterator();
        Assertions.assertEquals(0, response.size(), "No files added as no name was provided");
    }

    @Test
    public void testDownloadFileFromBlobStorageSuccess() {
        when(pcqContainer.getBlobClient(TEST_BLOB_FILENAME1)).thenReturn(blobClient);
        when(blobClient.downloadToFile(TEST_PCQ_FILE_PATH + "/" + TEST_BLOB_FILENAME1, true)).thenReturn(null);

        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
        File fileResponse = testBlobStorageManager.downloadFileFromBlobStorage(pcqContainer, TEST_BLOB_FILENAME1);

        verify(blobClient, times(1)).downloadToFile(TEST_PCQ_FILE_PATH + "/" + TEST_BLOB_FILENAME1, true);
        Assertions.assertNotNull(fileResponse, "File response is not null");
    }

    @Test
    public void testDownloadFileFromBlobStorageError() {
        when(pcqContainer.getBlobClient(TEST_BLOB_FILENAME1)).thenReturn(blobClient);
        when(blobClient.downloadToFile(TEST_PCQ_FILE_PATH + "/" + TEST_BLOB_FILENAME1, true))
            .thenThrow(new RuntimeException("Error"));

        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);

        try {
            testBlobStorageManager.downloadFileFromBlobStorage(pcqContainer, TEST_BLOB_FILENAME1);
            Assertions.fail("BlobProcessingException should be thrown");
        } catch (BlobProcessingException bpe) {
            verify(blobClient, times(1)).downloadToFile(TEST_PCQ_FILE_PATH + "/" + TEST_BLOB_FILENAME1, true);
        }
    }
}
