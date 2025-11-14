package com.phonecontactscall.contectapp.phonedialerr.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.telecom.Call;
import android.telecom.InCallService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.phonecontactscall.contectapp.phonedialerr.Glob;
import com.phonecontactscall.contectapp.phonedialerr.R;
import com.phonecontactscall.contectapp.phonedialerr.callback.EventListener;

import java.util.Arrays;
import java.util.List;



public class CallerHelper {
    private static CallerHelper instance;
    private InCallService inCallService;

    public void clearInCallService() {
        this.inCallService = null;
    }

    public void setInCallService(InCallService inCallService) {
        this.inCallService = inCallService;
    }

    public static void showDialog(final TelecomManager telecomManager, final Context context, final String str, final EventListener eventListener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_sim_choose);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_theme;
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();



        LinearLayout textView = (LinearLayout) dialog.findViewById(R.id.lin_sim1);
        LinearLayout textView2 = (LinearLayout) dialog.findViewById(R.id.lin_sim2);
        if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            return;
        }
        final List<PhoneAccountHandle> callCapablePhoneAccounts = telecomManager.getCallCapablePhoneAccounts();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri fromParts = Uri.fromParts("tel", str, null);
                Bundle bundle = new Bundle();
                List list = callCapablePhoneAccounts;
                if (list != null && list.size() > 0) {
                    bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", (Parcelable) callCapablePhoneAccounts.get(0));
                    if (ActivityCompat.checkSelfPermission(context, "android.permission.CALL_PHONE") != 0) {
                        return;
                    }
                    EventListener eventListener2 = eventListener;
                    if (eventListener2 != null) {
                        eventListener2.onFinish();
                    }
                    telecomManager.placeCall(fromParts, bundle);
                }
                dialog.dismiss();
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri fromParts = Uri.fromParts("tel", str, null);
                Bundle bundle = new Bundle();
                List list = callCapablePhoneAccounts;
                if (list != null && list.size() > 1) {
                    bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", (Parcelable) callCapablePhoneAccounts.get(1));
                    if (ActivityCompat.checkSelfPermission(context, "android.permission.CALL_PHONE") != 0) {
                        return;
                    }
                    EventListener eventListener2 = eventListener;
                    if (eventListener2 != null) {
                        eventListener2.onFinish();
                    }
                    telecomManager.placeCall(fromParts, bundle);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }



    public static void callPhoneClick(Context context, String str) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService("telecom");

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        if (simState == TelephonyManager.SIM_STATE_ABSENT) {
            Glob.showToast(context, context.getString(R.string.str_no_sim));
            return;
        }
        PhoneAccountHandle defaultOutgoingPhoneAccount = TelecomUtils.getDefaultOutgoingPhoneAccount((Activity) context, telecomManager, "tel");
        if (defaultOutgoingPhoneAccount == null) {
            if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
                return;
            }
            if (isDefaultSimSetForCall(telecomManager, context)) {

                Uri fromParts = Uri.fromParts("tel", str, null);
                Bundle bundle = new Bundle();
                bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", defaultOutgoingPhoneAccount);
                if (ActivityCompat.checkSelfPermission(context, "android.permission.CALL_PHONE") != 0) {
                    return;
                }
                telecomManager.placeCall(fromParts, bundle);
                return;
            }

            showDialog(telecomManager, context, str, null);
            return;
        }
        Uri fromParts2 = Uri.fromParts("tel", str, null);
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", defaultOutgoingPhoneAccount);
        if (ActivityCompat.checkSelfPermission(context, "android.permission.CALL_PHONE") != 0) {
            return;
        }
        telecomManager.placeCall(fromParts2, bundle2);
    }


    public static boolean isDefaultSimSetForCall(TelecomManager telecomManager, Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") == 0 && telecomManager.getDefaultOutgoingPhoneAccount(Uri.fromParts("tel", "text", null).getScheme()) != null;
    }

    public static final CallerHelper getInstances() {
        if (instance == null) {
            instance = new CallerHelper();
        }
        return instance;
    }

    public static final boolean isMethodAvailable(String str, String str2, Class<?>... clsArr) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        if (str2 == null || str == null) {
            return true;
        }
        try {
            Class.forName(str).getMethod(str2, (Class[]) Arrays.copyOf(clsArr, clsArr.length));
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    public final List<Call> getAllCalls() {
        InCallService inCallService = this.inCallService;
        if (inCallService == null) {
            return null;
        }
        return inCallService.getCalls();
    }

}
