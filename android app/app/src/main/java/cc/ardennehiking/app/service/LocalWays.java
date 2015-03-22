package cc.ardennehiking.app.service;


import android.content.Context;

import org.djodjo.json.JsonArray;
import org.djodjo.json.JsonElement;

import java.io.IOException;
import java.io.InputStreamReader;

public class LocalWays {

    private static LocalWays inst;

    JsonArray ways;
    JsonArray pois;
    private LocalWays(Context ctx) {
        try {
            ways = JsonElement.readFrom(new InputStreamReader(ctx.getAssets().open("tracks.json"))).asJsonArray();
            pois = JsonElement.readFrom(new InputStreamReader(ctx.getAssets().open("points.json"))).asJsonArray();
        } catch (IOException e) {
            e.printStackTrace();
            new RuntimeException("We cannot read ways!!!");
        }
    }


    public static void init(Context ctx) {
        inst = new LocalWays(ctx);
    }

    public static LocalWays get() {
        return inst;
    }

    public JsonArray getWays() {
        return ways;
    }

    public JsonArray getPois() {
        return pois;
    }



}
