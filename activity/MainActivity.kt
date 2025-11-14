package com.phonecontactscall.contectapp.phonedialerr.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper
import com.phonecontactscall.contectapp.phonedialerr.Fragment.Call_Fragment
import com.phonecontactscall.contectapp.phonedialerr.Fragment.Contect_Fragment
import com.phonecontactscall.contectapp.phonedialerr.Fragment.Contect_Fragment.Companion.staticRecyclerView
import com.phonecontactscall.contectapp.phonedialerr.Fragment.Fav_Fragment
import com.phonecontactscall.contectapp.phonedialerr.Fragment.Setting_Fragment
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Glob.callend
import com.phonecontactscall.contectapp.phonedialerr.Glob.iscontect
import com.phonecontactscall.contectapp.phonedialerr.Glob.isseting
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
//import com.phonecontactscall.contectapp.phonedialer.RateDialog
import com.phonecontactscall.contectapp.phonedialerr.RateDialog.star_number
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactRepository
import com.phonecontactscall.contectapp.phonedialerr.activity.SplashActivity.Companion.rateus_onetime
import com.phonecontactscall.contectapp.phonedialerr.ads.adsload.NativeAdsLoaded
import com.phonecontactscall.contectapp.phonedialerr.ads.adsload.InterstitialAds
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.bumptech.glide.Glide
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.launch
import kotlin.jvm.internal.Intrinsics


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var REQUEST_CODE_SET_DEFAULT_DIALER = 100
    val PERMISSION_REQUEST_CODE = 1

    companion object {
        lateinit var mainActivity: MainActivity
        lateinit var bottomNavigationView: BottomNavigationView
        lateinit var rel_default: RelativeLayout
        lateinit var rel_data: RelativeLayout
        lateinit var rel_main: RelativeLayout
        private const val LOG_TAG = "Contect_Event--"
        private const val TAG = "Main_Act"
    }

    private var reviewManager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null
    // Rate Dialog

    //rate
    lateinit var sharedPreferences_rate: SharedPreferences
    lateinit var editor_rate: SharedPreferences.Editor
    private var sec_time_open = 0
    private var sec_time_boole = false

    //update dialog
    var MY_REQUEST_CODE = 111
    var appUpdateManager: AppUpdateManager? = null

    // exit dialog
    var PREFS_NAME_EXit = "Pref_exit_dialog"
    var VALUE_KEY_Exit = "Exit_dialog_Value_store"

    var exitDialogWithAd: Dialog? = null
    var exit_dialg_withou_ads: Dialog? = null
    lateinit var img_rate1: ImageView
    lateinit var img_rate2: ImageView
    lateinit var img_rate3: ImageView
    lateinit var img_rate4: ImageView
    lateinit var img_rate5: ImageView
    var permission_dialog_notification: Dialog? = null
    private var opendialog = false
    private var opendialog_noti = false
    var setting_dialog_notification: Dialog? = null
    var setting_dialog: Dialog? = null
    var permission_dialog: Dialog? = null
    private var contactRepository = ContactRepository()

    private var constrain_adview: ConstraintLayout? = null
    private var mHeight = 0

    private var rel_banner: RelativeLayout? = null
    private var fram_banner: FrameLayout? = null
    private var txt_ads_space: TextView? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initView() {
        mainActivity = this
        bottomNavigationView = findViewById(R.id.bottom_navigation)


        val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color_top, this)
        val color1 = Glob.getResourceFromAttr(R.attr.navigation_bg_color, this)
