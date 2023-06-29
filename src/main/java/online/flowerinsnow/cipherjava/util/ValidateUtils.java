package online.flowerinsnow.cipherjava.util;

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
}
