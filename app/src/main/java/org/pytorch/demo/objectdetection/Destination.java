package org.pytorch.demo.objectdetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Destination extends AppCompatActivity implements VoiceControl.TextToSpeechListener {
    private VoiceControl texttospeech;
    EditText editText = null;
    ImageButton imageButton;

    SpeechRecognizer speechRecognizer;
//    SwipeListener swipeListener;

    int count = 0;
    private float x1,x2,y1,y2;
    private static int MIN_DISTANCE =150;
    //        swipeListener = new SwipeListener();
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_destination);
//TTS
        texttospeech = new VoiceControl(this, this);
        imageButton = findViewById(R.id.speech);
        editText = findViewById(R.id.destination);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
        }


//      Speech To Text
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(count ==0){
                    //Start Listening
                    speechRecognizer.startListening(speechRecognizerIntent);
                    count =1;
                }
                else {
                    //Stop Listening
                    speechRecognizer.stopListening();
                    count =0;
                }
            }
        });
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float v) {}

            @Override
            public void onBufferReceived(byte[] bytes) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int i) {}

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> dest = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(dest.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {}

            @Override
            public void onEvent(int i, Bundle bundle) {}
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Grant", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
            }
        }

    }

    @Override
    public void onTextToSpeechReady() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                texttospeech.speak("Vivien is asking about your desired destination, where would you like to go?");
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        }, 5975);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String dest = editText.getText().toString();
                final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                texttospeech.speak("Your destination is");
                texttospeech.speak(dest);
                texttospeech.speak("Do you confirm your destination");
            }
        }, 11000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        }, 16500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String text = editText.getText().toString();
                Log.i("confirm", "your text is " + text);

                if (text.equals("yes")){
                    Intent intent = new Intent(Destination.this, ObjectCollisionActivity.class);
                    startActivity(intent);
                }
                else {
                    onTextToSpeechReady();
                }
            }
        }, 20000);
    }

}