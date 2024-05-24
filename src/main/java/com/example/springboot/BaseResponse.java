package com.example.springboot;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BaseResponse {
    private String status;
    private Integer code;
}
