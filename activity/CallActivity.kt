package com.phonecontactscall.contectapp.phonedialerr.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Glob.accept_call
import com.phonecontactscall.contectapp.phonedialerr.Glob.calldurationstart
import com.phonecontactscall.contectapp.phonedialerr.Glob.isConference
import com.phonecontactscall.contectapp.phonedialerr.Glob.isONOffSpeaker
import com.phonecontactscall.contectapp.phonedialerr.Glob.isReceiverRegistered
import com.phonecontactscall.contectapp.phonedialerr.Glob.isbluetoothconnect
import com.phonecontactscall.contectapp.phonedialerr.Glob.isbluetoothconnect_new
import com.phonecontactscall.contectapp.phonedialerr.Glob.isconfere
import com.phonecontactscall.contectapp.phonedialerr.Glob.iscounttime
import com.phonecontactscall.contectapp.phonedialerr.Glob.noticication_acepticlick
import com.phonecontactscall.contectapp.phonedialerr.Glob.receivenotification
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils
import com.phonecontactscall.contectapp.phonedialerr.Util.CallStateManager
import com.phonecontactscall.contectapp.phonedialerr.Util.CallUtils
import com.phonecontactscall.contectapp.phonedialerr.Util.CallUtils.callMain
import com.phonecontactscall.contectapp.phonedialerr.Util.CallUtils.hasCapability
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactHelper
import com.phonecontactscall.contectapp.phonedialerr.Util.NotificationUtils
import com.phonecontactscall.contectapp.phonedialerr.adapter.ActiveCallAdapter
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityCallNewBinding
import com.phonecontactscall.contectapp.phonedialerr.reciver.callStatusReceiver
import com.phonecontactscall.contectapp.phonedialerr.service.CallService
import com.phonecontactscall.contectapp.phonedialerr.service.CallService.activeCalls
import com.phonecontactscall.contectapp.phonedialerr.service.CallService.isMerging
import com.makeramen.roundedimageview.RoundedImageView
import com.ncorti.slidetoact.SlideToActView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.Locale
import java.util.Timer
import java.util.TimerTask


