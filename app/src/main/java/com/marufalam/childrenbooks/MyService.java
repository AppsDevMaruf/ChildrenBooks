 /*
 * Created by Maruf Alam Porosh on 4/3/21 12:31 AM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 4/3/21 12:31 AM
 */

package com.marufalam.childrenbooks;

 import android.app.NotificationChannel;
 import android.app.NotificationManager;
 import android.app.PendingIntent;
 import android.content.Intent;
 import android.os.Build;

 import androidx.annotation.NonNull;
 import androidx.core.app.NotificationCompat;

 import com.google.firebase.messaging.FirebaseMessagingService;
 import com.google.firebase.messaging.RemoteMessage;
 import com.marufalam.childrenbooks.Activites.MainActivity;

 public class MyService extends FirebaseMessagingService {
    public MyService() {
    }

     @Override
     public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
         super.onMessageReceived(remoteMessage);
         String title = remoteMessage.getNotification().getTitle();
         String body = remoteMessage.getNotification().getBody();
         showNotification(title, body);
     }

     private void showNotification(String title, String body) {


         // notification builder
         NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ID");
         builder.setContentTitle(title);
         builder.setContentText(body);
         builder.setSmallIcon(R.mipmap.ic_launcher_round);
         builder.setAutoCancel(true);

         // intent ready
         Intent intent = new Intent(getApplicationContext(), MainActivity.class);
         PendingIntent pendingIntent = PendingIntent.getActivity(
                 this,
                 101,
                 intent,
                 PendingIntent.FLAG_UPDATE_CURRENT);


         builder.setContentIntent(pendingIntent);


         int notifId = (int) System.currentTimeMillis();

         NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             NotificationChannel notificationChannel = new NotificationChannel(
                     "ID",
                     "name",
                     NotificationManager.IMPORTANCE_HIGH);

             notificationManager.createNotificationChannel(notificationChannel);

             notificationManager.notify(notifId, builder.build());

         } else {

             notificationManager.notify(notifId, builder.build());
         }


     }
 }