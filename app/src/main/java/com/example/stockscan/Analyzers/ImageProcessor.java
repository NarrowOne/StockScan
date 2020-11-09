package com.example.stockscan.Analyzers;

import android.os.Build;

import com.example.stockscan.Models.ScanPopup;

import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageProxy;

public interface ImageProcessor {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    void processImageProxy(ImageProxy proxy, ScanPopup popup);
    void stop();
    boolean isAnalyzing();
    void requestAnalysis();
}
