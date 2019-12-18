package root.dto;

public class CreateRequest {
    private String uuid;
    private String encodedPubKey;
    private String encodedPubDH;
    private String encodedPrvDH;
    private String encodedSig;

    public String getEncodedPubKey() {
        return encodedPubKey;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEncodedPubDH() {
        return encodedPubDH;
    }

    public String getEncodedPrvDH() {
        return encodedPrvDH;
    }

    public String getEncodedSig() {
        return encodedSig;
    }
}
