package com.example.dtbs;

public class ListElement {
    public String color;
    public String name;
    public String city;
    public String status;

    public String getSlotname() {
        return slotname;
    }

    public String slotname;


    public void setSlotname(String slotname) {
        this.slotname = slotname;
    }

    public ListElement(String color, String name, String city, String status, String slotname) {
        this.color = color;
        this.name = name;
        this.city = city;
        this.status = status;
        this.slotname = slotname;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}