package com.example.plantkriya.screen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.plantkriya.R;
import com.example.plantkriya.account.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    private IntroViewPager introViewPager;
    private Button btnNext;
    private int mCurrentPage;
    private List<Screen_Item> mList;
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            mCurrentPage = i;

            if (i == 0) {
//                screenPager.setCurrentItem(mCurrentPage + 1);


            } else if (i == mList.size() - 1) {

                btnNext.setEnabled(true);
                btnNext.setText("GET STARTED");
                loadLastScreen();
            } else {

            }

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intro);

        if (restorePrefData()) {

            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

        btnNext = findViewById(R.id.btnNext);

        mList = new ArrayList<>();
        mList.add(new Screen_Item(R.drawable.plant));
        mList.add(new Screen_Item(R.drawable.boxscreen));
        mList.add(new Screen_Item(R.drawable.screen));

        screenPager = findViewById(R.id.screen_viewpager);
        introViewPager = new IntroViewPager(this, mList);
        screenPager.setAdapter(introViewPager);
        screenPager.addOnPageChangeListener(viewListener);

        mCurrentPage = 0;
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if (mCurrentPage == mList.size()-1) {
//                    savePrefData();
//
//                }

                savePrefData();


            }
        });

    }

    private boolean restorePrefData() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean isIntroActivityOpenedBefore = prefs.getBoolean("isIntroOpened", false);
        return isIntroActivityOpenedBefore;
    }

    private void savePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.apply();

        //open login
        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
        //save activity for once
        finish();

    }

    private void loadLastScreen() {
        btnNext.setVisibility(View.VISIBLE);
    }


}