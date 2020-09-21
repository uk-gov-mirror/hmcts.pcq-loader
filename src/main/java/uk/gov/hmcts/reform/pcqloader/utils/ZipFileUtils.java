package uk.gov.hmcts.reform.pcqloader.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pcqloader.exceptions.ZipProcessingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
@Component
public class ZipFileUtils {

    private static final String ZIP_FOLDER_POSTFIX = ".zip";

    private static final String METADATA_FILE_NAME = "metadata.json";

    public Boolean confirmFileCanBeCreated(File blobFile) {
        File blobFolder = blobFile.getParentFile();
        if ((blobFolder.exists() || blobFolder.mkdirs()) && blobFolder.isDirectory()) {
            try {
                if (!blobFile.exists()) {
                    return blobFile.createNewFile() && blobFile.delete();
                }
                return true;

            } catch (IOException e) {
                log.error(
                    "Unable to confirm if {} can be created.",
                    blobFile.getName()
                );
            }
        }
        return false;
    }

    public File unzipBlobDownloadZipFile(File blobDownload) {
        if (blobDownload.exists()
            && blobDownload.isFile()
            && blobDownload.getPath().toLowerCase(Locale.ENGLISH).endsWith(ZIP_FOLDER_POSTFIX)) {
            try (ZipFile zipFile = new ZipFile(blobDownload.getAbsoluteFile())) {
                File outputDir = new File(FilenameUtils.removeExtension(blobDownload.getPath()));
                initialiseDirectory(outputDir);
                Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
                zipEntries.asIterator().forEachRemaining(entry -> {
                    try {
                        String simpleName = FilenameUtils.getName(entry.getName());
                        if (!entry.isDirectory() && simpleName.charAt(0) != '.') {
                            log.info("Found zip content: " + entry.getName());
                            var fileToCreate = outputDir.toPath().resolve(simpleName);
                            Files.copy(zipFile.getInputStream(entry), fileToCreate);
                        }
                    } catch (IOException ioe) {
                        log.error("An error occured while unzipping file from blob storage",ioe);
                        throw new ZipProcessingException("Unable to unpack zip file "
                                                             + blobDownload.getName(), ioe);
                    }
                });
                return outputDir;

            } catch (IOException ioe) {
                throw new ZipProcessingException("Unable to read zip file " + blobDownload.getName(), ioe);
            }

        } else {
            throw new ZipProcessingException("Unable to process blob zip file " + blobDownload.getName());
        }
    }

    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.CollapsibleIfStatements"})
    public void deleteFilesFromLocalStorage(File zipFile, File unzippedFiles) {

        if (zipFile != null && !zipFile.delete()) {
            log.warn("Zip file {} not removed from local storage.", zipFile.getName());
        }

        if (unzippedFiles != null) {
            File[] files = unzippedFiles.listFiles();
            for (File file : Objects.requireNonNull(files)) {
                if (!file.delete()) {
                    log.warn("File {} not removed from local storage.", file.getName());
                }
            }
            if (!unzippedFiles.delete()) {
                log.warn("File {} not removed from local storage.", unzippedFiles.getName());
            }
        }

    }

    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.UseVarargs"})
    public File getMetaDataFile(File[] unzippedFiles) {
        File metaDataFile = null;
        for (File file : unzippedFiles) {
            if (METADATA_FILE_NAME.equals(file.getName())) {
                metaDataFile = file;
            }
        }

        return metaDataFile;
    }

    public String readAllBytesFromFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    private void initialiseDirectory(File directory) throws IOException {
        if (directory.exists() || directory.mkdirs()) {
            FileUtils.cleanDirectory(directory);
        }
    }
}
