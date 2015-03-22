package cc.ardennehiking.app.model;


import org.djodjo.json.JsonObject;

public class OSMNode extends Place{

    @Override
    public Double getLon() {
        return getJson().getDouble("lon");
    }

    public JsonObject getTags() {
        return getJson().optJsonObject("tags");
    }

    @Override
    public String getTitle() {
        JsonObject tags = getTags();
        String title = null;
        if(tags!=null) {
             title = tags.optString("name");
            if (title == null || title.isEmpty()) {
                title = getJson().optString("type");
            }
        }
        return title;
    }

    @Override
    public String getSummary() {
        return getTitle();
       // return getTags().toString();
    }

    @Override
    public String getWikiLink() {
        return null;
    }

    @Override
    public String getId() {
        return getJson().get("id").toString();
    }


}
