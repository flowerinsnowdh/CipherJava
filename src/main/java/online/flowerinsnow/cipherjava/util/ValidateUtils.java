package online.flowerinsnow.cipherjava.util;

import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.parameter.Parameters;

public abstract class ValidateUtils {
    private ValidateUtils() {
    }

    public static int outputRequiredInFileModeWithPrint() {
        if (Parameters.OUTPUT.getValue() == null) { // 文件模式下 --output 输出文件是必须的
            return LanguageUtils.printOutputIsRequiredInFileMode();
        }
        return 0;
    }

    public static int checkEncryptDecryptOneMatchWithPrint() {
        // --key --keybase64 --keyhex --keyfile --keyfilebase64 --keyfilehex 必须同时存在一个且最多存在一个
        if (!ConditionUtils.oneMatch(() -> Parameters.KEY.getValue() != null, () -> Parameters.KEY_BASE64.getValue() != null, () -> Parameters.KEY_HEX.getValue() != null,
                () -> Parameters.KEY_FILE.getValue() != null, () -> Parameters.KEY_FILE_BASE64.getValue() != null, () -> Parameters.KEY_FILE_HEX.getValue() != null)) {
            Language.printf(System.err, Language.Error.Parameters.KEYS_COUNT);
            return -2;
        }

        // --string --file 必须同时存在一个且最多存在一个
        if (!ConditionUtils.oneMatch(() -> Parameters.STRING.getValue() != null,() -> Parameters.FILE.getValue() != null)) {
            Language.printf(System.err, Language.Error.Parameters.SOURCE_COUNT);
            return -2;
        }
        return 0;
    }
}
