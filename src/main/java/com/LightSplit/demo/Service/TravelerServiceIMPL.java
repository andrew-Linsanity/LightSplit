package com.LightSplit.demo.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Exception.ItemCollectionException;
import com.LightSplit.demo.Exception.TravelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Repository.itemRepository;
import com.LightSplit.demo.Repository.travelerRepository;

import jakarta.validation.ConstraintViolationException;

@Service
public class TravelerServiceIMPL implements TravelerService {
    
    @Autowired
    private travelerRepository travRepo;

    @Autowired
    private itemRepository itemRepo;

    @Autowired
    private groupRepository groupRepo;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService; 
    
    @Override
    public void createTraveler(Traveler traveler) throws ConstraintViolationException, TravelerCollectionException {
        if(traveler.getNickName() == "" || traveler.getNickName() == null) { 
            throw new TravelerCollectionException(TravelerCollectionException.InvalidNickName());
        } else { 
            travRepo.save(traveler); 
        } 
    } 

    @Override
    public Traveler findSingleTraveler(String id) throws TravelerCollectionException {
        Optional<Traveler> travelerOptional = travRepo.findById(id);
        if(!travelerOptional.isPresent()) {
            throw new TravelerCollectionException(TravelerCollectionException.TravelerNotFound(id));
        } 
        return travelerOptional.get(); 
    } 

    @Override
    public Traveler updateTraveler(String id, Traveler traveler) throws TravelerCollectionException {
        Optional<Traveler> travelerOptional = travRepo.findById(id);
        if(!travelerOptional.isPresent()) {
            throw new TravelerCollectionException(TravelerCollectionException.TravelerNotFound(id));
        } 
        Traveler travelerToSave = travelerOptional.get();
        travelerToSave.setNickName(traveler.getNickName() == null ? travelerToSave.getNickName() : traveler.getNickName());
        travRepo.save(travelerToSave);
        return travelerToSave;
    }

    @Override
    public Traveler deleteTraveler(String travelerId) throws TravelerCollectionException {
        Optional<Traveler> travelerOptional = travRepo.findById(travelerId);
        if(travelerOptional.isPresent()) { 
            travRepo.deleteById(travelerId); 
            return travelerOptional.get();
        } else { 
            throw new TravelerCollectionException(TravelerCollectionException.TravelerNotFound(travelerId));
        }
    }

    @Override
    public Group addSingleTravelerToGroup(@PathVariable String groupId, @PathVariable String travelerId) throws GroupCollectionException, TravelerCollectionException {
        // find group 
        Group group = groupService.getSingleGroup(groupId);
        List<Traveler> groupTravelers = groupService.findAllTravelers(group);
        // find trav
        Traveler travelerTarget = findSingleTraveler(travelerId);
        // find all travelers 
        if(!groupTravelers.contains(travelerTarget)) {
            groupTravelers.add(travelerTarget);
            groupRepo.save(group);
        }
        return group;
    } 

    @Override
    public Item addTravelerToItem(String itemId, String travelerId) throws TravelerCollectionException, ItemCollectionException {
        // find item
        Item item = itemService.getSingleItem(itemId);
        // find traveler 
        Traveler targetTraveler = findSingleTraveler(travelerId);

        List<Traveler> itemTravelers;
        if(item.getTravelers() == null) { 
            List<Traveler> tempItemTravelers = new ArrayList<>();
            tempItemTravelers.add(targetTraveler);
            item.setTravelers(tempItemTravelers);
        } else {
            itemTravelers = item.getTravelers();
            for (Traveler traveler : itemTravelers) {
                if (itemTravelers.contains(traveler)) {
                    throw new TravelerCollectionException(TravelerCollectionException.TravelerAlreadyInItem(travelerId, itemId));
                }
            }
            itemTravelers.add(targetTraveler);
        } 

        itemRepo.save(item);
        return item;
    }
}
