package com.example.phone_excersize_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatisticsActivity extends AppCompatActivity {


    private SharedPreferences sharedPreferences;

    private void create_main_Ui(){
        ConstraintLayout statistics = findViewById(R.id.statistics);

        // change header title
        FrameLayout header = findViewById(R.id.statisticsHeader);
        TextView mainTitle = header.findViewById(R.id.headerTitle);
        mainTitle.setText(getString(R.string.statistics));

        // put the statistics in the TextView
        TextView statisticsText = statistics.findViewById(R.id.statisticsTextView);
        int totalNumberPlayed = 0;
        int[] best = new int[3];
        String [] bestNames = new String[3];

        for (int i =0; i<MainActivity.bookLibrary.getSize(); i++){
            int value = sharedPreferences.getInt("playNumber"+ i, 0);
            totalNumberPlayed += value;

            int hangingValue = value;
            String hangingName = MainActivity.bookLibrary.getBook(i).getTitle();
            for (int j=0 ;j<best.length; j++){
                if ( best[j] < hangingValue){
                    int oldValue = best[j];
                    String oldName = bestNames[j];
                    best[j] = hangingValue;
                    bestNames[j] = hangingName;
                    hangingValue=  oldValue;
                    hangingName = oldName;
                }
            }
        }
        Log.d("D", bestNames[0] + " " +bestNames[1] + " " +bestNames[2]);
        String text ="<H5><strong>" + getString(R.string.numTimes) + "</strong></H5><H1>" + totalNumberPlayed + "</H1><br>" +
                "<H5><strong>" + getString(R.string.first) + " " + getString(R.string.favourite) + "</strong></H5><H3>" + ((bestNames[0] == null)? "-" : bestNames[0]) + "</H3><br>" +
                "<H5><strong>" + getString(R.string.second) + " " + getString(R.string.favourite) + "</strong></H5><H3>" + ((bestNames[1] == null)? "-" : bestNames[1]) + "</H3><br>" +
                "<H5><strong>" + getString(R.string.third) + " " + getString(R.string.favourite) + "</strong></H5><H3>" + ((bestNames[2] == null)? "-" : bestNames[2])+"</H3>";
        statisticsText.setText(Html.fromHtml( text, Html.FROM_HTML_MODE_LEGACY));

        // refresh icon
        ImageView refreshIcon = statistics.findViewById(R.id.statisticsRefreshIcon);
        refreshIcon.setOnClickListener( v->{
            TextView statisticsText1 = statistics.findViewById(R.id.statisticsTextView);
            int totalNumberPlayed1 = 0;
            int[] best1 = new int[3];
            String [] bestNames1 = new String[3];

            for (int i =0; i<MainActivity.bookLibrary.getSize(); i++){
                int value = sharedPreferences.getInt("playNumber"+ i, 0);
                totalNumberPlayed1 += value;

                int hangingValue = value;
                String hangingName = MainActivity.bookLibrary.getBook(i).getTitle();
                for (int j=0 ;j<best1.length; j++){
                    if ( best1[j] < hangingValue){
                        int oldValue = best1[j];
                        String oldName = bestNames1[j];
                        best1[j] = hangingValue;
                        bestNames1[j] = hangingName;
                        hangingValue=  oldValue;
                        hangingName = oldName;
                    }
                }
            }
            String text1 ="<H5><strong>" + getString(R.string.numTimes) + "</strong></H5><H1>" + totalNumberPlayed1 + "</H1><br>" +
                    "<H5><strong>" + getString(R.string.first) + " " + getString(R.string.favourite) + "</strong></H5><H3>" + ((bestNames1[0] == null)? "-" : bestNames1[0]) + "</H3><br>" +
                    "<H5><strong>" + getString(R.string.second) + " " + getString(R.string.favourite) + "</strong></H5><H3>" + ((bestNames1[1] == null)? "-" : bestNames1[1]) + "</H3><br>" +
                    "<H5><strong>" + getString(R.string.third) + " " + getString(R.string.favourite) + "</strong></H5><H3>" + ((bestNames1[2] == null)? "-" : bestNames1[2])+"</H3>";
            statisticsText1.setText(Html.fromHtml( text1, Html.FROM_HTML_MODE_LEGACY));
        });

        // add the sidebar
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View sidebar = layoutInflater.inflate(R.layout.sidebar, statistics, false);
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
            Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        TextView sidebarOption1 = sidebar.findViewById(R.id.sidebarOption1);
        sidebarOption1.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, StoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        TextView sidebarOption2 = sidebar.findViewById(R.id.sidebarOption2);
        sidebarOption2.setOnClickListener(v -> {
            sidebar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        });

        ImageView openListIcon1 = header.findViewById(R.id.headerOpenListIcon);
        openListIcon1.setOnClickListener(v -> {
            sidebar.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        });

        statistics.addView(sidebar); // put the sidebar in the main layout
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
        setContentView(R.layout.statistics);
        //lock horizontal view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statistics), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        changeTopBarAppearance();
        this.sharedPreferences =
                getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);

        create_main_Ui();
    }
}
