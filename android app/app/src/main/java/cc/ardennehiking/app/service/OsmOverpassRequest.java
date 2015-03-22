package cc.ardennehiking.app.service;

import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import org.djodjo.comm.jus.AuthFailureError;
import org.djodjo.comm.jus.Cache;
import org.djodjo.comm.jus.JusError;
import org.djodjo.comm.jus.NetworkResponse;
import org.djodjo.comm.jus.ParseError;
import org.djodjo.comm.jus.Response;
import org.djodjo.comm.jus.toolbox.HttpHeaderParser;
import org.djodjo.comm.jus.toolbox.JsonRequest;
import org.djodjo.json.JsonElement;
import org.djodjo.json.JsonObject;
import org.djodjo.json.exception.JsonException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

//http://wiki.openstreetmap.org/wiki/Overpass_API/Language_Guide

/**
 * A request for retrieving a {@link org.json.JSONObject} response body at a given URL, allowing for an
 * optional {@link org.json.JSONObject} to be passed in as part of the request body.
 */
public class OsmOverpassRequest extends JsonRequest<JsonObject>
{
    //overpassoverpass.osm.rambler.ru/cgi/interpreter?data=[out:json];node[tourism](50.7,7.1,50.8,7.25);out;
    private static final String TAG = "OSM-overpass";
    /**
     * Creates a new request
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public OsmOverpassRequest(String featureCode, LatLngBounds bounds,  Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
              //  Config.osmOverpassBaseUrl + "(" + Math.min(bounds.northeast.latitude, bounds.southwest.latitude)+","+Math.min(bounds.northeast.longitude, bounds.southwest.longitude)+","+Math.max(bounds.northeast.latitude, bounds.southwest.latitude)+","+ Math.max(bounds.northeast.longitude, bounds.southwest.longitude)+ ");" + "out%20500;"
                Config.osmOverpassBaseUrl + ((featureCode==null)?"[name]":"["+featureCode+"]") + "(" + Math.min(bounds.northeast.latitude, bounds.southwest.latitude)+","+Math.min(bounds.northeast.longitude, bounds.southwest.longitude)+","+Math.max(bounds.northeast.latitude, bounds.southwest.latitude)+","+ Math.max(bounds.northeast.longitude, bounds.southwest.longitude)+ ");" + "out%20500;"
                // Config.osmOverpassBaseUrl + ((featureCode==null)?"[tourism]":"["+featureCode+"]") + "(" + Math.min(bounds.northeast.latitude, bounds.southwest.latitude)+","+Math.min(bounds.northeast.longitude, bounds.southwest.longitude)+","+Math.max(bounds.northeast.latitude, bounds.southwest.latitude)+","+ Math.max(bounds.northeast.longitude, bounds.southwest.longitude)+ ");" + "out%20500;"
                //Config.osmOverpassBaseUrl + "[tourism]" + "(" + Math.min(bounds.northeast.latitude, bounds.southwest.latitude)+","+Math.min(bounds.northeast.longitude, bounds.southwest.longitude)+","+Math.max(bounds.northeast.latitude, bounds.southwest.latitude)+","+ Math.max(bounds.northeast.longitude, bounds.southwest.longitude)+ ");" + "out%205;"
                , null
                , listener,
                errorListener);
        this.setShouldCache(true);
        Log.d(TAG, "overpass created request: " + featureCode + ", bounds: " + bounds);
    }



    @Override
    protected Response<JsonObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            // Log.d("openbrussels", "response string: " + jsonString);
            //todo possibly return totalResultsCount for basic search response
            return Response.success(JsonElement.readFrom(jsonString).asJsonObject()
                    //        .optJsonObject("geonames")
                    ,
                    parseIgnoreCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (JsonException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }



    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> res = new HashMap<String, String>();
        addHeaders(res)
                .put("User-Agent", "GeoplacesAndroid/1.0");
        return res;
    }

    private Map<String, String> addHeaders(Map<String, String> in) throws AuthFailureError {

        return in;
    }



    @Override
    public void deliverError(JusError error) {
        Log.e(TAG, "request error: " + error);
        if(error!=null && error.networkResponse != null && error.networkResponse.data != null) {
            try {
                Log.e(TAG, "request error: " + new String(error.networkResponse.data, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        super.deliverError(error);
    }

    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long cacheHitButRefreshed = 10 * 60 * 1000; // in 10 minutes cache will be hit, but also refreshed on background
        final long cacheExpired = 3 * 24 * 60 * 60 * 1000; // in 3 days this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }
}
