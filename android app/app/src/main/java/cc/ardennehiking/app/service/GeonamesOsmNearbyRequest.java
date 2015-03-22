package cc.ardennehiking.app.service;


import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import org.djodjo.comm.jus.Response;
import org.djodjo.json.JsonObject;

public class GeonamesOsmNearbyRequest extends GeonamesRequest {

    /**
     * Creates a new request
     *
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public GeonamesOsmNearbyRequest(LatLngBounds bounds, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {

        super(
                Config.geoNamesBaseUrl + Config.geoNamesUriOSMNearby
                        + Config.defParams2
                        + "&lat=" + bounds.getCenter().latitude + "&lng=" + bounds.getCenter().longitude
                        //max is 1 :(
                        + "&radius=" + "1"
                , listener, errorListener);
    }

    @Override
    protected void deliverResponse(JsonObject response) {
        Log.d("geoplaces", "GeonamesOsmNearbyRequest:: search result: " + response.toString());

        super.deliverResponse(response);
    }

}
