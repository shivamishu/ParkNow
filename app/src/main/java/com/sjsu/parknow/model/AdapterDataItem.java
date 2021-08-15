package com.sjsu.parknow.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class AdapterDataItem implements Serializable, Parcelable {

    private String name;
    private String address;
    private String distance;
    private String latitude;
    private String longitude;
    private Boolean openNow;
    private Float rating;
    private String source;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Creator<AdapterDataItem> CREATOR = new Creator<AdapterDataItem>() {


        @SuppressWarnings({
                "unchecked"
        })
        public AdapterDataItem createFromParcel(android.os.Parcel in) {
            return new AdapterDataItem(in);
        }

        public AdapterDataItem[] newArray(int size) {
            return (new AdapterDataItem[size]);
        }

    };
    private final static long serialVersionUID = 2880401182914272932L;

    protected AdapterDataItem(android.os.Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((String) in.readValue((String.class.getClassLoader())));
        this.latitude = ((String) in.readValue((String.class.getClassLoader())));
        this.longitude = ((String) in.readValue((String.class.getClassLoader())));
        this.openNow = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.rating = ((Float) in.readValue((Double.class.getClassLoader())));
        this.source = ((String) in.readValue((String.class.getClassLoader())));
        this.additionalProperties = ((Map<String, Object>) in.readValue((Map.class.getClassLoader())));
    }

    public AdapterDataItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(address);
        dest.writeValue(distance);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(openNow);
        dest.writeValue(rating);
        dest.writeValue(source);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return 0;
    }

}