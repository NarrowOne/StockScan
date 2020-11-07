package com.example.stockscan.Models;


import com.google.mlkit.vision.text.Text;

public class Produce {
    private String iD;
    private String name;
    private String prodCode;
    private String batch;
    private double weight;
    private String expiryDate;
    private String[] tags;

    public Produce(){

    }

    public Produce(String iD, String name, String prodCode, String batch,
                   double weight, String expiryDate, String[] tags) {
        this.iD = iD;
        this.name = name;
        this.prodCode = prodCode;
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

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
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

    public static class Builder{
        private Text text;

        public Builder(Text text){
            this.text = text;
        }

        public Produce build(){


            return new Produce();
        }
    }
}
