package online.flowerinsnow.cipherjava.util;

import online.flowerinsnow.cipherjava.config.Language;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public abstract class LanguageUtils {
    private LanguageUtils() {
    }

    public static int printUnableRead(@NotNull Path file, @NotNull String message) {
        Language.printf(System.err, Language.Error.IO.UNABLE_READ_FILE, file.getFileName(), message);
        return -5;
    }

    public static int printUnableWrite(@NotNull Path file, @NotNull String message) {
        Language.printf(System.err, Language.Error.IO.UNABLE_WRITE_FILE, file.getFileName(), message);
        return -6;
    }

    public static int printOutputIsRequiredInFileMode() {
        Language.printf(System.err, Language.Error.Parameters.OUTPUT_IS_REQUIRED_IN_FILE_MODE);
        return -2;
    }

    public static int printUnsupportedCharset(@NotNull String charset) {
        Language.printf(System.err, Language.Error.Parameters.UNSUPPORTED_CHARSET, charset);
        return -3;
    }

    public static int printCannotDecrypt() {
        Language.printf(System.err, Language.Error.Key.CANNOT_DECRYPT);
        return -10;
    }

    public static int printInvalidCipher() {
        Language.printf(System.err, Language.Error.Parameters.INVALID_CIPHER);
        return -11;
    }
}
