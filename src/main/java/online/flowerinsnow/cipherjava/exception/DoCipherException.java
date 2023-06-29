package online.flowerinsnow.cipherjava.exception;

public class DoCipherException extends RuntimeException {
    private boolean output;

    public DoCipherException() {
        super();
    }

    public DoCipherException(String message) {
        super(message);
    }

    public DoCipherException(String message, Throwable cause) {
        super(message, cause);
    }

    public DoCipherException(Throwable cause) {
        super(cause);
    }

    public DoCipherException(boolean output) {
        super();
        this.output = output;
    }

    public DoCipherException(boolean output, String message) {
        super(message);
        this.output = output;
    }

    public DoCipherException(boolean output, String message, Throwable cause) {
        super(message, cause);
        this.output = output;
    }

    public DoCipherException(boolean output, Throwable cause) {
        super(cause);
        this.output = output;
    }

    /**
     * 如果是输出时抛出的该异常，返回true，否则返回false
     *
     * @return 如果是输出时抛出的该异常，返回true，否则返回false
     */
    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }
}
