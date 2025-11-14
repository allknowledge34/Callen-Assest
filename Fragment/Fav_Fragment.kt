package com.phonecontactscall.contectapp.phonedialerr.Fragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.provider.ContactsContract
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDao
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDatabase
import com.phonecontactscall.contectapp.phonedialerr.Glob.edit_number_fav_detail
import com.phonecontactscall.contectapp.phonedialerr.Glob.fav_remove
import com.phonecontactscall.contectapp.phonedialerr.Model.FavContact
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactRepository
import com.phonecontactscall.contectapp.phonedialerr.adapter.FavAdapter
import com.phonecontactscall.contectapp.phonedialerr.databinding.FragmentFavBinding
import com.phonecontactscall.contectapp.phonedialerr.Glob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Fav_Fragment : BaseFragment<FragmentFavBinding>() {

    private var arrayList = ArrayList<FavContact>()
    lateinit var adapter: FavAdapter
    private lateinit var contactDao: ContactDao
    private val contactRepository = ContactRepository()

    companion object{
lateinit var Fav_Fragment : Fav_Fragment

    }

    override fun getViewBinding(): FragmentFavBinding {
     return FragmentFavBinding.inflate(layoutInflater)
    }

    override fun initView() {
        Fav_Fragment = this
        val database = ContactDatabase.getDatabase(requireActivity())
        contactDao = database.contactDao()
        CoroutineScope(Dispatchers.IO).launch {
            val favouriteContacts = contactDao.getFavouriteContacts()
            arrayList.clear()
            arrayList.addAll(favouriteContacts)
            getFavoriteContacts()
            arrayList.sortByDescending { it.timestamp }
            requireActivity().runOnUiThread(Runnable {

                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val theme = Glob.check_theme()


                when (theme) {
                    "light" ->  binding.imgFavIcon.setImageResource(R.drawable.ic_no_favorite)

                    "dark" -> binding.imgFavIcon.setImageResource(R.drawable.ic_no_fav_dark)
                    "System_theme" -> if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                        binding.imgFavIcon.setImageResource(R.drawable.ic_no_fav_dark)
                    } else {
                        binding.imgFavIcon.setImageResource(R.drawable.ic_no_favorite)
                    }
                }



                binding.revFev.layoutManager = LinearLayoutManager(requireActivity())
                adapter = FavAdapter(requireActivity(), arrayList)


                binding.revFev.adapter = adapter


                if(arrayList.isEmpty() || arrayList.size==0){
                    binding.relEmptyAll.visibility = View.VISIBLE
                }else{
                    binding.relEmptyAll.visibility = View.GONE
                }
            })

        }



    }

    override fun onResume() {
        super.onResume()

        if( edit_number_fav_detail ||fav_remove){
            fav_remove = false
            edit_number_fav_detail = false

            lifecycleScope.launch {
                contactRepository.fetchContacts(requireActivity())
            }
            CoroutineScope(Dispatchers.IO).launch {
                val favouriteContacts = contactDao.getFavouriteContacts()
                arrayList.clear()
                arrayList.addAll(favouriteContacts)
                getFavoriteContacts()

                requireActivity().runOnUiThread(Runnable {
                    binding.revFev.layoutManager = LinearLayoutManager(requireActivity())
                    adapter = FavAdapter(requireActivity(), arrayList)

                    binding.revFev.adapter = adapter


                    if(arrayList.isEmpty() || arrayList.size==0){
                        binding.relEmptyAll.visibility = View.VISIBLE
                    }else{
                        binding.relEmptyAll.visibility = View.GONE
                    }
                })

            }
        }else{

//

        }

    }

    override fun initObserve() {
    }
    @SuppressLint("Range")
    fun getFavoriteContacts() {
        val mArrayList = ArrayList<FavContact>()

        val queryUri = ContactsContract.Contacts.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
        )

        val selection = ContactsContract.Contacts.STARRED + "='1'"
        val cursor = requireActivity().contentResolver.query(queryUri, projection, selection, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val contact = try {
                    val contactID = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                    val fav = it.getInt(it.getColumnIndexOrThrow(ContactsContract.Contacts.STARRED))
                    val timestamp = it.getLong(it.getColumnIndexOrThrow(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))

                    val isFav = fav == 1
                    val phoneNumber = getPhoneNumber(contactID)
                    val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactID).toString()
                    FavContact(contactID.toLong(), name, phoneNumber, uri, isFav,timestamp)
                } catch (e: Exception) {
                    null
                }
                contact?.apply {
                    mArrayList.add(contact)
                }
            }
            arrayList = arrayListOf()
            arrayList.addAll(mArrayList)

        }
    }

    private fun getPhoneNumber(contactId: String): String {
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val phoneProjection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
        val phoneSelectionArgs = arrayOf(contactId)
        val contentResolver = context?.contentResolver ?: return ""
        val phoneCursor = contentResolver.query(phoneUri, phoneProjection, phoneSelection, phoneSelectionArgs, null)

        phoneCursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }

        return ""
    }


}