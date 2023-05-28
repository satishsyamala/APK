package com.example.wms.listview;

public class SubjectData {
    private String string1;
    private String string2;
    private int quantity;
    private String Image;
    private int imgid;
    private int colourId;
    private int redirectId;

    public SubjectData(String string1, int imgid) {
        this.string1 = string1;
        this.imgid = imgid;

    }

    public SubjectData(String string1, int imgid,int colourId,int redirectId) {
        this.string1 = string1;
        this.imgid = imgid;
        this.colourId=colourId;
        this.redirectId=redirectId;

    }

    public SubjectData(String string1, String string2) {
        this.string1 = string1;
        this.string2 = string2;

    }

    public SubjectData(String string1, String string2, int quantity) {
        this.string1 = string1;
        this.string2 = string2;
        this.quantity = quantity;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }

    public int getColourId() {
        return colourId;
    }

    public void setColourId(int colourId) {
        this.colourId = colourId;
    }

    public int getRedirectId() {
        return redirectId;
    }

    public void setRedirectId(int redirectId) {
        this.redirectId = redirectId;
    }
}
