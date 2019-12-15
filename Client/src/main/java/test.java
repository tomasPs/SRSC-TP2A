import utils.KeyStoreManager;

import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

public class test {
    static KeyStoreManager manager = new KeyStoreManager();

    public static void main(String[] args) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        KeyPair pair = manager.getKeyPair();

        String pubKeyStringHEX = toHex(pair.getPublic().getEncoded());
        String priKeyStringHEX = toHex(pair.getPrivate().getEncoded());


        System.out.println("---------------- HEXADECIMAL--------------");
        System.out.println("Public Key");
        System.out.println(pubKeyStringHEX);
        System.out.println("------------------------------------------");
        System.out.println("Private Key");
        System.out.println(priKeyStringHEX);
        System.out.println("------------------------------------------");
    }

    public static String toHex(byte[] data, int length) {
        String digits = "0123456789abcdef";

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i != length; i++) {
            int v = data[i] & 0xff;

            buf.append(digits.charAt(v >> 4));
            buf.append(digits.charAt(v & 0xf));
        }

        return buf.toString();
    }

    public static String toHex(byte[] data) {
        return toHex(data, data.length);
    }
}
