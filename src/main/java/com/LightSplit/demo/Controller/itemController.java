package com.LightSplit.demo.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Repository.itemRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/* When a new transaction occured, users can create a new item object */
// Error: Maybe I should have designed item controller before the split functions
// when an item object is deleted, needs to update the balance field as well.
@RestController
public class itemController {
    
    @Autowired
    private itemRepository itemRepo;
    
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
    /* delete the an item */
    // Make sure to reverse the payments. How ???????  
    // 2. add a global variable map that stores the transaction
    // @DeletetEntity("/item")
    // public ResponseEntity<?> deleItem(@RequestBody Item item) {

    // }
}

