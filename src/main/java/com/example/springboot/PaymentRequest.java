package com.example.springboot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {
    private int userId;
    private String itemId;
    private double discount;
}
