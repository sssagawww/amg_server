package com.example.springboot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinRoomRequest {
    private int roomId;
    private String userName;
    private int userId;
}
