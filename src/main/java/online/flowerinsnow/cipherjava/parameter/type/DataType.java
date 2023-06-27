package online.flowerinsnow.cipherjava.parameter.type;

import online.flowerinsnow.cipherjava.exception.EncodingParsingException;
import online.flowerinsnow.cipherjava.util.IntUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public enum DataType {
    BASE64("base64") {
        @Override
        public byte[] encode(byte[] bytes) {
            return Base64.getEncoder().encode(bytes);
        }

        @Override
        public byte[] decode(byte[] bytes) throws EncodingParsingException {
            try {
                return Base64.getDecoder().decode(bytes);
            } catch (IllegalArgumentException e) {
                throw new EncodingParsingException(e);
            }
        }
    },
    /**
     * 十六进制字符串
     */
    HEX("hex") {
        @Override
        public byte[] encode(byte[] bytes) {
            return IntUtils.hex(bytes).getBytes(StandardCharsets.US_ASCII);
        }

        @Override
        public byte[] decode(byte[] bytes) throws EncodingParsingException {
            return IntUtils.parseHEX(new String(bytes, StandardCharsets.US_ASCII));
        }
    },
    BIN("bin") {
        @Override
        public byte[] encode(byte[] bytes) {
            byte[] copy = new byte[bytes.length];
            System.arraycopy(bytes, 0, copy, 0, copy.length);
            return copy;
        }

        @Override
        public byte[] decode(byte[] bytes) throws EncodingParsingException {
            byte[] copy = new byte[bytes.length];
            System.arraycopy(bytes, 0, copy, 0, copy.length);
            return copy;
        }
    };

    @NotNull public final String name;

    @NotNull public static final HashMap<String, DataType> BY_NAME = new HashMap<>();

    DataType(@NotNull String name) {
        this.name = name;
    }

    public abstract byte[] encode(byte[] bytes);
    public abstract byte[] decode(byte[] bytes) throws EncodingParsingException;

    static {
        for (DataType value : values()) {
            BY_NAME.put(value.name, value);
        }
    }
}
