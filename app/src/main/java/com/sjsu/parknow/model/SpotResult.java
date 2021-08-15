
package com.sjsu.parknow.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Parcelable.Creator;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "distance", "latitude", "longitude" })
@Generated("jsonschema2pojo")
public class SpotResult implements Serializable, Parcelable {

    @JsonProperty("name")
    private String name;
    @JsonProperty("distance")
    private String distance;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Creator<SpotResult> CREATOR = new Creator<SpotResult>() {

        @SuppressWarnings({ "unchecked" })
        public SpotResult createFromParcel(android.os.Parcel in) {
            return new SpotResult(in);
        }

        public SpotResult[] newArray(int size) {
            return (new SpotResult[size]);
        }

    };
    private final static long serialVersionUID = 6217929481845529841L;

    protected SpotResult(android.os.Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((String) in.readValue((String.class.getClassLoader())));
        this.latitude = ((String) in.readValue((String.class.getClassLoader())));
        this.longitude = ((String) in.readValue((String.class.getClassLoader())));
        this.additionalProperties = ((Map<String, Object>) in.readValue((Map.class.getClassLoader())));
    }

    public SpotResult() {
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("distance")
    public String getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(String distance) {
        this.distance = distance;
    }

    @JsonProperty("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(distance);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return 0;
    }

}
