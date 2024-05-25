package com.example.springboot;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BaseResponse {
    private int userId;
    private String userName;
    private float number;
}
