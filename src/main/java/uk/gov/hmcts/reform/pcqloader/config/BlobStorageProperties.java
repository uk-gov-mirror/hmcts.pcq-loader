package uk.gov.hmcts.reform.pcqloader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "storage")
public class BlobStorageProperties {

    private String blobStorageDownloadPath;

    private String blobPcqContainer;

    private String blobPcqRejectedContainer;

    /*
    private Integer blobCopyTimeoutInMillis;

    private Integer blobLeaseTimeout;

    private Integer blobCopyPollingDelayInMillis;

    private Integer blobLeaseAcquireDelayInSeconds;
    */

    private String processedFolderName;

    public String getBlobPcqContainer() {
        return blobPcqContainer;
    }

    public void setBlobPcqContainer(String blobPcqContainer) {
        this.blobPcqContainer = blobPcqContainer;
    }

    public String getBlobStorageDownloadPath() {
        return blobStorageDownloadPath;
    }

    public void setBlobStorageDownloadPath(String blobStorageDownloadPath) {
        this.blobStorageDownloadPath = blobStorageDownloadPath;
    }

    public String getBlobPcqRejectedContainer() {
        return blobPcqRejectedContainer;
    }

    public void setBlobPcqRejectedContainer(String blobPcqRejectedContainer) {
        this.blobPcqRejectedContainer = blobPcqRejectedContainer;
    }

    /*

    public Integer getBlobCopyTimeoutInMillis() {
        return blobCopyTimeoutInMillis;
    }

    public void setBlobCopyTimeoutInMillis(int blobCopyTimeoutInMillis) {
        this.blobCopyTimeoutInMillis = blobCopyTimeoutInMillis;
    }

    public Integer getBlobLeaseTimeout() {
        return blobLeaseTimeout;
    }

    public void setBlobLeaseTimeout(Integer blobLeaseTimeout) {
        this.blobLeaseTimeout = blobLeaseTimeout;
    }

    public Integer getBlobCopyPollingDelayInMillis() {
        return blobCopyPollingDelayInMillis;
    }

    public void setBlobCopyPollingDelayInMillis(int blobCopyPollingDelayInMillis) {
        this.blobCopyPollingDelayInMillis = blobCopyPollingDelayInMillis;
    }

    public Integer getBlobLeaseAcquireDelayInSeconds() {
        return blobLeaseAcquireDelayInSeconds;
    }

    public void setBlobLeaseAcquireDelayInSeconds(Integer blobLeaseAcquireDelayInSeconds) {
        this.blobLeaseAcquireDelayInSeconds = blobLeaseAcquireDelayInSeconds;
    }
    */

    public String getProcessedFolderName() {
        return processedFolderName;
    }

    public void setProcessedFolderName(String processedFolderName) {
        this.processedFolderName = processedFolderName;
    }
}
