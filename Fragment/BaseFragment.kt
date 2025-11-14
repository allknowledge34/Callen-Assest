package com.phonecontactscall.contectapp.phonedialerr.Fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.phonecontactscall.contectapp.phonedialerr.Glob
//import com.phonecontactscall.contectapp.phonedialer.RateDialog
import com.phonecontactscall.contectapp.phonedialerr.activity.MainActivity.Companion.rel_data
import com.phonecontactscall.contectapp.phonedialerr.activity.MainActivity.Companion.rel_default

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    lateinit var binding: B

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = getViewBinding()



        if (Build.VERSION.SDK_INT >= 29) {

            if (!Glob.isDefaultDialer(activity)) {
                rel_data.visibility = View.GONE
                rel_default.visibility = View.VISIBLE

            } else {
                initView()

            }

        } else {

                initView()
        }



        initObserve()
        return binding.root

    }

    abstract fun getViewBinding(): B
    abstract fun initView()
    abstract fun initObserve()
}

