package com.example.ccw.fyp;

        import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Seller {

    private String email;
    private String username;
    private String pass;
    private String phone;
    private String imageUrl;

    public Seller(){

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Seller(String email, String username, String pass, String phone, String imageUrl) {
        this.email = email;
        this.username = username;
        this.pass = pass;
        this.phone = phone;
        this.imageUrl=imageUrl;

    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPass() {
        return pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
