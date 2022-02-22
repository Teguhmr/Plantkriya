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
import com.example.plantkriya.activity.DetailSubmissionReviewerActivity;
import com.example.plantkriya.filter.FilterSubmissionReviewer;
import com.example.plantkriya.model.ModelSubmissionUserForReviewer;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterSubmissionUserForReviewer extends RecyclerView.Adapter<AdapterSubmissionUserForReviewer.HolderSubmissionReviewer>
        implements Filterable {

    public ArrayList<ModelSubmissionUserForReviewer> submissionUserForReviewerArrayList, filterList;
    private Context context;
    private FilterSubmissionReviewer filter;

    public AdapterSubmissionUserForReviewer(Context context, ArrayList<ModelSubmissionUserForReviewer> submissionUserForReviewerArrayList) {
        this.context = context;
        this.submissionUserForReviewerArrayList = submissionUserForReviewerArrayList;
        this.filterList = submissionUserForReviewerArrayList;
    }

    @NonNull
    @Override
    public HolderSubmissionReviewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_reviewer, parent, false);
        return new HolderSubmissionReviewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSubmissionReviewer holder, int position) {
        ModelSubmissionUserForReviewer modelSubmissionUserForReviewer = submissionUserForReviewerArrayList.get(position);
        final String submissionId = modelSubmissionUserForReviewer.getSubmissionId();
        String date = modelSubmissionUserForReviewer.getSubmissionTime();
        String title = modelSubmissionUserForReviewer.getMaterialTitle();
        String email = modelSubmissionUserForReviewer.getEmailProfile();
        String category = modelSubmissionUserForReviewer.getMaterialCategoryPlant();
        String status = modelSubmissionUserForReviewer.getSubmissionStatus();

        holder.tv_submissionId.setText("ID : " + submissionId);
        holder.title_tv.setText(title);
        holder.tv_category_plant.setText("Kategori : " + category);
        holder.status_tv.setText(status);
        holder.tv_email.setText(email);

        if (status.equals("Diproses")) {
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.blue_active));
        } else if (status.equals("Diterima")) {
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.green_active));
        } else if (status.equals("Ditolak")) {
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.red_active));
        }

        //convert Time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        String formattedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.date_tv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailSubmissionReviewerActivity.class);
                intent.putExtra("submissionId", submissionId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return submissionUserForReviewerArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            //init filter
            filter = new FilterSubmissionReviewer(this, filterList);
        }
        return filter;
    }

    class HolderSubmissionReviewer extends RecyclerView.ViewHolder {
        private TextView tv_submissionId, date_tv, title_tv, tv_email, tv_category_plant, status_tv;

        public HolderSubmissionReviewer(@NonNull View itemView) {
            super(itemView);

            tv_submissionId = itemView.findViewById(R.id.tv_submissionId);
            date_tv = itemView.findViewById(R.id.date_tv);
            title_tv = itemView.findViewById(R.id.title_tv);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_category_plant = itemView.findViewById(R.id.tv_category_plant);
            status_tv = itemView.findViewById(R.id.status_tv);
        }
    }
}
