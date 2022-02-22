package com.example.plantkriya.filter;

import android.widget.Filter;

import com.example.plantkriya.adapter.MaterialPlantAdapter;
import com.example.plantkriya.model.ModelMaterial;

import java.util.ArrayList;

public class FilterMaterial extends Filter {
    private MaterialPlantAdapter materialPlantAdapter;
    private ArrayList<ModelMaterial> filterList;

    public FilterMaterial(MaterialPlantAdapter materialPlantAdapter, ArrayList<ModelMaterial> filterList) {
        this.materialPlantAdapter = materialPlantAdapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //valide data for search query
        if (constraint != null && constraint.length() > 0) {
            //search filed not empty. perform search
            //our filtered list
            ArrayList<ModelMaterial> filteredModel = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //check search by title and category
                if (filterList.get(i).getMaterialTitle().toLowerCase().contains(constraint) ||
                        filterList.get(i).getMaterialCategory().toLowerCase().contains(constraint)
                ) {
                    filteredModel.add(filterList.get(i));
                }
            }

            results.count = filteredModel.size();
            results.values = filteredModel;
        } else {
            //search filed empty. return original

            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        materialPlantAdapter.modelMaterialArrayList = (ArrayList<ModelMaterial>) results.values;
        //refresh adapter
        materialPlantAdapter.notifyDataSetChanged();
    }
}
