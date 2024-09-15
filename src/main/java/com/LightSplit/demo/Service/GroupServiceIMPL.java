package com.LightSplit.demo.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LightSplit.demo.DTO.FinalTransactions;
import com.LightSplit.demo.DTO.TravelerDTO;
import com.LightSplit.demo.Exception.GroupCollectionException;

import com.LightSplit.demo.Exception.TravelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Repository.groupRepository;

import jakarta.validation.ConstraintViolationException;

@Service
public class GroupServiceIMPL implements GroupService {
    
    @Autowired
    private groupRepository groupRepo; 

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
    public List<TravelerDTO> findAllTravelers(Group group) {
        return group.getTravelers();
    } 

    @Override
    public Group addUserToGroup(String username, Group group) {
        TravelerDTO traveler = new TravelerDTO(username);
        group.getTravelers().add(traveler);
        groupRepo.save(group);  
        return group;
    }

    @Override
    public List<TravelerDTO> findTravelersFromGroup(Group group, List<TravelerDTO> travelers) throws TravelerCollectionException { 
        List<TravelerDTO> groupTravelers = findAllTravelers(group); 
        for (TravelerDTO traveler : travelers) {
            if (!groupTravelers.contains(traveler)) {
                throw new TravelerCollectionException(TravelerCollectionException.TravelerNotInGroup(group.getId(), traveler.getUsername()));
            } 
        }
        return groupTravelers;
    } 

    @Override
    public TravelerDTO findSingleTravelerFromGroup(Group group, String username) throws TravelerCollectionException, GroupCollectionException {
        List<TravelerDTO> groupTravelers = group.getTravelers(); 

        for(TravelerDTO trav : groupTravelers) {
            if(trav.getUsername().equals(username)) {
                return trav;
            }
        }
        throw new TravelerCollectionException(TravelerCollectionException.TravelerNotInGroup(username, group.getId()));
    }

    public List<FinalTransactions> finalizeCost(Group group) {
        List<TravelerDTO> travelers = group.getTravelers();
        ArrayList<TravelerDTO> posTravs = new ArrayList<>();
        ArrayList<TravelerDTO> negTravs = new ArrayList<>();
        ArrayList<FinalTransactions> finalTransactList = new ArrayList<>();
        
        for(TravelerDTO trav : travelers) {
            if(trav.getBalance() < 0) {
                negTravs.add(trav);
            } else if(trav.getBalance() > 0){
                posTravs.add(trav);
            }
        }
  
        for(TravelerDTO negTrav : negTravs) {
            boolean containsMatch = false;
            for(TravelerDTO posTrav : posTravs) {
                if (negTrav.getBalance() + posTrav.getBalance() == 0 && negTrav.getBalance() != 0) { 
                    finalTransactList.add( new FinalTransactions(negTrav, posTrav, -negTrav.getBalance()));
                    containsMatch = true;
                } 
            } 
            if(containsMatch == false) {
                while(negTrav.getBalance() != 0) {
                    TravelerDTO curPosTraveler = posTravs.get(0);
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
