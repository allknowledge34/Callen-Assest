package com.phonecontactscall.contectapp.phonedialerr.ads.adsload

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

object NativeAdsLoaded {

    private const val TAG = "NativeAdLoaded"

    var isLangLoading = false
    var issplashLangLoading = false


    var exitNativeAds: NativeAd? = null
    var languageNativeAds: NativeAd? = null

    var isLanMutableLiveData : MutableLiveData<Boolean?> = MutableLiveData(null)


    private fun fetchAdRequest(): AdRequest {
        val extras = Bundle()
        extras.putString("maxContentRatingAd", Ads_Utils.maxContentRatingAd)
        return AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
    }

    fun fetchGoogleNativeAds(context: Context, adId: String, nativeAdReference: (NativeAd?) -> Unit) {
        val builder = AdLoader.Builder(context, adId).forNativeAd { nativeAd ->
            nativeAdReference(nativeAd)
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                nativeAdReference(null)
                Log.e(TAG, "loadGoogleNativeAd_failed" + loadAdError.code)
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.e(TAG, "loadGoogleNativeAd_loaded")
            }
            override fun onAdClicked() {

            }
        }).build()
        adLoader.loadAd(fetchAdRequest())
    }
}