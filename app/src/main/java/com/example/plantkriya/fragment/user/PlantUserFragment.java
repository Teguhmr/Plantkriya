package com.example.plantkriya.fragment.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.example.plantkriya.Constants;
import com.example.plantkriya.NavigationIconClickListener;
import com.example.plantkriya.R;
import com.example.plantkriya.account.LoginActivity;
import com.example.plantkriya.activity.submissionplant.SubmissionActivity;
import com.example.plantkriya.adapter.MaterialPlantUserAdapter;
import com.example.plantkriya.decoration.ProductGridItemDecoration;
import com.example.plantkriya.model.ModelMaterial;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class PlantUserFragment extends Fragment {

    //permission constants
    private static final int CAMERA_REQ_CODE = 100;
    private static final int STORAGE_REQ_CODE = 200;
    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private TextView btnLogout, editProfileBtn, submissionBtn, filteredMaterialTv, tvname;
    private Button changeProfileBtn, updateDatabtn, cancelupdateBtn;
    private TextView nameTv, emailTv, noHpTv;
    private EditText edtnameTv, edtemailTv, edtnoHpTv, searchProductTv;
    private BottomSheetDialog bottomSheetDialog, bottomSheetDialogUpdate;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    //peremission arrays
    private String[] cameraPermission;
    private String[] storagePermission;
    private Uri image_uri;

    private AnstronCoreHelper anstronCoreHelper;

    private ImageView profileView;
    private ImageButton filteredMaterialBtn;
    private RecyclerView rvMaterial;
    private ArrayList<ModelMaterial> materialArrayList;
    private MaterialPlantUserAdapter materialPlantAdapter;
    private String fullName, phoneNumber, email;

    public PlantUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plant_user, container, false);
        // Set up the tool bar
        setUpToolbar(view);

        btnLogout = view.findViewById(R.id.logout);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        searchProductTv = view.findViewById(R.id.search_materialEdt);
        filteredMaterialBtn = view.findViewById(R.id.filteredProduct);
        filteredMaterialTv = view.findViewById(R.id.filteredProductTv);
        rvMaterial = view.findViewById(R.id.rv_material);
        submissionBtn = view.findViewById(R.id.btn_submission);
        tvname = view.findViewById(R.id.tv_name);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        anstronCoreHelper = new AnstronCoreHelper(getContext());

        profileView = view.findViewById(R.id.profile_pic);

        searchProductTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    materialPlantAdapter.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filteredMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pilih Kategori")
                        .setItems(Constants.plantCategory, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CharSequence selected = Constants.plantCategory[which];
                                filteredMaterialTv.setText(selected);
                                materialPlantAdapter.getFilter().filter(selected);
                                if (selected.equals("All")) {
                                    //load all
                                    loadAllMaterial();
                                } else {
                                    //load filtered
                                    loadFilteredMaterial(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //open edit
//                startActivity(new Intent(getContext(), ProfileEditReviewerActivity.class));
                bottomSheetDialog = new BottomSheetDialog(widget.getContext(),
                        R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(widget.getContext()).inflate(
                        R.layout.layout_bottom_sheet,
                        (LinearLayout) widget.findViewById(R.id.bottomSheetContainer),
                        false

                );

                profileView = bottomSheetView.findViewById(R.id.profile_pic);
                nameTv = bottomSheetView.findViewById(R.id.tv_name);
                emailTv = bottomSheetView.findViewById(R.id.tv_email);
                noHpTv = bottomSheetView.findViewById(R.id.tv_phone);
                changeProfileBtn = bottomSheetView.findViewById(R.id.btn_save);
                changeProfileBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        bottomSheetDialogUpdate = new BottomSheetDialog(v.getContext(),
                                R.style.BottomSheetDialogTheme);
                        View bottomSheetViewUpdate = LayoutInflater.from(v.getContext()).inflate(
                                R.layout.layout_bottom_sheet_update,
                                (LinearLayout) v.findViewById(R.id.bottomSheetContainerUpdate),
                                false

                        );

                        profileView = bottomSheetViewUpdate.findViewById(R.id.profile_pic_update);
                        profileView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showImagePickDialog();
                            }
                        });
                        edtnameTv = bottomSheetViewUpdate.findViewById(R.id.edt_name_update);
                        edtemailTv = bottomSheetViewUpdate.findViewById(R.id.edt_email_update);
                        edtnoHpTv = bottomSheetViewUpdate.findViewById(R.id.edt_phone_update);
                        cancelupdateBtn = bottomSheetViewUpdate.findViewById(R.id.cancel_update_btn);
                        updateDatabtn = bottomSheetViewUpdate.findViewById(R.id.btn_update);
                        updateDatabtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                inputData();
                            }
                        });
                        cancelupdateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialogUpdate.dismiss();
                            }
                        });
                        checkUserUpdate();

                        bottomSheetDialogUpdate.setContentView(bottomSheetViewUpdate);
                        bottomSheetDialogUpdate.show();
                    }

                });

                checkUser();

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.textColorScreenMenu));
            }

        };

        ClickableSpan clickableSpanLogout = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showAlertDialog();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.textColorScreenMenu));
            }

        };
        ClickableSpan clickableSpanSubmission = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(getContext(), SubmissionActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.textColorScreenMenu));
            }

        };

        String text = "MY ACCOUNT";
        String text2 = "LOGOUT";
        String text3 = "SUBMISSION";
        SpannableString ss = new SpannableString(text);
        SpannableString ss2 = new SpannableString(text2);
        SpannableString ss3 = new SpannableString(text3);


        ss.setSpan(clickableSpan, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(clickableSpanLogout, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss3.setSpan(clickableSpanSubmission, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editProfileBtn.setText(ss);
        editProfileBtn.setMovementMethod(LinkMovementMethod.getInstance());
        btnLogout.setText(ss2);
        btnLogout.setMovementMethod(LinkMovementMethod.getInstance());
        submissionBtn.setText(ss3);
        submissionBtn.setMovementMethod(LinkMovementMethod.getInstance());

        checkUserProfile();
        loadAllMaterial();

        int largePadding = getResources().getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_large);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_small);
        rvMaterial.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_grid)
                    .setBackgroundResource(R.drawable.shr_product_grid_background_shape);
        }

        return view;
    }

    private void loadFilteredMaterial(final CharSequence selected) {
        materialArrayList = new ArrayList<>();

        //get All material
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child("Material")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting rest list

                        materialArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            final String materialCategory = "" + dataSnapshot.child("materialCategoryPlant").getValue();

                            if (selected.equals(materialCategory)) {
                                ModelMaterial modelMaterial = dataSnapshot.getValue(ModelMaterial.class);
                                materialArrayList.add(modelMaterial);
                            }

                        }
                        //setup adapter
                        materialPlantAdapter = new MaterialPlantUserAdapter(getContext(), materialArrayList);
                        //set adapter
                        rvMaterial.setAdapter(materialPlantAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllMaterial() {
        materialArrayList = new ArrayList<>();

        //get All material
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child("Material")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting rest list
                        materialArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ModelMaterial modelMaterial = dataSnapshot.getValue(ModelMaterial.class);
                            materialArrayList.add(modelMaterial);
                        }
                        //setup adapter
                        materialPlantAdapter = new MaterialPlantUserAdapter(getContext(), materialArrayList);
                        //set adapter
                        rvMaterial.setAdapter(materialPlantAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showAlertDialog() {
//        int ALERT_DIALOG_CLOSE = 10;
//        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage, yes;

        yes = "Ya";
        dialogTitle = "Keluar";
        dialogMessage = "Apakah anda ingin keluar?";


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeMeOffline();
                    }
                })

                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.product_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.ic_planticonnavdwar),
                getContext().getResources().getDrawable(R.drawable.ic_baseline_close_24)));
    }

    private void makeMeOffline() {
        //after log in, make user online
        progressDialog.setMessage("Logging Out...");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //update value to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update successfully
                        progressDialog.show();
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
//            finish();
        } else {
            loadMyInfo();
        }
    }

    private void checkUserUpdate() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
