package com.bloodcircle.app.Model.Others;

public class OthersItem {

    public String name;
    public String phone;
    public int district;
    public String address;
    public String imgUrl;

    public OthersItem(String name, String phone, int district, String address, String imgUrl) {
        this.name = name;
        this.phone = phone;
        this.district = district;
        this.address = address;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
