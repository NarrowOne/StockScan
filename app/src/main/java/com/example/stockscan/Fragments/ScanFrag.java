package com.example.stockscan.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.stockscan.Analyzers.ImageProcessor;
import com.example.stockscan.Analyzers.TextProcessor;
import com.example.stockscan.R;
import com.example.stockscan.Models.ScanPopup;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFrag extends Fragment {
    private static final String TAG = "ScanFrag";
    private static final int PERMISSION_REQUESTS = 1;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    private Context context;

    private PreviewView previewView;
    private ScanPopup popup;
//    CardView popup;
//    Button cancel;
//    TextView prodName, prodCode, prodBatch, prodWeight, prodExp;

    @Nullable private ProcessCameraProvider cameraProvider;
    @Nullable private ImageAnalysis analysisUseCase;
    @Nullable private ImageProcessor processor;

    private CameraSelector cameraSelector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;

    private CameraSource cameraSource;

    public ScanFrag() {
        // Required empty public constructor
    }

    public static ScanFrag newInstance() {
        return new ScanFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewView = view.findViewById(R.id.camera);
        ImageView scanProd = view.findViewById(R.id.scanProd);

        popup = new ScanPopup(view.findViewById(R.id.popup),
                              view.findViewById(R.id.cancelScan),
                              view.findViewById(R.id.saveProd),
                              view.findViewById(R.id.prodName),
                              view.findViewById(R.id.prodCode),
                              view.findViewById(R.id.prodBatch),
                              view.findViewById(R.id.prodWeight),
                              view.findViewById(R.id.prodExpiry));

        if (!allPermissionsGranted())
            getRuntimePermissions();

        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderListenableFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderListenableFuture.get();
                bindAllCameraUsecase();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));

        cameraSource = new CameraSource.Builder(context, new TextRecognizer.Builder(context).build()).build();

        startCamera();

        scanProd.setOnClickListener(l ->{
            processor.requestAnalysis();
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

        processor = new TextProcessor(context);

        analysisUseCase = new ImageAnalysis.Builder().build();
        analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(context), image -> {
            processor.processImageProxy(image, popup);
        });

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
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED)
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
                    getActivity(), neededPerms.toArray(new String[0]),
                    PERMISSION_REQUESTS);
    }

    private boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        if (allPermissionsGranted()) {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private void saveScannedProduct(){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        double weight = Double.parseDouble(weightDisplay.getText().toString()) *1000;
//
//        Map<String, Object> scannedDetails = new HashMap<>();
//        scannedDetails.put("Name", nameDisplay.getText().toString());
//        scannedDetails.put("Product Code", idDisplay.getText().toString());
//        scannedDetails.put("Weight", String.format("%.0f", weight));
//        scannedDetails.put("Batch", batchDisplay.getText().toString());
//        scannedDetails.put("Expiry", expDisplay.getText().toString());
//        scannedDetails.put("Tags", "Meat, Pork");
//
//        db.collection("Users")
//                .document("Test")
//                .collection("Stock")
//                .add(scannedDetails)
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful()){
//                        Toast.makeText(context, "Product Recorded", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Log.e(TAG, "Error recording product", task.getException());
//                    }
//                });
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }
}