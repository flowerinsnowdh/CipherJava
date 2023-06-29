package online.flowerinsnow.cipherjava.util;

import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.exception.ReadKeyException;
import online.flowerinsnow.cipherjava.exception.UnexceptedException;
import online.flowerinsnow.cipherjava.object.returnable.BIReturn;
import online.flowerinsnow.cipherjava.parameter.Parameters;
import online.flowerinsnow.cipherjava.parameter.type.Algorithm;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;

public abstract class CipherUtils {
    private CipherUtils() {
    }

    @NotNull public static final String INVALID_KEY_LENGTH_PATTERN = "(Invalid AES key length: [0-9]+ bytes|Wrong key size)";

    public static void testKey(@NotNull Key key) throws InvalidKeyException {
        testKey(key, Parameters.ALGORITHM.getValue());
    }

    public static void testKey(@NotNull Key key, @NotNull Algorithm algorithm) throws InvalidKeyException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.cipherName);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new UnexceptedException(e);
        }
    }

    public static @NotNull Cipher getDefaultAESCipherWithoutException(int mode, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(Algorithm.AES.cipherName);
            cipher.init(mode, key);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> @NotNull BIReturn<T, Integer> getKeyOrHandlePrintException(@NotNull Supplier<T> supplier) {
        return getKeyOrHandlePrintException(Parameters.ALGORITHM.getValue(), supplier);
    }

    public static <T> @NotNull BIReturn<T, Integer> getKeyOrHandlePrintException(@NotNull Algorithm algorithm, @NotNull Supplier<T> supplier) {
        try {
            return new BIReturn<>(supplier.get(), 0);
        } catch (ReadKeyException.IO e) { // 发生IO错误
            Language.printf(System.err, Language.Error.IO.UNABLE_READ_FILE, e.getFile().getFileName(), e.getMessage());
            return new BIReturn<>(null, -4);
        } catch (ReadKeyException.ParseFailed | ReadKeyException.InvalidKey e) { // 密钥解析错误或不合法
            if (e instanceof ReadKeyException.InvalidKey && e.getMessage().matches(INVALID_KEY_LENGTH_PATTERN)) {
                // 密钥长度不正确
                Language.printf(System.err, Language.Error.Parameters.INVALID_KEY_LENGTH, Parameters.ALGORITHM.getValue().keyName);
            } else {
                Language.printf(System.err, Language.Error.Key.CANNOT_DECRYPT);
            }
            return new BIReturn<>(null, -8);
        }
    }
}
