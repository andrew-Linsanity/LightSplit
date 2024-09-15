package com.LightSplit.demo.DTO;

import java.util.Objects;

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

    public boolean equals(Object o) {
        if (this == o) return true; // A
        if (o == null || getClass() != o.getClass()) return false; // 
        TravelerDTO trav = (TravelerDTO) o;
        return Objects.equals(username, trav.getUsername()); // B, I don't get how line A & B need to be seperated; is it because equals method returns error if the compared objects aren't the same class ?
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
