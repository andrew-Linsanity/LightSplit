package com.LightSplit.demo.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.travelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.itemRepository;

import jakarta.validation.ConstraintViolationException; 

public class ItemServiceIMPL implements ItemService {

    @Autowired
    private itemRepository itemRepo;

    @Override
    public Item getSingleItem(String itemId) throws ItemCollectionException {
        Optional<Item> itemOptional = itemRepo.findById(itemId);
        if(itemOptional.isPresent()) {
            Item item = itemOptional.get();
            return item;
        } else {
            throw new ItemCollectionException(ItemCollectionException.NotFoundException(itemId));
        }
    }

    @Override
    public HashMap<String,Double> splitEqually(double cost, List<Traveler> groupTravelers, Traveler payer) {
        double splitCost = cost / groupTravelers.size();
    
        HashMap<String,Double> travCostMap = new HashMap<>();
        // Update the balance of the travelers in the group
        for (Traveler groupTraveler : groupTravelers) {
            Double updateBalance = splitCost;
            if (groupTraveler.equals(payer)) {
                updateBalance -= cost;
            } 
            groupTraveler.setBalance(groupTraveler.getBalance() - updateBalance);
            travCostMap.put(groupTraveler.getId(), updateBalance);
        } 
        
        return travCostMap;
    }

    @Override
    public HashMap<String, Double> splitCustomized(Group group, double cost, List<Traveler> groupTravelers, List<Traveler> travelers, Traveler payer) throws ConstraintViolationException, travelerCollectionException, ItemCollectionException {

        HashMap<String, Double> travCostMap = new HashMap<>();

        // Check if all travelers in the request body are in the group
        Double checkSum = cost;

        for (Traveler traveler : travelers) {
            if (!groupTravelers.contains(traveler)) {
                throw new travelerCollectionException(travelerCollectionException.TravelerNotInGroup(traveler.getId(), group.getId()));
            } else {  // 1. save the selected list of traveler in a Map<Traveler, Double>; 
                checkSum -= traveler.getBalance(); 
                travCostMap.put(traveler.getId(), traveler != payer ? traveler.getBalance() : traveler.getBalance() - cost); // new update, unconfirmed
            }
        } 
        // check if sum doesn't add up: no need to run a for loop again!!! 
        if(checkSum != 0) throw new ItemCollectionException(ItemCollectionException.SumNotAddUp());
        // Update groupTraveler's balance
        // 2. for loop through groupTravelers, and find key (travleer)
        for(Traveler groupTraveler : groupTravelers) {
            double curBalance = groupTraveler.getBalance();
            double updateBalance = travCostMap.get(groupTraveler.getId());
            groupTraveler.setBalance(curBalance - updateBalance);
        } 

        return travCostMap;
    }
}
