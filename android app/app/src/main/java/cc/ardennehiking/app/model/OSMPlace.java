package cc.ardennehiking.app.model;


public class OSMPlace extends Place{

    @Override
    public String getTitle() {
        String title = getJson().optString("name");
        if(title==null || title.isEmpty()) {
            title = getJson().optString("typeName");
        }
        return title;
    }

    @Override
    public String getSummary() {
        return getJson().optString("typeName") + " ("  + getJson().optString("typeClass") + ")";
    }

    @Override
    public String getWikiLink() {
        return null;
    }
    @Override
    public String getId() {
        return getTitle()+"_"+getSummary()+"_"+getLat()+"_"+getLon();
    }


}
