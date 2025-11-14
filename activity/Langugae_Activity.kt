package com.phonecontactscall.contectapp.phonedialerr.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Model.Langugae_model
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.SharePrefLang
import com.phonecontactscall.contectapp.phonedialerr.adapter.Langugae_Adapter
import com.phonecontactscall.contectapp.phonedialerr.ads.adsload.NativeAdsLoaded
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils.showBigLangNative
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityLangugaeBinding

import com.bumptech.glide.Glide
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class Langugae_Activity : BaseActivity<ActivityLangugaeBinding>() {

    lateinit var img_done: ImageView
    lateinit var img_back: ImageView
     lateinit var sharePrefLang: SharePrefLang

    lateinit var rev_langauge: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var langugaeAdapter: Langugae_Adapter
    var langlist = ArrayList<Langugae_model>()
    var fromsplash: Boolean = false

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private var mHeight = 0
    lateinit var lunselec: String
    var z: Boolean = false

    private var constrain_adview: ConstraintLayout? = null

    private var rel_small_native_banner: RelativeLayout? = null
    private var txt_ads_view: TextView? = null
    private var frm_small_n_banner: FrameLayout? = null

    private var rel_native_big: RelativeLayout? = null
    private var frame_text_space: FrameLayout? = null
    private var space_view: Space? = null
    private var txt_big_view: TextView? = null
    private var fram_big_native: FrameLayout? = null


    companion object {



        var LanugaeActivity: Activity?=null
        private const val LOG_TAG = "Contect_Event--"
        private const val TAG = "Lang_Act"


    }
    override fun getViewBinding(): ActivityLangugaeBinding {

        return ActivityLangugaeBinding.inflate(layoutInflater)
    }

    override fun initView() {

        val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color,this)
        val color1 = Glob.getResourceFromAttr(R.attr.navigation_bg_color,this)
        window.statusBarColor = color
        window.navigationBarColor = color1

        Glob.SetStatusbarColor(window)

        LanugaeActivity = this

        sharedPreferences = getSharedPreferences("lang_screen_prefrence", Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        sharePrefLang = SharePrefLang.getInstance(this)

        img_done = findViewById(R.id.img_done)
        img_back = findViewById(R.id.img_back)
        rev_langauge = findViewById(R.id.rev_langauge)

        fromsplash = intent.getBooleanExtra("first_splash_lang", false)


        img_back.setOnClickListener(View.OnClickListener {

            onBackPressedDispatcher.onBackPressed()
        })

        // for ads

        constrain_adview = findViewById(R.id.constrain_adview)
        rel_small_native_banner = findViewById(R.id.rel_small_native_banner)
        txt_ads_view = findViewById(R.id.txt_ads_view)
        frm_small_n_banner = findViewById(R.id.frm_small_n_banner)
        rel_native_big = findViewById(R.id.rel_native_big)
        frame_text_space = findViewById(R.id.frame_text_space)
        space_view = findViewById(R.id.space_view)
        txt_big_view = findViewById(R.id.txt_big_view)
        fram_big_native = findViewById(R.id.fram_big_native)

        if (fromsplash) {
            constrain_adview?.visibility = View.VISIBLE
            img_back.visibility = View.GONE
            val params = binding.txtTitle.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(45, 0, 15, 0)
            binding.txtTitle.layoutParams = params

        }else{
            img_back.visibility = View.VISIBLE
            constrain_adview?.visibility = View.GONE
            val params =binding.txtTitle.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(15, 0, 15, 0)
            binding.txtTitle.layoutParams = params
        }


        showAdView()


        langlist.add(Langugae_model(R.drawable.ic_english, getString(R.string.str_english), "en", "English"))
        langlist.add(Langugae_model(R.drawable.ic_portugal, getString(R.string.str_port), "pt", "Português"))
        langlist.add(Langugae_model(R.drawable.ic_france, getString(R.string.str_fra), "fr", "Français"))
        langlist.add(Langugae_model(R.drawable.ic_egypt, getString(R.string.str_arb), "ar", "عربي"))
        langlist.add(Langugae_model(R.drawable.ic_russia, getString(R.string.str_rus), "ru", "Русский"))
        langlist.add(Langugae_model(R.drawable.ic_china, getString(R.string.str_chi), "zh", "中国人"))
        langlist.add(Langugae_model(R.drawable.ic_spain, getString(R.string.str_spa), "es", "España"))
        langlist.add(Langugae_model(R.drawable.ic_japanese, getString(R.string.str_jp), "ja", "日本語"))
        langlist.add(Langugae_model(R.drawable.ic_pakistan, getString(R.string.str_urdu), "ur", "اردو"))
        langlist.add(Langugae_model(R.drawable.ic_bangladesh, getString(R.string.str_ban), "bn", "বাংলা"))
        langlist.add(Langugae_model(R.drawable.ic_germany, getString(R.string.str_ger), "de", "Deutsch"))
        langlist.add(Langugae_model(R.drawable.ic_india, getString(R.string.str_hindi), "hi", "हिन्दी"))


        lunselec = Glob.getLanValue(this, "en")



        linearLayoutManager = LinearLayoutManager(this)
        rev_langauge.layoutManager = linearLayoutManager
        langugaeAdapter = Langugae_Adapter(this, lunselec, langlist) { mLanguageModel ->
            lunselec = mLanguageModel.code
        }

        rev_langauge.adapter = langugaeAdapter


        img_done.setOnClickListener(View.OnClickListener {
            Glob.LansetValue(this, lunselec);
            Glob.getInstance().SetBoolean(this, "chke_lang_open",true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!Glob.isDefaultDialer(this)) {
                    val intent = Intent(this, DefaultCall_Activity::class.java)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                        finish()
                    } else {

                    }
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                        finish()
                    } else {

                    }
                }
            } else {
                val intent = Intent(this, MainActivity::class.java)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                    finish()
                } else {

                }
            }



        })


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "Lang_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("Lang_Act_onBackpress", Bundle())
                img_done.alpha = 1f

                if (!fromsplash) {

                    finish()
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    if (!Glob.isDefaultDialer(this@Langugae_Activity)) {
                        val intent = Intent(this@Langugae_Activity, DefaultCall_Activity::class.java)
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                            finish()
                        } else {

                        }

                    } else {
                        val intent = Intent(this@Langugae_Activity, MainActivity::class.java)
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                            finish()
                        } else {

                        }
                    }
                } else {
                    val intent = Intent(this@Langugae_Activity, MainActivity::class.java)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                        finish()
                    } else {

                    }
                }

            }

        })


        Log.e("Contect_Event--", "Lang_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Lang_Act_onCreate", Bundle())

    }






    private fun showAdView() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        mHeight = displayMetrics.heightPixels
        val params = space_view?.layoutParams
        params?.height = mHeight / 5
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        space_view?.layoutParams = params

        if (Ads_Utils.showLangNativeAd == "yes") {
            if (Ads_Utils.onlyLangMore == "yes") {
                if (showBigLangNative == "yes") {
                    rel_native_big?.visibility = View.VISIBLE
                    txt_big_view?.visibility = View.VISIBLE
                    val params1 = space_view?.layoutParams
                    if (mHeight / 5 > 300) {
                        params1?.height = mHeight / 5
                    } else {
                        params1?.height = 300
                    }
                    params1?.width = ViewGroup.LayoutParams.MATCH_PARENT
                    space_view?.layoutParams = params1
                    if (Ads_Utils.more_List_data.size > 0) {
                        fetchMoreAppNativeAd(
                            activity = this,
                            frameLayout = fram_big_native!!
                        )
                    }
                } else {
                    rel_small_native_banner?.visibility = View.VISIBLE
                    txt_ads_view?.visibility = View.VISIBLE
                    if (Ads_Utils.more_List_data.size > 0) {
                        loadMoreAppNativeBannerAd(
                            activity = this,
                            frameLayout = frm_small_n_banner!!
                        )
                    }
                }
            } else {
                NativeAdsLoaded.isLanMutableLiveData.observe(this) { loadedFromSplash ->
                    if (showBigLangNative == "yes") {
                        rel_native_big?.visibility = View.VISIBLE
                        txt_big_view?.visibility = View.VISIBLE
                        loadedFromSplash?.let {
                            if (it) {
                                NativeAdsLoaded.languageNativeAds?.let {
                                    DisplayNativeBanner(activity = this, frameLayout = fram_big_native!!, tvAdText = txt_big_view!!)
                                }
                            } else {
                                gg_NativeAd(
                                    activity = this,
                                    adID = Ads_Utils.GN_LANGUAGE,
                                    tvAdText = txt_big_view!!,
                                    frameLayout = fram_big_native!!
                                )
                            }
                        } ?: run {
                            if (!NativeAdsLoaded.isLangLoading && !NativeAdsLoaded.issplashLangLoading) {
                                NativeAdsLoaded.isLangLoading = true
                                gg_NativeAd(activity = this, adID = Ads_Utils.GN_LANGUAGE, tvAdText = txt_big_view!!, frameLayout = fram_big_native!!)
                            }
                        }
                    } else {
                        rel_small_native_banner?.visibility = View.VISIBLE
                        txt_ads_view?.visibility = View.VISIBLE
                        loadedFromSplash?.let {
                            if (it) {
                                NativeAdsLoaded.languageNativeAds?.let {
                                    DisplayNativeBanner(
                                        activity = this,
                                        frameLayout = frm_small_n_banner!!,
                                        tvAdText = txt_ads_view!!
                                    )
                                }
                            } else {
                                Native_Banner_Goggle_Ad(
                                    activity = this,
                                    adID = Ads_Utils.GNB_LANGUAGE,
                                    tvAdText = txt_ads_view!!,
                                    frameLayout = frm_small_n_banner!!
                                )
                            }
                        } ?: run {
                            if (!NativeAdsLoaded.isLangLoading && !NativeAdsLoaded.issplashLangLoading) {
                                NativeAdsLoaded.isLangLoading = true
                                Native_Banner_Goggle_Ad(
                                    activity = this,
                                    adID = Ads_Utils.GNB_LANGUAGE,
                                    tvAdText = txt_ads_view!!,
                                    frameLayout = frm_small_n_banner!!
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun DisplayNativeBanner(
        activity: Activity,
        frameLayout: FrameLayout,
        tvAdText: TextView
    ) {

        tvAdText.visibility = View.GONE

        val adView = if (showBigLangNative == "yes") {
            activity.layoutInflater.inflate(
                R.layout.google_native_banner_ad,
                activity.findViewById(R.id.nativeAd),
                false
            ) as NativeAdView
        } else {
            activity.layoutInflater.inflate(
                R.layout.google_native_lang_ads,
                activity.findViewById(R.id.nativeAd),
                false
            ) as NativeAdView
        }

        NativeAdsLoaded.languageNativeAds?.let {
            populateAppInstallAdView(it, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }
    }

    private fun populateAppInstallAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {

        adView.iconView = adView.findViewById(R.id.adIcon)
        adView.headlineView = adView.findViewById(R.id.adName)
        adView.bodyView = adView.findViewById(R.id.adBody)

        if (showBigLangNative == "yes") {
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
        }

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

    private fun getAddRequest(): AdRequest {
        val extras = Bundle()
        extras.putString("maxContentRatingAd", Ads_Utils.maxContentRatingAd)
        return AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
    }

    private fun gg_NativeAd(
        activity: Activity,
        adID: String,
        tvAdText: TextView,
        frameLayout: FrameLayout
    ) {

        val builder = AdLoader.Builder(activity, adID).forNativeAd { nativeAd ->
            Log.e("Splash_ads---", "GN_onAdLoaded")
            NativeAdsLoaded.languageNativeAds = nativeAd
            tvAdText.visibility = View.GONE
            DisplayNativeBanner(activity, frameLayout, tvAdText)
        }

        val adLoader = builder.withAdListener(object : AdListener() {

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("Splash_ads---", "GN_onAdFailedToLoad"+loadAdError.code)
                NativeAdsLoaded.languageNativeAds = null

                if (Ads_Utils.showMoreLang == "yes") {
                    if (Ads_Utils.more_List_data.size > 0) {
                        if (!activity.isFinishing) {
                            fetchMoreAppNativeAd(activity, frameLayout)
                        }
                    } else {
                        tvAdText.visibility = View.VISIBLE
                    }
                } else {
                    tvAdText.visibility = View.VISIBLE
                }
            }

            override fun onAdLoaded() {

                tvAdText.visibility = View.GONE
            }

            override fun onAdClicked() {

                Log.e("Splash_ads---", "GN_onAdClicked")

                NativeAdsLoaded.languageNativeAds = null
            }
        }).build()

        val request = getAddRequest()
        adLoader.loadAd(request)
    }

    private fun Native_Banner_Goggle_Ad(
        activity: Activity,
        adID: String,
        tvAdText: TextView,
        frameLayout: FrameLayout
    ) {
        val builder = AdLoader.Builder(activity, adID).forNativeAd { nativeAd ->
            Log.e("Splash_ads---", "GNBads_onAdLoaded")
            NativeAdsLoaded.languageNativeAds = nativeAd
            tvAdText.visibility = View.GONE
            DisplayNativeBanner(activity, frameLayout, tvAdText)
        }

        val adLoader = builder.withAdListener(object : AdListener() {

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("Splash_ads---", "GNBads_onAdFailedToLoad"+loadAdError.code)
                NativeAdsLoaded.languageNativeAds = null
                if (Ads_Utils.showMoreLang == "yes") {
                    if (Ads_Utils.more_List_data.size > 0) {
                        if (!activity.isFinishing) {
                            loadMoreAppNativeBannerAd(activity, frameLayout)
                        }
                    } else {
                        tvAdText.visibility = View.VISIBLE
                    }
                } else {
                    tvAdText.visibility = View.VISIBLE
                }
            }

            override fun onAdLoaded() {
                tvAdText.visibility = View.GONE
                Log.e("Splash_ads---", "GNBads_onAdLoaded")
            }

            override fun onAdClicked() {
                Log.e("Splash_ads---", "GNBads_onAdClicked")

                NativeAdsLoaded.languageNativeAds = null
            }
        }).build()

        val request = getAddRequest()
        adLoader.loadAd(request)
    }

    private fun fetchMoreAppNativeAd(
        activity: Activity,
        frameLayout: FrameLayout
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
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_ads_call_action_more.setOnClickListener {
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

    }

    private fun loadMoreAppNativeBannerAd(
        activity: Activity,
        frameLayout: FrameLayout
    ) {
        val view = activity.layoutInflater.inflate(
            R.layout.google_native_lang_ads_more,
            activity.findViewById(R.id.nativeAd), false
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
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_name_moreas.setOnClickListener {
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_adsbodymore.setOnClickListener {
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_ads_call_action_more.setOnClickListener {
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

    }


    private fun DisplayAdClick(activity: Activity, link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {

        }
    }

}