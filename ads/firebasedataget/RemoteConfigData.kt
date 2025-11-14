package com.phonecontactscall.contectapp.phonedialerr.ads.firebasedataget

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.ads.api.moredata.serverMoreData
import com.phonecontactscall.contectapp.phonedialerr.ads.dialog.ContectAppUpdateDialog
import com.phonecontactscall.contectapp.phonedialerr.ads.model.moredata.DataMoreApi
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class RemoteConfigData(private val activity: Activity) {

    private var mfirebaseremoteconfig: FirebaseRemoteConfig? = null
    private var dataloadseconds = 0
    private var datasecondsMoreAds = 0
    private var check_Running = false
    private var dataads_Handler = Handler(Looper.getMainLooper())
    private var dataads_Runnable = Runnable {

    }

    var syatemUpdateDialog: ContectAppUpdateDialog? = null

    init {
        Log.e("Eveninge_Data", "remort_data_loadstart")
        MyApplication.mFirebaseAnalytics?.logEvent("remort_data_loadstart", Bundle())

        check_Running = true
        Data_Get_startTimer()

        mfirebaseremoteconfig = FirebaseRemoteConfig.getInstance()
        val firebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        mfirebaseremoteconfig?.setConfigSettingsAsync(firebaseRemoteConfigSettings)
        mfirebaseremoteconfig?.fetchAndActivate()?.addOnCompleteListener {
            if (it.isSuccessful) {

                Ads_Utils.check_Loaded_ads_ID = true

                val updated = it.result

                Log.e("Eveninge_Data", "remort_data_success")
                MyApplication.mFirebaseAnalytics?.logEvent("remort_data_success", Bundle())

                Log.e("Eveninge_Data", "remort_data_successtime$dataloadseconds")
                MyApplication.mFirebaseAnalytics?.logEvent("remort_data_successtime$dataloadseconds", Bundle())

                Log.e("Eveninge_Data", "config_params_updated$updated")
                MyApplication.mFirebaseAnalytics?.logEvent("remort_data_updated$updated", Bundle())


                val googleInterstitial = mfirebaseremoteconfig?.getString("GOOGLE_INTERSTITIAL")


                val GNB_BLOCK = mfirebaseremoteconfig?.getString("GNB_BLOCK")
                val GNB_CONTECT_DETAILS = mfirebaseremoteconfig?.getString("GNB_CONTECT_DETAILS")
                val GNB_SEARCH_DETAILS = mfirebaseremoteconfig?.getString("GNB_SEARCH_DETAILS")
                val GNB_FAV_DETAILS = mfirebaseremoteconfig?.getString("GNB_FAV_DETAILS")
                val GNB_RECENT_DETAILS = mfirebaseremoteconfig?.getString("GNB_RECENT_DETAILS")
                val GNB_HISTORY = mfirebaseremoteconfig?.getString("GNB_HISTORY")
                val GOOGLE_BANNER_MAIN = mfirebaseremoteconfig?.getString("GOOGLE_BANNER_MAIN")
                val GN_EXIT_DIALOG = mfirebaseremoteconfig?.getString("GN_EXIT_DIALOG")
                val showGoogleNativeContectDetail = mfirebaseremoteconfig?.getString("showGoogleNativeContectDetail")
                val showGoogleNativeSearchDetail = mfirebaseremoteconfig?.getString("showGoogleNativeSearchDetail")
                val showGoogleNativeRecent = mfirebaseremoteconfig?.getString("showGoogleNativeRecent")
                val showGoogleNativeBlock = mfirebaseremoteconfig?.getString("showGoogleNativeBlock")
                val showGoogleNativeFav = mfirebaseremoteconfig?.getString("showGoogleNativeFav")
                val showGoogleNativeCallHistory = mfirebaseremoteconfig?.getString("showGoogleNativeCallHistory")
                val showGoogleBannerMain = mfirebaseremoteconfig?.getString("showGoogleBannerMain")
                val Update_Dialog = mfirebaseremoteconfig?.getString("Update_Dialog")
                val playstore_link = mfirebaseremoteconfig?.getString("playstore_link")
                val maxContentRatingAd = mfirebaseremoteconfig?.getString("maxContentRatingAd")
                val showBigLangNative = mfirebaseremoteconfig?.getString("showBigLangNative")
                val showLangNativeAd = mfirebaseremoteconfig?.getString("showLangNativeAd")
                val googleMaxInterShow = mfirebaseremoteconfig?.getString("googleMaxInterShow")
                val InterGapBetweenAds = mfirebaseremoteconfig?.getString("InterGapBetweenAds")
                val interCountDown = mfirebaseremoteconfig?.getString("interCountDown")
                val inter_firstTime = mfirebaseremoteconfig?.getString("inter_firstTime")
                val showMoreLang = mfirebaseremoteconfig?.getString("showMoreLang")
                val onlyLangMore = mfirebaseremoteconfig?.getString("onlyLangMore")
                val showMoreNative = mfirebaseremoteconfig?.getString("showMoreNative")
                val showMoreNativeBanner = mfirebaseremoteconfig?.getString("showMoreNativeBanner")
                val showMoreBanner = mfirebaseremoteconfig?.getString("showMoreBanner")
                val onlyMoreNativeExit = mfirebaseremoteconfig?.getString("onlyMoreNativeExit")
                val onlyMoreNativeBanner = mfirebaseremoteconfig?.getString("onlyMoreNativeBanner")
                val onlyMoreAppBannerMain = mfirebaseremoteconfig?.getString("onlyMoreAppBannerMain")
                val more_Url = mfirebaseremoteconfig?.getString("more_Url")
                val more_ACName = mfirebaseremoteconfig?.getString("more_ACName")
                val showExitAd = mfirebaseremoteconfig?.getString("showExitAd")


                if (!googleInterstitial.isNullOrEmpty()) {
                    Ads_Utils.GOOGLE_INTERSTITIAL = googleInterstitial
                }
                if (!GNB_BLOCK.isNullOrEmpty()) {
                    Ads_Utils.GNB_BLOCK = GNB_BLOCK
                }
                if (!GNB_CONTECT_DETAILS.isNullOrEmpty()) {
                    Ads_Utils.GNB_CONTECT_DETAILS = GNB_CONTECT_DETAILS
                }
                if (!GNB_SEARCH_DETAILS.isNullOrEmpty()) {
                    Ads_Utils.GNB_SEARCH_DETAILS = GNB_SEARCH_DETAILS
                }
                if (!GNB_FAV_DETAILS.isNullOrEmpty()) {
                    Ads_Utils.GNB_FAV_DETAILS = GNB_FAV_DETAILS
                }
                if (!GNB_RECENT_DETAILS.isNullOrEmpty()) {
                    Ads_Utils.GNB_RECENT_DETAILS = GNB_RECENT_DETAILS
                }
                if (!GNB_HISTORY.isNullOrEmpty()) {
                    Ads_Utils.GNB_HISTORY = GNB_HISTORY
                }
                if (!GOOGLE_BANNER_MAIN.isNullOrEmpty()) {
                    Ads_Utils.GOOGLE_BANNER_MAIN = GOOGLE_BANNER_MAIN
                }

                if (!GN_EXIT_DIALOG.isNullOrEmpty()) {
                    Ads_Utils.GN_EXIT_DIALOG = GN_EXIT_DIALOG
                }

                if (!showGoogleNativeRecent.isNullOrEmpty()) {
                    Ads_Utils.showGoogleNativeRecent = showGoogleNativeRecent
                }
                if (!showGoogleNativeContectDetail.isNullOrEmpty()) {
                    Ads_Utils.showGoogleNativeContectDetail = showGoogleNativeContectDetail
                }

                if (!showGoogleNativeSearchDetail.isNullOrEmpty()) {
                    Ads_Utils.showGoogleNativeSearchDetail = showGoogleNativeSearchDetail
                }

                if (!showGoogleNativeBlock.isNullOrEmpty()) {
                    Ads_Utils.showGoogleNativeBlock = showGoogleNativeBlock
                }
                if (!showGoogleNativeFav.isNullOrEmpty()) {
                    Ads_Utils.showGoogleNativeFav = showGoogleNativeFav
                }
                if (!showGoogleNativeCallHistory.isNullOrEmpty()) {
                    Ads_Utils.showGoogleNativeCallHistory = showGoogleNativeCallHistory
                }

                if (!showGoogleBannerMain.isNullOrEmpty()) {
                    Ads_Utils.showGoogleBannerMain = showGoogleBannerMain
                }

                if (!Update_Dialog.isNullOrEmpty()) {
                    Ads_Utils.Update_Dialog = Update_Dialog
                }

                if (!playstore_link.isNullOrEmpty()) {
                    Ads_Utils.playstore_link = playstore_link
                }
                if (!maxContentRatingAd.isNullOrEmpty()) {
                    Ads_Utils.maxContentRatingAd = maxContentRatingAd
                }
                if (!showBigLangNative.isNullOrEmpty()) {
                    Ads_Utils.showBigLangNative = showBigLangNative
                }

                if (!showLangNativeAd.isNullOrEmpty()) {
                    Ads_Utils.showLangNativeAd = showLangNativeAd
                }

                if (!googleMaxInterShow.isNullOrEmpty()) {
                    Ads_Utils.googleMaxInterShow = googleMaxInterShow.toInt()
                }

                if (!InterGapBetweenAds.isNullOrEmpty()) {
                    Ads_Utils.InterGapBetweenAds = InterGapBetweenAds.toInt()
                }

                if (!interCountDown.isNullOrEmpty()) {
                    Ads_Utils.interCountDown = interCountDown.toLong()
                }

                if (!inter_firstTime.isNullOrEmpty()) {
                    Ads_Utils.inter_firstTime = inter_firstTime.toBoolean()
                }

                if (!showMoreLang.isNullOrEmpty()) {
                    Ads_Utils.showMoreLang = showMoreLang
                }

                if (!onlyLangMore.isNullOrEmpty()) {
                    Ads_Utils.onlyLangMore = onlyLangMore
                }

                if (!showMoreNative.isNullOrEmpty()) {
                    Ads_Utils.showMoreNative = showMoreNative
                }

                if (!showMoreNativeBanner.isNullOrEmpty()) {
                    Ads_Utils.showMoreNativeBanner = showMoreNativeBanner
                }
                if (!showMoreBanner.isNullOrEmpty()) {
                    Ads_Utils.showMoreBanner = showMoreBanner
                }

                if (!onlyMoreNativeExit.isNullOrEmpty()) {
                    Ads_Utils.onlyMoreNativeExit = onlyMoreNativeExit
                }
                if (!onlyMoreNativeBanner.isNullOrEmpty()) {
                    Ads_Utils.onlyMoreNativeBanner = onlyMoreNativeBanner
                }

                if (!onlyMoreAppBannerMain.isNullOrEmpty()) {
                    Ads_Utils.onlyMoreAppBannerMain = onlyMoreAppBannerMain
                }

                if (!more_Url.isNullOrEmpty()) {
                    Ads_Utils.more_Url = more_Url
                }

                if (!more_ACName.isNullOrEmpty()) {
                    Ads_Utils.more_ACName = more_ACName
                }

                if (!showExitAd.isNullOrEmpty()) {
                    Ads_Utils.showExitAd = showExitAd
                }
                stopTimer()

//                MoreAppDataload()

                Log.e("Eveninge_Data", "GOOGLE_INTERSTITIAL: ${Ads_Utils.GOOGLE_INTERSTITIAL}")
                Log.e("Eveninge_Data", "GNB_BLOCK: ${Ads_Utils.GNB_BLOCK}")
                Log.e("Eveninge_Data", "GNB_CONTECT_DETAILS: ${Ads_Utils.GNB_CONTECT_DETAILS}")
                Log.e("Eveninge_Data", "GNB_SEARCH_DETAILS: ${Ads_Utils.GNB_SEARCH_DETAILS}")
                Log.e("Eveninge_Data", "GNB_HISTORY: ${Ads_Utils.GNB_HISTORY}")
                Log.e("Eveninge_Data", "GNB_FAV_DETAILS: ${Ads_Utils.GNB_FAV_DETAILS}")
                Log.e("Eveninge_Data", "GNB_RECENT_DETAILS: ${Ads_Utils.GNB_RECENT_DETAILS}")
                Log.e("Eveninge_Data", "GOOGLE_BANNER_MAIN: ${Ads_Utils.GOOGLE_BANNER_MAIN}")
                Log.e("Eveninge_Data", "GN_EXIT_DIALOG: ${Ads_Utils.GN_EXIT_DIALOG}")
                Log.e("Eveninge_Data", "showGoogleNativeFav: ${Ads_Utils.showGoogleNativeFav}")
                Log.e("Eveninge_Data", "showGoogleNativeContectDetail: ${Ads_Utils.showGoogleNativeContectDetail}")
                Log.e("Eveninge_Data", "showGoogleNativeSearchDetail: ${Ads_Utils.showGoogleNativeSearchDetail}")
                Log.e("Eveninge_Data", "showGoogleNativeCallHistory: ${Ads_Utils.showGoogleNativeCallHistory}")
                Log.e("Eveninge_Data", "showGoogleNativeBlock: ${Ads_Utils.showGoogleNativeBlock}")
                Log.e("Eveninge_Data", "showGoogleNativeRecent: ${Ads_Utils.showGoogleNativeRecent}")
                Log.e("Eveninge_Data", "showGoogleBannerMain: ${Ads_Utils.showGoogleBannerMain}")
                Log.e("Eveninge_Data", "showMoreNativeBanner: ${Ads_Utils.showMoreNativeBanner}")
                Log.e("Eveninge_Data", "showMoreNative: ${Ads_Utils.showMoreNative}")
                Log.e("Eveninge_Data", "Update_Dialog: ${Ads_Utils.Update_Dialog}")
                Log.e("Eveninge_Data", "playstore_link: ${Ads_Utils.playstore_link}")
                Log.e("Eveninge_Data", "maxContentRatingAd: ${Ads_Utils.maxContentRatingAd}")
                Log.e("Eveninge_Data", "showBigLangNative: ${Ads_Utils.showBigLangNative}")
                Log.e("Eveninge_Data", "showLangNativeAd: ${Ads_Utils.showLangNativeAd}")
                Log.e("Eveninge_Data", "googleMaxInterShow: ${Ads_Utils.googleMaxInterShow}")
                Log.e("Eveninge_Data", "InterGapBetweenAds: ${Ads_Utils.InterGapBetweenAds}")
                Log.e("Eveninge_Data", "interCountDown: ${Ads_Utils.interCountDown}")
                Log.e("Eveninge_Data", "firstTime: ${Ads_Utils.inter_firstTime}")
                Log.e("Eveninge_Data", "showMoreLang: ${Ads_Utils.showMoreLang}")
                Log.e("Eveninge_Data", "onlyLangMore: ${Ads_Utils.onlyLangMore}")
                Log.e("Eveninge_Data", "showMoreNative: ${Ads_Utils.showMoreNative}")
                Log.e("Eveninge_Data", "showMoreBanner: ${Ads_Utils.showMoreBanner}")
                Log.e("Eveninge_Data", "onlyMoreNativeBanner: ${Ads_Utils.onlyMoreNativeBanner}")
                Log.e("Eveninge_Data", "onlyMoreNativeExit: ${Ads_Utils.onlyMoreNativeExit}")
                Log.e("Eveninge_Data", "onlyMoreAppBannerMain: ${Ads_Utils.onlyMoreAppBannerMain}")
                Log.e("Eveninge_Data", "more_Url: ${Ads_Utils.more_Url}")
                Log.e("Eveninge_Data", "more_ACName: ${Ads_Utils.more_ACName}")
                Log.e("Eveninge_Data", "showExitAd: ${Ads_Utils.showExitAd}")
                /*************************************** Logging End  *******************************************/

                if (Update_Dialog == "yes") {
                    if (!activity.isFinishing &&  !Ads_Utils.check_Splash_Showed) {
                        syatemUpdateDialog = ContectAppUpdateDialog(
                            activity = activity
                        )
                        syatemUpdateDialog?.show()
                    }
                } else {

                }
            } else {
                stopTimer()

                Log.e("Eveninge_Data", "remort_data_failed")
                MyApplication.mFirebaseAnalytics?.logEvent("remort_data_failed", Bundle())

                Log.e("Eveninge_Data", "remort_data_failedtime$dataloadseconds")
                MyApplication.mFirebaseAnalytics?.logEvent(
                    "remort_data_failedtime$dataloadseconds",
                    Bundle()
                )
            }
        }
    }

    private fun MoreAppDataload() {
        startTimerMoreApp()

        Log.e("Eveninge_Data", "more_App_Request")
        MyApplication.mFirebaseAnalytics?.logEvent("more_App_Request", Bundle())

        try {
            val moreDataApi = serverMoreData.getMoreApiInterface(Ads_Utils.more_Url)

            Ads_Utils.more_List_data = ArrayList()
            val response = moreDataApi.FetchMoreList(Ads_Utils.more_ACName)
            response.enqueue(object : Callback<DataMoreApi> {
                override fun onResponse(
                    call: Call<DataMoreApi?>,
                    response: Response<DataMoreApi?>
                ) {
                    Log.e("Eveninge_Data", "loadMoreData_Success")
                    MyApplication.mFirebaseAnalytics?.logEvent("loadMoreData_Success", Bundle())

                    Log.e("Eveninge_Data", "loadMoreData_Success_time$datasecondsMoreAds")
                    MyApplication.mFirebaseAnalytics?.logEvent(
                        "loadMoreData_Success_time$datasecondsMoreAds",
                        Bundle()
                    )

                    MoreAppDatastopTime()
                    if (response.isSuccessful && response.body() != null) {
                        val moreApp = response.body()
                        Log.e("Eveninge_Data", "loadMoreData_Success")
                        if (moreApp != null) {
                            val moreAppList = moreApp.app_moreAppData
                            Log.e("Eveninge_Data", "moreAppList: ::: $moreAppList")
                            if (!moreAppList.isNullOrEmpty()) {
                                Ads_Utils.more_List_data.addAll(moreAppList)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<DataMoreApi>, t: Throwable) {
                    Log.e("Eveninge_Data", "loadMoreApp_onFailure")
                    MyApplication.mFirebaseAnalytics?.logEvent("loadMoreApp_onFailure", Bundle())

                    Log.e("Eveninge_Data", "loadMoreApp_onFailure_time$datasecondsMoreAds")
                    MyApplication.mFirebaseAnalytics?.logEvent(
                        "loadMoreApp_onFailure_time$datasecondsMoreAds",
                        Bundle()
                    )

                    MoreAppDatastopTime()
                    call.cancel()
                }
            })

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun Data_Get_startTimer() {
        dataloadseconds = 0

        dataads_Runnable = Runnable {
            val sec = dataloadseconds % 60
            val time = String.format(Locale.getDefault(), "%02d", sec)
            Log.e("Eveninge_Data", "remote_Time_$time")

            if (dataloadseconds == 120) {
                Log.e("Eveninge_Data", "Remort_120_TIMEOUT")
                MyApplication.mFirebaseAnalytics!!.logEvent(
                    "Remort_120_TIMEOUT$dataloadseconds",
                    Bundle()
                )

                stopTimer()
                return@Runnable
            }

            if (check_Running) {
                dataloadseconds++
            }
            dataads_Handler.postDelayed(dataads_Runnable, 1000)
        }

        dataads_Handler.post(dataads_Runnable)
    }

    private fun stopTimer() {
        if (dataads_Handler != null && dataads_Runnable != null) {
            check_Running = false
            dataads_Handler.removeCallbacks(dataads_Runnable)
        }
    }

    private fun startTimerMoreApp() {
        datasecondsMoreAds = 0
        check_Running = true
        dataads_Runnable = Runnable {
            val sec = datasecondsMoreAds % 60
            val time = String.format(Locale.getDefault(), "%02d", sec)
            Log.e("Eveninge_Data", "moreApp_Time_$time")

            if (datasecondsMoreAds == 120) {

                Log.e("Evening_Data", "moreapp_DATA_120_TIMEOUT")
                MyApplication.mFirebaseAnalytics!!.logEvent(
                    "moreapp_DATA_120_TIMEOUT$dataloadseconds",
                    Bundle()
                )

                MoreAppDatastopTime()

                return@Runnable
            }

            if (check_Running) {
                datasecondsMoreAds++
            }
            dataads_Handler.postDelayed(dataads_Runnable, 1000)
        }

        dataads_Handler.post(dataads_Runnable)
    }

    private fun MoreAppDatastopTime() {
        if (dataads_Handler != null && dataads_Runnable != null) {
            check_Running = false
            dataads_Handler.removeCallbacks(dataads_Runnable)
        }
    }

}