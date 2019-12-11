package io.hamed.firebasepush;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> params = remoteMessage.getData();
        String jsonStr = params.get("data");
        Log.i(MainActivity.TAG, "FireBase -> JSON: " + jsonStr);

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            final int id = jsonObject.getInt("id");
            final String title = jsonObject.getString("title");
            final String description = jsonObject.getString("description");
            final String image = jsonObject.getString("imageUrl");
            final String languageTitle = jsonObject.getString("languageTitle");

            Glide.with(this)
                    .asBitmap()
                    .load(image)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            showNotification(title, description, null, languageTitle, id);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            showNotification(title, description, resource, languageTitle, id);
                            return false;
                        }
                    })
                    .submit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String title, String description, Bitmap bitmap, String languageTitle, int id) {
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("id", id);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(description)
                .setSubText(languageTitle)
                .setTicker(getString(R.string.app_name))
                .setContentIntent(resultPendingIntent);

        if (bitmap != null) {
            NotificationCompat.BigPictureStyle bpStyle = new NotificationCompat.BigPictureStyle();
            bpStyle.bigPicture(bitmap);
            bpStyle.build();
            builder.setStyle(bpStyle);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence _name = getString(R.string.channel_name);
            String _description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, _name, importance);
            channel.setDescription(_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(id, builder.build());
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(id, builder.build());
        }
    }
}
