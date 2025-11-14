package com.phonecontactscall.contectapp.phonedialerr.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.BlockedNumberContract
import android.provider.ContactsContract
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper.callPhoneClick
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDao
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDatabase
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Glob.edit_number_fav_detail
import com.phonecontactscall.contectapp.phonedialerr.Glob.fav_remove
import com.phonecontactscall.contectapp.phonedialerr.Model.FavContact
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactHelper
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityContectDetialActivtyBinding
import com.google.gson.Gson
import com.phonecontactscall.contectapp.phonedialerr.ads.adsload.InterstitialAds
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils
import com.phonecontactscall.contectapp.phonedialerr.Glob.delte_number
import com.bumptech.glide.Glide
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


class FavContectDetialActivty : BaseActivity<ActivityContectDetialActivtyBinding>() {
    private var sharedPrefs: SharedPreferences? = null
    private var recent_model: FavContact? = null
    lateinit var contactDao: ContactDao
    private val arrayList = mutableListOf<FavContact>()


    companion object {

        private const val LOG_TAG = "Contect_Event--"
        private const val TAG = "Fav_Act"
    }

    private var constrain_adview: ConstraintLayout? = null
    private var rel_native_banner: RelativeLayout? = null
    private var txt_ad_view: TextView? = null
    private var frame_native_banner: FrameLayout? = null

    override fun getViewBinding(): ActivityContectDetialActivtyBinding {
        return ActivityContectDetialActivtyBinding.inflate(layoutInflater)
    }

    var main_popup: PopupWindow? = null
    override fun initView() {
        val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color, this)
        val color1 = Glob.getResourceFromAttr(R.attr.lang_bg_color, this)
//
        window.statusBarColor = color
        window.navigationBarColor = color1

        Glob.SetStatusbarColor(window)
        InterstitialAds.Show_google_Interstitialads(this, TAG)
        constrain_adview = findViewById(R.id.constrain_adview)
        rel_native_banner = findViewById(R.id.rel_native_banner)
        txt_ad_view = findViewById(R.id.txt_ad_view)
        frame_native_banner = findViewById(R.id.frame_native_banner)
        showAdView()


        val database = ContactDatabase.getDatabase(this)
        contactDao = database.contactDao()

        val gson = Gson()
        recent_model =
            gson.fromJson<FavContact>(intent.getStringExtra("identifier_fav"), FavContact::class.java)



        retrieveContactPhoto(this, recent_model!!)

        binding.txtName.text = recent_model!!.name
        binding.txtNumber.text = recent_model!!.number

        binding.txtWpMessage.text = recent_model!!.number
        binding.txtVoiceCall.text = recent_model!!.number
        binding.txtVideoCall.text = recent_model!!.number


        sharedPrefs = getSharedPreferences("favorites", Context.MODE_PRIVATE)


        binding.imgBack.setOnClickListener(View.OnClickListener {
            onBackPressedDispatcher.onBackPressed()
        })
        binding.txtNumber.setOnLongClickListener(View.OnLongClickListener {
            copycontect(recent_model!!)

        })
        binding.imgMore.setOnClickListener(View.OnClickListener {

            showPopup(v = binding.imgMore, recent_model!!)
        })

        binding.linMessage.setOnClickListener(View.OnClickListener {
            sendSMS(recent_model!!.number)
        })

        binding.linCall.setOnClickListener(View.OnClickListener {


            callPhoneClick(this, recent_model!!.number)

        })

