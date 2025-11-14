package com.phonecontactscall.contectapp.phonedialerr

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

object RateDialog {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var img_rate1: ImageView
    lateinit var img_rate2: ImageView
    lateinit var img_rate3: ImageView
    lateinit var img_rate4: ImageView
    lateinit var img_rate5: ImageView
    lateinit var img_rate_icon: ImageView

    lateinit var txt_message: TextView

    var star_number = 0

    @JvmStatic
    fun Show_RateDialog(context: Context) {
        val dialog: Dialog
        dialog = Dialog((context as Activity))
        dialog.setContentView(R.layout.dialog_rateapp)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.dialog_theme

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)


        try {

            if (!context.isFinishing) {
                dialog.show()
            }

        } catch (e: Exception) {
        }
        dialog.window!!.setGravity(Gravity.CENTER)


        sharedPreferences = context.getSharedPreferences("share_pref_rate_caller", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val lin_rate_us = dialog.findViewById<View>(R.id.lin_rate_us) as LinearLayout
        val img_close_rate = dialog.findViewById<View>(R.id.img_close_rate) as ImageView
        img_rate1 = dialog.findViewById<View>(R.id.img_rate1) as ImageView
        img_rate2 = dialog.findViewById<View>(R.id.img_rate2) as ImageView
        img_rate3 = dialog.findViewById<View>(R.id.img_rate3) as ImageView
        img_rate4 = dialog.findViewById<View>(R.id.img_rate4) as ImageView
        img_rate5 = dialog.findViewById<View>(R.id.img_rate5) as ImageView
        img_rate_icon = dialog.findViewById<View>(R.id.img_rate_icon) as ImageView
        txt_message = dialog.findViewById<View>(R.id.txt_message) as TextView


        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val theme = Glob.check_theme()


        when (theme) {
            "light" -> img_rate_icon.setImageResource(R.drawable.ic_rate_us_light)

            "dark" -> img_rate_icon.setImageResource(R.drawable.ic_rate_us)
            "System_theme" -> if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                img_rate_icon.setImageResource(R.drawable.ic_rate_us)
            } else {
                img_rate_icon.setImageResource(R.drawable.ic_rate_us_light)
            }
        }


        val sharedPrefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        img_rate1.setOnClickListener(View.OnClickListener {
            star_number = 1
            StartCLick(1, context)
        })

        img_rate2.setOnClickListener(View.OnClickListener {
            star_number = 2
            StartCLick(2, context)
        })
        img_rate3.setOnClickListener(View.OnClickListener {
            star_number = 3
            StartCLick(3, context)

        })
        img_rate4.setOnClickListener(View.OnClickListener {
            star_number = 4
            StartCLick(4, context)
        })
        img_rate5.setOnClickListener(View.OnClickListener {
            star_number = 5
            StartCLick(5, context)
        })
        star_number = 4
        StartCLick(4, context)

        img_close_rate.setOnClickListener { // Close dialog
            Glob.israting = true

            sharedPrefs.edit().putBoolean("ratingShown", true).apply()
            dialog.dismiss()
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            Glob.israting = true
            dialog.dismiss()
        }
        Glob.rate_value_contect = sharedPreferences.getInt("contect_rate_prefrence", 0)
        lin_rate_us.setOnClickListener { // Close dialog
            dialog.dismiss()
            Glob.rate_value_contect = sharedPreferences.getInt("contect_rate_prefrence", 0)
            Glob.rate_value_contect++
            editor.putInt("contect_rate_prefrence", Glob.rate_value_contect)
            editor.apply()
            Glob.israting = true
            if(star_number > 3){
                Glob.showToast(context, context.getString(R.string.toast_rate_good))

            }else{
                Glob.showToast(context, context.getString(R.string.toast_rate))

            }
            Handler().postDelayed({
                dialog.dismiss()
                val str = "android.intent.action.VIEW"
                try {
                    context.startActivity(
                        Intent(
                            str,
                            Uri.parse(Ads_Utils.Playstorelink1 + MyApplication.getInstance().packageName)
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            str,
                            Uri.parse(Ads_Utils.Playstorelink2 + MyApplication.getInstance().packageName)
                        )
                    )
                }
            }, 300)
        }
    }

    fun StartCLick(value: Int, context: Activity) {
        img_rate1.setImageResource(R.drawable.ic_star_unselect)
        img_rate2.setImageResource(R.drawable.ic_star_unselect)
        img_rate3.setImageResource(R.drawable.ic_star_unselect)
        img_rate4.setImageResource(R.drawable.ic_star_unselect)
        img_rate5.setImageResource(R.drawable.ic_star_unselect)

        if (value == 1) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
        } else if (value == 2) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
            img_rate2.setImageResource(R.drawable.ic_star_selected)
        } else if (value == 3) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
            img_rate2.setImageResource(R.drawable.ic_star_selected)
            img_rate3.setImageResource(R.drawable.ic_star_selected)
        } else if (value == 4) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
            img_rate2.setImageResource(R.drawable.ic_star_selected)
            img_rate3.setImageResource(R.drawable.ic_star_selected)
            img_rate4.setImageResource(R.drawable.ic_star_selected)
        } else if (value == 5) {
            img_rate1.setImageResource(R.drawable.ic_star_selected)
            img_rate2.setImageResource(R.drawable.ic_star_selected)
            img_rate3.setImageResource(R.drawable.ic_star_selected)
            img_rate4.setImageResource(R.drawable.ic_star_selected)
            img_rate5.setImageResource(R.drawable.ic_star_selected)
        }
    }


}