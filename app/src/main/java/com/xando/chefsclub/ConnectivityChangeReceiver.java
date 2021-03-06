package com.xando.chefsclub;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xando.chefsclub.helper.NetworkHelper;
import com.xando.chefsclub.recipes.upload.SyncFavoriteService;


public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "ConnectivityChangeRecei";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + NetworkHelper.isConnected(context));

        ComponentName comp = new ComponentName(context.getPackageName(),
                SyncFavoriteService.class.getName());
        intent.putExtra("isNetworkConnected", NetworkHelper.isConnected(context));

        context.startService(intent.setComponent(comp));
    }
}
