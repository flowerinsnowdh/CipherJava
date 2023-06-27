package online.flowerinsnow.cipherjava.parameter;

import java.util.Objects;

public class Parameter<T> {
    private T value;

    public Parameter() {
    }

    public Parameter(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter<?> parameter = (Parameter<?>) o;
        return Objects.equals(value, parameter.value);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "value=" + value +
                '}';
    }
}
