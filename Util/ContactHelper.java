package com.phonecontactscall.contectapp.phonedialerr.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;


public class ContactHelper {

    @SuppressLint("Range")
    public static String[] ContactPhotoUriAndDisplayName(Context context, String str) {
        String str2;
        String str3;
        String str4 = "";
        if (!TextUtils.isEmpty(str) && com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils.hasPermission(context, "android.permission.READ_CONTACTS")) {
            Cursor query = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(str.trim())), new String[]{"photo_uri", "_id", "display_name"}, null, null, null);
            String str5 = null;
            if (query != null) {
                try {
                    try {
                        if (query.moveToFirst()) {
                            str2 = query.getString(query.getColumnIndex("_id"));
                            try {
                                str3 = query.getString(query.getColumnIndexOrThrow("photo_uri"));
                            } catch (Exception unused) {
                                str3 = null;
                            }
                            try {
                                str5 = str2;
                                str4 = query.getString(query.getColumnIndex("display_name"));
                            } catch (Exception unused2) {
                                query.close();
                                str5 = str2;
                                str4 = null;
                                return new String[]{str5, str3, str4};
                            }
                        } else {
                            str3 = null;
                            str4 = null;
                        }
                    } catch (Exception unused3) {
                        str3 = null;
                        str2 = null;
                    }
                } finally {
                    query.close();
                }
            } else {
                str3 = null;
                str4 = null;
            }
            return new String[]{str5, str3, str4};
        }
        return new String[]{"", "", ""};
    }



    public static String getContactPhoneNumber(Context context, String contactName) {

        if (contactName == null || contactName.isEmpty()) {
            return null;
        }


        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };


        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{contactName};


        String phoneNumber = null;


        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {

                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        } catch (Exception e) {
            Log.e("ContactUtils", "Error fetching phone number", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return phoneNumber;
    }
    public static String getContactName(Context context, String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            if (TelecomUtils.hasPermission(context, "android.permission.READ_CONTACTS")) {
                Cursor query = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(str)), new String[]{"display_name"}, null, null, null);
                if (query == null) {
                    return "";
                }
                String string = query.moveToFirst() ? query.getString(query.getColumnIndexOrThrow("display_name")) : null;
                if (!query.isClosed()) {
                    query.close();
                }
                return string != null ? string : z ? str : "";
            }
        } catch (Exception unused) {
        }

        return z ? str : "";
    }





}
