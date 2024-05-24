package com.example.springboot;

import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public BaseResponse showStatus() {
        return new BaseResponse(SUCCESS_STATUS, 1);
    }

    @PostMapping("/pay")
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
    }

    @PostMapping("/join")
    public JoinResponce join(@RequestBody Request request) {
        final JoinResponce responce;
        if (!(miniGame.equals(MUSHROOMS) || miniGame.equals(PAINT))) {
            miniGame = request.getMiniGame();
            responce = new JoinResponce("can join");
            players.put(request.getUserId(), new Player(request.getUserName()));
        } else {
            if (request.getMiniGame().equals(miniGame)) {
                canStart = true;
                responce = new JoinResponce("can join");
                players.put(request.getUserId(), new Player(request.getUserName()));

            } else {
                responce = new JoinResponce("can't join");
            }
        }
        return responce;
    }

    @PostMapping("/leave")
    public void leave(@RequestBody Request request) {
        players.remove(request.getUserId());
        if (players.isEmpty()) {
            miniGame = "null";
        }
    }

    @PostMapping("/playerisready")
    public void playersready(@RequestBody Request request) {
        players.get(request.getUserId()).setReady(true);
    }

    @GetMapping("/readyornot")
    public boolean readyornot(@RequestBody Request request) {
        for (Player player : players.values()) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    @PostMapping("/setplayerready")
    public void setplayerready(@RequestBody Request request) {
        players.get(request.getUserId()).setReady(true);
        players.get(request.getUserId()).setAccuracy(request.getAccuracy());

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

        responce = new JoinResponce(winnerNick + " won!");

        return responce;
    }
}

