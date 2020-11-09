package com.example.stockscan.Adapters;

import android.util.Log;

import com.example.stockscan.Models.Produce;
import com.example.stockscan.Models.RecognizedProduce;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ScannedProductAdapter {
    private static final String TAG = "Product Adapter";
    private final Produce produce;
    private List<RecognizedProduce> recognizedProduces;
    private final Object lock = new Object();

    public ScannedProductAdapter() {
        produce = new Produce();
    }

    public Task<QuerySnapshot> getProduceFromText(Text text){
        if(text == null) return null;
//        if(!validateScan(text)) return;
        recognizedProduces = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return db.collection("Recognized Products")
                .get().addOnCompleteListener(task -> {
            boolean valid = false;

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String name = doc.get("Name").toString();
                    String code = doc.get("Code").toString();
                    String producer = doc.get("Producer").toString();
                    String barcode = doc.get("Barcode").toString();

                    recognizedProduces.add(
                            new RecognizedProduce(name, code, producer, barcode)
                    );
                }


                for (int i = 0; i < text.getTextBlocks().size(); ++i) {
                    List<Text.Line> lines = text.getTextBlocks().get(i).getLines();
                    for (int j = 0; j < lines.size(); ++j) {
                        List<Text.Element> elements = lines.get(j).getElements();
                        String line = "";
                        for (int k = 0; k < elements.size(); ++k) {
                            Text.Element element = elements.get(k);
                            line += element.getText().toLowerCase() + " ";
                        }
                        Log.d(TAG, "Line " + j + ":\n" + line);
                        for (int k = 0; k < recognizedProduces.size(); k++) {
                            String name = recognizedProduces.get(k).getName().toLowerCase();
                            if (line.contains(name.toLowerCase())) {
                                valid = true;
                                produce.setName(name);
                            }
                        }
                    }
                }
                if(valid) {
                    Produce newProduce = new Produce.Builder(text).build();
                    produce.setBatch(newProduce.getBatch());
                    produce.setExpiryDate(newProduce.getExpiryDate());
                    produce.setProdCode(newProduce.getProdCode());
                    produce.setWeight(newProduce.getWeight());
                }
            }

        });
    }

//    Check if produce is already recorded
//    TODO add check for producer, code and barcode
//    public boolean validateScan(Text text){
//        boolean valid = false;
//        final boolean[] dataRetrieved = {false};
//        recognizedProduces = new ArrayList<>();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//        db.collection("Recognized Products")
//                .get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot doc : task.getResult()) {
//                    String name = doc.get("Name").toString();
//                    String code = doc.get("Code").toString();
//                    String producer = doc.get("Producer").toString();
//                    String barcode = doc.get("Barcode").toString();
//
//                    recognizedProduces.add(
//                            new RecognizedProduce(name, code, producer, barcode)
//                    );
//                }
//            }
//
//        });
//
//        for (int i = 0; i < text.getTextBlocks().size(); ++i) {
//            List<Text.Line> lines = text.getTextBlocks().get(i).getLines();
//            for (int j = 0; j < lines.size(); ++j) {
//                List<Text.Element> elements = lines.get(j).getElements();
//                String line = "";
//                for (int k = 0; k < elements.size(); ++k) {
//                    Text.Element element = elements.get(k);
//                    line += element.getText().toLowerCase() + " ";
//                }
//                Log.d(TAG, "Line " + j + ":\n" + line);
//                for (int k = 0; k < recognizedProduces.size(); k++) {
//                    String name = recognizedProduces.get(k).getName();
//                    if (line.contains(name.toLowerCase())) {
//                        valid = true;
//                        produce.setName(name);
//                    }
//                }
//            }
//        }
//        return valid;
//    }

    public Produce getProduce() {
        return produce;
    }
}
