package com.example.phone_excersize_2;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;
import java.util.function.Supplier;

public class TracableSpeaker {
    private final TextToSpeech textToSpeech;
    private String qurrentString, qurrentSubstring;
    private Bundle params = new Bundle();
    private TextToSpeech.OnInitListener initListener =
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status==TextToSpeech.SUCCESS){
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            };
    public TracableSpeaker(Context context, Supplier<Integer> onDone){
        textToSpeech = new TextToSpeech(context,initListener);
        qurrentString = "";
        qurrentSubstring = "";
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "uniqueUtteranceId");
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {}
            @Override
            public void onDone(String utteranceId) {
                onDone.get();
            }
            @Override
            public void onError(String utteranceId) {}
            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                qurrentSubstring = qurrentString.substring(start);
            }
        });
    }
    public void speak(String message){
        qurrentString = message;
        textToSpeech.speak(message,TextToSpeech.QUEUE_FLUSH, params, "uniqueUtteranceId");
    }
    public boolean isSpeaking(){
        return textToSpeech.isSpeaking();
    }
    public String shutUp(){
        if(textToSpeech.isSpeaking()) textToSpeech.stop();
        return qurrentSubstring;
    }


}
