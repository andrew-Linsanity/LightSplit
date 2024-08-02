package com.LightSplit.demo.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.travelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Repository.itemRepository;
import com.LightSplit.demo.Repository.travelerRepository;
import com.LightSplit.demo.Service.GroupService;
import com.LightSplit.demo.Service.ItemService;

import jakarta.validation.ConstraintViolationException;

@RestController
public class groupController {

    @Autowired 
    private groupRepository groupRepo;

    @Autowired
    private travelerRepository travRepo;

    @Autowired
    private itemRepository itemRepo;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService;


    /* Create a group of travelers */
    @PostMapping("/group")
    public ResponseEntity<?> createGroup(@RequestBody Group group) { 
        try { 
            groupService.createGroup(group);
            return new ResponseEntity<Group>(group, HttpStatus.OK);
        } catch(ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch(GroupCollectionException e) { // The general Exception (catach all), with additional, specific Exception
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    } 

    /* Get all groups */
    @GetMapping("/groups")
    public ResponseEntity<?> getAllGroups() {
        try {
            List<Group> groups = groupService.getAllGroups();
            return new ResponseEntity<>(groups, HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /* Get a single group */
    @GetMapping("/group/{id}")
    public ResponseEntity<?> getSingleGroup(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<Group>(groupService.getSingleGroup(id), HttpStatus.OK);
        }
        catch(GroupCollectionException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } 
    } 

    /* Ｕpdate group info; name, description, travelers */  
    @PutMapping("/group/{id}") 
    public ResponseEntity<?> updateGroup(@PathVariable("id") String id, @RequestBody Group group) { 
        try {
            return new ResponseEntity<Group>(groupService.updateGroup(id, group), HttpStatus.OK);
        } 
        catch(GroupCollectionException e) { 
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<?> deleteById(@PathVariable("groupId") String groupId) {
        try {
            groupService.deleteById(groupId);
            return new ResponseEntity<String>("Successfully delete id: " + groupId, HttpStatus.OK);
        } 
        catch(GroupCollectionException e) { 
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }  
    }

    // Delete Every Group 
    @DeleteMapping("/groups")
    public ResponseEntity<?> deleteAllGroups() {
        groupService.deleteAllGroups();
        return new ResponseEntity<String>("Successfully deleted every group!", HttpStatus.OK);
    }

}