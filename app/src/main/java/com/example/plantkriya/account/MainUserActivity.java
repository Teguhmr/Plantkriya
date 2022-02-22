package com.example.plantkriya.account;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.plantkriya.R;
import com.example.plantkriya.fragment.user.NavigationUserFragment;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class MainUserActivity extends AppCompatActivity {

    private long backPressed;
    private StyleableToast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.contentFrameUser);
        if (fragment == null) {
            fragment = new NavigationUserFragment();
            addFragment(fragment, R.id.contentFrameUser);
        }

    }

    private void addFragment(Fragment fragment, int contentFrame) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(contentFrame, fragment);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            isDestroyed();
            super.onBackPressed();
            return;
        } else {
            backToast = StyleableToast.makeText(getBaseContext(), "Tekan sekali lagi untuk keluar", R.style.backToast);
            backToast.show();
        }
        backPressed = System.currentTimeMillis();
    }
}