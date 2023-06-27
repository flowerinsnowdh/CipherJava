package online.flowerinsnow.cipherjava.exception;

/**
 * 当出现了不应该出现的错误时抛出
 * 一般来说，不需要捕获，应为它本身就不应该出现
 * 多用于占位
 */
public class UnexceptedException extends RuntimeException {
    public UnexceptedException() {
        super();
    }

    public UnexceptedException(String message) {
        super(message);
    }

    public UnexceptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexceptedException(Throwable cause) {
        super(cause);
    }
}
