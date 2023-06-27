package online.flowerinsnow.cipherjava.test;

import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;

public class KeySizeTest {
    @Test
    public void test() throws NoSuchAlgorithmException {
        for (int i = 0; i < 64*8; i += 8) {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
                kpg.initialize(i);
                kpg.generateKeyPair();
                System.out.println("- " + (i / 8));
            } catch (InvalidParameterException | ProviderException ignored) {
            }
        }

//        for (int i = 0; i < 10240*8; i += 8) {
//            try {
//                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//                kpg.initialize(i);
//                System.out.println("- " + (i / 8));
//            } catch (InvalidParameterException ignored) {
//            }
//        }
    }
}
