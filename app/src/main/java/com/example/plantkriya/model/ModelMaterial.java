package com.example.plantkriya.model;

public class ModelMaterial {
    private String materialId,
            materialTitle,
            materialDesc,
            materialCategory,
            materialSubtitle,
            materialCategoryPlant,
            materialIcon,
            materialLevel,
            levelAvailable,
            timestamp,
            uid;

    public ModelMaterial() {

    }

    public ModelMaterial(String materialId, String materialTitle, String materialDesc, String materialCategory,
                         String materialSubtitle, String materialCategoryPlant, String materialIcon,
                         String materialLevel, String levelAvailable, String timestamp, String uid) {
        this.materialId = materialId;
        this.materialTitle = materialTitle;
        this.materialDesc = materialDesc;
        this.materialCategory = materialCategory;
        this.materialSubtitle = materialSubtitle;
        this.materialCategoryPlant = materialCategoryPlant;
        this.materialIcon = materialIcon;
        this.materialLevel = materialLevel;
        this.levelAvailable = levelAvailable;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialTitle() {
        return materialTitle;
    }

    public void setMaterialTitle(String materialTitle) {
        this.materialTitle = materialTitle;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getMaterialCategory() {
        return materialCategory;
    }

    public void setMaterialCategory(String materialCategory) {
        this.materialCategory = materialCategory;
    }

    public String getMaterialSubtitle() {
        return materialSubtitle;
    }

    public void setMaterialSubtitle(String materialSubtitle) {
        this.materialSubtitle = materialSubtitle;
    }

    public String getMaterialCategoryPlant() {
        return materialCategoryPlant;
    }

    public void setMaterialCategoryPlant(String materialCategoryPlant) {
        this.materialCategoryPlant = materialCategoryPlant;
    }

    public String getMaterialIcon() {
        return materialIcon;
    }

    public void setMaterialIcon(String materialIcon) {
        this.materialIcon = materialIcon;
    }

    public String getMaterialLevel() {
        return materialLevel;
    }

    public void setMaterialLevel(String materialLevel) {
        this.materialLevel = materialLevel;
    }

    public String getLevelAvailable() {
        return levelAvailable;
    }

    public void setLevelAvailable(String levelAvailable) {
        this.levelAvailable = levelAvailable;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
