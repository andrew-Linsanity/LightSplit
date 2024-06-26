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
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Repository.travelerRepository;
import com.LightSplit.demo.Service.GroupService;

import jakarta.validation.ConstraintViolationException;

@RestController
public class groupController {

    @Autowired 
    private groupRepository groupRepo;

    @Autowired
    private travelerRepository travRepo;

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
        List<Group> groups = groupService.getAllGroups(); // ok, so it has become clear that : 1) we want controllers to hanfle the http reponse 2) and service to handle the logics; 
        // Yep, now I understnand: we include the business logic in controller first to check the end points, if it works then we at last refactor it to the service class
        if(groups.size() > 0) {
            return new ResponseEntity<List<Group>>(groups, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("No groups found", HttpStatus.NOT_FOUND);
        }
    }
    /* Get a single group */
    @GetMapping("/groups/{id}")
    public ResponseEntity<?> getSingleGroup(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<Group>(groupService.getSingleGroup(id), HttpStatus.OK);
        }
        catch(GroupCollectionException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } 
    } 
    /* Ｕpdate group info; name, description, travelers */  
    // When users update the info, will they see empty boxes they need to fill up again, or the previous edition that they can edit?
    // is this backend's responsibility or front end?
    @PutMapping("/group/{id}") 
    public ResponseEntity<?> updateGroup(@PathVariable("id") String id, @RequestBody Group group) { // is it possible to get only name tho!
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

    /* Add traveler to a group */
    // Question: make sure there's no duplicate names in a group
    @PutMapping("/group/{groupId}/traveler/{travelerId}")
    public ResponseEntity<?> addSingleTravelerToGroup(@PathVariable String groupId, @PathVariable String travelerId) {
        Optional<Group> groupOptional = groupRepo.findById(groupId);
        Optional<Traveler> travelerOptional = travRepo.findById(travelerId);
        if(!groupOptional.isPresent()) {
            return new ResponseEntity<String>("Group with ID: " + groupId + "is not found.", HttpStatus.NOT_FOUND);
        }
        if(!travelerOptional.isPresent()) {
            return new ResponseEntity<String>("Traveler with ID: " + travelerId + "is not found.", HttpStatus.NOT_FOUND);
        }
        Group group = groupOptional.get();
        List<Traveler> groupTravelers = group.getTravelers();
        Traveler travelerTarget = travelerOptional.get();
        // delete the travler from the group
        if(!groupTravelers.contains(travelerTarget)) {
            groupTravelers.add(travelerTarget);
            groupRepo.save(group);
            return new ResponseEntity<Group>(group, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Traveler with id: " + travelerId + " already exists in the group", HttpStatus.CONFLICT);
        }
    }

    // DeleteMapping: remove traveler from a group
    // @DeleteMapping("/group/{groupId}/traveler/{travelerId}")
    
    // DeleteMapping
    @DeleteMapping("group/{groupId}/traveler/{travelerId}")
    public ResponseEntity<?> deleteSingleTravelerFromGroup(@PathVariable String groupId, @PathVariable String travelerId) {
        Optional<Group> groupOptional= groupRepo.findById(groupId);
        Optional<Traveler> travelerOptional = travRepo.findById(travelerId);
        if(!groupOptional.isPresent()) {
            return new ResponseEntity<String>("Group with id: " + groupId + " is not found.", HttpStatus.NOT_FOUND);
        }
        if(!travelerOptional.isPresent()) {
            return new ResponseEntity<String>("Traveler with id: " + travelerId + " is not found.", HttpStatus.NOT_FOUND);
        }

        Group group = groupOptional.get();
        List<Traveler> groupTravelers = group.getTravelers();
        Traveler travelerTarget = travelerOptional.get();
        // delete the travler from the group
        if(groupTravelers.contains(travelerTarget)) {
            groupTravelers.remove(travelerTarget);
            groupRepo.save(group);
            return new ResponseEntity<Group>(group, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Traveler " + travelerId + " is not found in the group.", HttpStatus.NOT_FOUND);
        } 
    }

    // Update balance, payed by who, and shared by who; split equally
    @PutMapping("group/{groupId}/payment/{cost}/traveler/{payerId}")
    public ResponseEntity<?> splitCostEqually(@PathVariable double cost, @PathVariable String groupId, @PathVariable String payerId, @RequestBody List<Traveler> travelers) {
        Optional<Group> groupOptional= groupRepo.findById(groupId);
        if (!groupOptional.isPresent()) {
            return new ResponseEntity<>("Group with id: " + groupId + " is not found.", HttpStatus.NOT_FOUND);
        }

        Group group = groupOptional.get();
        List<Traveler> groupTravelers = group.getTravelers();
        Optional<Traveler> payerOptional = travRepo.findById(payerId);

        if(!payerOptional.isPresent()) return new ResponseEntity<>("Payer with id: " + groupId + " is not found.", HttpStatus.NOT_FOUND);
        Traveler payer = payerOptional.get();

        if(!groupTravelers.contains(payer)) return new ResponseEntity<>("Payer with id: " + groupId + " is not in the group.", HttpStatus.NOT_FOUND);
    
        // Calculate the split cost
        double splitCost = cost / travelers.size();
    
        // Check if all travelers in the request body are in the group
        for (Traveler traveler : travelers) {
            if (!groupTravelers.contains(traveler)) {
                return new ResponseEntity<>("Traveler " + traveler.getId() + " is not in the group: " + groupId + ".", HttpStatus.NOT_FOUND);
            }
        }
        
        // Update the balance of the travelers in the group
        for (Traveler groupTraveler : groupTravelers) {
            if (travelers.contains(groupTraveler)) {
                groupTraveler.setBalance(groupTraveler.getBalance() - splitCost);
            }
            if (groupTraveler.equals(payer)) {
                groupTraveler.setBalance(groupTraveler.getBalance() + cost);
            }
        }
        
        // Save the group after updating the travelers' balances
        groupRepo.save(group);

        return new ResponseEntity<>(group, HttpStatus.OK);
    } 
    
    /* FrontEnd: It will be nice if front end shows the expected amount payed by the last traveler in the group!
    /* Update balance, paid and shared by who with customized amount */
    // 1. in the request body, use balance field to save the amount travlers owed. Set it back to 0 after accounted for in the group. 
    // 1.1 Actually might not need to set to 0. Just don't save them to travRepo at last. 
    // 1.2 So the balance field in travRepository is like a temp variable for the updated balance 
    @PutMapping("customization/group/{groupId}/payment/{cost}/traveler/{payerId}")
    public ResponseEntity<?> splitCostCustomized(@PathVariable double cost, @PathVariable String groupId, @PathVariable String payerId, @RequestBody List<Traveler> travelers) {
        Optional<Group> groupOptional= groupRepo.findById(groupId);
        if (!groupOptional.isPresent()) {
            return new ResponseEntity<>("Group with id: " + groupId + " is not found.", HttpStatus.NOT_FOUND);
        }
        
        Group group = groupOptional.get();
        List<Traveler> groupTravelers = group.getTravelers();
        Optional<Traveler> payerOptional = travRepo.findById(payerId);

        if(!payerOptional.isPresent()) return new ResponseEntity<>("Payer with id: " + groupId + " is not found.", HttpStatus.NOT_FOUND);
        Traveler payer = payerOptional.get();

        if(!groupTravelers.contains(payer)) return new ResponseEntity<>("Payer with id: " + groupId + " is not in the group.", HttpStatus.NOT_FOUND);
        
        HashMap<Traveler, Double> travCostMap = new HashMap<>();

        // Check if all travelers in the request body are in the group
        Double checkSum = cost;

        for (Traveler traveler : travelers) {
            if (!groupTravelers.contains(traveler)) {
                return new ResponseEntity<>("Traveler " + traveler.getId() + " is not in the group: " + groupId + ".", HttpStatus.NOT_FOUND);
            } else if(traveler.getBalance() == 0) { // Catch exceptino when the amount owed is 0; or if the field is null (no need, taken care of by the validation class)
                return new ResponseEntity<String>("Traveler with id: " + traveler.getId() + " cannot have balance = 0.", HttpStatus.CONFLICT);
            } else {  // 1. save the selected list of traveler in a Map<Traveler, Double>; 
                travCostMap.put(traveler, traveler.getBalance()); 
                checkSum -= traveler.getBalance(); 
            }
        } 
        // check if sum doesn't add up: no need to run a for loop again!!! 
        if(checkSum != 0) return new ResponseEntity<String>("Total shared amounts don't add up with the cost.", HttpStatus.CONFLICT);
        // Update groupTraveler's balance
        // 2. for loop through groupTravelers, and find key (travleer)
        for(Traveler groupTraveler : groupTravelers) {
            double curBalance = groupTraveler.getBalance();
            double updateBalance = travCostMap.get(groupTraveler);
            if(groupTraveler.equals(payer)) {
                groupTraveler.setBalance(curBalance - updateBalance + cost);
            } else {
                groupTraveler.setBalance(curBalance - updateBalance);
            }
        } 

        // Save the group after updating the travelers' balances
        groupRepo.save(group);

        return new ResponseEntity<>(group, HttpStatus.OK);
    }
}