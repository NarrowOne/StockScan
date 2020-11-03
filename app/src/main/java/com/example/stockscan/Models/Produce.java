package com.example.stockscan.Models;

public class Produce {
    private String iD;
    private String name;
    private String prodID;
    private String batch;
    private double weight;
    private String expiryDate;
    private String[] tags;

    public Produce(String iD, String name, String prodID, String batch,
                   double weight, String expiryDate, String[] tags) {
        this.iD = iD;
        this.name = name;
        this.prodID = prodID;
        this.batch = batch;
        this.weight = weight;
        this.expiryDate = expiryDate;
        this.tags = tags;
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProdID() {
        return prodID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String[] getTags() {
        return tags;
    }
}
