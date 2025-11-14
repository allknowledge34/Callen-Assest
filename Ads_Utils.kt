package com.phonecontactscall.contectapp.phonedialerr

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.phonecontactscall.contectapp.phonedialerr.ads.model.moredata.MoreApp

object Ads_Utils {


    var GOOGLE_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"


    var GNB_CONTECT_DETAILS = "ca-app-pub-3940256099942544/2247696110"
    var GNB_SEARCH_DETAILS = "ca-app-pub-3940256099942544/2247696110"
    var GNB_FAV_DETAILS = "ca-app-pub-3940256099942544/2247696110"
    var GNB_RECENT_DETAILS = "ca-app-pub-3940256099942544/2247696110"
    var GNB_BLOCK = "ca-app-pub-3940256099942544/2247696110"
    var GNB_HISTORY = "ca-app-pub-3940256099942544/2247696110"

    var GOOGLE_BANNER_MAIN = "ca-app-pub-3940256099942544/6300978111"

    var GN_LANGUAGE = "ca-app-pub-3940256099942544/2247696110"
    var GNB_LANGUAGE = "ca-app-pub-3940256099942544/2247696110"

    var GN_EXIT_DIALOG = "ca-app-pub-3940256099942544/2247696110"




    var showGoogleNativeBlock = "yes"
    var showGoogleNativeRecent = "yes"
    var showGoogleNativeFav = "yes"
    var showGoogleNativeCallHistory = "yes"
    var showGoogleNativeContectDetail = "yes"
    var showGoogleNativeSearchDetail = "yes"
    var showGoogleBannerMain = "yes"
    var showBigLangNative = "no"
    var showLangNativeAd = "yes"
    var showExitAd = "yes"


    var Update_Dialog = ""
    var playstore_link = ""
    var maxContentRatingAd = "PG"

    var checkSplashInterCall = false



    var googleMaxInterShow = 1
    var InterGapBetweenAds = 2
    var interCountDown: Long = 10000




    var showMoreLang = "no"
    var onlyLangMore = "no"
    var inter_firstTime = false

    var showMoreNative = "yes"
    var showMoreNativeBanner = "yes"
    var showMoreBanner = "yes"


    var onlyMoreNativeExit = "no"
    var onlyMoreNativeBanner = "no"
    var onlyMoreAppBannerMain = "no"


    var more_Url = ""
    var more_ACName = ""

    var more_List_data = arrayListOf<MoreApp>()
    var adCount = -1

    var check_Loaded_ads_ID = false
    var check_Splash_Showed = false
    var ispauseResume = false
    var Playstorelink1 = "market://details?id=";
    var Playstorelink2 = "http://play.google.com/store/apps/details?id=";


    fun Check_Network_Connected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}