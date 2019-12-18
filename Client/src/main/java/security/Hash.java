package security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Hash {
    public static byte[] hash(byte[] data) throws NoSuchAlgorithmException, NoSuchProviderException {
        return MessageDigest.getInstance("SHA256","BC").digest(data);
    }
}
