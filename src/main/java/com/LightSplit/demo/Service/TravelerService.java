package com.LightSplit.demo.Service;

import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.travelerCollectionException;
import com.LightSplit.demo.Model.Traveler;

import jakarta.validation.ConstraintViolationException;

@Service
public interface TravelerService {

    public void createTraveler(Traveler traveler) throws ConstraintViolationException, travelerCollectionException;

    public Traveler findSingleTraveler(String id) throws travelerCollectionException;
}
