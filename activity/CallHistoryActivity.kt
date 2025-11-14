package com.phonecontactscall.contectapp.phonedialerr.activity

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper.callPhoneClick
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Glob.delte_number
import com.phonecontactscall.contectapp.phonedialerr.Model.CallLogEntry
import com.phonecontactscall.contectapp.phonedialerr.Model.RecentModel
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.adapter.CallLogAdapter
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityCallLogDetailBinding
import com.bumptech.glide.Glide
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import java.io.IOException


class CallHistoryActivity : BaseActivity<ActivityCallLogDetailBinding>() {
    private var recent_model: RecentModel? = null
    var main_popup: PopupWindow? = null
    companion object {
        private const val LOG_TAG = "Contect_Event--"
        private const val TAG = "History_Act"
    }

    private var constrain_adview: ConstraintLayout? = null
    private var rel_native_banner: RelativeLayout? = null
    private var txt_ad_view: TextView? = null
    private var frame_native_banner: FrameLayout? = null

    override fun getViewBinding(): ActivityCallLogDetailBinding {
        return ActivityCallLogDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color,this)
        val color1 = Glob.getResourceFromAttr(R.attr.default_bg_color,this)
        window.statusBarColor = color
        window.navigationBarColor = color1

        Glob.SetStatusbarColor(window)
//        InterstitialAds.Show_google_Interstitialads(this, TAG)
        
        
        constrain_adview = findViewById(R.id.constrain_adview)
        rel_native_banner = findViewById(R.id.rel_native_banner)
        txt_ad_view = findViewById(R.id.txt_ad_view)
        frame_native_banner = findViewById(R.id.frame_native_banner)

        showAdView()

        val gson = Gson()
        recent_model = gson.fromJson<RecentModel>(intent.getStringExtra("identifier"), RecentModel::class.java)

        val contactName = getContactNameFromPhoneNumber( this, recent_model!!.number!!)

        if(contactName!=null){
            binding.tvName.text = contactName
        }else{
            binding.tvName.text = recent_model!!.number
        }
        retrieveContactPhoto(this,recent_model!!,binding.ivImage)
        binding.tvNumber.text = recent_model!!.number
        
        binding.imgMore.setOnClickListener(View.OnClickListener {

            showPopup(v = binding.imgMore, recent_model!!)
        })


        binding.ivCall.setOnClickListener(View.OnClickListener {

            callPhoneClick(this, recent_model!!.number)
        })

        val callLogs = getCallLogForNumber(this, recent_model!!.number)
        binding.revCallLogdetail.layoutManager = LinearLayoutManager(this)
        val adapter = CallLogAdapter(this@CallHistoryActivity, callLogs)
        binding.revCallLogdetail.adapter = adapter


