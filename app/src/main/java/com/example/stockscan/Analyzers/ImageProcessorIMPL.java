package com.example.stockscan.Analyzers;

import android.app.ActivityManager;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.example.stockscan.Models.ScanPopup;
import com.example.stockscan.REST.RestClient;
import com.example.stockscan.Utils.ScopedExecutor;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import cz.msebera.android.httpclient.Header;

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
    public void processImageProxy(ImageProxy proxy, ScanPopup popup) {
        if(isShutdown || !analysisRequested){
            proxy.close();
            return;
        }

        Image image = proxy.getImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] imgBytes = new byte[buffer.capacity()];
        buffer.get(imgBytes);

        String encodedString = Base64.getEncoder().encodeToString(imgBytes);

        RequestParams params = new RequestParams();
        params.add("image_data", encodedString);

        RestClient.post("https://us-central1-stockscan-2d0f2.cloudfunctions.net/parse-image",
                        params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if(!response.getBoolean("error")){
                        Log.d(TAG, response.getString("image_text"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "error: "+responseString);
            }
        });

//        Bitmap bitmap = BitmapUtils.getBitmap(proxy);



        requestDetectInImage(
                InputImage.fromMediaImage(proxy.getImage(),
                        proxy.getImageInfo().getRotationDegrees()),
                popup)
                .addOnCompleteListener(result ->{
                    proxy.close();
                    analysisRequested = false;
                });
    }

    private Task<T> requestDetectInImage(final InputImage image, ScanPopup popup){
        final long startMS = SystemClock.elapsedRealtime();

        return detectInImage(image, popup).addOnFailureListener(executor, e -> {
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

    protected abstract Task<T> detectInImage(InputImage image, ScanPopup popup);
    protected abstract void onSuccess(@NonNull T results);
    protected abstract void onFailure(@NonNull Exception e);

}
