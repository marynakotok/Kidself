package com.example.thekidself.chat.helper;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.thekidself.R;
import com.example.thekidself.activity.MenuChildActivity;
import com.example.thekidself.activity.MenuParentActivity;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationHandler {

    private Context mContext;
    Intent intent;

    public NotificationHandler(Context mContext) {
        this.mContext = mContext;
    }


    //This method would display the notification
    public void showNotificationMessage(final String title, final String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.drawable.logo_icon);
        SharedPreferences mSharedPreferencesParent = mContext.getSharedPreferences("Parent", MODE_PRIVATE);
        String codeParent = mSharedPreferencesParent.getString("codeParent","");

        SharedPreferences mSharedPreferencesChild = mContext.getSharedPreferences("Child", MODE_PRIVATE);
        String codeChild = mSharedPreferencesChild.getString("codeChild","");

        if (codeChild.equals(""))
        {
            intent = new Intent(mContext, MenuParentActivity.class);
        } else {
            intent = new Intent(mContext, MenuChildActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo_icon));
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }


    //This method will check whether the app is in background or not
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

}
