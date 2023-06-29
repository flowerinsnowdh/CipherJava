package online.flowerinsnow.cipherjava.exception;

import java.io.IOException;
import java.nio.file.Path;

public class ReadKeyException extends RuntimeException {
    public ReadKeyException() {
        super();
    }

    public ReadKeyException(String message) {
        super(message);
    }

    public ReadKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadKeyException(Throwable cause) {
        super(cause);
    }

    public static class IO extends ReadKeyException {
        private Path file;

        public IO() {
            super();
        }

        public IO(String message) {
            super(message);
        }

        public IO(String message, IOException cause) {
            super(message, cause);
        }

        public IO(Path file) {
            this.file = file;
        }

        public IO(Path file, String message) {
            super(message);
            this.file = file;
        }

        public IO(Path file, IOException cause) {
            super(cause);
            this.file = file;
        }

        public IO(Path file, String message, IOException cause) {
            super(message, cause);
            this.file = file;
        }

        public IO(IOException cause) {
            super(cause);
        }

        public Path getFile() {
            return file;
        }

        public void setFile(Path file) {
            this.file = file;
        }

        @Override
        public synchronized IOException getCause() {
            return (IOException) super.getCause();
        }
    }

    public static class ParseFailed extends ReadKeyException {
        public ParseFailed() {
            super();
        }

        public ParseFailed(String message) {
            super(message);
        }

        public ParseFailed(String message, Throwable cause) {
            super(message, cause);
        }

        public ParseFailed(Throwable cause) {
            super(cause);
        }
    }

    public static class InvalidKey extends ReadKeyException {
        public InvalidKey() {
            super();
        }

        public InvalidKey(String message) {
            super(message);
        }

        public InvalidKey(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidKey(Throwable cause) {
            super(cause);
        }
    }
}
