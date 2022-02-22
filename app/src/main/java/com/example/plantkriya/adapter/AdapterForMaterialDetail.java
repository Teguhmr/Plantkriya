package com.example.plantkriya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.plantkriya.R;
import com.example.plantkriya.model.ModelMaterialDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterForMaterialDetail extends PagerAdapter {

    Context context;
    List<ModelMaterialDetail> detailList;
    LayoutInflater inflater;

    public AdapterForMaterialDetail(Context context, List<ModelMaterialDetail> detailList) {
        this.context = context;
        this.detailList = detailList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return detailList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final ModelMaterialDetail modelMaterial = detailList.get(position);
        String materialIconIv = modelMaterial.getImage();


        View view = inflater.inflate(R.layout.view_pager_detail, container, false);

        ImageView imgDetail = view.findViewById(R.id.detail_material_plant);


        try {
            Picasso.get()
                    .load(materialIconIv)
                    .into(imgDetail);
        } catch (Exception e) {
            imgDetail.setImageResource(R.drawable.pp1);
        }

        container.addView(view);
        return view;
    }
}
