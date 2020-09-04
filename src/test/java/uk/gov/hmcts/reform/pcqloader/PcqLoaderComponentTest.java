package uk.gov.hmcts.reform.pcqloader;

import com.azure.storage.blob.BlobContainerClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pcqloader.services.BlobStorageManager;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PcqLoaderComponentTest {

    private static final String TEST_BLOB_FILENAME1 = "1579002492_31-08-2020-11-35-10.zip";
    private static final String TEST_BLOB_FILENAME2 = "1579002493_31-08-2020-11-48-42.zip";

    @InjectMocks
    private PcqLoaderComponent pcqLoaderComponent;

    @Mock
    private BlobStorageManager blobStorageManager;

    @Mock
    private BlobContainerClient blobContainerClient;

    @Test
    public void executeSuccess() {
        List<String> blobFileNames = Arrays.asList(TEST_BLOB_FILENAME1, TEST_BLOB_FILENAME2);

        when(blobStorageManager.getPcqContainer()).thenReturn(blobContainerClient);
        when(blobContainerClient.exists()).thenReturn(true);
        when(blobStorageManager.collectBlobFileNamesFromContainer(blobContainerClient)).thenReturn(blobFileNames);

        pcqLoaderComponent.execute();
    }
}
