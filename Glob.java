package com.phonecontactscall.contectapp.phonedialerr;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.BlockedNumberContract;
import android.telecom.Call;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.phonecontactscall.contectapp.phonedialerr.activity.DialpadActivity;
import com.phonecontactscall.contectapp.phonedialerr.Model.ContactModel;
import com.phonecontactscall.contectapp.phonedialerr.Model.SIMAccount;
import com.phonecontactscall.contectapp.phonedialerr.Model.SpeedDial;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import kotlin.jvm.internal.Intrinsics;

public class Glob {

    public static Glob sInstance = null;
    public static Context activity = null;
    public static Glob myApplication = null;
    public static ArrayList<ContactModel> all_contect_list = new ArrayList<>();
    public static boolean callend = false;
    public static boolean iscounttime = false;
    public static boolean accept_call = false;
    public static boolean iscoutnew = false;
    public static boolean isphonecall = false;
    public static boolean isONOffSpeaker = false;
    public static boolean calldurationstart = false;
    public static boolean receivenotification = false;
    public static boolean isthemechnage = false;
    public static boolean noticication_acepticlick = false;
    public static boolean isseting = false;
    public static boolean isReceiverRegistered = false;

    private static SharedPreferences prefs = null;
    public static SharedPreferences mSharedPreferences;
    public static SharedPreferences mSharedPreferences_searchenging;
    public static SharePrefLang sharePrefLang;
    private static String PREFS_NAME = "CustomSIMPrefs";





    public static boolean delte_number = false;
    public static boolean edit_number = false;
    public static boolean serach_number = false;
    public static boolean edit_number_fav = false;
    public static boolean edit_number_fav_detail = false;
    public static boolean fav_remove = false;
    public static boolean iscontect = false;
    public static boolean israting = false;
    public static int rate_value_contect = 0;
    public static boolean call_pass = false;
    public static boolean isConference = false;
    public static boolean isconfere = false;
    public static boolean view_incoming = false;

    public static boolean isbluetoothconnect = false;
    public static boolean isbluetoothconnect_new = false;
    public static boolean isaddcall;
    public static int callstate;

    public Glob(Context context) {
        sharePrefLang = SharePrefLang.Companion.getInstance(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mSharedPreferences_searchenging = PreferenceManager.getDefaultSharedPreferences(context);
        myApplication = this;
        activity = context;
    }

    public static Glob getInstance() {
        return sInstance;
    }

    public static int getResourceFromAttr(int attr, Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();

        theme.resolveAttribute(attr, typedValue, true);

        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
                typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return typedValue.data;
        } else {
            return typedValue.resourceId;
        }
    }

    public static void string_update_Language(Context context) {
        String string = Glob.getLanValue(context,"en");
        Locale locale = Locale.getDefault();
        if (string == null || string.isEmpty()) {
            locale = context.getResources().getConfiguration().locale;
            string = locale.getLanguage();
        }

        if (string == null || string.isEmpty()) return;


        locale = new Locale(string.toLowerCase());
        Locale.setDefault(locale);
        context.getResources().getConfiguration().setLocale(locale);
        context.getResources().getConfiguration().setLayoutDirection(locale);
        context.getResources().updateConfiguration(context.getResources().getConfiguration(), context.getApplicationContext().getResources().getDisplayMetrics());
    }

