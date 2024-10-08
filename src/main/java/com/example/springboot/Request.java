package com.example.springboot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Request {
    private int userId;
    private String userName;
    private float number;
    private boolean ready;
    private int roomId;
}
