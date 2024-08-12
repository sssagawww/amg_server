package com.example.springboot;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;

@Value
@Builder
public class BaseRoomResponse {
    private String userIds;
    private String userNames;
    private String numbers;
}
