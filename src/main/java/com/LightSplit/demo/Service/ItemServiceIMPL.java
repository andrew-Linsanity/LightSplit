package com.LightSplit.demo.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.TravelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Repository.itemRepository;

import jakarta.validation.ConstraintViolationException; 

@Service
public class ItemServiceIMPL implements ItemService {

    @Autowired
    private itemRepository itemRepo; 

    @Autowired
    private groupRepository groupRepo;

    @Autowired
    private GroupService groupService;

    @Autowired TravelerService travService;

    @Autowired
    public Item saveItem(Item item) {
        itemRepo.save(item);
        return item;
    }

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
    public Group addItemToGroup(String itemId, String groupId) throws GroupCollectionException, ItemCollectionException {
        Group group = groupService.getSingleGroup(groupId);
        Item item = getSingleItem(itemId);
        group.getItems().add(item);
        groupRepo.save(group); 
        return group;
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
    public HashMap<String, Double> splitCustomized(Group group, double cost, List<Traveler> groupTravelers, List<Traveler> travelers, Traveler payer) throws ConstraintViolationException, TravelerCollectionException, ItemCollectionException {

        HashMap<String, Double> travCostMap = new HashMap<>();

        // Check if all travelers in the request body are in the group
        Double checkSum = cost;

        for (Traveler traveler : travelers) {
            if (!groupTravelers.contains(traveler)) {
                throw new TravelerCollectionException(TravelerCollectionException.TravelerNotInGroup(traveler.getId(), group.getId()));
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

    @Override
    public Group deleteItemFromGroup(String itemId, String groupId) throws GroupCollectionException, ItemCollectionException, TravelerCollectionException {
        Group group = groupService.getSingleGroup(groupId);
        List<Item> items = group.getItems(); 
        Item tarItem = getSingleItem(itemId); //uncheck 
        for(Item item : items) { // find target item in the group 
            if(item.equals(tarItem)) {
                HashMap<String, Double> map = item.getPaymentMap();
                for(HashMap.Entry<String,Double> set : map.entrySet()) {
                    String tempId = set.getKey();
                    Traveler targetTrav = travService.findSingleTraveler(tempId);
                    Double tempBal = set.getValue();
                    for(Traveler groupTrav : group.getTravelers()) { // update balance of the grouptravelers
                        if(groupTrav.equals(targetTrav)) {
                            groupTrav.setBalance(groupTrav.getBalance() + tempBal);
                            break;
                        }
                    }
                };
                break;
            }
        } 
        items.remove(tarItem);
        groupRepo.save(group);
        return group;
    }
}
