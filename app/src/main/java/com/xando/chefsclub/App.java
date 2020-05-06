package com.xando.chefsclub;

import android.app.Application;
import android.util.Log;

import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.dataworkers.AppDatabase;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

import androidx.room.Room;
import io.reactivex.plugins.RxJavaPlugins;
import kotlin.io.FilesKt;


public class App extends Application {
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        //Init corner radius for gallery view
        Constants.ImageConstants.CORNER_RADIUS = getResources().getDimensionPixelSize(R.dimen.image_corner_radius);

        database = Room.databaseBuilder(this, AppDatabase.class, "profilesDatabase")
                .build();

        JodaTimeAndroid.init(this);

        // Remove old files
        FilesKt.deleteRecursively(new File(Constants.Files.getDirectoryForTemporaryFiles(this)));
        FilesKt.deleteRecursively(new File(Constants.Files.getDirectoryForCaptures(this)));

        RxJavaPlugins.setErrorHandler(throwable -> {
            if (BuildConfig.DEBUG) throw new RuntimeException(throwable);
            else Log.e("RxErrorHandler", "OnError", throwable);
        });
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
