package uk.gov.hmcts.reform.pcqloader.utils;

import uk.gov.hmcts.reform.pcqloader.exceptions.BlobProcessingException;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public Boolean confirmEmptyFileCanBeCreated(File blobFilePath) throws IOException {
        File blobFolder = blobFilePath.getParentFile();
        if ((blobFolder.exists() || blobFolder.mkdirs())
            && (blobFilePath.exists() || blobFilePath.createNewFile())) {
            return true;
        } else {
            throw new BlobProcessingException("Failed to create temp blob file.");
        }
    }
}
