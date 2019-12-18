import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import utils.CertificateReader;
import utils.StoreManager;
import utils.propreties.Config;
import utils.propreties.ConfigReader;

import javax.net.ssl.SSLContext;

import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

public class test {
    private static Config config = ConfigReader.getConfig();
    static StoreManager keyStore = new StoreManager(config.getKeyStore(), "JCEKS", config.getKeyStorePassword());
    static StoreManager trustStore = new StoreManager(config.getTrustStore(), "JCEKS", config.getKeyStorePassword());
    static X509Certificate serverCert;

    static {
        try {
            serverCert = CertificateReader.getCertificate(config.getServerCert());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Certificate chain = keyStore.getCert("user1 (rootca)");
        X509Certificate ca = trustStore.getCert("rootca");

        try {
            chain.verify(ca.getPublicKey());
            System.out.println("ALL GOOD");
        } catch (Exception e) {
            System.out.println("ERROR");
        }


        String url = "https://localhost:8443/hello";
        try {
            RestTemplate template = restTemplate(config);

            ResponseEntity<String> response =
                template.getForEntity(url, String.class, Collections.emptyMap());
            System.out.println(response.getBody());
        } catch (ResourceAccessException e) {
            e.printStackTrace();
        }
    }

    private static RestTemplate restTemplate(Config config) throws Exception {

        TrustStrategy strategy = (x509Certificates, auth) -> {
            try {
                X509Certificate ca = trustStore.getCert("rootca");

                for (X509Certificate cert : x509Certificates) {
                    try {
                        cert.checkValidity();
                        ca.checkValidity();
                        cert.verify(ca.getPublicKey());
                        if (cert.getSubjectDN().getName().startsWith("CN=server") && cert.getPublicKey()
                            .equals(serverCert.getPublicKey()))
                            return true;
                    } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
                    }
                }
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            return false;
        };

        SSLContextBuilder builder = new SSLContextBuilder();
        String auth = config.getAuthMode();

        if (auth.equalsIgnoreCase("MUTUAL") || auth.equalsIgnoreCase("SERVER-ONLY"))
            builder.loadTrustMaterial(strategy);
        if (auth.equalsIgnoreCase("MUTUAL") || auth.equalsIgnoreCase("CLIENT-ONLY"))
            builder.loadKeyMaterial(keyStore.getStore(), "password".toCharArray());

        SSLContext sslContext = builder.build();

        String[] protocols = config.getProtocols().toArray(new String[0]);
        String[] suites = config.getCipherSuites().toArray(new String[0]);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
            sslContext,
            protocols,
            suites,
            NoopHostnameVerifier.INSTANCE
        );
        HttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(socketFactory)
            .build();
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
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
