package online.flowerinsnow.cipherjava.util;

import online.flowerinsnow.cipherjava.CipherJava;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileUtils {
    private FileUtils() {
    }

    public static Path getJarFile() {
        String path = CipherJava.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if ("sun.nio.fs.WindowsFileSystem".equals(FileSystems.getDefault().getClass().getName())) {
            path = path.substring(1);
        }
        return Paths.get(path);
    }
}
