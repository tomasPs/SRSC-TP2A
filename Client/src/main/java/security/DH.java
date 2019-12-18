package security;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class DH {

    private static BigInteger g512 = new BigInteger(
        "153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7"
            + "749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b"
            + "410b7a0f12ca1cb9a428cc", 16);
    private static BigInteger p512 = new BigInteger(
        "9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd387"
            + "44d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94b"
            + "f0573bf047a3aca98cdf3b", 16);

    public static KeyPair generateDHParams() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        DHParameterSpec dhParams = new DHParameterSpec(p512, g512);
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
        keyGen.initialize(dhParams);
        return keyGen.genKeyPair();
    }

    public static byte[] generateSecret(
        Key ourSecret,
        Key otherPublic
    ) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement KeyAgree = KeyAgreement.getInstance("DH", "BC");

        KeyAgree.init(ourSecret);
        KeyAgree.doPhase(otherPublic, true);
        return KeyAgree.generateSecret();
    }

    public static byte[] generateSecret(
        BigInteger ourSecret,
        BigInteger otherPublic
    ) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("DH","BC");
        Key privateKey = kf.generatePrivate(new DHPrivateKeySpec(ourSecret,p512,g512));
        Key publicKey = kf.generatePublic(new DHPublicKeySpec(otherPublic, p512, g512));

//        Key our = new SecretKeySpec(ourSecret, "DH");
//        Key other = new SecretKeySpec(otherPublic, "DH");
        return generateSecret(privateKey, publicKey);
    }

    public static byte[] generateSecret(byte[] secret, byte[] publicVal) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException {
        BigInteger s = new BigInteger(secret);
        BigInteger p = new BigInteger(publicVal);

        //TODO Enable this later
//        return generateSecret(s,p);
        return "dfrjgaerjgierjaioet43tih345p2h45pt89q56v8y5nt-8qy5t-q8by543t8qyb54tvq8ytertaeray545".getBytes();
    }
}
