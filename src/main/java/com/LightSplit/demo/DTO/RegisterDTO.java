package com.LightSplit.demo.DTO;

import lombok.Data;

@Data
public class RegisterDTO {

    private String username;
    private String password;
    private String adminKey = "default"; // apparently you couldn't have underscore in your field name
}
