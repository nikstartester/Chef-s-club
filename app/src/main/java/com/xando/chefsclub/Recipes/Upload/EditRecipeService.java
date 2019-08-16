package com.xando.chefsclub.Recipes.Upload;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xando.chefsclub.DataWorkers.DataUploader;
import com.xando.chefsclub.DataWorkers.DataUploaderService;
import com.xando.chefsclub.Recipes.Data.RecipeData;

import static com.xando.chefsclub.Helpers.FirebaseHelper.getUid;


public class EditRecipeService extends DataUploaderService<RecipeData> {

    private static final String TAG = "EditRecipeService";

    public static Intent getIntent(Context context, RecipeData recipeData) {
        Intent intent = new Intent(context, EditRecipeService.class);
        return intent.putExtra(EXTRA_DATA, recipeData);
    }

    public static Intent getIntent(Context context, RecipeData recipeData, Action action) {
        Intent intent = new Intent(context, EditRecipeService.class);
        return intent.putExtra(EXTRA_DATA, recipeData).putExtra(EXTRA_ACTIONS, action);
    }

    public static void startEditRecipeService(Context context, RecipeData data) {
        startEditRecipeService(context, data, null);
    }

    public static void startEditRecipeService(Context context, RecipeData data, Action action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(EditRecipeService.getIntent(context, data, action));
        } else {
            context.startService(EditRecipeService.getIntent(context, data, action));
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationsForUploadRecipe.O.createNotification(this);
        else NotificationsForUploadRecipe.PreO.createNotification(this);

        super.onHandleIntent(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public DataUploader<RecipeData> getDataUploader() {
        return new RecipeDataUploader(this, new String[]{"/recipes/", "/user-recipes/" + getUid() + "/"});
    }
}
