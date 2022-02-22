package com.example.plantkriya.filter;

import android.widget.Filter;

import com.example.plantkriya.adapter.MaterialKriyaAdapter;
import com.example.plantkriya.model.ModelMaterialKriya;

import java.util.ArrayList;

public class FilterMaterialKriya extends Filter {
    private MaterialKriyaAdapter materialPlantAdapter;
    private ArrayList<ModelMaterialKriya> filterList;

    public FilterMaterialKriya(MaterialKriyaAdapter materialPlantAdapter, ArrayList<ModelMaterialKriya> filterList) {
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
            ArrayList<ModelMaterialKriya> filteredModel = new ArrayList<>();
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
        materialPlantAdapter.modelMaterialArrayList = (ArrayList<ModelMaterialKriya>) results.values;
        //refresh adapter
        materialPlantAdapter.notifyDataSetChanged();
    }
}
