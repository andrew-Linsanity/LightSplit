package com.LightSplit.demo.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.GroupCollectionException;

import com.LightSplit.demo.Exception.travelerCollectionException;

import com.LightSplit.demo.Model.FinalTransactions;

import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
import com.LightSplit.demo.Model.Traveler;
import com.LightSplit.demo.Repository.groupRepository;
import com.LightSplit.demo.Repository.travelerRepository;

import jakarta.validation.ConstraintViolationException;

@Service
public class GroupServiceIMPL implements GroupService {
    
    @Autowired
    private groupRepository groupRepo; 

    @Autowired
    private travelerRepository travRepo; 

    @Autowired
    private TravelerService travService; 


    /* Create a group, call Exception when its name field already exists */
    @Override // this function might be more useful for traveler
    public void createGroup(Group group) throws ConstraintViolationException, GroupCollectionException {
        Optional<Group> groupOptional = groupRepo.findByGroup(group.getName()); 
        if(groupOptional.isPresent()) {
            throw new GroupCollectionException(GroupCollectionException.GroupAlreadyExists());
        } else { 
            groupRepo.save(group); 
        }
    } 

    @Override
    public List<Group> getAllGroups() {
        List<Group> groups = groupRepo.findAll();
        if(groups.size() > 0) {
            return groups;
        } else {
            return new ArrayList<Group>();
        } 
    }

    @Override
    /* update the information of a group */
    public Group getSingleGroup(String groupId) throws GroupCollectionException {
        Optional<Group> groupOptional = groupRepo.findById(groupId); 
        // exception: not found
        if(groupOptional.isPresent()) {
            return groupOptional.get();
        } else {
            throw new GroupCollectionException(GroupCollectionException.NotFoundException(groupId));
        }
    }

    @Override
    public Group updateGroup(String groupId, Group group) throws GroupCollectionException {
        Optional<Group> groupOptional = groupRepo.findById(groupId);
        if(groupOptional.isPresent()) {
            Group groupToSave = groupOptional.get();
            groupToSave.setDescription(group.getDescription() == null ? groupToSave.getDescription() : group.getDescription());
            groupToSave.setName(group.getName() == null ? groupToSave.getName() : groupToSave.getName());
            groupToSave.setTravelers(group.getTravelers() == null ? groupToSave.getTravelers() : group.getTravelers());
            groupRepo.save(groupToSave);
            return groupToSave;
        } else {
            throw new GroupCollectionException(GroupCollectionException.NotFoundException(groupId));
        }
    }

    @Override 
    public Group deleteById(String groupId) throws GroupCollectionException {
        Optional<Group> groupOptional = groupRepo.findById(groupId);
        if(groupOptional.isPresent()) {
            groupRepo.deleteById(groupId);
            return groupOptional.get();
        } else {
            throw new GroupCollectionException(GroupCollectionException.NotFoundException(groupId));
        }
    }

    @Override
    public void deleteAllGroups() {
        groupRepo.deleteAll();
    }

    @Override
    public List<Traveler> findAllTravelers(Group group) {
        return group.getTravelers();
    }

    @Override
    public List<Traveler> findTravelersFromGroup(Group group, List<Traveler> travelers) throws travelerCollectionException {
        List<Traveler> groupTravelers = findAllTravelers(group);
        for (Traveler traveler : travelers) {
            if (!groupTravelers.contains(traveler)) {
                throw new travelerCollectionException(travelerCollectionException.TravelerNotInGroup(group.getId(), traveler.getId()));
            } 
        }
        return groupTravelers;
    }

    @Override
    public Traveler findSingleTravelerFromGroup(Group group, String travId) throws travelerCollectionException, GroupCollectionException {
        Traveler traveler = travService.findSingleTraveler(travId);
        if(!group.getTravelers().contains(traveler)) {
            throw new travelerCollectionException(travelerCollectionException.TravelerNotInGroup(travId, group.getId()));
        }
        return traveler;
    }


    public List<FinalTransactions> finalizeCost(Group group) {
        List<Traveler> travelers = group.getTravelers();
        ArrayList<Traveler> posTravs = new ArrayList<>();
        ArrayList<Traveler> negTravs = new ArrayList<>();
        ArrayList<FinalTransactions> finalTransactList = new ArrayList<>();
        
        for(Traveler trav : travelers) {
            if(trav.getBalance() < 0) {
                negTravs.add(trav);
            } else if(trav.getBalance() > 0){
                posTravs.add(trav);
            }
        }
  
        for(Traveler negTrav : negTravs) {
            boolean containsMatch = false;
            for(Traveler posTrav : posTravs) {
                if (negTrav.getBalance() + posTrav.getBalance() == 0) {
                    finalTransactList.add( new FinalTransactions(negTrav, posTrav, -negTrav.getBalance()));
                    containsMatch = true;
                } 
            } 
            if(containsMatch == false) {
                while(negTrav.getBalance() != 0) {
                    Traveler curPosTraveler = posTravs.get(0);
                    if( -negTrav.getBalance() < curPosTraveler.getBalance()) {
                        curPosTraveler.setBalance(curPosTraveler.getBalance() + negTrav.getBalance());
                        negTrav.setBalance(0);
                        finalTransactList.add( new FinalTransactions(negTrav, curPosTraveler, -negTrav.getBalance()));
                    } else {
                        negTrav.setBalance(negTrav.getBalance() + curPosTraveler.getBalance());
                        posTravs.removeFirst();
                        finalTransactList.add( new FinalTransactions(negTrav, curPosTraveler, curPosTraveler.getBalance()));
                    }
                }
            }
        }

        return finalTransactList;
    }
}
