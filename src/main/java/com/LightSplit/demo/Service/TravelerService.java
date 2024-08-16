package com.LightSplit.demo.Service;

import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.TravelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;

import jakarta.validation.ConstraintViolationException;

@Service
public interface TravelerService {

    public void createTraveler(Traveler traveler) throws ConstraintViolationException, TravelerCollectionException;

    public Traveler findSingleTraveler(String id) throws TravelerCollectionException;

    public Traveler updateTraveler(String id, Traveler traveler) throws TravelerCollectionException;

    public Traveler deleteTraveler(String id) throws TravelerCollectionException; 

    public Group addSingleTravelerToGroup(String groupId, String travelerId) throws GroupCollectionException, TravelerCollectionException; 

    public Item addTravelerToItem(String itemId, String travelerId) throws TravelerCollectionException, ItemCollectionException;
}
