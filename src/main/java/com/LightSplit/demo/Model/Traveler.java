package com.LightSplit.demo.Model;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "travelers")
public class Traveler {

    @Id
    private String id;

    private String nickName;
    
    private double balance = 0;

    // I'm really confused by what's happening here 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // A
        if (o == null || getClass() != o.getClass()) return false; // 
        Traveler traveler = (Traveler) o;
        return Objects.equals(id, traveler.id); // B, I don't get how line A & B need to be seperated; is it because equals method returns error if the compared objects aren't the same class ?
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