        if(recent_model!!.equals(null)){
            binding.relEmptyAll.visibility = View.VISIBLE
            binding.relData.visibility = View.GONE
        }else{
            binding.relEmptyAll.visibility = View.GONE
            binding.relData.visibility = View.VISIBLE
        }
        binding.imgBack.setOnClickListener(View.OnClickListener {
            onBackPressedDispatcher.onBackPressed()
        })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "History_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("History_Act_onBackpress", Bundle())
//                InterstitialAds.Show_google_Interstitialads(this@CallHistoryActivity, "History_BACK")
                finish()
            }

        })
        Log.e("Contect_Event--", "History_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("History_Act_onCreate", Bundle())

    }


    fun retrieveContactPhoto(
        context: Activity,
        recentModel: RecentModel,
        iv_image: RoundedImageView

    ): Bitmap {
        val contentResolver = context.contentResolver
        var contactId: String = recentModel.id.toString()
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(recentModel.number)
        )

        val projection =
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)

        val cursor =
            contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            cursor.close()
        }



        var photo = BitmapFactory.decodeResource(context.resources,R.drawable.ic_contact_1)

        try {
            if (contactId != null && contactId.toInt() != -1) {
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    context.contentResolver,
                    ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        contactId.toLong()
                    )
                )

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()

                } else {

                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        iv_image.setImageBitmap(photo)
        iv_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        iv_image.setCornerRadius(30f)
        iv_image.setOval(true);
        return photo
    }
    private fun showAdView() {
        if (Ads_Utils.showGoogleNativeCallHistory == "yes") {
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
                    adID = Ads_Utils.GNB_HISTORY,
                    txt_ad_view = txt_ad_view!!,
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
        txt_ad_view: TextView,
        frameLayout: FrameLayout
    ) {
        val builder = AdLoader.Builder(activity, adID).forNativeAd { nativeAd ->
            Log.e(LOG_TAG, "${TAG}_NBads_show")
            txt_ad_view.visibility = View.GONE
            DisplayNativeBanner(activity, frameLayout, txt_ad_view, nativeAd)
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
                        txt_ad_view.visibility = View.VISIBLE
                    }
                } else {
                    txt_ad_view.visibility = View.VISIBLE
                }
            }

            override fun onAdLoaded() {
                txt_ad_view.visibility = View.GONE
            }

            override fun onAdClicked() {
                Log.e(LOG_TAG, "${TAG}_NBads_clicked")

                Native_Banner_Goggle_Ad(activity, adID, txt_ad_view, frameLayout)
            }
        }).build()

        val request = getAddRequest()
        adLoader.loadAd(request)
    }

    private fun DisplayNativeBanner(
        activity: Activity,
        frameLayout: FrameLayout,
        txt_ad_view: TextView,
        nativeAd: NativeAd
    ) {
        txt_ad_view.visibility = View.GONE

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
            showAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_name_moreas.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoreNBad_clicked")
            showAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_adsbodymore.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoreNBad_clicked")
            showAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }

        txt_ads_call_action_more.setOnClickListener {
            Log.e(LOG_TAG, "${TAG}_MoreNBad_clicked")
            showAdClick(activity, Ads_Utils.more_List_data[number].more_appLink.toString())
        }
        Log.e(LOG_TAG, "${TAG}_MoreNBad_show")
    }

    private fun showAdClick(activity: Activity, link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {

        }
    }



    fun showPopup(v: View, callLog: RecentModel) {

        val inflater: LayoutInflater = layoutInflater

        val view: View = inflater.inflate(R.layout.popup_history, null)
        main_popup = PopupWindow(this)
        main_popup!!.contentView = view
        main_popup!!.isFocusable = true
        main_popup!!.isOutsideTouchable = true
        main_popup!!.setBackgroundDrawable(ColorDrawable(0))
        main_popup!!.width = WindowManager.LayoutParams.WRAP_CONTENT;
        main_popup!!.height = WindowManager.LayoutParams.WRAP_CONTENT;

        val values = IntArray(2)
        v.getLocationInWindow(values)
        val positionOfIcon = values[1]
        println("Position Y:$positionOfIcon")

        val displayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels * 2) / 3.5
        println("Height:$height")

        if (positionOfIcon > height) {
            main_popup!!.showAsDropDown(v, 0, -500)
        } else {
            main_popup!!.showAsDropDown(v, 0, 0)
        }



        val rel_copy: RelativeLayout = view.findViewById(R.id.rel_copy)
        val rel_delete: RelativeLayout = view.findViewById(R.id.rel_delete)


        rel_delete.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()



            val delete_dialog = Dialog(this@CallHistoryActivity)

            delete_dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            delete_dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            delete_dialog.setContentView(R.layout.dialog_delete)

            delete_dialog.window!!.setGravity(Gravity.CENTER)
            delete_dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            delete_dialog.window!!.getAttributes().windowAnimations = R.style.dialog_theme
            delete_dialog.setCancelable(true)
            delete_dialog.setCanceledOnTouchOutside(true)
            delete_dialog.show()
            val txt_re = delete_dialog.findViewById<TextView>(R.id.txt_re)
            val dialog_title = delete_dialog.findViewById<TextView>(R.id.dialog_title)

            txt_re.text = getString(R.string.str_delete_history)
            dialog_title.text = getString(R.string.str_delte_file_msg_history)
            val img_close_delete = delete_dialog.findViewById<TextView>(R.id.txt_cancel)
            val btn_delete = delete_dialog.findViewById<TextView>(R.id.text_ok)
            btn_delete.setOnClickListener {
                delete_dialog.dismiss()
                try {
                    deleteCallLogByNumber(this, recent_model!!.number)
                    Glob.showToast(this, getString(R.string.toast_delete));

                    delte_number = true
                    finish()
                } catch (e: SecurityException) {
                }

            }
            img_close_delete.setOnClickListener {
                delete_dialog.dismiss()


            }


        })


        rel_copy.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()
            copycontect(recent_model!!)

        })


    }

    private fun copycontect(recentModel: RecentModel) {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", recentModel.number)
        clipboard.setPrimaryClip(clip)
        Glob.showToast(this, getString(R.string.toast_copy) + recentModel.number);
    }
    fun deleteCallLogByNumber(context: Context, phoneNumber: String) {





        val uri = CallLog.Calls.CONTENT_URI
        val selection = "${CallLog.Calls.NUMBER} = ?"
        val selectionArgs = arrayOf(phoneNumber)

        val rowsDeleted = context.contentResolver.delete(uri, selection, selectionArgs)

        if (rowsDeleted > 0) {
        } else {
        }
    }


    fun getContactNameFromPhoneNumber(context: Context, phoneNumber: String): String? {
        val contentResolver = context.contentResolver
        val cleanedPhoneNumber = phoneNumber.trim()

        if (cleanedPhoneNumber.isEmpty()) {
            Log.e("ContactLookup", "Phone number is empty or invalid.")
            return null
        }

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(cleanedPhoneNumber)
        )

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        try {
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                } else {
                    Log.e("ContactLookup", "No contact found for phone number: $cleanedPhoneNumber")
                }
            }
        } catch (e: SecurityException) {
            Log.e("ContactLookup", "Permission denied: ${e.message}")
        } catch (e: IllegalArgumentException) {
            Log.e("ContactLookup", "Invalid URI or query: ${e.message}")
        } catch (e: Exception) {
            Log.e("ContactLookup", "Unexpected error: ${e.message}")
        }

        return null
    }

    fun getCallLogForNumber(context: Context, phoneNumber: String): List<CallLogEntry> {
        val callLogEntries = mutableListOf<CallLogEntry>()

        val cursor: Cursor? = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            "${CallLog.Calls.NUMBER} = ?",
            arrayOf(phoneNumber),
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)

            while (cursor.moveToNext()) {
                val number = cursor.getString(numberIndex)
                val type = cursor.getInt(typeIndex)
                val date = cursor.getLong(dateIndex)
                val duration = cursor.getInt(durationIndex)

                callLogEntries.add(CallLogEntry(number, type, date, duration))
            }
        }
        return callLogEntries
    }

}