package com.example.springboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.example.springboot.Global.*;

@RestController
@RequestMapping("/multiplayer")
public class MultiPlayerController {
    private static final String PASSWORD = "super-secret-password";

    @PostMapping("/info")
    public BaseRoomResponse info(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD) && getRoomByRoomID.get(request.getRoomId()) != null) {
            ArrayList<Integer> userIds = new ArrayList<>();
            ArrayList<String> userNames = new ArrayList<>();
            ArrayList<Float> numbers = new ArrayList<>();

            HashMap<Integer, Player> roomPlayers = getRoomByRoomID.get(request.getRoomId()).getPlayers();

            //обновляем данные игрока
            roomPlayers.get(request.getUserId()).setAccuracy(request.getNumber());

            //посылаем обратно данные остальных игроков
            for (Player player : roomPlayers.values()) {
                if (player.getUserId() != request.getUserId()) {
                    userIds.add(player.getUserId());
                    userNames.add(player.getUsername());
                    numbers.add(player.getAccuracy());
                }
            }

            //преобразуем массив в json
            ObjectMapper objectMapper = new ObjectMapper();
            String userIdsJson, userNamesJson, numbersJson;
            try {
                userIdsJson = objectMapper.writeValueAsString(userIds);
                userNamesJson = objectMapper.writeValueAsString(userNames);
                numbersJson = objectMapper.writeValueAsString(numbers);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return new BaseRoomResponse(userIdsJson, userNamesJson, numbersJson);
        }
        return null;
    }

    @PostMapping("/createroom")
    public Responce createRoom(@RequestBody CreateRoomRequest request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            //задаём id для комнаты
            Random rnd = new Random();
            int randRoomId = rnd.nextInt(10000);
            System.out.println(randRoomId + " roomId");

            //если такой id уже есть, задаём заново
            while (getRoomByRoomID.containsKey(randRoomId)) {
                randRoomId = rnd.nextInt(10000);
                System.out.println(randRoomId + " new roomId");
            }

            //если игрок уже был в какой-то комнате, его нужно удалить оттуда
            if (getRoomByPlayerID.containsKey(request.getUserId())) {
                int delRoomID = getRoomByPlayerID.get(request.getUserId()).getRoomId();
                getRoomByPlayerID.remove(request.getUserId());
                getRoomByRoomID.remove(delRoomID);
            }

            //создаём комнату и добавляем в нее игрока
            Room newRoom = new Room(randRoomId, request.getMiniGame());
            newRoom.addPlayer(new Player(request.getUserId(), request.getUserName()));

            //добавляем комнату по id в список
            getRoomByRoomID.put(newRoom.getRoomId(), newRoom);
            getRoomByPlayerID.put(request.getUserId(), newRoom);

            System.out.println("rooms by roomID: " + getRoomByRoomID.toString() + ", rooms by userID: " + getRoomByPlayerID.toString());

            return new Responce(newRoom.getRoomId() + "");
        }
        return null;
    }

    @PostMapping("/joinroom")
    public Responce joinRoom(@RequestBody JoinRoomRequest request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD)) {
            final Responce responce;
            //если есть комната с таким id - добавляем в нее игрока
            if (getRoomByRoomID.containsKey(request.getRoomId()) && getRoomByRoomID.get(request.getRoomId()) != null) {
                getRoomByRoomID.get(request.getRoomId()).addPlayer(new Player(request.getUserId(), request.getUserName()));

                if (getRoomByPlayerID.get(request.getUserId()) == null) {
                    getRoomByPlayerID.put(request.getUserId(), getRoomByRoomID.get(request.getRoomId()));
                }
                getRoomByPlayerID.get(request.getUserId()).addPlayer(new Player(request.getUserId(), request.getUserName()));

                responce = new Responce(getRoomByRoomID.get(request.getRoomId()).getMiniGame());

                System.out.println(request.getRoomId() + " room players: " + getRoomByRoomID.get(request.getRoomId()).getPlayers());
            } else {
                responce = new Responce("can't join");
            }
            return responce;
        }
        return null;
    }

    @PostMapping("/leaveroom")
    public Responce leaveRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD) && getRoomByRoomID.get(request.getRoomId()) != null) {
            //удалим игрока из списка
            getRoomByRoomID.get(request.getRoomId()).deletePlayer(request.getUserId());
            getRoomByPlayerID.get(request.getUserId()).deletePlayer(request.getUserId());
            //если комната пустая, удалим и её
            if (getRoomByRoomID.get(request.getRoomId()).isEmpty()) {
                getRoomByRoomID.remove(request.getRoomId());
            }
            if (getRoomByPlayerID.get(request.getUserId()).isEmpty()) {
                getRoomByPlayerID.remove(request.getUserId());
            }

            System.out.println("user " + request.getUserId() + " left room " + request.getRoomId());
            return new Responce("left " + request.getUserId());
        }
        return null;
    }

    @PostMapping("/playerisreadyroom")
    public Responce playerIsReadyRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD) && getRoomByRoomID.get(request.getRoomId()) != null) {
            //уставливаем, что этот игрок готов
            getRoomByRoomID.get(request.getRoomId()).getPlayer(request.getUserId()).setReady(true);
            return new Responce(request.getUserName() + " isReady");
        }
        return null;
    }

    @PostMapping("/setplayerreadyroom")
    public void setPlayerReadyRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD) && getRoomByRoomID.get(request.getRoomId()) != null) {
            //устанавливаем готовность игрока (да/нет)
            getRoomByRoomID.get(request.getRoomId()).getPlayer(request.getUserId()).setReady(request.isReady());
            getRoomByRoomID.get(request.getRoomId()).getPlayer(request.getUserId()).setAccuracy(request.getNumber());
        }
    }

    @PostMapping("/getroomid")
    public int getRoomId(@RequestBody GetRoomIdRequest request, @RequestHeader(value = "SecretHeader") String header) {
        //найти id комнаты по игроку
        if (header.equals(PASSWORD) && getRoomByPlayerID.containsKey(request.getUserId())) {
            Integer id = request.getUserId();
            int roomId = getRoomByPlayerID.get(id).getRoomId();
            getRoomByPlayerID.remove(request.getUserId());
            return roomId;
        }
        return -1;
    }

    @PostMapping("/readyornotroom")
    public boolean readyOrNotRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD) && getRoomByRoomID.get(request.getRoomId()) != null) {
            return getRoomByRoomID.get(request.getRoomId()).everyoneIsReady();
        }
        return false;
    }

    @PostMapping("/getwinnerroom")
    public Responce getWinnerRoom(@RequestBody Request request, @RequestHeader(value = "SecretHeader") String header) {
        if (header.equals(PASSWORD) && getRoomByRoomID.get(request.getRoomId()) != null) {
            Room room = getRoomByRoomID.get(request.getRoomId());
            String winnerNick = room.getWinner();
            return new Responce(winnerNick + " выиграл!");
        }
        return null;
    }
}

