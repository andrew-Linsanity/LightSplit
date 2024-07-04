package com.LightSplit.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.LightSplit.demo.Exception.travellerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Repository.itemRepository;
import com.LightSplit.demo.Repository.travelerRepository;
import com.LightSplit.demo.Service.TravelerService;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class travelerController {

    @Autowired
    private groupRepository groupRepo;

    @Autowired 
    private itemRepository itemRepo;
    
    @Autowired
    private travelerRepository travRepo;

    @Autowired
    private TravelerService travService;

    @GetMapping("/travelers")
    public ResponseEntity<?> getAllTravelers() {
        List<Traveler> travelers = travRepo.findAll();
        if(travelers.size() > 0) { 
            return new ResponseEntity<>(travelers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No travelers available.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/traveler")
    public ResponseEntity<?> createTraveler(@RequestBody Traveler traveler) {
        try {
            travService.createTraveler(traveler);
            return new ResponseEntity<Traveler>(traveler, HttpStatus.OK);
        } catch(ConstraintViolationException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch(travellerCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        }
    } 

    @DeleteMapping("traveler/{travelerId}")
    public ResponseEntity<?> deleteById(@PathVariable String travelerId) {
        Optional<Traveler> travelerOptional = travRepo.findById(travelerId);
        if(travelerOptional.isPresent()) {
            travRepo.deleteById(travelerId);
            return new ResponseEntity<String>("Successfully deleted traveler: " + travelerId, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Id: " + travelerId + " is not found.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("travelers")
    public ResponseEntity<?> deleteAll() {
        if(travRepo.count() > 0) {
            travRepo.deleteAll();
            return new ResponseEntity<String>("Successfully deleted all travelers.", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("There are no travelers in the repository.", HttpStatus.NOT_FOUND);
        }
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

    // DeleteMapping
    @DeleteMapping("/group/{groupId}/traveler/{travelerId}")
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

    // Add traveler to the list field in an item
    @PutMapping("/item/{itemId}/traveler/{travelerId}")
    public ResponseEntity<?> addTravelerToItem(@PathVariable("itemId") String itemId, @PathVariable("travelerId") String travelerId) {
        Optional<Item> itemOptional = itemRepo.findById(itemId);
        Optional<Traveler> travelerOptional = travRepo.findById(travelerId);
        if(!itemOptional.isPresent()) {
            return new ResponseEntity<String>("item with ID: " + itemId + " is not found.", HttpStatus.NOT_FOUND);
        }
        if(!travelerOptional.isPresent()) {
            return new ResponseEntity<String>("Traveler with ID: " + travelerId + "is not found.", HttpStatus.NOT_FOUND);
        }
        Item item = itemOptional.get();
        Traveler targetTraveler = travelerOptional.get();
        List<Traveler> itemTravelers = item.getTravelers();
        for (Traveler traveler : itemTravelers) {
            if (!itemTravelers.contains(traveler)) {
                return new ResponseEntity<>("Traveler " + traveler.getId() + " is not in the item: " + itemId + ".", HttpStatus.NOT_FOUND);
            }
        }
        itemTravelers.add(targetTraveler);
        itemRepo.save(item);
        return new ResponseEntity<Item>(item, HttpStatus.OK);
    }

    // delete traveler from item
    
}
