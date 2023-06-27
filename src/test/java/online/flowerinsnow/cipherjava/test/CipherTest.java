package online.flowerinsnow.cipherjava.test;

import online.flowerinsnow.cipherjava.CipherJava;
import online.flowerinsnow.cipherjava.config.Language;
import online.flowerinsnow.cipherjava.exception.UnexceptedException;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

public class CipherTest {
    @Test
    public void test() throws Exception {
        CipherJava.loadLanguage();
        SecureRandom sr = new SecureRandom();

//        byte[] key = new byte[11];
//        sr.nextBytes(key);
//        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        /*
        Invalid AES key length: 11 bytes
        java.security.InvalidKeyException: Invalid AES key length: 11 bytes
            at java.base/com.sun.crypto.provider.AESCrypt.init(AESCrypt.java:90)
            at java.base/com.sun.crypto.provider.ElectronicCodeBook.init(ElectronicCodeBook.java:97)
            at java.base/com.sun.crypto.provider.CipherCore.init(CipherCore.java:482)
            at java.base/com.sun.crypto.provider.CipherCore.init(CipherCore.java:400)
            at java.base/com.sun.crypto.provider.AESCipher.engineInit(AESCipher.java:306)
            at java.base/javax.crypto.Cipher.implInit(Cipher.java:867)
            at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:929)
            at java.base/javax.crypto.Cipher.init(Cipher.java:1299)
            at java.base/javax.crypto.Cipher.init(Cipher.java:1236)
            at online.flowerinsnow.cipherjava.test.CipherTest.test(CipherTest.java:21)
         */


//        byte[] key = new byte[11];
//        sr.nextBytes(key);
//        SecretKeySpec keySpec = new SecretKeySpec(key, "DES");
//        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
                /*
        Wrong key size
        java.security.InvalidKeyException: Wrong key size
        	at java.base/com.sun.crypto.provider.DESCrypt.init(DESCrypt.java:536)
        	at java.base/com.sun.crypto.provider.ElectronicCodeBook.init(ElectronicCodeBook.java:97)
        	at java.base/com.sun.crypto.provider.CipherCore.init(CipherCore.java:482)
        	at java.base/com.sun.crypto.provider.CipherCore.init(CipherCore.java:400)
        	at java.base/com.sun.crypto.provider.DESCipher.engineInit(DESCipher.java:187)
        	at java.base/javax.crypto.Cipher.implInit(Cipher.java:867)
        	at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:929)
        	at java.base/javax.crypto.Cipher.init(Cipher.java:1299)
        	at java.base/javax.crypto.Cipher.init(Cipher.java:1236)
        	at online.flowerinsnow.cipherjava.test.CipherTest.test(CipherTest.java:27)
         */

        int length = 11;
        byte[] key = new byte[length];
        sr.nextBytes(key);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        } catch (InvalidKeyException e) {
            if (e.getMessage() != null && e.getMessage().matches("(Invalid AES key length: [0-9]+ bytes|Wrong key size)")) {
                Language.printf(System.err, Language.Error.Parameters.INVALID_KEY_LENGTH, "AES", length);
                return;
            }
            throw new UnexceptedException();
        }
    }
}
