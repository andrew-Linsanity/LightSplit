package com.LightSplit.demo.Service;

import java.util.HashMap;
import java.util.List;

import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.travelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;

import jakarta.validation.ConstraintViolationException;

public interface ItemService {

    public Item getSingleItem(String itemId) throws ItemCollectionException;

    public HashMap<String,Double> splitEqually(double cost, List<Traveler> groupTravelers, Traveler payer);
    
    public HashMap<String, Double> splitCustomized(Group group, double cost, List<Traveler> groupTravelers, List<Traveler> travelers, Traveler payer) throws ConstraintViolationException, travelerCollectionException, ItemCollectionException;
}
