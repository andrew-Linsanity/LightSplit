package com.LightSplit.demo.Components;

import org.springframework.stereotype.Component;

import com.LightSplit.demo.Model.Traveler;

@Component
public class FinalTransactions {
    // Fields
    private Traveler payer;

    private Traveler reciever;

    private double amount;

    public FinalTransactions(Traveler payer, Traveler reciever, double amount) {
        this.payer = payer;
        this.reciever = reciever;
        this.amount = amount;
    }

    public Traveler getPayer() {
        return payer;
    }

    public Traveler getReciever() {
        return reciever;
    } 

    public double getAmount() {
        return amount;
    }
}