class CallActivity : AppCompatActivity(), View.OnClickListener,
    SensorEventListener {

    lateinit var imgNum0: FrameLayout
    lateinit var imgNum1: FrameLayout
    lateinit var imgNum2: FrameLayout
    lateinit var imgNum3: FrameLayout
    lateinit var imgNum4: FrameLayout
    lateinit var imgNum5: FrameLayout
    lateinit var imgNum6: FrameLayout
    lateinit var imgNum7: FrameLayout
    lateinit var imgNum8: FrameLayout
    lateinit var imgNum9: FrameLayout
    lateinit var imgNumStar: FrameLayout
    lateinit var imgNumHash: FrameLayout
    var iscallActive: Boolean = false
    lateinit var ivClose: ImageView

    lateinit var tvdialkeypad: TextView
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private lateinit var telephonyManager: TelephonyManager
    var isMuteConfig: Boolean = true
    var countTime: Int = 0
    var bluetoothname: String = ""
    private var timer: Timer? = null
    lateinit var callReceiver: callStatusReceiver
    var message_dialogue: Dialog? = null
    var manage_call_dialogue: Dialog? = null

    lateinit var binding: ActivityCallNewBinding


    companion object {
        lateinit var callActivity: CallActivity

        var isTimerRunning = false

        lateinit var ll_call_holder: ConstraintLayout
        lateinit var txt_hold_name: TextView
        lateinit var txt_hold_number: TextView
        lateinit var call_status_label: TextView
        lateinit var txtName: TextView
        lateinit var tvaddCall: TextView
        lateinit var imgPhotouri: RoundedImageView
        lateinit var relMergeCall: RelativeLayout
        lateinit var lin_bluetooth_view: LinearLayout
        lateinit var relAddCall: RelativeLayout
        lateinit var rel_add_people: RelativeLayout
        lateinit var iv_addpeopleCall: ImageView
        lateinit var ivSpeakDefault: ImageView
        lateinit var iv_addCall: ImageView
        lateinit var ivHold: ImageView


        lateinit var duratHandler: Handler
        var isRunning: Boolean = false

        lateinit var uCallDurationTask: Runnable
    }

    private fun removeCallDurationCallBack() {
        try {
                duratHandler.removeCallbacks(uCallDurationTask)
        } catch (_: Exception) {
        }
    }


    override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
        super.onApplyThemeResource(theme, resid, first)

        try {

            if (calldurationstart) {

                calldurationstart = false
                updateContactDetails1()


                if (duratHandler == null) {
                    duratHandler = Handler(Looper.getMainLooper())
                }

                duratHandler?.post(uCallDurationTask!!)

            }

        } catch (e: Exception) {
        }
    }


    private val audioRouteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val audioRoute = intent.getStringExtra("audioRoute")


            val color = resources.getColor(R.color.second_text_color)
            val selectColor = resources.getColor(R.color.white)


            when (audioRoute) {
                "BLUETOOTH" -> {

                    isbluetoothconnect = true
                    bluetoothname = getString(R.string.str_bluetooth)
                    updateUIForBluetoothRoute(selectColor, color)
                    registerAudioRouteReceiver()
                }

                "SPEAKER" -> {
                    updateUIForSpeakerRoute(selectColor, color)
                    unregisterAudioRouteReceiver()
                }

                "PHONE" -> {
                    updateUIForPhoneRoute(selectColor, color)
                    unregisterAudioRouteReceiver()
                }


                else -> {
                    isbluetoothconnect = false
                    binding.ivBluetoothConnect.visibility = View.GONE
                    binding.ivPhoneConnect.visibility = View.GONE
                    binding.ivSpeakConnect.visibility = View.GONE
                    ivSpeakDefault.visibility = View.VISIBLE
                }
            }

            setupClickListeners()
        }
    }

    private fun registerAudioRouteReceiver() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
            audioRouteReceiver,
            IntentFilter("com.contect.CALL_AUDIO_ROUTE_CHANGED")
        )
    }

    private fun unregisterAudioRouteReceiver() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(audioRouteReceiver)
        isReceiverRegistered = false
        isbluetoothconnect_new = true

    }

    fun BluetoothVisible() {
        val color = resources.getColor(R.color.second_text_color)
        val selectColor = resources.getColor(R.color.white)

        binding.ivBluetoothConnect.visibility = View.VISIBLE
        ivSpeakDefault.visibility = View.GONE
        binding.txtSepecker.text = getString(R.string.str_bluetooth)
        binding.ivBluetoothConnect.setOnClickListener {

            if (isbluetoothconnect) {
                lin_bluetooth_view.visibility = View.VISIBLE
                setAudioRoute(AudioManager.MODE_IN_COMMUNICATION)
                CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_BLUETOOTH)
                updateUIForBluetoothRoute(
                    selectColor, color
                )
            } else {
            }
        }
    }

    fun defaultSpeackerVisible() {
        binding.txtSepecker.text = getString(R.string.str_speaker)
        binding.ivPhoneConnect.visibility = View.GONE
        binding.ivBluetoothConnect.visibility = View.GONE
        binding.ivSpeakConnect.visibility = View.GONE
        ivSpeakDefault.visibility = View.VISIBLE
    }

    private fun setupClickListeners() {

        val color = resources.getColor(R.color.second_text_color)
        val selectColor = resources.getColor(R.color.white)

        binding.ivBluetoothConnect.setOnClickListener {

            if (isbluetoothconnect) {
                lin_bluetooth_view.visibility = View.VISIBLE
                setAudioRoute(AudioManager.MODE_IN_COMMUNICATION)
                CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_BLUETOOTH)
                updateUIForBluetoothRoute(
                    selectColor, color
                )
            } else {
            }
        }

        binding.ivSpeakConnect.setOnClickListener {
            lin_bluetooth_view.visibility = View.VISIBLE
            setAudioRoute(AudioManager.MODE_NORMAL)
            CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_SPEAKER)
            lin_bluetooth_view.visibility = View.VISIBLE
            updateUIForSpeakerRoute(selectColor, color)

        }

        binding.ivPhoneConnect.setOnClickListener {

            Glob.isphonecall = true
            lin_bluetooth_view.visibility = View.VISIBLE
            setAudioRoute(AudioManager.MODE_IN_CALL)
            CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_EARPIECE)
            lin_bluetooth_view.visibility = View.VISIBLE
            ivSpeakDefault.visibility = View.GONE
            updateUIForPhoneRoute(
                selectColor, color
            )
        }
    }


    private fun updateUIForBluetoothRoute(
        selectColor: Int,
        color: Int
    ) {
        binding.ivBluetoothConnect.visibility = View.VISIBLE
        binding.ivPhoneConnect.visibility = View.GONE
        binding.ivSpeakConnect.visibility = View.GONE
        ivSpeakDefault.visibility = View.GONE
        binding.txtSepecker.text = getString(R.string.str_bluetooth)
        binding.ivBluetooth.setImageResource(R.drawable.ic_bluetooth_small_size)
        binding.ivPhone.setImageResource(R.drawable.ic_phone_small_size_unselect)
        binding.ivSpeaker.setImageResource(R.drawable.ic_speaker_small_size_unselect)
        binding.txtBluetooth.setTextColor(selectColor)
        binding.txtSpeakerCall.setTextColor(color)
        binding.txtPhone.setTextColor(color)
        binding.ivBluetooth.performClick()
    }

    private fun updateUIForSpeakerRoute(selectColor: Int, color: Int) {
        if (isbluetoothconnect) {
            binding.ivSpeakConnect.setImageResource(R.drawable.ic_speaker)
            ivSpeakDefault.visibility = View.GONE
            binding.ivPhoneConnect.visibility = View.GONE
            binding.ivBluetoothConnect.visibility = View.GONE
            binding.ivSpeakConnect.visibility = View.VISIBLE

            binding.ivBluetooth.setImageResource(R.drawable.ic_bluetooth_small_size_unselect)
            binding.ivPhone.setImageResource(R.drawable.ic_phone_small_size_unselect)
            binding.ivSpeaker.setImageResource(R.drawable.ic_speaker_small_size)

            binding.txtSepecker.text = getString(R.string.str_speaker)
            binding.txtBluetooth.setTextColor(color)
            binding.txtSpeakerCall.setTextColor(selectColor)
            binding.txtPhone.setTextColor(color)
        } else {
            ivSpeakDefault.visibility = View.VISIBLE
        }

    }

    private fun updateUIForPhoneRoute(selectColor: Int, color: Int) {


        binding.ivPhoneConnect.setImageResource(R.drawable.ic_phone)

        ivSpeakDefault.visibility = View.GONE
        binding.ivPhoneConnect.visibility = View.VISIBLE
        binding.ivBluetoothConnect.visibility = View.GONE
        binding.ivSpeakConnect.visibility = View.GONE

        binding.ivBluetooth.setImageResource(R.drawable.ic_bluetooth_small_size_unselect)
        binding.ivPhone.setImageResource(R.drawable.ic_phone_small_size)
        binding.ivSpeaker.setImageResource(R.drawable.ic_speaker_small_size_unselect)

        binding.txtSepecker.text = getString(R.string.str_phone)
        binding.txtBluetooth.setTextColor(color)
        binding.txtSpeakerCall.setTextColor(color)
        binding.txtPhone.setTextColor(selectColor)
    }

    fun Int.durationFormatted(forceShowHours: Boolean = false): String {
        val sb = StringBuilder(8)
        val hours = this / 3600
        val minutes = this % 3600 / 60
        val seconds = this % 60

        if (this >= 3600) {
            sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
        } else if (forceShowHours) {
            sb.append("0:")
        }

        sb.append(String.format(Locale.getDefault(), "%02d", minutes))
        sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
        return sb.toString()
    }


    private fun startSensor() {


        if (!wakeLock.isHeld) {
            wakeLock.acquire(10 * 60 * 1000L)
        }

        proximitySensor?.let {
            val isRegistered =
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            if (isRegistered) {
                val newWakeLock = (getSystemService("power") as PowerManager).newWakeLock(
                    32, java.lang.String.valueOf(
                        applicationContext
                    )
                )


                this.wakeLock = newWakeLock
                newWakeLock.acquire()

            } else {
            }
        }
    }

    private fun turnScreenOn() {
        if (!wakeLock.isHeld) {
            wakeLock.acquire(10 * 60 * 1000L)
        }
    }


    private fun stopSensor() {

        if (wakeLock.isHeld) {
            wakeLock.acquire(1) // Ensure proper release
            wakeLock.release()
        }

        sensorManager.unregisterListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        )


    }


    private fun isBluetoothConnected(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            for (device in bluetoothAdapter.bondedDevices) {
                val connectionState =
                    bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET)
                if (connectionState == BluetoothProfile.STATE_CONNECTED) {
                    return true
                }
            }
        }
        return false
    }

    fun setTranslucentNavigation() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTranslucentNavigation()
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        callActivity = this

        duratHandler = Handler(Looper.getMainLooper())


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager



        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "com.phonecontactscall.contectapp.phonedialer:WakeLock"
        )
        wakeLock?.acquire()


        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        try {
            telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE -> {
                            stopSensor()
                        }

                        TelephonyManager.CALL_STATE_RINGING -> {
                            turnScreenOn()
                            stopSensor()
                        }

                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            startSensor()
                        }
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
        }


        var details: Call.Details? = null
        var str: String? = null
        var handle: Uri? = null
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        rel_add_people = findViewById(R.id.rel_add_people)
        iv_addpeopleCall = findViewById(R.id.iv_addpeopleCall)
        ivSpeakDefault = findViewById(R.id.iv_speak_default)
        relMergeCall = findViewById(R.id.rel_merge_call)
        lin_bluetooth_view = findViewById(R.id.lin_bluetooth_view)
        txt_hold_name = findViewById(R.id.txt_hold_name)
        txt_hold_number = findViewById(R.id.txt_hold_number)
        txtName = findViewById(R.id.txt_name)
        tvaddCall = findViewById(R.id.tv_addCall)
        imgPhotouri = findViewById(R.id.img_photouri)
        call_status_label = findViewById(R.id.call_status_label)
        ll_call_holder = findViewById(R.id.ll_call_holder)
        relAddCall = findViewById(R.id.rel_add_call)
        iv_addCall = findViewById(R.id.iv_addCall)
        ivHold = findViewById(R.id.iv_hold)

        callReceiver = callStatusReceiver(relMergeCall, relAddCall)


        val bAdapter = BluetoothAdapter.getDefaultAdapter()


        if (bAdapter.isEnabled) {
            if (isBluetoothConnected() && isbluetoothconnect) {

                BluetoothVisible()
            } else {
                defaultSpeackerVisible()
                isbluetoothconnect = false;
            }

        }

        registerAudioRouteReceiver();


        imgNum0 = findViewById<FrameLayout>(R.id.imgNum0)
        imgNum1 = findViewById<FrameLayout>(R.id.imgNum1)
        imgNum2 = findViewById<FrameLayout>(R.id.imgNum2)
        imgNum3 = findViewById<FrameLayout>(R.id.imgNum3)
        imgNum4 = findViewById<FrameLayout>(R.id.imgNum4)
        imgNum5 = findViewById<FrameLayout>(R.id.imgNum5)
        imgNum6 = findViewById<FrameLayout>(R.id.imgNum6)
        imgNum7 = findViewById<FrameLayout>(R.id.imgNum7)
        imgNum8 = findViewById<FrameLayout>(R.id.imgNum8)
        imgNum9 = findViewById<FrameLayout>(R.id.imgNum9)
        imgNumStar = findViewById<FrameLayout>(R.id.imgNumStar)
        imgNumHash = findViewById<FrameLayout>(R.id.imgNumHash)
        imgNum0.setOnClickListener(this)
        imgNum1.setOnClickListener(this)
        imgNum2.setOnClickListener(this)
        imgNum3.setOnClickListener(this)
        imgNum4.setOnClickListener(this)
        imgNum5.setOnClickListener(this)
        imgNum6.setOnClickListener(this)
        imgNum7.setOnClickListener(this)
        imgNum8.setOnClickListener(this)
        imgNum9.setOnClickListener(this)
        imgNumStar.setOnClickListener(this)
        imgNumHash.setOnClickListener(this)


        if (isONOffSpeaker) {
            ivSpeakDefault.setImageResource(R.drawable.ic_speaker_select);

        } else {
            ivSpeakDefault.setImageResource(R.drawable.ic_speaker_unselect);

        }
        this.tvdialkeypad = (findViewById(R.id.txtKeypadDial) as TextView)!!
        val findViewById: View = findViewById(R.id.imgNum0)
        val findViewById2: View = findViewById(R.id.imgNumStar)
        val findViewById3: View = findViewById(R.id.imgNumHash)
        (findViewById.findViewById<View>(R.id.dialpad_key_number) as TextView).text =
            "0"
        (findViewById.findViewById<View>(R.id.dialpad_key_letters) as TextView).text = "+"
        (findViewById.findViewById<View>(R.id.dialpad_key_letters) as TextView).setTextSize(
            0,
            resources.getDimension(R.dimen._12sdp)
        )
        (findViewById2.findViewById<View>(R.id.dialpad_key_letters) as TextView).setText(
            "*"
        )
        (findViewById3.findViewById<View>(R.id.dialpad_key_letters) as TextView).text =
            "#"
        val iArr = intArrayOf(
            R.id.imgNum2,
            R.id.imgNum3,
            R.id.imgNum4,
            R.id.imgNum5,
            R.id.imgNum6,
            R.id.imgNum7,
            R.id.imgNum8,
            R.id.imgNum9
        )
        val iArr2 = intArrayOf(
            R.string.key_two,
            R.string.key_three,
            R.string.key_four,
            R.string.key_five,
            R.string.key_six,
            R.string.key_seven,
            R.string.key_eight,
            R.string.key_nine
        )
        for (i in 0 until 8) {
            findViewById<FrameLayout>(iArr[i]).findViewById<TextView>(R.id.dialpad_key_letters).text =
                iArr2[i].toString()
            findViewById<FrameLayout>(iArr[i]).findViewById<TextView>(R.id.dialpad_key_number).text =
                (i + 2).toString()
        }
        val imageView = findViewById(R.id.ivClose) as ImageView
        this.ivClose = imageView
        imageView.setOnClickListener { view ->
            finish()
        }

        var stringExtra = intent.getStringExtra("state")
        if (TextUtils.isEmpty(stringExtra)) {
            this.ivClose.visibility = View.VISIBLE
            stringExtra = TelecomUtils.startCall
        }


        if (stringExtra != null) {
            stringExtra.hashCode()

            when (stringExtra.hashCode()) {


                -1554560808 -> if (stringExtra == TelecomUtils.OutgoingCallStarted) {


                    if (callMain != null) {

                        callMain.answer(0)
                    }
                    binding.incomingCallHolder.visibility = View.INVISIBLE
                    binding.ongoingCallHolder.visibility = View.VISIBLE
                    call_status_label.setText(R.string.str_call_activity_calling)
                }

                -903640504 -> if (stringExtra == TelecomUtils.startCall) {
                    if (callMain.state == Call.STATE_ACTIVE) {
                        this.iscallActive = true
                        removeCallDurationCallBack()
                        try {
                            duratHandler.post(uCallDurationTask)
                        } catch (_: Exception) {
                        }

                        binding.incomingCallHolder.visibility = View.INVISIBLE
                        binding.ongoingCallHolder.visibility = View.VISIBLE
                    } else if (callMain.state == Call.STATE_DIALING) {

                        if (intent.getBooleanExtra(TelecomUtils.IS_LOCK, false)) {
                            binding.relSlider.visibility = View.INVISIBLE
                            binding.txtCallCut.visibility = View.INVISIBLE
                            binding.ivCallCut.visibility = View.VISIBLE
                            binding.ivCallRecive.visibility = View.VISIBLE
                            binding.txtCallRecive.visibility = View.VISIBLE
                            binding.txtCallDecline.visibility = View.VISIBLE
                            binding.linMessage.visibility = View.VISIBLE
                        } else {
                            binding.relSlider.visibility = View.VISIBLE
                            binding.txtCallCut.visibility = View.VISIBLE
                            binding.ivCallCut.visibility = View.INVISIBLE
                            binding.ivCallRecive.visibility = View.INVISIBLE
                            binding.txtCallRecive.visibility = View.INVISIBLE
                            binding.txtCallDecline.visibility = View.INVISIBLE
                            binding.linMessage.visibility = View.INVISIBLE
                        }
                        binding.incomingCallHolder.visibility = View.INVISIBLE
                        binding.ongoingCallHolder.visibility = View.VISIBLE
                    } else if (callMain.state == Call.STATE_RINGING) {

                        binding.relSlider.visibility = View.INVISIBLE
                        binding.txtCallCut.visibility = View.INVISIBLE
                        binding.ivCallCut.visibility = View.VISIBLE
                        binding.ivCallRecive.visibility = View.VISIBLE
                        binding.txtCallRecive.visibility = View.VISIBLE
                        binding.txtCallDecline.visibility = View.VISIBLE
                        binding.linMessage.visibility = View.VISIBLE
                        binding.incomingCallHolder.visibility = View.VISIBLE
                        binding.ongoingCallHolder.visibility = View.INVISIBLE
                    }
                }

                -269981148 -> if (stringExtra == TelecomUtils.IncomingCallReceived) {
                    if (isConference) {

                        if (CallUtils.inCallService != null) {
                            (CallUtils.inCallService as CallService).holdConferenceCalls()
                        }

                        pauseTimer()

                    }

                    if (intent.getBooleanExtra(TelecomUtils.IS_LOCK, false)) {


                        if (!accept_call) {


                            binding.incomingCallHolder.visibility = View.VISIBLE
                            binding.ongoingCallHolder.visibility = View.GONE
                        }

                        binding.relSlider.visibility = View.INVISIBLE
                        binding.txtCallCut.visibility = View.INVISIBLE
                        binding.ivCallCut.visibility = View.VISIBLE
                        binding.ivCallRecive.visibility = View.VISIBLE
                        binding.txtCallRecive.visibility = View.VISIBLE
                        binding.txtCallDecline.visibility = View.VISIBLE
                        binding.linMessage.visibility = View.VISIBLE
                    } else {
                        binding.incomingCallHolder.visibility = View.VISIBLE
                        binding.ongoingCallHolder.visibility = View.INVISIBLE
                        binding.relSlider.visibility = View.INVISIBLE
                        binding.txtCallCut.visibility = View.INVISIBLE
                        binding.ivCallCut.visibility = View.VISIBLE
                        binding.ivCallRecive.visibility = View.VISIBLE
                        binding.txtCallRecive.visibility = View.VISIBLE
                        binding.txtCallDecline.visibility = View.VISIBLE
                        binding.linMessage.visibility = View.VISIBLE
                    }

                }

                39179552 -> if (stringExtra == TelecomUtils.IncomingCallAnswered) {
                    binding.incomingCallHolder.visibility = View.GONE
                    binding.ongoingCallHolder.visibility = View.VISIBLE

                    Glob.view_incoming = true
                    callMain.answer(0)
                }
            }
            val color = resources.getColor(R.color.second_text_color)
            val select_color = resources.getColor(R.color.white)
            binding.ivKeyboard.setOnClickListener({
                this@CallActivity.showDiaplatdKeyboard(color, select_color)
            })

            binding.imgCloseKeypad.setOnClickListener(View.OnClickListener {

                DialpadhideKeyboard(color, select_color)
            })
            binding.txtCallCut.setOnClickListener({ this@CallActivity.OnOutgoingCallEnded() })

            binding.linMessage.setOnClickListener(View.OnClickListener {


                showMessageDialog()
            })

            binding.stopAlarm.onSlideCompleteListener =
                object : SlideToActView.OnSlideCompleteListener {
                    override fun onSlideComplete(slideToActView2: SlideToActView) {

                        binding.incomingCallHolder.visibility = View.GONE
                        binding.ongoingCallHolder.visibility = View.VISIBLE
                        if (callMain != null) {
                            callMain.answer(0)
                        }
                    }
                }

            try {
                details = callMain.getDetails()
                str = null
                handle = if (details != null) details!!.getHandle() else null
            } catch (e: Exception) {
            }


            binding.relBluetooth.setOnClickListener {
                lin_bluetooth_view.visibility = View.GONE
                setAudioRoute(AudioManager.MODE_IN_COMMUNICATION)
                CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_BLUETOOTH) // Bluetooth
                updateUIForBluetoothRoute(select_color, color)
            }

            binding.relSpeaker.setOnClickListener {
                lin_bluetooth_view.visibility = View.GONE
                setAudioRoute(AudioManager.MODE_NORMAL)
                CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_SPEAKER) // Speaker
                updateUIForSpeakerRoute(select_color, color)
            }

            binding.relPhone.setOnClickListener {
                lin_bluetooth_view.visibility = View.GONE
                setAudioRoute(AudioManager.MODE_IN_CALL)
                CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_EARPIECE) // Phone
                updateUIForPhoneRoute(select_color, color)
            }


            rel_add_people.setOnClickListener { v ->
                ManageCallDialog()

            }


            relMergeCall.setOnClickListener {
                ll_call_holder.visibility = View.GONE
                try {
                    val activeCalls = activeCalls
                    val callMain = callMain

                    Glob.iscoutnew = true


                    val conferenceableCalls = callMain!!.conferenceableCalls
                    if (conferenceableCalls.isNotEmpty()) {
                        callMain!!.conference(conferenceableCalls.first())
                    } else {
                        if (hasCapability(callMain!!, Call.Details.CAPABILITY_MERGE_CONFERENCE)) {
                            callMain!!.mergeConference()
                        }
                    }
                    isMerging = true;
                    isConference = true;
                    Handler().postDelayed(kotlinx.coroutines.Runnable {
                        relMergeCall.visibility = View.GONE;
                    }, 500)


                    CallStateManager.getInstance().setMainConferenceCall(callMain);
                    isconfere = true;


//                    if (callMain != null && activeCalls.size > 1) {
//
//                        activeCalls.forEach { call ->
//                            if (call != callMain && call.state == Call.STATE_ACTIVE || call.state == Call.STATE_HOLDING) {
//                                try {
//                                    callMain.conference(call)
//                                    mergedCalls.add(call);
//
//                                    rel_add_people.visibility = View.VISIBLE
//                                    relMergeCall.visibility = View.GONE
//                                } catch (e: Exception) {
//
//                                }
//                            }
//                        }
//
//                        isMerging = true;
//                        isConference = true;
//
//                        // ✅ Check if merge was successful
////                        if (isCallMerged(callMain)) {
////                            Log.d("ConferenceCheck", "Call is successfully merged!")
////                            rel_add_people.visibility = View.VISIBLE
////                            relMergeCall.visibility = View.GONE
////                        } else {
////                            Log.d("ConferenceCheck", "Call is not merged yet.")
////
//////                            Glob.showToast(this,getString(R.string.str_not_merge))
////                        }
//                        Handler().postDelayed(kotlinx.coroutines.Runnable {
//                            relMergeCall.visibility = View.GONE;
//                        }, 500)
//
//
//                        CallStateManager.getInstance().setMainConferenceCall(callMain);
//                        isconfere = true;
//
//                    } else {
//
//                    }
                } catch (e: Exception) {
                    isMerging = false;
                }
            }


            binding.callCancel.setOnClickListener(View.OnClickListener {
                this@CallActivity.OnOutgoingCallEnded()
            })
            binding.callBack.setOnClickListener(
                View.OnClickListener
                {
                    if (callMain == null) {
                        return@OnClickListener
                    }
                    try {
                        val telecomManager =
                            this@CallActivity.getSystemService("telecom") as TelecomManager
                        val defaultOutgoingPhoneAccount: PhoneAccountHandle =
                            TelecomUtils.getDefaultOutgoingPhoneAccount(
                                this@CallActivity, telecomManager, "tel"
                            )
                        val details2: Call.Details = callMain.getDetails()
                        val schemeSpecificPart =
                            if (details2 == null) null else details2.handle.schemeSpecificPart
                        if (schemeSpecificPart == null) {
                            return@OnClickListener
                        }
                        val fromParts = Uri.fromParts("tel", schemeSpecificPart, null)
                        if (CallerHelper.isDefaultSimSetForCall(
                                telecomManager,
                                this@CallActivity
                            )
                        ) {
                            val bundle2 = Bundle()
                            bundle2.putParcelable(
                                "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                                defaultOutgoingPhoneAccount
                            )
                            if (ActivityCompat.checkSelfPermission(
                                    this@CallActivity,
                                    "android.permission.CALL_PHONE"
                                ) != 0
                            ) {
                                return@OnClickListener
                            }
                            telecomManager.placeCall(fromParts, bundle2)

                            binding.incomingCallHolder.visibility = View.GONE
                            binding.ongoingCallHolder.visibility = View.VISIBLE
                            call_status_label.setText(com.phonecontactscall.contectapp.phonedialerr.R.string.str_call_activity_calling)
                            return@OnClickListener
                        }
                        CallerHelper.showDialog(
                            telecomManager,
                            this@CallActivity,
                            schemeSpecificPart,
                            null
                        )
                    } catch (e: Exception) {
                    }
                })
            binding.ivCallRecive.setOnClickListener(
                View.OnClickListener
                {

                    binding.incomingCallHolder.visibility = View.GONE
                    binding.ongoingCallHolder.visibility = View.VISIBLE

                    Glob.accept_call = true


                    val incomingCall = getIncomingCall()
                    if (incomingCall != null) {
                        if (CallUtils.inCallService != null) {
                            (CallUtils.inCallService as CallService).acceptIncomingCall(incomingCall)
                        }
                    } else {
                    }


                })
            binding.ivCallCut.setOnClickListener({
                this@CallActivity.OnOutgoingCallEnded()
            })



            binding.callEnd.setOnClickListener {
                val secondaryCall = CallUtils.secondaryCall
                val incomingCall = CallStateManager.getInstance().getIncomingCall()
                val conferenceCall = CallStateManager.getInstance().getMainConferenceCall()
                val dialingCall =
                    activeCalls.find { it.state == Call.STATE_DIALING } // Find the dialing call

                stopTimer()
                isONOffSpeaker = false
                CallUtils.inCallService.setAudioRoute(1)

                try {
                    dialingCall?.let {
                        it.disconnect()

                        return@setOnClickListener
                    }

                    val iterator = activeCalls.iterator()
                    while (iterator.hasNext()) {
                        val call = iterator.next()
                        call?.let {
                            when (it.state) {
                                Call.STATE_RINGING -> {
                                    it.reject(false, null)
                                }

                                Call.STATE_ACTIVE -> {
                                    when {
                                        conferenceCall != null && it != conferenceCall -> {
                                            it.disconnect()
                                        }

                                        secondaryCall != null && it != secondaryCall -> {
                                            it.disconnect()
                                        }

                                        else -> {
                                            it.disconnect()
                                        }
                                    }
                                }

                                !in listOf(Call.STATE_DISCONNECTED, Call.STATE_DISCONNECTING) -> {
                                    it.disconnect()
                                }
                            }
                        }
                    }
                    activeCalls.clear()

                    // Step 3: Clean up UI and notifications
                    removeCallDurationCallBack()
                    NotificationUtils.removeNotificationFromID(this, 1008)
                    EventBus.getDefault().post("KEY_CALLLOG_UPDATE")


                    // Step 4: Handle navigation & screen close
                    Handler(Looper.getMainLooper()).postDelayed({
                        stopSensor()

                        this@CallActivity.finish()
                    }, 2000L)

                    if (receivenotification) {
                        receivenotification = false
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {

                        }
                    } else {
                        finish()
                    }

                } catch (e: Exception) {
                    Log.e("CallEndError", "Error ending call: ${e.message}")
                }
            }



            relAddCall.setOnClickListener(View.OnClickListener {


                val intent: Intent = Intent(
                    getApplicationContext(),
                    DialpadActivity::class.java
                )
                intent.addFlags(1073741824)
                startActivity(intent)
            })

            ivHold.setOnClickListener(View.OnClickListener {

                val isOnHold = toggleHold()
                updateCallHolderUI(isOnHold)


                return@OnClickListener

            })


            binding.ivMute.setOnClickListener(View.OnClickListener {
                if (CallUtils.inCallService != null) {
                    val audioManager =
                        this@CallActivity.applicationContext.getSystemService("audio") as AudioManager
                    if (this@CallActivity.isMuteConfig) {
                        this@CallActivity.isMuteConfig = false
                        binding.ivMute.setImageResource(R.drawable.ic_mute_select)
                        binding.txtMute.setTextColor(select_color)

                    } else {
                        this@CallActivity.isMuteConfig = true
                        binding.ivMute.setImageResource(R.drawable.ic_mute_unselect)
                        binding.txtMute.setTextColor(color)
                    }
                    audioManager.isMicrophoneMute = !this@CallActivity.isMuteConfig
                    CallUtils.inCallService.setMuted(!this@CallActivity.isMuteConfig)
                }
            })

            ivSpeakDefault.setOnClickListener(
                View.OnClickListener
                {
                    try {


                        if (CallUtils.inCallService == null) {
                            return@OnClickListener
                        }
                        val audioManager =
                            this@CallActivity.applicationContext.getSystemService("audio") as AudioManager

                        if (isONOffSpeaker) {
                            isONOffSpeaker = false
                            ivSpeakDefault.setImageResource(R.drawable.ic_speaker_unselect)
                            binding.txtSepecker.setTextColor(color)
                        } else {
                            Glob.isONOffSpeaker = true

                            ivSpeakDefault.setImageResource(R.drawable.ic_speaker_select)
                            binding.txtSepecker.setTextColor(select_color)
                        }
                        audioManager.isSpeakerphoneOn = isONOffSpeaker
                        if (isONOffSpeaker) {

                            CallUtils.inCallService.setAudioRoute(8)
                        } else {
                            CallUtils.inCallService.setAudioRoute(1)
                        }
                    } catch (unused: Exception) {
                    }
                })



            try {
                val details = callMain.getDetails()
                val handle = details?.getHandle()
                val schemeSpecificPart = handle?.getSchemeSpecificPart()

                if (details != null && handle != null) {
                    try {
                        val contactName =
                            ContactHelper.getContactName(this, schemeSpecificPart, true)
                        val contactNumber =
                            ContactHelper.getContactPhoneNumber(this, schemeSpecificPart)

                        binding.txtName.text = contactName ?: "Unknown Caller"
                        binding.txtNumber.visibility = View.VISIBLE


                        val contactPhoto = retrieveContactPhoto(this, contactName ?: "Unknown")
                        binding.imgPhotouri.apply {
                            setImageBitmap(contactPhoto)
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setCornerRadius(30f)
                            setOval(true)
                        }


                        if (details.hasProperty(Call.Details.PROPERTY_CONFERENCE) || isconfere) {

                            binding.txtName.text = getString(R.string.str_cnference_call)
                            binding.txtNumber.visibility = View.GONE
                            binding.txtName.visibility = View.VISIBLE

                        }
                    } catch (e: Exception) {
                    }


                    try {
                        val retrieveContactData =
                            ContactHelper.ContactPhotoUriAndDisplayName(
                                this, schemeSpecificPart
                            )

                        if (retrieveContactData.size > 1) {
                            val photoUri = retrieveContactData[1]
                        }
                    } catch (e: Exception) {
                    }
                } else {
                    if (activeCalls.size >= 2 || isconfere) {
                        binding.txtName.text = getString(R.string.str_cnference_call)
                        binding.txtName.visibility = View.VISIBLE
                        binding.txtNumber.visibility = View.GONE
                        binding.imgPhotouri.apply {
                            setImageBitmap(
                                BitmapFactory.decodeResource(
                                    context.resources,
                                    R.drawable.ic_contact_1
                                )
                            )
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setCornerRadius(30f)
                            setOval(true)
                        }


                    }
                    checkIfCallsAreMerged(callMain)
                }
            } catch (e: Exception) {
            }

            return
        }




        Log.e("Contect_Event--", "Call_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Call_Act_onCreate", Bundle())
    }

    fun isCallMerged(call: Call): Boolean {
        return call.conferenceableCalls.isEmpty()
    }

    fun getActiveCallCount(): Int {
        return activeCalls.filter { call ->
            call.state == Call.STATE_ACTIVE || call.state == Call.STATE_HOLDING
        }.size
    }


    fun checkIfCallsAreMerged(call: Call) {
        val parentCall = call.parent
        val activeCallCount = getActiveCallCount()

        if (parentCall != null) {
            if (activeCallCount == 2) {
                relMergeCall.visibility = View.GONE
                rel_add_people.visibility = View.GONE
            } else {
                rel_add_people.visibility = View.VISIBLE
                relMergeCall.visibility = View.GONE


            }


            val isMerged = call!!.details.hasProperty(Call.Details.PROPERTY_CONFERENCE)
            if (isMerged) {
                relMergeCall.visibility = View.GONE
                rel_add_people.visibility = View.VISIBLE
            } else {
                relMergeCall.visibility = View.VISIBLE
                rel_add_people.visibility = View.GONE

            }
        } else {
            if (activeCallCount >= 2) {
                when (call.state) {

                    Call.STATE_ACTIVE -> {

                        Handler().postDelayed({
                            val isMerged =
                                call!!.details.hasProperty(Call.Details.PROPERTY_CONFERENCE)

                            if (isMerged) {
                                relMergeCall.visibility = View.GONE
                                rel_add_people.visibility = View.VISIBLE
                            } else {
                                relMergeCall.visibility = View.VISIBLE
                                rel_add_people.visibility = View.GONE

                            }
                            if (!isMerging) {


                                if (!isConference) {

                                    val filteredActiveCalls = activeCalls.filter { call ->
                                        val name =
                                            call.details.handle?.schemeSpecificPart ?: "Unknown"
                                        name != "Unknown"
                                    }

                                    if (filteredActiveCalls.size == 1) {
                                        relMergeCall.visibility = View.GONE
                                        rel_add_people.visibility = View.GONE
                                    } else {
                                        relMergeCall.visibility = View.VISIBLE
                                        rel_add_people.visibility = View.GONE
                                    }


                                } else {

                                    if (activeCallCount == 2) {
                                        relMergeCall.visibility = View.GONE
                                        rel_add_people.visibility = View.GONE
                                    } else {
                                        relMergeCall.visibility = View.GONE
                                        rel_add_people.visibility = View.VISIBLE
                                    }

                                }


                            } else {

                                if (activeCallCount >= 4) {
                                    val isMerged =
                                        call!!.details.hasProperty(Call.Details.PROPERTY_CONFERENCE)

                                    if (isMerged) {
                                        relMergeCall.visibility = View.GONE
                                        rel_add_people.visibility = View.VISIBLE
                                    } else {
                                        relMergeCall.visibility = View.VISIBLE
                                        rel_add_people.visibility = View.GONE

                                    }

                                } else if (isConference) {
                                    relMergeCall.visibility = View.GONE
                                    rel_add_people.visibility = View.VISIBLE
                                } else {
                                    relMergeCall.visibility = View.VISIBLE
                                    rel_add_people.visibility = View.GONE
                                }

                            }
                        }, 1600)


                    }

                    else -> {

                        if (activeCallCount == 2) {
                            isConference = false


                            relMergeCall.visibility = View.GONE
                            rel_add_people.visibility = View.GONE
                        } else {
                            Handler().postDelayed({
                                relMergeCall.visibility = View.GONE
                                rel_add_people.visibility = View.VISIBLE
                            }, 1600)
                        }


                    }
                }
            } else {
                Handler().postDelayed({

                    relMergeCall.visibility = View.GONE
                    rel_add_people.visibility = View.GONE
                }, 1600)


            }
        }

        parentCall?.children?.let { childCalls ->
            childCalls.forEach { childCall ->
            }
        }
    }

    private fun ManageCallDialog() {
        manage_call_dialogue = Dialog(this@CallActivity)
        manage_call_dialogue!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        manage_call_dialogue!!.setContentView(R.layout.dialog_manage_call)
        manage_call_dialogue!!.setCancelable(true)
        manage_call_dialogue!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        manage_call_dialogue!!.window!!.attributes.windowAnimations = R.style.dialog_theme
        manage_call_dialogue!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        manage_call_dialogue!!.setCanceledOnTouchOutside(true)
        val rev_add_call = manage_call_dialogue!!.findViewById<RecyclerView>(R.id.rev_add_call)
        rev_add_call.layoutManager = LinearLayoutManager(this)
        val filteredActiveCalls = activeCalls.filter { call ->
            val name = call.details.handle?.schemeSpecificPart ?: "Unknown"
            name != "Unknown"
        }


        val adapter = ActiveCallAdapter(this@CallActivity, filteredActiveCalls) { call ->


            endCall(call, filteredActiveCalls)
            removeCallDurationCallBack()
            updateContactDetails()
            manage_call_dialogue!!.dismiss()
        }
        rev_add_call.adapter = adapter


        try {
            if (!isFinishing) {
                manage_call_dialogue!!.show()

            }
        } catch (e: Exception) {
            manage_call_dialogue!!.dismiss()
        }

    }

    private fun endCall(call: Call, filteredActiveCalls: List<Call>) {


        try {
            call.disconnect()


            Handler().postDelayed({
                if (filteredActiveCalls.size <= 1) {
                    isconfere = false
                }
            }, 500)


        } catch (e: Exception) {
        }
    }

    private fun showMessageDialog() {
        message_dialogue = Dialog(this@CallActivity)
        message_dialogue!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        message_dialogue!!.setContentView(R.layout.dialog_message)
        message_dialogue!!.setCancelable(true)
        message_dialogue!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        message_dialogue!!.window!!.attributes.windowAnimations = R.style.dialog_theme
        message_dialogue!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        message_dialogue!!.setCanceledOnTouchOutside(true)
        val txt_first = message_dialogue!!.findViewById<TextView>(R.id.txt_first)
        val txt_second = message_dialogue!!.findViewById<TextView>(R.id.txt_second)
        val txt_third = message_dialogue!!.findViewById<TextView>(R.id.txt_third)
        val txt_four = message_dialogue!!.findViewById<TextView>(R.id.txt_four)
        val txt_write = message_dialogue!!.findViewById<EditText>(R.id.txt_write)
        val text_ok = message_dialogue!!.findViewById<TextView>(R.id.text_ok)

        val textViews = listOf(txt_first, txt_second, txt_third, txt_four)

        textViews.forEach { textView ->
            textView.setOnClickListener {
                val selectedText = textView.text.toString()
                sendDirectReply(selectedText)
                binding.ivCallCut.performClick()

                message_dialogue!!.dismiss()
            }
        }

        text_ok.setOnClickListener {

            if (txt_write.text.equals(null) || txt_write.text.toString().isEmpty()) {
                Glob.showToast(this, getString(R.string.toast_please_enetr_msg))
            } else {
                sendDirectReply(txt_write.text.toString())

                binding.ivCallCut.performClick()
            }

            message_dialogue!!.dismiss()

        }


        message_dialogue!!.show()
    }


    private fun sendDirectReply(message: String) {
        val details = callMain.getDetails()
        val handle = details.handle
        val phoneNumber = handle?.schemeSpecificPart ?: "Unknown"

        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        Glob.showToast(this, getString(R.string.toast_msg_send))

    }


    private fun getIncomingCall(): Call? {
        for (call in activeCalls) {
            if (call.state == Call.STATE_RINGING) {
                return call
            }
        }
        return null
    }

    private fun setAudioRoute(mode: Int) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = mode
    }


    fun retrieveContactPhoto(context: Context, str: String): Bitmap {
        if (str.isNullOrEmpty()) {
            return BitmapFactory.decodeResource(context.resources, R.drawable.ic_contact_1)
        }

        if (!isSingleCallConnected()) {
            return BitmapFactory.decodeResource(context.resources, R.drawable.ic_contact_1)
        }

        val contentResolver = context.contentResolver
        var contactId: String? = null
        val uri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        )
        val selection = "${ContactsContract.Contacts.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(str)

        contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            }
        }

        return try {
            if (!contactId.isNullOrEmpty()) {
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    contentResolver,
                    ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        contactId!!.toLong()
                    )
                )
                inputStream?.use {
                    BitmapFactory.decodeStream(it)
                } ?: BitmapFactory.decodeResource(context.resources, R.drawable.ic_contact_1)
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.ic_contact_1)
            }
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_contact_1)
        }
    }

    fun isSingleCallConnected(): Boolean {
        val activeCalls = CallService.activeCalls
        return activeCalls.size == 1
    }


    fun toggleHold(): Boolean {
        val allCalls = CallerHelper.getInstances()?.getAllCalls() ?: return false
        allCalls.forEach { call ->
        }
        val heldCall = allCalls.firstOrNull { it.state == Call.STATE_HOLDING }
        val activeCall = allCalls.firstOrNull { it.state == Call.STATE_ACTIVE }
        val conferenceCall = allCalls.firstOrNull {
            it.details.hasProperty(Call.Details.PROPERTY_CONFERENCE)
        }

        var isCurrentlyHeld = false
        if (conferenceCall != null) {
            when (conferenceCall.state) {
                Call.STATE_HOLDING -> {
                    conferenceCall.unhold()
                    ll_call_holder.visibility = View.GONE
                    isCurrentlyHeld = false
                }

                Call.STATE_ACTIVE -> {
                    conferenceCall.hold()
                    ll_call_holder.visibility = View.VISIBLE
                    binding.callStatusLabel.text = getString(R.string.str_call_on_hold)
                    isCurrentlyHeld = true
                }

                else -> {
                }
            }
            updateCallHolderUI(isCurrentlyHeld)
            return isCurrentlyHeld
        }


        heldCall?.let {
            it.unhold()
            ll_call_holder.visibility = View.GONE
            isCurrentlyHeld = false
        }

        activeCall?.let {
            it.hold()
            ll_call_holder.visibility = View.VISIBLE
            isCurrentlyHeld = true
        }
        if (heldCall == null && activeCall == null && conferenceCall == null) {
            ll_call_holder.visibility = View.GONE
        }
        updateCallHolderUI(isCurrentlyHeld)
        return isCurrentlyHeld
    }


    private fun updateCallHolderUI(isHeld: Boolean) {

        val color = resources.getColor(R.color.second_text_color)
        val select_color = resources.getColor(R.color.white)

        if (isHeld) {
            ivHold.setImageResource(R.drawable.ic_hold_select)
            ll_call_holder.visibility = View.VISIBLE
            binding.callStatusLabel.text = getString(R.string.str_call_on_hold)
            binding.tvHold.text = getString(R.string.str_un_hold)
            binding.tvHold.setTextColor(select_color)
        } else {
            ivHold.setImageResource(R.drawable.ic_hold_unselect)
            ll_call_holder.visibility = View.GONE
            binding.tvHold.text = getString(R.string.str_hold)
            binding.tvHold.setTextColor(color)
        }


    }

    fun stopTimer() {

        if (timer != null) {
            timer!!.cancel();
            timer = null

        }
        countTime = 0;
        isTimerRunning = false;
    }

    fun OnOutgoingCallEnded() {
        val activeCallCount = getActiveCallCount()

        NotificationUtils.removeNotificationFromID(this, 1008)
        EventBus.getDefault().post("KEY_CALLLOG_UPDATE")


        if (activeCallCount <= 2) {
            isConference = false
            ll_call_holder.visibility = View.GONE

        }
        if (isConference) {
            ll_call_holder.visibility = View.GONE

        } else {

            isconfere = false
        }


        if (callMain == null) {
            return
        } else {
            callMain.disconnect()
        }


        runOnUiThread {
            val textView = call_status_label
            textView.text = TelecomUtils.formatTime(
                this@CallActivity.countTime,
                false,
                1
            ) + " (" + this@CallActivity.getString(R.string.str_call_ended) + ')'

        }

        if (activeCallCount == 0) {
            finish()
        }
        EventBus.getDefault().post("KEY_CALLLOG_UPDATE")

    }


    fun showDiaplatdKeyboard(color: Int, select_color: Int) {
        binding.relKeyboardView.scaleX = 0.0f
        binding.relKeyboardView.scaleY = 0.0f
        binding.relKeyboardView.alpha = 0.0f
        binding.ivKeyboard.setImageResource(R.drawable.ic_keypad_select)
        binding.txtKeypad.setTextColor(select_color)
        binding.relKeyboardView.visibility = View.VISIBLE
        binding.relKeyboardView.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setListener(null)
            .start()
        binding.llContent.animate().scaleX(1.1f).scaleY(1.1f).alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator) {
                    super.onAnimationEnd(animator)
                    binding.llContent.visibility = View.GONE
                    binding.llEnd.visibility = View.GONE

                }
            }).start()
    }

    private fun DialpadhideKeyboard(color: Int, select_color: Int) {
        binding.relKeyboardView.animate().scaleX(0.0f).scaleY(0.0f).alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator) {
                    super.onAnimationEnd(animator)
                    binding.relKeyboardView.visibility = View.GONE
                }
            }).start()
        binding.llContent.visibility = View.VISIBLE
        binding.llEnd.visibility = View.VISIBLE
        binding.ivKeyboard.setImageResource(R.drawable.ic_keypad_unselect)
        binding.txtKeypad.setTextColor(color)
        binding.llContent.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setListener(null).start()
        tvdialkeypad.text = ""
    }

    fun inputNumber(c: Char) {
        if (callMain != null) {
            callMain.playDtmfTone(c)
            callMain.stopDtmfTone()
        }
    }

    fun pauseTimer() {
        if (timer != null) {
            timer?.cancel()
        }
        isTimerRunning = false
    }


    private fun startOrResumeTimer() {


        if (!isTimerRunning) {

            isTimerRunning = true
            if (timer == null) {
                timer = Timer()
            }


            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    countTime++
                    this@CallActivity.runOnUiThread {

                        call_status_label.text = TelecomUtils.formatTime(countTime, false, 1)

                    }
                }
            }, 1000L, 1000L)
        }
    }


    override fun onClick(v: View) {
        val id: Int = v.getId()
        when (id) {
            R.id.imgNum0 -> {
                inputNumber('0')
                tvdialkeypad.append("0")
                return
            }

            R.id.imgNum1 -> {
                inputNumber('1')
                tvdialkeypad.append("1")
                return
            }

            R.id.imgNum2 -> {
                inputNumber('2')
                tvdialkeypad.append(ExifInterface.GPS_MEASUREMENT_2D)
                return
            }

            R.id.imgNum3 -> {
                inputNumber('3')
                tvdialkeypad.append(ExifInterface.GPS_MEASUREMENT_3D)
                return
            }

            R.id.imgNum4 -> {
                inputNumber('4')
                tvdialkeypad.append("4")
                return
            }

            R.id.imgNum5 -> {
                inputNumber('5')
                tvdialkeypad.append("5")
                return
            }

            R.id.imgNum6 -> {
                inputNumber('6')
                tvdialkeypad.append("6")
                return
            }

            R.id.imgNum7 -> {
                inputNumber('7')
                tvdialkeypad.append("7")
                return
            }

            R.id.imgNum8 -> {
                inputNumber('8')
                tvdialkeypad.append("8")
                return
            }

            R.id.imgNum9 -> {
                inputNumber('9')
                tvdialkeypad.append("9")
                return
            }

            R.id.imgNumHash -> {
                inputNumber('#')
                tvdialkeypad.append("#")
                return
            }

            R.id.imgNumStar -> {
                inputNumber('*')
                tvdialkeypad.append("*")
                return
            }

            else -> return
        }

    }


    @Subscribe
    fun onMessageEvent(str: String) {

        if (TextUtils.isEmpty(str)) {
            return
        }


        if (str == TelecomUtils.IncomingCallReceived) {

            if (!accept_call) {

                binding.incomingCallHolder.visibility = View.VISIBLE
                binding.ongoingCallHolder.visibility = View.GONE
            } else {

            }

        } else if (str == TelecomUtils.IncomingCallAnswered) {
            if (callMain == null) {
                return
            }
            iscounttime = false;
            binding.incomingCallHolder.visibility = View.GONE
            binding.ongoingCallHolder.visibility = View.VISIBLE
            updateContactDetails();
        } else if (str == TelecomUtils.IncomingCallEnded) {
            OnOutgoingCallEnded()
        } else if (str == TelecomUtils.OutgoingCallStarted) {

            binding.incomingCallHolder.visibility = View.GONE
            binding.ongoingCallHolder.visibility = View.VISIBLE
            call_status_label.setText(R.string.str_call_activity_calling)
        } else if (str != TelecomUtils.OutgoingCallEnded) {
            if (str == TelecomUtils.OutgoingCallAnswered) {

                Glob.calldurationstart = true

                removeCallDurationCallBack()
                try {
                    duratHandler.post(uCallDurationTask)
                } catch (_: Exception) {
                }

                updateContactDetails()
                this.iscallActive = true
            } else if (str == TelecomUtils.MissedCall) {
                OnOutgoingCallEnded()
            } else if (str == TelecomUtils.OutgoingCallDISCONNECTED) {

                OnOutgoingCallEnded()


                call_status_label.setText(R.string.str_call_activity_callend)
            }
        } else if (this.iscallActive) {
            OnOutgoingCallEnded()
            call_status_label.setText(R.string.str_call_activity_callend)
        } else {

        }
        updateContactDetails();


    }


    fun updateContactDetails1() {


        val activeCallCount = getActiveCallCount()
        if (activeCallCount >= 3) {
            txtName.setText(getString(R.string.str_cnference_call));
            binding.txtName.visibility = View.VISIBLE
            binding.txtNumber.visibility = View.GONE
            return
        }


        activeCalls.forEachIndexed { index, call ->
            val details = call.details
            val handle = details.handle
            val phoneNumber = handle?.schemeSpecificPart ?: "Unknown"
            val contactName = ContactHelper.getContactName(this, phoneNumber, true)


            if (details.hasProperty(Call.Details.PROPERTY_CONFERENCE)) {
                ll_call_holder.visibility = View.GONE
            } else {
                if (contactName == phoneNumber) {
                    binding.txtName.visibility = View.GONE
                } else {
                    binding.txtName.visibility = View.VISIBLE
                }

                binding.txtName.text = contactName ?: phoneNumber
                binding.txtNumber.text = phoneNumber

                val contactPhoto = retrieveContactPhoto(this, contactName)
                binding.imgPhotouri.setImageBitmap(contactPhoto)
                binding.imgPhotouri.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.imgPhotouri.setCornerRadius(30f)
                binding.imgPhotouri.setOval(true)
            }

        }

    }


    fun updateContactDetails() {


        if (activeCalls.isEmpty()) {
            binding.txtName.text = getString(R.string.str_no_active_call)
            binding.txtNumber.text = ""
            isONOffSpeaker = false
            CallUtils.inCallService.setAudioRoute(1)
            Handler().postDelayed({
                this@CallActivity.intent.putExtra("update_calllog", "update")
                this@CallActivity.finish()
            }, 2000L)

            return
        }


        val activeCallCount = getActiveCallCount()

        if (activeCallCount >= 3) {
            activeCalls.forEachIndexed { index, call ->
                val details = call.details
                val handle = details.handle
                val phoneNumber = handle?.schemeSpecificPart ?: "Unknown"
                val contactName = ContactHelper.getContactName(this, phoneNumber, true)
                val isMerged = call!!.details.hasProperty(Call.Details.PROPERTY_CONFERENCE)

                if (!isMerged) {

                    if (isconfere) {
                        txtName.setText(getString(R.string.str_cnference_call));
                        binding.txtNumber.visibility = View.GONE
                        binding.txtName.visibility = View.VISIBLE
                    } else {
                        binding.txtNumber.visibility = View.VISIBLE
                        binding.txtName.text = contactName ?: phoneNumber
                        binding.txtNumber.text = phoneNumber
                    }


                    val contactPhoto = retrieveContactPhoto(this, contactName)
                    binding.imgPhotouri.setImageBitmap(contactPhoto)
                    binding.imgPhotouri.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.imgPhotouri.setCornerRadius(30f)
                    binding.imgPhotouri.setOval(true)
                } else {
                    txtName.setText(getString(R.string.str_cnference_call));
                    binding.txtNumber.visibility = View.GONE
                    binding.txtName.visibility = View.VISIBLE
                }
            }

            return
        }

        activeCalls.forEachIndexed { index, call ->
            val details = call.details
            val handle = details.handle
            val phoneNumber = handle?.schemeSpecificPart ?: "Unknown"
            val contactName = ContactHelper.getContactName(this, phoneNumber, true)

            if (contactName == phoneNumber) {
                binding.txtName.visibility = View.GONE
                binding.txtNumber.visibility = View.VISIBLE


            } else {
                binding.txtName.visibility = View.VISIBLE
            }


            if (details.hasProperty(Call.Details.PROPERTY_CONFERENCE)) {
                ll_call_holder.visibility = View.GONE
            } else {

                binding.txtNumber.visibility = View.VISIBLE

                binding.txtName.text = contactName ?: phoneNumber
                binding.txtNumber.text = phoneNumber


                val contactPhoto = retrieveContactPhoto(this, contactName)
                binding.imgPhotouri.setImageBitmap(contactPhoto)
                binding.imgPhotouri.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.imgPhotouri.setCornerRadius(30f)
                binding.imgPhotouri.setOval(true)

            }

        }

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        isRunning = true;
        startCallDurationUpdater()
        val filter = IntentFilter("com.example.CALL_STATUS")
        LocalBroadcastManager.getInstance(this).registerReceiver(callReceiver, filter)

    }

    fun startCallDurationUpdater() {
        try {
            updateContactDetails1()

            if (duratHandler == null) {
                duratHandler = Handler(Looper.getMainLooper())
            }
            checkIfCallsAreMerged(callMain)
            val allCalls = CallerHelper.getInstances()?.getAllCalls() ?: listOf()
            val heldCall = allCalls.firstOrNull { it.state == Call.STATE_HOLDING }
            if (heldCall != null) {
                heldCall?.let {
                    it.hold()

                }
                updateCallHolderUI(true)
            } else {
                updateCallHolderUI(false)
            }
            val color = resources.getColor(R.color.second_text_color)
            val select_color = resources.getColor(R.color.white)
            uCallDurationTask = object : Runnable {
                override fun run() {
                    val currentCall = callMain
                    if (currentCall == null) {
                        binding.callStatusLabel.text = getString(R.string.str_no_active_call)
                        Handler().postDelayed({
                            this@CallActivity.intent.putExtra("update_calllog", "update")
                            this@CallActivity.finish()
                        }, 1000L)
                        return
                    }





                    try {
                        val secondaryCall = if (activeCalls.size > 1) activeCalls[1] else null
                        val activeCall = activeCalls.firstOrNull { it.state == Call.STATE_ACTIVE }
                        val heldCall = activeCalls.firstOrNull { it.state == Call.STATE_HOLDING }


                        when (currentCall.state) {

                            Call.STATE_DIALING -> {
                                binding.callStatusLabel.text =
                                    getString(R.string.str_call_activity_calling)
                                updateCallHolderUI(false)
                            }

                            Call.STATE_RINGING -> {
                                binding.callStatusLabel.text =
                                    getString(R.string.str_call_activity_calling)
                                updateCallHolderUI(false)
                            }

                            Call.STATE_HOLDING -> {
                                if (heldCall != null) {

                                    ivHold.setImageResource(R.drawable.ic_hold_select)
                                    ll_call_holder.visibility = View.VISIBLE
                                    binding.tvHold.text = getString(R.string.str_un_hold)
                                    binding.tvHold.setTextColor(select_color)
                                } else if (secondaryCall?.state!!.equals(Call.STATE_DIALING)) {
                                    binding.callStatusLabel.text =
                                        getString(R.string.str_call_activity_calling)
                                } else {
                                    binding.callStatusLabel.text =
                                        getString(R.string.str_call_on_hold)
                                    updateCallHolderUI(true)
                                }
                            }

                            Call.STATE_ACTIVE -> {
                                val durcall = CallUtils.getDuration(currentCall)
                                val formattedDuration = durcall.durationFormatted()
                                if (formattedDuration == "00:00") {
                                    binding.callStatusLabel.text =
                                        getString(R.string.str_call_activity_calling)
                                } else {

                                    if (heldCall != null) {
                                        binding.callStatusLabel.text = formattedDuration

                                        ivHold.setImageResource(R.drawable.ic_hold_select)
                                        ll_call_holder.visibility = View.VISIBLE
                                        binding.tvHold.text = getString(R.string.str_un_hold)
                                        binding.tvHold.setTextColor(select_color)
                                    } else {
                                        binding.callStatusLabel.text = formattedDuration
                                        updateCallHolderUI(false)
                                    }


                                }
                            }

                            Call.STATE_DISCONNECTED -> {
                                if (activeCall == null && heldCall != null) {
                                    binding.callStatusLabel.text =
                                        getString(R.string.str_call_on_hold)
                                    updateCallHolderUI(true)
                                } else if (activeCall != null) {
                                    val durcall = CallUtils.getDuration(currentCall)
                                    val formattedDuration = durcall.durationFormatted()
                                    if (formattedDuration == "00:00") {
                                        binding.callStatusLabel.text =
                                            getString(R.string.str_call_activity_calling)
                                    } else {

                                        if (heldCall != null) {
                                            binding.callStatusLabel.text = formattedDuration

                                            ivHold.setImageResource(R.drawable.ic_hold_select)
                                            ll_call_holder.visibility = View.VISIBLE
                                            binding.tvHold.text = getString(R.string.str_un_hold)
                                            binding.tvHold.setTextColor(select_color)
                                        } else {
                                            binding.callStatusLabel.text = formattedDuration
                                            updateCallHolderUI(false)
                                        }


                                    }
                                }
                            }

                            else -> {

                                if (activeCall != null) {
                                    val durcall = CallUtils.getDuration(activeCall)
                                    val formattedDuration = durcall.durationFormatted()
                                    binding.callStatusLabel.text =
                                        if (formattedDuration == "00:00") {
                                            getString(R.string.str_call_activity_calling)
                                        } else {
                                            formattedDuration
                                        }


                                } else {

                                    binding.callStatusLabel.text =
                                        getString(R.string.str_call_activity_calling)
                                }


                            }
                        }

                    } catch (e: Exception) {
                    }

                    try {
                        duratHandler.postDelayed(this, 1000)
                    } catch (_: Exception) {
                    }
                }
            }

            try {
                duratHandler.post(uCallDurationTask!!)
            } catch (_: Exception) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopCallDurationUpdater() {
        removeCallDurationCallBack()
    }
    override fun onStop() {
        super.onStop()
        isRunning = false;
        stopCallDurationUpdater()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(callReceiver)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val proximityValue = event.values[0]


        }
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }


    override fun onResume() {
        super.onResume()



        try {
            if (noticication_acepticlick) {
                noticication_acepticlick = false

                binding.incomingCallHolder.visibility = View.GONE
                binding.ongoingCallHolder.visibility = View.VISIBLE

                Glob.view_incoming = true
                callMain.answer(0)
            }
        } catch (e: Exception) {
        }
        if (callMain == null) {
            if (receivenotification) {
                receivenotification = false
                val intent: Intent = Intent(
                    getApplicationContext(),
                    MainActivity::class.java
                )
                intent.addFlags(1073741824)
                startActivity(intent)
            } else {
                finish()

            }
        }
    }


    override fun onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(audioRouteReceiver);
        stopCallDurationUpdater()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }


        stopSensor()


        super.onDestroy()
    }


    override fun onPause() {
        super.onPause()

    }


}