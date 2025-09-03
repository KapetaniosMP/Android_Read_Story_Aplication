package com.example.phone_excersize_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

public class StoryActivity extends AppCompatActivity {

    private BookSpeaker bookSpeaker;
    private ImagesLoader imagesLoader;
    private Handler handler;
    private int currentBookPointer, butchNumber;
    private boolean wasSpeaking;

    private void colorCurrentText(){
        Log.d("A","A");
        TextView butchText = findViewById(R.id.storyTextView);   // the text displayed on the activity
        Book currentBook = MainActivity.bookLibrary.getBook(currentBookPointer);
        String startStr = "",currentStr="",endStr="";
        int stop = (currentBook.getSentences().length - butchNumber * 10 > 10)? 10 :
                currentBook.getSentences().length - butchNumber * 10;
        for(int i = 0; i<stop; i++){

            if(i + butchNumber * 10 < currentBook.getSentenceCounter()-1){
                startStr += currentBook.getSentences()[i + butchNumber * 10];
            }
            else if(i + butchNumber * 10 == currentBook.getSentenceCounter()-1){
                currentStr += currentBook.getSentences()[i + butchNumber * 10];
            }
            else{
                endStr += currentBook.getSentences()[i + butchNumber * 10];
            }
        }

        String coloredText = "<font color='#979998'>"+ startStr +"</font> " +
                "<font color='#FF0000'>"+ currentStr +"</font> " +
                "<font color='#000000'>"+ endStr +"</font>";
        butchText.setText(Html.fromHtml(coloredText, Html.FROM_HTML_MODE_LEGACY));
        butchText.postInvalidate();
    }
    private void create_main_Ui(){
        ConstraintLayout story = findViewById(R.id.story);

        // change header title
        FrameLayout header = findViewById(R.id.storyHeader);
        TextView mainTitle = header.findViewById(R.id.headerTitle);
        mainTitle.setText(getString(R.string.story));

        // add sound button functionality
        ImageView playIcon = story.findViewById(R.id.storyPlayIcon);   // create onclick listener for playIcon
        playIcon.setOnClickListener(
                v -> {
                    bookSpeaker.startSpeaking(currentBookPointer);
                    colorCurrentText();
                }
        );
        ImageView replayIcon = story.findViewById(R.id.storyReplayIcon);   // create onclick listener for replayIcon
        replayIcon.setOnClickListener(
                v -> {
                    bookSpeaker.restartSpeaking(currentBookPointer);
                    colorCurrentText();
                }
        );
        ImageView stopIcon = story.findViewById(R.id.storyStopIcon);   // create onclick listener for stopIcon
        stopIcon.setOnClickListener(
                v -> {
                    bookSpeaker.stopSpeaking(currentBookPointer);
                    colorCurrentText();
                }
        );

        // add correct image view
        ImageView imgView = findViewById(R.id.storyImageView);
        imagesLoader.loadImageToImageView(imgView,
                MainActivity.bookLibrary.getBook(currentBookPointer).getImg());

        // add page button functionality
        TextView pageNumberView = findViewById(R.id.storyPageNumber);   // the text displaying the butchNumber
        ImageView leftIcon = story.findViewById(R.id.storyLeftIcon);   // create onclick listener for leftIcon
        leftIcon.setOnClickListener(
                v -> {
                    Book currentBook = MainActivity.bookLibrary.getBook(currentBookPointer);
                    butchNumber --;   // decrease the butchNumber by 1
                    butchNumber = ( butchNumber<0 )? currentBook.getSentences().length/10 : butchNumber; // create a circle movement
                    colorCurrentText();
                    pageNumberView.setText(Integer.toString(butchNumber));
                }
        );
        ImageView rightIcon = story.findViewById(R.id.storyRightIcon);   // create onclick listener for rightIcon
        rightIcon.setOnClickListener(
                v -> {
                    Book currentBook = MainActivity.bookLibrary.getBook(currentBookPointer);
                    butchNumber ++;   // increase the butchNumber by 1
                    butchNumber = ( butchNumber > currentBook.getSentences().length/10 )? 0 : butchNumber; // create a circle movement
                    colorCurrentText();
                    pageNumberView.setText(Integer.toString(butchNumber));
                }
        );

        // add the sidebar
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View sidebar = layoutInflater.inflate(R.layout.sidebar, story, false);
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
            // stop speaking
            bookSpeaker.stopSpeaking();
            // change activity
            Intent intent = new Intent(StoryActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        TextView sidebarOption1 = sidebar.findViewById(R.id.sidebarOption1);
        sidebarOption1.setOnClickListener(v -> {
            sidebar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        });
        TextView sidebarOption2 = sidebar.findViewById(R.id.sidebarOption2);
        sidebarOption2.setOnClickListener(v -> {
            // stop speaking
            bookSpeaker.stopSpeaking();
            // change activity
            Intent intent = new Intent(StoryActivity.this, StatisticsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        ImageView openListIcon1 = header.findViewById(R.id.headerOpenListIcon);
        openListIcon1.setOnClickListener(v -> {
            sidebar.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        });

        story.addView(sidebar); // put the sidebar in the main layout
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
        setContentView(R.layout.story);
        //lock horizontal view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.story), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        handler = new Handler(Looper.getMainLooper());
        imagesLoader = new ImagesLoader(this);
        bookSpeaker = new BookSpeaker(this, MainActivity.bookLibrary, ()->{
            handler.post(this::colorCurrentText);
            return 0;
        });
        currentBookPointer = MainActivity.bookLibrary.getPointer();
        wasSpeaking = false;

        changeTopBarAppearance();
        create_main_Ui();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // create butchText
        currentBookPointer = MainActivity.bookLibrary.getPointer();
        // add correct image view
        ImageView imgView = findViewById(R.id.storyImageView);
        imagesLoader.loadImageToImageView(imgView,
                MainActivity.bookLibrary.getBook(currentBookPointer).getImg());
        //color that text
        colorCurrentText();
        if (wasSpeaking){
            //if the speaker was playing before the activity was paused
            bookSpeaker.startSpeaking(currentBookPointer);
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
}