//
        window.statusBarColor = color
        window.navigationBarColor = color1

        Glob.SetStatusbarColor(window)

        rel_default = findViewById(R.id.rel_default)
        rel_data = findViewById(R.id.rel_data)
        rel_main = findViewById(R.id.rel_main)

        if (!Glob.getInstance().GetBoolean(this@MainActivity, "chke_lang_open")) {
//            if (!Ads_Utils.checkSplashInterCall) {
//                InterstitialAds.LoadgoogleInterstitial(this)
//            }
        }

        InterstitialAds.Show_google_Interstitialads(this@MainActivity, "MainActivity_Oncreat")

        rel_default.setOnClickListener { v -> }

        constrain_adview = findViewById(R.id.constrain_adview)
        rel_banner = findViewById(R.id.rel_banner)
        fram_banner = findViewById(R.id.fram_banner)
        txt_ads_space = findViewById(R.id.txt_ads_space)
        ShowAdView()

        sharedPreferences = getSharedPreferences("fregment_save_screen", Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()

        val intent = intent
        if (intent.data != null) {
//
            CallerHelper.callPhoneClick(this, intent.data.toString().substring(4))
            finish()
            return
        }

        //  rate dialog initalize
        sharedPreferences_rate = getSharedPreferences("share_pref_rate_caller", 0)
        editor_rate = sharedPreferences_rate.edit()
        sec_time_open = sharedPreferences_rate.getInt("sec_time_cnint", 0)
        sec_time_boole = sharedPreferences_rate.getBoolean("sec_time_cnboolean", false)


        appUpdateManager = AppUpdateManagerFactory.create(this)
        Exit_dialog_Initalize()
        ExitdialogWithads()


        if (Build.VERSION.SDK_INT >= 29) {
            if (!Glob.isDefaultDialer(this)) {
                rel_data.visibility = View.GONE
                rel_default.visibility = View.VISIBLE
                val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color, this)
                val color1 = Glob.getResourceFromAttr(R.attr.navigation_bg_color, this)
//
                window.statusBarColor = color
                window.navigationBarColor = color1

                Glob.SetStatusbarColor(window)


            } else {


                val progressDialog = ProgressDialog(this).apply {
                    setMessage(getString(R.string.str_load_contect))
                    setCancelable(false)
                }
                progressDialog.show()

                lifecycleScope.launch {
                    contactRepository.fetchContacts(this@MainActivity)

                    contactRepository.contactsStateFlow.collect { contactItemList ->
                        progressDialog.dismiss()


                        if (rateus_onetime == true) {

                            rateus_onetime = false
                            if (!sec_time_boole) {
                                sec_time_open = sec_time_open + 1
                                editor_rate.putInt("sec_time_cnint", sec_time_open)
                                editor_rate.apply()
                            }
                            val sharedPreferences1 =
                                getSharedPreferences("share_pref_rate_caller", MODE_PRIVATE)
                            if (sec_time_open == 2 && !sec_time_boole) {
                                editor_rate.putBoolean("sec_time_cnboolean", true)
                                editor_rate.apply()
                                if (sharedPreferences1.getInt("contect_rate_prefrence", 0) < 2) {
                                    if (Glob.israting == false) {
//                                        RateDialog.Show_RateDialog(this@MainActivity)
                                        requestReviewInfo()
                                    } else {
                                        updatedialog()
                                    }
                                } else {
                                    updatedialog()
                                }
                            } else {
                                updatedialog()
                            }
                        }

                    }
                }



                sharedPreferences = getSharedPreferences("fregment_save_screen", 0)
                val call_fragment = sharedPreferences.getString("fragment_call", "")

                if (getIntent().getAction() != null && getIntent().getAction()
                        .equals("com.android.phone.action.RECENT_CALLS")
                ) {
                    this.binding.bottomNavigation.selectedItemId = R.id.item_call
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                } else if (call_fragment!!.contains("Setting_Fragment")) {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.GONE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.VISIBLE
                    val fragment = Setting_Fragment()
                    addFragment_setting(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_setting
                } else if (call_fragment!!.contains("Fav_Fragment")) {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.VISIBLE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Fav_Fragment()
                    addFragment_fav(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_fav
                } else if (call_fragment!!.contains("Contect_Fragment")) {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.VISIBLE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Contect_Fragment()
                    addFragment_contect(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_contect
                } else if (call_fragment!!.contains("Call_Fragment")) {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_call
                } else {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE

                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                    editor.putString("fragment_call", fragment.toString())
                    editor.apply()
                }

            }
        } else {
            if (!permissionAlreadyGranted()) {
                requestPermission()
            } else {
                val progressDialog = ProgressDialog(this).apply {
                    setMessage(getString(R.string.str_load_contect))
                    setCancelable(false)
                }
                progressDialog.show()

                lifecycleScope.launch {
                    contactRepository.fetchContacts(this@MainActivity)

                    contactRepository.contactsStateFlow.collect { contactItemList ->


                        if (rateus_onetime == true) {

                            rateus_onetime = false
                            if (!sec_time_boole) {
                                sec_time_open = sec_time_open + 1
                                editor_rate.putInt("sec_time_cnint", sec_time_open)
                                editor_rate.apply()
                            }
                            val sharedPreferences1 =
                                getSharedPreferences("share_pref_rate_caller", MODE_PRIVATE)
                            if (sec_time_open == 2 && !sec_time_boole) {
                                editor_rate.putBoolean("sec_time_cnboolean", true)
                                editor_rate.apply()
                                if (sharedPreferences1.getInt("contect_rate_prefrence", 0) < 2) {
                                    if (Glob.israting == false) {
//                                        RateDialog.Show_RateDialog(this@MainActivity)
                                        requestReviewInfo()

                                    } else {
                                        updatedialog()
                                    }
                                } else {
                                    updatedialog()
                                }
                            } else {
                                updatedialog()
                            }
                        }


                        progressDialog.dismiss()
                    }
                }

                sharedPreferences = getSharedPreferences("fregment_save_screen", 0)
                val call_fragment = sharedPreferences.getString("fragment_call", "")


                if (getIntent().getAction() != null && getIntent().getAction()
                        .equals("com.android.phone.action.RECENT_CALLS")
                ) {
                    this.binding.bottomNavigation.selectedItemId = R.id.item_call
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                } else if (call_fragment!!.contains("Setting_Fragment")) {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.GONE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.VISIBLE
                    val fragment = Setting_Fragment()
                    addFragment_setting(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_setting
                } else if (call_fragment!!.contains("Fav_Fragment")) {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.VISIBLE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Fav_Fragment()
                    addFragment_fav(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_fav
                } else if (call_fragment!!.contains("Contect_Fragment")) {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.VISIBLE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Contect_Fragment()
                    addFragment_contect(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_contect
                } else if (call_fragment!!.contains("Call_Fragment")) {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_call
                } else {
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)

                    editor.putString("fragment_call", fragment.toString())
                    editor.apply()
                }
            }

        }


        binding.relSetdefault.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= 29) {
                launchSetDefaultDialerIntent();

            } else {
                if (!permissionAlreadyGranted()) {
                    requestPermission()
                } else {

                    val progressDialog = ProgressDialog(this).apply {
                        setMessage(getString(R.string.str_load_contect))
                        setCancelable(false)
                    }
                    progressDialog.show()

                    lifecycleScope.launch {
                        contactRepository.fetchContacts(this@MainActivity)

                        contactRepository.contactsStateFlow.collect { contactItemList ->
                            progressDialog.dismiss()
                        }
                    }

                    if (rateus_onetime == true) {

                        rateus_onetime = false
                        if (!sec_time_boole) {
                            sec_time_open = sec_time_open + 1
                            editor_rate.putInt("sec_time_cnint", sec_time_open)
                            editor_rate.apply()
                        }
                        val sharedPreferences1 =
                            getSharedPreferences("share_pref_rate_caller", MODE_PRIVATE)
                        if (sec_time_open == 2 && !sec_time_boole) {
                            editor_rate.putBoolean("sec_time_cnboolean", true)
                            editor_rate.apply()
                            if (sharedPreferences1.getInt("contect_rate_prefrence", 0) < 2) {
                                if (Glob.israting == false) {
//                                    RateDialog.Show_RateDialog(this@MainActivity)
                                    requestReviewInfo()
                                } else {
                                    updatedialog()
                                }
                            } else {
                                updatedialog()
                            }
                        } else {
                            updatedialog()
                        }
                    }

                    sharedPreferences = getSharedPreferences("fregment_save_screen", 0)
                    val call_fragment = sharedPreferences.getString("fragment_call", "")


                    if (getIntent().getAction() != null && getIntent().getAction()
                            .equals("com.android.phone.action.RECENT_CALLS")
                    ) {
                        this.binding.bottomNavigation.selectedItemId = R.id.item_call
                        rel_default.visibility = View.GONE
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.VISIBLE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Call_Fragment()
                        addFragment_call(fragment)
                    } else if (call_fragment!!.contains("Setting_Fragment")) {
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.GONE
                        binding.callFramlayout.visibility = View.GONE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.VISIBLE
                        val fragment = Setting_Fragment()
                        addFragment_setting(fragment)
                        binding.bottomNavigation.selectedItemId = R.id.item_setting
                    } else if (call_fragment!!.contains("Fav_Fragment")) {
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.GONE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.VISIBLE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Fav_Fragment()
                        addFragment_fav(fragment)
                        binding.bottomNavigation.selectedItemId = R.id.item_fav
                    } else if (call_fragment!!.contains("Contect_Fragment")) {
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.GONE
                        binding.contectFramelayout.visibility = View.VISIBLE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Contect_Fragment()
                        addFragment_contect(fragment)
                        binding.bottomNavigation.selectedItemId = R.id.item_contect
                    } else if (call_fragment!!.contains("Call_Fragment")) {
                        rel_data.visibility = View.VISIBLE
                        rel_default.visibility = View.GONE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.VISIBLE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Call_Fragment()
                        addFragment_call(fragment)
                        binding.bottomNavigation.selectedItemId = R.id.item_call
                    } else {
                        rel_data.visibility = View.VISIBLE
                        rel_default.visibility = View.GONE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.VISIBLE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Call_Fragment()
                        addFragment_call(fragment)

                        editor.putString("fragment_call", fragment.toString())
                        editor.apply()
                    }

                }


            }

        })







        contactRepository = ContactRepository()


        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_call -> {
                    isseting = false
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)

                    editor.putString("fragment_call", fragment.toString())
                    editor.apply()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.item_contect -> {
                    isseting = false
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.VISIBLE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Contect_Fragment()
                    addFragment_contect(fragment)

                    editor.putString("fragment_call", fragment.toString())
                    editor.apply()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.item_fav -> {
                    isseting = false
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.VISIBLE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Fav_Fragment()
                    addFragment_fav(fragment)

                    editor.putString("fragment_call", fragment.toString())
                    editor.apply()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.item_setting -> {
                    binding.ivDialer.visibility = View.GONE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.VISIBLE
                    val fragment = Setting_Fragment()
                    addFragment_setting(fragment)
                    editor.putString("fragment_call", fragment.toString())
                    editor.apply()
                    return@setOnNavigationItemSelectedListener true
                }

                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        binding.imgSearch.setOnClickListener(View.OnClickListener {


            try {
                if (staticRecyclerView != null) {
                    staticRecyclerView!!.stopScroll()
                }
            } catch (e: Exception) {
            }

            val intent = Intent(this, Search_Activity::class.java)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Search activity open nahi ho paayi", Toast.LENGTH_SHORT).show()
            }

        })



        binding.ivDialer.setOnClickListener(View.OnClickListener {

            try {
                if (staticRecyclerView != null) {
                    staticRecyclerView!!.stopScroll()
                }
            } catch (e: Exception) {
            }

            val intent = Intent(this, DialpadActivity::class.java)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Dialpad open nahi ho paaya", Toast.LENGTH_SHORT).show()
            }

        })


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "Main_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("Main_Act_onBackpress", Bundle())

                if (Ads_Utils.showExitAd == "yes") {
                    if (!isFinishing) {
                        try {

                            exitDialogWithAd?.show()
                        } catch (e: WindowManager.BadTokenException) {

                        }
                    }
                } else {
                    var storedValue: Int = getStoredValue()

                    if (storedValue != 10) {
                        storedValue++
                        saveValue(storedValue)
                        if (storedValue == 1 || storedValue == 3 || storedValue == 6) {

                            if (!isFinishing) {
                                exit_dialg_withou_ads!!.show()

                            }
                            if (exit_dialg_withou_ads != null) {
                                StartCLick(4)
                                star_number = 4
                            } else {
                                finishAffinity()
                            }

                        } else {
                            finishAffinity()
                        }
                    } else {
                        finishAffinity()
                    }

                }


            }

        })



        Log.e("Contect_Event--", "Main_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Main_Act_onCreate", Bundle())

    }

    private fun addFragment_call(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.call_framlayout, fragment)
            .addToBackStack(fragment.javaClass.getSimpleName())
            .commit()

        binding.imgSearch.visibility = View.VISIBLE
    }

    private fun addFragment_contect(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contect_framelayout, fragment)
            .addToBackStack(fragment.javaClass.getSimpleName())
            .commit()

        binding.imgSearch.visibility = View.VISIBLE
    }


    private fun addFragment_fav(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fav_frame, fragment)
            .addToBackStack(fragment.javaClass.getSimpleName())
            .commit()


        binding.imgSearch.visibility = View.VISIBLE
    }

    private fun addFragment_setting(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.setting_frame, fragment)
            .addToBackStack(fragment.javaClass.getSimpleName())
            .commit()


        binding.imgSearch.visibility = View.GONE
    }


    fun permissionAlreadyGranted(): Boolean {
        if (Build.VERSION.SDK_INT < 29) {
            val result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            val result1 =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            val result3 =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)
            val result4 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                return false
            }
        }
        return true
    }


    fun requestPermission() {
        if (Build.VERSION.SDK_INT < 29) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CALL_LOG
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_CALL_LOG
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CALL_PHONE
                )
            ) {
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.CALL_PHONE
                ),
                105
            )
        }
    }


    fun launchSetDefaultDialerIntent() {
        if (Glob.isQPlus()) {
            val roleManager = getSystemService(RoleManager::class.java) as RoleManager
            Intrinsics.checkNotNull(roleManager)
            if (!roleManager.isRoleAvailable("android.app.role.DIALER") || roleManager.isRoleHeld("android.app.role.DIALER")) {
                return
            }
            val createRequestRoleIntent =
                roleManager.createRequestRoleIntent("android.app.role.DIALER")
            Intrinsics.checkNotNullExpressionValue(
                createRequestRoleIntent,
                "roleManager.createReques\u2026(RoleManager.ROLE_DIALER)"
            )

            startActivityForResult(createRequestRoleIntent, REQUEST_CODE_SET_DEFAULT_DIALER)
            return
        }
        Intent("android.telecom.action.CHANGE_DEFAULT_DIALER").putExtra(
            "android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME",
            packageName
        )
        try {

            val intent = intent
            Intrinsics.checkNotNullExpressionValue(intent, "intent")

            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
        } catch (unused: ActivityNotFoundException) {
        } catch (e: Exception) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            if (resultCode == Activity.RESULT_OK) {


                if (!Glob.isDefaultDialer(this)) {
                    try {
                        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
                        intent.setData(Uri.parse("package:" + getPackageName()))
                        startActivity(intent)
                    } catch (unused: java.lang.Exception) {
                    }
                    finish()
                    return
                } else {

                    val progressDialog = ProgressDialog(this).apply {
                        setMessage(getString(R.string.str_load_contect))
                        setCancelable(false)
                    }
                    progressDialog.show()

                    lifecycleScope.launch {
                        contactRepository.fetchContacts(this@MainActivity)

                        contactRepository.contactsStateFlow.collect { contactItemList ->
                            progressDialog.dismiss()
                        }
                    }

                    if (rateus_onetime == true) {

                        rateus_onetime = false
                        if (!sec_time_boole) {
                            sec_time_open = sec_time_open + 1
                            editor_rate.putInt("sec_time_cnint", sec_time_open)
                            editor_rate.apply()
                        }
                        val sharedPreferences1 =
                            getSharedPreferences("share_pref_rate_caller", MODE_PRIVATE)
                        if (sec_time_open == 2 && !sec_time_boole) {
                            editor_rate.putBoolean("sec_time_cnboolean", true)
                            editor_rate.apply()
                            if (sharedPreferences1.getInt("contect_rate_prefrence", 0) < 2) {
                                if (Glob.israting == false) {
//                                    RateDialog.Show_RateDialog(this@MainActivity)
                                    requestReviewInfo()
                                } else {
                                    updatedialog()
                                }
                            } else {
                                updatedialog()
                            }
                        } else {
                            updatedialog()
                        }
                    }

                    sharedPreferences = getSharedPreferences("fregment_save_screen", 0)
                    val call_fragment = sharedPreferences.getString("fragment_call", "")

                    if (getIntent().getAction() != null && getIntent().getAction()
                            .equals("com.android.phone.action.RECENT_CALLS")
                    ) {
                        this.binding.bottomNavigation.selectedItemId = R.id.item_call
                        rel_default.visibility = View.GONE
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.VISIBLE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Call_Fragment()
                        addFragment_call(fragment)
                    } else if (call_fragment!!.contains("Setting_Fragment")) {
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.GONE
                        binding.callFramlayout.visibility = View.GONE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.VISIBLE
                        val fragment = Setting_Fragment()
                        addFragment_setting(fragment)
                        binding.bottomNavigation.selectedItemId = R.id.item_setting
                    } else if (call_fragment!!.contains("Fav_Fragment")) {
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.GONE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.VISIBLE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Fav_Fragment()
                        addFragment_fav(fragment)
                        binding.bottomNavigation.selectedItemId = R.id.item_fav
                    } else if (call_fragment!!.contains("Contect_Fragment")) {
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.GONE
                        binding.contectFramelayout.visibility = View.VISIBLE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Contect_Fragment()
                        addFragment_contect(fragment)
                        binding.bottomNavigation.selectedItemId = R.id.item_contect
                    } else if (call_fragment!!.contains("Call_Fragment")) {
                        rel_data.visibility = View.VISIBLE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.VISIBLE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Call_Fragment()
                        addFragment_call(fragment)
                        binding.bottomNavigation.selectedItemId = R.id.item_call
                    } else {
                        rel_data.visibility = View.VISIBLE
                        rel_default.visibility = View.GONE
                        binding.ivDialer.visibility = View.VISIBLE
                        binding.callFramlayout.visibility = View.VISIBLE
                        binding.contectFramelayout.visibility = View.GONE
                        binding.favFrame.visibility = View.GONE
                        binding.settingFrame.visibility = View.GONE
                        val fragment = Call_Fragment()
                        addFragment_call(fragment)

                        editor.putString("fragment_call", fragment.toString())
                        editor.apply()
                    }


                }


            } else {

                Glob.showToast(this, getString(R.string.toast_default_dialer))

            }
        }

        if (requestCode == 101) {
            if (permissionAlreadyGranted()) {

                val progressDialog = ProgressDialog(this).apply {
                    setMessage(getString(R.string.str_load_contect))
                    setCancelable(false)
                }
                progressDialog.show()

                lifecycleScope.launch {
                    contactRepository.fetchContacts(this@MainActivity)

                    contactRepository.contactsStateFlow.collect { contactItemList ->
                        progressDialog.dismiss()
                    }
                }

                if (rateus_onetime == true) {

                    rateus_onetime = false
                    if (!sec_time_boole) {
                        sec_time_open = sec_time_open + 1
                        editor_rate.putInt("sec_time_cnint", sec_time_open)
                        editor_rate.apply()
                    }
                    val sharedPreferences1 =
                        getSharedPreferences("share_pref_rate_caller", MODE_PRIVATE)
                    if (sec_time_open == 2 && !sec_time_boole) {
                        editor_rate.putBoolean("sec_time_cnboolean", true)
                        editor_rate.apply()
                        if (sharedPreferences1.getInt("contect_rate_prefrence", 0) < 2) {
                            if (Glob.israting == false) {
//                                RateDialog.Show_RateDialog(this@MainActivity)
                                requestReviewInfo()
                            } else {
                                updatedialog()
                            }
                        } else {
                            updatedialog()
                        }
                    } else {
                        updatedialog()
                    }
                }

                sharedPreferences = getSharedPreferences("fregment_save_screen", 0)
                val call_fragment = sharedPreferences.getString("fragment_call", "")



                if (getIntent().getAction() != null && getIntent().getAction()
                        .equals("com.android.phone.action.RECENT_CALLS")
                ) {
                    this.binding.bottomNavigation.selectedItemId = R.id.item_call
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                } else if (call_fragment!!.contains("Setting_Fragment")) {
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.GONE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.VISIBLE
                    val fragment = Setting_Fragment()
                    addFragment_setting(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_setting
                } else if (call_fragment!!.contains("Fav_Fragment")) {
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.VISIBLE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Fav_Fragment()
                    addFragment_fav(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_fav
                } else if (call_fragment!!.contains("Contect_Fragment")) {
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.VISIBLE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Contect_Fragment()
                    addFragment_contect(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_contect
                } else if (call_fragment!!.contains("Call_Fragment")) {
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_call
                } else {
                    rel_data.visibility = View.VISIBLE
                    rel_default.visibility = View.GONE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)

                    editor.putString("fragment_call", fragment.toString())
                    editor.apply()
                }

                return
            }
            requestPermission()
        }



        if (requestCode == 1011) {
            if (permissionAlreadyGranted_notification()) {

            } else {

                requestPermission_notification()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 105) {

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {

                val progressDialog = ProgressDialog(this).apply {
                    setMessage(getString(R.string.str_load_contect))
                    setCancelable(false)
                }
                progressDialog.show()

                lifecycleScope.launch {
                    contactRepository.fetchContacts(this@MainActivity)

                    contactRepository.contactsStateFlow.collect { contactItemList ->
                        progressDialog.dismiss()
                    }
                }

                if (rateus_onetime == true) {

                    rateus_onetime = false
                    if (!sec_time_boole) {
                        sec_time_open = sec_time_open + 1
                        editor_rate.putInt("sec_time_cnint", sec_time_open)
                        editor_rate.apply()
                    }
                    val sharedPreferences1 =
                        getSharedPreferences("share_pref_rate_caller", MODE_PRIVATE)
                    if (sec_time_open == 2 && !sec_time_boole) {
                        editor_rate.putBoolean("sec_time_cnboolean", true)
                        editor_rate.apply()
                        if (sharedPreferences1.getInt("contect_rate_prefrence", 0) < 2) {
                            if (Glob.israting == false) {
                                requestReviewInfo()
                            } else {
                                updatedialog()
                            }
                        } else {
                            updatedialog()
                        }
                    } else {
                        updatedialog()
                    }
                }

                sharedPreferences = getSharedPreferences("fregment_save_screen", 0)
                val call_fragment = sharedPreferences.getString("fragment_call", "")



                if (getIntent().getAction() != null && getIntent().getAction()
                        .equals("com.android.phone.action.RECENT_CALLS")
                ) {
                    this.binding.bottomNavigation.selectedItemId = R.id.item_call
                    rel_default.visibility = View.GONE
                    rel_data.visibility = View.VISIBLE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                } else if (call_fragment!!.contains("Setting_Fragment")) {
                    rel_data.visibility = View.VISIBLE
                    rel_default.visibility = View.GONE
                    binding.ivDialer.visibility = View.GONE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.VISIBLE
                    val fragment = Setting_Fragment()
                    addFragment_setting(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_setting
                } else if (call_fragment!!.contains("Fav_Fragment")) {
                    rel_data.visibility = View.VISIBLE
                    rel_default.visibility = View.GONE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.VISIBLE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Fav_Fragment()
                    addFragment_fav(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_fav
                } else if (call_fragment!!.contains("Contect_Fragment")) {
                    rel_data.visibility = View.VISIBLE
                    rel_default.visibility = View.GONE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.GONE
                    binding.contectFramelayout.visibility = View.VISIBLE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Contect_Fragment()
                    addFragment_contect(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_contect
                } else if (call_fragment!!.contains("Call_Fragment")) {
                    rel_data.visibility = View.VISIBLE
                    rel_default.visibility = View.GONE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)
                    binding.bottomNavigation.selectedItemId = R.id.item_call
                } else {
                    rel_data.visibility = View.VISIBLE
                    rel_default.visibility = View.GONE
                    binding.ivDialer.visibility = View.VISIBLE
                    binding.callFramlayout.visibility = View.VISIBLE
                    binding.contectFramelayout.visibility = View.GONE
                    binding.favFrame.visibility = View.GONE
                    binding.settingFrame.visibility = View.GONE
                    val fragment = Call_Fragment()
                    addFragment_call(fragment)

                    editor.putString("fragment_call", fragment.toString())
                    editor.apply()
                }

                Log.e("Contect_Event--", "Main_Act_Permission_Success")
                MyApplication.mFirebaseAnalytics.logEvent(
                    "Main_Act_Permission_Success",
                    Bundle()
                )
            } else {
                val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)
                val showRationale1 =
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                val showRationale2 =
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG)
                val showRationale3 =
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALL_LOG)
                val showRationale4 =
                    shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)
                if (!showRationale && !showRationale1 && !showRationale2 && !showRationale3 && !showRationale4) {
                    openSettingsDialog()

                } else {
                    permissiondialog()

                }
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {


            } else {

                Toast.makeText(this, "Bluetooth permissions are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        if (requestCode == 11) {
            if (Build.VERSION.SDK_INT >= 33) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("Contect_Event--", "Main_Act_Permission_Success")
                    MyApplication.mFirebaseAnalytics.logEvent(
                        "Main_Act_Permission_Success",
                        Bundle()
                    )


                } else {
                    val showRationale =
                        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

                    if (!showRationale) {
                        openSettingsDialog_notification()
                    } else {
                        permissiondialog_notificaton()
                    }
                }
            }
        }
    }


    fun permissiondialog_notificaton() {
        permission_dialog_notification = Dialog(this@MainActivity)
        permission_dialog_notification!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        permission_dialog_notification!!.setContentView(R.layout.dialog_notification_permission)
        permission_dialog_notification!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        permission_dialog_notification!!.window!!.getAttributes().windowAnimations =
            R.style.dialog_theme
        permission_dialog_notification!!.window!!.setGravity(Gravity.BOTTOM)
        permission_dialog_notification!!.setCanceledOnTouchOutside(true)
        permission_dialog_notification!!.setCancelable(false)
        permission_dialog_notification!!.show()
        val lin_allow =
            permission_dialog_notification!!.findViewById<View>(R.id.lin_allow) as LinearLayout
        val img_cancel =
            permission_dialog_notification!!.findViewById<View>(R.id.img_close) as ImageView
        lin_allow.setOnClickListener(View.OnClickListener {
            permission_dialog_notification!!.dismiss()
            if (permissionAlreadyGranted_notification()) {
            } else {
                requestPermission_notification()

            }
        })

        img_cancel.setOnClickListener(View.OnClickListener {
            permission_dialog_notification!!.dismiss()
            if (!permissionAlreadyGranted_notification()) {
                requestPermission_notification()

            }
        })

        permission_dialog_notification!!.setOnCancelListener(DialogInterface.OnCancelListener {
            permission_dialog_notification!!.dismiss()
        })
    }

    private fun openSettingsDialog_notification() {
        setting_dialog_notification = Dialog(this@MainActivity)
        setting_dialog_notification!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        setting_dialog_notification!!.setContentView(R.layout.dialog_notication_setting)
        setting_dialog_notification!!.setCanceledOnTouchOutside(true)
        setting_dialog_notification!!.setCancelable(false)
        setting_dialog_notification!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        setting_dialog_notification!!.window!!.getAttributes().windowAnimations =
            R.style.dialog_theme
        setting_dialog_notification!!.window!!.setGravity(Gravity.BOTTOM)



        Handler().postDelayed(Runnable {
            if (!setting_dialog_notification!!.isShowing()) {
                setting_dialog_notification!!.show()

            }

        }, 1000)


        val img_close =
            setting_dialog_notification!!.findViewById<View>(R.id.img_close) as ImageView
        val tv_setting =
            setting_dialog_notification!!.findViewById<View>(R.id.lin_setting) as LinearLayout
        img_close.setOnClickListener {
            setting_dialog_notification!!.cancel()

            if (!permissionAlreadyGranted()) {
                requestPermission()

            }
        }
        tv_setting.setOnClickListener {
            setting_dialog_notification!!.cancel()


            val intent = Intent()
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
            intent.putExtra("app_package", packageName)
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
            startActivityForResult(intent, 1011)

        }
    }


    fun requestReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this)
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
                showReviewFlow()
            } else {

            }
        }
    }

    private fun showReviewFlow() {
        reviewInfo?.let { info ->
            val flow: Task<Void> = reviewManager!!.launchReviewFlow(this, info)
            flow.addOnCompleteListener { task ->
                if (task.isSuccessful) {


                } else {

                }
            }
        } ?: run {


        }
    }


    private fun ShowAdView() {
        if (Ads_Utils.showGoogleBannerMain == "yes") {
            if (Ads_Utils.onlyMoreAppBannerMain == "yes") {
                if (Ads_Utils.more_List_data.size > 0) {
                    Log.e(LOG_TAG, "More_App_Fetch_Banner")
                    More_App_Fetch_BannerAd(
                        activity = this,
                        frameLayout = fram_banner!!
                    )
                } else {
                    txt_ads_space?.visibility = View.VISIBLE
                }
            } else {
                Log.e(LOG_TAG, "Loaded_Banner")
                gg_BannerAd(
                    activity = this,
                    adID = Ads_Utils.GOOGLE_BANNER_MAIN,
                    tvAdText = txt_ads_space!!,
                    frameLayout = fram_banner!!
                )
            }
        } else {
            rel_banner?.visibility = View.GONE
        }
    }

    private fun fetchAdRequest(): AdRequest {
        val extras = Bundle().apply {
            putString("maxContentRatingAd", Ads_Utils.maxContentRatingAd)
        }
        return AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
    }

    private fun gg_BannerAd(
        activity: Activity,
        adID: String,
        tvAdText: TextView,
        frameLayout: FrameLayout
    ) {

        val adViewGoogle = AdView(activity)
        adViewGoogle.adUnitId = adID
        frameLayout.addView(adViewGoogle)
        val adSize = fetchbannerAdSize(activity, frameLayout)

        adViewGoogle.setAdSize(adSize)

        val request = fetchAdRequest()
        adViewGoogle.loadAd(request)
        adViewGoogle.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("main_Banner", "onAdFailedToLoad${loadAdError.code}")
                if (Ads_Utils.showMoreBanner == "yes") {
                    if (Ads_Utils.more_List_data.size > 0) {
                        if (!activity.isFinishing) {
                            More_App_Fetch_BannerAd(activity, frameLayout)
                        }
                    }
                }
                tvAdText.visibility = View.VISIBLE
            }

            override fun onAdLoaded() {
                Log.e("main_Banner", "${TAG}_onAdLoaded")
                frameLayout.visibility = View.VISIBLE
                tvAdText.visibility = View.GONE
            }

            override fun onAdClicked() {
                Log.e("main_Banner", "${TAG}_onAdClicked")
                Handler(Looper.getMainLooper()).postDelayed({
                }, 500)
                gg_BannerAd(activity, adID, tvAdText, frameLayout)
            }

        }
    }

    private fun fetchbannerAdSize(
        activity: Activity,
        frameLayout: FrameLayout
    ): AdSize {

        val display: Display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = frameLayout.width

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels
        }

        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    private fun More_App_Fetch_BannerAd(
        activity: Activity,
        frameLayout: FrameLayout
    ) {
        val view = activity.layoutInflater.inflate(
            R.layout.google_banner_ad_more,
            activity.findViewById(R.id.bannerAd),
            false
        )
        val app_icon_more = view.findViewById<ImageView>(R.id.app_icon_more)
        val txt_name_moreas = view.findViewById<TextView>(R.id.txt_name_moreas)
        val txt_adsbodymore = view.findViewById<TextView>(R.id.txt_adsbodymore)
        val txt_ads_call_action_more = view.findViewById<TextView>(R.id.txt_ads_call_action_more)
        frameLayout.removeAllViews()
        frameLayout.addView(view)

        Ads_Utils.adCount += 1

        if (Ads_Utils.more_List_data.size == Ads_Utils.adCount) {
            Ads_Utils.adCount = 0
        }

        val number = Ads_Utils.adCount

        Glide.with(activity.applicationContext)
            .asBitmap()
            .load(Ads_Utils.more_List_data[number].more_appIcon)
            .into(app_icon_more)
        txt_name_moreas.text = Ads_Utils.more_List_data[number].more_appName
        txt_adsbodymore.text = Ads_Utils.more_List_data[number].more_appDescription

        txt_ads_call_action_more.text = activity.getString(R.string.str_install)

        app_icon_more.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoredataBanner_click")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_name_moreas.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoredataBanner_click")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_adsbodymore.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoredataBanner_click")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_ads_call_action_more.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoredataBanner_click")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }
        Log.e(LOG_TAG, "${TAG}_MoredataBanner_show")
    }


    public fun permissionAlreadyGranted_notification(): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            val result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (result == PackageManager.PERMISSION_GRANTED) {
                true
            } else false
        } else {
            false
        }
    }


    public fun requestPermission_notification() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                11
            )
        } else {

        }
    }

    private fun openSettingsDialog() {
        setting_dialog = Dialog(this@MainActivity)
        setting_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        setting_dialog!!.setContentView(R.layout.dialog_per_setting)
        setting_dialog!!.setCanceledOnTouchOutside(true)
        setting_dialog!!.setCancelable(false)
        setting_dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        setting_dialog!!.window!!.getAttributes().windowAnimations = R.style.dialog_theme
        setting_dialog!!.window!!.setGravity(Gravity.BOTTOM)
        setting_dialog!!.show()
        val img_close = setting_dialog!!.findViewById<View>(R.id.img_cancel) as ImageView
        val lin_setting = setting_dialog!!.findViewById<View>(R.id.lin_setting) as LinearLayout
        img_close.setOnClickListener {
            setting_dialog!!.cancel()

            if (!permissionAlreadyGranted()) {
                requestPermission()

            }
        }
        lin_setting.setOnClickListener {
            setting_dialog!!.cancel()
            if (!Checkpermission()) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivityForResult(intent, 101)
            } else {
                opendialog = true
                val intent = Intent()
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                intent.putExtra("app_package", packageName)
                intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
                startActivity(intent)
            }
        }
    }

    private fun Checkpermission(): Boolean {
        if (Build.VERSION.SDK_INT <= 29) {
            val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            val result1 =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            val result3 =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)
            val result4 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED) {
                true
            } else false
        }
        return false
    }

    fun permissiondialog() {
        permission_dialog = Dialog(this@MainActivity)
        permission_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        permission_dialog!!.setContentView(R.layout.dialog_permission)
        permission_dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        permission_dialog!!.window!!.setGravity(Gravity.BOTTOM)
        permission_dialog!!.window!!.getAttributes().windowAnimations = R.style.dialog_theme
        permission_dialog!!.setCanceledOnTouchOutside(true)
        permission_dialog!!.setCancelable(false)
        permission_dialog!!.show()
        val lin_allow = permission_dialog!!.findViewById<View>(R.id.lin_allow) as LinearLayout
        val img_cancel = permission_dialog!!.findViewById<View>(R.id.img_close) as ImageView
        lin_allow.setOnClickListener(View.OnClickListener {
            permission_dialog!!.dismiss()
            if (permissionAlreadyGranted()) {
            } else {
                requestPermission()

            }
        })

        img_cancel.setOnClickListener(View.OnClickListener {
            permission_dialog!!.dismiss()
            if (permissionAlreadyGranted()) {
                return@OnClickListener
            }
            requestPermission()
        })

        permission_dialog!!.setOnCancelListener(DialogInterface.OnCancelListener {
            permission_dialog!!.dismiss()
        })
    }

    public fun Exit_dialog_Initalize() {
        exit_dialg_withou_ads = Dialog(this@MainActivity)
        exit_dialg_withou_ads!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))



        exit_dialg_withou_ads!!.getWindow()!!.setGravity(Gravity.CENTER)
        exit_dialg_withou_ads!!.setCancelable(true)
        exit_dialg_withou_ads!!.setContentView(R.layout.dialog_exitapp)
        exit_dialg_withou_ads!!.getWindow()!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        exit_dialg_withou_ads!!.getWindow()!!.attributes.windowAnimations = R.style.dialog_theme


        val img_exit_icon = exit_dialg_withou_ads!!.findViewById<ImageView>(R.id.img_exit_icon)
        val img_close_exit = exit_dialg_withou_ads!!.findViewById<ImageView>(R.id.img_close_exit)
        val lin_exit = exit_dialg_withou_ads!!.findViewById<LinearLayout>(R.id.lin_exit_app)
        val lin_submit = exit_dialg_withou_ads!!.findViewById<LinearLayout>(R.id.lin_submit)

        img_rate1 = exit_dialg_withou_ads!!.findViewById<ImageView>(R.id.img_rate1)
        img_rate2 = exit_dialg_withou_ads!!.findViewById<ImageView>(R.id.img_rate2)
        img_rate3 = exit_dialg_withou_ads!!.findViewById<ImageView>(R.id.img_rate3)
        img_rate4 = exit_dialg_withou_ads!!.findViewById<ImageView>(R.id.img_rate4)
        img_rate5 = exit_dialg_withou_ads!!.findViewById<ImageView>(R.id.img_rate5)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        val theme = Glob.check_theme()


        when (theme) {
            "light" -> img_exit_icon.setImageResource(R.drawable.ic_exit_app)

            "dark" -> img_exit_icon.setImageResource(R.drawable.ic_exit_app_dark)
            "System_theme" -> if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                img_exit_icon.setImageResource(R.drawable.ic_exit_app_dark)
            } else {
                img_exit_icon.setImageResource(R.drawable.ic_exit_app)
            }
        }

        img_rate1!!.setOnClickListener(View.OnClickListener {
            star_number = 1
            StartCLick(1)
        })

        img_rate2!!.setOnClickListener(View.OnClickListener {
            star_number = 2
            StartCLick(2)
        })
        img_rate3!!.setOnClickListener(View.OnClickListener {
            star_number = 3
            StartCLick(3)

        })
        img_rate4!!.setOnClickListener(View.OnClickListener {
            star_number = 4
            StartCLick(4)
        })
        img_rate5!!.setOnClickListener(View.OnClickListener {
            star_number = 5
            StartCLick(5)
        })

        star_number = 4
        StartCLick(4)
        lin_submit.setOnClickListener {
            val str = "android.intent.action.VIEW"
            exit_dialg_withou_ads!!.dismiss()
            if (star_number >= 3) {
                Glob.showToast(this, getString(R.string.toast_rate_good))

            } else {
                Glob.showToast(this, getString(R.string.toast_rate))

            }

            val marketIntent = Intent(str, Uri.parse("market://details?id=$packageName"))
            if (marketIntent.resolveActivity(packageManager) != null) {
                startActivity(marketIntent)
            } else {
                val webIntent = Intent(str, Uri.parse("http://play.google.com/store/apps/details?id=$packageName"))
                if (webIntent.resolveActivity(packageManager) != null) {
                    startActivity(webIntent)
                } else {
                    Toast.makeText(this, "No app found to open Play Store", Toast.LENGTH_SHORT).show()
                }
            }

        }
        img_close_exit.setOnClickListener {
            try {
                exit_dialg_withou_ads!!.dismiss()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        lin_exit.setOnClickListener {
            try {
                exit_dialg_withou_ads!!.dismiss()
                finishAffinity()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            MyApplication.mFirebaseAnalytics.logEvent("Act_Main_exit", Bundle())
        }


    }


    public fun ExitdialogWithads() {
        exitDialogWithAd = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        exitDialogWithAd!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))


        exitDialogWithAd!!.getWindow()!!.setGravity(Gravity.CENTER)
        exitDialogWithAd!!.setCancelable(true)
        exitDialogWithAd!!.setCanceledOnTouchOutside(true)
        exitDialogWithAd!!.setContentView(R.layout.dialog_exit_with_ad)
        exitDialogWithAd!!.window!!.setGravity(Gravity.BOTTOM)
        exitDialogWithAd!!.getWindow()!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val rlNative = exitDialogWithAd!!.findViewById<RelativeLayout>(R.id.rlNative)
        val textSpaceFrame = exitDialogWithAd!!.findViewById<FrameLayout>(R.id.textSpaceFrame)
        val space = exitDialogWithAd!!.findViewById<Space>(R.id.space)
        val tvAdText = exitDialogWithAd!!.findViewById<TextView>(R.id.tvAdText)
        val flNative = exitDialogWithAd!!.findViewById<FrameLayout>(R.id.flNative)

        val btnTapToExit = exitDialogWithAd!!.findViewById<TextView>(R.id.btnTapToExit)


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        mHeight = displayMetrics.heightPixels
        val params = space?.layoutParams
        params?.height = mHeight / 5
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        space?.layoutParams = params

        if (Ads_Utils.onlyMoreNativeExit == "yes") {
            if (Ads_Utils.more_List_data.size > 0) {
                fetchMoreAppNativeAd(
                    this,
                    flNative
                )
            } else {
                tvAdText?.visibility = View.VISIBLE
            }
        } else {
            if (NativeAdsLoaded.exitNativeAds != null) {
                showNativeBanner(
                    activity = this,
                    frameLayout = flNative,
                    tvAdText = tvAdText,
                    nativeAd = NativeAdsLoaded.exitNativeAds!!
                )
            } else {
                googleNativeAd(
                    activity = this,
                    adID = Ads_Utils.GN_EXIT_DIALOG,
                    tvAdText = tvAdText,
                    frameLayout = flNative
                )
            }
        }

        btnTapToExit?.setOnClickListener {
            exitDialogWithAd!!.dismiss()
            finishAffinity()
        }


    }

    private fun getAddRequest(): AdRequest {
        val extras = Bundle()
        extras.putString("maxContentRatingAd", Ads_Utils.maxContentRatingAd)
        return AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
    }

    fun googleNativeAd(
        activity: Activity,
        adID: String,
        tvAdText: TextView?,
        frameLayout: FrameLayout?
    ) {
        val builder = AdLoader.Builder(activity, adID).forNativeAd { nativeAd ->
            NativeAdsLoaded.exitNativeAds = nativeAd
            Log.e(TAG, "${TAG}_onNativeAdLoaded")
            tvAdText?.visibility = View.GONE
            showNativeBanner(activity, frameLayout, tvAdText, nativeAd)
        }

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e(TAG, "${TAG}_onAdFailedToLoad_$loadAdError")
                NativeAdsLoaded.exitNativeAds = null
                if (Ads_Utils.showMoreNative == "yes") {
                    if (Ads_Utils.more_List_data.size > 0) {
                        if (!activity.isFinishing) {
                            fetchMoreAppNativeAd(activity, frameLayout)
                        }
                    } else {
                        tvAdText?.visibility = View.VISIBLE
                    }
                } else {
                    tvAdText?.visibility = View.VISIBLE
                }
            }

            override fun onAdLoaded() {
                tvAdText?.visibility = View.GONE
            }

            override fun onAdClicked() {
                NativeAdsLoaded.exitNativeAds = null
                Log.e(TAG, "${TAG}_onAdClicked")

                googleNativeAd(activity, adID, tvAdText, frameLayout)
            }
        }).build()

        val request = getAddRequest()
        adLoader.loadAd(request)
    }

    private fun showNativeBanner(
        activity: Activity,
        frameLayout: FrameLayout?,
        tvAdText: TextView?,
        nativeAd: NativeAd
    ) {
        tvAdText?.visibility = View.GONE

        val adView = activity.layoutInflater.inflate(
            R.layout.google_native_banner_ad,
            activity.findViewById(R.id.nativeAd),
            false
        ) as NativeAdView

        populateAppInstallAdView(nativeAd, adView)
        frameLayout?.removeAllViews()
        frameLayout?.addView(adView)
    }

    private fun populateAppInstallAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        adView.iconView = adView.findViewById(R.id.adIcon)
        adView.headlineView = adView.findViewById(R.id.adName)
        adView.bodyView = adView.findViewById(R.id.adBody)

        val mediaView = adView.findViewById<MediaView>(R.id.adMedia)
        val params = mediaView.layoutParams

        if (mHeight / 5 > 300) {
            params.height = mHeight / 5
        } else {
            params.height = 300
        }
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        mediaView.layoutParams = params
        mediaView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                if (child is ImageView) {
                    child.adjustViewBounds = true
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {}
        })
        adView.mediaView = mediaView

        adView.callToActionView = adView.findViewById(R.id.adCallToAction)
        (adView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            adView.iconView?.visibility = View.VISIBLE
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }

        adView.setNativeAd(nativeAd)
    }

    fun fetchMoreAppNativeAd(
        activity: Activity,
        frameLayout: FrameLayout?
    ) {
        val view = activity.layoutInflater.inflate(
            R.layout.google_native_ad_view_moreapp,
            activity.findViewById(R.id.nativeAd),
            false
        )
        val app_icon_more = view.findViewById<ImageView>(R.id.app_icon_more)
        val txt_name_moreas = view.findViewById<TextView>(R.id.txt_name_moreas)
        val txt_adsbodymore = view.findViewById<TextView>(R.id.txt_adsbodymore)
        val adMediamore = view.findViewById<ImageView>(R.id.adMediamore)
        val txt_ads_call_action_more = view.findViewById<TextView>(R.id.txt_ads_call_action_more)
        frameLayout?.removeAllViews()
        frameLayout?.addView(view)

        Ads_Utils.adCount += 1

        if (Ads_Utils.more_List_data.size == Ads_Utils.adCount) {
            Ads_Utils.adCount = 0
        }

        val number = Ads_Utils.adCount

        Glide.with(activity.applicationContext)
            .asBitmap()
            .load(Ads_Utils.more_List_data[number].more_appIcon)
            .into(app_icon_more)
        txt_name_moreas.text = Ads_Utils.more_List_data[number].more_appName
        txt_adsbodymore.text = Ads_Utils.more_List_data[number].more_appDescription

        val adMediaCloneParam = adMediamore.layoutParams
        if (mHeight / 5 > 300) {
            adMediaCloneParam.height = mHeight / 5
        } else {
            adMediaCloneParam.height = 300
        }
        adMediaCloneParam.width = ViewGroup.LayoutParams.MATCH_PARENT
        adMediamore.layoutParams = adMediaCloneParam

        Glide.with(activity.applicationContext)
            .asBitmap()
            .load(Ads_Utils.more_List_data[number].more_appBanner)
            .into(adMediamore)

        txt_ads_call_action_more.text = activity.getString(R.string.str_install)

        adMediamore.setOnClickListener {
            Log.e(TAG, "${TAG}_fetchMoreNativeAd_click")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_ads_call_action_more.setOnClickListener {
            Log.e(TAG, "${TAG}_fetchMoreNativeAd_click")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }
        Log.e(TAG, "${TAG}_fetchMoreNativeAd_show")
    }

    private fun DisplayAdClick(activity: Activity, link: String) {
        val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        if (viewIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(viewIntent)
        } else {

        }
    }

    fun StartCLick(value: Int) {
        img_rate1.setImageResource(R.drawable.ic_star_unselect)
        img_rate2.setImageResource(R.drawable.ic_star_unselect)
        img_rate3.setImageResource(R.drawable.ic_star_unselect)
        img_rate4.setImageResource(R.drawable.ic_star_unselect)
        img_rate5.setImageResource(R.drawable.ic_star_unselect)

        if (value == 1) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
        } else if (value == 2) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
            img_rate2.setImageResource(R.drawable.ic_star_selected)
        } else if (value == 3) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
            img_rate2.setImageResource(R.drawable.ic_star_selected)
            img_rate3.setImageResource(R.drawable.ic_star_selected)
        } else if (value == 4) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
            img_rate2.setImageResource(R.drawable.ic_star_selected)
            img_rate3.setImageResource(R.drawable.ic_star_selected)
            img_rate4.setImageResource(R.drawable.ic_star_selected)
        } else if (value == 5) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
            img_rate2.setImageResource(R.drawable.ic_star_selected)
            img_rate3.setImageResource(R.drawable.ic_star_selected)
            img_rate4.setImageResource(R.drawable.ic_star_selected)
            img_rate5.setImageResource(R.drawable.ic_star_selected)
        }
    }

    private fun getStoredValue(): Int {
        val prefs = getSharedPreferences(PREFS_NAME_EXit, MODE_PRIVATE)
        return prefs.getInt(VALUE_KEY_Exit, 0)
    }

    private fun saveValue(value: Int) {
        val editor = getSharedPreferences(PREFS_NAME_EXit, MODE_PRIVATE).edit()
        editor.putInt(VALUE_KEY_Exit, value)
        editor.apply()
    }


    fun updatedialog() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) || appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.FLEXIBLE
                ))
            ) {
                try {
                    appUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                            .setAllowAssetPackDeletion(true)
                            .build(),
                        MY_REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
        appUpdateManager!!.registerListener(listener)
    }


    override fun onStop() {
        super.onStop()
        appUpdateManager!!.unregisterListener(listener)
    }


    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "App Update Alomost done.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(
            "RESTART"
        ) { view: View? -> appUpdateManager!!.completeUpdate() }

        val color = Glob.getResourceFromAttr(R.attr.text_color, this)
        snackbar.setActionTextColor(
            color
        )
        snackbar.show()
    }

    var listener = InstallStateUpdatedListener { state: InstallState ->
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            val bytesDownloaded = state.bytesDownloaded()
            val totalBytesToDownload = state.totalBytesToDownload()
        }
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate()
        }
    }

    fun hideKeyboard(view: View, context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()



        if (appUpdateManager != null) {
            appUpdateManager!!.getAppUpdateInfo()
                .addOnSuccessListener(OnSuccessListener<AppUpdateInfo> { appUpdateInfo: AppUpdateInfo ->
                    if (appUpdateInfo.updateAvailability()
                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    ) {
                        try {
                            appUpdateManager!!.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                this,
                                MY_REQUEST_CODE
                            )
                        } catch (e: SendIntentException) {
                            e.printStackTrace()
                        }
                    }
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate()
                    }
                })
        }



        if (opendialog) {
            opendialog = false
            if (permissionAlreadyGranted()) {

            } else {
                requestPermission()
            }
        }

        if (opendialog_noti && !setting_dialog_notification!!.isShowing) {
            opendialog_noti = false




            if (permissionAlreadyGranted_notification()) {

            } else {
                requestPermission_notification()
            }
        }


        val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val ratingShown = sharedPrefs.getBoolean("rate_dialog_shown_caller", false)



        if (!ratingShown) {
            if (callend) {
                if (Glob.israting == false) {
                    requestReviewInfo()
                }

                sharedPrefs.edit().putBoolean("rate_dialog_shown_caller", true).apply()
            }
        }
        Handler().postDelayed(Runnable {

            if (rel_main != null) {
                hideKeyboard(rel_main, this)

            }

        }, 500)

        try {

            if (Build.VERSION.SDK_INT >= 29) {
                if (!Glob.isDefaultDialer(this)) {
                    rel_data.visibility = View.GONE
                    rel_default.visibility = View.VISIBLE
                    val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color, this)
                    val color1 = Glob.getResourceFromAttr(R.attr.navigation_bg_color, this)
//
                    iscontect = false
                    window.statusBarColor = color
                    window.navigationBarColor = color1

                    Glob.SetStatusbarColor(window)


                } else {
                    Handler().postDelayed(Runnable {
                        try {


                            if (isseting) {
                                isseting = false
                                rel_default.visibility = View.GONE
                                rel_data.visibility = View.VISIBLE
                                binding.ivDialer.visibility = View.GONE
                                binding.callFramlayout.visibility = View.GONE
                                binding.contectFramelayout.visibility = View.GONE
                                binding.favFrame.visibility = View.GONE
                                binding.settingFrame.visibility = View.VISIBLE
                                val fragment = Setting_Fragment()
                                addFragment_setting(fragment)
                                binding.bottomNavigation.selectedItemId = R.id.item_setting

                                return@Runnable
                            }
                            sharedPreferences = getSharedPreferences("fregment_save_screen", 0)
                            val call_fragment = sharedPreferences.getString("fragment_call", "")


                            if (!iscontect) {
                                if (call_fragment!!.contains("Setting_Fragment")) {
                                    rel_default.visibility = View.GONE
                                    rel_data.visibility = View.VISIBLE
                                    binding.ivDialer.visibility = View.GONE
                                    binding.callFramlayout.visibility = View.GONE
                                    binding.contectFramelayout.visibility = View.GONE
                                    binding.favFrame.visibility = View.GONE
                                    binding.settingFrame.visibility = View.VISIBLE
                                    val fragment = Setting_Fragment()
                                    addFragment_setting(fragment)
                                    binding.bottomNavigation.selectedItemId = R.id.item_setting
                                } else if (call_fragment!!.contains("Fav_Fragment")) {
                                    rel_default.visibility = View.GONE
                                    rel_data.visibility = View.VISIBLE
                                    binding.ivDialer.visibility = View.VISIBLE
                                    binding.callFramlayout.visibility = View.GONE
                                    binding.contectFramelayout.visibility = View.GONE
                                    binding.favFrame.visibility = View.VISIBLE
                                    binding.settingFrame.visibility = View.GONE
                                    val fragment = Fav_Fragment()
                                    addFragment_fav(fragment)
                                    binding.bottomNavigation.selectedItemId = R.id.item_fav
                                } else if (call_fragment!!.contains("Contect_Fragment")) {
                                    rel_default.visibility = View.GONE
                                    rel_data.visibility = View.VISIBLE
                                    binding.ivDialer.visibility = View.VISIBLE
                                    binding.callFramlayout.visibility = View.GONE
                                    binding.contectFramelayout.visibility = View.VISIBLE
                                    binding.favFrame.visibility = View.GONE
                                    binding.settingFrame.visibility = View.GONE
                                    val fragment = Contect_Fragment()
                                    addFragment_contect(fragment)
                                    binding.bottomNavigation.selectedItemId = R.id.item_contect
                                } else if (call_fragment!!.contains("Call_Fragment")) {
                                    rel_default.visibility = View.GONE
                                    rel_data.visibility = View.VISIBLE
                                    binding.ivDialer.visibility = View.VISIBLE
                                    binding.callFramlayout.visibility = View.VISIBLE
                                    binding.contectFramelayout.visibility = View.GONE
                                    binding.favFrame.visibility = View.GONE
                                    binding.settingFrame.visibility = View.GONE
                                    val fragment = Call_Fragment()
                                    addFragment_call(fragment)
                                    binding.bottomNavigation.selectedItemId = R.id.item_call
                                } else {
                                    rel_default.visibility = View.GONE
                                    rel_data.visibility = View.VISIBLE
                                    binding.ivDialer.visibility = View.VISIBLE
                                    binding.callFramlayout.visibility = View.VISIBLE
                                    binding.contectFramelayout.visibility = View.GONE
                                    binding.favFrame.visibility = View.GONE
                                    binding.settingFrame.visibility = View.GONE
                                    val fragment = Call_Fragment()
                                    addFragment_call(fragment)
                                    binding.bottomNavigation.selectedItemId = R.id.item_call
                                    editor.putString("fragment_call", fragment.toString())
                                    editor.apply()
                                }
                            }


                        } catch (e: Exception) {
                        }

                    }, 500)

                }

            } else {


            }


        } catch (e: Exception) {
        }


    }
}