package org.openthings;

/**
 * Created by cvasquez on 19.03.15.
 */
public class UrlResolver {
    public static String getOsmEntityURL(String id){
        return Root.URIQA+"/detail/"+id;
    }
}
