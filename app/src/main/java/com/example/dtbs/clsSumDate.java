package com.example.dtbs;

public class clsSumDate {
    private String txtDate;
    private int percent;
    private int num;

    public clsSumDate(String txtDate, int percent, int num) {
        this.txtDate = txtDate;
        this.percent = percent;
        this.num = num;
    }

    public String getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(String txtDate) {
        this.txtDate = txtDate;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
