package com.app.khaleeji.FirebaseService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//import com.applozic.mobicomkit.Applozic;
//import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
//import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
//import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
//import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.BuildConfig;
import com.app.khaleeji.R;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import Constants.Bundle_Identifier;
import Utility.SavePref;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    private static final String CHANNEL_NAME = "Khaleeji Notification Channel";
    private static final String CHANNEL_DESCRIPTION = "";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        SavePref.getInstance(this).save_FirebaseDeviceKey(token);

//        Applozic.getInstance(this).setDeviceRegistrationId(token);
//        if (MobiComUserPreference.getInstance(this).isRegistered()) {
//            try {
//                RegistrationResponse registrationResponse = new RegisterUserClientService(this).updatePushNotificationId(token);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Push Message data payload: " + remoteMessage.getData());

//            if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.getData())) {
//                Log.i(TAG, "Applozic notification processing...");
//                MobiComPushReceiver.processMessageAsync(this, remoteMessage.getData());
//                return;
//            }

            String title = "";
            String bodyMessage = "";
            String arabicTitle = "";
            String arabicMessage = "";
            String type = "";
            String engMessage = "";
            String entTitle = "";

            JSONObject notificationJson = null;
            try {
                notificationJson = new JSONObject(remoteMessage.getData());
                entTitle = notificationJson.getString("title");
                engMessage = notificationJson.getString("message");
                arabicTitle = notificationJson.getString("arabicTitle");
                arabicMessage = notificationJson.getString("arabicMessage");
                type = notificationJson.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
                title = arabicTitle;
                bodyMessage = arabicMessage;
            }else{
                title = entTitle;
                bodyMessage = engMessage;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if(type.equals("7")){
                SavePref.getInstance(getApplicationContext()).setNewMsgNotification(true);
                intent.putExtra("startFragment", "message");
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.NEW_MSG_NOTIFICATION);
                EventBus.getDefault().post(messageEvent);
            }else{
                SavePref.getInstance(getApplicationContext()).setShowmessageReminder(true);
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.NEW_MSG);
                EventBus.getDefault().post(messageEvent);
                intent.putExtra("startFragment", "NotificationFragments");
            }

            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            final Uri NOTIFICATION_SOUND_URI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.sound_notification);
            String chanelId = "khaleejiId";

            int badgeCount = SavePref.getInstance(getApplicationContext()).getNotificationCount();
            badgeCount ++;
            SavePref.getInstance(getApplicationContext()).setNotificationCount(badgeCount);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE );

            // show the notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, chanelId)
                    .setContentTitle(title)
                    .setContentText(bodyMessage)
                    .setSound(NOTIFICATION_SOUND_URI)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setNumber(badgeCount)
                    .setColor(getColor(R.color.colorPrimary))
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            mBuilder.setSmallIcon(R.drawable.ic_notification);

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mBuilder.setChannelId( chanelId ) ;
                CharSequence name = CHANNEL_NAME;
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(chanelId, name, importance);
                channel.setDescription(CHANNEL_DESCRIPTION);
                channel.setShowBadge(true);
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                channel.setSound(NOTIFICATION_SOUND_URI, attributes);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }
            assert notificationManager != null;
            notificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

}