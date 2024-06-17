package com.LightSplit.demo.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LightSplit.demo.Model.Traveler;


@Repository
public interface travelerRepository extends MongoRepository<Traveler, String>{
    
}
