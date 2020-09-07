package uk.gov.hmcts.reform.pcqloader.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pcqloader.exceptions.BlobProcessingException;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileUtilsTest {

    private static final String BLOB_FILE_PATH = "/var/tmp/pcq-blobs/test-pcq.zip";

    @Mock
    private File tempFile;

    public FileUtils fileUtils;

    @BeforeEach
    public void setUp() {
        fileUtils = new FileUtils();
        tempFile = new File(BLOB_FILE_PATH);
        MockitoAnnotations.initMocks(fileUtils);
    }

    @Test
    public void testConfirmEmptyFileCanBeCreated() throws IOException {
        boolean result = fileUtils.confirmEmptyFileCanBeCreated(tempFile);
        Assertions.assertTrue(result, "Should be ok to create file");
    }

    @Test
    public void testErrorWhenConfirmEmptyFileCanBeCreated() throws IOException {
        File mockedFile = Mockito.mock(File.class);

        try {
            when(mockedFile.exists()).thenThrow(new BlobProcessingException("Failed to create temp blob file."));
            when(mockedFile.getPath()).thenReturn("/var/tmp/pcq-bloba");
            fileUtils.confirmEmptyFileCanBeCreated(mockedFile);
            fail("Should be throwing BlobProcessingException.");
        } catch (BlobProcessingException bpe) {
            Assertions.assertNotNull(bpe, "Should raise BlobProcessingException");
        }
    }
}
