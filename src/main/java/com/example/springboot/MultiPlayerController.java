package com.example.springboot;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.springboot.Global.*;

@RestController
@RequestMapping("/multiplayer")
public class MultiPlayerController {

    private final String sharedKey = "SHARED_KEY";

    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";
    private static final int CODE_SUCCESS = 100;
    private static final int AUTH_FAILURE = 102;
    private boolean canStart;

    /*@GetMapping
    public BaseResponse showStatus() {
        return new BaseResponse(SUCCESS_STATUS, 1);
    }*/

    /*@PostMapping("/pay")
    public BaseResponse pay(@RequestParam(value = "key") String key, @RequestBody PaymentRequest request) {

        final BaseResponse response;

        if (sharedKey.equalsIgnoreCase(key)) {
            int userId = request.getUserId();
            String itemId = request.getItemId();
            double discount = request.getDiscount();
            // Process the request
            // ....
            // Return success response to the client.
            response = new BaseResponse(SUCCESS_STATUS, CODE_SUCCESS);
        } else {
            response = new BaseResponse(ERROR_STATUS, AUTH_FAILURE);
        }
        return response;
    }*/

    @PostMapping("/join")
    public JoinResponce join(@RequestBody Request request) {
        final JoinResponce responce;
        if (!(miniGame.equals(MUSHROOMS) || miniGame.equals(PAINT))) {
            miniGame = request.getMiniGame();
            responce = new JoinResponce("can join");
            players.put(request.getUserId(), new Player(request.getUserId(), request.getUserName()));
        } else {
            if (request.getMiniGame().equals(miniGame)) {
                canStart = true;
                responce = new JoinResponce("can join");
                players.put(request.getUserId(), new Player(request.getUserId(), request.getUserName()));
            } else {
                responce = new JoinResponce("can't join");
            }
        }
        System.out.println(players);
        return responce;
    }

    @PostMapping("/info")
    public BaseResponse info(@RequestBody Request request) {
        int userId = players.get(request.getUserId()).getUserId();
        String userName =  players.get(request.getUserId()).getUsername();
        float number =  players.get(request.getUserId()).getAccuracy();
        players.get(request.getUserId()).setAccuracy(request.getNumber());
        for (Player player : players.values()) {
            if (player.getUserId() != request.getUserId()) {
                number = player.getAccuracy();
                userName = player.getUsername();
                userId = player.getUserId();
            }
        }
        return new BaseResponse(userId, userName, number);
    }

    @PostMapping("/leave")
    public JoinResponce leave(@RequestBody Request request) {
        players.remove(request.getUserId());
        if (players.isEmpty()) {
            miniGame = "null";
        }
        return new JoinResponce("left " + request.getUserId());
    }

    @PostMapping("/playerisready")
    public JoinResponce playersready(@RequestBody Request request) {
        players.get(request.getUserId()).setReady(true);
        return new JoinResponce(request.getUserName() + " isReady");
    }

    @GetMapping("/readyornot")
    public boolean readyornot() {
        for (Player player : players.values()) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    @PostMapping("/setplayerready")
    public void setplayerready(@RequestBody Request request) {
        players.get(request.getUserId()).setReady(request.isReady());
        players.get(request.getUserId()).setAccuracy(request.getNumber());
    }

    @GetMapping("/getwinner")
    public JoinResponce getWinner() {
        JoinResponce responce;

        String winnerNick = "";
        float maxAcc = Float.MIN_VALUE;

        for (Player player : players.values()) {
            if (player.getAccuracy() > maxAcc) {
                maxAcc = player.getAccuracy();
                winnerNick = player.getUsername();
            }
        }

        responce = new JoinResponce(winnerNick + " выиграл!");

        return responce;
    }
}

