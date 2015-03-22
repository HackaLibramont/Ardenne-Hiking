package cc.ardennehiking.app.model;


import org.djodjo.json.JsonElement;

public class WikiPlace extends Place{

    @Override
    public String getTitle() {
        return getJson().optString("title");
    }

    @Override
    public String getSummary() {
        return getJson().optString("summary");
    }

    @Override
    public String getWikiLink() {
        return "http://" + getJson().getString("wikipediaUrl");
    }
    @Override
    public String getId() {
        JsonElement id = getJson().opt("geoNameId");
        if(id!=null) return id.toString();
        return getJson().getString("wikipediaUrl");
    }


}
