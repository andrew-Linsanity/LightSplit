package com.LightSplit.demo.Exception;

public class travellerCollectionException extends Exception {

    public travellerCollectionException(String message) {
        super(message); // constructs an exception with specified and detailed messages
    }

    public static String InvalidNickName() {
        return "NickName must contains at least one letter."; 
    } 
}
