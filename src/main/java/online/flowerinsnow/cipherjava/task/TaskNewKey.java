package online.flowerinsnow.cipherjava.task;

import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.exception.UnexceptedException;
import online.flowerinsnow.cipherjava.parameter.Parameters;
import online.flowerinsnow.cipherjava.parameter.type.Algorithm;
import online.flowerinsnow.cipherjava.util.CipherUtils;
import online.flowerinsnow.cipherjava.util.ParameterUtils;
import org.jetbrains.annotations.Nullable;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
                CipherUtils.testKey(keySpec, algorithm);
            } catch (InvalidKeyException e) {
                // 长度不正确
                if (e.getMessage() != null && e.getMessage().matches("(Invalid AES key length: [0-9]+ bytes|Wrong key size)")) {
                    Language.printf(System.err, Language.Error.Parameters.INVALID_KEY_LENGTH, algorithm.keyName, length);
                    return -3;
                }
                throw new UnexceptedException(e);
            }

            // 验证通过，写入文件
            if (output != null) {
                return ParameterUtils.encodeAndTryWritingFileWithFeedback(output, key);
            } else {
                return ParameterUtils.encodeAndWriteConsole(Language.NewKey.SYMMETRICAL, key);
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
                int ret = ParameterUtils.encodeAndTryWritingFileWithFeedback(output, privateKey.getEncoded());
                if (ret != 0) { // 出现问题，终止
                    return ret;
                }
            } else {
                return ParameterUtils.encodeAndWriteConsole(Language.NewKey.PRIVATE, privateKey.getEncoded());
            }

            // 写入公钥
            Path publicOut = Parameters.PUBLIC_OUTPUT.getValue();
            if (publicOut != null) {
                return ParameterUtils.encodeAndTryWritingFileWithFeedback(publicOut, publicKey.getEncoded());
            } else {
                return ParameterUtils.encodeAndWriteConsole(Language.NewKey.PUBLIC, publicKey.getEncoded());
            }
        }
    }
}
