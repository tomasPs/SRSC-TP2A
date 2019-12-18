package root.service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.exception.ServerErrorException;
import root.model.User;
import root.repository.UserRepository;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public Optional<User> getUser(int id) {
        return repository.findById(id);
    }

//    public Optional<User> getUser(String uuid) {
//        return repository.findByUuid(uuid);
//    }

    public User createUser(User user) {
        final String mboxesPath = "mboxes";
        final String descFilename = "description";

        if (repository.existsByDescriptionUuid(user.getDescription().getUuid()))
            throw new ServerErrorException("User already exists");

        String path = null;

        user = repository.save(user);

        try {
            (new File(user.getMbox())).mkdir();
            (new File(user.getRbox())).mkdir();
        } catch (Exception e) {
            System.err.println("Cannot create directory for user: " + e);
            System.exit(1);
        }

        try {
            Gson gson = new Gson();

            path = mboxesPath + "/" + user.getId() + "/" + descFilename;
            saveOnFile(path, gson.toJson(user.getDescription()));
        } catch (Exception e) {
            System.err.println("Cannot create description file " + path + ": " + e);
            System.exit(1);
        }

        return user;
    }

    private void saveOnFile(String path, String data) throws Exception {
        FileWriter f = new FileWriter(path);
        f.write(data);
        f.flush();
        f.close();
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
