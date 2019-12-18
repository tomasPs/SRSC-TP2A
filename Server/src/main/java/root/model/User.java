package root.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import root.dto.CreateRequest;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Embedded
    private UserDescription description;

    public User() { }

    public User(int id, UserDescription description) {
        this.id = id;
        this.description = description;
    }

    public User(CreateRequest request) {
        this.id = 0;
        this.description = new UserDescription(request);
    }

    public User(int id, JsonElement description) {
        this.id = id;
        Gson gson = new Gson();
        this.description = gson.fromJson(description, UserDescription.class);
    }

    public UserDescription getDescription() {
        return description;
    }

    public void setDescription(UserDescription description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMbox() {
        return "mboxes/" + id;
    }

    public String getRbox() {
        return "receipts/" + id;
    }
}
