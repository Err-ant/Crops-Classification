package com.example.cropsclassification;

public class RatingModel {

    String ratingUserid, ratingUserImage, ratingText, ratingUserName;
    int givenRating;

    public RatingModel() {
    }

    public RatingModel(String ratingUserid, String ratingUserImage, String ratingText, String ratingUserName, int givenRat) {

        this.ratingUserid = ratingUserid;
        this.ratingUserImage = ratingUserImage;
        this.ratingText = ratingText;
        this.ratingUserName = ratingUserName;
        this.givenRating = givenRat;
    }

    public String getRatingUserid() {
        return ratingUserid;
    }

    public void setRatingUserid(String ratingUserid) {
        this.ratingUserid = ratingUserid;
    }

    public String getRatingUserImage() {
        return ratingUserImage;
    }

    public void setRatingUserImage(String ratingUserImage) {
        this.ratingUserImage = ratingUserImage;
    }

    public String getRatingText() {
        return ratingText;
    }

    public void setRatingText(String ratingText) {
        this.ratingText = ratingText;
    }

    public String getRatingUserName() {
        return ratingUserName;
    }

    public void setRatingUserName(String ratingUserName) {
        this.ratingUserName = ratingUserName;
    }

    public int getGivenRating() {
        return givenRating;
    }

    public void setGivenRating(int givenRat) {
        this.givenRating = givenRat;
    }
}
