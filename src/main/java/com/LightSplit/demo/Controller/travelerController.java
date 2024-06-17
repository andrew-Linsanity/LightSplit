package com.LightSplit.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.LightSplit.demo.Exception.travellerCollectionException;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.travelerRepository;
import com.LightSplit.demo.Service.TravelerServiceIMPL;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class travelerController {
    
    @Autowired
    private travelerRepository travRepo;

    @Autowired
    private TravelerServiceIMPL travService;

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
}
