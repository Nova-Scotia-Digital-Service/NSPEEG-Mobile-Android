package com.ibm.emergencyapp.persistence;

/**
 * Created by tara on 10/19/2017.
 */

public class Notification {

    public int id;
    public String date;
    public String content;


    public Notification(String date, String content) {
        this.date = date;
        this.content = content;
    }
    public Notification(int id, String date, String content) {
        this.date = date;
        this.content = content;
        this.id = id;
    }


}
