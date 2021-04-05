package com.example.stockscan.Models;


import com.google.mlkit.vision.text.Text;

import java.util.Arrays;

public class Produce {
    private String iD;
    private String name;
    private String product_code;
    private String producer;
    private String batch;
    private double weight;
    private String expiry;
    private String[] tags;

    public Produce(){

    }

    public Produce(String name, String product_code, String producer, String batch,
                   double weight, String expiry) {
        this.name = name;
        this.product_code = product_code;
        this.producer = producer;
        this.batch = batch;
        this.weight = weight;
        this.expiry = expiry;
    }

    public Produce(String iD, String name, String product_code, String producer, String batch,
                   double weight, String expiry, String[] tags) {
        this.iD = iD;
        this.name = name;
        this.product_code = product_code;
        this.producer = producer;
        this.batch = batch;
        this.weight = weight;
        this.expiry = expiry;
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

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
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

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Produce{" +
                "name='" + name + '\'' +
                ", product_code='" + product_code + '\'' +
                ", producer='" + producer + '\'' +
                ", batch='" + batch + '\'' +
                ", weight=" + weight +
                ", expiry='" + expiry + '\'' +
                '}';
    }
}
