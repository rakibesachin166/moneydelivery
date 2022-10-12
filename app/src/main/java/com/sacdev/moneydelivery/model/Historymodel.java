package com.sacdev.moneydelivery.model;

public class Historymodel {
    String address, id , amount , status ;
    public Historymodel(String id, String amount, String status, String address){
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.address = address;

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
