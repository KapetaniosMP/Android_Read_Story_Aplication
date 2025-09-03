package com.example.phone_excersize_2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    static BookLibrary bookLibrary;
    private BookSpeaker bookSpeaker;
    private ImagesLoader imagesLoader;
    private Handler handler;
    private ArrayList<SeekBar> seekBars;
    private ArrayList<ImageView> playButtons;
    private int bookPointer;
    private boolean wasSpeaking;

    private void create_main_Ui(){
        ConstraintLayout main = findViewById(R.id.main);
        LinearLayout mainLayout = findViewById(R.id.parentLayout);

        // change header title
        FrameLayout header = findViewById(R.id.activityMainHeader);
        TextView mainTitle = header.findViewById(R.id.headerTitle);
        mainTitle.setText(getString(R.string.books));

        // add books
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for (int i = 0; i< bookLibrary.getSize(); i++ ) {

            View bookLayout = layoutInflater.inflate(R.layout.book, mainLayout, false);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bookLayout.getLayoutParams();
            int marginInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10,
                    getResources().getDisplayMetrics()
            );
            params.setMargins(0, marginInPixels, 0, 0); // Top margin for vertical spacing
            bookLayout.setLayoutParams(params);

            SeekBar seekBar = bookLayout.findViewById(R.id.bookProgressBar);   // disable seekBar
            seekBar.setEnabled(false);
            seekBars.add(seekBar);

            ImageView imgView = bookLayout.findViewById(R.id.bookFront);   // change image
            imagesLoader.loadImageToImageView(imgView,
                    bookLibrary.getBook(i).getImg());

            TextView textView = bookLayout.findViewById(R.id.bookTitle);   // change the title of the book
            textView.setText(
                    bookLibrary.getBook(i).getTitle() + " (" +
                    bookLibrary.getBook(i).getWriter() +", " +
                    bookLibrary.getBook(i).getYear()+")"
            );

            int textNumber = i;
            ImageView textIcon = bookLayout.findViewById(R.id.bookOpenIcon);   // create onclick listener for stopIcon
            textIcon.setOnClickListener(
                    v -> {
                        bookSpeaker.stopSpeaking();
                        // save the number of the book in the library
                        bookLibrary.setBookPointer(textNumber);
                        // change activity
                        Intent intent = new Intent(MainActivity.this, StoryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }
            );
            ImageView playIcon = bookLayout.findViewById(R.id.bookPlayIcon);   // create onclick listener for playIcon
            playButtons.add(playIcon);
            playIcon.setOnClickListener(
                    v -> {
                        bookPointer = textNumber;
                        bookSpeaker.startSpeaking(textNumber);
                        for (ImageView playButton: playButtons){
                            playButton.setImageResource(R.drawable.play_icon);
                        }
                        playIcon.setImageResource(R.drawable.play_icon_negative);
                        seekBar.setMax(
                                bookLibrary.getBook(textNumber).getSentences().length);
                        seekBar.setProgress(
                                bookLibrary.getBook(textNumber).getSentenceCounter());
                    }
            );
            ImageView restartIcon = bookLayout.findViewById(R.id.bookRestartIcon);   // create onclick listener for restartIcon
            restartIcon.setOnClickListener(
                    v -> {
                        bookPointer = textNumber;
                        bookSpeaker.restartSpeaking(textNumber);
                        for (ImageView playButton: playButtons){
                            playButton.setImageResource(R.drawable.play_icon);
                        }
                        playIcon.setImageResource(R.drawable.play_icon_negative);
                        seekBar.setMax(
                                bookLibrary.getBook(textNumber).getSentences().length);
                        seekBar.setProgress(
                                bookLibrary.getBook(textNumber).getSentenceCounter());
                    }
            );
            ImageView stopIcon = bookLayout.findViewById(R.id.bookStopIcon);   // create onclick listener for stopIcon
            stopIcon.setOnClickListener(
                    v -> {
                        bookPointer = textNumber;
                        bookSpeaker.stopSpeaking(textNumber);
                        playIcon.setImageResource(R.drawable.play_icon);
                    }
            );
            seekBar.setMax(
                    bookLibrary.getBook(textNumber).getSentences().length);
            seekBar.setProgress(
                    bookLibrary.getBook(textNumber).getSentenceCounter());
            mainLayout.addView(bookLayout); // put the book in the main layout
        }

        // add the sidebar
        View sidebar = layoutInflater.inflate(R.layout.sidebar, main, false);
        sidebar.setVisibility(View.GONE);
        View view = sidebar.findViewById(R.id.view);
        view.setOnClickListener(v -> {
            sidebar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        });
        view.setVisibility(View.GONE);
        ImageView openListIcon0 = sidebar.findViewById(R.id.sidebarOpenListIcon);
        openListIcon0.setOnClickListener(v -> {
            sidebar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        });
        TextView sidebarOption0 = sidebar.findViewById(R.id.sidebarOption0);
        sidebarOption0.setOnClickListener(v -> {
            sidebar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        });
        TextView sidebarOption1 = sidebar.findViewById(R.id.sidebarOption1);
        sidebarOption1.setOnClickListener(v -> {
            // stop speaking
            bookSpeaker.stopSpeaking();
            // change activity
            Intent intent = new Intent(MainActivity.this, StoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        TextView sidebarOption2 = sidebar.findViewById(R.id.sidebarOption2);
        sidebarOption2.setOnClickListener(v -> {
            // stop speaking
            bookSpeaker.stopSpeaking();
            // change activity
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        ImageView openListIcon1 = header.findViewById(R.id.headerOpenListIcon);
        openListIcon1.setOnClickListener(v -> {
            sidebar.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        });
        main.addView(sidebar); // put the sidebar in the main layout
    }

    private void changeTopBarAppearance() {
        View decoration_View = getWindow().getDecorView();
        WindowInsetsController window_insets_Controller = decoration_View.getWindowInsetsController();
        try{
            window_insets_Controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        }catch (Exception ex){
            //Log.d("An exception occured.", "This is the following: " + ex);    }}
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //lock horizontal view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handler = new Handler(Looper.getMainLooper());
        seekBars = new ArrayList<>();
        playButtons = new ArrayList<>();
        bookPointer = 0;
        wasSpeaking = false;

        changeTopBarAppearance();

        bookLibrary = new BookLibrary(this,()->{
            create_main_Ui();
            return 0;
        });
        bookSpeaker = new BookSpeaker(this,bookLibrary,()-> {
            handler.post(()->{
                seekBars.get(bookPointer).setProgress(
                        bookLibrary.getBook(bookPointer).getSentenceCounter()
                );
            });
            return 0;
        });
        imagesLoader = new ImagesLoader(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i<bookLibrary.getSize(); i++){
            seekBars.get(i).setMax(
                    bookLibrary.getBook(i).getSentences().length);
            seekBars.get(i).setProgress(
                    bookLibrary.getBook(i).getSentenceCounter());
            playButtons.get(i).setImageResource(R.drawable.play_icon);
            i++;
        }
        if (wasSpeaking){
            //if the speaker was playing before the activity was paused
            bookSpeaker.startSpeaking(bookPointer);
            wasSpeaking = false;
        }
    }

    @Override
    protected void onPause() {
        wasSpeaking = bookSpeaker.isSpeaking();
        //close the speaker from mainActivity before the activity is paused
        bookSpeaker.stopSpeaking();
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        wasSpeaking = bookSpeaker.isSpeaking();
        //close the speaker from mainActivity before the activity is paused
        bookSpeaker.stopSpeaking();
        super .onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //close the speaker from mainActivity before the activity is closed
        bookSpeaker.stopSpeaking();
        super.onDestroy();
    }
}