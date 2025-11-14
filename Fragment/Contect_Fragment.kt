package com.phonecontactscall.contectapp.phonedialerr.Fragment

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phonecontactscall.contectapp.phonedialerr.Glob.all_contect_list
import com.phonecontactscall.contectapp.phonedialerr.Glob.edit_number
import com.phonecontactscall.contectapp.phonedialerr.Glob.edit_number_fav
import com.phonecontactscall.contectapp.phonedialerr.Model.ContactModel
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactRepository
import com.phonecontactscall.contectapp.phonedialerr.adapter.ContectAdapter
import com.phonecontactscall.contectapp.phonedialerr.databinding.FragmentContectBinding
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import kotlinx.coroutines.launch


class Contect_Fragment : BaseFragment<FragmentContectBinding>() {

    private lateinit var adapter: ContectAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private var contactRepository =  ContactRepository()

    companion object{
        var staticRecyclerView: RecyclerView? = null


    }

    override fun getViewBinding(): FragmentContectBinding {

     return FragmentContectBinding.inflate(layoutInflater)
    }

    override fun initView() {


        contactRepository = ContactRepository()
        viewLifecycleOwner.lifecycleScope.launch {
            contactRepository.contactsStateFlow.collect { contactItemList ->
                linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.revContect.layoutManager = linearLayoutManager
                adapter = ContectAdapter(requireActivity(), all_contect_list)
                binding.revContect.adapter = adapter
                staticRecyclerView = binding.revContect


                all_contect_list.sortBy { it.name.toUpperCase() }
                updateGUi(all_contect_list)
                try {
                    if (all_contect_list.isNotEmpty()) {
                        try {
                            binding.fastscroller.setupWithRecyclerView(
                                binding.revContect,
                                { position ->
                                    if (position >= 0 && position < all_contect_list.size) {
                                        val item = all_contect_list[position]
                                        FastScrollItemIndicator.Text(item.name.substring(0, 1).uppercase())
                                    } else {
                                        FastScrollItemIndicator.Text("#")
                                    }
                                }
                            )

                            binding.fastscroller.itemIndicatorSelectedCallbacks += object : FastScrollerView.ItemIndicatorSelectedCallback {
                                override fun onItemIndicatorSelected(
                                    indicator: FastScrollItemIndicator,
                                    indicatorCenterY: Int,
                                    itemPosition: Int
                                ) {
                                    if (itemPosition < 0 || itemPosition >= all_contect_list.size) {
                                        return
                                    }

                                    binding.revContect.stopScroll()

                                    val offset = 50
                                    linearLayoutManager.scrollToPositionWithOffset(itemPosition, offset)
                                }
                            }
                        } catch (e: Exception) {
                        }
                    } else {
                    }
                } catch (e: Exception) {
                }



            }
        }




    }

    override fun onResume() {
        super.onResume()


        if(edit_number || edit_number_fav){

            edit_number= false
            edit_number_fav= false
            retrieveContactsList()
        }

    }

    override fun initObserve() {
    }
    private fun retrieveContactsList() {
        contactRepository = ContactRepository()
        viewLifecycleOwner.lifecycleScope.launch {
            contactRepository.fetchContacts(requireActivity())

                contactRepository.contactsStateFlow.collect { contactItemList ->
                    linearLayoutManager = LinearLayoutManager(requireActivity())
                    binding.revContect.layoutManager = linearLayoutManager
                    adapter = ContectAdapter(requireActivity(), all_contect_list)
                    all_contect_list.sortBy { it.name.toUpperCase() }
                    binding.revContect.adapter = adapter
                    updateGUi(all_contect_list)

                    try {
                        if (all_contect_list.isNotEmpty()) {
                            try {
                                binding.fastscroller.setupWithRecyclerView(
                                    binding.revContect,
                                    { position ->
                                        if (position >= 0 && position < all_contect_list.size) {
                                            val item = all_contect_list[position]
                                            FastScrollItemIndicator.Text(item.name.substring(0, 1).uppercase())
                                        } else {
                                            FastScrollItemIndicator.Text("#")
                                        }
                                    }
                                )

                                binding.fastscroller.itemIndicatorSelectedCallbacks += object : FastScrollerView.ItemIndicatorSelectedCallback {
                                    override fun onItemIndicatorSelected(
                                        indicator: FastScrollItemIndicator,
                                        indicatorCenterY: Int,
                                        itemPosition: Int
                                    ) {
                                        if (itemPosition < 0 || itemPosition >= all_contect_list.size) {
                                            return
                                        }

                                        binding.revContect.stopScroll()

                                        val offset = 50
                                        linearLayoutManager.scrollToPositionWithOffset(itemPosition, offset)
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        } else {
                        }
                    } catch (e: Exception) {
                    }


            }
        }


    }




    private fun updateGUi(arrayList: ArrayList<ContactModel>) {


        if (arrayList.isEmpty() || arrayList.size == 0) {
            binding.relEmptyAll.visibility = View.VISIBLE
            binding.revContectData.visibility = View.GONE
        } else {
            binding.relEmptyAll.visibility = View.GONE
            binding.revContectData.visibility = View.VISIBLE
        }

    }

}