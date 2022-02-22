package com.example.plantkriya.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantkriya.R;
import com.example.plantkriya.activity.plantproduct.EditMaterialActivity;
import com.example.plantkriya.filter.FilterMaterial;
import com.example.plantkriya.model.ModelMaterial;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MaterialPlantAdapter extends RecyclerView.Adapter<MaterialPlantAdapter.MaterialViewHolder>
        implements Filterable {

    public ArrayList<ModelMaterial> modelMaterialArrayList, filterList;
    private Context context;
    private FilterMaterial filterMaterial;

    public MaterialPlantAdapter(Context context, ArrayList<ModelMaterial> modelMaterialArrayList) {
        this.context = context;
        this.modelMaterialArrayList = modelMaterialArrayList;
        this.filterList = modelMaterialArrayList;
    }

    @NonNull
    @Override
    public MaterialPlantAdapter.MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_material_layout, parent, false);

        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialPlantAdapter.MaterialViewHolder holder, int position) {
        final ModelMaterial modelMaterial = modelMaterialArrayList.get(position);
        String id = modelMaterial.getMaterialId();
        String uid = modelMaterial.getUid();
        String materialIconIv = modelMaterial.getMaterialIcon();
        String materialAvailable = modelMaterial.getLevelAvailable();
        String materialTitleTv = modelMaterial.getMaterialTitle();
        String materialSubtitleTv = modelMaterial.getMaterialSubtitle();
        String materialLevelTv = modelMaterial.getMaterialLevel();
        String materialDescTv = modelMaterial.getMaterialDesc();
        String materialCategory = modelMaterial.getMaterialCategory();
        String materialCategoryPlant = modelMaterial.getMaterialCategoryPlant();
        String timestamp = modelMaterial.getTimestamp();

        holder.titleTv.setText(materialTitleTv);
        holder.subtitleTv.setText(materialSubtitleTv);
        holder.levelTv.setText(materialLevelTv);
//        holder.categoryTv.setText(materialCategory);
        if (materialAvailable.equals("true")) {
            //product level
            holder.levelTv.setVisibility(View.VISIBLE);
//            holder.subtitleTv.setPaintFlags(holder.subtitleTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            //no level
            holder.levelTv.setVisibility(View.GONE);
        }

        try {
            Picasso.get()
                    .load(materialIconIv)
                    .placeholder(R.drawable.pp1)
                    .into(holder.materialIconIv);
        } catch (Exception e) {
            holder.materialIconIv.setImageResource(R.drawable.pp1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show detail
                detailBottomSheet(modelMaterial);
            }
        });
    }

    private void detailBottomSheet(ModelMaterial modelMaterial) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_material, null,
                false);
        bottomSheetDialog.setContentView(view);

        ImageView materialPhoto = view.findViewById(R.id.material_pic);
        TextView levelText = view.findViewById(R.id.levelTv);
        final TextView titleText = view.findViewById(R.id.tv_title);
        TextView descText = view.findViewById(R.id.tv_desc);
        TextView subtitleText = view.findViewById(R.id.tv_subtitle);
        TextView categoryText = view.findViewById(R.id.tv_category);
        Button btnChange = view.findViewById(R.id.btn_change);
        Button btnDelete = view.findViewById(R.id.btn_delete);

        final String id = modelMaterial.getMaterialId();
        String uid = modelMaterial.getUid();
        String materialIconIv = modelMaterial.getMaterialIcon();
        String materialAvailable = modelMaterial.getLevelAvailable();
        final String materialTitleTv = modelMaterial.getMaterialTitle();
        String materialSubtitleTv = modelMaterial.getMaterialSubtitle();
        String materialLevelTv = modelMaterial.getMaterialLevel();
        String materialDescTv = modelMaterial.getMaterialDesc();
        String materialCategory = modelMaterial.getMaterialCategory();
        String materialCategoryPlant = modelMaterial.getMaterialCategoryPlant();
        String timestamp = modelMaterial.getTimestamp();

        titleText.setText(materialTitleTv);
        descText.setText(materialDescTv);
        levelText.setText(materialLevelTv);
        subtitleText.setText(materialSubtitleTv);
        categoryText.setText(materialCategory);

        if (materialAvailable.equals("true")) {
            //product level
            levelText.setVisibility(View.VISIBLE);
//            holder.subtitleTv.setPaintFlags(holder.subtitleTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            //no level
            levelText.setVisibility(View.GONE);
        }

        try {
            Picasso.get()
                    .load(materialIconIv)
                    .placeholder(R.drawable.pp1)
                    .into(materialPhoto);
        } catch (Exception e) {
            materialPhoto.setImageResource(R.drawable.pp1);
        }

        bottomSheetDialog.show();
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditMaterialActivity.class);
                intent.putExtra("materialId", id);
                context.startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus")
                        .setMessage("Apakah anda ingin menghapus " + materialTitleTv + " ?")
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMaterial(id);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void deleteMaterial(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Material").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        StyleableToast.makeText(context, "Dihapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        StyleableToast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    @Override
    public int getItemCount() {
        return modelMaterialArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterMaterial == null) {
            filterMaterial = new FilterMaterial(this, filterList);

        }
        return filterMaterial;
    }

    public class MaterialViewHolder extends RecyclerView.ViewHolder {

        private ImageView materialIconIv;
        private TextView titleTv, subtitleTv, levelTv, categoryTv;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);

            materialIconIv = itemView.findViewById(R.id.materialIconIv);
            titleTv = itemView.findViewById(R.id.materialTitleTv);
            subtitleTv = itemView.findViewById(R.id.materialSubtitleTv);
            levelTv = itemView.findViewById(R.id.levelTv);
//            categoryTv = itemView.findViewById(R.id.tv_category_row);
        }
    }
}
