package com.example.plantkriya.listener;

import com.example.plantkriya.model.ModelMaterialDetail;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadSuccess(List<ModelMaterialDetail> modelMaterialDetailList);

    void onFirebaseLoadFailed(String message);
}
