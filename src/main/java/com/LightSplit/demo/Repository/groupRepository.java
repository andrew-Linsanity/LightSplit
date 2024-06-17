package com.LightSplit.demo.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.LightSplit.demo.Model.Group;

@Repository
public interface groupRepository extends MongoRepository<Group, String> {

    @Query("{'group' : ?0}") // this automatically looks for the Group entity with the associated value
    Optional<Group> findByGroup(String group); 
}  

