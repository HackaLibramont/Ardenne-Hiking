package org.ardennes.pojo.app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ardennes.Constants;
import org.ardennes.pojo.osm.Feature;

import java.util.List;
import java.util.Map;

/**
 * Created by cvasquez on 21.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Track {
    private String type="Track";

    public String getType() {
        return type;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("URL")
    public String getHref(){
        return Constants.getTrackURL(getId());
    }
    
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    private List<Event> events;
    public List<Event> getEvents() {
        return events;
    }
    public List<Map<String,String>> pois;
    public Map<String, String> info;

    public List<Map<String, String>> getPois() {
        return pois;
    }

    public void setPois(List<Map<String, String>> pois) {
        this.pois = pois;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

}
