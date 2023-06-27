package online.flowerinsnow.cipherjava.task;

import cc.carm.lib.configuration.core.value.type.ConfiguredValue;
import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.exception.UnexceptedException;
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
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;

public class TaskNewKey implements ITask {

    @Override
    public int runTask() {
        Algorithm algorithm = Parameters.ALGORITHM.getValue();
        SecureRandom random;
        // 若指定了种子，种子就是指定的种子
        @Nullable String seedString = Parameters.SEED.getValue();
        if (seedString != null) {
            random = new SecureRandom(seedString.getBytes(StandardCharsets.UTF_8));
        } else { // 若没有指定种子，则随机种子
            random = new SecureRandom();
        }

        int length = Parameters.LENGTH.getValue();

        @Nullable Path output = Parameters.OUTPUT.getValue();

        if (algorithm.symmetrical) { // 对称密钥
            byte[] key = new byte[length];
            random.nextBytes(key);

            // 验证
            SecretKeySpec keySpec = new SecretKeySpec(key, algorithm.keyName);
            try {
                Cipher cipher = Cipher.getInstance(algorithm.cipherName);
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            } catch (InvalidKeyException e) {
                // 长度不正确
                if (e.getMessage() != null && e.getMessage().matches("(Invalid AES key length: [0-9]+ bytes|Wrong key size)")) {
                    Language.printf(System.err, Language.Error.Parameters.INVALID_KEY_LENGTH, algorithm.keyName, length);
                    return -3;
                }
                throw new UnexceptedException(e);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new UnexceptedException(e);
            }

            // 验证通过，写入文件
            if (output != null) {
                return encodeAndTryWritingFileWithFeedback(output, key);
            } else {
                encodeAndWriteConsole(Language.NewKey.SYMMETRICAL, key);
                return 0;
            }
        } else { // 非对称密钥
            KeyPairGenerator kpg;
            try {
                kpg = KeyPairGenerator.getInstance(algorithm.keyName);
                kpg.initialize(length * 8, random);
            } catch (InvalidParameterException e) {
                if (e.getMessage() != null && e.getMessage().matches("(No EC parameters available for key size [0-9]+ bits|Key size must be at (most|least) [0-9]+ bits)")) {
                    Language.printf(System.err, Language.Error.Parameters.INVALID_KEY_LENGTH, algorithm.keyName, length);
                    return -3;
                }
                throw new UnexceptedException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new UnexceptedException(e);
            }
            KeyPair kp = kpg.generateKeyPair();
            PrivateKey privateKey = kp.getPrivate();
            PublicKey publicKey = kp.getPublic();

            // 写入私钥
            if (output != null) {
                int ret = encodeAndTryWritingFileWithFeedback(output, privateKey.getEncoded());
                if (ret != 0) { // 出现问题，终止
                    return ret;
                }
            } else {
                encodeAndWriteConsole(Language.NewKey.PRIVATE, privateKey.getEncoded());
            }

            // 写入公钥
            Path publicOut = Parameters.PUBLIC_OUTPUT.getValue();
            if (publicOut != null) {
                return encodeAndTryWritingFileWithFeedback(publicOut, publicKey.getEncoded());
            } else {
                encodeAndWriteConsole(Language.NewKey.PUBLIC, publicKey.getEncoded());
                return 0;
            }
        }

        // --key --keybase64 --keyhex --keyfile --keyfilebase64 --keyfilehex 同时最多存在一个
//        if (!ConditionUtils.timesMatch(0, 1, () -> Arguments.KEY.getValue() != null, () -> Arguments.KEY_BASE64.getValue() != null, () -> Arguments.KEY_HEX.getValue() != null),
//        () -> Arguments.KEY_FILE.getValue() != null, () -> Arguments.KEY_FILE_BASE64.getValue() != null, () -> Arguments.KEY_FILE_HEX.getValue() != null) {
//            Language.printf(System.err, Language.Error.Arguments.KEYS_COUNT);
//            return -2;
//        }
//
//        byte[] key = null;
//        boolean asSeed = false;
//        if (Arguments.KEY.getValue() != null) {
//            key = Arguments.KEY.getValue().getBytes(StandardCharsets.UTF_8);
//            asSeed = true;
//        } else if (Arguments.KEY_BASE64.getValue() != null) {
//            String base64 = Arguments.KEY_BASE64.getValue();
//            try {
//                key = Base64.getDecoder().decode(base64);
//            } catch (IllegalArgumentException e) {
//                Language.printf(System.err, Language.Error.Arguments.INVALID_BASE64, base64);
//                return -8;
//            }
//        } else if (Arguments.KEY_HEX.getValue() != null) {
//            String hex = Arguments.KEY_HEX.getValue();
//            try {
//                key = IntUtils.parseHEX(hex);
//            } catch (HEXParseException e) {
//                Language.printf(System.err, Language.Error.Arguments.INVALID_HEX_STRING, hex);
//                return -8;
//            }
//        } else if (Arguments.KEY_FILE.getValue() != null) {
//            Path keyFile = Arguments.KEY_FILE.getValue();
//            try {
//                key = Files.readAllBytes(keyFile);
//            } catch (IOException e) {
//                Language.printf(System.err, Language.Error.IO.UNABLE_READ_FILE, keyFile.getFileName(), e.getMessage());
//                return -4;
//            }
//        } else if (Arguments.KEY_FILE_BASE64.getValue() != null) {
//            Path keyFile = Arguments.KEY_FILE_BASE64.getValue();
//            byte[] content;
//            try {
//                content = Files.readAllBytes(keyFile);
//            } catch (IOException e) {
//                Language.printf(System.err, Language.Error.IO.UNABLE_READ_FILE, keyFile.getFileName(), e.getMessage());
//                return -4;
//            }
//            try {
//                key = Base64.getDecoder().decode(content);
//            } catch (IllegalArgumentException e) {
//                return -8;
//            }
//        } else if (Arguments.KEY_FILE_HEX.getValue() != null) {
//            Path keyFile = Arguments.KEY_FILE_HEX.getValue();
//            String content;
//            try {
//                content = Files.readString(keyFile, StandardCharsets.US_ASCII);
//            } catch (IOException e) {
//                Language.printf(System.err, Language.Error.IO.UNABLE_READ_FILE, keyFile.getFileName(), e.getMessage());
//                return -4;
//            }
//        }
    }

    private static int encodeAndTryWritingFileWithFeedback(@NotNull Path path, byte[] encoded) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            //noinspection ResultOfMethodCallIgnored
            fc.write(ByteBuffer.wrap(Parameters.OUTPUT_TYPE.getValue().encode(encoded)));
            return 0;
        } catch (IOException e) {
            Language.printf(System.err, Language.Error.IO.UNABLE_WRITE, path.getFileName(), e.getMessage());
            return -6;
        }
    }

    private static void encodeAndWriteConsole(@NotNull ConfiguredValue<String> prefix, byte[] encoded) {
        System.out.print(prefix.getNotNull());
        try {
            System.out.write(Parameters.OUTPUT_TYPE.getValue().encode(encoded));
        } catch (IOException e) {
            throw new UnexceptedException(e);
        }
        System.out.println();
    }
}
