package online.flowerinsnow.cipherjava.util;

import online.flowerinsnow.cipherjava.exception.HEXParseException;

public abstract class IntUtils {
    private IntUtils() {
    }

    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String hex(byte b) {
        byte left = (byte) ((b >> 4) & 0xF);
        byte right = (byte) (b & 0xF);
        return Character.toString(HEX[left]) + HEX[right];
    }

    public static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(hex(b));
        }
        return sb.toString();
    }

    public static byte[] parseHEX(String s) {
        if (s.length() / 2 != 0) {
            throw new HEXParseException.StringLengthIsNotAMultipleOf2();
        }

        byte[] result = new byte[s.length() / 2];

        for (int i = 0; i < result.length; i++) {
            char leftChar = s.charAt(i * 2);
            char rightChar = s.charAt(i * 2 + 1);
            Byte left = null;
            Byte right = null;
            for (byte b = 0; b < HEX.length; b++) {
                if (HEX[b] == leftChar) {
                    left = b;
                }
                if (HEX[b] == rightChar) {
                    right = b;
                }
            }

            if (left == null || right == null) {
                throw new HEXParseException.CharacterIsNotHexadecimal(s);
            }

            result[i] = (byte) ((left << 4) | right);
        }
        return result;
    }

    public static Byte[] packing(byte[] array) {
        Byte[] bytes = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bytes[i] = array[i];
        }
        return bytes;
    }

    /**
     * 将包装类型Byte数组解包
     * 为null的数值将填充0
     *
     * @param array 待解包的包装类型Byte数组
     * @return 解包的Byte数组
     */
    public static byte[] unpacking(Byte[] array) {
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bytes[i] = (array[i] != null ? array[i] : 0);
        }
        return bytes;
    }
}
