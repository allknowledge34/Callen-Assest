package com.phonecontactscall.contectapp.phonedialerr.Util;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telecom.Call;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.phonecontactscall.contectapp.phonedialerr.Glob;
import com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity;
import com.phonecontactscall.contectapp.phonedialerr.activity.MainActivity;
import com.phonecontactscall.contectapp.phonedialerr.R;


public class NotificationUtils {

    public static int getFlagVersion() {
        return Build.VERSION.SDK_INT >= 31 ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static final void createAcceptDeclineNotification(Call call, Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");

        boolean isHighPriority = call.getState() == Call.STATE_RINGING ;

        String channelId = isHighPriority ? "high_priority" : "contect_call";
        if (Glob.isOreoPlus()) {
            int importance = isHighPriority ? NotificationManager.IMPORTANCE_HIGH :  NotificationManager.IMPORTANCE_DEFAULT;
            String name = isHighPriority ? "call_name_high_priority" : "contect_call";

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setSound(null, null);
            channel.setLockscreenVisibility(0);
            notificationManager.createNotificationChannel(channel);
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), (int) R.layout.layout_incoming_call_notification);
        Uri handle = call.getDetails().getHandle();
        String contactName = com.phonecontactscall.contectapp.phonedialerr.Util.ContactHelper.getContactName(context, handle == null ? null : handle.getSchemeSpecificPart(), true);
        if (contactName == null || contactName.length() == 0) {
            return;
        }
        Intent action = new Intent(context, NotificationActionService.class).setAction("Decline_Call");
        Intent action2 = new Intent(context, NotificationActionService.class).setAction("Accept_Call");
        PendingIntent service = PendingIntent.getService(context, 0, action, getFlagVersion());
        PendingIntent service2 = PendingIntent.getService(context, 0, action2, getFlagVersion());
        remoteViews.setTextViewText(R.id.textViewContactNameNumber, contactName);
        remoteViews.setViewVisibility(R.id.imageViewAnswer,  call.getState() == Call.STATE_RINGING ? View.VISIBLE : View.GONE);

        String[] retrieveContactPhotoUriAndDisplayName = com.phonecontactscall.contectapp.phonedialerr.Util.ContactHelper.ContactPhotoUriAndDisplayName(context, handle.getSchemeSpecificPart());
        if (retrieveContactPhotoUriAndDisplayName[1] != null) {
            try {
                remoteViews.setImageViewBitmap(R.id.avatar, CallerHelper.getCroppedBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(retrieveContactPhotoUriAndDisplayName[1]))));
            } catch (Exception unused) {
            }
        } else {
            remoteViews.setViewVisibility(R.id.avatar, 8);
        }
        if (contactName == null || contactName.isEmpty()) {
            return;
        }
        String[] contactPhotoUri = ContactHelper.ContactPhotoUriAndDisplayName(context, handle.getSchemeSpecificPart());
        Uri avatarUri = contactPhotoUri[1] != null ? Uri.parse(contactPhotoUri[1]) : null;


        Intent declineIntent = new Intent(context, NotificationActionService.class).setAction("Decline_Call");
        PendingIntent declinePendingIntent = PendingIntent.getService(context, 0, declineIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent acceptIntent = new Intent(context, NotificationActionService.class).setAction("Accept_Call");
        PendingIntent acceptPendingIntent = PendingIntent.getService(context, 0, acceptIntent, PendingIntent.FLAG_IMMUTABLE);

        Person incomingCaller = new Person.Builder()
                .setName(contactName)
                .setImportant(true)
                .build();
        Intent intent = new Intent();

        if (call.getState() == Call.STATE_RINGING) {
            intent.setClass(context, CallActivity.class);
            intent.putExtra("state", com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils.IncomingCallReceived);
            Glob.receivenotification = true;
            Glob.accept_call = false;
        } else {
            intent.setClass(context, CallActivity.class);
            intent.putExtra("state", com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils.startCall);
            Glob.receivenotification = true;
            Glob.isReceiverRegistered = false;
        }
        boolean isScreenOn = ((PowerManager) context.getSystemService("power")).isScreenOn();
        if (((KeyguardManager) context.getSystemService("keyguard")).isKeyguardLocked()) {
            intent.putExtra(com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils.IS_LOCK, false);
        } else if (!isScreenOn) {
            intent.putExtra(com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils.IS_LOCK, false);
            intent.putExtra(com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils.IS_LOCK, false);
        } else {
            intent.putExtra(TelecomUtils.IS_LOCK, true);
        }


        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, getFlagVersion());
        remoteViews.setOnClickPendingIntent(R.id.imageViewAnswer, service2);
        remoteViews.setOnClickPendingIntent(R.id.imageViewDecline, service);

        long callDurationMillis = CallUtils.getDuration(call) * 1000L;
        long callStartTime = System.currentTimeMillis() - callDurationMillis;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            Notification.Builder builder1 = new Notification.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launch_square)
                    .setOngoing(true)
                    .setContentTitle(contactName)
                    .setContentIntent(activity)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setWhen(0)
                    .setUsesChronometer(call.getState() == Call.STATE_ACTIVE)
                    .setPriority(isHighPriority ? NotificationManager.IMPORTANCE_HIGH : NotificationCompat.PRIORITY_DEFAULT)
                    .addPerson(incomingCaller)
                    .setDeleteIntent(clearIncommingPendingIntent(context))
                    .setFullScreenIntent(activity, true);


            if (call.getState() == Call.STATE_DIALING) {
                Glob.accept_call = false;
                builder1.setStyle(Notification.CallStyle.forOngoingCall(incomingCaller, declinePendingIntent)).setShowWhen(false) // Hide the time in the status bar
                        .setWhen(0).setUsesChronometer(false);
            }
            else if (call.getState() == Call.STATE_RINGING) {

                Glob.accept_call = false;
                builder1.setStyle(Notification.CallStyle.forIncomingCall(incomingCaller, declinePendingIntent, acceptPendingIntent)).setShowWhen(false) // Hide the time in the status bar
                        .setWhen(0).setUsesChronometer(false);
            } else {
                builder1.setStyle(Notification.CallStyle.forOngoingCall(incomingCaller, declinePendingIntent)).setShowWhen(true).setWhen(callStartTime).setUsesChronometer(true);;

            }
            Notification notification = builder1.build();
            notificationManager.notify(1008, notification);
        }else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launch_square)
                    .setContentIntent(activity)
                    .setPriority(isHighPriority ? NotificationManager.IMPORTANCE_HIGH : NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(Notification.CATEGORY_CALL)
                    .setCustomContentView(remoteViews)
                    .setOngoing(true)
                    .setSound(null)
                    .setFullScreenIntent(activity,true)
                    .setUsesChronometer(call.getState() == Call.STATE_ACTIVE)
                    .setChannelId(channelId)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle());


            Notification notification = builder.build();
            notificationManager.notify(1008, notification);

        }



        if(call==null ||call.equals(null)){
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                notificationManager.cancel(1008);
            }, 1000);
        }


    }


    public static void removeNotificationFromID(Context context, int i) {

        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            stopVibrate(context);
            notificationManager.cancel(1008);
            notificationManager.cancel(i);
            notificationManager.cancelAll();
        } catch (Exception unused) {
        }
    }




    public static void updateMissedCallNotification(int missedCallCount, String phoneNumber, Context context) {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String channelId = "high_priority";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Missed Calls",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for missed calls");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(notificationSound, new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.sym_call_missed)
                .setContentTitle("Missed Call")
                .setContentText("You have " + missedCallCount + " missed call(s) from " + phoneNumber)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(notificationSound)
                .setContentIntent(createCallLogPendingIntent(context))
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }



    private static final PendingIntent clearIncommingPendingIntent(Context context) {
        Intent intent = new Intent(context, NotificationActionService.class);
        intent.setAction("Decline_Call");
        return PendingIntent.getService(context, 0, intent, getFlagVersion());
    }

    private static final PendingIntent createCallLogPendingIntent(Context context) {
        return PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).setAction("com.android.phone.action.RECENT_CALLS"), getFlagVersion());
    }

  

    public static void stopVibrate(Context context) {
        ((Vibrator) context.getSystemService("vibrator")).cancel();
    }



}
