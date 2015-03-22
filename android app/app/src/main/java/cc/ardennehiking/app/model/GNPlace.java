package cc.ardennehiking.app.model;


import org.djodjo.json.JsonArray;
import org.djodjo.json.JsonElement;

public class GNPlace extends Place{


    private String wikiLink = null;


    @Override
    public String getTitle() {
        return getJson().optString("name");
    }

    @Override
    public String getSummary() {
        return getJson().optString("fcodeName");
    }

    @Override
    public String getWikiLink() {
        if(wikiLink==null) {
            wikiLink = findWikiLink();
        }
        return wikiLink;
    }

    private String findWikiLink() {
        String res = "";
        JsonArray altNames = getJson().optJsonArray("alternateNames");
        if(altNames!=null) {
            for(JsonElement je:altNames) {
                if(je.isJsonObject() && "link".equals(je.asJsonObject().optString("lang"))) {
                    return je.asJsonObject().optString("name");
                }
            }
        }
        return res;
    }



}
