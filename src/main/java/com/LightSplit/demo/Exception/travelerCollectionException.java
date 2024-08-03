package com.LightSplit.demo.Exception;

public class travelerCollectionException extends Exception {

    public travelerCollectionException(String message) {
        super(message); // constructs an exception with specified and detailed messages
    }

    public static String InvalidNickName() {
        return "NickName must contains at least one letter."; 
    } 

    public static String TravelerNotFound(String id) {
        return "Traveler with id: " + id + " is not found.";
    } 

    public static String TravelerNotInGroup(String travId, String groupId) {
        return "Traveler with id: " + travId + " is not in group: " + groupId;
    }
}
