package root.model;

import com.google.gson.JsonElement;
import root.dto.CreateRequest;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SecData {
    @Column(nullable = false, unique = true, length = 4096)
    private String publicKey;
    @Column(nullable = false, length = 4096)
    private String publicDH;
    @Column(nullable = false, length = 4096)
    private String privateDH;
    @Column(nullable = false, length = 4096)
    private String signature;

    public SecData(){}

    public SecData(CreateRequest request) {
        this.publicKey = request.getEncodedPubKey();
        this.publicDH = request.getEncodedPubDH();
        this.privateDH = request.getEncodedPrvDH();
        this.signature = request.getEncodedSig();
    }

    public SecData(JsonElement description) {
        this.publicKey = description.getAsJsonObject().get("publicKey").getAsString();
        this.publicDH = description.getAsJsonObject().get("publicDH").getAsString();
        this.privateDH = description.getAsJsonObject().get("privateDH").getAsString();
        this.signature = description.getAsJsonObject().get("signature").getAsString();
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicDH() {
        return publicDH;
    }

    public void setPublicDH(String publicDH) {
        this.publicDH = publicDH;
    }

    public String getPrivateDH() {
        return privateDH;
    }

    public void setPrivateDH(String privateDH) {
        this.privateDH = privateDH;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
