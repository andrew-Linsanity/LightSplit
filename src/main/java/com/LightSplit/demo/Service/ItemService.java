package com.LightSplit.demo.Service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.TravelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;

import jakarta.validation.ConstraintViolationException;

@Service
public interface ItemService {

    public Item saveItem(Item item) throws ConstraintViolationException;

    public Item getSingleItem(String itemId) throws ItemCollectionException;

    public HashMap<String,Double> splitEqually(double cost, List<Traveler> groupTravelers, Traveler payer);
    
    public HashMap<String, Double> splitCustomized(Group group, double cost, List<Traveler> groupTravelers, List<Traveler> travelers, Traveler payer) throws ConstraintViolationException, TravelerCollectionException, ItemCollectionException;

    public Group addItemToGroup(String itemId, String groupId) throws GroupCollectionException, ItemCollectionException;
    
    public Group deleteItemFromGroup(String itemId, String groupId) throws GroupCollectionException, ItemCollectionException, TravelerCollectionException;
}
