package com.xando.chefsclub;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.room.Room;

import com.squareup.leakcanary.RefWatcher;
import com.xando.chefsclub.compilations.sync.SyncCompilationService;
import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.dataworkers.AppDatabase;
import com.xando.chefsclub.recipes.upload.SyncFavoriteService;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.io.FilesKt;

import static com.xando.chefsclub.helper.FirebaseHelper.getUid;


public class App extends Application {

    private static Context context;
    private RefWatcher refWatcher;

    private static App sInstance;

    private AppDatabase database;

    private BroadcastReceiver mNetworkReceiver;

    private CompositeDisposable disposer = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();

        //Init corner radius for gallery view
        Constants.ImageConstants.CORNER_RADIUS = getResources().getDimensionPixelSize(R.dimen.image_corner_radius);

        sInstance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "profilesDatabase")
                .build();

        JodaTimeAndroid.init(this);

        // Remove old files
        FilesKt.deleteRecursively(new File(Constants.Files.getDirectoryForTemporaryFiles(this)));
        FilesKt.deleteRecursively(new File(Constants.Files.getDirectoryForCaptures(this)));

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

        startFavoriteUpdateIfNeed();
        //startSyncCompilationsTittle();

        //mNetworkReceiver = new ConnectivityChangeReceiver();
        //registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }
    /*public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }*/


    // TODO: need rebase to MainActivity
    private void startFavoriteUpdateIfNeed() {
        disposer.add(getDatabase().recipesToFavoriteDao().getSingleAll()
                .subscribeOn(Schedulers.io())
                .subscribe(entities -> {
                    if (!entities.isEmpty()) startFavoriteUpdateService();
                }));
    }

    private void startFavoriteUpdateService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(new Intent(this, SyncFavoriteService.class));
        else startService(new Intent(this, SyncFavoriteService.class));
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
