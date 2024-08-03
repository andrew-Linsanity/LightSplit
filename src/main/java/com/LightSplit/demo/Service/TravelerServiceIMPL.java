package com.LightSplit.demo.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.travelerCollectionException;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.travelerRepository;

import jakarta.validation.ConstraintViolationException;

@Service
public class TravelerServiceIMPL implements TravelerService {
    
    @Autowired
    private travelerRepository travRepo;
    
    @Override
    public void createTraveler(Traveler traveler) throws ConstraintViolationException, travelerCollectionException {
        if(traveler.getNickName() == "" || traveler.getNickName() == null) {
            throw new travelerCollectionException(travelerCollectionException.InvalidNickName());
        } else {
            travRepo.save(traveler);
        }
    } 

    @Override
    public Traveler findSingleTraveler(String id) throws travelerCollectionException {
        Optional<Traveler> travelerOptional = travRepo.findById(id);
        if(!travelerOptional.isPresent()) {
            throw new travelerCollectionException(travelerCollectionException.TravelerNotFound(id));
        } 
        return travelerOptional.get();
    }
}
