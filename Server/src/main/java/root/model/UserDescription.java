package root.model;

import com.google.gson.JsonElement;
import root.dto.CreateRequest;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class UserDescription {
    @Column(nullable = false, unique = true)
    private String uuid;
    @Embedded
    private SecData secData;

    public UserDescription(){}

    public UserDescription(String uuid, SecData secData) {
        this.uuid = uuid;
        this.secData = secData;
    }

    public UserDescription(CreateRequest request) {
        this.uuid = request.getUuid();
        this.secData = new SecData(request);
    }

    public UserDescription(JsonElement description) {
        uuid = description.getAsJsonObject().get("uuid").getAsString();
        this.secData = new SecData(description);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SecData getSecData() {
        return secData;
    }

    public void setSecData(SecData secData) {
        this.secData = secData;
    }
}