    public static void showToast(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        TextView textView = layout.findViewById(R.id.text_message);
        textView.setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.show();
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    public static boolean isDefaultDialer(Context context) {
        String packageName = context.getPackageName();

        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        RoleManager roleManager = (RoleManager) context.getSystemService(Context.ROLE_SERVICE);

        if (isQPlus()) {
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER) && roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                return true;
            } else {
                return false;
            }
        } else if (isMarshmallowPlus() && telecomManager.getDefaultDialerPackage().equals(packageName)) {
            return true;
        } else {
            return false;
        }
    }


    private static boolean isMarshmallowPlus() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public static ArrayList<SIMAccount> getAvailableSIMCardLabels(Context context) {
        ArrayList<SIMAccount> arrayList = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return arrayList;
        }

        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        List<PhoneAccountHandle> phoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();

        for (int index = 0; index < phoneAccountHandles.size(); index++) {
            PhoneAccountHandle phoneAccountHandle = phoneAccountHandles.get(index);
            PhoneAccount phoneAccount = telecomManager.getPhoneAccount(phoneAccountHandle);
            String label = phoneAccount.getLabel().toString();
            String uri = phoneAccount.getAddress().toString();

            if (uri.startsWith("tel:")) {
                uri = Uri.decode(uri.substring("tel:".length()));
            }

            arrayList.add(new SIMAccount(index + 1, phoneAccountHandle, label, uri));
        }

        return arrayList;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    public static boolean addToBlock(Context context, String phoneNumber) {
        try {
            String blockNumber = phoneNumber.replace(" ", "");
            Uri uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI;
            ContentValues values = new ContentValues();

            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, blockNumber);
            context.getContentResolver().insert(uri, values);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    public static boolean removeFromBlock(Context context, String phoneNumber) {
        try {
            String blockNumber = phoneNumber.replace(" ", "");
            Uri uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI;
            String selection = BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER + " = ?";
            String[] selectionArgs = new String[]{blockNumber};

            int deletedRows = context.getContentResolver().delete(uri, selection, selectionArgs);

            return deletedRows > 0;
        } catch (Exception e) {
            return false;
        }
    }



    public static Pattern getNormalizedRegex() {
        return Pattern.compile("some regex pattern");
    }

    public static Glob init(Context context) {
        if (sInstance == null) {
            sInstance = new Glob(context);
        }
        return sInstance;
    }

    private static final String Select_Lunnn = "Select_Lang";

    public static String getLanValue(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Select_Lunnn, defaultLanguage);
    }

    public static void LansetValue(Activity activity, String name) {
        if (activity == null) return;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Select_Lunnn, name);
        editor.apply();
    }
    public static void setLanguage(Activity activity, String name) {

        if (name == null) return;

        Locale locale = new Locale(name);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        activity.getBaseContext().getResources().updateConfiguration(
                config,
                activity.getBaseContext().getResources().getDisplayMetrics()
        );
    }
    public static String check_theme() {
        return prefs.getString("is_using_theme", "System_theme");
    }

    public static String setUsingSystemTheme(String string) {
        prefs.edit().putString("is_using_theme", string).apply();

        switch (string) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        return string;
    }


    public static void SetStatusbarColor(Window window){
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        String theme = Glob.check_theme();



        int flags = window.getDecorView().getSystemUiVisibility();
        switch (theme) {
            case "light":
                flags = flags | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);

                break;
            case "dark":
                flags = flags & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR ;
                window.getDecorView().setSystemUiVisibility(flags);
                break;
            case "System_theme":
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    flags = flags & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR ;
                    window.getDecorView().setSystemUiVisibility(flags);

                } else {
                    flags = flags | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    window.getDecorView().setSystemUiVisibility(flags);

                }
                break;
        }
    }



    public static boolean isConference(Call call) {
        Call.Details details = null;
        if (call == null || (details = call.getDetails()) == null || !details.hasProperty(1)) {
            return false;
        }
        return true;
    }



    public void SetBoolean(Context context, String key, boolean str) {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        }
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(key, str);
        edit.commit();
    }

    public boolean GetBoolean(Context context, String key) {
        if (mSharedPreferences == null) {

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return mSharedPreferences.getBoolean(key, false);
    }


    public static Rect getBoundingBox(View view) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect;
    }
    public static boolean isOutgoingCall(Call call) {
        return call != null && call.getState() == Call.STATE_DIALING;
    }
    public static boolean isQPlus() {
        return Build.VERSION.SDK_INT >= 29;
    }

    public static boolean isRPlus() {
        return Build.VERSION.SDK_INT >= 30;
    }

    public static boolean isOreoPlus() {
        return Build.VERSION.SDK_INT >= 26;
    }

    public static String getSpeedDial(DialpadActivity dialpadActivity) {
        SharedPreferences prefs = dialpadActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("speed_dial", "");
    }

    public static ArrayList<SpeedDial> getSpeedDialValues(DialpadActivity dialpadActivity) {
        String jsonString = getSpeedDial(dialpadActivity);
        java.lang.reflect.Type type = new TypeToken<List<SpeedDial>>() {
        }.getType();
        ArrayList<SpeedDial> arrayList = new Gson().fromJson(jsonString, type);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        for (int i = 1; i <= 9; i++) {
            boolean exists = false;
            for (SpeedDial speedDial : arrayList) {
                if (speedDial.getId() == i) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                arrayList.add(new SpeedDial(i, "", ""));
            }
        }
        return arrayList;
    }

}