package com.example.dtbs;

public class myTimeSlot
{
    String timetext;
    boolean timefree;
    String txtBooked;

    public String getTxtBooked() {
        return txtBooked;
    }

    public void setTxtBooked(String txtBooked) {
        this.txtBooked = txtBooked;
    }

    public myTimeSlot(String timetext, boolean timefree, String txtBooked) {
        this.timetext = timetext;
        this.timefree = timefree;
        this.txtBooked = txtBooked;
    }

    public String getTimetext() {
        return timetext;
    }

    public void setTimetext(String timetext) {
        this.timetext = timetext;
    }

    public boolean isTimefree() {
        return timefree;
    }

    public void setTimefree(boolean timefree) {
        this.timefree = timefree;
    }
}
