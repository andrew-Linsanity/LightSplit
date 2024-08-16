package com.LightSplit.demo.Exception;

public class GroupCollectionException extends Exception {

    public GroupCollectionException(String message) {
        super(message); // constructs an exception with specified and detailed messages
    }

    public static String NotFoundException(String id) {
        return "Group with id " + id + " is not found.";
    }

    public static String GroupAlreadyExists() {
        return "Group with given name already exists.";
    }
}
