package com.example.plantkriya.activity.plantproduct;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.example.plantkriya.Constants;
import com.example.plantkriya.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.io.File;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    //pemission constant
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    String materialTitle, materialDesc, materialCategory, materialKind, materialSubtitle, levelMaterial;
    private ImageButton btnBack;
    private ImageView materialPhoto;
    private EditText titleEdt, descEdt, subtitleEdt, levelEdt;
    private TextView categoryTv, plantTv;
    private SwitchCompat levelSwitch;
    private Button addmaterialBtn;
    //permission array
    private String[] cameraPermission;
    private String[] storagePermission;
    //image picked uri
    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private AnstronCoreHelper anstronCoreHelper;
    private boolean levelAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        btnBack = findViewById(R.id.btnBack);
        materialPhoto = findViewById(R.id.profile_pic);
        titleEdt = findViewById(R.id.edt_title);
        descEdt = findViewById(R.id.edt_desc);
        subtitleEdt = findViewById(R.id.edt_subTitle);
        categoryTv = findViewById(R.id.tv_category);
        levelSwitch = findViewById(R.id.switchLevel);
        levelEdt = findViewById(R.id.edt_level);
        addmaterialBtn = findViewById(R.id.btn_addmaterial);
        plantTv = findViewById(R.id.tv_plant);

        firebaseAuth = FirebaseAuth.getInstance();

        //setup Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        anstronCoreHelper = new AnstronCoreHelper(this);
        levelEdt.setVisibility(View.GONE);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        levelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //checked Show
                    levelEdt.setVisibility(View.VISIBLE);
                } else {
                    //hide
                    levelEdt.setVisibility(View.GONE);
                }
            }
        });

        materialPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        addmaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Flow
                //Input data
                //ValidateData
                //add data to db

                inputData();
            }
        });

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        plantTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPlantDialog();
            }
        });
    }

    private void inputData() {
        materialTitle = titleEdt.getText().toString().trim();
        materialDesc = descEdt.getText().toString().trim();
        materialCategory = categoryTv.getText().toString().trim();
        materialKind = plantTv.getText().toString().trim();
        materialSubtitle = subtitleEdt.getText().toString().trim();
        levelAvailable = levelSwitch.isChecked();

        if (TextUtils.isEmpty(materialTitle)) {
            StyleableToast.makeText(this, "Isi judul", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(materialCategory)) {
            StyleableToast.makeText(this, "Isi Kategori", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(materialKind)) {
            StyleableToast.makeText(this, "Isi Kategori Plant", Toast.LENGTH_SHORT).show();
            return;
        }
        if (levelAvailable) {
            levelMaterial = levelEdt.getText().toString().trim();
            if (TextUtils.isEmpty(levelMaterial)) {
                StyleableToast.makeText(this, "Isi Level", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            levelMaterial = "";
        }

        addMaterial();
    }

    private void addMaterial() {
        progressDialog.setMessage("Menambahkan Item");
        progressDialog.show();

        final String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            //upload without image

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("materialId", "" + timestamp);
            hashMap.put("materialTitle", "" + materialTitle);
            hashMap.put("materialDesc", "" + materialDesc);
            hashMap.put("materialCategory", "" + materialCategory);
            hashMap.put("materialSubtitle", "" + materialSubtitle);
            hashMap.put("materialCategoryPlant", "" + materialKind);
            hashMap.put("materialIcon", "");
            hashMap.put("materialLevel", "" + levelMaterial);
            hashMap.put("levelAvailable", "" + levelAvailable);
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("uid", "" + firebaseAuth.getUid());

            //add to db
            //save to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child("Material").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //db updated
                            progressDialog.dismiss();
                            StyleableToast.makeText(AddProductActivity.this, "Material ditambahkan", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed updating db
                            progressDialog.dismiss();
                            StyleableToast.makeText(AddProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            //save info with image

            //name & path with image

            String filePathandName = "material_images/" + "" + timestamp;

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
                                hashMap.put("materialId", "" + timestamp);
                                hashMap.put("materialTitle", "" + materialTitle);
                                hashMap.put("materialDesc", "" + materialDesc);
                                hashMap.put("materialCategory", "" + materialCategory);
                                hashMap.put("materialSubtitle", "" + materialSubtitle);
                                hashMap.put("materialCategoryPlant", "" + materialKind);
                                hashMap.put("materialIcon", "" + downloadImageUri);
                                hashMap.put("materialLevel", "" + levelMaterial);
                                hashMap.put("levelAvailable", "" + levelAvailable);
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("uid", "" + firebaseAuth.getUid());

                                //save to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child("Material").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //db updated
                                                progressDialog.dismiss();
                                                StyleableToast.makeText(AddProductActivity.this, "Material ditambahkan", Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed updating db
                                                progressDialog.dismiss();
                                                StyleableToast.makeText(AddProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            StyleableToast.makeText(AddProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void clearData() {
        //clear data after aploading
        titleEdt.setText("");
        descEdt.setText("");
        categoryTv.setText("");
        subtitleEdt.setText("");
        plantTv.setText("");
        levelSwitch.setChecked(false);
        materialPhoto.setImageResource(R.drawable.pp1);
        levelEdt.setText("");
        image_uri = null;
    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Category")
                .setItems(Constants.optionsCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked
                        String category = Constants.optionsCategory[which];
                        //set picked
                        categoryTv.setText(category);
                    }
                }).show();
    }

    private void categoryPlantDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Category")
                .setItems(Constants.plantCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked
                        String category = Constants.plantCategory[which];
                        //set picked
                        plantTv.setText(category);
                    }
                }).show();
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
                materialPhoto.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview
                materialPhoto.setImageURI(image_uri);
            }
        }
    }
}