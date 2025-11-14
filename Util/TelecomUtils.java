package com.phonecontactscall.contectapp.phonedialerr.Util;

import android.app.Activity;
import android.content.Context;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.os.Process;

import java.util.Arrays;
import java.util.Locale;


public class TelecomUtils {
    public static final String IS_LOCK = "check_lock";
    public static final String startCall = "startCall";
    public static final String IncomingCallAnswered = "onIncomingCallAnswered";
    public static final String IncomingCallEnded = "onIncomingCallEnded";
    public static final String IncomingCallReceived = "onIncomingCallReceived";
    public static final String MissedCall = "onMissedCall";
    public static final String OutgoingCallAnswered = "onOutgoingCallAnswered";
    public static final String OutgoingCallDISCONNECTED = "onOutgoingCallDisconnected";
    public static final String OutgoingCallEnded = "onOutgoingCallEnded";
    public static final String OutgoingCallStarted = "onOutgoingCallStarted";
    private static boolean sWarningLogged;

    public static final PhoneAccountHandle getDefaultOutgoingPhoneAccount(Activity activity, TelecomManager telecomManager, String str) {
        if (telecomManager != null && CallerHelper.isMethodAvailable("android.telecom.TelecomManager", "getDefaultOutgoingPhoneAccount", String.class) && activity.checkPermission("android.permission.READ_PHONE_STATE", Process.myUid(), Process.myPid()) == 0) {
            return telecomManager.getDefaultOutgoingPhoneAccount(str);
        }
        return null;
    }

    public static final String getDefaultDialerPackage(TelecomManager telecomManager) {
        if (telecomManager == null) {
            return null;
        }
        return telecomManager.getDefaultDialerPackage();
    }

    private static final TelecomManager getTelecomManager(Context context) {
        return (TelecomManager) (context == null ? null : context.getSystemService("telecom"));
    }

    public static String formatTime(int i, boolean z, int i2) {
        if ((i2 & 1) != 0) {
            z = false;
        }
        StringBuilder sb = new StringBuilder(8);
        int i3 = i / 3600;
        int i4 = (i % 3600) / 60;
        int i5 = i % 60;
        if (i >= 3600) {
            sb.append(String.format(Locale.getDefault(), "%02d", Arrays.copyOf(new Object[]{Integer.valueOf(i3)}, 1)));
            sb.append(":");
        } else if (z) {
            sb.append("0:");
        }
        sb.append(String.format(Locale.getDefault(), "%02d", Arrays.copyOf(new Object[]{Integer.valueOf(i4)}, 1)));
        sb.append(":");
        sb.append(String.format(Locale.getDefault(), "%02d", Arrays.copyOf(new Object[]{Integer.valueOf(i5)}, 1)));
        return sb.toString();
    }

    public static final void cancelMissedCallsNotification(Context context) {
        if (hasModifyPhoneStatePermission(context)) {
            try {
                getTelecomManager(context).cancelMissedCallsNotification();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SecurityException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static final boolean hasModifyPhoneStatePermission(Context context) {
        return isDefaultDialer(context) || hasPermission(context, "android.permission.MODIFY_PHONE_STATE");
    }

    public static final boolean isDefaultDialer(Context context) {
        boolean equals = TextUtils.equals(context.getPackageName(), getDefaultDialerPackage(getTelecomManager(context)));
        if (equals) {
            sWarningLogged = false;
        } else if (!sWarningLogged) {
            sWarningLogged = true;
        }
        return equals;
    }

    public static final boolean hasPermission(Context context, String str) {
        return chenk(context, str) == 0;
    }

    public static int chenk(Context context, String str) {
        if (str != null) {
            return context.checkPermission(str, Process.myPid(), Process.myUid());
        }
        throw new IllegalArgumentException("permission is null");
    }
}
