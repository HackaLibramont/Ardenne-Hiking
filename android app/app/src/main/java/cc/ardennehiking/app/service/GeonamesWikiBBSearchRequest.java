package cc.ardennehiking.app.service;


import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import org.djodjo.comm.jus.Response;
import org.djodjo.json.JsonObject;

public class GeonamesWikiBBSearchRequest extends GeonamesRequest {

    /**
     * Creates a new request
     *
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public GeonamesWikiBBSearchRequest(LatLngBounds bounds, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
        super(
                Config.geoNamesBaseUrl + Config.geoNamesUriWikiBBSearch
                        + Config.defParams
                        + Config.getBoundigboxParams(bounds.northeast.latitude,bounds.southwest.latitude,bounds.northeast.longitude,bounds.southwest.longitude)
                , listener, errorListener);
    }

    @Override
    protected void deliverResponse(JsonObject response) {
        Log.d("geoplaces", "GeonamesWikiBBSearchRequest:: search result: " + response.toString());

        super.deliverResponse(response);
    }

}
