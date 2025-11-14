package com.phonecontactscall.contectapp.phonedialerr

import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

class MyApplication : Application() {

    companion object {
        private var instance: MyApplication? = null
        lateinit var mFirebaseAnalytics: FirebaseAnalytics
        lateinit var sharePrefLang: SharePrefLang

        @Synchronized
        fun getInstance(): MyApplication {
            return instance!!
        }



    }


    override fun onCreate() {
        super.onCreate()

        instance = this
        Glob.init(this)
        MobileAds.initialize(this);
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sharePrefLang = SharePrefLang.getInstance(this)

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
            })

        Clarity.setOnNewSessionStartedCallback {
            Clarity.getCurrentSessionId()?.let { url->
                val bundle = Bundle()
                bundle.putString("clarity_link",url)
                mFirebaseAnalytics.logEvent("main_session_started",bundle)
            }
        }

        initClarity()


    }
    private val  projectID:String  ="u0vm3e642a" // Project Id Add karvani
    fun initClarity(){
        val config = ClarityConfig(projectId = projectID, logLevel = LogLevel.None)
        Clarity.initialize(applicationContext, config)
    }


}