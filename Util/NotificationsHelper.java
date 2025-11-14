package com.phonecontactscall.contectapp.phonedialerr.Util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NotificationsHelper {
    private static NotificationsHelper sInstance;
    AddCallsQuery addCallsQuery;

    
    public interface AddCallsQuery {
        List<NewCall> query(Integer num);
    }

    public NotificationsHelper(AddCallsQuery newCallsQuery) {
        this.addCallsQuery = newCallsQuery;
    }

    
    public static final class NewCall {
        private final String accountName;
        private final String accountId;
        private final Uri callsUri;
        private final String countryIso;
        private final long dateMs;
        private final String number;
        private final int numberPresentation;
        private final String transcription;
        private final Uri voicemailUri;

        public final String getAccountName() {
            return this.accountName;
        }

        public final String getAccountId() {
            return this.accountId;
        }

        public final Uri getCallsUri() {
            return this.callsUri;
        }

        public final String getCountryIso() {
            return this.countryIso;
        }

        public final long getDateMs() {
            return this.dateMs;
        }

        public final String getNumber() {
            return this.number;
        }

        public final int getNumberPresentation() {
            return this.numberPresentation;
        }

        public final String getTranscription() {
            return this.transcription;
        }

        public final Uri getVoicemailUri() {
            return this.voicemailUri;
        }

        public NewCall(Uri uri, Uri uri2, String str, int i, String str2, String str3, String str4, String str5, long j) {
            this.callsUri = uri;
            this.voicemailUri = uri2;
            this.number = str;
            this.numberPresentation = i;
            this.accountName = str2;
            this.accountId = str3;
            this.transcription = str4;
            this.countryIso = str5;
            this.dateMs = j;
        }
    }


    
    public static final class Instants {
        private Instants() {
        }

        private static final AddCallsQuery createNewCallsQuery(Context context, ContentResolver contentResolver) {
            return new DefaultCallsQuery(context, contentResolver);
        }

        public static final NotificationsHelper getInstance(Context context) {
            if (NotificationsHelper.sInstance == null) {
                NotificationsHelper unused = NotificationsHelper.sInstance = new NotificationsHelper(createNewCallsQuery(context, context.getContentResolver()));
            }
            return NotificationsHelper.sInstance;
        }

        public static final void removeMissedCallNotifications(Context context) {
            SharedPreferences sharedPreferences;
            if (context != null) {
                try {
                    TelecomUtils.cancelMissedCallsNotification(context);
                } catch (Exception unused) {
                    return;
                }
            }
            SharedPreferences.Editor edit = (context == null || (sharedPreferences = context.getSharedPreferences("NOTY_PREFS", 0)) == null) ? null : sharedPreferences.edit();
            if (edit != null) {
                edit.putInt("MissedCount", 0);
            }
            if (edit != null) {
                edit.apply();
            }
        }
    }

    
    public static final class DefaultCallsQuery implements AddCallsQuery {
        private final ContentResolver mContentResolver;
        private final Context mContext;

        public DefaultCallsQuery(Context context, ContentResolver contentResolver) {
            this.mContext = context;
            this.mContentResolver = contentResolver;
        }

        private final NewCall createNewCallsFromCursor(Cursor cursor) {
            String string = cursor.getString(2);
            return new NewCall(ContentUris.withAppendedId(CallLog.Calls.CONTENT_URI_WITH_VOICEMAIL, cursor.getLong(0)), string == null ? null : Uri.parse(string), cursor.getString(1), cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getLong(8));
        }

        @Override
        public List<NewCall> query(Integer num) {
            if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_CALL_LOG") == 0 && num != null) {
                String format = String.format("%s = 1 AND %s = ?", Arrays.copyOf(new Object[]{"new", "type"}, 2));
                String[] strArr = {num.toString()};
                String[] strArr2 = {"_id", "number", "voicemail_uri", "presentation", "subscription_component_name", "subscription_id", "transcription", "countryiso", "date"};
                try {
                    if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_CALL_LOG") == 0) {
                        Cursor query = this.mContentResolver.query(CallLog.Calls.CONTENT_URI_WITH_VOICEMAIL, strArr2, format, strArr, "date DESC");
                        if (query == null) {
                            query.close();
                            return null;
                        }
                        ArrayList arrayList = new ArrayList();
                        while (query.moveToNext()) {
                            arrayList.add(createNewCallsFromCursor(query));
                        }
                        query.close();
                        return arrayList;
                    }
                } catch (RuntimeException unused) {
                }
            }else {
            }
            return null;
        }
    }
}
