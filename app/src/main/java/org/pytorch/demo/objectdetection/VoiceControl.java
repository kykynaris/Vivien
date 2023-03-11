package org.pytorch.demo.objectdetection;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class VoiceControl {
    private TextToSpeech tts;
    private boolean isReady = false;

    public VoiceControl(Context context, final TextToSpeechListener listener) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.ENGLISH);
                    isReady = true;
                    if (listener != null) {
                        listener.onTextToSpeechReady();
                    }
                }
            }
        });
    }

    public boolean isReady() {
        return isReady;
    }

    public void speak(String text) {
        if (isReady) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    public void stop() {
        if (isReady) {
            tts.stop();
        }
    }

    public void shutdown() {
        if (isReady) {
            tts.stop();
            tts.shutdown();
        }
    }

    public interface TextToSpeechListener {
        void onTextToSpeechReady();
    }
}
