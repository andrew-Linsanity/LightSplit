package com.LightSplit.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.TravelerCollectionException;

import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.DTO.TravelerDTO; 
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Repository.itemRepository;

import com.LightSplit.demo.Service.GroupService;
import com.LightSplit.demo.Service.ItemService;

import jakarta.validation.ConstraintViolationException;

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
    
    @PostMapping("/item") 
    public ResponseEntity<?> createItem(@RequestBody Item item) {
        try {
            return new ResponseEntity<>(itemService.saveItem(item), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable String itemId) {
        try {
            return new ResponseEntity<>(itemService.getSingleItem(itemId), HttpStatus.OK);
        } catch ( ItemCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("item/{itemId}/group/{groupId}/payment/{cost}/traveler/{payerUsername}")
    public ResponseEntity<?> splitCostEqually(@PathVariable String itemId, @PathVariable double cost, @PathVariable String groupId, @PathVariable String payerUsername, @RequestBody List<TravelerDTO> travelers) {
        try {
            Group group = groupService.getSingleGroup(groupId);
            Item item = itemService.getSingleItem(itemId);
            List<TravelerDTO> groupTravelers = groupService.findTravelersFromGroup(group, group.getTravelers());
            TravelerDTO payer = groupService.findSingleTravelerFromGroup(group, payerUsername);
            item.setPaymentMap(itemService.splitEqually(cost, groupTravelers, payer));
            groupRepo.save(group);
            itemRepo.save(item);
            
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch(ItemCollectionException | GroupCollectionException | TravelerCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } 
    } 

    /* FrontEnd: It will be nice if front end shows the expected amount payed by the last traveler in the group!
    /* Update balance, paid and shared by who with customized amount */
    // 1. in the request body, use balance field to save the amount travlers owed. Set it back to 0 after accounted for in the group. 
    // 1.1 Actually might not need to set to 0. Just don't save them to travRepo at last. 
    // 1.2 So the balance field in travRepository is like a temp variable for the updated balance 
    @PutMapping("item/{itemId}/customization/group/{groupId}/payment/{cost}/traveler/{payerUsername}")
    public ResponseEntity<?> splitCostCustomized(@PathVariable double cost, @PathVariable String groupId, @PathVariable String itemId, @PathVariable String payerUsername, @RequestBody List<TravelerDTO> travelers) {

        try {
            Group group = groupService.getSingleGroup(groupId);
            Item item = itemService.getSingleItem(itemId);
            List<TravelerDTO> groupTravelers = groupService.findTravelersFromGroup(group, group.getTravelers());
            TravelerDTO payer = groupService.findSingleTravelerFromGroup(group, payerUsername);
            item.setPaymentMap(itemService.splitCustomized(group, cost, groupTravelers, travelers, payer));
            groupRepo.save(group);
            itemRepo.save(item);

            return new ResponseEntity<>(group, HttpStatus.OK);
            
        } catch(ConstraintViolationException | ItemCollectionException | GroupCollectionException | TravelerCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } 
    }  
    
    
    // Add item to item list
    @PutMapping("/group/{groupId}/item")
    public ResponseEntity<?> addItemToGroup(@PathVariable String groupId, @RequestBody String itemId) { // Raw text for the request body
        try {
            return new ResponseEntity<Group>(itemService.addItemToGroup(itemId, groupId), HttpStatus.OK); 
        } catch(ItemCollectionException | GroupCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } 
    }

    /* delete the an item */
    // Make sure to reverse the payments. How ???????  
    // 2. add a global variable map that stores the transaction
    // 3. Potentially I might wanna save the items and transactions history in the itemRepo, but rn let's keep it simple

    @DeleteMapping("group/{groupId}/item/{itemId}") 
    public ResponseEntity<?> deleteItemFromGroup(@PathVariable String itemId, @PathVariable String groupId) {
        try {
            return new ResponseEntity<>(itemService.deleteItemFromGroup(itemId, groupId), HttpStatus.OK);
        } catch(ConstraintViolationException | ItemCollectionException | GroupCollectionException | TravelerCollectionException e) { 
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT); 
        } 
    }
}

