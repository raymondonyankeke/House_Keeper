package com.java.housekeeper.admin;

public class AdminModel {
    private String uid, email, password;
    private boolean isAdmin;


    public AdminModel() {
    }

    public AdminModel(String uid, String email, String password, boolean isAdmin) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
