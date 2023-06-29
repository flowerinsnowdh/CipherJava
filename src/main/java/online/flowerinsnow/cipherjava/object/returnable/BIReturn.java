package online.flowerinsnow.cipherjava.object.returnable;

import java.util.Objects;

public class BIReturn<A, B> {
    private A value1;
    private B value2;

    public BIReturn(A value1, B value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public A getValue1() {
        return value1;
    }

    public void setValue1(A value1) {
        this.value1 = value1;
    }

    public B getValue2() {
        return value2;
    }

    public void setValue2(B value2) {
        this.value2 = value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BIReturn<?, ?> that = (BIReturn<?, ?>) o;
        return Objects.equals(value1, that.value1) && Objects.equals(value2, that.value2);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (value1 != null ? value1.hashCode() : 0);
        result = 31 * result + (value2 != null ? value2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BIReturn{" +
                "value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }
}
