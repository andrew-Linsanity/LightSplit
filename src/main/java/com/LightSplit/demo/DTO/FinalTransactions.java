package com.LightSplit.demo.DTO;

import lombok.Getter;

@Getter
public class FinalTransactions {
    // Fields
    private TravelerDTO payer;

    private TravelerDTO reciever;

    private double amount;

    public FinalTransactions(TravelerDTO payer, TravelerDTO reciever, double amount) {
        this.payer = payer;
        this.reciever = reciever;
        this.amount = amount;
    }
}
