package security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

public class AES {
    private static byte[] ivBytes = new byte[]{
        0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01,
        0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01};

    public static byte[] cipher(byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding","BC");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return cipher.doFinal(data);
    }

    public static byte[] decipher(
        byte[] data,
        Key key
    ) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(data);
    }

    public static Key byteToKey(byte[] data)
    {
        data = Arrays.copyOf(data,16);
        return new SecretKeySpec(data, "AES");
    }
}
