
package com.sjsu.parknow.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "northeast",
    "southwest"
})
@Generated("jsonschema2pojo")
public class Viewport {

    @JsonProperty("northeast")
    private Northeast northeast;
    @JsonProperty("southwest")
    private Southwest southwest;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("northeast")
    public Northeast getNortheast() {
        return northeast;
    }

    @JsonProperty("northeast")
    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    @JsonProperty("southwest")
    public Southwest getSouthwest() {
        return southwest;
    }

    @JsonProperty("southwest")
    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Viewport.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("northeast");
        sb.append('=');
        sb.append(((this.northeast == null)?"<null>":this.northeast));
        sb.append(',');
        sb.append("southwest");
        sb.append('=');
        sb.append(((this.southwest == null)?"<null>":this.southwest));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
