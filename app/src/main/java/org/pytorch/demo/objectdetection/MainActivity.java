package org.pytorch.demo.objectdetection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int mImageIndex = 0;

    private static String[] checkpermission = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION };

    //    private ImageView mImageView;
//    private ResultView mResultView;
//    private Button mButtonDetect;
//    private ProgressBar mProgressBar;
    private Bitmap mBitmap = null;
    private Module mModule = null;
//    private float mImgScaleX, mImgScaleY, mIvScaleX, mIvScaleY, mStartX, mStartY;
//    private VoiceControl texttospeech;
//    private String textmainmenu = "Hello, my name is Vivian. I am your guide";
//    private String textfindpositioning = "Vivian would like to check your current position for a moment.";

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, checkpermission, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        setContentView(R.layout.activity_main);

//        mImageView = findViewById(R.id.imageView);
//        mImageView.setImageBitmap(mBitmap);
//        mResultView = findViewById(R.id.resultView);
//        mResultView.setVisibility(View.INVISIBLE);

        //TTS
//        texttospeech = new VoiceControl(this, this);

        //Button RSSI
        final Button buttonTest = findViewById(R.id.testButton);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity_scanwifi.class);
                startActivity(intent);
            }
        });

        //Button Select
        final Button buttonSelect = findViewById(R.id.selectButton);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Init.class);
                startActivity(intent);
            }
        });

        //Button Live
        final Button buttonLive = findViewById(R.id.liveButton);
        buttonLive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });

//        mButtonDetect = findViewById(R.id.detectButton);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        mButtonDetect.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mButtonDetect.setEnabled(false);
//                mProgressBar.setVisibility(ProgressBar.VISIBLE);
//                mButtonDetect.setText(getString(R.string.run_model));
//
//                mImgScaleX = (float)mBitmap.getWidth() / PrePostProcessor.mInputWidth;
//                mImgScaleY = (float)mBitmap.getHeight() / PrePostProcessor.mInputHeight;
//
//                mIvScaleX = (mBitmap.getWidth() > mBitmap.getHeight() ? (float)mImageView.getWidth() / mBitmap.getWidth() : (float)mImageView.getHeight() / mBitmap.getHeight());
//                mIvScaleY  = (mBitmap.getHeight() > mBitmap.getWidth() ? (float)mImageView.getHeight() / mBitmap.getHeight() : (float)mImageView.getWidth() / mBitmap.getWidth());
//
//                mStartX = (mImageView.getWidth() - mIvScaleX * mBitmap.getWidth())/2;
//                mStartY = (mImageView.getHeight() -  mIvScaleY * mBitmap.getHeight())/2;
//
//                Thread thread = new Thread(MainActivity.this);
//                thread.start();
//            }
//        });

        try {
            mModule = LiteModuleLoader.load(MainActivity.assetFilePath(getApplicationContext(), "yolov5s.torchscript.ptl"));
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("classes.txt")));
            String line;
            List<String> classes = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                classes.add(line);
            }
            PrePostProcessor.mClasses = new String[classes.size()];
            classes.toArray(PrePostProcessor.mClasses);
        } catch (IOException e) {
            Log.e("Object Detection", "Error reading assets", e);
            finish();
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_CANCELED) {
//            switch (requestCode) {
//                case 0:
//                    if (resultCode == RESULT_OK && data != null) {
//                        mBitmap = (Bitmap) data.getExtras().get("data");
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(90.0f);
//                        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
//                        mImageView.setImageBitmap(mBitmap);
//                    }
//                    break;
//                case 1:
//                    if (resultCode == RESULT_OK && data != null) {
//                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                        if (selectedImage != null) {
//                            Cursor cursor = getContentResolver().query(selectedImage,
//                                    filePathColumn, null, null, null);
//                            if (cursor != null) {
//                                cursor.moveToFirst();
//                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                                String picturePath = cursor.getString(columnIndex);
//                                mBitmap = BitmapFactory.decodeFile(picturePath);
//                                Matrix matrix = new Matrix();
//                                matrix.postRotate(90.0f);
//                                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
//                                mImageView.setImageBitmap(mBitmap);
//                                cursor.close();
//                            }
//                        }
//                    }
//                    break;
//            }
//        }
//    }

//    @Override
//    public void run() {
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(mBitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);
//        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);
//        IValue[] outputTuple = mModule.forward(IValue.from(inputTensor)).toTuple();
//        final Tensor outputTensor = outputTuple[0].toTensor();
//        final float[] outputs = outputTensor.getDataAsFloatArray();
//        final ArrayList<Result> results =  PrePostProcessor.outputsToNMSPredictions(outputs, mImgScaleX, mImgScaleY, mIvScaleX, mIvScaleY, mStartX, mStartY);

//        runOnUiThread(() -> {
//            mButtonDetect.setEnabled(true);
//            mButtonDetect.setText(getString(R.string.detect));
//            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//            mResultView.setResults(results);
//            mResultView.invalidate();
//            mResultView.setVisibility(View.VISIBLE);
//        });
//    }


//    @Override
//    public void onTextToSpeechReady() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                texttospeech.speak(textmainmenu);
//            }
//        }, 3000);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                texttospeech.speak(textfindpositioning);
//            }
//        }, 8000);
//    }

//    public void CompareJson() {
//        try {
//            InputStream inputStream = getAssets().open("myfile.json");
//            CompareJson comparator = new CompareJson(inputStream);
//            boolean nameMatched = comparator.compare("name", "John Smith");
//            boolean ageMatched = comparator.compare("age", 30);
//            if (nameMatched && ageMatched) {
//                // Values match
//            } else {
//                // Values do not match
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//    }

}
