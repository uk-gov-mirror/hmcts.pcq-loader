package uk.gov.hmcts.reform.pcqloader.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.pcqloader.exceptions.ZipProcessingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZipFileUtilsTest {

    private static final String BLOB_FILENAME_1 = "1579002492_31-08-2020-11-35-10.zip";
    private static final String BAD_ZIP = "1579002493_31-08-2020-11-35-ERROR.zip";

    @Mock
    private File mockedFile;

    @Mock
    private File mockedFolder;

    private File badZip;
    private File actualZip;

    private ZipFileUtils zipFileUtils;

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        zipFileUtils = new ZipFileUtils();
        actualZip = ResourceUtils.getFile("classpath:testZipFiles/" + BLOB_FILENAME_1);
        badZip = ResourceUtils.getFile("classpath:testZipFiles/" + BAD_ZIP);
    }

    @Test
    public void testConfirmFileCanBeCreated() throws IOException {
        when(mockedFile.getParentFile()).thenReturn(mockedFolder);
        when(mockedFolder.exists()).thenReturn(false);
        when(mockedFolder.mkdirs()).thenReturn(true);
        when(mockedFolder.isDirectory()).thenReturn(true);
        when(mockedFile.createNewFile()).thenReturn(true);
        when(mockedFile.delete()).thenReturn(true);

        boolean result = zipFileUtils.confirmFileCanBeCreated(mockedFile);
        assertTrue(result, "Should be ok to create file");
        verify(mockedFile, times(1)).createNewFile();
        verify(mockedFile, times(1)).delete();
    }

    @Test
    public void testConfirmFolderPathCanNotBeCreated() throws IOException {
        when(mockedFile.getParentFile()).thenReturn(mockedFolder);
        when(mockedFolder.exists()).thenReturn(false);
        when(mockedFolder.mkdirs()).thenReturn(false);

        boolean result = zipFileUtils.confirmFileCanBeCreated(mockedFile);
        assertFalse(result, "Should not be ok to create file as path can't be created");
        verify(mockedFile, times(0)).createNewFile();
        verify(mockedFile, times(0)).delete();
        verify(mockedFolder, times(1)).mkdirs();
    }

    @Test
    public void testConfirmFileCanNotBeCreated() throws IOException {
        when(mockedFile.getParentFile()).thenReturn(mockedFolder);
        when(mockedFolder.exists()).thenReturn(false);
        when(mockedFolder.mkdirs()).thenReturn(true);
        when(mockedFolder.isDirectory()).thenReturn(true);
        when(mockedFile.exists()).thenReturn(false);
        when(mockedFile.createNewFile()).thenThrow(new IOException());

        boolean result = zipFileUtils.confirmFileCanBeCreated(mockedFile);

        assertFalse(result, "Should not be able to create file");
        verify(mockedFile, times(1)).createNewFile();
        verify(mockedFile, times(0)).delete();
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void testUnzipBlobDownloadZipFileSuccess() {
        File result = zipFileUtils.unzipBlobDownloadZipFile(actualZip);
        List<String> knownFiles = Arrays.asList("metadata.json", "1111001.pdf");
        assertNotNull(result, "Valid folder of unpacked zip returned.");
        assertEquals(2, result.listFiles().length, "Number of files correct in zip.");
        for (File unpackedFile : result.listFiles()) {
            assertTrue(knownFiles.contains(unpackedFile.getName()),"Zip contents correct");
        }
    }

    @Test
    public void testUnzipBlobDownloadZipFileError() {
        try {
            zipFileUtils.unzipBlobDownloadZipFile(badZip);
            fail("ZipProcessingException exception should be thrown.");
        } catch (ZipProcessingException zpe) {
            assertNotNull(zpe, "ZipProcessingException exception is thrown.");
        }
    }
}
