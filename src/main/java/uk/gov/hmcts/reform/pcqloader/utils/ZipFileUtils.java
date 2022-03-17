package uk.gov.hmcts.reform.pcqloader.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pcqloader.exceptions.ZipProcessingException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
                return checkUnzipFileSize(zipEntries,outputDir,zipFile);

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

    public File checkUnzipFileSize(Enumeration<? extends ZipEntry> zipEntries,
                                   File outputDir,ZipFile zipFile) throws IOException {
        int thresholdEntries = 10_000;
        int thresholdSize = 1_000_000_000; // 1 GB
        double thresholdRatio = 10;
        int totalSizeArchive = 0;
        int totalEntryArchive = 0;
        InputStream in = null;
        OutputStream out = null;
        byte[] buffer = new byte[1248];

        while (zipEntries.hasMoreElements()) {
            ZipEntry ze = zipEntries.nextElement();
            String simpleName = FilenameUtils.getName(ze.getName());
            if (!ze.isDirectory() && simpleName.charAt(0) != '.') {
                log.info("Found zip content: " + ze.getName());
                var fileToCreate = outputDir.toPath().resolve(simpleName);
                try {
                    in = zipFile.getInputStream(ze);
                    out = Files.newOutputStream(Paths.get(fileToCreate.toString()));
                    totalEntryArchive++;

                    int bytes;
                    double totalSizeEntry = 0;
                    bytes = in.read(buffer);
                    while (bytes > 0) { // Compliant
                        out.write(buffer, 0, bytes);
                        totalSizeEntry += bytes;
                        totalSizeArchive += bytes;
                        bytes = in.read(buffer);
                        double compressionRatio = totalSizeEntry / ze.getCompressedSize();
                        if (compressionRatio > thresholdRatio) {
                            // ratio between compressed and uncompressed data is highly suspicious,
                            // looks like a Zip Bomb Attack
                            throw new ZipProcessingException(
                                "Ratio between compressed and uncompressed data is highly suspicious "
                                    + ze.getName());
                        }
                    }
                } catch (IOException e) {
                    log.error("An error occured while unzipping file from blob storage",e);
                    throw new ZipProcessingException("Unable to unpack zip file "
                                                         + ze.getName(), e);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }
                if (totalSizeArchive > thresholdSize) {
                    // the uncompressed data size is too much for the application resource capacity
                    throw new ZipProcessingException(
                        "Uncompressed data size is too much for the application resource capacity :"
                            + totalSizeArchive + " : "
                            + ze.getName());
                }

                if (totalEntryArchive > thresholdEntries) {
                    // too much entries in this archive, can lead to inodes exhaustion of the system
                    throw new ZipProcessingException(
                        "Too much entries in this archive, can lead to inodes exhaustion of the system :"
                            + totalEntryArchive + " : "
                            + ze.getName());
                }
                Files.copy(zipFile.getInputStream(ze), fileToCreate, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return outputDir;
    }
}
