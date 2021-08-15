
package com.sjsu.parknow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
@JsonPropertyOrder({
    "results"
})
@Generated("jsonschema2pojo")
public class SpotsResponse implements Serializable, Parcelable
{

    @JsonProperty("results")
    private ArrayList<SpotResult> results = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Creator<SpotsResponse> CREATOR = new Creator<SpotsResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public SpotsResponse createFromParcel(android.os.Parcel in) {
            return new SpotsResponse(in);
        }

        public SpotsResponse[] newArray(int size) {
            return (new SpotsResponse[size]);
        }

    };
    private final static long serialVersionUID = -5214154942861840481L;

    protected SpotsResponse(android.os.Parcel in) {
        in.readList(this.results, (com.sjsu.parknow.model.SpotResult.class.getClassLoader()));
        this.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
    }

    public SpotsResponse() {
    }

    @JsonProperty("results")
    public ArrayList<SpotResult> getResults() {
        return results;
    }

    @JsonProperty("results")
    public void setResults(ArrayList<SpotResult> results) {
        this.results = results;
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
        dest.writeList(results);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
