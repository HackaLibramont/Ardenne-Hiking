package cc.ardennehiking.app.service;


import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import org.djodjo.comm.jus.Response;
import org.djodjo.json.JsonObject;

public class GeonamesSearchJsonRequest extends GeonamesRequest {

    /**
     * Creates a new request
     *
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public GeonamesSearchJsonRequest(LatLngBounds bounds, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
        this(null, bounds, listener, errorListener);
    }

    public GeonamesSearchJsonRequest(String featureCode, LatLngBounds bounds, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
        super(
                Config.geoNamesBaseUrl + Config.geoNamesUriSearch
                        + Config.defParams
                        + Config.getBoundigboxParams(bounds.northeast.latitude,bounds.southwest.latitude,bounds.northeast.longitude,bounds.southwest.longitude)
                        + ((featureCode!=null)?"&featureCode="+featureCode:"")
                , listener, errorListener);
    }

    @Override
    protected void deliverResponse(JsonObject response) {
        Log.d("geoPlaces", "datasets result: " + response.toString());

        super.deliverResponse(response);
    }
}
