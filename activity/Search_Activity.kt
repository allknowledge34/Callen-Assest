package com.phonecontactscall.contectapp.phonedialerr.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper.callPhoneClick
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Glob.callend
import com.phonecontactscall.contectapp.phonedialerr.Glob.serach_number
import com.phonecontactscall.contectapp.phonedialerr.Model.ContactModel
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R

import com.phonecontactscall.contectapp.phonedialerr.adapter.SearchAdapter
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivitySearchBinding
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactItem
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactRepository
import com.phonecontactscall.contectapp.phonedialerr.activity.MainActivity.Companion.mainActivity
import kotlinx.coroutines.launch


class Search_Activity : BaseActivity<ActivitySearchBinding>() {

    private val arrayList = mutableListOf<ContactModel>()
    private lateinit var adapter: SearchAdapter
    private val contactRepository =  ContactRepository()

    companion object{
       lateinit  var edit_serch_contect : EditText
    }
    
    override fun getViewBinding(): ActivitySearchBinding {
       return ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
        super.onApplyThemeResource(theme, resid, first)

        try {
            if(edit_serch_contect!=null){



                Handler().postDelayed(Runnable {
                    if(!edit_serch_contect.text.isEmpty()){


                        edit_serch_contect!!.setText("")
                        edit_serch_contect.setText("")
                        edit_serch_contect.text.clear()


                    }
                    hideKeyboard(edit_serch_contect,this)

                },500)

            }
        } catch (e: Exception) {
        }
    }

    override fun initView() {

        val color = Glob.getResourceFromAttr(R.attr.navigation_bg_color,this)
        val color1 = Glob.getResourceFromAttr(R.attr.default_bg_color,this)
//
        window.statusBarColor = color
        window.navigationBarColor = color1

        Glob.SetStatusbarColor(window)


        edit_serch_contect = findViewById<EditText>(R.id.edit_serch_contect)
        val isReadContactsGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

        val isWriteContactsGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
        if (isReadContactsGranted && isWriteContactsGranted) {
            retrieveContactsList()
        }




        edit_serch_contect!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialpadValueChanged(s.toString());

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.imgClose.setOnClickListener { v ->

            hideKeyboard(view = v,this)
            edit_serch_contect!!.text.clear()
            edit_serch_contect!!.text.equals(" ")
        }


        binding.imgBack.setOnClickListener(View.OnClickListener {
             onBackPressedDispatcher.onBackPressed()
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "Search_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("Search_Act_onBackpress", Bundle())
                try {
                    if(edit_serch_contect!=null){
                        hideKeyboard(edit_serch_contect,this@Search_Activity)
                    }
                } catch (e: Exception) {
                }


                if(edit_serch_contect!!.text.toString().isNotEmpty()){
                    edit_serch_contect!!.text.clear()
                    edit_serch_contect!!.text.equals(" ")
                    binding.revSearchContect.layoutManager = LinearLayoutManager(this@Search_Activity)
                    adapter = SearchAdapter(this@Search_Activity, arrayList) { call ->
                        hideKeyboard(edit_serch_contect,this@Search_Activity)

                        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager?

                        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                        val simState = telephonyManager?.simState

                        if (simState == TelephonyManager.SIM_STATE_ABSENT) {
                            Glob.showToast(this@Search_Activity, getString(R.string.str_no_sim))
                            if(edit_serch_contect!=null){

                                edit_serch_contect!!.setText("")
                            }
                        }else{
                            callPhoneClick(this@Search_Activity, call.number)

                            Handler().postDelayed({
                                if(edit_serch_contect!=null){

                                    edit_serch_contect!!.setText("")
                                }
                            },500)

                        }








                    }
                    binding.revSearchContect.adapter = adapter
                    adapter.notifyDataSetChanged()
                }else{
                   finish()

                }
            }

        })


        Log.e("Contect_Event--", "Search_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Search_Act_onCreate", Bundle())
    }

    override fun onResume() {
        super.onResume()



        val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val ratingShown = sharedPrefs.getBoolean("rate_dialog_shown_caller", false)



        if (!ratingShown) {
            if (callend) {
                if (Glob.israting == false) {

                    mainActivity.requestReviewInfo()

                }

                sharedPrefs.edit().putBoolean("rate_dialog_shown_caller", true).apply()
            }
        }

        if(serach_number){
            serach_number= false;
            retrieveContactsList()
        }

    }


    fun hideKeyboard(view: View, context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun dialpadValueChanged(str: String) {
        var z = false
        z = str.trim().isNotEmpty()
        if (z) {
            filter(str)
        } else {

            if (!isFinishing && !isDestroyed) {
                retrieveContactsList()
            }

        }
    }
    fun filter(text: String) {
        val filterList = ArrayList<ContactModel>()


        for (item in arrayList) {

            if (item.name.lowercase().contains(text.toLowerCase())|| item.number.toString().trim().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item)

            }else{

            }
            adapter.filterlist(filterList)
        }



        if (filterList.isEmpty()) {
            binding.relEmptyAll.visibility = View.VISIBLE
            binding.revSearchContect.visibility = View.GONE
            return
        }
        binding.relEmptyAll.visibility = View.GONE
        binding.revSearchContect.visibility = View.VISIBLE

    }


    private fun updateGUi(arrayList: MutableList<ContactModel>) {

        if (arrayList.isEmpty()) {
            binding.relEmptyAll.visibility = View.VISIBLE
            binding.revSearchContect.visibility = View.GONE
            return
        }


        binding.relEmptyAll.visibility = View.GONE
        binding.revSearchContect.visibility = View.VISIBLE
    }
    private fun retrieveContactsList() {

        val progressDialog = ProgressDialog(this).apply {
            setMessage(getString(R.string.str_load_contect))
            setCancelable(false)
        }
        try {
            if (! isFinishing()) {
                progressDialog.show()
            }
        } catch (e: Exception) {
        }
        lifecycleScope.launch {
            try {
                contactRepository.fetchContacts(this@Search_Activity)

                contactRepository.contactsStateFlow.collect { contactItemList ->
                    arrayList.clear()
                    arrayList.addAll(contactItemList.filterIsInstance<ContactItem.Contact>().map { it.itemContact })
                    progressDialog.dismiss()

                    binding.revSearchContect.layoutManager = LinearLayoutManager(this@Search_Activity)
                    adapter = SearchAdapter(this@Search_Activity, arrayList){ call ->
                        hideKeyboard(edit_serch_contect,this@Search_Activity)


                        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                        val simState = telephonyManager?.simState

                        if (simState == TelephonyManager.SIM_STATE_ABSENT) {
                            Glob.showToast(this@Search_Activity, getString(R.string.str_no_sim))

                            if(!edit_serch_contect.text.isEmpty()){
                                if(edit_serch_contect!=null){

                                    edit_serch_contect!!.setText("")
                                }
                            }

                        }else{
                            callPhoneClick(this@Search_Activity, call.number)

                            Handler().postDelayed({
                                if(!edit_serch_contect.text.isEmpty()){
                                    if(edit_serch_contect!=null){

                                        edit_serch_contect!!.setText("")
                                    }
                                }
                            },500)

                        }






                    }
                    binding.revSearchContect.adapter = adapter
                    Handler().postDelayed(Runnable {
                        showKeyboard(this@Search_Activity,edit_serch_contect)
                        edit_serch_contect!!.requestFocus()

                    },500)

                    updateGUi(arrayList)

                }
            } catch (e: Exception) {

               finish()

                hideKeyboard(view = edit_serch_contect,this@Search_Activity)
            }
        }


    }
    fun showKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

}