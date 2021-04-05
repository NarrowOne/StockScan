package com.example.stockscan.Analyzers;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

import com.example.stockscan.Models.ScanPopup;
import com.example.stockscan.REST.OCRRest;
import com.example.stockscan.REST.RestClient;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;

public class ImageProcessorIMPL implements ImageProcessor {
    private static final String TAG = "ImageProcessorBase";
    private boolean isShutdown;
    private boolean analysisRequested = false;

    public ImageProcessorIMPL(){
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
        assert image != null;

        byte[] imgBytes = null;
        imgBytes = NV21toJPEG(
                YUV_420_888toNV21(image),
                image.getWidth(), image.getHeight());

        String encodedString = Base64.getEncoder().encodeToString(imgBytes);

        RestClient ocrRest = new OCRRest(popup, "POST");
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "ocr");
        data.put("image_data", encodedString);
        ocrRest.execute(data);

        analysisRequested = false;
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


}