        binding.linBlock.setOnClickListener(View.OnClickListener {

            blockSelectedNumber(recent_model!!)
        })
        binding.imgFav.setImageResource(R.drawable.ic_favorite)
        binding.linFav.setOnClickListener(View.OnClickListener {



            CoroutineScope(Dispatchers.IO).launch {
                recent_model!!.isFav = !recent_model!!.isFav
                val existingContact = contactDao.getFavContactById(recent_model!!.id!!)
                if (existingContact != null) {
                    if (existingContact.isFav) {

                        CoroutineScope(Dispatchers.IO).launch {
                            contactDao.Delete_fav(recent_model!!!!.name)
                        }
                        fav_remove = true
                        runOnUiThread {
                            Glob.showToast(this@FavContectDetialActivty,  getString(R.string.toast_unfav_sucess))

                        }


                    }

                } else {
                    withContext(Dispatchers.Main) {

                        try {
                            if (recent_model!!.isFav) {

                                val contentValues = ContentValues().apply {
                                    put(
                                        ContactsContract.Contacts.STARRED,
                                        1
                                    )
                                }

                                val uri = Uri.withAppendedPath(
                                    ContactsContract.Contacts.CONTENT_URI, recent_model!!.id.toString()
                                )

                                val rowsUpdated =
                                    contentResolver.update(uri, contentValues, null, null)

                                if (rowsUpdated > 0) {
                                } else {

                                }
                                binding.imgFav.setImageResource(R.drawable.ic_favorite)
                                withContext(Dispatchers.Main) {
                                    Glob.showToast(this@FavContectDetialActivty,  getString(R.string.toast_fav_sucess))




                                }
                            } else {
                                fav_remove = true

                                val contentValues = ContentValues().apply {
                                    put(
                                        ContactsContract.Contacts.STARRED,
                                        0
                                    )
                                }

                                val uri = Uri.withAppendedPath(
                                    ContactsContract.Contacts.CONTENT_URI, recent_model!!.id.toString()
                                )

                                val rowsUpdated =
                                    contentResolver.update(uri, contentValues, null, null)
                                CoroutineScope(Dispatchers.IO).launch {
                                    contactDao.Delete_fav(recent_model!!!!.name)
                                }
                                if (rowsUpdated > 0) {
                                } else {
                                    Toast.makeText(
                                        this@FavContectDetialActivty,
                                        "Failed to mark contact as favorite",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                binding.imgFav.setImageResource(R.drawable.ic_favorite_unfav)
                                withContext(Dispatchers.Main) {

                                    Glob.showToast(this@FavContectDetialActivty,  getString(R.string.toast_unfav_sucess))


                                }
                            }
                        } catch (e: Exception) {
                        }

                    }
                }
            }


        })

        binding.linWpMessage.setOnClickListener(View.OnClickListener {

            if(isAppInstalled("com.whatsapp")) {
                openWhatsappContact(recent_model!!.number)
            } else {

                showCustomInstallDialog()

            }


        })

        binding.linWpVoicecall.setOnClickListener(View.OnClickListener {



            if(isAppInstalled("com.whatsapp")) {
                initiateWhatsAppCall(this, recent_model!!.number, isVideoCall = false)

            } else {
                showCustomInstallDialog()

            }



        })

        binding.linWpVideoCall.setOnClickListener(View.OnClickListener {


            if(isAppInstalled("com.whatsapp")) {
                initiateWhatsAppVideoCall(this, recent_model!!.number, isVideoCall = true)
            } else {
                showCustomInstallDialog()
            }


        })

        binding.linHistory.setOnClickListener(View.OnClickListener {


            val gson = Gson()
            val intent = Intent(this@FavContectDetialActivty, CallHistoryActivity::class.java)
            intent.putExtra("identifier", gson.toJson(recent_model))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {

            }


        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "Fav_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("Fav_Act_onBackpress", Bundle())
//                InterstitialAds.Show_google_Interstitialads(this@FavContectDetialActivty, "Fav_Act_Back")
                finish()
            }

        })



        Log.e("Contect_Event--", "Fav_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Fav_Act_onCreate", Bundle())
    }
    var wp_dialogue: Dialog ?= null

