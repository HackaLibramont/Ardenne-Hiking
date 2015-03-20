package org.ardennes;

/**
 * Created by cvasquez on 19.03.15.
 */
public class UrlResolver {
    public static String getEventURL(String id){
        return Root.URIQA+"/entity/event/"+id;
    }
    public static String getUserURL(String id){
        return Root.URIQA+"/entity/user/"+id;
    }
    public static String getFeatureURL(String id){
        return Root.URIQA+"/entity/feature/"+id;
    }
    public static String getEntityURL(String id){
        return Root.URIQA+"/entity/"+id;
    }

}
