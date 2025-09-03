package com.example.phone_excersize_2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.function.Supplier;

public class BookSpeaker {
    private BookLibrary bookLibrary;
    private Book currentBook;
    private TracableSpeaker speaker;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    BookSpeaker(Context context, BookLibrary bookLibrary, Supplier<Integer> onDone){
        this.speaker = new TracableSpeaker(context,()->{
            // speak all following sentences after the current sentence ends
            String[] sentences = currentBook.getSentences();
            int sentenceCounter = currentBook.getSentenceCounter();

            if (sentences != null && sentenceCounter <sentences.length){
                speaker.speak(sentences[sentenceCounter]);
                currentBook.setSentenceCounter(sentenceCounter + 1);
            }

            // do any other actions
            return onDone.get();
        });
        this.bookLibrary = bookLibrary;
        this.sharedPreferences = context.
                getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        this.sharedPreferencesEditor = sharedPreferences.edit();

    }

    private void start(){
        // speak the current sentence, after where you possibly stopped
        speaker.speak(currentBook.getCurrentSentence() );
        // update the number-the-story-has-been-played counter
        int playNumber = sharedPreferences.getInt("playNumber"+ currentBook.getNumber(), 0);
        sharedPreferencesEditor.putInt("playNumber"+ currentBook.getNumber(),playNumber + 1);
        sharedPreferencesEditor.apply();
    }
    private void restart(){
        // assign the first sentence to the currentTexts
        currentBook.setCurrentSentence( currentBook.getSentences()[0] );
        // assign the number of the next sentence to the sentence counter
        currentBook.setSentenceCounter(1);
        // then speak the first sentence
        speaker.speak( currentBook.getCurrentSentence() );
        // update the number-the-story-has-been-played counter
        int playNumber = sharedPreferences.getInt("playNumber"+ currentBook.getNumber(), 0);
        sharedPreferencesEditor.putInt("playNumber"+ currentBook.getNumber(),playNumber + 1);
        sharedPreferencesEditor.apply();
    }
    private void stop() {
        // save the text at the word it stopped
        currentBook.setCurrentSentence(speaker.shutUp());
        // make changes in shared preferences
        sharedPreferencesEditor.putString("currentText" + currentBook.getNumber(), currentBook.getCurrentSentence());
        sharedPreferencesEditor.putInt("sentenceNumber" + currentBook.getNumber(), currentBook.getSentenceCounter());
        sharedPreferencesEditor.apply();
    }

    public boolean isSpeaking(){
        return speaker.isSpeaking();
    }
    public void stopSpeaking(){
        if(speaker.isSpeaking()){
            stop();
        }
    }
    public void stopSpeaking(int bookPointer){
        if( bookPointer == currentBook.getNumber()){
            // if the stopIcon belongs to the book currently played
            stop();
        }
    }
    public void startSpeaking(int bookPointer){
        stopSpeaking();
        Log.d("Books",""+bookLibrary.getSize());
        //ensure the story being played is this story
        currentBook = bookLibrary.getBook(bookPointer);
        start();}
    public void restartSpeaking(int bookPointer){
        stopSpeaking();
        //ensure the story being played is this story
        currentBook = bookLibrary.getBook(bookPointer);
        restart();}

}
