package uk.gov.hmcts.reform.pcqloader.utils;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pcqloader.exceptions.BlobProcessingException;

import java.io.File;
import java.io.IOException;

@Component
public class FileUtils {

    public Boolean confirmEmptyFileCanBeCreated(File blobFilePath) throws IOException {
        String blobFolderPath = blobFilePath.getPath();
        File blobFolder = new File(blobFolderPath);
        if (blobFolder.exists() || blobFolder.mkdirs()) {
            if (blobFilePath.exists() || blobFilePath.createNewFile()) {
                blobFilePath.delete();
                return Boolean.TRUE;
            } else {
                throw new BlobProcessingException("Failed to create temp blob file.");
            }
        } else {
            throw new BlobProcessingException("Failed to create temp blob dir.");
        }
    }
}
