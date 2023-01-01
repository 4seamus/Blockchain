package blockchain.utils;

import org.junit.Test;

import static blockchain.utils.CryptUtil.*;
import static org.junit.Assert.*;

public class CryptUtilTest {

    @Test
    public void verifySignatureValid() {
        String transferSender = "A";
        String transferRecipient = "B";
        long amount = 20;
        String transactionContent = transferSender + transferRecipient + amount;
        try {
            RSAKeyPair rsaKeyPair = createKeyPair(1024);
            String signature = sign(transactionContent, rsaKeyPair.PrivateKey());
            assertTrue(verifySignature(transactionContent, signature, rsaKeyPair.PublicKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifySignatureInvalid() {
        String INVALID_TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCv6VQZqNbT/4W+tu/dw6L/EjZc1uRMihS1gcrwkVt7xHTmnmS039OgqLkV/o3EbEIUiUc1/z99KnVSaWZr53Ts9VOG8eNWA18GEXNr7IaO7cdUn8pMs2ILJ8rSeYa39U4w4fm+M/W0jQ00lGGtvJ/frbKPEg8mBSviFHv+o4igqwIDAQAB";
        String transferSender = "C";
        String transferRecipient = "D";
        long amount = 73;
        String transactionContent = transferSender + transferRecipient + amount;
        try {
            RSAKeyPair rsaKeyPair = createKeyPair(1024);
            String signature = sign(transactionContent, rsaKeyPair.PrivateKey());
            assertFalse(verifySignature(transactionContent, signature, INVALID_TEST_PUBLIC_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}