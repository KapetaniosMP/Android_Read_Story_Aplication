package com.example.phone_excersize_2;

public class Book {
    private String title, writer, year, img, currentSentence;
    private String[] sentences;
    private int number, sentenceCounter;
    Book(int number, String title, String writer, String year, String[] sentences, String img){
        this.number = number;
        this.title = title;
        this.writer = writer;
        this.year = year;
        this.sentences = sentences;
        this.img = img;
    }

    int getNumber(){
        return number;
    }
    String getTitle(){
        return title;
    }
    String getWriter(){
        return writer;
    }
    String getYear(){
        return year;
    }
    String[] getSentences(){
        return sentences;
    }
    String getImg(){
        return img;
    }
    String getCurrentSentence(){
        return currentSentence;
    }
    int getSentenceCounter(){
        return sentenceCounter;
    }

    void setCurrentSentence(String currentSentence){
        this.currentSentence = currentSentence;
    }
    void setSentenceCounter(int sentenceCounter){
        this.sentenceCounter = sentenceCounter;
    }
}
