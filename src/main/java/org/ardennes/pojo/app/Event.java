package org.ardennes.pojo.app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ardennes.Common;

import java.util.Date;

/**
 * Created by cvasquez on 20.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {

    @JsonProperty("URL")
    private String getHref(){
        return Common.getEventURL(getId());
    }
    private String id;
    private String type="Event";
    Date eventCreationDate;
    @JsonProperty("userURL")
    private String getUserHref(){
        return Common.getUserURL(getEventUserId());
    }
    String eventUserId;
    @JsonProperty("featureURL")
    private String getFeatureHref(){
        return Common.getEventURL(getId());
    }
    String eventFeatureId;
    EventEnum eventType;
    String eventValue;
    Double[] coordinates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getEventCreationDate() {
        return eventCreationDate;
    }

    public void setEventCreationDate(Date eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
    }

    public String getEventUserId() {
        return eventUserId;
    }

    public void setEventUserId(String eventUserId) {
        this.eventUserId = eventUserId;
    }

    public String getEventFeatureId() {
        return eventFeatureId;
    }

    public void setEventFeatureId(String eventFeatureId) {
        this.eventFeatureId = eventFeatureId;
    }

    public EventEnum getEventType() {
        return eventType;
    }

    public void setEventType(EventEnum eventType) {
        this.eventType = eventType;
    }

    public String getEventValue() {
        return eventValue;
    }

    public void setEventValue(String eventValue) {
        this.eventValue = eventValue;
    }

    public Double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Double[] coordinates) {
        this.coordinates = coordinates;
    }
}
