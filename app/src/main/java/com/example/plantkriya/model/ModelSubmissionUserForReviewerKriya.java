package com.example.plantkriya.model;

public class ModelSubmissionUserForReviewerKriya {

    String submissionId, submissionTime, materialTitle, materialCategoryKriya, submissionStatus,
            emailProfile, nameProfile;

    public ModelSubmissionUserForReviewerKriya() {
    }

    public ModelSubmissionUserForReviewerKriya(String submissionId, String submissionTime, String materialTitle, String materialCategoryKriya, String submissionStatus, String emailProfile, String nameProfile) {
        this.submissionId = submissionId;
        this.submissionTime = submissionTime;
        this.materialTitle = materialTitle;
        this.materialCategoryKriya = materialCategoryKriya;
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

    public String getMaterialCategoryKriya() {
        return materialCategoryKriya;
    }

    public void setMaterialCategoryKriya(String materialCategoryKriya) {
        this.materialCategoryKriya = materialCategoryKriya;
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