    private fun showCustomInstallDialog() {
        wp_dialogue = Dialog(this@FavContectDetialActivty)
        wp_dialogue!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        wp_dialogue!!.setContentView(R.layout.dialog_wp)
        wp_dialogue!!.setCancelable(true)
        wp_dialogue!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        wp_dialogue!!.window!!.attributes.windowAnimations = R.style.dialog_theme
        
        wp_dialogue!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        wp_dialogue!!.setCanceledOnTouchOutside(true)
        val lin_install  = wp_dialogue!!.findViewById<LinearLayout>(R.id.lin_install)
        val close  = wp_dialogue!!.findViewById<ImageView>(R.id.close)

        lin_install.setOnClickListener {
            try {
                val intent = Intent()
                intent.setAction(Intent.ACTION_VIEW)
                intent.setData(Uri.parse("market://details?id=" + "com.whatsapp"))
                startActivityForResult(intent, 102)

            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                val intent = Intent()
                intent.setAction(Intent.ACTION_VIEW)
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + "com.whatsapp"))
                startActivityForResult(intent, 102)
            }
            wp_dialogue!!.dismiss()
        }

        close.setOnClickListener {
            wp_dialogue!!.dismiss()
        }
        wp_dialogue!!.show()
    }


    private fun initiateWhatsAppCall(context: Context, phoneNumber: String, isVideoCall: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$phoneNumber")
                    setPackage("com.whatsapp")
                }
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showCustomInstallDialog()
            }
        } else {

            if(isAppInstalled("com.whatsapp")) {
                val intent = Intent()
                intent.setAction(Intent.ACTION_VIEW)

                val name = getContactName(recent_model!!.number, this@FavContectDetialActivty)
                val whatsappcall = getContactIdForWhatsAppCall(name, this@FavContectDetialActivty)
                if (whatsappcall != 0) {
                    intent.setDataAndType(
                        Uri.parse("content://com.android.contacts/data/$whatsappcall"),
                        "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"
                    )
                    intent.setPackage("com.whatsapp")

                    startActivity(intent)
                }else {
                    Glob.showToast(context, getString(R.string.str_whatspp_nocontect))                }
            } else {
                showCustomInstallDialog()
            }



        }
    }


    private fun initiateWhatsAppVideoCall(context: Context, phoneNumber: String, isVideoCall: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$phoneNumber")
                    setPackage("com.whatsapp")
                }
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showCustomInstallDialog()
            }
        } else {

            if(isAppInstalled("com.whatsapp")) {

                val intent = Intent()
                intent.setAction(Intent.ACTION_VIEW)


                val name = ContactHelper.getContactName(this, recent_model!!.number, true)
                val videocall = getContactIdForWhatsAppVideoCall(name, this@FavContectDetialActivty)
                if (videocall != 0) {
                    intent.setDataAndType(
                        Uri.parse("content://com.android.contacts/data/$videocall"),
                        "vnd.android.cursor.item/vnd.com.whatsapp.video.call"
                    )
                    intent.setPackage("com.whatsapp")
                    startActivity(intent)
                }else {
                    Glob.showToast(context, getString(R.string.str_whatspp_nocontect))                }

            } else {
                showCustomInstallDialog()
            }


        }
    }



    private fun showAdView() {
        if (Ads_Utils.showGoogleNativeFav == "yes") {
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
                    adID = Ads_Utils.GNB_FAV_DETAILS,
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

    private fun isAppInstalled(packageName: String): Boolean {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (ignored: PackageManager.NameNotFoundException) {
            return false
        }
    }




    private fun copycontect(recentModel: FavContact): Boolean {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", recentModel.number)

        clipboard.setPrimaryClip(clip)

        Glob.showToast(this@FavContectDetialActivty,  getString(R.string.toast_copy) + recentModel.number)
        return true
    }
    fun showPopup(v: View, callLog: FavContact) {

        val inflater: LayoutInflater = layoutInflater

        val view: View = inflater.inflate(R.layout.popup_contect_detail, null)
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
        main_popup!!.setOnDismissListener(PopupWindow.OnDismissListener {

        })
        val rel_delete: RelativeLayout = view.findViewById(R.id.rel_delete)
        val rel_share: RelativeLayout = view.findViewById(R.id.rel_share)
        val rel_edit_contect: RelativeLayout = view.findViewById(R.id.rel_edit_contect)
        rel_edit_contect.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()
            val editIntent = Intent(Intent.ACTION_EDIT)
            editIntent.data =
                Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
                    recent_model!!.id.toString()
                )
            startActivityForResult(editIntent, 1002)

        })
        rel_delete.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()


            val delete_dialog = Dialog(this@FavContectDetialActivty)

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
            val img_close_delete = delete_dialog.findViewById<TextView>(R.id.txt_cancel)
            val btn_delete = delete_dialog.findViewById<TextView>(R.id.text_ok)
            btn_delete.setOnClickListener {
                delete_dialog.dismiss()
                try {
                    deleteContact(this@FavContectDetialActivty, recent_model!!.name, recent_model!!.number)

                } catch (e: SecurityException) {
                }

            }
            img_close_delete.setOnClickListener {
                delete_dialog.dismiss()
            }


        })


        rel_share.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()
            shareContact(recent_model!!)

        })


    }

    fun createVcf(contact: FavContact): File {
        val vcfContent = """
        BEGIN:VCARD
        VERSION:3.0
        FN:${contact.name}
        TEL:${contact.number}
        END:VCARD
    """.trimIndent()


        val fileName = "${contact.name}.vcf"
        val file = File(cacheDir, fileName)
        file.writeText(vcfContent)

        return file
    }

    fun shareContact(
        contact: FavContact

    ) {

        val vcfFile = createVcf(contact)

        val uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", vcfFile)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/x-vcard"
            putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))

            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Contact"))

    }




    fun deleteContact(context: Context, name: String ,number: String) {
        try {
            val contactId = getContactIdByName(this,name) ?: return
            val rawContactId = getRawContactId(contactId) ?: return


            CoroutineScope(Dispatchers.IO).launch {
                contactDao.Delete_fav(name)
            }
            val rawContactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId.toLong())

            val rowsUpdated = contentResolver.delete(rawContactUri, null, null)
            if (rowsUpdated > 0) {
                delte_number = true
                Glob.showToast(this@FavContectDetialActivty, getString(R.string.toast_delete));


                finish()
            } else {
            }
        } catch (e: Exception) {
        }
    }
    fun getContactIdByName(context: Context, name: String): String? {
        var contactId: String? = null
        val cursor = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts._ID),
            "${ContactsContract.Contacts.DISPLAY_NAME} = ?",
            arrayOf(name),
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
            }
        }

        return contactId
    }

    fun getRawContactId(contactId: String): String? {
        var rawContactId: String? = null
        val cursor = contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            arrayOf(ContactsContract.RawContacts._ID),
            "${ContactsContract.RawContacts.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                rawContactId = it.getString(it.getColumnIndex(ContactsContract.RawContacts._ID))
            }
        }

        return rawContactId
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun isNumberBlocked(context: Context, number: String): Boolean {
        return try {
            val uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI
            val projection = arrayOf(BlockedNumberContract.BlockedNumbers.COLUMN_ID)
            val selection = "${BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER} = ?"
            val selectionArgs = arrayOf(number)

            context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                ?.use { cursor ->
                    cursor.moveToFirst()
                } ?: false
        } catch (e: SecurityException) {
            false
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun blockSelectedNumber(selectedConversations: FavContact) {
        val allBlockedIdList = mutableListOf<Long>()
        try {



            val numberWithoutSpaces = selectedConversations.number.replace(" ", "")


            val isAlreadyBlocked = isNumberBlocked(this, numberWithoutSpaces)


            if (isAlreadyBlocked) {

                Glob.removeFromBlock(this, selectedConversations.number)
                Glob.showToast(this@FavContectDetialActivty,  getString(R.string.toast_remove_sucess))
                binding.txtBlock.setText(getString(R.string.str_block))

            }else{
                val isBlock = Glob.addToBlock(this, selectedConversations.number)
                Glob.showToast(this@FavContectDetialActivty,  getString(R.string.toast_added_to_blocklist))
                binding.txtBlock.setText(getString(R.string.str_un_block))


                if (isBlock) {
                    allBlockedIdList.add(selectedConversations.id!!)

                }
            }

        } catch (e: Exception) {
        }
        arrayList.removeAll { conversation ->
            allBlockedIdList.contains(conversation.id)
        }


    }

    fun getContactName(phoneNumber: String?, context: Context): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        var contactName = ""
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }

        return contactName
    }

    fun retrieveContactPhoto(context: Context, recentModel: FavContact): Bitmap {
        val contentResolver = context.contentResolver

        val phoneNumber = recentModel.number.trim()

        if (phoneNumber.isEmpty()) {
            return BitmapFactory.decodeResource(context.resources, R.drawable.ic_contact_1)
        }
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )

        val projection = arrayOf(
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup._ID
        )

        var contactId: String? = null
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            } else {
            }
        }

        if (contactId.isNullOrEmpty()) {
            return BitmapFactory.decodeResource(context.resources, R.drawable.ic_contact_1)
        }
        var photo = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.ic_contact_1
        )

        contactId?.let {
            try {
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    contentResolver,
                    ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        it.toLong()
                    )
                )
                inputStream?.use { stream ->
                    photo = BitmapFactory.decodeStream(stream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        binding.imgPhotouri.apply {
            setImageBitmap(photo)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setCornerRadius(30f)
            setOval(true)
        }

        return photo
    }


    private fun sendSMS(number: String?) {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_SENDTO,
                    Uri.parse("sms:" + number)
                )
            )
            return
        } catch (unused: Exception) {
            return
        }
    }

    fun openWhatsappContact(number: String?) {
        try {
            val uri = Uri.parse("smsto:$number")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to open WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }



    @SuppressLint("Range")
    fun getContactIdForWhatsAppCall(name: String, context: Context?): Int {
        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf<String>(ContactsContract.Data._ID),
            ContactsContract.Data.DISPLAY_NAME + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
            arrayOf<String>(name, "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"),
            ContactsContract.Contacts.DISPLAY_NAME
        )

        if (cursor!!.getCount() > 0) {
            cursor!!.moveToNext()
            val phoneContactID: Int =
                cursor!!.getInt(cursor!!.getColumnIndex(ContactsContract.Data._ID))
            return phoneContactID
        } else {
            return 0
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1002) {
            if (resultCode == RESULT_OK) {

                edit_number_fav_detail = true
                finish()



            }
        }
    }

    @SuppressLint("Range")
    fun getContactIdForWhatsAppVideoCall(name: String, context: Context?): Int {
        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(ContactsContract.Data._ID),
            ContactsContract.Data.DISPLAY_NAME + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
            arrayOf(name, "vnd.android.cursor.item/vnd.com.whatsapp.video.call"),
            ContactsContract.Contacts.DISPLAY_NAME
        )

        if (cursor!!.count > 0) {
            cursor!!.moveToFirst()
            val phoneContactID = cursor!!.getInt(cursor!!.getColumnIndex(ContactsContract.Data._ID))
            return phoneContactID
        } else {
            println("8888888888888888888          ")
            return 0
        }
    }

}