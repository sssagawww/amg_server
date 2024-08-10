package com.example.springboot;

import org.springframework.web.bind.annotation.*;

import java.util.Random;

import static com.example.springboot.Global.*;

@RestController
@RequestMapping("/multiplayer")
public class MultiPlayerController {

    private final String sharedKey = "SHARED_KEY";

    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";
    private static final int CODE_SUCCESS = 100;
    private static final int AUTH_FAILURE = 102;
    private static final String PASSWORD = "super-secret-password";
    private boolean canStart;

//    @PostMapping("/join")
//    public Responce join(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
//        if (header.equals(PASSWORD)) {
//            final Responce responce;
//            if (!(miniGame.equals(MUSHROOMS) || miniGame.equals(PAINT))) {
//                miniGame = request.getMiniGame();
//                responce = new Responce("can join");
//                players.put(request.getUserId(), new Player(request.getUserId(), request.getUserName()));
//            } else {
//                if (request.getMiniGame().equals(miniGame)) {
//                    canStart = true;
//                    responce = new Responce("can join");
//                    players.put(request.getUserId(), new Player(request.getUserId(), request.getUserName()));
//                } else {
//                    responce = new Responce("can't join");
//                }
//            }
//            System.out.println(players);
//            return responce;
//        }
//        return null;
//    }
//
//    @PostMapping("/info")
//    public BaseResponse info(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
//        if (header.equals(PASSWORD)) {
//            int userId = players.get(request.getUserId()).getUserId();
//            String userName = players.get(request.getUserId()).getUsername();
//            float number = players.get(request.getUserId()).getAccuracy();
//            players.get(request.getUserId()).setAccuracy(request.getNumber());
//            for (Player player : players.values()) {
//                if (player.getUserId() != request.getUserId()) {
//                    number = player.getAccuracy();
//                    userName = player.getUsername();
//                    userId = player.getUserId();
//                }
//            }
//            return new BaseResponse(userId, userName, number);
//        }
//        return null;
//    }

    @PostMapping("/leave")
    public Responce leave(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            players.remove(request.getUserId());
            if (players.isEmpty()) {
                miniGame = "null";
            }
            return new Responce("left " + request.getUserId());
        }
        return null;
    }

    @PostMapping("/playerisready")
    public Responce playersready(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            players.get(request.getUserId()).setReady(true);
            return new Responce(request.getUserName() + " isReady");
        }
        return null;
    }

    @GetMapping("/readyornot")
    public boolean readyornot(@RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            for (Player player : players.values()) {
                if (!player.isReady()) {
                    return false;
                }
            }
            return true;
        }
        return Boolean.parseBoolean(null);
    }

    @PostMapping("/setplayerready")
    public void setplayerready(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            players.get(request.getUserId()).setReady(request.isReady());
            players.get(request.getUserId()).setAccuracy(request.getNumber());
        }
    }

    @GetMapping("/getwinner")
    public Responce getWinner(@RequestHeader(value = "SecretHeader") String header) {

        if (header.equals(PASSWORD)) {
            Responce responce;

            String winnerNick = "";
            float maxAcc = Float.MIN_VALUE;

            for (Player player : players.values()) {
                if (player.getAccuracy() > maxAcc) {
                    maxAcc = player.getAccuracy();
                    winnerNick = player.getUsername();
                }
            }

            responce = new Responce(winnerNick + " выиграл!");

            return responce;
        }
        return null;
    }

    /**
     *
     *
     * -----------------NEW SERVER CODE USING ROOMS----------------------------
     *
     *
     */

    @PostMapping("/createroom")
    public void createRoom(@RequestBody CreateRoomRequest request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            Random rnd = new Random();
            int randRoomId = rnd.nextInt(10000);
            System.out.println(randRoomId);
            while (getRoomByRoomID.containsKey(randRoomId)) {
                randRoomId = rnd.nextInt(10000);
                System.out.println(randRoomId);
            }

            Room newRoom = new Room(randRoomId, request.getMiniGame());

            newRoom.addPlayer(new Player(request.getUserId(), request.getUserName()));

            getRoomByRoomID.put(newRoom.getRoomId(), newRoom);
            getRoomByPlayerID.put(request.getUserId(), newRoom);

        }
    }

    @GetMapping("/getroomid")
    public int getRoomId(@RequestBody GetRoomIdRequest request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD) && getRoomByPlayerID.containsKey(request.getUserId())) {
            Integer id = request.getUserId();
            int roomId = getRoomByPlayerID.get(id).getRoomId();
            getRoomByPlayerID.remove(request.getUserId());
            return roomId;
        }
        return -1;
    }

    @PostMapping("/joinroom")
    public Responce joinRoom(@RequestBody JoinRoomRequest request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            final Responce responce;
            if (getRoomByRoomID.containsKey(request.getRoomId())) {
                getRoomByRoomID.get(request.getRoomId()).addPlayer(new Player(request.getUserId(), request.getUserName()));
                responce = new Responce("can join," + getRoomByRoomID.get(request.getRoomId()).getMiniGame());
            } else {
                responce = new Responce("can't join");
            }
            return responce;
        }
        return null;
    }

    @PostMapping("/leaveroom")
    public Responce leaveRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            getRoomByRoomID.get(request.getRoomId()).deletePlayer(request.getUserId());
            if (getRoomByRoomID.get(request.getRoomId()).isEmpty()) {
                getRoomByRoomID.remove(request.getRoomId());
            }
            return new Responce("left " + request.getUserId());
        }
        return null;
    }

    @PostMapping("/playerisreadyroom")
    public Responce playerIsReadyRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            getRoomByRoomID.get(request.getRoomId()).getPlayer(request.getUserId()).setReady(true);
            return new Responce(request.getUserName() + " isReady");
        }
        return null;
    }

    @GetMapping("/readyornotroom")
    public boolean readyOrNotRoom(@RequestBody Request request,@RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            return getRoomByRoomID.get(request.getRoomId()).everyoneIsReady();
        }
        return Boolean.parseBoolean(null);
    }

    @PostMapping("/setplayerreadyroom")
    public void setPlayerReadyRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {

            getRoomByRoomID.get(request.getRoomId()).getPlayer(request.getUserId()).setReady(request.isReady());
            getRoomByRoomID.get(request.getRoomId()).getPlayer(request.getUserId()).setAccuracy(request.getNumber());

            players.get(request.getUserId()).setReady(request.isReady());
            players.get(request.getUserId()).setAccuracy(request.getNumber());
        }
    }
    @GetMapping("/getwinnerroom")
    public Responce getWinnerRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            Responce responce;

            Room room = getRoomByRoomID.get(request.getRoomId());

            String winnerNick = room.getWinner();

            responce = new Responce(winnerNick + " выиграл!");

            return responce;
        }
        return null;
    }
}

