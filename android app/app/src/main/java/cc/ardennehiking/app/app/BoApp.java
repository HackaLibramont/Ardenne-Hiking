package cc.ardennehiking.app.app;

import android.app.Application;

import cc.ardennehiking.app.service.LocalWays;

public class BoApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    private void init() {
        MyJus.init(this);
        LocalWays.init(getApplicationContext());
    }

}
