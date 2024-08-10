package com.example.springboot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoomRequest {
    private int userId;
    private String userName;
    private String miniGame;
}
