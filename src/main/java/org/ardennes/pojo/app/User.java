package org.ardennes.pojo.app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by cvasquez on 20.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @JsonProperty("href")
    private String getHref(){
        return org.ardennes.UrlResolver.getUserURL(getId());
    }
    
    private String type="User";
    private String id;
    private List<Event> events;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
