
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
    "compound_code",
    "global_code"
})
@Generated("jsonschema2pojo")
public class PlusCode {

    @JsonProperty("compound_code")
    private String compoundCode;
    @JsonProperty("global_code")
    private String globalCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("compound_code")
    public String getCompoundCode() {
        return compoundCode;
    }

    @JsonProperty("compound_code")
    public void setCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
    }

    @JsonProperty("global_code")
    public String getGlobalCode() {
        return globalCode;
    }

    @JsonProperty("global_code")
    public void setGlobalCode(String globalCode) {
        this.globalCode = globalCode;
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
        sb.append(PlusCode.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("compoundCode");
        sb.append('=');
        sb.append(((this.compoundCode == null)?"<null>":this.compoundCode));
        sb.append(',');
        sb.append("globalCode");
        sb.append('=');
        sb.append(((this.globalCode == null)?"<null>":this.globalCode));
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
