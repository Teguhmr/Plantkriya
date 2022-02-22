package com.example.plantkriya.activity.plantproduct;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.plantkriya.R;
import com.example.plantkriya.activity.submissionkriya.UploadSubmissionKriyaActivity;
import com.example.plantkriya.adapter.AdapterForMaterialDetail;
import com.example.plantkriya.adapter.DepthPageTransformer;
import com.example.plantkriya.listener.IFirebaseLoadDone;
import com.example.plantkriya.model.ModelMaterialDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.List;

public class DetailMaterialKriyaActivity extends AppCompatActivity implements IFirebaseLoadDone, ValueEventListener {


    public static String materialId;
    private ImageButton btnUpload;
    private ImageView btnPrev, btnNext;
    private ViewPager viewPager;
    private AdapterForMaterialDetail adapter;
    private DatabaseReference detailReference;
    private IFirebaseLoadDone iFirebaseLoadDone;

    private int mCurrentPage;
    private List<ModelMaterialDetail> mList;
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            mCurrentPage = i;

            if (i == 0) {

                btnNext.setEnabled(true);
                btnPrev.setEnabled(false);
                btnPrev.setVisibility(View.INVISIBLE);

            } else if (i == mList.size() - 1) {

                btnNext.setEnabled(true);
                btnPrev.setEnabled(true);
                btnPrev.setVisibility(View.VISIBLE);

                btnUpload.setVisibility(View.VISIBLE);


            } else {

                btnNext.setEnabled(true);
                btnPrev.setEnabled(true);
                btnPrev.setVisibility(View.VISIBLE);


            }

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make fullscreen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail_material_kriya);

        mediaPlayer = MediaPlayer.create(this, R.raw.aroma_indosiar);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        materialId = getIntent().getStringExtra("materialId");

        detailReference = FirebaseDatabase.getInstance().getReference("Users").child("Material_Kriya").child(materialId).child("detail_kriya");

        btnUpload = findViewById(R.id.btn_forSubmission);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent intent = new Intent(DetailMaterialKriyaActivity.this, UploadSubmissionKriyaActivity.class);
                intent.putExtra("materialId", materialId);
                startActivity(intent);
                finish();
            }
        });
        mList = new ArrayList<>();

        adapter = new AdapterForMaterialDetail(this, mList);
        viewPager = findViewById(R.id.viewPagerDetail);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(viewListener);

        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnPrev.setVisibility(View.INVISIBLE);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

        viewPager.setPageTransformer(true, new DepthPageTransformer());
        iFirebaseLoadDone = this;

        loadDetail();

        startTootipAnimation();
    }

    private void startTootipAnimation() {
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(btnUpload, "scaleY", 0.8f);
        scaleY.setDuration(200);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(btnUpload, "scaleY", 1f);
        scaleYBack.setDuration(500);
        scaleYBack.setInterpolator(new BounceInterpolator());
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(1000);
        animatorSet.playSequentially(scaleY, scaleYBack);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet.setStartDelay(2000);
                animatorSet.start();
            }
        });
        btnUpload.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        super.onBackPressed();
    }

    private void loadDetail() {
        detailReference.addValueEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailReference.addValueEventListener(this);
    }

    @Override
    protected void onStop() {
        detailReference.removeEventListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        detailReference.removeEventListener(this);
        super.onDestroy();
    }

    @Override
    public void onFirebaseLoadSuccess(List<ModelMaterialDetail> modelMaterialDetailList) {
        adapter = new AdapterForMaterialDetail(this, modelMaterialDetailList);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        StyleableToast.makeText(this, "" + message, R.style.exampleToast);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        final List<ModelMaterialDetail> modelMaterialDetailList = new ArrayList<>();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            modelMaterialDetailList.add(dataSnapshot.getValue(ModelMaterialDetail.class));
            iFirebaseLoadDone.onFirebaseLoadSuccess(modelMaterialDetailList);
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        iFirebaseLoadDone.onFirebaseLoadFailed(error.getMessage());

    }
}