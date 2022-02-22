package com.example.plantkriya.activity.submissionplant;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantkriya.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class DetailSubmissionActivity extends AppCompatActivity {

    private String submissionId;

    private ImageButton backBtn;
    private TextView tvSubmission, tvDate, tvTitle, tvCategory, tvStatus, tvName, tvEmail, tvNoteReviewer, tvMinggu;
    private ImageView picMaterial, picSubmission;
    private FirebaseAuth firebaseAuth;
    private RatingBar ratingBar;
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_submission);

        submissionId = getIntent().getStringExtra("submissionId");
        firebaseAuth = FirebaseAuth.getInstance();

        picMaterial = findViewById(R.id.material_pic);
        picSubmission = findViewById(R.id.image_submission);
        tvSubmission = findViewById(R.id.tv_submissionId);
        tvDate = findViewById(R.id.date_tv);
        tvTitle = findViewById(R.id.title_tv);
        tvCategory = findViewById(R.id.tv_category_plant);
        tvStatus = findViewById(R.id.status_submission_tv);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        ratingBar = findViewById(R.id.rating_bar);
        tvNoteReviewer = findViewById(R.id.tv_reviewer_note);
        tvMinggu = findViewById(R.id.tv_minggu);

        backBtn = findViewById(R.id.btnBack);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadSubmissionDetail();

        picSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSubmissionDetailPhoto();

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailSubmissionActivity.this);
                View mVIew = getLayoutInflater().inflate(R.layout.zoomview, null);
                photoView = mVIew.findViewById(R.id.zoomview);
                builder.setView(mVIew);
                Button btnCancel = mVIew.findViewById(R.id.btn_close);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });


            }
        });

    }

    private void loadSubmissionDetail() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String uid = "" + ds.getRef().getKey();

                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(uid);
                            databaseReference1.child("Submission_Plant").child(submissionId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String submissionId = "" + snapshot.child("submissionId").getValue();
                                    String materialTitle = "" + snapshot.child("materialTitle").getValue();
                                    String materialCategoryPlant = "" + snapshot.child("materialCategoryPlant").getValue();
                                    String materialIcon = "" + snapshot.child("materialIcon").getValue();
                                    String submissionTime = "" + snapshot.child("submissionTime").getValue();
                                    String submissionStatus = "" + snapshot.child("submissionStatus").getValue();
                                    String images_submission = "" + snapshot.child("images_submission").getValue();
                                    String nameProfile = "" + snapshot.child("nameProfile").getValue();
                                    String emailProfile = "" + snapshot.child("emailProfile").getValue();
                                    String ratings = "" + snapshot.child("ratings").getValue();
                                    String minggu = "" + snapshot.child("minggu_ke").getValue();
                                    String noteReviewer = "" + snapshot.child("noteReviewer").getValue();
                                    //addlist


//                                    picMaterial
//                                            picSubmission
                                    tvSubmission.setText(submissionId);
                                    tvTitle.setText(materialTitle);
                                    tvCategory.setText(materialCategoryPlant);
                                    tvStatus.setText(submissionStatus);
                                    tvName.setText(nameProfile);
                                    tvEmail.setText(emailProfile);
                                    tvNoteReviewer.setText(noteReviewer);
                                    tvMinggu.setText(minggu);

                                    float myratings = Float.parseFloat(ratings);
                                    ratingBar.setRating(myratings);

                                    if (submissionStatus.equals("Diproses")) {
                                        tvStatus.setTextColor(getResources().getColor(R.color.blue_active));
                                    } else if (submissionStatus.equals("Diterima")) {
                                        tvStatus.setTextColor(getResources().getColor(R.color.green_active));
                                    } else if (submissionStatus.equals("Ditolak")) {
                                        tvStatus.setTextColor(getResources().getColor(R.color.red_active));
                                    }

//                                    convert Time
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(Long.parseLong(submissionTime));
                                    String formattedDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();
                                    tvDate.setText(formattedDate);

                                    try {
                                        Picasso.get()
                                                .load(materialIcon)
                                                .placeholder(R.drawable.pp1)
                                                .into(picMaterial);
                                    } catch (Exception e) {
                                        picMaterial.setImageResource(R.drawable.pp1);
                                    }

                                    try {
                                        Picasso.get()
                                                .load(images_submission)
                                                .placeholder(R.drawable.ic_baseline_photo_library_24)
                                                .into(picSubmission);
                                    } catch (Exception e) {
                                        picSubmission.setImageResource(R.drawable.ic_baseline_photo_library_24);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadSubmissionDetailPhoto() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String uid = "" + ds.getRef().getKey();

                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(uid);
                            databaseReference1.child("Submission_Plant").child(submissionId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String images_submission = "" + snapshot.child("images_submission").getValue();

                                    try {
                                        Picasso.get()
                                                .load(images_submission)
                                                .placeholder(R.drawable.pp1)
                                                .into(photoView);
                                    } catch (Exception e) {
                                        photoView.setImageResource(R.drawable.pp1);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}