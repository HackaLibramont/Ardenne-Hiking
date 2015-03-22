package cc.ardennehiking.app.model;

import org.djodjo.json.JsonObject;
import org.djodjo.json.wrapper.JsonObjectWrapper;

public class Track extends JsonObjectWrapper {

    public JsonObject getInfo() {
        return getJson().getJsonObject("info");
    }

    public String getTitle() {
        return getInfo().getString("title");
    }
    public String getWebUrl() {
        return getInfo().getString("webpageURL");
    }
    public String getDescription() {
        return getInfo().getString("description");
    }

    public String getPic1() {
        return getInfo().getString("pic1");
    }
}
