package com.sjsu.parknow.model;

import java.util.Date;

public class Item {
    public String id;
    public double latitude;
    public double longitude;
    public String userid;
    public String parkingstatus;
    public Date lastodifiedtimestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getParkingstatus() {
        return parkingstatus;
    }

    public void setParkingstatus(String parkingstatus) {
        this.parkingstatus = parkingstatus;
    }

    public Date getLastodifiedtimestamp() {
        return lastodifiedtimestamp;
    }

    public void setLastodifiedtimestamp(Date lastodifiedtimestamp) {
        this.lastodifiedtimestamp = lastodifiedtimestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

