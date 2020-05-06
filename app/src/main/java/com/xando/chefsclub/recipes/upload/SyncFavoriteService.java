package com.xando.chefsclub.recipes.upload;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.xando.chefsclub.App;

import io.reactivex.disposables.CompositeDisposable;

public class SyncFavoriteService extends Service {

    private static final String TAG = "SyncFavoriteService";

    public SyncFavoriteService() {
    }

    private CompositeDisposable disposer = new CompositeDisposable();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationsForSyncFavorite.O.createNotification(this);
        else NotificationsForSyncFavorite.PreO.createNotification(this);

        App app = (App) getApplication();
        //disposer.add(Favorite.INSTANCE.syncFavorite(app, this::stopSelf));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        disposer.dispose();
        super.onDestroy();
    }
}
