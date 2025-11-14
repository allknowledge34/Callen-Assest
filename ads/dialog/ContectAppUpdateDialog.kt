package com.phonecontactscall.contectapp.phonedialerr.ads.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.Ads_Utils

class ContectAppUpdateDialog(
    private val activity: Activity
): Dialog(activity) {

    private var btnUpdate: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_update)

        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.attributes?.windowAnimations = R.style.dialog_theme

        window?.setBackgroundDrawable(ColorDrawable(0))
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        btnUpdate = findViewById(R.id.btnUpdateApp)

        btnUpdate?.setOnClickListener {
            dismiss()
            try {
                val intent = Intent("android.intent.action.VIEW")
                intent.setData(Uri.parse(Ads_Utils.playstore_link))
                activity.startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                val linkString: String = Ads_Utils.playstore_link
                val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                defaultBrowser.setData(Uri.parse(linkString))
                activity.startActivity(defaultBrowser)
            }
        }

    }

}