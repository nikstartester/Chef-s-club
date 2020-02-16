package com.xando.chefsclub.compilations.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xando.chefsclub.compilations.data.ArrayCompilations;
import com.xando.chefsclub.compilations.repository.CompilationsRepository;
import com.xando.chefsclub.dataworkers.ParcResourceByParc;


public class SyncCompilationService extends Service {

    public static final String ACTION_RESPONSE = "com.xando.chefsclub.Compilations.Sync.action.RESPONSE";
    public static final String EXTRA_RESOURCE = "com.xando.chefsclub.Compilations.Sync.extra.RESOURCE";
    protected static final String EXTRA_DATA = "com.xando.chefsclub.Compilations.Sync.extra.DATA";
    private static final String TAG = "SyncCompilationTittleSe";

    public static Intent getIntent(Context context, String userUid) {
        Intent intent = new Intent(context, SyncCompilationService.class);

        intent.putExtra(EXTRA_DATA, userUid);

        return intent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String userUid = intent.getStringExtra(EXTRA_DATA);

            onStart(userUid);

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void onStart(String userUid) {
        if (userUid != null) {
            new CompilationsRepository(SyncCompilationService.this.getApplication(), null)
                    .sync(userUid, resource -> {
                        sendStickyBroadcast(createResponseIntent(resource));

                        if (resource.status == ParcResourceByParc.Status.ERROR
                                || resource.status == ParcResourceByParc.Status.SUCCESS) {
                            stopSelf();
                        }
                    });

        } else {
            sendStickyBroadcast(createResponseIntent(ParcResourceByParc
                    .error(new NullPointerException("UserUid might not be null"), null)));
            stopSelf();
        }
    }

    private Intent createResponseIntent(ParcResourceByParc<ArrayCompilations> resource) {
        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_RESPONSE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(EXTRA_RESOURCE, resource);

        return responseIntent;
    }
}
