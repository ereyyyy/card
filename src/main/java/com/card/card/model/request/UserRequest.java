package com.card.card.model.request;

import com.card.card.model.enums.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String name;
    private String email;
    private String addresses;
    private UserType userType;
    private String password;
}
