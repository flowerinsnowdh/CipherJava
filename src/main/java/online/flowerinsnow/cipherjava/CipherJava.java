package online.flowerinsnow.cipherjava;

import cc.carm.lib.configuration.EasyConfiguration;
import cc.carm.lib.configuration.hocon.HOCONFileConfigProvider;
import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.exception.ArgumentException;
import online.flowerinsnow.cipherjava.exception.UnexceptedException;
import online.flowerinsnow.cipherjava.object.ASNIColour;
import online.flowerinsnow.cipherjava.parameter.Parameters;
import online.flowerinsnow.cipherjava.parameter.type.Algorithm;
import online.flowerinsnow.cipherjava.parameter.type.DataType;
import online.flowerinsnow.cipherjava.parameter.type.Unit;
import online.flowerinsnow.cipherjava.task.TaskDecrypt;
import online.flowerinsnow.cipherjava.task.TaskEncrypt;
import online.flowerinsnow.cipherjava.task.TaskNewKey;
import online.flowerinsnow.cipherjava.util.ConditionUtils;
import online.flowerinsnow.cipherjava.util.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.security.Security;
import java.util.Locale;
import java.util.Map;

public class CipherJava {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
            loadLanguage();
        } catch (Exception e) {
            System.err.println(ASNIColour.RED.code + "Could not load language file." + ASNIColour.RESET);
            e.printStackTrace();
            System.exit(-1);
        }

        Security.addProvider(new BouncyCastleProvider());

        try {
            parseArguments(args);
        } catch (ArgumentException e) {
            if (e.getCause() instanceof NumberFormatException ex) {
                Language.printf(System.err, Language.Error.Parameters.NUMBER_FORMAT, ex.getMessage());
            } else if (e instanceof ArgumentException.ValueMissing ex) {
                Language.printf(System.err, Language.Error.Parameters.VALUE_MISSING, ex.getMessage());
            } else if (e instanceof ArgumentException.NoSuchEnum ex) {
                Language.printf(System.err, Language.Error.Parameters.NO_SUCH_ENUM, ex.getParameter(), ex.getValue());
                ex.getValid().forEach(valid -> System.err.println("\t- " + valid));
            }
            System.exit(-3);
        }

        int exit = runTask();
        long usedTime = System.currentTimeMillis() - startTime;
        if (exit == 0) {
            Language.printf(System.out, Language.Exit.ZERO, usedTime);
        } else {
            Language.printf(System.err, Language.Exit.NON_ZERO, usedTime, exit);
        }
        System.exit(exit);
    }

    public static void loadLanguage() throws Exception {
        HOCONFileConfigProvider provider = EasyConfiguration.from(FileUtils.getJarFile().resolveSibling("language.conf").toFile());
        provider.initialize(Language.class);
        provider.reload();
    }

    private static void parseArguments(String[] args) throws ArgumentException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--encrypt", "-e" -> Parameters.ENCRYPT.setValue(Unit.UNIT);
                case "--decrypt", "-d" -> Parameters.DECRYPT.setValue(Unit.UNIT);
                case "--newkey", "-n" -> Parameters.NEW_KEY.setValue(Unit.UNIT);
                case "--length", "-l" -> {
                    validateRequireNextArgument(args, i);
                    try {
                        Parameters.LENGTH.setValue(Integer.parseInt(args[i + 1]));
                    } catch (NumberFormatException e) {
                        throw new ArgumentException(e);
                    }
                    i++;
                }
                case "--seed" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.SEED.setValue(args[i + 1]);
                    i++;
                }
                case "--string", "-s" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.STRING.setValue(args[i + 1]);
                    i++;
                }
                case "--file", "-f" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.FILE.setValue(Paths.get(args[i + 1]));
                    i++;
                }
                case "--algorithm", "-a" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.ALGORITHM.setValue(enumByName(Algorithm.BY_NAME, "--algorithm", args[i + 1]));
                    i++;
                }
                case "--keybase64" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.KEY_BASE64.setValue(args[i + 1]);
                    i++;
                }
                case "--keyhex" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.KEY_HEX.setValue(args[i + 1]);
                    i++;
                }
                case "--key", "-k" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.KEY.setValue(args[i + 1]);
                    i++;
                }
                case "--keyfilebase64" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.KEY_FILE_BASE64.setValue(Paths.get(args[i + 1]));
                    i++;
                }
                case "--keyfilehex" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.KEY_FILE_HEX.setValue(Paths.get(args[i + 1]));
                    i++;
                }
                case "--keyfile" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.KEY_FILE.setValue(Paths.get(args[i + 1]));
                    i++;
                }
                case "--output", "-o" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.OUTPUT.setValue(Paths.get(args[i + 1]));
                    i++;
                }
                case "--publicout" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.PUBLIC_OUTPUT.setValue(Paths.get(args[i + 1]));
                    i++;
                }
                case "--datatype", "-t" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.DATA_TYPE.setValue(enumByName(DataType.BY_NAME, "--datatype", args[i + 1]));
                    i++;
                }
                case "--charset" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.CHARSET.setValue(args[i + 1]);
                    i++;
                }
                case "--cipher", "-c" -> {
                    validateRequireNextArgument(args, i);
                    Parameters.CIPHER.setValue(args[i + 1]);
                    i++;
                }
                case "--warpping", "-w" -> Parameters.WRAPPING.setValue(Unit.UNIT);
                default -> throw new ArgumentException.UnknownArgument(args[i]);
            }
        }

        if (Parameters.LENGTH.getValue() == null) {
            switch (Parameters.ALGORITHM.getValue()) {
                case AES -> Parameters.LENGTH.setValue(16);
                case DES -> Parameters.LENGTH.setValue(56);
                case RSA -> Parameters.LENGTH.setValue(256);
                case EC -> Parameters.LENGTH.setValue(32);
                default -> throw new UnexceptedException();
            }
        }
        if (Parameters.CIPHER.getValue() == null) {
            Parameters.CIPHER.setValue(Parameters.ALGORITHM.getValue().cipherName);
        }
    }

    private static int runTask() {
        // --encrypt --decrypt --newkey 同时必须存在一个且最多存在一个
        if (!ConditionUtils.oneMatch(() -> Parameters.ENCRYPT.getValue() == Unit.UNIT, () -> Parameters.DECRYPT.getValue() == Unit.UNIT, () -> Parameters.NEW_KEY.getValue() == Unit.UNIT)) {
            Language.printf(System.err, Language.Error.Parameters.RUN_MODE_COUNT);
            return -2;
        }

        if (Parameters.ENCRYPT.getValue() == Unit.UNIT) {
            return new TaskEncrypt().runTask();
        }

        if (Parameters.DECRYPT.getValue() == Unit.UNIT) {
            return new TaskDecrypt().runTask();
        }

        if (Parameters.NEW_KEY.getValue() == Unit.UNIT) {
            return new TaskNewKey().runTask();
        }
        throw new UnexceptedException();
    }

    private static void validateRequireNextArgument(@NotNull String[] args, int index) throws ArgumentException.ValueMissing {
        if (index + 1 >= args.length) {
            throw new ArgumentException.ValueMissing(args[index]);
        }
    }

    private static <T> T enumByName(@NotNull Map<String, T> data, @NotNull String parameter, @NotNull String key) throws ArgumentException.NoSuchEnum {
        T value = data.get(key.toLowerCase(Locale.ROOT));
        if (value == null) {
            throw new ArgumentException.NoSuchEnum(parameter, key, data.keySet());
        }
        return value;
    }
}
