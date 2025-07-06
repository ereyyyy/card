package com.card.card.model.request;

import lombok.Data;

@Data
public class RemoveProductRequest {
    private String productName;
    private String userName;
    private String email;
    private String addresses;
    private String userType;
    private String password;
} 