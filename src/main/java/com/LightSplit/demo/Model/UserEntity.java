package com.LightSplit.demo.Model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    
    @Id
    private String id;

    private String username;

    private String password;

    @DBRef
    private List<Roles> roles = new ArrayList<>();
}
