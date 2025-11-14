package com.phonecontactscall.contectapp.phonedialerr.Fragment

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import com.phonecontactscall.contectapp.phonedialerr.activity.BlockContectActivity
import com.phonecontactscall.contectapp.phonedialerr.activity.Langugae_Activity
import com.phonecontactscall.contectapp.phonedialerr.activity.MainActivity.Companion.bottomNavigationView
import com.phonecontactscall.contectapp.phonedialerr.activity.MainActivity.Companion.mainActivity
import com.phonecontactscall.contectapp.phonedialerr.Glob.check_theme
import com.phonecontactscall.contectapp.phonedialerr.Glob.setUsingSystemTheme
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.databinding.FragmentSettingBinding
import com.phonecontactscall.contectapp.phonedialerr.Glob


class Setting_Fragment : BaseFragment<FragmentSettingBinding>() {


    override fun getViewBinding(): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun initView() {


        Glob.isseting = true

        val versionName = getAppVersionName()
        binding.txtVersion.text = " $versionName"

        binding.relBlocker.setOnClickListener(View.OnClickListener {

            val intent = Intent(requireActivity(), BlockContectActivity::class.java)
            startActivity(intent)
        })

        binding.relLang.setOnClickListener(View.OnClickListener {

            val intent = Intent(requireActivity(), Langugae_Activity::class.java)
            startActivity(intent)
        })

        binding.relTheme.setOnClickListener(View.OnClickListener {




            val them_dialog = Dialog(requireActivity())

            them_dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            them_dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            them_dialog.setContentView(R.layout.dialog_theme)

            them_dialog.window!!.setGravity(Gravity.CENTER)
            them_dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            them_dialog.window!!.getAttributes().windowAnimations = R.style.dialog_theme
            them_dialog.setCancelable(true)
            them_dialog.setCanceledOnTouchOutside(true)
            them_dialog.show()


            val lin_dark = them_dialog.findViewById<RelativeLayout>(R.id.lin_dark)
            val radio_select_dark = them_dialog.findViewById<RadioButton>(R.id.radio_select_dark)
            val radio_select_light = them_dialog.findViewById<RadioButton>(R.id.radio_select_light)
            val radio_select_system = them_dialog.findViewById<RadioButton>(R.id.radio_select_system)
            val lin_light = them_dialog.findViewById<RelativeLayout>(R.id.lin_light)
            val lin_system = them_dialog.findViewById<RelativeLayout>(R.id.lin_system)
            val img_close_theme = them_dialog.findViewById<ImageView>(R.id.img_close_theme)

            img_close_theme.setOnClickListener { v ->
                them_dialog.dismiss()
            }


            when (check_theme()) {
                "light" -> radio_select_light.isChecked = true
                "dark" -> radio_select_dark.isChecked = true
                "System_theme" -> radio_select_system.isChecked = true
            }

            lin_dark.setOnClickListener {


                Handler(Looper.getMainLooper()).postDelayed({
                    setUsingSystemTheme("dark")
                    bottomNavigationView.selectedItemId = R.id.item_call
                    if (them_dialog.isShowing) {
                        them_dialog.dismiss()
                    }

                    Glob.isthemechnage = true
                    recreateActivitySafely()

                }, 150)



            }

            lin_light.setOnClickListener(View.OnClickListener {

                Handler(Looper.getMainLooper()).postDelayed({
                    setUsingSystemTheme("light")
                    bottomNavigationView.selectedItemId = R.id.item_call
                    if (them_dialog.isShowing) {
                        them_dialog.dismiss()
                    }

                    Glob.isthemechnage = true
                    recreateActivitySafely()

                }, 150)



            })

            lin_system.setOnClickListener(View.OnClickListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    setUsingSystemTheme("System_theme")
                    bottomNavigationView.selectedItemId = R.id.item_call
                    if (them_dialog.isShowing) {
                        them_dialog.dismiss()
                    }

                    Glob.isthemechnage = true
                    recreateActivitySafely()
                }, 150)


            })



        })

        binding.relShare.setOnClickListener(View.OnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                var shareMessage = """
 ${resources.getString(R.string.share_App_msg)}
"""
                shareMessage =
                    """
            ${shareMessage}https://play.google.com/store/apps/details?id=${requireActivity().packageName}
            
            
            """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
            }


        })

        binding.relRateus.setOnClickListener(View.OnClickListener {

            try {
                val sb = StringBuilder()
                sb.append("market://details?id=")
                sb.append(requireActivity().packageName)
                startActivity(Intent("android.intent.action.VIEW", Uri.parse(sb.toString())))
            } catch (unused: ActivityNotFoundException) {
                val sb2 = StringBuilder()
                sb2.append("https://play.google.com/store/apps/details?id=")
                sb2.append(requireActivity().packageName)
                startActivity(Intent("android.intent.action.VIEW", Uri.parse(sb2.toString())))
            }

        })

        binding.relPolicy.setOnClickListener(View.OnClickListener {
            try {
                val sb = StringBuilder()
                sb.append("https://sites.google.com/view/eveningstars507/home")
                startActivity(Intent("android.intent.action.VIEW", Uri.parse(sb.toString())))
            } catch (unused: ActivityNotFoundException) {
                val sb2 = StringBuilder()
                sb2.append("https://sites.google.com/view/eveningstars507/home")
                startActivity(Intent("android.intent.action.VIEW", Uri.parse(sb2.toString())))
            }


        })
    }




    private fun recreateActivitySafely() {
        mainActivity.recreate()

    }
    private fun getAppVersionName(): String {
        return try {
            val packageInfo: PackageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "N/A"
        }
    }
    override fun initObserve() {
    }


}