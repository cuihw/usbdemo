package com.xdja.usbdemo.bean;

/**
 * <b>Description : </b>
 * <p>Created by <a href="mailto:fanjiandong@outlook.com">fanjiandong</a> on 2017/1/17 0:45.</p>
 */

public class Fingerprint {
    private String id;
    private String personId;
    // 实际上存储的是 指纹特征值，并不是指纹图片。
    private String image;  // base64 acturly is fingerpring feature.
    private String createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
