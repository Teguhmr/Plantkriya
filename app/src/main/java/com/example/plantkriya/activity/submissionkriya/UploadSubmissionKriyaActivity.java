package com.example.plantkriya.activity.submissionkriya;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.example.plantkriya.R;
import com.example.plantkriya.activity.submissionplant.DetailSubmissionActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

public class UploadSubmissionKriyaActivity extends AppCompatActivity {
    //pemission constant
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    public static String materialId;
    String materialTitle, materialDesc, materialCategory, materialKind, materialSubtitle, nameProfile, emailProfile, phoneProfile, materialIcon;
    String ratings;
    private ImageButton btnBack;
    private ImageView materialPhoto, imgSubmission;
    private TextView titleTv, descTv, subtitleTv, categoryTv, plantTv, nameTv, emailTv, phoneTv, tvLihatContoh;
    private Button uploadSubmissionBtn;
    //permission array
    private String[] cameraPermission;
    private String[] storagePermission;
    //image picked uri
    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private AnstronCoreHelper anstronCoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_submission_kriya);

        materialId = getIntent().getStringExtra("materialId");

        btnBack = findViewById(R.id.btnBack);
        materialPhoto = findViewById(R.id.profile_pic);
        imgSubmission = findViewById(R.id.image_submission);
        titleTv = findViewById(R.id.tv_title);
        descTv = findViewById(R.id.tv_desc);
        subtitleTv = findViewById(R.id.tv_subTitle);
        categoryTv = findViewById(R.id.tv_category);
        nameTv = findViewById(R.id.tv_name);
        emailTv = findViewById(R.id.tv_email);
        uploadSubmissionBtn = findViewById(R.id.btn_upload_submission_fix);
        plantTv = findViewById(R.id.tv_plant);
        phoneTv = findViewById(R.id.tv_phone);

        firebaseAuth = FirebaseAuth.getInstance();

        //setup Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        anstronCoreHelper = new AnstronCoreHelper(this);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        uploadSubmissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Flow
                //Input data
                //ValidateData
                //update data to db
                inputData();

                onBackPressed();
            }
        });

        imgSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        loadMaterialDetail();
        loadMyInfo();

        tvLihatContoh = findViewById(R.id.txt_lihat_contoh);

        String text1 = "Lihat Contoh";
        SpannableString ss1 = new SpannableString(text1);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UploadSubmissionKriyaActivity.this);
                View mVIew = getLayoutInflater().inflate(R.layout.zoomview, null);
                ImageView photoView = mVIew.findViewById(R.id.zoomview);
                builder.setView(mVIew);
                photoView.setImageResource(R.drawable.image_kriya_contoh);
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

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorMiddle2));
            }

        };

        ss1.setSpan(clickableSpan, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLihatContoh.setText(ss1);
        tvLihatContoh.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loadMaterialDetail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Material_Kriya").child(materialId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String materialId = "" + snapshot.child("materialId").getValue();
                        String materialTitle = "" + snapshot.child("materialTitle").getValue();
                        String materialDesc = "" + snapshot.child("materialDesc").getValue();
                        String materialCategory = "" + snapshot.child("materialCategory").getValue();
                        String materialSubtitle = "" + snapshot.child("materialSubtitle").getValue();
                        String materialCategoryPlant = "" + snapshot.child("materialCategoryKriya").getValue();
                        String materialIcon = "" + snapshot.child("materialIcon").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String uid = "" + snapshot.child("uid").getValue();

                        titleTv.setText(materialTitle);
                        descTv.setText(materialDesc);
                        subtitleTv.setText(materialSubtitle);
                        categoryTv.setText(materialCategory);
                        plantTv.setText(materialCategoryPlant);

                        try {
                            Picasso.get()
                                    .load(materialIcon)
                                    .placeholder(R.drawable.pp1)
                                    .into(materialPhoto);
                        } catch (Exception e) {
                            materialPhoto.setImageResource(R.drawable.pp1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String name = "" + dataSnapshot.child("name").getValue();
                            String email = "" + dataSnapshot.child("email").getValue();
                            String phone = "" + dataSnapshot.child("phone").getValue();
                            String online = "" + dataSnapshot.child("online").getValue();
                            String profileImage = "" + dataSnapshot.child("profileImage").getValue();
                            String accountType = "" + dataSnapshot.child("accountType").getValue();

                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
//
//                            edtnameTv.setText(name);
//                            edtemailTv.setText(email);
//                            edtnoHpTv.setText(phone);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputData() {
        materialTitle = titleTv.getText().toString().trim();
        materialDesc = descTv.getText().toString().trim();
        materialCategory = categoryTv.getText().toString().trim();
        materialKind = plantTv.getText().toString().trim();
        materialSubtitle = subtitleTv.getText().toString().trim();
        nameProfile = nameTv.getText().toString().trim();
        emailProfile = emailTv.getText().toString().trim();
        phoneProfile = phoneTv.getText().toString().trim();
        addMaterial();

    }

    private void addMaterial() {
        progressDialog.setMessage("Menambahkan Item");
        progressDialog.show();

        final String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            //upload without image

            final HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("materialTitle", "" + materialTitle);
            hashMap.put("materialDesc", "" + materialDesc);
            hashMap.put("materialCategory", "" + materialCategory);
            hashMap.put("materialSubtitle", "" + materialSubtitle);
            hashMap.put("materialCategoryKriya", "" + materialKind);
            hashMap.put("nameProfile", "" + nameProfile);
            hashMap.put("emailProfile", "" + emailProfile);
            hashMap.put("phoneProfile", "" + phoneProfile);
            hashMap.put("materialIcon", "");
            hashMap.put("submissionStatus", "Diproses");//sedang diproses/ditolak/diterima
            hashMap.put("images_submission", "");
            hashMap.put("ratings", "0");
            hashMap.put("noteReviewer", "Menunggu");
            hashMap.put("submissionId", "" + timestamp);
            hashMap.put("submissionTime", "" + timestamp);

            //add to db
            //save to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(firebaseAuth.getUid()).child("Submission_Kriya");
            reference.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //db updated
                            progressDialog.dismiss();
                            StyleableToast.makeText(UploadSubmissionKriyaActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(UploadSubmissionKriyaActivity.this, DetailSubmissionKriyaActivity.class);
                            intent.putExtra("submissionId", timestamp);
                            startActivity(intent);

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed updating db
                            progressDialog.dismiss();
                            StyleableToast.makeText(UploadSubmissionKriyaActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            //save info with image

            //name & path with image

            String filePathandName = "submission_kriya_images/" + "" + materialId;

            //uploadImage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathandName);
            final File file = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this, image_uri),
                    new File(this.getCacheDir(), "temp")));
            Uri uri = Uri.fromFile(file);
            storageReference.child(anstronCoreHelper.getFileNameFromUri(uri))
                    .putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                //setup data to save
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("materialTitle", "" + materialTitle);
                                hashMap.put("materialDesc", "" + materialDesc);
                                hashMap.put("materialCategory", "" + materialCategory);
                                hashMap.put("materialSubtitle", "" + materialSubtitle);
                                hashMap.put("materialCategoryKriya", "" + materialKind);
                                hashMap.put("materialIcon", "" + materialIcon);
                                hashMap.put("nameProfile", "" + nameProfile);
                                hashMap.put("emailProfile", "" + emailProfile);
                                hashMap.put("phoneProfile", "" + phoneProfile);
                                hashMap.put("images_submission", "" + downloadImageUri);
                                hashMap.put("submissionId", "" + timestamp);
                                hashMap.put("submissionTime", "" + timestamp);
                                hashMap.put("ratings", "0");
                                hashMap.put("noteReviewer", "Menunggu");
                                hashMap.put("submissionStatus", "Diproses");//sedang diproses/ditolak/diterima

                                //save to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(firebaseAuth.getUid()).child("Submission_Kriya");
                                reference.child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //db updated
                                                progressDialog.dismiss();
                                                StyleableToast.makeText(UploadSubmissionKriyaActivity.this, "Upload success", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(UploadSubmissionKriyaActivity.this, DetailSubmissionActivity.class);
                                                intent.putExtra("submissionId", timestamp);
                                                startActivity(intent);
                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed updating db
                                                progressDialog.dismiss();
                                                StyleableToast.makeText(UploadSubmissionKriyaActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            StyleableToast.makeText(UploadSubmissionKriyaActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void showImagePickDialog() {

        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //camera clicked
                            if (checkCameraPermission()) {
                                //camerapermissionallowed
                                pickFromCamera();
                            } else {
                                requestCameraPemission();
                            }
                        } else {
                            //gallery clicked
                            if (checkStoragePermission()) {
                                //storageallowed
                                pickFromGallery();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })

                .show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Images_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Images_Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPemission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        //deniedpermission
                        Toast.makeText(this, "Camera permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        //deniedpermission
                        Toast.makeText(this, "Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get picked image
                image_uri = data.getData();
                //set to imageview
                imgSubmission.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview
                imgSubmission.setImageURI(image_uri);
            }
        }
    }
}