package utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class KeyStoreManager {
    private KeyStore keyStore;

    public KeyStoreManager() {
        this("JCEKS");
    }

    public KeyStoreManager(String type) {
        try {
            keyStore = KeyStore.getInstance(type);
            InputStream keyStoreStream = getClass().getClassLoader().getResourceAsStream("keyStore.jceks");
            keyStore.load(keyStoreStream, "password".toCharArray());
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public Key getSymmetricKey(
        String alias,
        String password
    ) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return keyStore.getKey(alias+"-se", password.toCharArray());
    }

    public Key getMacKey(
        String alias,
        String password
    ) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return keyStore.getKey(alias + "-mac", password.toCharArray());
    }

    public X509Certificate getCert() throws KeyStoreException {
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("cert");
        return cert;
    }

    public KeyPair getKeyPair() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        Key key = keyStore.getKey("cert", "password".toCharArray());

        Certificate cert = keyStore.getCertificate("cert");
        return new KeyPair(cert.getPublicKey(), (PrivateKey) key);
    }
}
