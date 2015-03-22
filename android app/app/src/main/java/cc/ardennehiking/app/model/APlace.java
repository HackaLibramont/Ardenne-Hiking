package cc.ardennehiking.app.model;


import org.djodjo.json.JsonArray;
import org.djodjo.json.JsonElement;

public class APlace extends Place{

    @Override
    public String getTitle() {
        JsonArray names = getJson().getJsonArray("name");
        for(JsonElement name:names) {
            if(name.asJsonObject().getString("lg").equals("fr")) {
                return name.asJsonObject().getString("content");
            }
        }
        return "POI";
    }

    @Override
    public String getSummary() {
        JsonArray names = getJson().optJsonArray("desc");
        if(names==null) return "";
        for(JsonElement name:names) {
            if(name.asJsonObject().getString("lg").equals("fr")) {
                return name.asJsonObject().getString("content");
            }
        }
        return "";
    }

    @Override
    public String getWikiLink() {
        return null;
    }

    @Override
    public String getId() {
        return getJson().getInt("id").toString();
    }

    @Override
    public Double getLat() {
        if(getJson().optJsonArray("location")==null) return null;
        return getJson().getJsonArray("location").getDouble(0);
    }

    @Override
    public Double getLon() {
        if(getJson().optJsonArray("location")==null) return null;
        return getJson().getJsonArray("location").getDouble(1);
    }

    public String getLang() {
        return null;
    }

    public String getImage() {
        return "";
    }

    public String getCategory() {
        return "";
    }
}
