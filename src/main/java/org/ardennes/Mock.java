package org.ardennes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ardennes.pojo.app.Event;
import org.ardennes.pojo.app.User;
import org.ardennes.pojo.osm.Feature;
import org.ardennes.pojo.osm.FeatureCollection;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by cvasquez on 20.03.15.
 */
public class Mock {


    
    public static FeatureCollection getAll() throws Exception {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("3tracks.geojson");
        ObjectMapper objectMapper = Common.getMapper();
        return objectMapper.readValue(input, FeatureCollection.class);
    }
    public static Feature getFeature(String id) throws Exception {
        FeatureCollection collection = getAll();
        for(Feature current: collection.getFeatures()){
            if (current.getId().equalsIgnoreCase("way/"+id)){
                current.setEvents(Arrays.asList(getEvent("32", "44"), getEvent("32", "44"), getEvent("32", "44")));
                return current;
            }
        }
        return null;
    }
    
    public static User getUser(String id){
        User user = new User();
        user.setId(new Random().toString());
        user.setEvents(Arrays.asList(getEvent("32","44"),getEvent("32","44"),getEvent("32","44")));
        return user;
    }
    
    public static Event getEvent(String featureId,String userId){
        Event event = new Event();
        event.setEventFeatureId(featureId);
        event.setEventUserId(userId);
        event.setId(new Random().toString());
        return event;
    }

    public static Event getEvent(String userId){
        Event event = new Event();
        event.setEventUserId(userId);
        event.setId(new Random().toString());
        return event;
    }
    
}
