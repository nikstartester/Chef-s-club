package com.xando.chefsclub;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.squareup.leakcanary.RefWatcher;
import com.xando.chefsclub.Compilations.Sync.SyncCompilationService;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.AppDatabase;
import com.xando.chefsclub.Recipes.Upload.SyncFavoriteService;
import com.xando.chefsclub.Settings.SettingsCacheFragment;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

import static com.xando.chefsclub.Helpers.FirebaseHelper.getUid;


public class App extends Application {

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
    public static Context getAppContext() {
        return context;
    }
}
