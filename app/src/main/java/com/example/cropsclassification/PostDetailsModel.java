package com.example.cropsclassification;

public class PostDetailsModel {

    public String imageURL;
    public String userID, userName, predictionResult, uploadLocation;
    int numberOfReact, rating;
    public PostDetailsModel(){}

    public PostDetailsModel(String name, String url, String userID, String userName, String uploadLocation, int numberOfReact, int rating) {
        this.predictionResult = name;
        this.imageURL = url;
        this.userID = userID;
        this.userName = userName;
        this.uploadLocation = uploadLocation;
        this.numberOfReact = numberOfReact;
        this.rating = rating;

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
