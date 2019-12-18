package utils.propreties;

import java.util.List;

public class Config {
    private String keyStorePassword;
    private String keyStore;
    private String trustStore;
    private String trustStorePassword;
    private String authMode;
    private List<String> protocols;
    private List<String> cipherSuites;
    private String serverCert;

    public String getServerCert() {
        return serverCert;
    }

    public void setServerCert(String serverCert) {
        this.serverCert = serverCert;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public List<String> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<String> protocols) {
        this.protocols = protocols;
    }

    public List<String> getCipherSuites() {
        return cipherSuites;
    }

    public void setCipherSuites(List<String> cipherSuites) {
        this.cipherSuites = cipherSuites;
    }

    @Override
    public String toString() {
        return "Config{" +
            "keyStorePassword='" + keyStorePassword + '\'' +
            ", keyStore='" + keyStore + '\'' +
            ", trustStore='" + trustStore + '\'' +
            ", trustStorePassword='" + trustStorePassword + '\'' +
            ", authMode=" + authMode +
            ", protocols=" + protocols +
            ", cipherSuites=" + cipherSuites +
            '}';
    }
}
