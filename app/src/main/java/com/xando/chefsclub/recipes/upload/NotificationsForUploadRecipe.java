package com.xando.chefsclub.recipes.upload;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.NotificationCompat;

import com.xando.chefsclub.R;

import static com.xando.chefsclub.recipes.upload.NotificationsForUploadRecipe.O.CHANNEL_ID;

class NotificationsForUploadRecipe {

    private static final int SMALL_ICON = R.drawable.ic_file_upload_blue_48dp;

    private static final int ONGOING_NOTIFICATION_ID = 1;

    private static int getRandomId() {
        return 124214;
    }

    private static String getNotificationTitle() {
        return "Upload recipe";
    }

    @TargetApi(26)
    static class O {

        static final String CHANNEL_ID = "uploadRecipe";

        static void createNotification(Service context) {
            String channelId = createChannel(context);
            Notification notification =
                    buildNotification(context, channelId);
            context.startForeground(
                    ONGOING_NOTIFICATION_ID, notification);
        }

        private static Notification buildNotification(
                Service context, String channelId) {

            // Create a notification.
            return new Notification.Builder(context, channelId)
                    .setContentTitle(getNotificationTitle())
                    .setProgress(100, 0, true)
                    .setSmallIcon(SMALL_ICON)
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setChannelId(CHANNEL_ID)
                    .build();
        }

        @NonNull
        private static String createChannel(Service ctx) {
            // Create a channel.
            NotificationManager notificationManager =
                    (NotificationManager)
                            ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence channelName = "Uploader channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel =
                    new NotificationChannel(
                            CHANNEL_ID, channelName, importance);

            notificationManager.createNotificationChannel(
                    notificationChannel);
            return CHANNEL_ID;
        }
    }

    @TargetApi(25)
    static class PreO {

        static void createNotification(Service context) {
            // Create a notification.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(getNotificationTitle())
                    .setProgress(100, 0, true)
                    .setColor(context.getResources().getColor(R.color.colorPrimary));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                builder.setSmallIcon(SMALL_ICON);

            Notification mNotification = builder.build();

            context.startForeground(
                    ONGOING_NOTIFICATION_ID, mNotification);
        }
    }

    private static Drawable getIcon(Context context) {
        return VectorDrawableCompat.create(context.getResources(), SMALL_ICON, null);
    }
}
