package security;

import java.security.*;

public class SignatureUtil {
    public static byte[] getSignature(PrivateKey privateKey, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        Signature s = Signature.getInstance("SHA512withRSA","BC");

        s.initSign(privateKey);
        s.update(data);
        return s.sign();
    }

    public static boolean checkSign(PublicKey publicKey, byte[] message, byte[] signature) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        Signature s = Signature.getInstance("SHA512withRSA", "BC");

        s.initVerify(publicKey);
        s.update(message);
        return s.verify(signature);
    }
}
