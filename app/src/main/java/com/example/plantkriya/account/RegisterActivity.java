package com.example.plantkriya.account;

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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.example.plantkriya.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
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

public class RegisterActivity extends AppCompatActivity {

    //permission constants
    private static final int CAMERA_REQ_CODE = 100;
    private static final int STORAGE_REQ_CODE = 200;
    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private ImageButton btnBack;
    private ImageView profile_pic;
    private Button btnReg;
    private TextInputEditText edtName, edtphone, edtmail, edtpassword, edtConfirmPassword;
    //peremission arrays
    private String[] cameraPermission;
    private String[] storagePermission;
    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    //Storage Ref
    private AnstronCoreHelper anstronCoreHelper;
    private String fullName, phoneNumber, email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnBack = findViewById(R.id.btnBack);
        profile_pic = findViewById(R.id.profile_pic);
        edtName = findViewById(R.id.edt_fullnm);
        edtmail = findViewById(R.id.edt_email);
        edtphone = findViewById(R.id.edt_phone);
        edtpassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirmpassword);
        btnReg = findViewById(R.id.btnRegister);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        anstronCoreHelper = new AnstronCoreHelper(this);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImagePickDialog();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void inputData() {

        //validation
        validateUsername();
        validateNumberPhone();
        validateEmail();
        validatePassword();
        validateConfrimPassword();


        createAccount();

    }

    private boolean validateUsername() {
        fullName = edtName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            edtName.setError("Field tidak boleh kosong");
            return false;
        } else if (fullName.length() > 15) {
            edtName.setError("Nama terlalu panjang");
            return false;
        } else {
            edtName.setError(null);
            return true;
        }
    }

    private boolean validateNumberPhone() {
        phoneNumber = edtphone.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            edtphone.setError("Field tidak boleh kosong");
            return false;
        } else {
            edtphone.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        email = edtmail.getText().toString().trim();
        if (email.isEmpty()) {
            edtmail.setError("Field tidak boleh kosong");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StyleableToast.makeText(this, "Email tidak valid", R.style.exampleToast).show();
            return false;
        } else {
            edtmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        password = edtpassword.getText().toString().trim();

        if (password.isEmpty()) {
            edtpassword.setError("Field tidak boleh kosong");
            return false;
        } else if (password.length() < 6) {
            edtpassword.setError("Password harus lebih 6 karakter");
            return false;
        } else {
            edtpassword.setError(null);
            return true;
        }
    }

    private boolean validateConfrimPassword() {
        confirmPassword = edtConfirmPassword.getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Field tidak boleh kosong");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Password harus sama");
            return false;
        } else {
            edtConfirmPassword.setError(null);
            return true;
        }
    }

    private void createAccount() {
        progressDialog.setMessage("Membuat akun..");
        if (!validateUsername() | !validateNumberPhone() | !validateEmail() | !validatePassword() | !validateConfrimPassword()) {
            return;
        }

        //create
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account created
                        progressDialog.show();
                        saveFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("Menyimpan Data..");

        if (image_uri == null) {
            //save info without image

            //setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" +  firebaseAuth.getUid());
            hashMap.put("email", "" + email);
            hashMap.put("name", "" + fullName);
            hashMap.put("phone", "" + phoneNumber);
            hashMap.put("password", "" + password);
            hashMap.put("confirmPassword", "" + confirmPassword);
            hashMap.put("accountType", "User");
            hashMap.put("online", "true");
            hashMap.put("profileImage", "");
            hashMap.put("point", "0");

            //save to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //db updated
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
                            finish();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed updating db
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
                            finish();
                        }
                    });
        } else {
            //save info with image

            //name & path with image

            String filePathandName = "profile_images/" + "" + firebaseAuth.getUid();

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
                                hashMap.put("uid", "" + firebaseAuth.getUid());
                                hashMap.put("email", "" + email);
                                hashMap.put("name", "" + fullName);
                                hashMap.put("phone", "" + phoneNumber);
                                hashMap.put("password", "" + password);
                                hashMap.put("confirmPassword", "" + confirmPassword);
                                hashMap.put("accountType", "User");
                                hashMap.put("online", "true");
                                hashMap.put("point", "0");
                                hashMap.put("profileImage", "" + downloadImageUri);//urluploading image

                                //save to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //db updated
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
                                                finish();
                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed updating db
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
                                                finish();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            file.delete();

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
                                requestStoragePemission();
                            }
                        }
                    }
                })

                .show();
    }

   /* private void compressUpload() {
        if (image_uri != null){
            File file = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this, image_uri),
                    new File(this.getCacheDir(), "temp")));
            Uri uri = Uri.fromFile(file);
        }
    }*/

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePemission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQ_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPemission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQ_CODE:
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
            case STORAGE_REQ_CODE:
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
                profile_pic.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview
                profile_pic.setImageURI(image_uri);
            }
        }
    }
}