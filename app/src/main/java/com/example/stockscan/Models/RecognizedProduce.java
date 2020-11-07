package com.example.stockscan.Models;

public class RecognizedProduce {
    private final String name;
    private final String code;
    private final String producer;
    private final String barcode;

    public RecognizedProduce(String name, String code, String producer, String barcode) {
        this.name = name;
        this.code = code;
        this.producer = producer;
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getProducer() {
        return producer;
    }

    public String getBarcode() {
        return barcode;
    }
}
