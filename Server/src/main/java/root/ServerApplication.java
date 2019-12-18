package root;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import root.model.User;
import root.model.UserDescription;
import root.service.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentSkipListSet;

@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}

@Component
class Init {
    @Autowired
    private UserService userService;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        final String mboxesPath = "mboxes";
        final String receiptsPath = "receipts";
        final String descFilename = "description";

        // Create mboxes directory, if not found

        File mboxesDir = new File(mboxesPath);

        if (!mboxesDir.exists()) {
            try {
                mboxesDir.mkdir();
            } catch (Exception e) {
                System.err.println("Cannot create directory " + mboxesPath + ": " + e);
                System.exit(1);
            }
        }

        // Create receipts directory, if not found

        File receiptsDir = new File(receiptsPath);

        if (!receiptsDir.exists()) {
            try {
                receiptsDir.mkdir();
            } catch (Exception e) {
                System.err.println("Cannot create directory " + receiptsPath + ": " + e);
                System.exit(1);
            }
        }

        // Load data for each and every user

        for (File file : mboxesDir.listFiles()) {
            if (file.isDirectory()) { // Users have a directory of their own
                int id;
                JsonElement description = null;

                try {
                    id = Integer.parseUnsignedInt(file.getName());
                } catch (Exception e) {
                    continue; // Not a user directory
                }

                // Read JSON description from file

                String path = mboxesPath + "/" + file.getName() + "/" + descFilename;

                try {
                    description = new JsonParser().parse(readFromFile(path));
                } catch (Exception e) {
                    System.err.println("Cannot load user description from " + path + ": " + e);
                    System.exit(1);
                }

                // Add user to the internal structure
                userService.createUser(new User(id,description));
            }
        }
    }

    private String readFromFile(String path) throws Exception {
        FileInputStream f = new FileInputStream(path);
        byte[] buffer = new byte[f.available()];
        f.read(buffer);
        f.close();

        return new String(buffer, StandardCharsets.UTF_8);
    }
}
