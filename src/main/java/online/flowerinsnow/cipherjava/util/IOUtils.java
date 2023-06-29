package online.flowerinsnow.cipherjava.util;

public abstract class IOUtils {
    private IOUtils() {
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
