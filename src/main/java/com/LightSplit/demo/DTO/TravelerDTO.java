package com.LightSplit.demo.DTO;

import lombok.Data;

@Data
public class TravelerDTO {

    // will serve as id for now
    private String username;
    private double balance;

    public TravelerDTO(String username) {
        this.username = username;
        balance = 0.0;
    }
}
