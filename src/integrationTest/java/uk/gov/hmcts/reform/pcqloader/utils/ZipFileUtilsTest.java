package uk.gov.hmcts.reform.pcqloader.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ZipFileUtilsTest {

    private static final String BLOB_FILENAME_1 = "1579002492_31-08-2020-11-35-10.zip";
    private static final String TEST_UNPACK_ZIP_FOLDER = "/var/tmp/pcq-loader/unpack-test/blobs";
    private static final File UNPACK_FOLDER_FILE = new File(TEST_UNPACK_ZIP_FOLDER);

    private File blobFile1;

    private ZipFileUtils zipFileUtils;

    @BeforeEach
    public void setUp() throws Exception {
        zipFileUtils = new ZipFileUtils();
        blobFile1 = ResourceUtils.getFile("classpath:blobTestFiles/" + BLOB_FILENAME_1);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FileUtils.cleanDirectory(UNPACK_FOLDER_FILE);
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void testUnzipBlobDownloadZipFile() throws IOException {
        File fileToUnzip = new File(TEST_UNPACK_ZIP_FOLDER + File.separator + BLOB_FILENAME_1);
        if (zipFileUtils.confirmFileCanBeCreated(fileToUnzip)) {
            FileUtils.copyFile(blobFile1, fileToUnzip);
            if (fileToUnzip.exists() && fileToUnzip.isFile()) {
                File unzippedFileFolder = zipFileUtils.unzipBlobDownloadZipFile(fileToUnzip);
                List<String> knownFiles = Arrays.asList("metadata.json", "1111001.pdf");
                Assertions.assertNotNull(unzippedFileFolder, "Valid folder of unpacked zip returned.");
                Assertions.assertEquals(2, unzippedFileFolder.listFiles().length, "Number of files correct in zip.");
                for (File unpackedFile : unzippedFileFolder.listFiles()) {
                    Assertions.assertTrue(knownFiles.contains(unpackedFile.getName()),"Zip contents correct");
                }
            } else {
                Assertions.fail("Unable to identify file to unzip");
            }
        } else {
            Assertions.fail("Unable to reach unpack folder to unzip file.");
        }
    }
}
