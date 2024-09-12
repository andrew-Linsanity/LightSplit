package com.LightSplit.demo.Model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.LightSplit.demo.DTO.TravelerDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "items")
public class Item {
    
    @Id
    private String id;
    
    @NotNull
    private String name;

    @NotNull
    private Double price;
    
    private List<TravelerDTO> travelers;

    private HashMap<String, Double> paymentMap = new HashMap<>();

    @CreatedDate
    private LocalDateTime payedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean equals(Object o) {
        if (this == o) return true; // A
        if (o == null || getClass() != o.getClass()) return false; // 
        Item item = (Item) o;
        return Objects.equals(id, item.id); // B, I don't get how line A & B need to be seperated; is it because equals method returns error if the compared objects aren't the same class ?
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
