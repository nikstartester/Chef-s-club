package com.example.nikis.bludogramfirebase;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.squareup.leakcanary.RefWatcher;


public class App extends Application {
    private static Context context;
    private RefWatcher refWatcher;
    @Override
    public void onCreate() {
        super.onCreate();
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        context = getApplicationContext();*/

        LocalUserData.getInstance().getValueFromPreferences(getApplicationContext());

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
    /*public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }*/

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
