package online.flowerinsnow.cipherjava.object.returnable;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Objects;

/**
 * @see online.flowerinsnow.cipherjava.util.ParameterUtils#getCipherFromParametersWithPrint(int, Key)
 */
public class CipherFromParametersWithPrintReturn {
    private Cipher cipher;
    private int exitCode;

    public CipherFromParametersWithPrintReturn(Cipher cipher) {
        this(cipher, 0);
    }

    public CipherFromParametersWithPrintReturn(int exitCode) {
        this(null, exitCode);
    }

    public CipherFromParametersWithPrintReturn(Cipher cipher, int exitCode) {
        this.cipher = cipher;
        this.exitCode = exitCode;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CipherFromParametersWithPrintReturn that = (CipherFromParametersWithPrintReturn) o;
        return exitCode == that.exitCode && Objects.equals(cipher, that.cipher);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (cipher != null ? cipher.hashCode() : 0);
        result = 31 * result + exitCode;
        return result;
    }

    @Override
    public String toString() {
        return "CipherFromParametersWithPrintReturn{" +
                "cipher=" + cipher +
                ", exitCode=" + exitCode +
                '}';
    }
}
