package com.phonecontactscall.contectapp.phonedialerr.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.BlockedNumberContract
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Model.BlockContect
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.adapter.BlockAdapter
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityBlockContectBinding
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.bumptech.glide.Glide
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.Collections

class BlockContectActivity : BaseActivity<ActivityBlockContectBinding>() {

    private var blockNumberList = mutableListOf<BlockContect>()
    private lateinit var mBlockAdapter: BlockAdapter

    companion object{
        private const val LOG_TAG = "Contect_Event--"
        private const val TAG = "Block_Act"
        lateinit var relEmptyAll :LinearLayout
        lateinit var relData :RelativeLayout
        lateinit var constrain_adview: ConstraintLayout
        lateinit var blockContectActivity: BlockContectActivity
    }

    private var rel_native_banner: RelativeLayout? = null
    private var txt_ad_view: TextView? = null
    private var frame_native_banner: FrameLayout? = null

    override fun getViewBinding(): ActivityBlockContectBinding {
        return ActivityBlockContectBinding.inflate(layoutInflater)
    }

    override fun initView() {
        relEmptyAll = findViewById(R.id.rel_empty_all)
        relData = findViewById(R.id.rel_data)
        val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color,this)
        val color1 = Glob.getResourceFromAttr(R.attr.navigation_bg_color,this)
        window.statusBarColor = color
        window.navigationBarColor = color1
        blockContectActivity = this
        Glob.SetStatusbarColor(window)

        // ads view

        constrain_adview = findViewById(R.id.constrain_adview)
        rel_native_banner = findViewById(R.id.rel_native_banner)
        txt_ad_view = findViewById(R.id.txt_ad_view)
        frame_native_banner = findViewById(R.id.frame_native_banner)


//        InterstitialAds.Show_google_Interstitialads(this, TAG)
        showAdView()
        
        blockNumberList = getBlockedNumbers(this@BlockContectActivity)
        setAdapter(blockNumberList)


        if(blockNumberList.isEmpty()||blockNumberList.equals(null)){
            binding.relEmptyAll.visibility = View.VISIBLE
            binding.constrainAdview.visibility = View.GONE
            binding.relData.visibility = View.GONE
        }else{
            binding.constrainAdview.visibility = View.VISIBLE
            binding.relEmptyAll.visibility = View.GONE
            binding.relData.visibility = View.VISIBLE
        }

        binding.imgBack.setOnClickListener(View.OnClickListener {

            onBackPressedDispatcher.onBackPressed()
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "Block_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("Block_Act_onBackpress", Bundle())
//                InterstitialAds.Show_google_Interstitialads(this@BlockContectActivity, "Block_Act_Backpress")
                finish()
            }

        })

        Log.e("Contect_Event--", "Block_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Block_Act_onCreate", Bundle())
    }

    private fun setAdapter(blockList: MutableList<BlockContect>) {
        binding.revBlocklist.apply {
            mBlockAdapter = BlockAdapter(this@BlockContectActivity, blockList)
            adapter = mBlockAdapter
            layoutManager = LinearLayoutManager(this@BlockContectActivity)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun getBlockedNumbers(activity: Activity): MutableList<BlockContect> {
        val uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI
        val cursor = activity.contentResolver.query(
            uri, null, null, null, null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ID)
                val number = cursor.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER)
                val blockId = cursor.getLong(id)
                val blockedNumber = cursor.getString(number)


                val contactName = getContactNameFromNumber(activity, blockedNumber) // Get contact name

                blockNumberList.add(BlockContect(blockId, blockedNumber,contactName))
            }
            cursor.close()
        }

        Collections.reverse(blockNumberList)
        return blockNumberList
    }
    @SuppressLint("Range")
    fun getContactNameFromNumber(context: Context, number: String): String? {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
            }
        }
        return null
    }


    private fun showAdView() {
        if (Ads_Utils.showGoogleNativeBlock == "yes") {
            if (Ads_Utils.onlyMoreNativeBanner == "yes") {
                if (Ads_Utils.more_List_data.size > 0) {
                    More_Native_Banner_Ad_Load(
                        activity = this,
                        frameLayout = frame_native_banner!!
                    )
                } else {
                    txt_ad_view?.visibility = View.VISIBLE
                }
            } else {
                Native_Banner_Goggle_Ad(
                    activity = this,
                    adID = Ads_Utils.GNB_BLOCK,
                    tvAdText = txt_ad_view!!,
                    frameLayout = frame_native_banner!!
                )
            }
        } else {
            rel_native_banner?.visibility = View.GONE
        }
    }

    private fun getAddRequest(): AdRequest {
        val extras = Bundle()
        extras.putString("maxContentRatingAd", Ads_Utils.maxContentRatingAd)
        return AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
    }

    private fun Native_Banner_Goggle_Ad(
        activity: Activity,
        adID: String,
        tvAdText: TextView,
        frameLayout: FrameLayout
    ) {
        val builder = AdLoader.Builder(activity, adID).forNativeAd { nativeAd ->
            Log.e(LOG_TAG, "${TAG}_NBads_show")
            tvAdText.visibility = View.GONE
            DisplayNativeBanner(activity, frameLayout, tvAdText, nativeAd)
        }

        val adLoader = builder.withAdListener(object : AdListener() {

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e(LOG_TAG, "${TAG}_NBads_failed")
                if (Ads_Utils.showMoreNativeBanner == "yes") {
                    if (Ads_Utils.more_List_data.size > 0) {
                        if (!activity.isFinishing) {
                            More_Native_Banner_Ad_Load(
                                activity,
                                frameLayout
                            )
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
                Log.e(LOG_TAG, "${TAG}_NBads_clicked")
                Handler(Looper.getMainLooper()).postDelayed({
                }, 500)
                Native_Banner_Goggle_Ad(activity, adID, tvAdText, frameLayout)
            }
        }).build()

        val request = getAddRequest()
        adLoader.loadAd(request)
    }

    private fun DisplayNativeBanner(
        activity: Activity,
        frameLayout: FrameLayout,
        tvAdText: TextView,
        nativeAd: NativeAd
    ) {
        tvAdText.visibility = View.GONE

        val adView = activity.layoutInflater.inflate(
            R.layout.google_native_ad,
            activity.findViewById(R.id.nativeAd),
            false
        ) as NativeAdView

        populateAppInstallAdView(nativeAd, adView)
        frameLayout.removeAllViews()
        frameLayout.addView(adView)
    }

    private fun populateAppInstallAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        adView.iconView = adView.findViewById(R.id.adIcon)
        adView.headlineView = adView.findViewById(R.id.adName)
        adView.bodyView = adView.findViewById(R.id.adBody)

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

    fun More_Native_Banner_Ad_Load(
        activity: Activity,
        frameLayout: FrameLayout
    ) {
        val view =  activity.layoutInflater.inflate(
            R.layout.google_native_ad_moreapp,
            activity.findViewById(R.id.nativeAd),
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
            Log.e(LOG_TAG, "${TAG}_MoreNBad_clicked")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_name_moreas.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoreNBad_clicked")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_adsbodymore.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoreNBad_clicked")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_ads_call_action_more.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoreNBad_clicked")
            DisplayAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }
        Log.e(LOG_TAG, "${TAG}_MoreNBad_show")
    }


    private fun DisplayAdClick(activity: Activity, link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {

        }
    }



}