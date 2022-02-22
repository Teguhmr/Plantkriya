package com.example.plantkriya.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantkriya.R;
import com.example.plantkriya.activity.submissionplant.DetailSubmissionActivity;
import com.example.plantkriya.filter.FilterSubmissionUserPlant;
import com.example.plantkriya.model.ModelSubmissionUser;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterSubmissionUser extends RecyclerView.Adapter<AdapterSubmissionUser.HolderSubmissionUser>
        implements Filterable {

    public ArrayList<ModelSubmissionUser> submissionUserArrayList, filterList;
    private Context context;
    private FilterSubmissionUserPlant filter;


    public AdapterSubmissionUser(Context context, ArrayList<ModelSubmissionUser> submissionUserArrayList) {
        this.context = context;
        this.submissionUserArrayList = submissionUserArrayList;
        this.filterList = submissionUserArrayList;

    }

    @NonNull
    @Override
    public HolderSubmissionUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_submission_usere, parent, false);
        return new HolderSubmissionUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSubmissionUser holder, int position) {
        ModelSubmissionUser modelSubmissionUser = submissionUserArrayList.get(position);
        final String submissionId = modelSubmissionUser.getSubmissionId();
        String dateSubmission = modelSubmissionUser.getSubmissionTime();
        String titleSubmission = modelSubmissionUser.getMaterialTitle();
        String categorySubmission = modelSubmissionUser.getMaterialCategoryPlant();
        String statusSubmission = modelSubmissionUser.getSubmissionStatus();

        holder.tv_submissionId.setText("ID : " + submissionId);
        holder.title_tv.setText(titleSubmission);
        holder.tv_category_plant.setText("Kategori : " + categorySubmission);
        holder.status_tv.setText(statusSubmission);

        if (statusSubmission.equals("Diproses")) {
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.blue_active));
        } else if (statusSubmission.equals("Diterima")) {
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.green_active));
        } else if (statusSubmission.equals("Ditolak")) {
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.red_active));
        }

        //convert Time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(dateSubmission));
        String formattedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.date_tv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailSubmissionActivity.class);
                intent.putExtra("submissionId", submissionId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return submissionUserArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            //init filter
            filter = new FilterSubmissionUserPlant(this, filterList);
        }
        return filter;
    }


    class HolderSubmissionUser extends RecyclerView.ViewHolder {

        private TextView tv_submissionId, date_tv, title_tv, tv_category_plant, status_tv;

        public HolderSubmissionUser(@NonNull View itemView) {
            super(itemView);

            tv_submissionId = itemView.findViewById(R.id.tv_submissionId);
            date_tv = itemView.findViewById(R.id.date_tv);
            title_tv = itemView.findViewById(R.id.title_tv);
            tv_category_plant = itemView.findViewById(R.id.tv_category_plant);
            status_tv = itemView.findViewById(R.id.status_tv);
        }
    }
}
