package com.example.ccw.fyp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class FoodInfo {

    public String foodname, foodprice, fooddesc, imageUrl, storename;

    public FoodInfo(){

    }




    public FoodInfo(String foodname, String foodprice, String fooddesc, String imageUrl, String storename ) {
        this.foodname = foodname;
        this.foodprice = foodprice;
        this.fooddesc = fooddesc;
        this.imageUrl = imageUrl;
        this.storename = storename;


    }

    public String getFoodname() {
        return foodname;
    }

    public String getFoodprice() {
        return foodprice;
    }

    public String getFooddesc() {
        return fooddesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStorename() {
        return storename;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }


}


