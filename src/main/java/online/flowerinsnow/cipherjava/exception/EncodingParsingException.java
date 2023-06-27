package online.flowerinsnow.cipherjava.exception;

public class EncodingParsingException extends RuntimeException {
    public EncodingParsingException() {
        super();
    }

    public EncodingParsingException(String message) {
        super(message);
    }

    public EncodingParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncodingParsingException(Throwable cause) {
        super(cause);
    }
}
