package com.phonecontactscall.contectapp.phonedialerr.activity

import android.app.Activity
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityDefaultSetBinding
import kotlin.jvm.internal.Intrinsics

class DefaultCall_Activity : BaseActivity<ActivityDefaultSetBinding>() {

    private var REQUEST_CODE_SET_DEFAULT_DIALER = 100

    override fun getViewBinding(): ActivityDefaultSetBinding {
        return ActivityDefaultSetBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initView() {

        val color = Glob.getResourceFromAttr(R.attr.continuescreen_color,this)
        val color1 = Glob.getResourceFromAttr(R.attr.default_bg_color,this)
        window.statusBarColor = color
        window.navigationBarColor = color1

        Glob.SetStatusbarColor(window)

        binding.relSetdefault.setOnClickListener(View.OnClickListener {
            if (!Glob.isDefaultDialer(this)) {
                launchSetDefaultDialerIntent();
            }
        })

        binding.txtPolicy.setOnClickListener(View.OnClickListener {
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/eveningstars507/home")
                )
                startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/eveningstars507/home")
                )
                startActivity(browserIntent)
            }
        })


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "DefaultCall_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("DefaultCall_Act_onBackpress", Bundle())
                val intent = Intent(this@DefaultCall_Activity, MainActivity::class.java)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                    finish()
                } else {

                }
            }

        })




        Log.e("Contect_Event--", "DefaultCall_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("DefaultCall_Act_onCreate", Bundle())
    }

    fun launchSetDefaultDialerIntent() {
        if (Glob.isQPlus()) {
            val roleManager = getSystemService(RoleManager::class.java) as RoleManager
            Intrinsics.checkNotNull(roleManager)
            if (!roleManager.isRoleAvailable("android.app.role.DIALER") || roleManager.isRoleHeld("android.app.role.DIALER")) {
                return
            }
            val createRequestRoleIntent =
                roleManager.createRequestRoleIntent("android.app.role.DIALER")
            Intrinsics.checkNotNullExpressionValue(
                createRequestRoleIntent,
                "roleManager.createReques\u2026(RoleManager.ROLE_DIALER)"
            )

            startActivityForResult(createRequestRoleIntent, REQUEST_CODE_SET_DEFAULT_DIALER)
            return
        }
        Intent("android.telecom.action.CHANGE_DEFAULT_DIALER").putExtra(
            "android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME",
            packageName
        )
        try {

            val intent = intent
            Intrinsics.checkNotNullExpressionValue(intent, "intent")

            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
        } catch (unused: ActivityNotFoundException) {
        } catch (e: Exception) {
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            if (resultCode == Activity.RESULT_OK) {


                if (!Glob.isDefaultDialer(this)) {
                    try {
                        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
                        intent.setData(Uri.parse("package:" + getPackageName()))
                        startActivity(intent)
                    } catch (unused: java.lang.Exception) {
                    }
                    finish()
                    return
                }else{
                    val intent = Intent(this, MainActivity::class.java)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                    }
                }


            } else {
            }
        }
    }


}