//            finish();
        } else {
            loadMyInfoUpdate();
        }
    }

    private void checkUserProfile() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
//            finish();
        } else {
            loadMyInfoProfile();
        }
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
                            noHpTv.setText(phone);
//
//                            edtnameTv.setText(name);
//                            edtemailTv.setText(email);
//                            edtnoHpTv.setText(phone);

                            try {
                                Picasso.get()
                                        .load(profileImage)
                                        .placeholder(R.drawable.pp1)
                                        .into(profileView);
                            } catch (Exception e) {
                                profileView.setImageResource(R.drawable.pp1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyInfoUpdate() {
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

//                            nameTv.setText(name);
//                            emailTv.setText(email);
//                            noHpTv.setText(phone);
//
                            edtnameTv.setText(name);
                            edtemailTv.setText(email);
                            edtnoHpTv.setText(phone);

                            try {
                                Picasso.get()
                                        .load(profileImage)
                                        .placeholder(R.drawable.pp1)
                                        .into(profileView);
                            } catch (Exception e) {
                                profileView.setImageResource(R.drawable.pp1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyInfoProfile() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String name = "" + dataSnapshot.child("name").getValue();
                            String profileImage = "" + dataSnapshot.child("profileImage").getValue();

                            tvname.setText("Tanam yuk, " + name);
//                            noHpTv.setText(phone);
//
//                            edtnameTv.setText(name);
//                            edtemailTv.setText(email);
//                            edtnoHpTv.setText(phone);

                            try {
                                Picasso.get()
                                        .load(profileImage)
                                        .placeholder(R.drawable.pp1)
                                        .into(profileView);
                            } catch (Exception e) {
                                profileView.setImageResource(R.drawable.pp1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputData() {

        //validation
        validateUsername();
        validateNumberPhone();
        validateEmail();

        progressDialog.setMessage("Tunggu..");
        progressDialog.show();
        if (!validateUsername() | !validateNumberPhone() | !validateEmail()) {
            progressDialog.dismiss();
            return;
        }

        updateProfile();


    }

    private void updateProfile() {
        progressDialog.setMessage("Memperbarui akun..");
        progressDialog.show();

        if (image_uri == null) {
            //save info without image

            //setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("email", "" + email);
            hashMap.put("name", "" + fullName);
            hashMap.put("phone", "" + phoneNumber);

            //save to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //db updated
                            progressDialog.dismiss();
                            bottomSheetDialogUpdate.dismiss();
                            StyleableToast.makeText(getContext(), "Profile diperbarui", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed updating db
                            progressDialog.dismiss();
                            StyleableToast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            //save info with image

            //name & path with image

            String filePathandName = "profile_images/" + "" + firebaseAuth.getUid();

            //uploadImage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathandName);
            final File file = new File(SiliCompressor.with(getContext()).compress(FileUtils.getPath(getContext(), image_uri),
                    new File(getContext().getCacheDir(), "temp")));
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
                                hashMap.put("profileImage", "" + downloadImageUri);//urluploading image

                                //save to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //db updated
                                                progressDialog.dismiss();
                                                bottomSheetDialogUpdate.dismiss();
                                                StyleableToast.makeText(getContext(), "Profile diperbarui", Toast.LENGTH_SHORT).show();
                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed updating db
                                                progressDialog.dismiss();
                                                StyleableToast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }

    private boolean validateUsername() {
        fullName = edtnameTv.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            edtnameTv.setError("Field tidak boleh kosong");
            return false;
        } else if (fullName.length() > 15) {
            edtnameTv.setError("Nama terlalu panjang");
            return false;
        } else {
            edtnameTv.setError(null);
            return true;
        }
    }

    private boolean validateNumberPhone() {
        phoneNumber = edtnoHpTv.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            edtnoHpTv.setError("Field tidak boleh kosong");
            return false;
        } else {
            edtnoHpTv.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        email = edtemailTv.getText().toString().trim();
        if (email.isEmpty()) {
            edtemailTv.setError("Field tidak boleh kosong");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StyleableToast.makeText(getContext(), "Email tidak valid", R.style.exampleToast).show();
            return false;
        } else {
            edtemailTv.setError(null);
            return true;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get picked image
                image_uri = data.getData();
                //set to imageview
                profileView.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview
                profileView.setImageURI(image_uri);
            }
        }
    }

    private void showImagePickDialog() {

        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePemission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQ_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPemission() {
        ActivityCompat.requestPermissions(getActivity(), cameraPermission, CAMERA_REQ_CODE);
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
                        Toast.makeText(getContext(), "Camera permissions are necessary...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}