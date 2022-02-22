package com.example.plantkriya.model;

public class ModelSubmissionUser {

    String submissionId, submissionTime, materialTitle, materialCategoryPlant, submissionStatus,
            emailProfile, nameProfile;

    public ModelSubmissionUser() {
    }

    public ModelSubmissionUser(String submissionId, String submissionTime, String materialTitle, String materialCategoryPlant, String submissionStatus, String emailProfile, String nameProfile) {
        this.submissionId = submissionId;
        this.submissionTime = submissionTime;
        this.materialTitle = materialTitle;
        this.materialCategoryPlant = materialCategoryPlant;
        this.submissionStatus = submissionStatus;
        this.emailProfile = emailProfile;
        this.nameProfile = nameProfile;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getMaterialTitle() {
        return materialTitle;
    }

    public void setMaterialTitle(String materialTitle) {
        this.materialTitle = materialTitle;
    }

    public String getMaterialCategoryPlant() {
        return materialCategoryPlant;
    }

    public void setMaterialCategoryPlant(String materialCategoryPlant) {
        this.materialCategoryPlant = materialCategoryPlant;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public String getEmailProfile() {
        return emailProfile;
    }

    public void setEmailProfile(String emailProfile) {
        this.emailProfile = emailProfile;
    }

    public String getNameProfile() {
        return nameProfile;
    }

    public void setNameProfile(String nameProfile) {
        this.nameProfile = nameProfile;
    }
}
