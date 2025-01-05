package com.bloodcircle.app.Model.Donors;

public class DonorsItem {

    public String id;
    public String name;
    public String phone;
    public String last_date;
    public int bloodGroup;
    public int district;
    public String address;

    public DonorsItem(String id, String name, String phone, String last_date, int bloodGroup, int district, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.last_date = last_date;
        this.bloodGroup = bloodGroup;
        this.district = district;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }

    public int getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(int bloodGroup) {
        this.bloodGroup = bloodGroup;
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
}