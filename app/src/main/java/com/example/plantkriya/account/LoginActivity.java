package com.example.plantkriya.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantkriya.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    //UI view
    private EditText edtmail, edtpassword, edtmailRecover;
    private TextView forgotPasswordtv, noAccounttv;
    private Button loginBtn, recoverBtn, cancelBtn;
    private LinearLayout overBox, recoverForgotP;
    private ImageView ic_keyRecover;
    private ImageButton announcementBtn;

    private Animation fromsmall, fromnothing, foric_key, togo;

    private FirebaseAuth firebaseAuth, firebaseAuthRe;
    private ProgressDialog progressDialog, progressDialogRe;

    private long backPressed;
    private StyleableToast backToast;
    private String edt_email;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init UI view
        edtmail = findViewById(R.id.edt_email);
        edtpassword = findViewById(R.id.edt_password);
        forgotPasswordtv = findViewById(R.id.forgot);
        noAccounttv = findViewById(R.id.no_account);
        loginBtn = findViewById(R.id.btnLogin);

        edtmailRecover = findViewById(R.id.edt_email_recover);
        recoverBtn = findViewById(R.id.btnRecover);
        cancelBtn = findViewById(R.id.cancel_recover);
        overBox = findViewById(R.id.overBox);
        recoverForgotP = findViewById(R.id.recover_forgotPassword);
        ic_keyRecover = findViewById(R.id.icon_key_recover);

        fromsmall = AnimationUtils.loadAnimation(this, R.anim.fromsmall);
        fromnothing = AnimationUtils.loadAnimation(this, R.anim.fromnothing);
        foric_key = AnimationUtils.loadAnimation(this, R.anim.foric_key);
        togo = AnimationUtils.loadAnimation(this, R.anim.togo);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthRe = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialogRe = new ProgressDialog(this);
        progressDialogRe.setTitle("Please Wait");
        progressDialogRe.setCanceledOnTouchOutside(false);

        String text = "Not have account? Register";
        String text1 = "Forgot Password?";

        SpannableString ss = new SpannableString(text);
        SpannableString ss1 = new SpannableString(text1);

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorMiddle2));
            }

        };

        ic_keyRecover.setVisibility(View.GONE);
        overBox.setVisibility(View.GONE);
        recoverForgotP.setVisibility(View.GONE);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ic_keyRecover.setVisibility(View.VISIBLE);
                overBox.setVisibility(View.VISIBLE);
                recoverForgotP.setVisibility(View.VISIBLE);

                ic_keyRecover.startAnimation(foric_key);
                overBox.startAnimation(fromnothing);
                recoverForgotP.startAnimation(fromsmall);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.design_default_color_error));
            }

        };
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overBox.startAnimation(togo);
                recoverForgotP.startAnimation(togo);
                ic_keyRecover.startAnimation(togo);
                ic_keyRecover.setVisibility(View.GONE);
                overBox.setVisibility(View.GONE);
                recoverForgotP.setVisibility(View.GONE);

            }
        });

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
                overBox.startAnimation(togo);
                recoverForgotP.startAnimation(togo);
                ic_keyRecover.startAnimation(togo);
                ic_keyRecover.setVisibility(View.GONE);
                overBox.setVisibility(View.GONE);
                recoverForgotP.setVisibility(View.GONE);

            }
        });


        ss.setSpan(boldSpan, 18, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 18, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(clickableSpan1, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        forgotPasswordtv.setText(ss1);
        forgotPasswordtv.setMovementMethod(LinkMovementMethod.getInstance());

        noAccounttv.setText(ss);
        noAccounttv.setMovementMethod(LinkMovementMethod.getInstance());


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        announcementBtn = findViewById(R.id.announcement_imgBtn);
        announcementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View mVIew = getLayoutInflater().inflate(R.layout.zoomview, null);
                PhotoView photoView = mVIew.findViewById(R.id.zoomview);
                photoView.setImageResource(R.drawable.notecopy);
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
        startTootipAnimation();


    }

    private void startTootipAnimation() {
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(announcementBtn, "scaleY", 0.8f);
        scaleY.setDuration(200);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(announcementBtn, "scaleY", 1f);
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
        announcementBtn.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
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

    private void recoverPassword() {
        edt_email = edtmailRecover.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(edt_email).matches()) {
            StyleableToast.makeText(this, "Email tidak valid", R.style.exampleToast).show();
            return;
        }

        progressDialogRe.setMessage("Mengirim instruksi reset kata sandi");
        progressDialogRe.show();

        firebaseAuthRe.sendPasswordResetEmail(edt_email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //instruction sent
                        progressDialogRe.dismiss();
                        Toast.makeText(LoginActivity.this, "Instruksi reset kata sandi telah terkirim", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //instruction failed sent
                        progressDialogRe.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser() {
        email = edtmail.getText().toString().trim();
        password = edtpassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StyleableToast.makeText(this, "Email tidak valid", R.style.exampleToast).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            StyleableToast.makeText(this, "Masukkan Password", R.style.exampleToast).show();
            return;
        }

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //logged in succesfully
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeMeOnline() {
        //after log in, make user online
        progressDialog.setMessage("Checking User...");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        //update value to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update successfully
                        checkUserType();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String accountType = "" + dataSnapshot.child("accountType").getValue();
                            if (accountType.equals("User")) {
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
                                finish();
                            } else if (accountType.equals("Reviewer")) {
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainReviewerActivity.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}