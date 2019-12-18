package root.service;

import org.springframework.stereotype.Service;
import root.dto.Status;
import root.model.Receipt;
import root.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageService {
    private final String mboxesPath = "mboxes";
    private final String receiptsPath = "receipts";

    private List<String> userMessages(String path, String pattern) {
        File mbox = new File(path);
        Pattern msgPattern = Pattern.compile(pattern);
        List<String> result = new ArrayList<>();

        System.out.println("Look for files at " + path + " with pattern " + pattern);

        try {
            for (File file : mbox.listFiles()) {
                System.out.println("\tFound file " + file.getName());
                Matcher m = msgPattern.matcher(file.getName());
                if (m.matches()) {
                    result.add(file.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error while listing messages in directory " + mbox.getName() + ": " + e);
        }

        return result;
    }

    public List<String> userNewMessages(User user) {
        return userMessages(user.getMbox(), "[0-9]+_[0-9]+");
    }

    public List<String> userAllMessages(User user) {
        return userMessages(user.getMbox(), "_?+[0-9]+_[0-9]+");
    }

    public List<String> userSentMessages(User user) {
        return userMessages(user.getRbox(), "[0-9]+_[0-9]+");
    }

    public List<String> sendMessage(User src, User dst, String msg, String receipt) {
        int nr = 0;
        List<String> result = new ArrayList<>();
        List<String> emptyResult = new ArrayList<>();
        emptyResult.add("");
        emptyResult.add("");
        String path = null;

        try {
            path = dst.getMbox() + "/";
            nr = newFile(path, src.getId() + "_");
            saveOnFile(path + src.getId() + "_" + nr, msg);

            result.add(src.getId() + "_" + nr);

            path = src.getRbox() + "/" + dst.getId() + "_";
            saveOnFile(path + nr, receipt);
        } catch (Exception e) {
            System.err.println("Cannot create message or copy file " + path + nr + ": " + e);
            return emptyResult;
        }

        result.add(dst.getId() + "_" + nr);
        return result;
    }

    private int newFile(String path, String basename) {
        for (int i = 1; ; i++) {
            File file1 = new File(path + basename + i);
            File file2 = new File(path + "_" + basename + i);
            if (!file1.exists() && !file2.exists()) {
                return i;
            }
        }
    }

    private void saveOnFile(String path, String data) throws Exception {
        FileWriter f = new FileWriter(path);
        f.write(data);
        f.flush();
        f.close();
    }

    public boolean messageExists(User user, String message) {
        return (new File(user.getMbox() + "/" + message)).exists();
    }

    public boolean copyExists(User user, String message) {
        return (new File(user.getRbox() + "/" + message)).exists();
    }

    public List<String> recvMessage(User user, String msg) {
        List<String> result = new ArrayList<>();

        // Extract message sender id

        Pattern p = Pattern.compile("_?+([0-9]+)_[0-9]+");
        Matcher m = p.matcher(msg);

        if (!m.matches()) {
            System.err.println("Internal error, wrong message file name (" + msg + ") format!");
            System.exit(2);
        }

        result.add(m.group(1));

        // Read message

        try {
            result.add(readMsgFile(user, msg));
        } catch (Exception e) {
            System.err.println("Cannot read message " + msg + " from user " + user.getId() + ": " + e);
            result.add("");
        }

        return result;
    }

    String readMsgFile(User user, String msg) throws Exception {
        String path = user.getMbox() + "/";

        if (msg.charAt(0) == '_') { // Already red
            path += msg;
        } else {
            File f = new File(path + "_" + msg);
            if (f.exists()) {         // Already red
                path += "_" + msg;
            } else { // Rename before reading
                try {
                    f = new File(path + msg);
                    path += "_" + msg;
                    f.renameTo(new File(path));
                } catch (Exception e) {
                    System.err.println("Cannot rename message file to " + path + ": " + e);
                    path += msg; // Fall back to the non-renamed file
                }
            }
        }

        return readFromFile(path);
    }

    private String readFromFile(String path) throws Exception {
        FileInputStream f = new FileInputStream(path);
        byte[] buffer = new byte[f.available()];
        f.read(buffer);
        f.close();

        return new String(buffer, StandardCharsets.UTF_8);
    }

    public boolean messageWasRed(User user, String msg) {
        if (msg.charAt(0) == '_') {
            return (new File(user.getMbox() + "/" + msg)).exists();
        } else {
            return (new File(user.getMbox() + "/_" + msg)).exists();
        }
    }

    public void storeReceipt(User user, String msg, String receipt) {
        Pattern p = Pattern.compile("_?+([0-9]+)_([0-9])");
        Matcher m = p.matcher(msg);

        if (!m.matches()) {
            System.err.println("Internal error, wrong message file name (" + msg + ") format!");
            System.exit(2);
        }

        String path = userReceiptBox(Integer.parseInt(m.group(1))) + "/_" + user.getId() + "_" + m.group(2) + "_" + System
            .currentTimeMillis();

        try {
            saveOnFile(path, receipt);
        } catch (Exception e) {
            System.err.println("Cannot create receipt file " + path + ": " + e);
        }
    }

    private String userMessageBox(int id) {
        return mboxesPath + "/" + id;
    }

    private String userReceiptBox(int id) {
        return receiptsPath + "/" + id;
    }

    public Status getReceipts(User user, String msg) {
        Pattern p = Pattern.compile("_(([0-9])+_[0-9])_([0-9]+)");
        File dir = new File(user.getRbox());
        List<Receipt> receipts = new ArrayList<>();
        String copy;

        try {
            copy = readFromFile(user.getRbox() + "/" + msg);
        } catch (Exception e) {
            System.err.println("Cannot read a copy file: " + e);
            copy = "";
        }

        for (File f : dir.listFiles()) {
            Matcher m = p.matcher(f.getName());
            if (m.matches() && m.group(1).equals(msg)) {
                String receipt;
                try {
                     receipt = readFromFile(user.getRbox() + "/" + f.getName());
                } catch (Exception e) {
                    System.err.println("Cannot read a receipt file: " + e);
                    receipt = "";
                }

                receipts.add(new Receipt(m.group(3), Integer.parseInt(m.group(2)), receipt));
            }
        }

        return new Status(copy, receipts);
    }
}
