package com.LightSplit.demo.Controller;

import java.util.List;

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
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Service.GroupService;

import jakarta.validation.ConstraintViolationException;

@RestController
public class groupController {

    @Autowired 
    private groupRepository groupRepo;

    @Autowired
    private GroupService groupService;



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
    public ResponseEntity<?> deleteById(@PathVariable String groupId) {
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

    // PutMapping: finalize minimum transactions, ending the trip
    // 1. Begin with the list of travelers's balance
    // 2. Map the most negative to the most positive and canceel each other, then the second most positive.
    // 2.1 If one of them becomes 0, move on to the second highest.
    // 3. Structure: two arrays, one with positive balance trav, the other negative, sorted(ZIG ZAG)
    // 4. Should return a list of "final transactions" objects
    // 4.1 here we create methods of

    @PutMapping("group/{groupId}/finalCost")
    public ResponseEntity<?> findMinimumTransactions(@PathVariable String groupId) {
        try {
            Group group = groupRepo.findById(groupId).get();
            return new ResponseEntity<>(groupService.finalizeCost(group), HttpStatus.OK);

        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}