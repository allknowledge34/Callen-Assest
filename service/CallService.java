package com.phonecontactscall.contectapp.phonedialerr.service;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.phonecontactscall.contectapp.phonedialerr.Glob.isConference;
import static com.phonecontactscall.contectapp.phonedialerr.Glob.isONOffSpeaker;
import static com.phonecontactscall.contectapp.phonedialerr.Glob.isbluetoothconnect;
import static com.phonecontactscall.contectapp.phonedialerr.Glob.isbluetoothconnect_new;
import static com.phonecontactscall.contectapp.phonedialerr.Glob.isconfere;
import static com.phonecontactscall.contectapp.phonedialerr.Glob.iscounttime;
import static com.phonecontactscall.contectapp.phonedialerr.Glob.iscoutnew;
import static com.phonecontactscall.contectapp.phonedialerr.Util.CallUtils.callMain;
import static com.phonecontactscall.contectapp.phonedialerr.Util.CallUtils.secondaryCall;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.callActivity;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.call_status_label;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.imgPhotouri;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.ivHold;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.iv_addCall;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.ll_call_holder;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.relAddCall;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.relMergeCall;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.rel_add_people;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.tvaddCall;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.txtName;
import static com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity.txt_hold_number;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.phonecontactscall.contectapp.phonedialerr.Glob;
import com.phonecontactscall.contectapp.phonedialerr.R;
import com.phonecontactscall.contectapp.phonedialerr.Util.CallStateManager;
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactHelper;
import com.phonecontactscall.contectapp.phonedialerr.activity.CallActivity;
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper;
import com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils;
import com.phonecontactscall.contectapp.phonedialerr.Util.CallUtils;
import com.phonecontactscall.contectapp.phonedialerr.Util.NotificationUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CallService extends InCallService {
    Call mCall;
    public static List<Call> activeCalls = new ArrayList<>();
    public static Set<Call> mergedCalls = new HashSet<>();

    OnListenerCall onListenerCall = new OnListenerCall();
    public static boolean isMerging = false;
    public static boolean iscallcut = false;
    public static boolean alwaysfullscreen = true;

    private int lastCallState = -1;

    public class OnListenerCall extends Call.Callback {
        public OnListenerCall() {

        }

        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            if (call == null) {
                return;
            }
            Glob.callstate = state;

            updateCallReferences();
            updateAddCallButtonState();

            if (call.getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)) {
                EventBus.getDefault().post("CONFERENCE_CALL_STARTED");
            }

            callMain = call;


            if(callActivity!=null){
                callActivity.checkIfCallsAreMerged(callMain);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (iscounttime) {
                        callMain = call;
                    } else {

                    }

                }
            }, 500);


            switch (state) {
                case Call.STATE_RINGING:

                    ivHold.setEnabled(false);
                    if (activeCalls.size() >= 2 && isConference) {
                        holdConferenceCalls();
                    }



                    break;


                case Call.STATE_HOLDING:
                    checkAndMergeCalls();


                    try {
                        String contactName = ContactHelper.getContactName(
                                getApplicationContext(),
                                call.getDetails().getHandle().getSchemeSpecificPart(),
                                true
                        );

                        if (contactName != null && !contactName.isEmpty()) {

                            txt_hold_number.setText(contactName);
                        } else {
                            ll_call_holder.setVisibility(GONE);
                            txt_hold_number.setText("Unknown Caller (On Hold)");
                        }
                    } catch (Exception e) {

                    }
                    break;
                case Call.STATE_ACTIVE:

                    try {
                        if(callActivity!=null){
                            ivHold.setEnabled(true);

                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    NotificationUtils.createAcceptDeclineNotification(call, CallService.this.getApplicationContext());
                    isMerging = false;


                    EventBus.getDefault().post(TelecomUtils.OutgoingCallAnswered);

                    if (isIncomingCall(call)) {
                        holdConferenceCalls();
                    }

                    if (activeCalls.size() >= 2) {

                    } else {
                        try {
                            if(callActivity!=null){

                                relMergeCall.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                        }
                    }


                    break;
                case Call.STATE_DISCONNECTED:


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (activeCalls.size() <= 2) {
                                if (rel_add_people != null) {
                                    rel_add_people.setVisibility(View.GONE);

                                }

                            } else {
                                if (rel_add_people != null) {
                                    rel_add_people.setVisibility(VISIBLE);
                                    relMergeCall.setVisibility(GONE);

                                }
                            }
                        }
                    }, 10);


                    if (isconfere) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                callActivity.updateContactDetails();
                                callActivity.startCallDurationUpdater();
                                isconfere = true;
                                CallStateManager.getInstance().setMainConferenceCall(callMain);
                            }
                        }, 1000);

                    }

                    if (isMerging) {

                        if (!iscoutnew) {
                            iscounttime = true;

                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                iscoutnew = false;
                            }
                        }, 500);

                        relMergeCall.setVisibility(GONE);
                        txtName.setText(getString(R.string.str_cnference_call));
                        txtName.setVisibility(View.VISIBLE);
                        imgPhotouri.setBackgroundResource(R.drawable.ic_contact_1);
                        iv_addCall.setAlpha(1f);
                        tvaddCall.setAlpha(1f);
                        relAddCall.setEnabled(true);
                    } else {
                        if (call.equals(CallStateManager.getInstance().getIncomingCall())) {
                            CallStateManager.getInstance().clearIncomingCall();
                        }

                        if (callMain != null && callMain.getState() == Call.STATE_HOLDING) {
                            callMain.unhold();
                            EventBus.getDefault().post("KEY_CALLLOG_UPDATE");
                            ll_call_holder.setVisibility(GONE);
                            relMergeCall.setVisibility(GONE);
                            call_status_label.setText(getString(R.string.str_call_activity_callend));
                        } else {

                            Glob.callend = true;
                            if(callActivity!=null){
                                call_status_label.setText(getString(R.string.str_call_ended));

                            }

                            NotificationUtils.removeNotificationFromID(CallService.this.getApplicationContext(), 1008);
                            EventBus.getDefault().post(TelecomUtils.OutgoingCallDISCONNECTED);
                        }

                        Glob.call_pass = true;
                    }

                    break;

                case Call.STATE_DIALING:

                    if (ivHold != null) {

                        callActivity.ivHold.setEnabled(false);
                    }
                    if (!CallService.this.isForeground(CallActivity.class.getName())) {
                        Intent intent = new Intent(CallService.this.getApplicationContext(), CallActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        intent.putExtra("state", TelecomUtils.OutgoingCallStarted);
                        CallService.this.getApplicationContext().startActivity(intent);
                    }
                    break;
                default:
            }


            CallService.this.lastCallState = call.getState();
            EventBus.getDefault().post("KEY_CALLLOG_UPDATE");
        }

        @Override
        public void onConferenceableCallsChanged(Call call, List<Call> conferenceableCalls) {
            super.onConferenceableCallsChanged(call, conferenceableCalls);

            if (conferenceableCalls != null && !conferenceableCalls.isEmpty()) {
                EventBus.getDefault().post("CONFERENCE_CALL_STARTED");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isMerging = true;


                    }
                }, 1000);

            } else {
            }
        }

    }

    public void holdConferenceCalls() {
        for (Call call : activeCalls) {
            if (call != null && call.getState() == Call.STATE_ACTIVE && !call.equals(mCall)) {
                call.hold();

            }
        }
    }

    private boolean isIncomingCall(Call call) {
        return call.getState() == Call.STATE_RINGING;
    }

    public void acceptIncomingCall(Call incomingCall) {
        if (incomingCall != null) {
            incomingCall.answer(0);


            if (isConferenceCallActive()) {
                holdConferenceCalls();
            }
        }
    }

    private boolean isConferenceCallActive() {
        for (Call call : activeCalls) {
            if (call.getState() == Call.STATE_ACTIVE && call.getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)) {

                callActivity.pauseTimer();
                call.hold();
                return true;
            }
        }
        return false;
    }

    private void updateAddCallButtonState() {
        try {

            boolean isAddCallEnabled = shouldEnableAddCallButton();

            if (isAddCallEnabled) {
                iv_addCall.setAlpha(1f);
                tvaddCall.setAlpha(1f);
                relAddCall.setEnabled(true);
            } else {
                iv_addCall.setAlpha(0.5f);
                tvaddCall.setAlpha(0.5f);
                relAddCall.setEnabled(false);
            }
        } catch (Exception e) {
        }
    }


    private boolean shouldEnableAddCallButton() {
        boolean isActiveCallsSizeValid = activeCalls.size() < 5;

        boolean isActivityForeground = isForeground(CallActivity.class.getName());

        boolean isMainCallActive = (callMain == null || callMain.getState() == Call.STATE_ACTIVE || callMain.getState() == Call.STATE_DISCONNECTED || callMain.getState() == Call.STATE_HOLDING);
        boolean isSecondaryCallValid = (secondaryCall == null  || secondaryCall.getState() == Call.STATE_ACTIVE || secondaryCall.getState() == Call.STATE_DISCONNECTED );
        boolean isConferenceValid = (!isConference || !isConferenceCallFull(callMain));

        return isActiveCallsSizeValid && isActivityForeground && isSecondaryCallValid && isMainCallActive && isConferenceValid;
    }

    private boolean isConferenceCallFull(Call call) {
        if (call == null) {
            return false;
        }
        if (call.getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)) {
            List<Call> conferenceableCalls = call.getConferenceableCalls();
            return conferenceableCalls != null && conferenceableCalls.size() >= 5;
        }
        return false;
    }




    private void checkAndMergeCalls() {
        if (activeCalls.size() >= 2) {
            TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            boolean canMerge = true;
            for (Call call : activeCalls) {
                if (!isCallMergeable(call)) {
                    canMerge = false;
                    break;
                }
            }

            if (canMerge) {
                ll_call_holder.setVisibility(View.GONE);
            } else {
                boolean hasHeldCall = false;
                for (Call call : activeCalls) {
                    if (call.getState() == Call.STATE_HOLDING) {
                        hasHeldCall = true;
                        break;
                    }
                }

                if (hasHeldCall) {
                    ll_call_holder.setVisibility(View.VISIBLE);
                } else {
                    ll_call_holder.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean isCallMergeable(Call call) {
        return call.getState() == Call.STATE_ACTIVE && call.getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        CallerHelper instances = CallerHelper.getInstances();
        if (instances != null) {
            instances.setInCallService(this);
        }
    }

    private void updateCallReferences() {
        callMain = null;
        secondaryCall = null;

        if (activeCalls != null && !activeCalls.isEmpty()) {
            callMain = activeCalls.get(0);

            if (activeCalls.size() > 1) {
                secondaryCall = activeCalls.get(1);
            }
        }

        if (callMain != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                NotificationUtils.createAcceptDeclineNotification(callMain, CallService.this.getApplicationContext());
            }

        } else {
            NotificationUtils.removeNotificationFromID(CallService.this.getApplicationContext(), 1008);
        }

    }

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        this.mCall = call;
        activeCalls.add(call);
        if (call == null) {
            return;
        }
        callMain = call;
        CallUtils.inCallService = this;

        if (activeCalls.size() == 1) {
            callMain = call;
        } else if (activeCalls.size() == 2) {
            secondaryCall = call;
        }
        if (call.getState() == Call.STATE_RINGING) {
            CallUtils.incomingCall = call;
            CallStateManager.getInstance().setIncomingCall(CallUtils.incomingCall);

        }

        call.registerCallback(this.onListenerCall);
        this.lastCallState = call.getState();

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean isScreenLocked = keyguardManager.isDeviceLocked();


        try {
            if ( Glob.isOutgoingCall(call) || isScreenLocked||alwaysfullscreen) {

                NotificationUtils.createAcceptDeclineNotification(call, CallService.this.getApplicationContext());


            } else {
                NotificationUtils.createAcceptDeclineNotification(call, CallService.this.getApplicationContext());

            }
        } catch (Exception e) {
            NotificationUtils.createAcceptDeclineNotification(call, CallService.this.getApplicationContext());

        }

        if (activeCalls.size() >= 2) {


            Intent intent = new Intent("com.example.CALL_STATUS");
            intent.putExtra("two_calls_active", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        if (call.getState() == 2) {

            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), CallActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("state", TelecomUtils.IncomingCallReceived);
            boolean isScreenOn = ((PowerManager) getApplicationContext().getSystemService("power")).isScreenOn();
            if (((KeyguardManager) getApplicationContext().getSystemService("keyguard")).isKeyguardLocked()) {
                intent.putExtra(TelecomUtils.IS_LOCK, false);
            } else if (!isScreenOn) {
                intent.putExtra(TelecomUtils.IS_LOCK, false);
            } else {
                intent.putExtra(TelecomUtils.IS_LOCK, true);
            }



        } else if (call.getState() == 9 && !isForeground(getApplicationContext().getPackageName())) {
            Intent intent = new Intent(getApplicationContext(), CallActivity.class);
            intent.setFlags(268435456);
            intent.putExtra("state", TelecomUtils.OutgoingCallStarted);
            getApplicationContext().startActivity(intent);
        }
        EventBus.getDefault().post("KEY_CALLLOG_UPDATE");
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        if (call.getState() == Call.STATE_DISCONNECTED) {
            activeCalls.remove(call);
            updateCallReferences();
            lastCallState = -1;
        }



    }

    public boolean isForeground(String str) {
        ComponentName componentName;
        try {
            List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) getSystemService("activity")).getRunningTasks(1);
            if (runningTasks.size() > 0) {
                componentName = runningTasks.get(0).topActivity;
                return componentName.getClassName().equals(str);
            }
        } catch (Exception unused) {
        }
        return false;
    }
    private boolean isBluetoothConnected() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled() && isbluetoothconnect==true) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
                int connectionState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
                if (connectionState == BluetoothProfile.STATE_CONNECTED) {

                    return true;
                }
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCallAudioStateChanged(CallAudioState audioState) {
        super.onCallAudioStateChanged(audioState);



        List<BluetoothDevice> bluetoothDevices = (List<BluetoothDevice>) audioState.getSupportedBluetoothDevices();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isBluetoothConnected() || !bluetoothDevices.isEmpty() ) {
                    isbluetoothconnect = true;
                } else {
                    isbluetoothconnect = false;
                }

            }
        },1000);
        if (audioState != null) {
            int currentRoute = audioState.getRoute();
            String routeName = "";
            String deviceName = "";
            switch (currentRoute) {
                case CallAudioState.ROUTE_BLUETOOTH:
                    routeName = "BLUETOOTH";
                    break;

                case CallAudioState.ROUTE_SPEAKER:
                    routeName = "SPEAKER";
                    break;

                case CallAudioState.ROUTE_WIRED_OR_EARPIECE:
                case CallAudioState.ROUTE_EARPIECE:
                    routeName = "PHONE";
                    break;

                default:
                    if (isbluetoothconnect_new) {
                        routeName = "UNKNOWN";
                        Log.w("CallAudioState", "Unknown audio route detected after Bluetooth disconnection");
                    }
                    break;
            }


            Intent intent = new Intent("com.contect.CALL_AUDIO_ROUTE_CHANGED");
            intent.putExtra("audioRoute", routeName);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            String finalRouteName = routeName;


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (callActivity != null) {



                    if(!isbluetoothconnect && isbluetoothconnect_new ){


                        if(isONOffSpeaker){
                            callActivity.ivSpeakDefault.setImageResource(R.drawable.ic_speaker_select);

                        }else {
                            callActivity.ivSpeakDefault.setImageResource(R.drawable.ic_speaker_unselect);

                        }
                        try {
                            callActivity.lin_bluetooth_view.setVisibility(GONE);
                            callActivity.defaultSpeackerVisible();
                        } catch (Exception e) {
                        }

                    }else {
                        if (finalRouteName.equals("BLUETOOTH")) {
                            isbluetoothconnect = true;
                            callActivity.BluetoothVisible();
                        }
                    }


                    }
                }
            },1000);



        }


    }


    @SuppressLint("MissingPermission")
    private String getConnectedBluetoothDeviceName() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        String deviceName = "";

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            @SuppressLint("MissingPermission") BluetoothDevice connectedDevice = bluetoothAdapter.getBondedDevices()
                    .stream()
                    .filter(device -> device.getBondState() == BluetoothDevice.BOND_BONDED)
                    .findFirst()
                    .orElse(null);

            if (connectedDevice != null) {
                deviceName = connectedDevice.getName();
            } else {
            }
        } else {
        }

        return deviceName;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Call call = this.mCall;
        if (call != null) {
            call.unregisterCallback(this.onListenerCall);
        }
        CallerHelper instances = CallerHelper.getInstances();
        if (instances != null) {
            instances.clearInCallService();
        }
    }
}
