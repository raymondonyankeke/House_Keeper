package com.java.housekeeper.model;

public class RatingModel {
    private float ratingValue;
    private String comment;
    private String maidName;
    private String raterName; // This is the name for the current user that's logged in. He will be rating.
    private String uid;

    public RatingModel() {
    }

    public RatingModel(float ratingValue, String comment, String raterName) {
        this.ratingValue = ratingValue;
        this.comment = comment;
        this.raterName = raterName;
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMaidName() {
        return maidName;
    }

    public void setMaidName(String maidName) {
        this.maidName = maidName;
    }

    public String getRaterName() {
        return raterName;
    }

    public void setRaterName(String raterName) {
        this.raterName = raterName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
