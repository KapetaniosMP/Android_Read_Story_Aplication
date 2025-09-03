package com.example.phone_excersize_2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.function.Supplier;

public class BookLibrary {

    private ArrayList<Book> books;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private SharedPreferences sharedPreferences;
    private int size, bookPointer;

    BookLibrary(Context context, Supplier<Integer> suppl){
        this.books = new ArrayList<>();
        this.bookPointer = 0;
        this.sharedPreferences =
                context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        this.database = FirebaseDatabase.getInstance();
        this.reference = database.getReference("books");

        // fill the books arrayList
        this.reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        for (DataSnapshot bookContents : snapshot.getChildren()){
                            Integer number = bookContents.child("number").getValue(Integer.class);
                            String title = bookContents.child("title").getValue(String.class);
                            String writer = bookContents.child("writer").getValue(String.class);
                            String year = bookContents.child("year").getValue(String.class);
                            String img = bookContents.child("image").getValue(String.class);
                            String text = bookContents.child("text").getValue(String.class);
                            Book book = null;

                            // divide the text to sentences and fill the book with them
                            if ( number != null && title != null && text != null && img != null){
                                //find all points in the text where an end of a sentence is reached and divide it in sentences
                                int textEnd = text.length(), numOfSentences = 0, sentenceStart = 0;
                                //byte[] textBytes = text.getBytes();

                                boolean mayStop = false, endReached = false;

                                for(int i = 0; i<2; i++){

                                    for(int j = 0; j <textEnd; j++){

                                        if ( text.charAt(j) == 0x21 || text.charAt(j) == 0x2e || text.charAt(j) == 0x3f){
                                            mayStop = true;
                                        }

                                        else if( mayStop && (( text.charAt(j) > 0x40 && text.charAt(j) < 0x5b) ||
                                                ( text.charAt(j) > 0x60 && text.charAt(j) < 0x7b)) ){
                                            if (endReached){
                                                // after the number of sentences has been found, fill the book with each sentence
                                                book.getSentences()[numOfSentences] = text.substring(sentenceStart,j);
                                                sentenceStart = j;
                                            }
                                            numOfSentences +=1;
                                            mayStop = false;
                                        }
                                    }

                                    if (!endReached) {
                                        // initialize the book after the number of the sentences in the text is found
                                        book = new Book(number, title, writer, year, new String[numOfSentences], img);
                                        numOfSentences = 0;
                                        mayStop = false;
                                        endReached = true;
                                    }
                                }

                                book.setCurrentSentence( book.getSentences()[0] );
                                // the default value for current sentence is the 0th sentence
                                book.setSentenceCounter(
                                        // the value for the sentence counter is the sentence after the current sentence,
                                        // so the default value is 1
                                        sharedPreferences.getInt("sentenceNumber"+ number, 1));
                                if(book.getSentenceCounter() > 0){
                                    book.setCurrentSentence( book.getSentences()[ book.getSentenceCounter() - 1 ] );
                                }
                                books.add(book);
                            }
                        }
                        size = books.size();
                        suppl.get();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {    }
                }
        );
    }

    Book getBook(int bookPointer){
        return books.get(bookPointer);
    }
    int getSize(){
        return size;
    }
    int getPointer(){
        return bookPointer;
    }
    void setBookPointer(int bookPointer){
        this.bookPointer = bookPointer;
    }
}
