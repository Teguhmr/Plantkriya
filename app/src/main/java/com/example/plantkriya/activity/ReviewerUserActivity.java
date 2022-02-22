package com.example.plantkriya.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantkriya.Constants;
import com.example.plantkriya.R;
import com.example.plantkriya.adapter.AdapterSubmissionUserForReviewer;
import com.example.plantkriya.model.ModelSubmissionUserForReviewer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewerUserActivity extends AppCompatActivity {

    private RecyclerView rvSubmission;
    private AdapterSubmissionUserForReviewer adapterSubmissionUser;
    private ArrayList<ModelSubmissionUserForReviewer> submissionUserArrayList;
    private ImageButton backBtn, filterBtn;
    private TextView search_materialEdt;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewer_user);

        rvSubmission = findViewById(R.id.rv_submission);
        backBtn = findViewById(R.id.btnBack);
        firebaseAuth = FirebaseAuth.getInstance();
        filterBtn = findViewById(R.id.filteredProduct);
        search_materialEdt = findViewById(R.id.search_materialEdt);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewerUserActivity.this);
                builder.setTitle("Filter Submission")
                        .setItems(Constants.optionsSubmission, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CharSequence selected = Constants.optionsSubmission[which];
                                if (selected.equals("all")) {
                                    //load all
                                    search_materialEdt.setText("Show all submission");
                                    adapterSubmissionUser.getFilter().filter("");//show all
                                } else {
                                    //load filtered
                                    search_materialEdt.setText("Showing " + selected + " submission");
                                    adapterSubmissionUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadSubmission();
    }

    private void loadSubmission() {
        submissionUserArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                submissionUserArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = "" + ds.getRef().getKey();

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users")
                            .child(uid);
                    databaseReference1.child("Submission_Plant").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    ModelSubmissionUserForReviewer modelSubmissionUser = dataSnapshot.getValue(ModelSubmissionUserForReviewer.class);


                                    //addlist
                                    submissionUserArrayList.add(modelSubmissionUser);
                                }

                                adapterSubmissionUser = new AdapterSubmissionUserForReviewer(ReviewerUserActivity.this, submissionUserArrayList);

                                rvSubmission.setAdapter(adapterSubmissionUser);
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

    private void loadAllSubmission() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Submission_Plant")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelSubmissionUserForReviewer modelSubmissionUserForReviewer = ds.getValue(ModelSubmissionUserForReviewer.class);

                            submissionUserArrayList.add(modelSubmissionUserForReviewer);
                        }
                        adapterSubmissionUser = new AdapterSubmissionUserForReviewer(ReviewerUserActivity.this, submissionUserArrayList);

                        rvSubmission.setAdapter(adapterSubmissionUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}