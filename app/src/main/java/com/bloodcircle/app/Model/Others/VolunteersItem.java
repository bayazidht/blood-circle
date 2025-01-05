package com.bloodcircle.app.Model.Others;

public class VolunteersItem {
    public String name;
    public String phone;
    public int bloodGroup;
    public int district;
    public String address;

    public VolunteersItem(String name, String phone, int bloodGroup, int district, String address) {
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.district = district;
        this.address = address;
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
