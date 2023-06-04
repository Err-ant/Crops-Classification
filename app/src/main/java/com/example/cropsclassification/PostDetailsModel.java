package com.example.cropsclassification;

public class PostDetailsModel {

    public String imageUploadId, imageURL;
    public String userID, userName, currDateTime, predictionResult, uploadLocation;
    int numberOfReact, rating;

    public PostDetailsModel(){}

    public PostDetailsModel(String imageUploadId, String url, String userID, String userName, String currDateTime, String predictionResult, String uploadLocation, int numberOfReact, int rating) {

        this.imageUploadId = imageUploadId;
        this.imageURL = url;
        this.userID = userID;
        this.userName = userName;
        this.currDateTime = currDateTime;
        this.predictionResult = predictionResult;
        this.uploadLocation = uploadLocation;
        this.numberOfReact = numberOfReact;
        this.rating = rating;

    }

    public String getImageUploadId() {
        return imageUploadId;
    }

    public void setImageUploadId(String imageUploadId) {
        this.imageUploadId = imageUploadId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrDateTime() {
        return currDateTime;
    }

    public void setCurrDateTime(String currDateTime) {
        this.currDateTime = currDateTime;
    }

    public String getPredictionResult() {
        return predictionResult;
    }

    public void setPredictionResult(String predictionResult) {
        this.predictionResult = predictionResult;
    }

    public String getUploadLocation() {
        return uploadLocation;
    }

    public void setUploadLocation(String uploadLocation) {
        this.uploadLocation = uploadLocation;
    }

    public int getNumberOfReact() {
        return numberOfReact;
    }

    public void setNumberOfReact(int numberOfReact) {
        this.numberOfReact = numberOfReact;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
