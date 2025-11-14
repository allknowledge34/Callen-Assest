package com.phonecontactscall.contectapp.phonedialerr.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.ads.adsload.InterstitialAds
import com.phonecontactscall.contectapp.phonedialerr.ads.adsload.NativeAdsLoaded
import com.phonecontactscall.contectapp.phonedialerr.ads.dialog.ContectAppUpdateDialog
import com.phonecontactscall.contectapp.phonedialerr.ads.firebasedataget.RemoteConfigData
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivitySplashBinding
import java.util.concurrent.atomic.AtomicBoolean


class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private var milliSec = 1000L


    private var Handler: Handler? = null
    private var Runnable: Runnable? = null


    private var remoteConfigData: RemoteConfigData? = null
    private lateinit var consentInformation: ConsentInformation

    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)


    override fun getViewBinding(): ActivitySplashBinding {

        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)



        if (remoteConfigData?.syatemUpdateDialog?.isShowing == true) {
            remoteConfigData?.syatemUpdateDialog?.dismiss()
        }
        remoteConfigData?.syatemUpdateDialog = ContectAppUpdateDialog(this)
        remoteConfigData?.syatemUpdateDialog?.show()

        Handler = Handler(Looper.getMainLooper())
        Runnable = Runnable {
            if (Ads_Utils.Check_Network_Connected(this)) {
                if (Ads_Utils.Update_Dialog == "yes") {
                    if (remoteConfigData?.syatemUpdateDialog != null &&
                        !remoteConfigData?.syatemUpdateDialog!!.isShowing
                    ) {
                        remoteConfigData?.syatemUpdateDialog?.show()
                    }
                } else {
                    startIntent()
                }
            } else {
                startIntent()
            }
        }
        Handler?.postDelayed(Runnable!!, milliSec)
    }


    override fun initView() {
        initializeViews()
        rateus_onetime = true

        isNetwork = false
        Ads_Utils.ispauseResume = false
        Ads_Utils.check_Loaded_ads_ID = false
        Ads_Utils.check_Splash_Showed = false
        InterstitialAds.AdsShow_original = 0



        if ((!isTaskRoot() && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)) && getIntent().getAction() != null && getIntent().getAction()
                .equals(Intent.ACTION_MAIN)
        ) {
            finish();
            return;
        }

