package online.flowerinsnow.cipherjava.util;

import cc.carm.lib.configuration.core.value.type.ConfiguredValue;
import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.exception.HEXParseException;
import online.flowerinsnow.cipherjava.exception.ReadKeyException;
import online.flowerinsnow.cipherjava.exception.UnexceptedException;
import online.flowerinsnow.cipherjava.exception.UnsupportedCipherException;
import online.flowerinsnow.cipherjava.object.returnable.BIReturn;
import online.flowerinsnow.cipherjava.parameter.Parameters;
import online.flowerinsnow.cipherjava.parameter.type.Algorithm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.Function;

public abstract class ParameterUtils {
    private ParameterUtils() {
    }

    /**
     * 从程序参数获取密钥并测试
     *
     * @param password 参数为密码时
     * @param base64 参数为Base64密钥时
     * @param hex 参数为十六进制字符串密钥时
     * @param binFile 参数为二进制文件时
     * @param base64File 参数为Base64文件时
     * @param hexFile 参数为十六进制字符串文件时
     * @param test 测试结果退出代码
     * @return 从程序参数获取的密钥，或异常
     * @param <T> 密钥类型
     */
    public static <T extends Key> @NotNull T getKeyFromParametersAndTest(
            @NotNull Function<String, T> password,
            @NotNull Function<String, T> base64,
            @NotNull Function<String, T> hex,
            @NotNull Function<Path, T> binFile,
            @NotNull Function<Path, T> base64File,
            @NotNull Function<Path, T> hexFile,
            @NotNull Function<T, ReadKeyException> test
    ) throws ReadKeyException {
        T key;
        if (Parameters.KEY.getValue() != null) {
            key = password.apply(Parameters.KEY.getValue());
        } else if (Parameters.KEY_BASE64.getValue() != null) {
            key = base64.apply(Parameters.KEY_BASE64.getValue());
        } else if (Parameters.KEY_HEX.getValue() != null) {
            key = hex.apply(Parameters.KEY_HEX.getValue());
        } else if (Parameters.KEY_FILE.getValue() != null) {
            key = binFile.apply(Parameters.KEY_FILE.getValue());
        } else if (Parameters.KEY_FILE_BASE64.getValue() != null) {
            key = base64File.apply(Parameters.KEY_FILE_BASE64.getValue());
        } else if (Parameters.KEY_FILE_HEX.getValue() != null) {
            key = hexFile.apply(Parameters.KEY_FILE_HEX.getValue());
        } else {
            throw new UnexceptedException();
        }
        ReadKeyException exception = test.apply(key);
        if (exception != null) {
            throw exception;
        }
        return key;
    }

    public static SecretKeySpec getSymmetricalKeyFromParameters() throws ReadKeyException {
        Algorithm algorithm = Parameters.ALGORITHM.getValue();

        return getKeyFromParametersAndTest(
                password -> { // 密码
                    SecureRandom random;
                    // 若指定了种子，种子就是指定的种子
                    @Nullable String seedString = password;
                    if (seedString != null) {
                        random = new SecureRandom(seedString.getBytes(StandardCharsets.UTF_8));
                    } else { // 若没有指定种子，则随机种子
                        random = new SecureRandom();
                    }
                    // 将密码作为种子生成
                    int length = Parameters.LENGTH.getValue();
                    byte[] key = new byte[length];
                    random.nextBytes(key);
                    return new SecretKeySpec(key, algorithm.keyName);
                },
                base64 -> { // Base64
                    try {
                        return new SecretKeySpec(Base64.getDecoder().decode(base64), algorithm.keyName);
                    } catch (IllegalArgumentException e) {
                        Language.printf(System.err, Language.Error.Parameters.INVALID_BASE64, base64);
                        throw new ReadKeyException.ParseFailed(e);
                    }
                },
                hex -> { // 十六进制字符串
                    try {
                        return new SecretKeySpec(IntUtils.parseHEX(hex), algorithm.keyName);
                    } catch (HEXParseException e) {
                        Language.printf(System.err, Language.Error.Parameters.INVALID_HEX_STRING, hex);
                        throw new ReadKeyException.ParseFailed(e);
                    }
                },
                binFile -> { // 二进制文件
                    try {
                        return new SecretKeySpec(Files.readAllBytes(binFile), algorithm.keyName);
                    } catch (IOException e) {
                        throw new ReadKeyException.IO(binFile, e);
                    }
                },
                base64File -> { // Base64文件
                    byte[] content;
                    try {
                        content = Files.readAllBytes(base64File);
                    } catch (IOException e) {
                        throw new ReadKeyException.IO(base64File, e);
                    }
                    try {
                        return new SecretKeySpec(Base64.getDecoder().decode(content), algorithm.keyName);
                    } catch (IllegalArgumentException e) {
                        throw new ReadKeyException.ParseFailed(e);
                    }
                },
                hexFile -> { // 十六进制字符串文件
                    String content;
                    try {
                        content = Files.readString(hexFile, StandardCharsets.US_ASCII);
                    } catch (IOException e) {
                        throw new ReadKeyException.IO(hexFile, e);
                    }
                    try {
                        return new SecretKeySpec(IntUtils.parseHEX(content), algorithm.keyName);
                    } catch (HEXParseException e) {
                        throw new ReadKeyException.ParseFailed(e);
                    }
                },
                keySpec -> { // 测试
                    try {
                        CipherUtils.testKey(keySpec, algorithm);
                        return null;
                    } catch (InvalidKeyException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    }
                }
        );
    }

