package uk.gov.hmcts.reform.pcqloader.exceptions;

public class BlobProcessingException extends RuntimeException {

    private static final long serialVersionUID = 4L;

    public BlobProcessingException(String message) {
        super(message);
    }

    public BlobProcessingException(String message, Exception exp) {
        super(message, exp);
    }
}
