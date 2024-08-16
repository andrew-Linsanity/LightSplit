package com.LightSplit.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.TravelerCollectionException;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.travelerRepository;
import com.LightSplit.demo.Service.TravelerService;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class TravelerController {
    
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
        } catch(TravelerCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        }
    } 

    @PostMapping("/traveler/{travId}") 
    public ResponseEntity<?> updateTraveler(@PathVariable String travId, Traveler traveler) {
        try {
            return new ResponseEntity<>(travService.updateTraveler(travId, traveler), HttpStatus.OK);
        } catch(TravelerCollectionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/traveler/{travelerId}")
    public ResponseEntity<?> deleteById(@PathVariable String travelerId) {
        try {
            travService.deleteTraveler(travelerId);
            return new ResponseEntity<String>("Successfully deleted traveler: " + travelerId, HttpStatus.OK);
        } catch(TravelerCollectionException e) {
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
        try {
            return new ResponseEntity<>(travService.addSingleTravelerToGroup(groupId, travelerId), HttpStatus.OK);
        } catch(TravelerCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(GroupCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Add traveler to the list field in an item
    @PutMapping("/item/{itemId}/traveler/{travelerId}")
    public ResponseEntity<?> addTravelerToItem(@PathVariable("itemId") String itemId, @PathVariable("travelerId") String travelerId) {
        try {
            return new ResponseEntity<Item>(travService.addTravelerToItem(itemId, travelerId), HttpStatus.OK);
        } catch(TravelerCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(ItemCollectionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
