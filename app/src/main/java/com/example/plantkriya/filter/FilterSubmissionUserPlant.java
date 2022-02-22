package com.example.plantkriya.filter;

import android.widget.Filter;

import com.example.plantkriya.adapter.AdapterSubmissionUser;
import com.example.plantkriya.model.ModelSubmissionUser;

import java.util.ArrayList;

public class FilterSubmissionUserPlant extends Filter {
    private AdapterSubmissionUser materialPlantAdapter;
    private ArrayList<ModelSubmissionUser> filterList;

    public FilterSubmissionUserPlant(AdapterSubmissionUser materialPlantAdapter, ArrayList<ModelSubmissionUser> filterList) {
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
            ArrayList<ModelSubmissionUser> filteredModel = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //check search by title and category
                if (filterList.get(i).getSubmissionStatus().toLowerCase().contains(constraint)
                        || filterList.get(i).getMaterialCategoryPlant().toLowerCase().contains(constraint)
                        || filterList.get(i).getMaterialTitle().toLowerCase().contains(constraint)
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
        materialPlantAdapter.submissionUserArrayList = (ArrayList<ModelSubmissionUser>) results.values;
        //refresh adapter
        materialPlantAdapter.notifyDataSetChanged();
    }
}
