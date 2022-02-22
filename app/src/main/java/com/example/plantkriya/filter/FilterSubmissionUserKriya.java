package com.example.plantkriya.filter;

import android.widget.Filter;

import com.example.plantkriya.adapter.AdapterSubmissionUserKriya;
import com.example.plantkriya.model.ModelSubmissionUserKriya;

import java.util.ArrayList;

public class FilterSubmissionUserKriya extends Filter {
    private AdapterSubmissionUserKriya materialPlantAdapter;
    private ArrayList<ModelSubmissionUserKriya> filterList;

    public FilterSubmissionUserKriya(AdapterSubmissionUserKriya materialPlantAdapter, ArrayList<ModelSubmissionUserKriya> filterList) {
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
            ArrayList<ModelSubmissionUserKriya> filteredModel = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //check search by title and category
                if (filterList.get(i).getSubmissionStatus().toLowerCase().contains(constraint)
                        || filterList.get(i).getMaterialCategoryKriya().toLowerCase().contains(constraint)
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
        materialPlantAdapter.submissionUserArrayList = (ArrayList<ModelSubmissionUserKriya>) results.values;
        //refresh adapter
        materialPlantAdapter.notifyDataSetChanged();
    }
}
