package cc.ardennehiking.app.model;


import org.djodjo.json.wrapper.JsonObjectWrapper;

public abstract class Place extends JsonObjectWrapper{

    public String getId() {
        return getJson().getString("geonameId", false);
    }

    public abstract String getTitle();


    public abstract String getSummary();

    public abstract String getWikiLink();

    public Double getLat() {
        return getJson().getDouble("lat", false);
    }

    public Double getLon() {
        return getJson().getDouble("lng", false);
    }

    public String getLang() {
        return getJson().optString("lang");
    }

}
