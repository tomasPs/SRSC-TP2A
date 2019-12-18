import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import security.*;
import utils.CertificateReader;
import utils.StoreManager;
import utils.propreties.Config;
import utils.propreties.ConfigReader;

import javax.net.ssl.SSLContext;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class Client {
    private static Config config;
    private static StoreManager keyStore;
    private static StoreManager trustStore;
    private static X509Certificate serverCert;
    private static String password;

    private static final String baseUrl = "https://localhost:8443";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Password must be provided");
            System.exit(1);
        }
        password = args[0];
        init();

        Scanner in = new Scanner(System.in);
        String command = "";
        System.out.println("STARTED:");
        while (!command.equals(Commands.EXIT)) {
            System.out.println("");
            System.out.println("INSERT COMMAND: ");
            command = in.nextLine().toUpperCase();
            commandsAnalyzer(command, in);
        }
    }

    private static void commandsAnalyzer(String command, Scanner in) throws Exception {
        switch (command) {
            case Commands.CREATE:
                createUser();
                break;
            case Commands.LIST:
                listUsers(in);
                break;
            case Commands.NEW:
                newMessages(in);
                break;
            case Commands.ALL:
                allMessages(in);
                break;
            case Commands.SEND:
                sendMessage(in);
                break;
            case Commands.RECV:
                receiveMessage(in);
                break;
            case Commands.RECEIPT:
                receiptMessage(in);
                break;
            case Commands.STATUS:
                messageStatus(in);
                break;
            default:
                System.out.println("Invalid command");
                break;
        }
    }

    private static void messageStatus(Scanner in) throws Exception {
        System.out.println("ID: ");
        int id = Integer.parseInt(in.nextLine());
        System.out.println("msgID: ");
        String msg = in.nextLine();

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("id", id);
        jsonRequest.put("msg", msg);

        String url = baseUrl + "/status";

        ResponseEntity<String> response = post(url, jsonRequest);
        System.out.println(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode node = objectMapper.readTree(response.getBody()).get("result");

        String recvMsg = node.get("msg").asText();
        byte[] decodedMsg = Base64.getDecoder().decode(recvMsg);
        recvMsg = new String(new PBEManager().decipher(password, decodedMsg));
        System.out.println("Message: ");
        System.out.println(recvMsg);
    }

    private static void receiptMessage(Scanner in) throws Exception {
        System.out.println("ID: ");
        int id = Integer.parseInt(in.nextLine());
        System.out.println("msgID: ");
        String msgId = in.nextLine();

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("id", id);
        jsonRequest.put("msg", msgId);

        //Get msg
        String url2 = baseUrl + "/recv";

        ResponseEntity<String> response = post(url2, jsonRequest);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode arrNode = objectMapper.readTree(response.getBody()).get("result");
        List<String> result = objectMapper.convertValue(arrNode, ArrayList.class);

        String msg = result.get(1);

        int from = Integer.parseInt(result.get(0));
        byte[] ourDH = new PBEManager().decipher(password, getPrvDH(from));
        byte[] otherDH = getPubDH(id);

        byte[] secret = DH.generateSecret(ourDH, otherDH);
        Key secretKey = AES.byteToKey(secret);

        byte[] msgB = Base64.getDecoder().decode(msg);
        msgB = AES.decipher(msgB, secretKey);

        byte[] sign = SignatureUtil.getSignature(getMyPrivateKey(), msgB);
        //......................................................

        jsonRequest.put("receipt", Base64.getEncoder().encodeToString(sign));

        String url = baseUrl + "/receipt";

        ResponseEntity<String> response2 = post(url, jsonRequest);
        System.out.println(response2.getBody());
    }

    private static void receiveMessage(Scanner in) throws Exception {
        System.out.println("ID: ");
        int id = Integer.parseInt(in.nextLine());
        System.out.println("msgID: ");
        String msgId = in.nextLine();

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("id", id);
        jsonRequest.put("msg", msgId);

        String url = baseUrl + "/recv";

        ResponseEntity<String> response = post(url, jsonRequest);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode arrNode = objectMapper.readTree(response.getBody()).get("result");
        List<String> result = objectMapper.convertValue(arrNode, ArrayList.class);

        System.out.println("SENDER:");
        System.out.println(result.get(0));
        System.out.println("MSG: ");
        String msg = result.get(1);

        byte[] ourDH = new PBEManager().decipher(password, getPrvDH(Integer.parseInt(result.get(0))));
        byte[] otherDH = getPubDH(id);

        byte[] secret = DH.generateSecret(ourDH, otherDH);
        Key secretKey = AES.byteToKey(secret);

        byte[] msgB = Base64.getDecoder().decode(msg);
//        System.out.println(new String(msgB));
        msgB = AES.decipher(msgB, secretKey);
        System.out.println(new String(msgB));
    }

    private static PrivateKey getMyPrivateKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return keyStore.getPrivKey("user (rootca)", config.getKeyStorePassword());
    }

    private static void sendMessage(Scanner in) throws Exception {
        System.out.println("From: ");
        int from = Integer.parseInt(in.nextLine());
        System.out.println("To: ");
        int to = Integer.parseInt(in.nextLine());
        System.out.println("Message: ");
        String msg = in.nextLine();

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("src", from);
        jsonRequest.put("dst", to);

        byte[] ourDH = new PBEManager().decipher(password, getPrvDH(from));
        byte[] otherDH = getPubDH(to);

        //todo fix this
//        byte[] secret = DH.generateSecret(new BigInteger(ourDH), new BigInteger(otherDH));
        byte[] secret = DH.generateSecret(ourDH, otherDH);
        Key secretKey = AES.byteToKey(secret);

//        JSONObject msgJSON = new JSONObject();
//        String content = Base64.getEncoder().encodeToString(AES.cipher(msg.getBytes(), secretKey));
//        msgJSON.put("content", content);

//        String sign = Base64.getEncoder().encodeToString(SignatureUtil.getSignature(getMyPrivateKey(), msg.getBytes()));
//        msgJSON.put("sign", sign);

        jsonRequest.put("msg", Base64.getEncoder().encodeToString(AES.cipher(msg.getBytes(), secretKey)));
        byte[] copyMsg = new PBEManager().cipher(password, msg.getBytes());
        jsonRequest.put("copy", Base64.getEncoder().encodeToString(copyMsg));

        String url = baseUrl + "/send";

        ResponseEntity<String> response = post(url, jsonRequest);
        System.out.println(response.getBody());
    }

    private static void allMessages(Scanner in) throws Exception {
        System.out.println("ID: ");
        int id = Integer.parseInt(in.nextLine());

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("id", id);

        String url = baseUrl + "/all";

        ResponseEntity<String> response = post(url, jsonRequest);
        System.out.println(response.getBody());
    }

    private static void newMessages(Scanner in) throws Exception {
        System.out.println("ID: ");
        int id = Integer.parseInt(in.nextLine());

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("id", id);

        String url = baseUrl + "/new";

        ResponseEntity<String> response = post(url, jsonRequest);
        System.out.println(response.getBody());
    }

    private static void createUser() throws Exception {
        Certificate cert = keyStore.getCert("user (rootca)");

        byte[] pubKey = cert.getPublicKey().getEncoded();

        JSONObject jsonRequest = new JSONObject();

        String uuid = Base64.getEncoder().encodeToString(Hash.hash(pubKey));
        jsonRequest.put("uuid", uuid);

        String encodedPubKey = Base64.getEncoder().encodeToString(pubKey);
        jsonRequest.put("encodedPubKey", encodedPubKey);

        KeyPair dhPair = DH.generateDHParams();

        System.out.println("PUBLIC DH");
        System.out.println(new BigInteger(dhPair.getPublic().getEncoded()));
        System.out.println("PRIVATE DH");
        System.out.println(new BigInteger(dhPair.getPrivate().getEncoded()));

        String encodedPubDH = Base64.getEncoder().encodeToString(dhPair.getPublic().getEncoded());
        jsonRequest.put("encodedPubDH", encodedPubDH);

        byte[] encDHPriv =
            new PBEManager().cipher(password, dhPair.getPrivate().getEncoded());

        String encodedPrvDH = Base64.getEncoder().encodeToString(encDHPriv);
        jsonRequest.put("encodedPrvDH", encodedPrvDH);

        String allParams = uuid + encodedPubKey + encodedPubDH + encodedPrvDH;
        String encodedSign =
            Base64.getEncoder().encodeToString(SignatureUtil.getSignature(getMyPrivateKey(), allParams.getBytes()));
        jsonRequest.put("encodedSig", encodedSign);

        String url = baseUrl + "/create";

        ResponseEntity<String> response = post(url, jsonRequest);
        System.out.println(response.getBody());
    }

    private static void listUsers(Scanner in) throws Exception {
        System.out.println("ID: ");
        int id = Integer.parseInt(in.nextLine());

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("id", id);

        String url = baseUrl + "/list";

        ResponseEntity<String> response = post(url, jsonRequest);
        System.out.println(response.getBody());
    }

