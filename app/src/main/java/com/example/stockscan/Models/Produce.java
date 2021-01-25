package com.example.stockscan.Models;


import android.util.Log;

import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

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
        private final Produce produce;

        public Builder(Text text){
            this.text = text;
            produce = new Produce();
        }

        public Produce build(){
            List<String> textBlock = new ArrayList<>();
            for (int i = 0; i < text.getTextBlocks().size(); ++i) {
                List<Text.Line> lines = text.getTextBlocks().get(i).getLines();
                for (int j = 0; j < lines.size(); ++j) {
                    List<Text.Element> elements = lines.get(j).getElements();
                    String line = "";
                    for (int k = 0; k < elements.size(); ++k) {
                        Text.Element element = elements.get(k);
                        line += element.getText() + " ";
                    }
                    textBlock.add(line);
                }
            }

            return produce;
        }
    }
}
