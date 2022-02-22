package com.example.plantkriya.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantkriya.R;
import com.example.plantkriya.activity.plantproduct.DetailMaterialPlantActivity;
import com.example.plantkriya.filter.FilterUserMaterial;
import com.example.plantkriya.model.ModelMaterial;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MaterialPlantUserAdapter extends RecyclerView.Adapter<MaterialPlantUserAdapter.MaterialViewHolder>
        implements Filterable {

    public ArrayList<ModelMaterial> modelMaterialsArrayList, filterList;
    private Context context;
    private FilterUserMaterial filterMaterial;

    public MaterialPlantUserAdapter(Context context, ArrayList<ModelMaterial> modelMaterialArrayList) {
        this.context = context;
        this.modelMaterialsArrayList = modelMaterialArrayList;
        this.filterList = modelMaterialArrayList;
    }

    @NonNull
    @Override
    public MaterialPlantUserAdapter.MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_material_layout, parent, false);

        return new MaterialViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialPlantUserAdapter.MaterialViewHolder holder, int position) {
        final ModelMaterial modelMaterial = modelMaterialsArrayList.get(position);
        final String id = modelMaterial.getMaterialId();
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
                Intent intent = new Intent(context, DetailMaterialPlantActivity.class);
                intent.putExtra("materialId", id);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return modelMaterialsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterMaterial == null) {
            filterMaterial = new FilterUserMaterial(this, filterList);

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
