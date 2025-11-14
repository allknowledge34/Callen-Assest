package com.phonecontactscall.contectapp.phonedialerr.adapter

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper.callPhoneClick
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDao
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDatabase
import com.phonecontactscall.contectapp.phonedialerr.Model.ContactModel
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.databinding.ItemDialpadContactBinding
import com.makeramen.roundedimageview.RoundedImageView
import java.io.IOException


class DailAdapter(
    private val activity: Activity,
    private var arrayList: MutableList<ContactModel>
) : RecyclerView.Adapter<DailAdapter.ViewHolder>() {
     lateinit var contactDao: ContactDao
    val  contactHelper : ContentResolver
    private var textToHighlight = ""
    init {

        val database = ContactDatabase.getDatabase(activity)
        contactDao = database.contactDao()
        contactHelper = activity.contentResolver
        notifyDataSetChanged()
    }

    fun textChanged(text: String) {
        this.textToHighlight = text.removePrefix(("+" as CharSequence)!!).removePrefix(
            ("*" as CharSequence)!!
        )
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDialpadContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolder(private val binding: ItemDialpadContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ContactModel) {

            binding.tvName.text = model.name
            binding.tvNumber.text = model.number
            val test = model.name
            val first = test[0]




            retrieveContactPhoto(activity!!, model!!, binding.ivImage,position)

            binding.txtCharcter.text = first.toString()
            binding.relContect.setOnClickListener(View.OnClickListener {

            })


            binding.imgCall.setOnClickListener(View.OnClickListener {
                callPhoneClick(activity, model.number)

            })
        }
    }


    fun retrieveContactPhoto(
        context: Activity,
        recentModel: ContactModel,
        iv_image: RoundedImageView,
        position: Int
    ): Bitmap {
        val contentResolver = context.contentResolver
        var contactId: String = recentModel.id.toString()
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(recentModel.number)
        )

        val projection =
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)

        val cursor =
            contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            cursor.close()
        }
        if (position % 7 == 0) {
            iv_image.setImageResource(R.drawable.ic_contact_1);
        } else if (position % 7 == 1) {
            iv_image.setImageResource(R.drawable.ic_contact_2);

        } else if (position % 7 == 2) {
            iv_image.setImageResource(R.drawable.ic_contact_3);

        } else if (position % 7 == 3) {
            iv_image.setImageResource(R.drawable.ic_contact_4);

        } else if (position % 7 == 4) {
            iv_image.setImageResource(R.drawable.ic_contact_5);

        } else if (position % 7 == 5) {
            iv_image.setImageResource(R.drawable.ic_contact_6);

        } else if (position % 7 == 6) {
            iv_image.setImageResource(R.drawable.ic_contact_7);

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

        var photo = BitmapFactory.decodeResource(context.resources,imageRes)

        try {
            if (contactId != null && contactId.toInt() != -1) {
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    context.contentResolver,
                    ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        contactId.toLong()
                    )
                )

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()

                } else {

                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        iv_image.setImageBitmap(photo)
        iv_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        iv_image.setCornerRadius(30f)
        iv_image.setOval(true);
        return photo
    }

    fun filterlist(filterlist: ArrayList<ContactModel>) {
        arrayList = filterlist
        notifyDataSetChanged()
    }





}
