package com.example.stockscan.Analyzers;

import android.content.Context;
import android.util.Log;

import com.example.stockscan.Adapters.ScannedProductAdapter;
import com.example.stockscan.Models.Produce;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.List;

import androidx.annotation.NonNull;

public class TextProcessor extends ImageProcessorIMPL<Text> {

    private static final String TAG = "TextProcessor";
    protected static final String MANUAL_TESTING_LOG = "LogTagForTest";

    private final TextRecognizer textRecognizer;
    private final ScannedProductAdapter adapter = new ScannedProductAdapter();

    public TextProcessor(Context context) {
        super(context);
        textRecognizer = TextRecognition.getClient();
    }

    @Override
    protected Task detectInImage(InputImage image) {
        return textRecognizer.process(image).addOnSuccessListener(text -> {
            Log.d(TAG, "Analysis succeeded");
            adapter.getProduceFromText(text);
//            logExtrasForTesting(text);
        }).addOnFailureListener(e -> {
            Log.d(TAG, "Analysis failed");
            e.printStackTrace();
        });
    }

    @Override
    public void stop() {
        super.stop();
        textRecognizer.close();
    }

    @Override
    protected void onSuccess(@NonNull Text text) {
        logExtrasForTesting(text);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        e.printStackTrace();
    }

    public Produce getScannedProduce(){
        return adapter.getProduce();
    }

    private static void logExtrasForTesting(Text text) {
        if (text != null) {
            Log.v(MANUAL_TESTING_LOG, "Detected text has : " + text.getTextBlocks().size() + " blocks");
            for (int i = 0; i < text.getTextBlocks().size(); ++i) {
                List<Text.Line> lines = text.getTextBlocks().get(i).getLines();
                Log.v(
                        MANUAL_TESTING_LOG,
                        String.format("Detected text block %d has %d lines", i, lines.size()));
                for (int j = 0; j < lines.size(); ++j) {
                    String line = "";
                    List<Text.Element> elements = lines.get(j).getElements();
                    Log.v(
                            MANUAL_TESTING_LOG,
                            String.format("Detected text line %d has %d elements", j, elements.size()));

                    for (int k = 0; k < elements.size(); ++k) {
                        Text.Element element = elements.get(k);
                        line += element.getText() + " ";
//                        Log.v(
//                                MANUAL_TESTING_LOG,
//                                String.format("Detected text element %d says: %s", k, element.getText()));
//                        Log.v(
//                                MANUAL_TESTING_LOG,
//                                String.format(
//                                        "Detected text element %d has a bounding box: %s",
//                                        k, element.getBoundingBox().flattenToString()));
//                        Log.v(
//                                MANUAL_TESTING_LOG,
//                                String.format(
//                                        "Expected corner point size is 4, get %d", element.getCornerPoints().length));
//                        for (Point point : element.getCornerPoints()) {
//                            Log.v(
//                                    MANUAL_TESTING_LOG,
//                                    String.format(
//                                            "Corner point for element %d is located at: x - %d, y = %d",
//                                            k, point.x, point.y));
//                        }

                    }
                    Log.d(MANUAL_TESTING_LOG, line);
                }
            }
        }
    }


}