//        startTimer(milliSec)
        consentDialog()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "Splash_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("Splash_Act_onBackpress", Bundle())
                finishAffinity()

            }

        })

        Log.e("Contect_Event--", "Splash_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Splash_Act_onCreate", Bundle())

    }


    private fun initializeViews() {
        val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color, this)
        val color1 = Glob.getResourceFromAttr(R.attr.default_bg_color, this)

        //
        window.statusBarColor = color1
        window.navigationBarColor = color1
        Glob.SetStatusbarColor(window)


    }


    private fun startTimer(milliSec: Long) {

        Log.e(LOG_TAG, "startTimer")


        if (Ads_Utils.Check_Network_Connected(this)) {
//            remoteConfigData = RemoteConfigData(activity = this)
            if (remoteConfigData?.syatemUpdateDialog == null) {
                remoteConfigData?.syatemUpdateDialog = ContectAppUpdateDialog(
                    activity = this
                )
            }
        }

        Handler = Handler(Looper.getMainLooper())
        Runnable = Runnable {
            if (Ads_Utils.Check_Network_Connected(this)) {
                if (Ads_Utils.Update_Dialog == "yes") {
                    if (remoteConfigData?.syatemUpdateDialog != null &&
                        !remoteConfigData?.syatemUpdateDialog!!.isShowing
                    ) {
                        remoteConfigData?.syatemUpdateDialog?.show()
                    }
                } else {
                    startIntent()

                }
            } else {
                startIntent()
            }
        }
        Handler?.postDelayed(Runnable!!, milliSec)
    }

    private fun startIntent() {
        if (!Ads_Utils.check_Splash_Showed) {
            Ads_Utils.check_Splash_Showed = true

            if (!Glob.getInstance().GetBoolean(this, "chke_lang_open")) {

                val intent = Intent(this, Continue_Activity::class.java)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                }

            } else {

                if (Build.VERSION.SDK_INT >= 29) {
                    if (!Glob.isDefaultDialer(this)) {
                        val intent = Intent(this, DefaultCall_Activity::class.java)
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                        }
                    } else {

                        val intent = Intent(this, MainActivity::class.java)
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                        }
                    }
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                    }
                }

            }
            finish()

        }
    }


    private fun consentDialog() {
        requestConsentForm_Called = true
        Log.d("ggggg-----", "aaaaaa")

        // Uncomment and use if needed
//    val debugSettings = ConsentDebugSettings.Builder(this)
//        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//        .addTestDeviceHashedId("D8CE6C6A73B50147D46C8EBEDD5A2652")
//        .build()

        val params = ConsentRequestParameters.Builder()
//            .setConsentDebugSettings(debugSettings)
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)

        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                Log.d("ggggg-----", "aaaaaa1111111111")

                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this
                ) { loadAndShowError ->
                    if (loadAndShowError != null) {
                        Log.d("ggggg-----", "bbbbbbb")
                        Log.w("TAG", "${loadAndShowError.errorCode}: ${loadAndShowError.message}")
                    }

                    if (consentInformation.canRequestAds()) {
                        Log.d("ggggg-----", "cccccccc")
                        initializeMobileAdsSdk()

                        MobileAds.initialize(this) { initializationStatus ->
                            Log.d("AdMob", "MobileAds initialized")
                            Log.e("Ads_Demo---", " MobileAds.initialize suc ")

                            if (!NativeAdsLoaded.isLangLoading) {
                                if (Ads_Utils.showLangNativeAd == "yes") {
                                    if (!Glob.getInstance().GetBoolean(this@SplashActivity, "chke_lang_open")) {
                                        if (Ads_Utils.onlyLangMore != "yes") {
                                            Log.e("Splash_ads---", "lange_native_onlyLangMore")
                                            if (Ads_Utils.showBigLangNative == "yes") {
                                                NativeAdsLoaded.issplashLangLoading = true
                                                NativeAdsLoaded.fetchGoogleNativeAds(
                                                    this@SplashActivity,
                                                    Ads_Utils.GN_LANGUAGE
                                                ) { nativeAd ->
                                                    if (NativeAdsLoaded.languageNativeAds != null) {
                                                        NativeAdsLoaded.languageNativeAds?.destroy()
                                                    }
                                                    NativeAdsLoaded.languageNativeAds = nativeAd
                                                    NativeAdsLoaded.isLanMutableLiveData.value = nativeAd != null
                                                    Log.e("Splash_ads---", "lange_native_ads_showBigLangNative")
                                                }
                                            } else {
                                                Log.e("Splash_ads---", "lange_native_ads_Small")
                                                NativeAdsLoaded.issplashLangLoading = true
                                                NativeAdsLoaded.fetchGoogleNativeAds(
                                                    this@SplashActivity, Ads_Utils.GNB_LANGUAGE
                                                ) { nativeAd ->
                                                    if (NativeAdsLoaded.languageNativeAds != null) {
                                                        NativeAdsLoaded.languageNativeAds?.destroy()
                                                    }
                                                    Log.e("Splash_ads---", "lange_native_ads_call_spl")
                                                    NativeAdsLoaded.languageNativeAds = nativeAd
                                                    NativeAdsLoaded.isLanMutableLiveData.value =
                                                        nativeAd != null
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (!Glob.getInstance().GetBoolean(this@SplashActivity, "chke_lang_open")) {
                                InterstitialAds.LoadgoogleInterstitial(this)
                            }

                        }
                    }
                }
            },
            { requestConsentError ->
                Log.w("ggggg", "${requestConsentError.errorCode}: ${requestConsentError.message}")
                initializeMobileAdsSdk()
            }
        )
    }

    private fun initializeMobileAdsSdk() {
        Log.d("ggggg-----", "eeeeeeeee")

        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            Log.d("ggggg-----", "fffffff")
            startTimer(milliSec)
            return
        }

        Log.d("ggggg-----", "ggggggggg")
        MobileAds.initialize(this)
        startTimer(milliSec)
    }


    override fun onPause() {
        super.onPause()
        Log.e("Contect_Event--", "Splash_Act_onPause")
        if (remoteConfigData?.syatemUpdateDialog != null) {
            if (remoteConfigData?.syatemUpdateDialog!!.isShowing) {
                remoteConfigData?.syatemUpdateDialog!!.dismiss()
            }
        }


        Ads_Utils.ispauseResume = true
        if (Handler != null && Runnable != null) {
            Handler?.removeCallbacks(Runnable!!)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("Contect_Event--", "Splash_Act_onResume")
        if (Ads_Utils.ispauseResume) {
            Ads_Utils.ispauseResume = false
            if (Ads_Utils.check_Loaded_ads_ID) {
                Log.e(LOG_TAG, "startTime_nn")
                if (Handler != null && Runnable != null) {
                    Handler?.postDelayed(Runnable!!, 3000)
                } else {
                    Log.e(LOG_TAG, "startTime_11")
//                    startTimer(milliSec)
                    Log.d("ggggg-----", "reume 111")
                    if (!::consentInformation.isInitialized || !consentInformation.isConsentFormAvailable) {
                        Log.d("ggggg-----", "reume 222")
                        if (!requestConsentForm_Called) {
                            consentDialog()
                            Log.d("ggggg-----", "reume 333")
                        }
                    }
                }
            } else {
//                startTimer(milliSec)
                Log.d("ggggg-----", "reume 111")
                if (!::consentInformation.isInitialized || !consentInformation.isConsentFormAvailable) {
                    Log.d("ggggg-----", "reume 222")
                    if (!requestConsentForm_Called) {
                        consentDialog()
                        Log.d("ggggg-----", "reume 333")
                    }
                }

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Contect_Event--", "Splash_Act_onDestroy")
    }

    companion object {
        private const val LOG_TAG = "Contect_Event--"
        var rateus_onetime: Boolean = false
        var requestConsentForm_Called: Boolean = false


        var isNetwork = false
    }
}

