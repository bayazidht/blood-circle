package com.bloodcircle.app.Model.Requests;

public class RequestsItem {

    public String name;
    public String medical;
    public String phone;
    public int bloodGroup;
    public String unit;
    public int type;
    public String date;
    public String time;
    public int district;
    public String address;
    public String details;
    public String email;
    public String uploadedTime;

    public RequestsItem(String name, String medical, String phone, int bloodGroup, String unit, int type, String date, String time, int district, String address, String details, String email, String uploadedTime) {
        this.name = name;
        this.medical = medical;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.unit = unit;
        this.type = type;
        this.date = date;
        this.time = time;
        this.district = district;
        this.address = address;
        this.details = details;
        this.email = email;
        this.uploadedTime = uploadedTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedical() {
        return medical;
    }

    public void setMedical(String medical) {
        this.medical = medical;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(int bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(String uploadedTime) {
        this.uploadedTime = uploadedTime;
    }
}