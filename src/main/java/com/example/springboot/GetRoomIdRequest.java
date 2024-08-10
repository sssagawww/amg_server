package com.example.springboot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetRoomIdRequest {
    private int userId;
}
