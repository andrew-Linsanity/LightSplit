package com.LightSplit.demo.Model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String Id;
    
    @NotNull
    private String name;

    @NotNull
    private Double price;

    private List<Traveler> travelers;

    private HashMap<Traveler, Double> paymentMap;

    private LocalDateTime payedAt;

    private LocalDateTime updatedAt;
}
