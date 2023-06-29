package online.flowerinsnow.cipherjava.object.returnable;

import online.flowerinsnow.cipherjava.util.ParameterUtils;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

/**
 * @see ParameterUtils#getSymmetricalKeyFromParametersWithPrint()
 */
public class SymmetricalKeyFromParametersWithPrintReturn {
    private SecretKeySpec keySpec;
    private int exitCode;

    public SymmetricalKeyFromParametersWithPrintReturn(SecretKeySpec keySpec, int exitCode) {
        this.keySpec = keySpec;
        this.exitCode = exitCode;
    }

    public SecretKeySpec getKeySpec() {
        return keySpec;
    }

    public void setKeySpec(SecretKeySpec keySpec) {
        this.keySpec = keySpec;
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
        SymmetricalKeyFromParametersWithPrintReturn that = (SymmetricalKeyFromParametersWithPrintReturn) o;
        return exitCode == that.exitCode && Objects.equals(keySpec, that.keySpec);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (keySpec != null ? keySpec.hashCode() : 0);
        result = 31 * result + exitCode;
        return result;
    }

    @Override
    public String toString() {
        return "SymmetricalKeyFromParametersWithPrintReturn{" +
                "keySpec=" + keySpec +
                ", exitCode=" + exitCode +
                '}';
    }
}
