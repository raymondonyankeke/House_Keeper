package com.java.housekeeper.model;

public class MaidModel {
    private String maidId, maidName, maidContactNumber, maidDetails, maidServices, maidPicture;
    private Double ratingValue, hourlyRate;
    private int ratingCount;


    public MaidModel() {
    }

    public MaidModel(String maidId, String maidName, String maidContactNumber, String maidDetails, String maidServices, String maidPicture, Double ratingValue, Double hourlyRate, int ratingCount) {
        this.maidId = maidId;
        this.maidName = maidName;
        this.maidContactNumber = maidContactNumber;
        this.maidDetails = maidDetails;
        this.maidServices = maidServices;
        this.maidPicture = maidPicture;
        this.ratingValue = ratingValue;
        this.hourlyRate = hourlyRate;
        this.ratingCount = ratingCount;
    }

    public String getMaidId() {
        return maidId;
    }

    public void setMaidId(String maidId) {
        this.maidId = maidId;
    }

    public String getMaidName() {
        return maidName;
    }

    public void setMaidName(String maidName) {
        this.maidName = maidName;
    }

    public String getMaidContactNumber() {
        return maidContactNumber;
    }

    public void setMaidContactNumber(String maidContactNumber) {
        this.maidContactNumber = maidContactNumber;
    }

    public String getMaidDetails() {
        return maidDetails;
    }

    public void setMaidDetails(String maidDetails) {
        this.maidDetails = maidDetails;
    }

    public String getMaidServices() {
        return maidServices;
    }

    public void setMaidServices(String maidServices) {
        this.maidServices = maidServices;
    }

    public String getMaidPicture() {
        return maidPicture;
    }

    public void setMaidPicture(String maidPicture) {
        this.maidPicture = maidPicture;
    }

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
