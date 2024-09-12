package com.LightSplit.demo.Repository;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.LightSplit.demo.Model.Roles;

public interface RoleRepository extends MongoRepository<Roles, String> {
    Optional<Roles> findByName(String name);

    Boolean existsByName(String name);
}