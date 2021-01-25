package com.example.authenticatorapp.Model;

public class Note {
    private String fName;
    private String Address;
    private String Details;
    private String UserId;

    public Note() {

    }

    public Note(String fName,String Address,String Details,String UserId){
        this.fName    = fName;
        this.Address = Address;
        this.Details = Details;
        this.UserId  = UserId;
    }

    public String getfName() {
        return fName;
    }
    public String getAddress() {
        return Address;
    }
    public String getDetails() {
        return Details;
    }
    public String getUserId() {
        return UserId;
    }

}

