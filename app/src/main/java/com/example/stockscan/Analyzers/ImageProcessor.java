package com.example.stockscan.Analyzers;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageProxy;

public interface ImageProcessor {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    void processImageProxy(ImageProxy proxy);
    void stop();
    boolean isAnalyzing();
    void requestAnalysis();
}
