package com.example.plantkriya.account;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.plantkriya.R;
import com.example.plantkriya.fragment.reviewer.NavigationFragment;

public class MainReviewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_reviewer);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = new NavigationFragment();
            addFragment(fragment, R.id.contentFrame);
        }

    }

    private void addFragment(Fragment fragment, int contentFrame) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(contentFrame, fragment);
        ft.commit();
    }

}