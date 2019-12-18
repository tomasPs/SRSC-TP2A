package security;

import utils.StoreManager;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PBEManager {
    private int iterationCount;
    SecretKeyFactory keyFact;

    public PBEManager() throws NoSuchProviderException, NoSuchAlgorithmException {
        this.iterationCount = 2048;
        this.keyFact = SecretKeyFactory.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC", "BC");
    }

    public byte[] cipher(
        String password,
        byte[] data
    ) throws InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        PBEKeySpec pbeSpec = new PBEKeySpec(password.toCharArray());
        Key sKey = keyFact.generateSecret(pbeSpec);

        byte[] salt = Hash.hash(password.getBytes()); //todo CHANGE
        Cipher cEnc = Cipher.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC", "BC");
        cEnc.init(Cipher.ENCRYPT_MODE, sKey, new PBEParameterSpec(salt, iterationCount));

        return cEnc.doFinal(data);
    }

    public byte[] decipher(
        String password,
        byte[] data
    ) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        PBEKeySpec pbeSpec = new PBEKeySpec(password.toCharArray());
        Key sKey = keyFact.generateSecret(pbeSpec);

        byte[] salt = Hash.hash(password.getBytes());  //todo CHANGE
        Cipher cDec = Cipher.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC", "BC");
        cDec.init(Cipher.DECRYPT_MODE, sKey, new PBEParameterSpec(salt, iterationCount));

        return cDec.doFinal(data);
    }

    public static String getPasswordFromKey(Key key) throws NoSuchProviderException, NoSuchAlgorithmException {
        byte[] hash = Hash.hash(key.getEncoded());
        return Base64.getEncoder().encodeToString(hash);
    }

    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        String test = "Eu sou um pato um grande pato dfadsfg adgadfg aergadrfg adfgh adfgh adkfjhgaiuhrg paerh gpahergpahdfgphuaer pghaprgh aperhg paeurhgpaeurhgpaerhu gpaehrg aerhgpahergpahe rgpaheur gpahergpaehrh43534htq34hgpqhregqaehutq34;htqerghqephp34itprghq34pth Eu sou um pato um grande pato dfadsfg adgadfg aergadrfg adfgh adfgh adkfjhgaiuhrg paerh gpahergpahdfgphuaer pghaprgh aperhg paeurhgpaeurhgpaerhu gpaehrg aerhgpahergpahe rgpaheur gpahergpaehrh43534htq34hgpqhregqaehutq34;htqerghqephp34itprghq34pth Eu sou um pato um grande pato dfadsfg adgadfg aergadrfg adfgh adfgh adkfjhgaiuhrg paerh gpahergpahdfgphuaer pghaprgh aperhg paeurhgpaeurhgpaerhu gpaehrg aerhgpahergpahe rgpaheur gpahergpaehrh43534htq34hgpqhregqaehutq34;htqerghqephp34itprghq34pth Eu sou um pato um grande pato dfadsfg adgadfg aergadrfg adfgh adfgh adkfjhgaiuhrg paerh gpahergpahdfgphuaer pghaprgh aperhg paeurhgpaeurhgpaerhu gpaehrg aerhgpahergpahe rgpaheur gpahergpaehrh43534htq34hgpqhregqaehutq34;htqerghqephp34itprghq34pth";

        byte[] ci = new PBEManager().cipher("pass123",test.getBytes());
        String encoded = Base64.getEncoder().encodeToString(ci);

        ci = Base64.getDecoder().decode(encoded);
        byte[] di = new PBEManager().decipher("pass123",ci);

        String out = new String(di);
        System.out.println(out);
        System.out.println(out.equals(test));

    }
}
