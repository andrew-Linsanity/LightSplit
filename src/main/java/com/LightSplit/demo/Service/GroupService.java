package com.LightSplit.demo.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.LightSplit.demo.DTO.FinalTransactions;
import com.LightSplit.demo.DTO.TravelerDTO;
import com.LightSplit.demo.Exception.GroupCollectionException;

import com.LightSplit.demo.Exception.TravelerCollectionException;
import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Traveler;

import jakarta.validation.ConstraintViolationException;

@Service
public interface GroupService {

    public void createGroup(Group group) throws ConstraintViolationException, GroupCollectionException;

    public List<Group> getAllGroups();

    public Group getSingleGroup(String groupID) throws GroupCollectionException;

    public Group updateGroup(String groupId, Group group) throws GroupCollectionException;

    public Group deleteById(String groupId) throws GroupCollectionException;

    public void deleteAllGroups();

    public List<TravelerDTO> findAllTravelers(Group group) throws GroupCollectionException, TravelerCollectionException;

    public List<TravelerDTO> findTravelersFromGroup(Group group, List<TravelerDTO> travelers) throws TravelerCollectionException, GroupCollectionException;

    public TravelerDTO findSingleTravelerFromGroup(Group group, String travId) throws TravelerCollectionException, GroupCollectionException; 

    public List<FinalTransactions> finalizeCost(Group group); 
} 
