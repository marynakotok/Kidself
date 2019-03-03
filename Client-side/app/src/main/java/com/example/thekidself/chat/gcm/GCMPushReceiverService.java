package com.example.thekidself.chat.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.example.thekidself.chat.helper.Constants;
import com.example.thekidself.chat.helper.NotificationHandler;
import com.google.android.gms.gcm.GcmListenerService;

public class GCMPushReceiverService extends GcmListenerService {
    @Override
    public void onMessageReceived(String registration_ids, Bundle data) {
        String message = data.getString("message");
        String title = data.getString("title");
        String code_user = data.getString("code_user");
        String code_family = data.getString("code_family");
        sendNotification(message, title, code_user, code_family);
    }

    private void sendNotification(String message, String title, String code_user, String code_family) {
        //Creating a broadcast intent
        Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
        //Adding notification data to the intent
        pushNotification.putExtra("message", message);
        pushNotification.putExtra("name", title);
        pushNotification.putExtra("code_user", code_user);
        pushNotification.putExtra("code_family", code_family);

        //We will create this class to handle notifications
        NotificationHandler notificationHandler = new NotificationHandler(getApplicationContext());

        //If the app is in foreground
        if (!NotificationHandler.isAppIsInBackground(getApplicationContext())) {
            //Sending a broadcast to the chatRoom to add the new message
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        } else {
            //If app is in foreground displaying push notification
            notificationHandler.showNotificationMessage(title, message);
        }
    }
}
