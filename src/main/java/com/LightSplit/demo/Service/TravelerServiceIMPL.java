package com.LightSplit.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.travellerCollectionException;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.travelerRepository;

import jakarta.validation.ConstraintViolationException;

@Service
public class TravelerServiceIMPL {
    
    @Autowired
    private travelerRepository travRepo;
    
    public void createTraveler(Traveler traveler) throws ConstraintViolationException, travellerCollectionException {
        if(traveler.getNickName() == "") {
            throw new travellerCollectionException(travellerCollectionException.InvalidNickName());
        } else {
            travRepo.save(traveler);
        }
    } 
    

}
