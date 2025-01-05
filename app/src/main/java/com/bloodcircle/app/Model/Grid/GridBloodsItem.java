package com.bloodcircle.app.Model.Grid;

public class GridBloodsItem {

    public GridBloodsItem(String title, String color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private String title;
    private String color;
}
