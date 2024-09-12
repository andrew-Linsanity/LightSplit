package com.LightSplit.demo.DTO;

import com.LightSplit.demo.Model.Traveler;

import lombok.Getter;

@Getter
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
}
