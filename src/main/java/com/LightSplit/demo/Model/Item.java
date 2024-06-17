package com.LightSplit.demo.Model;

import java.time.LocalDateTime;

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
@Document(collection = "items")
public class Item {
    
    @Id
    private String Id;
    
    private String name;

    private Double price;

    private LocalDateTime payedAt;

    private LocalDateTime updatedAt;
}
