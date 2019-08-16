package com.xando.chefsclub.Recipes.Upload;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.xando.chefsclub.App;
import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Recipes.db.RecipeToFavoriteEntity;
import com.xando.chefsclub.Recipes.db.RecipesToFavoriteDao;

import io.reactivex.schedulers.Schedulers;

public class SyncFavoriteService extends Service {

    private static final String TAG = "SyncFavoriteService";

    public SyncFavoriteService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationsForSyncFavorite.O.createNotification(this);
        else NotificationsForSyncFavorite.PreO.createNotification(this);

        App app = (App) getApplication();

        RecipesToFavoriteDao dao = app.getDatabase().recipesToFavoriteDao();

        dao.getSingleAll()
                .subscribeOn(Schedulers.io())
                .subscribe(entities -> {
                    for (RecipeToFavoriteEntity entity : entities) {
                        FirebaseHelper.Favorite.updateFavoriteOnServer(app, entity);
                    }
                    stopSelf();
                });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
