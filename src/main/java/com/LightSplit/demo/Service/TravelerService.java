package com.LightSplit.demo.Service;

import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.travellerCollectionException;
import com.LightSplit.demo.Model.Traveler;

import jakarta.validation.ConstraintViolationException;

@Service
public interface TravelerService {

    public void createTraveler(Traveler traveler) throws ConstraintViolationException, travellerCollectionException;
}
