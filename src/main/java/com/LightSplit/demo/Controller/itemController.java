package com.LightSplit.demo.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.travelerCollectionException;

import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Repository.itemRepository;

import com.LightSplit.demo.Service.GroupService;
import com.LightSplit.demo.Service.ItemService;

import jakarta.validation.ConstraintViolationException;
import com.LightSplit.demo.Repository.travelerRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/* When a new transaction occured, users can create a new item object */
// Error: Maybe I should have designed item controller before the split functions
// when an item object is deleted, needs to update the balance field as well.
@RestController
public class itemController {
    
    @Autowired
    private itemRepository itemRepo;

    @Autowired 
    private ItemService itemService;

    @Autowired
    private groupRepository groupRepo;

    @Autowired 
    private GroupService groupService;

    @Autowired
    private groupRepository groupRepo;

    @Autowired
    private travelerRepository travRepo;
    
    @PostMapping("/item") 
    public ResponseEntity<?> createItem(@RequestBody Item item) {
        try {
            itemRepo.save(item);
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable String itemId) {
            Optional<Item> itemOptional = itemRepo.findById(itemId);
            if(itemOptional.isPresent()) {
                return new ResponseEntity<>(itemOptional.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Item id: " + itemId  + " not Found.", HttpStatus.NOT_FOUND);
            }
    }


    @PutMapping("item/{itemId}/group/{groupId}/payment/{cost}/traveler/{payerId}")
    public ResponseEntity<?> splitCostEqually(@PathVariable String itemId, @PathVariable double cost, @PathVariable String groupId, @PathVariable String payerId, @RequestBody List<Traveler> travelers) {
        try {
            Group group = groupService.getSingleGroup(groupId);
            Item item = itemService.getSingleItem(itemId);
            List<Traveler> groupTravelers = groupService.findTravelersFromGroup(group, group.getTravelers());
            Traveler payer = groupService.findSingleTravelerFromGroup(group, payerId);
            item.setPaymentMap(itemService.splitEqually(cost, groupTravelers, payer));
            groupRepo.save(group);
            itemRepo.save(item);
            
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch(ItemCollectionException | GroupCollectionException | travelerCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } 
    } 

    /* FrontEnd: It will be nice if front end shows the expected amount payed by the last traveler in the group!
    /* Update balance, paid and shared by who with customized amount */
    // 1. in the request body, use balance field to save the amount travlers owed. Set it back to 0 after accounted for in the group. 
    // 1.1 Actually might not need to set to 0. Just don't save them to travRepo at last. 
    // 1.2 So the balance field in travRepository is like a temp variable for the updated balance 
    @PutMapping("item/{itemId}/customization/group/{groupId}/payment/{cost}/traveler/{payerId}")
    public ResponseEntity<?> splitCostCustomized(@PathVariable double cost, @PathVariable String groupId, @PathVariable String itemId, @PathVariable String payerId, @RequestBody List<Traveler> travelers) {

        try {
            Group group = groupService.getSingleGroup(groupId);
            Item item = itemService.getSingleItem(itemId);
            List<Traveler> groupTravelers = groupService.findTravelersFromGroup(group, group.getTravelers());
            Traveler payer = groupService.findSingleTravelerFromGroup(group, payerId);
            item.setPaymentMap(itemService.splitCustomized(group, cost, groupTravelers, travelers, payer));
            groupRepo.save(group);
            itemRepo.save(item);

            return new ResponseEntity<>(group, HttpStatus.OK);
            
        } catch(ConstraintViolationException | ItemCollectionException | GroupCollectionException | travelerCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } 
    }
    
    
    // Add item to item list
    @PutMapping("/group/{groupId}/item")
    public ResponseEntity<?> addItemToGroup(@PathVariable String groupId, @RequestBody String itemId) { // Raw text for the request body
        Optional<Group> groupOptional = groupRepo.findById(groupId);
        Optional<Item> itemOptional = itemRepo.findById(itemId);
        if(!groupOptional.isPresent()) return new ResponseEntity<>("Group with id: " + groupId + " not found.", HttpStatus.NOT_FOUND);
        if(!itemOptional.isPresent()) return new ResponseEntity<>("Item with id: " + itemId + " not found.", HttpStatus.NOT_FOUND);
        Group group = groupOptional.get();
        Item item = itemOptional.get();
        group.getItems().add(item);
        groupRepo.save(group); 
        return new ResponseEntity<Group>(group, HttpStatus.OK); 
    }

    /* delete the an item */
    // Make sure to reverse the payments. How ???????  
    // 2. add a global variable map that stores the transaction
    // 3. Potentially I might wanna save the items and transactions history in the itemRepo, but rn let's keep it simple

    @DeleteMapping("group/{groupId}/item/{itemId}") 
    public ResponseEntity<?> deleteItemFromGroup(@PathVariable String itemId, @PathVariable String groupId) {
        Optional<Group> groupOptional = groupRepo.findById(groupId);
        if(!groupOptional.isPresent()) return new ResponseEntity<>("Group with id: " + groupId + " not found.", HttpStatus.NOT_FOUND);
        Group group = groupOptional.get(); 
        List<Item> items = group.getItems(); 
        Item tarItem = itemRepo.findById(itemId).get(); //uncheck 
        for(Item item : items) { // find target item in the group 
            if(item.equals(tarItem)) {
                HashMap<String, Double> map = item.getPaymentMap();
                for(HashMap.Entry<String,Double> set : map.entrySet()) {
                    String tempId = set.getKey();
                    Traveler targetTrav = travRepo.findById(tempId).get();
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
        return new ResponseEntity<>(group, HttpStatus.OK);
    }
}

