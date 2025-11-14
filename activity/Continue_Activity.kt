package com.phonecontactscall.contectapp.phonedialerr.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityContinueBinding


class Continue_Activity : BaseActivity<ActivityContinueBinding>() {



    override fun getViewBinding(): ActivityContinueBinding {
        return ActivityContinueBinding.inflate(layoutInflater)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.ivBanner.setImageResource(R.drawable.welcome_banner_dark)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.ivBanner.setImageResource(R.drawable.welcome_banner)
            }
        }
    }

    override fun initView() {

        Glob.SetStatusbarColor(window)

        val color = Glob.getResourceFromAttr(R.attr.continuescreen_color,this)
        val color1 = Glob.getResourceFromAttr(R.attr.default_bg_color,this)

        window.statusBarColor = color
        window.navigationBarColor = color1
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val theme = Glob.check_theme()


        when (theme) {
            "light" ->  binding.ivBanner.setImageResource(R.drawable.welcome_banner)
            "dark" -> binding.ivBanner.setImageResource(R.drawable.welcome_banner_dark)
            "System_theme" -> if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                binding.ivBanner.setImageResource(R.drawable.welcome_banner_dark)
            } else {
                binding.ivBanner.setImageResource(R.drawable.welcome_banner)
            }
        }
        binding.txtPolicy.setOnClickListener { v ->

            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/eveningstars507/home")
            )

            if (browserIntent.resolveActivity(packageManager) != null) {
                startActivity(browserIntent)
            } else {
                Toast.makeText(this, "No browser app found", Toast.LENGTH_SHORT).show()
            }

        }
        binding.relContinue.setOnClickListener(View.OnClickListener {

            val langIntent = Intent(this, Langugae_Activity::class.java).putExtra("first_splash_lang", true)
            if (langIntent.resolveActivity(packageManager) != null) {
                startActivity(langIntent)
            } else {
                Toast.makeText(this, "Unable to open language screen", Toast.LENGTH_SHORT).show()
            }


        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "Continue_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("Continue_Act_onBackpress", Bundle())
                val langIntent = Intent(this@Continue_Activity, Langugae_Activity::class.java).putExtra("first_splash_lang", true)
                if (langIntent.resolveActivity(packageManager) != null) {
                    startActivity(langIntent)
                    finish()
                } else {
                    Toast.makeText(this@Continue_Activity, "Unable to open language screen", Toast.LENGTH_SHORT).show()
                }

            }

        })

        Log.e("Contect_Event--", "Continue_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Continue_Act_onCreate", Bundle())
    }


}