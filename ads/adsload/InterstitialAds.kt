package com.phonecontactscall.contectapp.phonedialerr.ads.adsload

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils.inter_firstTime
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAds {

    private const val TAG = "InterstitialAds"

    var  interstitialAd : InterstitialAd? = null
    var AdsShow_original = 0
    var inter_ads_Error = false
    var IntervalTime_show_ads = false
    private var adsClick = 0

    private fun getAdRequest(): AdRequest {
        val extras = Bundle()
        extras.putString("maxContentRatingAd", Ads_Utils.maxContentRatingAd)
        return AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .build()
    }

    fun LoadgoogleInterstitial(activity: Activity) {

        Ads_Utils.checkSplashInterCall = true
        Log.e(TAG, "request_to_gInter_load")

        if (AdsShow_original == Ads_Utils.googleMaxInterShow) {
            Log.d(TAG, "return init")
            return
        }

        Log.d(TAG, "init")

        val loadCallback = object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                inter_ads_Error = false
                Log.e(TAG, "${TAG}_loaded")
                this@InterstitialAds.interstitialAd = interstitialAd

                interstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            startTimer()
                            Log.e(TAG, "${TAG}_dismissedFull")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            this@InterstitialAds.interstitialAd = null
                            InterstitialAds.inter_ads_Error = true
                            Log.e(TAG, "${TAG}_failToShowFull$adError")
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.e(TAG, "${TAG}_AdShowedFull")
                            inter_firstTime = true
                            AdsShow_original++
//                            LoadgoogleInterstitial(activity)
                        }
                    }

            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
                inter_ads_Error = true
                Log.e(TAG, "${TAG}_AdFailed$error")
            }
        }

        val request = getAdRequest()
        InterstitialAd.load(
            activity,
            Ads_Utils.GOOGLE_INTERSTITIAL,
            request,
            loadCallback
        )
    }

    fun Show_google_Interstitialads(activity: Activity, from: String) {
        Log.d(TAG, "req")
        Log.e(TAG, "originalAdsShow$AdsShow_original")
        Log.e(TAG, "googleInterMaxInterAdsShow_${Ads_Utils.googleMaxInterShow}")
        if (AdsShow_original == Ads_Utils.googleMaxInterShow) return

        if (!inter_firstTime && !IntervalTime_show_ads) {
            Log.e(TAG, "${TAG}_req_$from")
            if (interstitialAd != null) {
                interstitialAd?.show(activity)
                Log.e(TAG, "${TAG}_Show_$from")
                Log.e(TAG, "${TAG}_Total_Show")
            } else {
                Log.e(TAG, "${TAG}_First_ex_show")
                if (Ads_Utils.Check_Network_Connected(activity)) {
                    if (inter_ads_Error) {
                        Log.e(TAG, "${TAG}_show_$from")
//                        LoadgoogleInterstitial(activity)
                    }
                }
            }
        } else {
            adsClick++
            Log.d(TAG, "adsClick_$adsClick")
            if (interstitialAd != null) {

                if (adsShowOrNot()) {
                    Log.e(TAG, "${TAG}_Show_else_$from")
                    interstitialAd?.show(activity)
                }
            } else {
                if (adsShowOrNot()) {
                    Log.e(TAG, "${TAG}_req_ex_show_$from")
                }
                if (Ads_Utils.Check_Network_Connected(activity)) {
                    if (inter_ads_Error) {
                        Log.e(TAG, "${TAG}_req_second_load_$from")
//                        LoadgoogleInterstitial(activity)
                    }
                }
            }
        }
    }

    private fun adsShowOrNot(): Boolean {
        return if (!IntervalTime_show_ads && adsClick > Ads_Utils.InterGapBetweenAds && AdsShow_original != Ads_Utils.googleMaxInterShow) {
            adsClick = 0
            Log.d(TAG, "${TAG}_adsShowOrNot: true")
            true
        } else {
            Log.d(TAG, "${TAG}__adsShowOrNot_false")
            false
        }
    }

    fun startTimer() {
        Log.d(TAG, "startTimer")
        IntervalTime_show_ads = true
        object : CountDownTimer(Ads_Utils.interCountDown, 1000) {
            override fun onTick(milliSec: Long) {
                Log.d(TAG, "timer_${(milliSec / 1000)}")
            }

            override fun onFinish() {
                Log.d(TAG, "timerStop")
                IntervalTime_show_ads = false
            }
        }.start()
    }

}