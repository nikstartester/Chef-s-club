package com.example.nikis.bludogramfirebase;

import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.example.nikis.bludogramfirebase.Compilations.Sync.SyncCompilationService;
import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.DataWorkers.AppDatabase;
import com.example.nikis.bludogramfirebase.Recipes.Upload.SyncFavoriteService;
import com.example.nikis.bludogramfirebase.Settings.SettingsCacheFragment;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

import static com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper.getUid;


public class App extends MultiDexApplication {
    private static Context context;
    private RefWatcher refWatcher;

    private static App sInstance;

    private AppDatabase database;

    private BroadcastReceiver mNetworkReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "profilesDatabase")
                .build();

        JodaTimeAndroid.init(this);

        SettingsCacheFragment.deleteDir(new File(Constants.Files.getDirectoryForTemporaryFiles(this)));

        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.

            return;
        }
        refWatcher = LeakCanary.install(this);
        context = getApplicationContext();*/

        //FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(1024*1024*6);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //FirebaseStorage.getInstance().setMaxUploadRetryTimeMillis(60000*2);

        startFavoriteUpdate();
        //startSyncCompilationsTittle();

        //mNetworkReceiver = new ConnectivityChangeReceiver();
        //registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }
    /*public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }*/

    private void startFavoriteUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, SyncFavoriteService.class));
        } else {
            startService(new Intent(this, SyncFavoriteService.class));
        }
        //startService(new Intent(this, SyncFavoriteService.class));
    }

    private void startSyncCompilationsTittle() {
        if (getUid() != null)
            startService(SyncCompilationService.getIntent(this, getUid()));
    }

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

}
