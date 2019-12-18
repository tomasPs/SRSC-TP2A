package root.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import root.dto.*;
import root.exception.ServerErrorException;
import root.model.User;
import root.service.MessageService;
import root.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
public class ServerController {
    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/hello")
    String hello() {
        return "Hello";
    }

    @PostMapping("/create")
    CreateResponse create(@RequestBody CreateRequest request) {
        User user = new User(request);
        int id = userService.createUser(user).getId();
        return new CreateResponse(id);
    }

    @PostMapping("/list")
    ListResponse list(@RequestBody ListRequest request) {
        if (request.getId() == 0)
            return new ListResponse(userService.getAllUsers());

        List<User> users = new ArrayList<>();
        User user = checkAndGetUser(request.getId());

        users.add(user);
        return new ListResponse(users);
    }

    @PostMapping("/new")
    NewResponse newRequest(@RequestBody NewRequest request) {
        User user = checkAndGetUser(request.getId());

        List<String> result = messageService.userNewMessages(user);
        return new NewResponse(result);
    }

    @PostMapping("/all")
    AllResponse all(@RequestBody AllRequest request) {
        User user = checkAndGetUser(request.getId());

        List<String> all = messageService.userAllMessages(user);
        List<String> sent = messageService.userSentMessages(user);

        List<String[]> reply = new ArrayList<>();
        reply.add(all.toArray(new String[0]));
        reply.add(sent.toArray(new String[0]));
        return new AllResponse(reply);
    }

    @PostMapping("/send")
    SendResponse send(@RequestBody SendRequest request) {
        User src = checkAndGetUser(request.getSrc());
        User dst = checkAndGetUser(request.getDst());

        List<String> reply = messageService.sendMessage(src, dst, request.getMsg(), request.getCopy());

        return new SendResponse(reply);
    }

    @PostMapping("/recv")
    RecvResponse recv(@RequestBody RecvRequest request) {
        User from = checkAndGetUser(request.getId());

        String msg = request.getMsg();

        if (!messageService.messageExists(from, msg) &&
            !messageService.messageExists(from, "_" + msg)) {
            System.err.println("Unknown message for 'recv' request: " + request.toString());
            throw new ServerErrorException("Could not find message");
        }

        List<String> reply = messageService.recvMessage(from, msg);

        return new RecvResponse(reply);
    }

    @PostMapping("/receipt")
    void receipt(@RequestBody ReceiptRequest request) {
        User user = checkAndGetUser(request.getId());

        String msg = request.getMsg();
        String receipt = request.getReceipt();

        if (!messageService.messageWasRed(user, msg)) {
            System.err.println("Unknown message or not yet read for request: " + request);
            throw new ServerErrorException("Could not find message or message has not been read yet");
        }

        messageService.storeReceipt(user, msg, receipt);
    }

    @PostMapping("/status")
    StatusResponse status(@RequestBody StatusRequest request) {
        User user = checkAndGetUser(request.getId());
        String msg = request.getMsg();

        if (!messageService.copyExists(user, msg)) {
            System.err.print("Unknown message for 'status' request: " + request);
            throw new ServerErrorException("Could not find message");
        }

        Status status = messageService.getReceipts(user, msg);
        return new StatusResponse(status);
    }

    @GetMapping("/pubKey/{id:[0-9]+}")
    String getPubKey(
        @PathVariable int id
    ) {
        return checkAndGetUser(id).getDescription().getSecData().getPublicKey();
    }

    @GetMapping("/pubDH/{id:[0-9]+}")
    String getPubDH(
        @PathVariable int id
    ) {
        return checkAndGetUser(id).getDescription().getSecData().getPublicDH();
    }

    @GetMapping("/prvDH/{id:[0-9]+}")
    String getPrvDH(
        @PathVariable int id
    ) {
        return checkAndGetUser(id).getDescription().getSecData().getPrivateDH();
    }

    private User checkAndGetUser(int id) {
        Optional<User> userOp = userService.getUser(id);
        if (userOp.isEmpty()) throw new ServerErrorException("No user found for id '" + id + "'");

        return userOp.get();
    }
}
