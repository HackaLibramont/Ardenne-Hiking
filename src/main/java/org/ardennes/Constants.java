package org.ardennes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.InetAddress;

/**
 * Created by cvasquez on 21.03.15.
 */
public class Constants {
    public static final String INDEX = "ardennes";
    public static final String CLUSTER = "sniper";
    public static final String USER_TYPE = "user";
    public static final String TRACK_TYPE = "track";
    public static final String EVENT_TYPE = "event";
    public static final String POI_TYPE = "poi";

    /**
     * Why URIQA is a constant?
     *
     * It is not recommended to dynamically prepare the URL at run time,
     * especially based on ServletRequest. This is primarily because
     * you have no idea of the URL that users would be using to access the application -
     * the application server could be behind a web server, a firewall or a load balancer.
     */
    public static String URIQA = "http://"+ getHost()+":8080/ardennes-hicking/api";

    
    private static String getHost(){
        try{
           return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            
        }
            return "localhost";
    }
    
    
    
    public static ObjectMapper getMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return objectMapper;
    }

    public static String getEventURL(String id){
        if (id==null){
            return null;
        }
        return URIQA+"/"+EVENT_TYPE+"/"+id;
    }
    public static String getUserURL(String id){
        if (id==null){
            return null;
        }

        return URIQA+"/"+USER_TYPE+"/"+id;
    }
    public static String getTrackURL(String id){
        if (id==null){
            return null;
        }
        return URIQA+"/"+ TRACK_TYPE +"/"+id;
    }

}
