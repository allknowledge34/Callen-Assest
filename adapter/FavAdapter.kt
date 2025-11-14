package com.phonecontactscall.contectapp.phonedialerr.adapter

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.phonecontactscall.contectapp.phonedialerr.activity.CallHistoryActivity
import com.phonecontactscall.contectapp.phonedialerr.activity.FavContectDetialActivty
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDao
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDatabase
import com.phonecontactscall.contectapp.phonedialerr.Fragment.Fav_Fragment.Companion.Fav_Fragment
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Model.FavContact
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.databinding.ItemFavBinding
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class FavAdapter(
    private val activity: Activity,
    private val arrayList: ArrayList<FavContact>
) : RecyclerView.Adapter<FavAdapter.ViewHolder>() {
    lateinit var contactDao: ContactDao
    val contactHelper: ContentResolver
    var main_popup: PopupWindow? = null

    init {

        val database = ContactDatabase.getDatabase(activity)
        contactDao = database.contactDao()
        contactHelper = activity.contentResolver
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolder(private val binding: ItemFavBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: FavContact) {

            binding.tvName.text = model.name
            val test = model.name
            val first = test[0]
            binding.txtCharcter.text = first.toString()


            retrieveContactPhoto(activity!!, model!!,binding.ivImage,position)
            binding.relContect.setOnClickListener(View.OnClickListener {

                val gson = Gson()
                val intent = Intent(activity, FavContectDetialActivty::class.java)
                intent.putExtra("identifier_fav", gson.toJson(model))
                activity.startActivity(intent)
            })

            binding.imgMore.setOnClickListener(View.OnClickListener {

                showPopup(binding.imgMore, model)
            })
        }
    }

    fun showPopup(v: View, callLog: FavContact) {

        val inflater: LayoutInflater = activity.layoutInflater

        val view: View = inflater.inflate(R.layout.popup_layout_fav, null)
        main_popup = PopupWindow(activity)
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

        val displayMetrics = activity.resources.displayMetrics
        val height = (displayMetrics.heightPixels * 2) / 3.5
        println("Height:$height")

        if (positionOfIcon > height) {
            main_popup!!.showAsDropDown(v, 0, -500)
        } else {
            main_popup!!.showAsDropDown(v, 0, 0)
        }

        val rel_contect_detail: RelativeLayout = view.findViewById(R.id.rel_contect_detail)
        val rel_fav: RelativeLayout = view.findViewById(R.id.rel_fav)
        val rel_history: RelativeLayout = view.findViewById(R.id.rel_history)

        rel_fav.setOnClickListener(View.OnClickListener {

            toggleFavouriteStatus(callLog, rel_fav)
            main_popup!!.dismiss()
        })

        rel_contect_detail.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()


            val gson = Gson()
            val intent = Intent(activity, FavContectDetialActivty::class.java)
            intent.putExtra("identifier_fav", gson.toJson(callLog))
            activity.startActivity(intent)

        })


        rel_history.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()
            val gson = Gson()
            val intent = Intent(activity, CallHistoryActivity::class.java)
            intent.putExtra("identifier", gson.toJson(callLog))
            activity.startActivity(intent)


        })


    }


    fun toggleFavouriteStatus(contact: FavContact, img_fav: RelativeLayout) {
        CoroutineScope(Dispatchers.IO).launch {
            contact.isFav = !contact.isFav
            val existingContact = contactDao.getFavContactById(contact.id!!)
            if (existingContact != null) {
                if (existingContact.isFav) {

                    CoroutineScope(Dispatchers.IO).launch {
                        contactDao.Delete_fav(contact!!.name)
                    }
                    activity.runOnUiThread {

                        Glob.showToast(activity, activity.getString(R.string.toast_unfav_sucess))


                        val position = arrayList.indexOf(contact)


                        if (position != -1) {
                            arrayList.removeAt(position)
                            notifyItemRemoved(position)
                        }

                        try {
                            if (arrayList.isEmpty() || arrayList.size == 0) {
                                Fav_Fragment.binding.relEmptyAll.visibility = View.VISIBLE
                                Fav_Fragment.binding.revFev.visibility = View.GONE
                            } else {
                                Fav_Fragment.binding.relEmptyAll.visibility = View.GONE
                                Fav_Fragment.binding.revFev.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                        }

                    }


                }

            } else {
                withContext(Dispatchers.Main) {


                    try {
                        if (contact.isFav) {

                            val contentValues = ContentValues().apply {
                                put(
                                    ContactsContract.Contacts.STARRED,
                                    1
                                )
                            }

                            val uri = Uri.withAppendedPath(
                                ContactsContract.Contacts.CONTENT_URI, contact.id.toString()
                            )

                            val rowsUpdated =
                                activity.contentResolver.update(uri, contentValues, null, null)

                            if (rowsUpdated > 0) {
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Failed to mark contact as favorite",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            withContext(Dispatchers.Main) {

                                Glob.showToast(activity, activity.getString(R.string.toast_fav_sucess))



                            }
                        } else {
                            val contentValues = ContentValues().apply {
                                put(
                                    ContactsContract.Contacts.STARRED,
                                    0
                                )
                            }

                            val uri = Uri.withAppendedPath(
                                ContactsContract.Contacts.CONTENT_URI, contact.id.toString()
                            )

                            val rowsUpdated =
                                activity.contentResolver.update(uri, contentValues, null, null)
                            CoroutineScope(Dispatchers.IO).launch {
                                contactDao.Delete_fav(contact!!.name)
                            }
                            if (rowsUpdated > 0) {
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Failed to mark contact as favorite",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            withContext(Dispatchers.Main) {

                                Glob.showToast(activity, activity.getString(R.string.toast_unfav_sucess))



                            }
                        }
                    } catch (e: Exception) {
                    }
                    val position = arrayList.indexOf(contact)

                    if (position != -1) {
                        arrayList.removeAt(position)
                        notifyItemRemoved(position)
                    }

                    try {
                        if (arrayList.isEmpty() || arrayList.size == 0) {
                            Fav_Fragment.binding.relEmptyAll.visibility = View.VISIBLE
                            Fav_Fragment.binding.revFev.visibility = View.GONE
                        } else {
                            Fav_Fragment.binding.relEmptyAll.visibility = View.GONE
                            Fav_Fragment.binding.revFev.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                    }

                }
            }
        }
    }

    fun retrieveContactPhoto(
        context: Activity,
        recentModel: FavContact,
        iv_image: RoundedImageView,
        position: Int
    ): Bitmap {
        val contentResolver = context.contentResolver
        var contactId: String? = null
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(recentModel.number))

        try {
            val projection = arrayOf(ContactsContract.PhoneLookup._ID)

            val cursor = contentResolver.query(uri, projection, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    contactId = it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
                }
            }
        } catch (e: Exception) {
        }

        val contactImages = arrayOf(
            R.drawable.ic_contact_1,
            R.drawable.ic_contact_2,
            R.drawable.ic_contact_3,
            R.drawable.ic_contact_4,
            R.drawable.ic_contact_5,
            R.drawable.ic_contact_6,
            R.drawable.ic_contact_7
        )

        val imageRes = contactImages[position % contactImages.size]
        var photo = BitmapFactory.decodeResource(context.resources, imageRes)

        try {
            if (!contactId.isNullOrEmpty() && contactId!!.toIntOrNull() != null) {
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    context.contentResolver,
                    ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        contactId!!.toLong()
                    )
                )

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        iv_image.setImageBitmap(photo)
        iv_image.scaleType = ImageView.ScaleType.CENTER_CROP
        iv_image.setCornerRadius(30f)
        iv_image.setOval(true)

        return photo
    }






}
