package online.flowerinsnow.cipherjava.parameter.type;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;

public enum Algorithm {
    AES("aes", "AES", "AES/ECB/PKCS5Padding", true),
    DES("des", "DES", "DES/ECB/PKCS5Padding", true),
    RSA("rsa", "RSA", "RSA", false),
    EC("ec", "EC", "ECIES", false);
    /**
     * 用于识别的名称
     */
    @NotNull public final String name;
    /**
     * 算法名，用于密钥生成
     */
    @NotNull public final String keyName;
    /**
     * 算法和填充组合，用于加解密
     */
    @NotNull public final String cipherName;

    @NotNull public static final HashMap<String, Algorithm> BY_NAME = new HashMap<>();
    public final boolean symmetrical;

    Algorithm(@NotNull String name, @NotNull String keyName, @NotNull String cipherName, boolean symmetrical) {
        this.name = name;
        this.keyName = keyName;
        this.cipherName = cipherName;
        this.symmetrical = symmetrical;
    }

    static {
        for (Algorithm value : values()) {
            BY_NAME.put(value.name.toLowerCase(Locale.ROOT), value);
        }
    }
}
