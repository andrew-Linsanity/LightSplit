package com.LightSplit.demo.Exception;

public class TravelerCollectionException extends Exception {

    public TravelerCollectionException(String message) {
        super(message); // constructs an exception with specified and detailed messages
    }

    public static String InvalidNickName() {
        return "NickName must contains at least one letter."; 
    } 

    public static String TravelerNotInGroup(String groupId, String username) { 
        return "Traveler " + username + " is not in group: " + groupId; 
    } 

    public static String TravelerNotInItem(String itemId, String username) { 
        return "Traveler " + username + " is not in item: " + itemId; 
    }

    public static String TravelerAlreadyInItem(String username, String itemId) {
        return "Traveler " + username + " is not in the item: " + itemId + ".";
    } 
}
