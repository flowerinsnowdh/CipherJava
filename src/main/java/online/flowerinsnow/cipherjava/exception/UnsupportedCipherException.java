package online.flowerinsnow.cipherjava.exception;

/**
 * 当Cipher算法或填充不支持时抛出
 */
public class UnsupportedCipherException extends RuntimeException {
    public UnsupportedCipherException() {
        super();
    }

    public UnsupportedCipherException(String message) {
        super(message);
    }

    public UnsupportedCipherException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedCipherException(Throwable cause) {
        super(cause);
    }
}
