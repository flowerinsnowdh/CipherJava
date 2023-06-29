package online.flowerinsnow.cipherjava.object.returnable;

import java.security.Key;
import java.util.Objects;
import java.util.function.Function;

/**
 * @see online.flowerinsnow.cipherjava.util.ParameterUtils#getKeyFromParametersAndTest(Function, Function, Function, Function, Function, Function, Function)
 */
public class KeyFromParametersAndTestReturn<T extends Key> {
    private T key;
    private int exitCode;

    public KeyFromParametersAndTestReturn(T key) {
        this(key, 0);
    }

    public KeyFromParametersAndTestReturn(int exitCode) {
        this(null, exitCode);
    }

    public KeyFromParametersAndTestReturn(T key, int exitCode) {
        this.key = key;
        this.exitCode = exitCode;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
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
        KeyFromParametersAndTestReturn<?> that = (KeyFromParametersAndTestReturn<?>) o;
        return exitCode == that.exitCode && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + exitCode;
        return result;
    }

    @Override
    public String toString() {
        return "KeyFromParametersAndTestReturn{" +
                "key=" + key +
                ", exitCode=" + exitCode +
                '}';
    }
}
