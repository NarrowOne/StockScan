package com.example.stockscan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockscan.Analyzers.ImageProcessor;
import com.example.stockscan.Analyzers.TextProcessor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScanScreen extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    private Executor executor = Executors.newSingleThreadExecutor();

    private TextView nameDisplay, idDisplay, weightDisplay,
                        batchDisplay, expDisplay;
    private ImageView scanProd;
    private Button saveProd;

    PreviewView previewView;
    @Nullable private ProcessCameraProvider cameraProvider;
    @Nullable private ImageAnalysis analysisUseCase;
    @Nullable private ImageProcessor processor;

    private CameraSelector cameraSelector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;

    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_screen);

        previewView = findViewById(R.id.camera);

        nameDisplay = findViewById(R.id.nameDisplay);
        idDisplay = findViewById(R.id.idDisplay);
        weightDisplay = findViewById(R.id.weightDisplay);
        batchDisplay = findViewById(R.id.batchDisplay);
        expDisplay = findViewById(R.id.expDisplay);
        scanProd = findViewById(R.id.scanProd);
        saveProd = findViewById(R.id.saveProd);

        if (!allPermissionsGranted())
            getRuntimePermissions();

        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderListenableFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderListenableFuture.get();
                bindAllCameraUsecase();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

        cameraSource = new CameraSource.Builder(this, new TextRecognizer.Builder(this).build()).build();

        startCamera();

        scanProd.setOnClickListener(l ->{
            if(processor != null)
                processor.requestAnalysis();
        });

        saveProd.setOnClickListener(l ->{
            saveScannedProduct();
        });
    }

    private void bindAllCameraUsecase() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindCameraPreview();
            bindAnalysisUsecase();
        }
    }

    private void bindCameraPreview() {
        if(cameraProvider == null) return;

        Preview preview = new Preview.Builder().build();
        cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    private void bindAnalysisUsecase() {
        if (cameraProvider == null) return;
        if (analysisUseCase != null) cameraProvider.unbind(analysisUseCase);
        if (processor != null) processor.stop();

        processor = new TextProcessor(this);

        analysisUseCase = new ImageAnalysis.Builder().build();
        analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(this), image -> processor.processImageProxy(image));

        cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            getRuntimePermissions();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean allPermissionsGranted() {
        for (String perm : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED)
                Log.d(TAG, "Permission not granted: " + perm);
            return false;
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> neededPerms = new ArrayList<>();
        for (String perm : PERMISSIONS) {
            if (!isGranted(perm))
                neededPerms.add(perm);
        }

        if (!neededPerms.isEmpty())
            ActivityCompat.requestPermissions(
                    this, neededPerms.toArray(new String[0]),
                    PERMISSION_REQUESTS);
    }

    private boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        if (allPermissionsGranted()) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveScannedProduct(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        double weight = Double.parseDouble(weightDisplay.getText().toString()) *1000;

        Map<String, Object> scannedDetails = new HashMap<>();
        scannedDetails.put("Name", nameDisplay.getText().toString());
        scannedDetails.put("Product Code", idDisplay.getText().toString());
        scannedDetails.put("Weight", String.format("%.0f", weight));
        scannedDetails.put("Batch", batchDisplay.getText().toString());
        scannedDetails.put("Expiry", expDisplay.getText().toString());
        scannedDetails.put("Tags", "Meat, Pork");

        db.collection("Users")
                .document("Test")
                .collection("Stock")
                .add(scannedDetails)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Product Recorded", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.e(TAG, "Error recording product", task.getException());
                    }
                });
    }
}