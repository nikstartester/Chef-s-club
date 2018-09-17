package com.example.nikis.bludogramfirebase.Recipe.NewRecipe;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.RecipeData.Images.UploadImagePath;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.zelory.compressor.Compressor;

public class CompressAndUploadImagesService extends Service {
    public static final String KEY_POSITION = "position";
    public static final String KEY_IS_STEP_IMAGE ="isStepImage" ;
    public static final String KEY_IMAGE_PATH = "imagePath";
    public static final String KEY_RECIPE_KEY = "recipeKey";
    public static final String KEY_P_INTENT = "pIntent";
    public static final int STATUS_FINISHED = 65;


    private ExecutorService executorService;
    private volatile int allImages = 0;
    private volatile int finishedProcesses = 0;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int position = intent.getIntExtra(KEY_POSITION, -1);
        boolean isStepImage = intent.getBooleanExtra(KEY_IS_STEP_IMAGE, true);
        String imagePath = intent.getStringExtra(KEY_IMAGE_PATH);
        String recipeKey = intent.getStringExtra(KEY_RECIPE_KEY);
        pendingIntent = intent.getParcelableExtra(KEY_P_INTENT);

        allImages ++;

        RunProcess process = new RunProcess(new UploadImagePath(position, isStepImage, imagePath, recipeKey), startId);

        executorService.execute(process);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopIfAllProcessesFinished(){
        finishedProcesses++;
        if (allImages == finishedProcesses) {
            try {
                pendingIntent.send(STATUS_FINISHED);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            onDestroy();
        }
    }

    class RunProcess implements Runnable{
        UploadImagePath uploadImagePath;
        int startId;


        RunProcess(UploadImagePath uploadImagePath, int startId) {
            this.uploadImagePath = uploadImagePath;
            this.startId = startId;
        }

        @Override
        public void run() {
            File compressedFile = null;
            try {
               compressedFile = new Compressor(getApplicationContext())
                        .setQuality(75)
                        .compressToFile(new File(uploadImagePath.getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (compressedFile != null){
                startUploadImageTask(compressedFile);
            }
        }
        private void startUploadImageTask(File file){
            StorageReference storageReference = FirebaseReferences.getStorageReference();

            Uri fileUri = Uri.fromFile(file);

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image")
                    .build();

            storageReference.child(uploadImagePath.getUploadPath())
                    .putFile(fileUri, metadata).addOnCompleteListener(task -> stopIfAllProcessesFinished());

        }
    }
}
