package com.LightSplit.demo.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.LightSplit.demo.Exception.GroupCollectionException;
import com.LightSplit.demo.Model.Group;

import jakarta.validation.ConstraintViolationException;

@Service
public interface GroupService {

    public void createGroup(Group group) throws ConstraintViolationException, GroupCollectionException;

    public List<Group> getAllGroups();

    public Group getSingleGroup(String groupID) throws GroupCollectionException;

    public Group updateGroup(String groupId, Group group) throws GroupCollectionException;

    public Group deleteById(String groupId) throws GroupCollectionException;

    public void deleteAllGroups();
} 
