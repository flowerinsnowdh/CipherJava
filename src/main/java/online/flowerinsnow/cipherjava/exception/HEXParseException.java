package online.flowerinsnow.cipherjava.exception;

/**
 * 当十六进制解析失败时抛出
 */
public class HEXParseException extends EncodingParsingException {
    public HEXParseException() {
        super();
    }

    public HEXParseException(String message) {
        super(message);
    }

    public HEXParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public HEXParseException(Throwable cause) {
        super(cause);
    }

    /**
     * 当字符串长度不是2的倍数时抛出
     */
    public static class StringLengthIsNotAMultipleOf2 extends HEXParseException {
        public StringLengthIsNotAMultipleOf2() {
            super();
        }

        public StringLengthIsNotAMultipleOf2(String message) {
            super(message);
        }

        public StringLengthIsNotAMultipleOf2(String message, Throwable cause) {
            super(message, cause);
        }

        public StringLengthIsNotAMultipleOf2(Throwable cause) {
            super(cause);
        }
    }

    /**
     * 当某个字符不是十六进制时抛出
     */
    public static class CharacterIsNotHexadecimal extends HEXParseException {
        public CharacterIsNotHexadecimal() {
            super();
        }

        public CharacterIsNotHexadecimal(String message) {
            super(message);
        }

        public CharacterIsNotHexadecimal(String message, Throwable cause) {
            super(message, cause);
        }

        public CharacterIsNotHexadecimal(Throwable cause) {
            super(cause);
        }
    }
}
