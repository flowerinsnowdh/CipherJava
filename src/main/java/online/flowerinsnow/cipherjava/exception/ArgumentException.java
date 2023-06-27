package online.flowerinsnow.cipherjava.exception;

import java.util.Set;

/**
 * 当参数不正确时抛出
 */
public class ArgumentException extends RuntimeException {
    public ArgumentException() {
        super();
    }

    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentException(Throwable cause) {
        super(cause);
    }

    public static class ValueMissing extends ArgumentException {
        public ValueMissing() {
            super();
        }

        public ValueMissing(String message) {
            super(message);
        }

        public ValueMissing(String message, Throwable cause) {
            super(message, cause);
        }

        public ValueMissing(Throwable cause) {
            super(cause);
        }
    }

    public static class NoSuchEnum extends ArgumentException {
        private String parameter;
        private String value;
        private Set<String> valid;

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Set<String> getValid() {
            return valid;
        }

        public void setValid(Set<String> valid) {
            this.valid = valid;
        }

        public NoSuchEnum() {
            super();
        }

        public NoSuchEnum(String message) {
            super(message);
        }

        public NoSuchEnum(String message, Throwable cause) {
            super(message, cause);
        }

        public NoSuchEnum(Throwable cause) {
            super(cause);
        }

        public NoSuchEnum(String parameter, String value, Set<String> valid) {
            this.parameter = parameter;
            this.value = value;
            this.valid = valid;
        }
    }

    public static class UnknownArgument extends ArgumentException {
        public UnknownArgument() {
            super();
        }

        public UnknownArgument(String message) {
            super(message);
        }

        public UnknownArgument(String message, Throwable cause) {
            super(message, cause);
        }

        public UnknownArgument(Throwable cause) {
            super(cause);
        }
    }
}
