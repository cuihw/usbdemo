package com.xdja.usbdemo.bean;


public class PersonBean {
    private String surname;
    private String name;
    private String sex;
    private String nationality;
    private String idNo;
    private String dob;
    private String country;
    private String status;
    private String image;  // base64
    private Fingerprint fingerprint;
    private String id;
    private String createdDate;
    
    private String image_path;  // path
    private String address;  //
    private String fingerPirnt_id;  //
    
    public String getFingerPirnt_id() {
        return fingerPirnt_id;
    }


    
    public void setFingerPirnt_id(String fingerPirnt_id) {
        this.fingerPirnt_id = fingerPirnt_id;
    }


    public String getImage_path() {
        return image_path;
    }

    
    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    
    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getSurname() {
        return surname;
    }

    
    public void setSurname(String surname) {
        this.surname = surname;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getSex() {
        return sex;
    }

    
    public void setSex(String sex) {
        this.sex = sex;
    }

    
    public String getNationality() {
        return nationality;
    }

    
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    
    public String getIdNo() {
        return idNo;
    }

    
    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    
    public String getDob() {
        return dob;
    }

    
    public void setDob(String dob) {
        this.dob = dob;
    }

    
    public String getCountry() {
        return country;
    }

    
    public void setCountry(String country) {
        this.country = country;
    }

    
    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status = status;
    }

    
    public String getImage() {
        return image;
    }

    
    public void setImage(String image) {
        this.image = image;
    }

    
    public Fingerprint getFingerprint() {
        return fingerprint;
    }

    
    public void setFingerprint(Fingerprint fingerprint) {
        this.fingerprint = fingerprint;
    }


}
