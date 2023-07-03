package online.flowerinsnow.cipherjava.task;

import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.exception.DoCipherException;
import online.flowerinsnow.cipherjava.exception.UnexceptedException;
import online.flowerinsnow.cipherjava.object.ConsoleProgress;
import online.flowerinsnow.cipherjava.object.returnable.BIReturn;
import online.flowerinsnow.cipherjava.parameter.Parameters;
import online.flowerinsnow.cipherjava.parameter.type.Algorithm;
import online.flowerinsnow.cipherjava.parameter.type.Unit;
import online.flowerinsnow.cipherjava.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

public class TaskEncrypt implements ITask {
    @Override
    public int runTask() {
        int exitCode = ValidateUtils.checkEncryptDecryptOneMatchWithPrint();
        if (exitCode != 0) {
            return exitCode;
        }

        Algorithm algorithm = Parameters.ALGORITHM.getValue(); // 算法

        if (algorithm.symmetrical) {
            return symmetrical();
        } else {
            return asymmetrical();
        }
    }

    /**
     * 对称加密模式
     *
     * @return 退出代码
     */
    private int symmetrical() {
        @Nullable Path outputFile = Parameters.OUTPUT.getValue(); // 输出文件

        
        // 获取密钥
        BIReturn<SecretKeySpec, Integer> keyResult = ParameterUtils.getSymmetricalKeyFromParametersWithPrint();
        if (keyResult.getValue2() != 0) { // 退出代码不为0，说明出现问题
            return keyResult.getValue2();
        }
        SecretKeySpec key = keyResult.getValue1(); // 加密用密钥

        
        // 获取加密算法和填充方法
        BIReturn<Cipher, Integer> cipherFromParams = ParameterUtils.getCipherFromParametersWithPrint(Cipher.ENCRYPT_MODE, key); // 从程序参数自动获得加密密码
        if (cipherFromParams.getValue2() != 0) { // 出现错误，用错误代码退出程序；上方方法中已经输出了错误信息
            return cipherFromParams.getValue2();
        }
        Cipher cipher = cipherFromParams.getValue1();
        
        
        // 执行加密和输出
        if (Parameters.STRING.getValue() != null) { // 字符串输入模式
            // 开始加密
            String charset = Parameters.CHARSET.getValue(); // 从参数获取编码
            byte[] result; // 最终得到的结果
            try {
                result = cipher.doFinal(Parameters.STRING.getValue().getBytes(charset)); // 保存加密得到的结果
            } catch (UnsupportedEncodingException e) {
                return LanguageUtils.printUnsupportedCharset(charset);
            } catch (IllegalBlockSizeException | BadPaddingException e) { // 加密时不会抛出这些异常，忽略
                throw new UnexceptedException(e);
            }
            
            
            // 开始输出
            if (outputFile == null) { // 输出到控制台
                Language.printf(System.out, Language.Encrypt.RESULT, new String(Parameters.DATA_TYPE.getValue().encode(result), StandardCharsets.US_ASCII));
                return 0;
            } else { // 输出到文件
                try {
                    Files.write(outputFile, Parameters.DATA_TYPE.getValue().encode(result));
                    return 0;
                } catch (IOException e) {
                    return LanguageUtils.printUnableWrite(outputFile, e.getMessage());
                }
            }
        } else if (Parameters.FILE.getValue() != null) { // 文件输入模式
            // 检查：文件模式下 --output 输出文件是必须的
            {
                int i;
                if ((i = ValidateUtils.outputRequiredInFileModeWithPrint()) != 0) {
                    return i;
                }
            }

            
            // 准备文件
            Path inputFile = Parameters.FILE.getValue(); // 获取输入文件
            ConsoleProgress consoleProgress = new ConsoleProgress(inputFile.toFile().length()); // 进度条

            
            // 准备流
            FileChannel input; // 源文件输入流
            FileChannel output; // 输出文件输出流
            
            // 打开流
            try {
                input = FileChannel.open(inputFile, StandardOpenOption.READ);
            } catch (IOException e) {
                return LanguageUtils.printUnableRead(inputFile, e.getMessage());
            }
            
            try {
                //noinspection DataFlowIssue 上方已判断过不会是null
                output = FileChannel.open(outputFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                IOUtils.closeQuietly(input);
                return LanguageUtils.printUnableWrite(outputFile, e.getMessage());
            }
            
            
            try {
                // 开始加密和输出
                return doCloneCipherWithPrint(inputFile, input, cipher, outputFile, output, consoleProgress); // 带输出的用密钥对源数据加密并写入目标文件再更新进度条
            } finally {
                IOUtils.closeQuietly(output, input);
            }
        } else {
            throw new UnexceptedException(); // 已经确定过不会执行到这里了
        }
    }

    /**
     * 非对称加密模式
     *
     * @return 退出代码
     */
    public int asymmetrical() {
        @Nullable Path outputFile = Parameters.OUTPUT.getValue(); // 输出文件


        // 获取密钥
        BIReturn<PublicKey, Integer> keyResult = ParameterUtils.getPublicKeyFromParametersWithPrint();
        if (keyResult.getValue2() != 0) { // 退出代码不为0，说明出现问题
            return keyResult.getValue2();
        }
        PublicKey key = keyResult.getValue1(); // 加密用密钥


        // 获取加密算法和填充
        BIReturn<Cipher, Integer> cipherResult = ParameterUtils.getCipherFromParametersWithPrint(Cipher.PUBLIC_KEY, key); // 从程序参数自动获得加密密码
        if (cipherResult.getValue2() != 0) { // 退出代码不为0，说明出现问题
            return cipherResult.getValue2();
        }
        Cipher cipher = cipherResult.getValue1(); // 非对称密钥


        // 开始加密和输出
        if (Parameters.STRING.getValue() != null) { // 字符串模式
            byte[] result; // 用于存放最终结果


            // 获取源内容
            String charset = Parameters.CHARSET.getValue(); // 从程序参数中获取指定编码
            byte[] string; // 通过指定编码获取的字符串字节
            try {
                string = Parameters.STRING.getValue().getBytes(charset); // 通过指定编码获取字符串字节
            } catch (UnsupportedEncodingException e) { // 不支持的编码
                Language.printf(System.err, Language.Error.Parameters.UNSUPPORTED_CHARSET, charset);
                return -3;
            }


            if (Parameters.WRAPPING.getValue() == Unit.UNIT) { // 密钥封装法
                // 先生成对称密钥
                SecureRandom sr = new SecureRandom();
                byte[] aes = new byte[16]; // 对称密钥内容
                sr.nextBytes(aes);
                SecretKeySpec keySpec = new SecretKeySpec(aes, Algorithm.AES.keyName); // 对称密钥


                // 对称加密
                Cipher aesCipher;
                try {
                    aesCipher = Cipher.getInstance(Algorithm.AES.cipherName);
                    aesCipher.init(Cipher.ENCRYPT_MODE, keySpec);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                    throw new UnexceptedException(e);
                }


                // 开始加密
                try {
                    byte[] encryptedAES = cipher.doFinal(aes); // 用非对称密钥加密后的对称密钥
                    byte[] encryptedSource = aesCipher.doFinal(string); // 用对称密钥加密后的源数据
                    // 结果 = 密钥长度(2bytes)+源数据密文+
                    result = new byte[1 + encryptedAES.length + encryptedSource.length];
                    /*
                    encryptedAES = [a, a, a, a]
                    encryptedSource = [b, b, b, b, b, b, b]
                    result = [4, a, a, a, a, b, b, b, b, b, b, b]
                     */
                    result[0] = (byte) encryptedAES.length;
                    System.arraycopy(encryptedAES, 0, result, 1, encryptedSource.length);
                    System.arraycopy(encryptedSource, 0, result, encryptedAES.length + 1, encryptedSource.length);
                } catch (IllegalBlockSizeException | BadPaddingException e) { // 加密模式下不会出现这些异常
                    throw new UnexceptedException(e);
                }
            } else { // 不使用密钥封装法
                try {
                    result = cipher.doFinal(string); // 直接用非对称密钥加密源数据
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    throw new UnexceptedException(e);
                }
            }


            // 开始输出
            if (outputFile == null) { // 输出到控制台
                return ParameterUtils.encodeAndWriteConsole(Language.Encrypt.RESULT, Parameters.DATA_TYPE.getValue().encode(result));
            } else { // 输出到文件
                return ParameterUtils.encodeAndTryWritingFileWithFeedback(outputFile, Parameters.DATA_TYPE.getValue().encode(result));
            }
        } else if (Parameters.FILE.getValue() != null) { // 文件模式
            // 检查：文件模式下 --output 输出文件是必须的
            {
                int i;
                if ((i = ValidateUtils.outputRequiredInFileModeWithPrint()) != 0) {
                    return i;
                }
            }


            Path inputFile = Parameters.FILE.getValue(); // 待读取文件
            ConsoleProgress consoleProgress = new ConsoleProgress(inputFile.toFile().length()); // 进度条


            // 打开流
            FileChannel input;
            FileChannel output;

            try {
                input = FileChannel.open(inputFile, StandardOpenOption.READ);
            } catch (IOException e) {
                return LanguageUtils.printUnableRead(inputFile, e.getMessage());
            }

            try {
                //noinspection DataFlowIssue
                output = FileChannel.open(outputFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                IOUtils.closeQuietly(input);
                return LanguageUtils.printUnableWrite(outputFile, e.getMessage());
            }

            if (Parameters.WRAPPING.getValue() == Unit.UNIT) {
                // 随机生成对称密钥
                SecureRandom random = new SecureRandom();
                byte[] aes = new byte[16];
                random.nextBytes(aes);


                try {
                    // 对称密钥加密密码
                    Cipher aesCipher = CipherUtils.getDefaultAESCipherWithoutException(Cipher.ENCRYPT_MODE, new SecretKeySpec(aes, Algorithm.AES.cipherName));
                    byte[] encryptedAES = cipher.doFinal(aes); // 用非对称密钥加密 AES 密钥
                    //noinspection ResultOfMethodCallIgnored
                    output.write(ByteBuffer.wrap(new byte[]{(byte) aes.length}));
                    //noinspection ResultOfMethodCallIgnored
                    output.write(ByteBuffer.wrap(encryptedAES));
                    try {
                        return doCloneCipherWithPrint(inputFile, input, aesCipher, outputFile, output, consoleProgress);
                    } finally {
                        IOUtils.closeQuietly(output, input);
                    }
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    throw new UnexceptedException(e);
                } catch (IOException e) {
                    return LanguageUtils.printUnableWrite(outputFile, e.getMessage());
                }
            } else { // 不使用密钥封装法
                try {
                    return doCloneCipherWithPrint(inputFile, input, cipher, outputFile, output, consoleProgress);  // 带输出的用非对称密钥直接对源数据加密并写入目标文件再更新进度条
                } finally {
                    IOUtils.closeQuietly(output, input);
                }
            }
        } else {
            throw new UnexceptedException();
        }
    }

    /**
     * 将输入流的内容加密写入输出流
     * 并更新进度条
     *
     * @param input 输入流
     * @param cipher 加密
     * @param output 输出流
     * @param consoleProgress 进度条
     * @throws DoCipherException IO异常、填充异常...具体请使用 {@link Throwable#getCause()} 获取
     */
    public static void doCloneCipher(@NotNull FileChannel input, @NotNull Cipher cipher, @NotNull FileChannel output, @NotNull ConsoleProgress consoleProgress) throws DoCipherException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            while (input.read(buffer) != -1) {
                buffer.flip();
                byte[] clearText = buffer.array();
                byte[] encryptedText = cipher.update(clearText, 0, buffer.limit());
                try {
                    //noinspection ResultOfMethodCallIgnored
                    output.write(ByteBuffer.wrap(encryptedText));
                } catch (IOException e) {
                    throw new DoCipherException(true, e);
                }
                consoleProgress.setCurrent(consoleProgress.getCurrent() + buffer.limit());
                buffer.clear();
            }
//            noinspection ResultOfMethodCallIgnored
            output.write(ByteBuffer.wrap(cipher.doFinal()));
            System.out.println();
        } catch (IOException e) {
            throw new DoCipherException(e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static int doCloneCipherWithPrint(@NotNull Path inputFile, @NotNull FileChannel input, @NotNull Cipher cipher, @NotNull Path outputFile, @NotNull FileChannel output, @NotNull ConsoleProgress consoleProgress) {
        try {
            doCloneCipher(input, cipher, output, consoleProgress);  // 源数据加密并写入目标文件再更新进度条
            return 0;
        } catch (DoCipherException e) { // 加密、填充或IO异常
            System.out.println();
            if (e.getCause() instanceof IOException ioException) { // IO异常
                if (e.isOutput()) { // 输出时出现的异常
                    return LanguageUtils.printUnableRead(inputFile, ioException.getMessage());
                } else { // 输入时出现的异常
                    return LanguageUtils.printUnableWrite(outputFile, ioException.getMessage());
                }
            }
            throw new UnexceptedException(); // 加密模式下不会出现其他异常
        }
    }
}
