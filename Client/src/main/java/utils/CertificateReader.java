package utils;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateReader {
    public static X509Certificate getCertificate(String cert) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        InputStream stream = CertificateReader.class.getClassLoader().getResourceAsStream(cert);
        return  (X509Certificate) fact.generateCertificate(stream);
    }
}