//    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.out.println("Password must be provided");
//            System.exit(1);
//        }
//        password = args[0];
//        init();
//
//        byte[] test = getPrvDH(1);
//        Base64.getEncoder().encodeToString(test);
//        System.out.println(test);
//    }

    private static byte[] getPubDH(int id) throws Exception {
        String url = baseUrl + "/pubDH/" + id;
        return Base64.getDecoder().decode(get(url).getBody().getBytes());
    }

    private static byte[] getPubKey(int id) throws Exception {
        String url = baseUrl + "/pubKey/" + id;
        return Base64.getDecoder().decode(get(url).getBody().getBytes());
    }

    public static byte[] getPrvDH(int id) throws Exception {
        String url = baseUrl + "/prvDH/" + id;
        return Base64.getDecoder().decode(get(url).getBody().getBytes());
    }

    private static ResponseEntity<String> get(String url) throws Exception {
        try {
            RestTemplate template = restTemplate();

            return template.getForEntity(url, String.class);
        } catch (ResourceAccessException e) {
            e.printStackTrace();
        } catch (HttpServerErrorException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private static ResponseEntity<String> post(String url, JSONObject jsonRequest) throws Exception {
        try {
            RestTemplate template = restTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonRequest.toJSONString(), headers);

            return template.postForEntity(url, request, String.class);
        } catch (ResourceAccessException e) {
            e.printStackTrace();
        } catch (HttpServerErrorException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    private static void init() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            config = ConfigReader.getConfig();
            keyStore = new StoreManager(config.getKeyStore(), "JCEKS", config.getKeyStorePassword());
            trustStore = new StoreManager(config.getTrustStore(), "JCEKS", config.getKeyStorePassword());
            serverCert = CertificateReader.getCertificate(config.getServerCert());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    private static RestTemplate restTemplate() throws Exception {
        return restTemplate(config);
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
}
