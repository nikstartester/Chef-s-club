package com.example.nikis.bludogramfirebase;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.res.Configuration;

import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;


public class App extends Application {
    private static Context context;
    private RefWatcher refWatcher;

    public static App sInstance;

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "profilesDatabase")
                .build();

        JodaTimeAndroid.init(this);

        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        context = getApplicationContext();*/

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
    /*public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }*/

    public static App getInstance() {
        return sInstance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    @Deprecated
    public static Context getAppContext(){
        return context;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
