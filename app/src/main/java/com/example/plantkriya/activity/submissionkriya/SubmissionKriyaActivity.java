package com.example.plantkriya.activity.submissionkriya;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantkriya.Constants;
import com.example.plantkriya.R;
import com.example.plantkriya.adapter.AdapterSubmissionUserKriya;
import com.example.plantkriya.model.ModelSubmissionUserKriya;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubmissionKriyaActivity extends AppCompatActivity {

    private RecyclerView rvSubmission;
    private AdapterSubmissionUserKriya adapterSubmissionUser;
    private ArrayList<ModelSubmissionUserKriya> submissionUserArrayList;
    private ImageButton backBtn, filterBtn;
    private TextView tvsub;
    private EditText search_materialEdt;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_kriya);
        rvSubmission = findViewById(R.id.rv_submission_kriya);
        backBtn = findViewById(R.id.btnBack);

        firebaseAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        filterBtn = findViewById(R.id.filteredProduct);
        tvsub = findViewById(R.id.tvsubmissionstatus);
        search_materialEdt = findViewById(R.id.search_materialEdt);


        loadSubmission();


        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SubmissionKriyaActivity.this);
                builder.setTitle("Filter Submission")
                        .setItems(Constants.optionsSubmissionkriya, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CharSequence selected = Constants.optionsSubmissionkriya[which];
                                if (selected.equals("all")) {
                                    //load all
                                    tvsub.setText("Menampilkan semua");
                                    adapterSubmissionUser.getFilter().filter("");//show all
                                } else {
                                    //load filtered
                                    tvsub.setText("Menampilan submission " + selected);
                                    adapterSubmissionUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        search_materialEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterSubmissionUser.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadSubmission() {
        submissionUserArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Submission_Kriya").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                submissionUserArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ModelSubmissionUserKriya modelSubmissionUser = dataSnapshot.getValue(ModelSubmissionUserKriya.class);


                        //addlist
                        submissionUserArrayList.add(modelSubmissionUser);
                    }

                    adapterSubmissionUser = new AdapterSubmissionUserKriya(SubmissionKriyaActivity.this, submissionUserArrayList);

                    rvSubmission.setAdapter(adapterSubmissionUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}