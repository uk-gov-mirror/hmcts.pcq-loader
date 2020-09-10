package uk.gov.hmcts.reform.pcqloader.exceptions;

public class ZipProcessingException extends RuntimeException {

    private static final long serialVersionUID = 4L;

    public ZipProcessingException(String message) {
        super(message);
    }

    public ZipProcessingException(String message, Exception exp) {
        super(message, exp);
    }
}
