package com.LightSplit.demo.Exception;

public class ItemCollectionException extends Exception {

    public ItemCollectionException(String message) {
        super(message);
    }
    
    public static String NotFoundException(String id) {
        return "Item with id: " + id + "is not found.";
    } 

    public static String SumNotAddUp() {
        return "Total shared amounts don't add up with the cost.";
    }
}
