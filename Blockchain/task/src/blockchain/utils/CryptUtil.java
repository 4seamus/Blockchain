package blockchain.utils;
import java.nio.charset.StandardCharsets;
import java.security.*;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptUtil {
    // applies SHA256 to a string and returns a hash.
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte elem: hash) {
                String hex = Integer.toHexString(0xff & elem);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            System.out.println("## ERROR ## Unable to calculate SHA-256 digest.");
            throw new RuntimeException(e);
        }
    }

    // signs data using the private key
    // Credits:
    //   https://mkyong.com/java/java-digital-signatures-example/
    //   https://stackoverflow.com/questions/5355466/converting-secret-key-into-a-string-and-vice-versa
    public static String sign(String data, String privateKey) throws Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        PrivateKey privateKeyDecoded = decodeBase64PrivateKey(privateKey);

        rsa.initSign(privateKeyDecoded);
        rsa.update(data.getBytes());

        return Base64.getEncoder().encodeToString(rsa.sign());
    }

    public static boolean verifySignature(String data, String base64Signature, String publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA1withRSA");
        PublicKey publicKeyDecoded = decodeBase64PublicKey(publicKey);

        sig.initVerify(publicKeyDecoded);
        sig.update(data.getBytes());

        byte[] signature = Base64.getDecoder().decode(base64Signature);
        return sig.verify(signature);
    }

    private static PrivateKey decodeBase64PrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private static PublicKey decodeBase64PublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static RSAKeyPair createKeyPair(int keyLength) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keyLength);
        KeyPair keyPair = keyGen.generateKeyPair();

        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        return new RSAKeyPair(privateKey, publicKey);
    }
}