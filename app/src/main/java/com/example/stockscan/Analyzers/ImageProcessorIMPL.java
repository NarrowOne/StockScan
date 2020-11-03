package com.example.stockscan.Analyzers;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.example.stockscan.Utils.ScopedExecutor;
import com.example.stockscan.Utils.BitmapUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

public abstract class ImageProcessorIMPL<T> implements ImageProcessor {
    private boolean isShutdown;
    private boolean analysisRequested = false;

    private final String TAG = "ImageProcessorBase";
    private final ActivityManager activityManager;
    private final ScopedExecutor executor;

    protected ImageProcessorIMPL(Context context){
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @ExperimentalGetImage
    public void processImageProxy(ImageProxy proxy) {
        if(isShutdown || !analysisRequested){
            proxy.close();
            return;
        }

//        Bitmap bitmap = BitmapUtils.getBitmap(proxy);

        requestDetectInImage(
                InputImage.fromMediaImage(proxy.getImage(),
                        proxy.getImageInfo().getRotationDegrees()))
                .addOnCompleteListener(result ->{
                    proxy.close();
                    analysisRequested = false;
                });
    }

    private Task<T> requestDetectInImage(final InputImage image){
        final long startMS = SystemClock.elapsedRealtime();
        return detectInImage(image).addOnFailureListener(executor, e -> {
            String error = "Failed to process. Error: " + e.getLocalizedMessage();
            Log.d(TAG, error);
        });
    }

    @Override
    public void stop() {
        isShutdown = true;
    }

    @Override
    public boolean isAnalyzing() {
        return analysisRequested;
    }

    @Override
    public void requestAnalysis() {
        analysisRequested = true;
    }

    protected abstract Task<T> detectInImage(InputImage image);
    protected abstract void onSuccess(@NonNull T results);
    protected abstract void onFailure(@NonNull Exception e);

}
