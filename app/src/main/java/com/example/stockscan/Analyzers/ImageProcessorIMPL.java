package com.example.stockscan.Analyzers;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.nfc.Tag;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.example.stockscan.Models.ScanPopup;
import com.example.stockscan.REST.OCRRest;
import com.example.stockscan.REST.RestClient;
import com.example.stockscan.Utils.ScopedExecutor;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;

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
        byte[] imgBytes = null;

        imgBytes = NV21toJPEG(
                YUV_420_888toNV21(image),
                image.getWidth(), image.getHeight());

//        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//        byte[] imgBytes = new byte[buffer.capacity()];
//        buffer.get(imgBytes);



        String encodedString = Base64.getEncoder().encodeToString(imgBytes);

//        loopJHTTPCall(encodedString);

        OCRRest ocrRest = new OCRRest(popup);
        HashMap<String, Object> data = new HashMap<>();
        data.put("image_data", encodedString);
        ocrRest.execute(data);

//        String json = "{\"image_data\" : \""+encodedString+"\"}";
//
//        RestClient.post("https://us-central1-stockscan-2d0f2.cloudfunctions.net/parse-image",
//                                 json);

        analysisRequested = false;

//        Bitmap bitmap = BitmapUtils.getBitmap(proxy);



//        requestDetectInImage(
//                InputImage.fromMediaImage(proxy.getImage(),
//                        proxy.getImageInfo().getRotationDegrees()),
//                popup)
//                .addOnCompleteListener(result ->{
//                    proxy.close();
//                    analysisRequested = false;
//                });
    }

    private static byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }


    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }

    private void loopJHTTPCall(String encodedString) {

        RequestParams params = new RequestParams();
        params.add("image_data", encodedString);
        params.put("image_data", encodedString);

        RestClient.post("https://us-central1-stockscan-2d0f2.cloudfunctions.net/parse-image",
                        params, new JsonHttpResponseHandler(){
            private final String TAG = "JsonHttpResponseHandler";

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if(!response.has("error")){
                        for (Iterator<String> it = response.keys(); it.hasNext(); ) {
                            String key = it.next();
                            Log.d(TAG, "{\""+key+"\" : \""+response.get(key)+"\"}");
                        }
                    }else{
                        Log.e(TAG, response.getString("error"));
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

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(errorResponse!= null)
                    Log.e(TAG, "error: "+errorResponse.toString());
            }
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
