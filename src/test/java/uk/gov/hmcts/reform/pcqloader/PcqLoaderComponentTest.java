package uk.gov.hmcts.reform.pcqloader;

import com.azure.storage.blob.BlobContainerClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pcqloader.services.BlobStorageManager;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PcqLoaderComponentTest {

    @InjectMocks
    private PcqLoaderComponent pcqLoaderComponent;

    @Mock
    private BlobStorageManager blobStorageManager;

    @Mock
    private BlobContainerClient blobContainerClient;


    @Test
    public void executeSuccess() {
        when(blobStorageManager.fetchPcqStorageContainer()).thenReturn(blobContainerClient);
        pcqLoaderComponent.execute();
    }
}
