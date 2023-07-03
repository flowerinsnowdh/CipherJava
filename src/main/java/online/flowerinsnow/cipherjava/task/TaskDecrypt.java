package online.flowerinsnow.cipherjava.task;

import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.exception.DoCipherException;
import online.flowerinsnow.cipherjava.exception.EncodingParsingException;
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
import java.security.PrivateKey;

public class TaskDecrypt implements ITask {
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
        BIReturn<Cipher, Integer> cipherFromParams = ParameterUtils.getCipherFromParametersWithPrint(Cipher.DECRYPT_MODE, key); // 从程序参数自动获得加密密码
        if (cipherFromParams.getValue2() != 0) { // 出现错误，用错误代码退出程序；上方方法中已经输出了错误信息
            return cipherFromParams.getValue2();
        }
        Cipher cipher = cipherFromParams.getValue1();


        // 执行加密和输出
        if (Parameters.STRING.getValue() != null) { // 字符串输入模式
            // 开始加密
            byte[] result; // 最终得到的结果
            try {
                result = cipher.doFinal(Parameters.DATA_TYPE.getValue().decode(Parameters.STRING.getValue().getBytes(StandardCharsets.US_ASCII))); // 保存加密得到的结果
            } catch (BadPaddingException e) {
                return LanguageUtils.printCannotDecrypt();
            } catch (IllegalBlockSizeException e) {
                throw new UnexceptedException(e);
            } catch (EncodingParsingException e) {
                return LanguageUtils.printInvalidCipher();
            }


            // 开始输出
            return printResult(outputFile, result);
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
                // 开始解密和输出
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
        BIReturn<PrivateKey, Integer> keyResult = ParameterUtils.getPrivateKeyFromParametersWithPrint();
        if (keyResult.getValue2() != 0) { // 退出代码不为0，说明出现问题
            return keyResult.getValue2();
        }
        PrivateKey key = keyResult.getValue1(); // 加密用密钥


        // 获取加密算法和填充
        BIReturn<Cipher, Integer> cipherResult = ParameterUtils.getCipherFromParametersWithPrint(Cipher.PRIVATE_KEY, key); // 从程序参数自动获得加密密码
        if (cipherResult.getValue2() != 0) { // 退出代码不为0，说明出现问题
            return cipherResult.getValue2();
        }
        Cipher cipher = cipherResult.getValue1(); // 非对称密钥


        // 开始加密和输出
        if (Parameters.STRING.getValue() != null) { // 字符串模式
            byte[] result; // 用于存放最终结果


            // 获取源内容
            byte[] src; // 通过指定编码获取字符串字节
            try {
                src = Parameters.DATA_TYPE.getValue().decode(Parameters.STRING.getValue().getBytes(StandardCharsets.US_ASCII));
            } catch (EncodingParsingException e) {
                return LanguageUtils.printInvalidCipher();
            }


            if (Parameters.WRAPPING.getValue() == Unit.UNIT) { // 密钥封装法
                byte len = src[0];
                if (len < 1) {
                    return LanguageUtils.printInvalidCipher();
                }
                byte[] encryptedAES = new byte[len];
                byte[] encryptedSource = new byte[src.length - len - 1];


                System.arraycopy(src, 1, encryptedAES, 0, len);
                System.arraycopy(src, len + 1, encryptedSource, 0, src.length - len - 1);


                // 先解密对称密钥
                byte[] aes; // 对称密钥内容
                try {
                    aes = cipher.doFinal(encryptedAES);
                } catch (IllegalBlockSizeException e) {
                    throw new UnexceptedException(e);
                } catch (BadPaddingException e) {
                    return LanguageUtils.printCannotDecrypt();
                }

                SecretKeySpec keySpec = new SecretKeySpec(aes, Algorithm.AES.keyName); // 对称密钥


                // 对称加密
                Cipher aesCipher;
                try {
                    aesCipher = Cipher.getInstance(Algorithm.AES.cipherName);
                    aesCipher.init(Cipher.DECRYPT_MODE, keySpec);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                    throw new UnexceptedException(e);
                }

                try {
                    result = aesCipher.doFinal(encryptedSource);
                } catch (IllegalBlockSizeException e) {
                    throw new UnexceptedException(e);
                } catch (BadPaddingException e) {
                    return LanguageUtils.printInvalidCipher();
                }
            } else { // 不使用密钥封装法
                try {
                    result = cipher.doFinal(src); // 直接用非对称密钥加密源数据
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    throw new UnexceptedException(e);
                }
            }


            return printResult(outputFile, result);
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
                Cipher aesCipher = null;
                try {
                    ByteBuffer var0 = ByteBuffer.allocate(1);
                    if (input.read(var0) == -1) {
                        return LanguageUtils.printInvalidCipher();
                    }
                    byte len = var0.get(0);
                    var0 = ByteBuffer.allocate(len);
                    //noinspection StatementWithEmptyBody
                    while (input.read(var0) != -1 && var0.position() < len) {
                    }
                    if (var0.position() < len) {
                        return LanguageUtils.printInvalidCipher();
                    }
                    byte[] encryptedAES = var0.array();
                    byte[] aes = cipher.doFinal(encryptedAES);
                    // 对称密钥加密密码
                    aesCipher = CipherUtils.getDefaultAESCipherWithoutException(Cipher.DECRYPT_MODE, new SecretKeySpec(aes, Algorithm.AES.cipherName));
                } catch (IllegalBlockSizeException e) {
                    throw new UnexceptedException(e);
                } catch (BadPaddingException e) {
                    return LanguageUtils.printInvalidCipher();
                } catch (IOException e) {
                    return LanguageUtils.printUnableWrite(outputFile, e.getMessage());
                } finally {
                    if (aesCipher == null) {
                        IOUtils.closeQuietly(output, input);
                    }
                }

                try {
                    return doCloneCipherWithPrint(inputFile, input, aesCipher, outputFile, output, consoleProgress);
                } finally {
                    IOUtils.closeQuietly(output, input);
                }
            } else { // 不使用密钥封装法
                try {
                    return doCloneCipherWithPrint(inputFile, input, cipher, outputFile, output, consoleProgress); // 带输出的用非对称密钥直接对源数据加密并写入目标文件再更新进度条
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
            //noinspection DuplicatedCode
            while (input.read(buffer) != -1) {
                buffer.flip();
                byte[] encryptedText = buffer.array();
                byte[] clearText = cipher.update(encryptedText, 0, buffer.limit());
                try {
                    //noinspection ResultOfMethodCallIgnored
                    output.write(ByteBuffer.wrap(clearText));
                } catch (IOException e) {
                    throw new DoCipherException(true, e);
                }
                consoleProgress.setCurrent(consoleProgress.getCurrent() + buffer.limit());
                buffer.clear();
            }
            //noinspection ResultOfMethodCallIgnored
            output.write(ByteBuffer.wrap(cipher.doFinal()));
            System.out.println();
        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DoCipherException(e);
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
            } else if (e.getCause() instanceof BadPaddingException paddingException) {
                return LanguageUtils.printCannotDecrypt();
            }
            throw new UnexceptedException(); // 加密模式下不会出现其他异常
        }
    }

    private int printResult(@Nullable Path outputFile,  byte[] result) {
        // 开始输出
        String charset = Parameters.CHARSET.getValue(); // 从参数获取编码
        if (outputFile == null) { // 输出到控制台
            try {
                Language.printf(System.out, Language.Encrypt.RESULT, new String(result, charset));
                return 0;
            } catch (UnsupportedEncodingException e) {
                return LanguageUtils.printUnsupportedCharset(charset);
            }
        } else { // 输出到文件
            try {
                Files.write(outputFile, result);
                return 0;
            } catch (IOException e) {
                return LanguageUtils.printUnableWrite(outputFile, e.getMessage());
            }
        }
    }
}
