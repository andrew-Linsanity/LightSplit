package com.LightSplit.demo.Service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.GroupCollectionException;

import com.LightSplit.demo.Exception.travelerCollectionException;

import com.LightSplit.demo.Model.FinalTransactions;

import com.LightSplit.demo.Model.Group;
import com.LightSplit.demo.Model.Item;
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

    public List<Traveler> findAllTravelers(Group group) throws GroupCollectionException, travelerCollectionException;

    public List<Traveler> findTravelersFromGroup(Group group, List<Traveler> travelers) throws travelerCollectionException, GroupCollectionException;

    public Traveler findSingleTravelerFromGroup(Group group, String travId) throws travelerCollectionException, GroupCollectionException; 

    public List<Traveler> findAllTravelers(Group group);

    public List<FinalTransactions> finalizeCost(Group group);
} 