    /**
     * 从程序参数中获取对称密钥，并自动处理返回消息
     *
     * @return 对称密钥或退出代码
     */
    public static @NotNull BIReturn<SecretKeySpec, Integer> getSymmetricalKeyFromParametersWithPrint() {
        return CipherUtils.getKeyOrHandlePrintException(ParameterUtils::getSymmetricalKeyFromParameters);
    }

    public static PublicKey getPublicKeyFromParameters() throws ReadKeyException {
        Algorithm algorithm = Parameters.ALGORITHM.getValue();

        return getKeyFromParametersAndTest(
                password -> { // 密码
                    SecureRandom random = new SecureRandom(password.getBytes(StandardCharsets.UTF_8));
                    try {
                        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm.keyName);
                        kpg.initialize(Parameters.LENGTH.getValue(), random);
                        return kpg.generateKeyPair().getPublic();
                    } catch (InvalidParameterException | NoSuchAlgorithmException e) {
                        if (e instanceof InvalidParameterException && e.getMessage().matches(CipherUtils.INVALID_KEY_LENGTH_PATTERN)) {
                            throw new ReadKeyException.InvalidKey(e);
                        }
                        throw new UnexceptedException();
                    }
                },
                base64 -> { // Base64
                    try {
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePublic(keySpec);
                    } catch (IllegalArgumentException e) { // Base64解码错误
                        throw new ReadKeyException.ParseFailed(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                hex -> { // 十六进制字符串
                    try {
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(IntUtils.parseHEX(hex), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePublic(keySpec);
                    } catch (HEXParseException e) { // 十六进制字符串解码错误
                        throw new ReadKeyException.ParseFailed(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                binFile -> { // 二进制文件
                    try {
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Files.readAllBytes(binFile), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePublic(keySpec);
                    } catch (IOException e) { // 读取文件错误
                        throw new ReadKeyException.IO(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                base64File -> { // Base64文件
                    try {
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(Files.readAllBytes(base64File)), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePublic(keySpec);
                    } catch (IOException e) { // 读取文件错误
                        throw new ReadKeyException.IO(e);
                    } catch (IllegalArgumentException e) { // Base64解码错误
                        throw new ReadKeyException.ParseFailed(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                hexFile -> { // 十六进制字符串文件
                    try {
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(IntUtils.parseHEX(Files.readString(hexFile, StandardCharsets.US_ASCII)), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePublic(keySpec);
                    } catch (IOException e) { // 读取文件错误
                        throw new ReadKeyException.IO(e);
                    } catch (HEXParseException e) { // 十六进制字符串解码错误
                        throw new ReadKeyException.ParseFailed(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                keySpec -> { // 测试
                    try {
                        CipherUtils.testKey(keySpec, algorithm);
                        return null;
                    } catch (InvalidKeyException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    }
                }
        );
    }

    /**
     * 从程序参数中获取对称密钥，并自动处理返回消息
     *
     * @return 对称密钥或退出代码
     */
    public static @NotNull BIReturn<PublicKey, Integer> getPublicKeyFromParametersWithPrint() {
        return CipherUtils.getKeyOrHandlePrintException(ParameterUtils::getPublicKeyFromParameters);
    }

    public static PrivateKey getPrivateKeyFromParameters() throws ReadKeyException {
        Algorithm algorithm = Parameters.ALGORITHM.getValue();

        return getKeyFromParametersAndTest(
                password -> { // 密码
                    SecureRandom random = new SecureRandom(password.getBytes(StandardCharsets.UTF_8));
                    try {
                        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm.keyName);
                        kpg.initialize(Parameters.LENGTH.getValue(), random);
                        return kpg.generateKeyPair().getPrivate();
                    } catch (InvalidParameterException | NoSuchAlgorithmException e) {
                        if (e instanceof InvalidParameterException && e.getMessage().matches(CipherUtils.INVALID_KEY_LENGTH_PATTERN)) {
                            throw new ReadKeyException.InvalidKey(e);
                        }
                        throw new UnexceptedException();
                    }
                },
                base64 -> { // Base64
                    try {
                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePrivate(keySpec);
                    } catch (IllegalArgumentException e) { // Base64解码错误
                        throw new ReadKeyException.ParseFailed(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                hex -> { // 十六进制字符串
                    try {
                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(IntUtils.parseHEX(hex), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePrivate(keySpec);
                    } catch (HEXParseException e) { // 十六进制字符串解码错误
                        throw new ReadKeyException.ParseFailed(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                binFile -> { // 二进制文件
                    try {
                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Files.readAllBytes(binFile), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePrivate(keySpec);
                    } catch (IOException e) { // 读取文件错误
                        throw new ReadKeyException.IO(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                base64File -> { // Base64文件
                    try {
                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(Files.readAllBytes(base64File)), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePrivate(keySpec);
                    } catch (IOException e) { // 读取文件错误
                        throw new ReadKeyException.IO(e);
                    } catch (IllegalArgumentException e) { // Base64解码错误
                        throw new ReadKeyException.ParseFailed(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                hexFile -> { // 十六进制字符串文件
                    try {
                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(IntUtils.parseHEX(Files.readString(hexFile, StandardCharsets.US_ASCII)), algorithm.keyName);
                        KeyFactory kf = KeyFactory.getInstance(algorithm.keyName);
                        return kf.generatePrivate(keySpec);
                    } catch (IOException e) { // 读取文件错误
                        throw new ReadKeyException.IO(e);
                    } catch (HEXParseException e) { // 十六进制字符串解码错误
                        throw new ReadKeyException.ParseFailed(e);
                    } catch (InvalidKeySpecException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnexceptedException(e);
                    }
                },
                keySpec -> { // 测试
                    try {
                        CipherUtils.testKey(keySpec, algorithm);
                        return null;
                    } catch (InvalidKeyException e) {
                        throw new ReadKeyException.InvalidKey(e);
                    }
                }
        );
    }

    /**
     * 从程序参数中获取对称密钥，并自动处理返回消息
     *
     * @return 对称密钥或退出代码
     */
    public static @NotNull BIReturn<PrivateKey, Integer> getPrivateKeyFromParametersWithPrint() {
        return CipherUtils.getKeyOrHandlePrintException(ParameterUtils::getPrivateKeyFromParameters);
    }

    public static int encodeAndTryWritingFileWithFeedback(@NotNull Path path, byte[] encoded) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            //noinspection ResultOfMethodCallIgnored
            fc.write(ByteBuffer.wrap(Parameters.DATA_TYPE.getValue().encode(encoded)));
            return 0;
        } catch (IOException e) {
            Language.printf(System.err, Language.Error.IO.UNABLE_WRITE_FILE, path.getFileName(), e.getMessage());
            return -6;
        }
    }

    public static int encodeAndWriteConsole(@NotNull ConfiguredValue<String> prefix, byte[] encoded) {
        System.out.print(prefix.getNotNull());
        try {
            System.out.write(Parameters.DATA_TYPE.getValue().encode(encoded));
        } catch (IOException e) {
            throw new UnexceptedException(e);
        }
        System.out.println();
        return 0;
    }

    /**
     * 从参数中获取加密算法和套件
     * 将会忽略非法密钥异常，请提前自行判断
     *
     * @param mode Cipher模式
     * @param key 密钥
     * @return Cipher
     * @throws UnsupportedCipherException 当加密算法或套件不支持时抛出
     */
    public static Cipher getCipherFromParameters(int mode, @NotNull Key key) throws UnsupportedCipherException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(Parameters.CIPHER.getValue());
            cipher.init(mode, key);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new UnsupportedCipherException(e);
        } catch (InvalidKeyException e) {
            throw new UnexceptedException(e);
        }
    }

    public static BIReturn<Cipher, Integer> getCipherFromParametersWithPrint(int mode, @NotNull Key key) {
        try {
            return new BIReturn<>(getCipherFromParameters(mode, key), 0);
        } catch (UnsupportedCipherException e) {
            Language.printf(System.err, Language.Error.Parameters.UNSUPPORTED_CIPHER, Parameters.CIPHER.getValue());
            return new BIReturn<>(null, -9);
        }
    }
}
