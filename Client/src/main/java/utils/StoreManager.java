package utils;

import utils.propreties.Config;
import utils.propreties.ConfigReader;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class StoreManager {
    private KeyStore keyStore;


    public KeyStore getStore(){
        return keyStore;
    }

    public StoreManager(String store,String type, String password) {
        try {
            keyStore = KeyStore.getInstance(type);
            InputStream keyStoreStream = getClass().getClassLoader().getResourceAsStream(store);
            keyStore.load(keyStoreStream, password.toCharArray());
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

//    public Key getSymmetricKey(
//        String alias,
//        String password
//    ) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
//        return keyStore.getKey(alias+"-se", password.toCharArray());
//    }
//
//    public Key getMacKey(
//        String alias,
//        String password
//    ) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
//        return keyStore.getKey(alias + "-mac", password.toCharArray());
//    }

    public X509Certificate getCert(String alias) throws KeyStoreException {
        X509Certificate cert = (X509Certificate)keyStore.getCertificate(alias);
        return cert;
    }

    public KeyPair getKeyPair(String alias, String password) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        Key key = keyStore.getKey(alias, password.toCharArray());

        Certificate cert = keyStore.getCertificate(alias);
        return new KeyPair(cert.getPublicKey(), (PrivateKey) key);
    }

    public PublicKey getPubKey(String alias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        Certificate cert = keyStore.getCertificate(alias);
        return cert.getPublicKey();
    }

    public PrivateKey getPrivKey(String alias, String password) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        Key key = keyStore.getKey(alias, password.toCharArray());

        return (PrivateKey) key;
    }

    public Certificate[] getCertChain(String alias) throws KeyStoreException {
        return keyStore.getCertificateChain(alias);
    }
}
