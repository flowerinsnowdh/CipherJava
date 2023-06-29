package online.flowerinsnow.cipherjava.test;

import online.flowerinsnow.cipherjava.object.ConsoleProgress;
import org.junit.jupiter.api.Test;

public class ProgressTest {
    @Test
    public void test() {
        ConsoleProgress cp = new ConsoleProgress(100);
        cp.setCurrent(9);
        cp.setCurrent(10);
    }
}
