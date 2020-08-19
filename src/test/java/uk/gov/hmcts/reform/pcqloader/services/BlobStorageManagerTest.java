package uk.gov.hmcts.reform.pcqloader.services;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pcqloader.config.BlobStorageProperties;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BlobStorageManagerTest {

    public BlobStorageManager testBlobStorageManager;

    @Mock
    private BlobServiceClient blobServiceClient;

    @Mock
    private BlobContainerClient blobContainerClient;

    private BlobStorageProperties blobStorageProperties;

    private static final String TEST_PCQ_CONTAINER_NAME = "PCQ_1";

    @BeforeEach
    public void setUp() {
        blobStorageProperties = new BlobStorageProperties();
        blobStorageProperties.setBlobPcqContainer(TEST_PCQ_CONTAINER_NAME);
    }

    @Test
    public void initialisationSuccess() {
        when(blobServiceClient.getBlobContainerClient(anyString())).thenReturn(blobContainerClient);
        testBlobStorageManager = new BlobStorageManager(blobStorageProperties, blobServiceClient);
        verify(blobServiceClient, times(1)).getBlobContainerClient(TEST_PCQ_CONTAINER_NAME);
    }
}
