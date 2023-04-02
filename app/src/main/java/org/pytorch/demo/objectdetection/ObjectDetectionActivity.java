package org.pytorch.demo.objectdetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ObjectDetectionActivity extends AbstractCameraXActivity<ObjectDetectionActivity.AnalysisResult>{
    private Module mModule = null;
    private ResultView mResultView;
    private VoiceControl texttospeech;

    static class AnalysisResult {
        private final ArrayList<Result> mResults;

        public AnalysisResult(ArrayList<Result> results) {
            mResults = results;
        }
    }

    @Override
    protected int getContentViewLayoutId() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ObjectDetectionActivity.this, Destination.class);
                startActivity(intent);
            }
        }, 10000);
        return R.layout.activity_object_detection;
    }

    @Override
    protected TextureView getCameraPreviewTextureView() {
        mResultView = findViewById(R.id.resultView);
        return ((ViewStub) findViewById(R.id.object_detection_texture_view_stub))
                .inflate()
                .findViewById(R.id.object_detection_texture_view);
    }

    @Override
    protected void applyToUiAnalyzeImageResult(AnalysisResult result) {
        mResultView.setResults(result.mResults);
        mResultView.invalidate();

        Exporter.exportObjectToJson(result.mResults, "objectlist.json");

        try {
            // Get the directory path where the file is stored
            File directory = Environment.getExternalStorageDirectory();
            String filePath = directory.getPath() + "/Documents/Vivien/fileJson/objectlist.json";

            // Load the JSON file into a String
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                String json = new String(buffer, "UTF-8");

                // Parse the JSON file into a JSON array
                JSONArray jsonArray = new JSONArray(json);

                // Set the classIndex to count
                int classIndex0 = 0;
                int classIndex13 = 13;
                int classIndex56 = 56;
                int classIndex60 = 60;
                int classIndex62 = 62;
                int classIndex72 = 72;

                // Initialize the count variable
                int count0 = 0;
                int count1 = 0;
                int count2 = 0;
                int count3 = 0;
                int count4 = 0;
                int count5 = 0;
                int count6 = 0;

                // Iterate over the objects in the JSON array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Check if the classIndex of the current object is equal to the classIndex to count
                    if (jsonObject.getInt("classIndex") == classIndex0) {
                        count0++;
                    } else if (jsonObject.getInt("classIndex") == classIndex13) {
                        count1++;
                    }else if (jsonObject.getInt("classIndex") == classIndex56) {
                        count2++;
                    }else if (jsonObject.getInt("classIndex") == classIndex60) {
                        count3++;
                    }else if (jsonObject.getInt("classIndex") == classIndex62) {
                        count4++;
                    }else if (jsonObject.getInt("classIndex") == classIndex72) {
                        count5++;
                    }else {
                        count6++;
                    }
                }

                // Print the count
                Log.i("JSON Count", "Count of person " + ": " + count0);
                Log.i("JSON Count", "Count of bench " + ": " + count1);
                Log.i("JSON Count", "Count of chair " + ": " + count2);
                Log.i("JSON Count", "Count of table " + ": " + count3);
                Log.i("JSON Count", "Count of window " + ": " + count4);
                Log.i("JSON Count", "Count of door "+ ": " + count5);
                Log.i("JSON Count", "Count of object " + ": " + count6);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap imgToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    @Override
    @WorkerThread
    @Nullable
    protected AnalysisResult analyzeImage(ImageProxy image, int rotationDegrees) {
        try {
            if (mModule == null) {
                mModule = LiteModuleLoader.load(MainActivity.assetFilePath(getApplicationContext(), "yolov5s.torchscript.ptl"));
            }
        } catch (IOException e) {
            Log.e("Object Detection", "Error reading assets", e);
            return null;
        }
        Bitmap bitmap = imgToBitmap(image.getImage());
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);

        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);
        IValue[] outputTuple = mModule.forward(IValue.from(inputTensor)).toTuple();
        final Tensor outputTensor = outputTuple[0].toTensor();
        final float[] outputs = outputTensor.getDataAsFloatArray();

        float imgScaleX = (float)bitmap.getWidth() / PrePostProcessor.mInputWidth;
        float imgScaleY = (float)bitmap.getHeight() / PrePostProcessor.mInputHeight;
        float ivScaleX = (float)mResultView.getWidth() / bitmap.getWidth();
        float ivScaleY = (float)mResultView.getHeight() / bitmap.getHeight();

        final ArrayList<Result> results = PrePostProcessor.outputsToNMSPredictions(outputs, imgScaleX, imgScaleY, ivScaleX, ivScaleY, 0, 0);
        return new AnalysisResult(results);
    }
//    @Override
//    public void onTextToSpeechReady() {
//        texttospeech = new VoiceControl(this, this);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                texttospeech.speak("Your current position has been successfully detected");
//            }
//        }, 5000);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(ObjectDetectionActivity.this, Destination.class);
//                startActivity(intent);
//            }
//        }, 10000);
//    }
}
