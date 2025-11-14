package com.phonecontactscall.contectapp.phonedialerr.adapter

import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.BlockedNumberContract
import android.provider.ContactsContract
import android.text.format.DateUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper.callPhoneClick
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDao
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDatabase
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Model.DateHeader
import com.phonecontactscall.contectapp.phonedialerr.Model.RecentModel
import com.phonecontactscall.contectapp.phonedialerr.Model.RecyclerViewItem
import com.phonecontactscall.contectapp.phonedialerr.R
import com.makeramen.roundedimageview.RoundedImageView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale


class RecentCallAdapter(
    private val activity: FragmentActivity?,
    private val itemClickListener: OnItemClickListener,
    private val arrayList: ArrayList<RecyclerViewItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var contactDao: ContactDao
    val contactHelper: ContentResolver

    companion object {
        private const val VIEW_TYPE_DATE_HEADER = 0
        private const val VIEW_TYPE_CALL_LOG = 1
        private const val DATE_FORMAT = "MMM dd, yyyy" //2021-05-20T11:28:24
        private const val TODAY = "today"
        private const val YESTERDAY = "yesterday"
    }

    override fun getItemViewType(position: Int): Int {
        return when (arrayList[position]) {
            is DateHeader -> VIEW_TYPE_DATE_HEADER
            is RecentModel -> VIEW_TYPE_CALL_LOG
        }
    }

    interface OnItemClickListener {
        fun onItemClick(callLog: RecentModel, itemView: View)
    }

    init {

        val database = ContactDatabase.getDatabase(activity!!)
        contactDao = database.contactDao()
        contactHelper = activity.contentResolver
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }

            VIEW_TYPE_CALL_LOG -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recent_contect, parent, false)
                CallLogViewHolder(view)
            }

            else -> {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recent_contect, parent, false)
                CallLogViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateHeaderViewHolder -> holder.bind(arrayList[position] as DateHeader, position)
            is CallLogViewHolder -> holder.bind(arrayList[position] as RecentModel, position)
        }
    }

    inner class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(dateHeader: DateHeader, position: Int) {


            itemView.findViewById<TextView>(R.id.txt_hearder).text = dateHeader.date
        }
    }

    inner class CallLogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @RequiresApi(Build.VERSION_CODES.O)

        fun bind(model: RecentModel, position: Int) {
            val tvDate = itemView.findViewById<TextView>(R.id.tv_date)
            val tv_number = itemView.findViewById<TextView>(R.id.tv_number)
            val img_type = itemView.findViewById<ImageView>(R.id.img_type)
            val img_more = itemView.findViewById<ImageView>(R.id.img_more)
            val tvName = itemView.findViewById<TextView>(R.id.tv_name)
            val iv_image = itemView.findViewById<RoundedImageView>(R.id.iv_image)
            val rel_selected = itemView.findViewById<RelativeLayout>(R.id.rel_selected)
            if (model.count > 1) {
                tvName.text = model.name + " " + "(" + model.count + ")"

            } else {
                tvName.text = model.name
            }



            tv_number.text = model.number
            tvDate.text = model.formattedDatetime


            model.formattedDate.formatStringDate("MMM dd, yyyy", DATE_FORMAT)

            var isTodayYesterday =
                model.formattedDate.getYesterdayToday(model.formattedDate, DATE_FORMAT)
            val currentString = model.formattedDatetime
            val separated = currentString.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            separated[0]
            separated[1]

            if (isTodayYesterday.equals("today") || isTodayYesterday.equals("yesterday")) {
                tvDate.text = separated[1]
            } else {
                tvDate.text = model.formattedDatetime
            }

            when (isTodayYesterday) {
                TODAY -> {
                }

                YESTERDAY -> {
                }
            }
            val valueOf: Int? = if (model != null) Integer.valueOf(model.type) else null

            if (valueOf != null && valueOf == 2) {
                img_type.setImageResource(R.drawable.ic_outgoing_calls)
            } else if (valueOf != null && valueOf == 3) {
                img_type.setImageResource(R.drawable.ic_call_missed)
            } else if (valueOf != null && valueOf == 1) {
                img_type.setImageResource(R.drawable.ic_call_incoming)
            } else if (valueOf != null && valueOf == 6) {
                img_type.setImageResource(R.drawable.ic_block)
            }


            retrieveContactPhoto(activity!!, model!!,iv_image,position)

            rel_selected.setOnClickListener(View.OnClickListener {

                val isBlocked = isNumberBlocked(activity!!, model!!.number)

                if (isBlocked) {

                    val block_call_dialog = Dialog(activity)

                    block_call_dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                    block_call_dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    block_call_dialog.setContentView(R.layout.dialog_block_call)

                    block_call_dialog.window!!.setGravity(Gravity.CENTER)
                    block_call_dialog.window!!.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    block_call_dialog.window!!.getAttributes().windowAnimations = R.style.dialog_theme
                    block_call_dialog.setCancelable(true)
                    block_call_dialog.setCanceledOnTouchOutside(true)
                    block_call_dialog.show()

                    val txt_number = block_call_dialog.findViewById<TextView>(R.id.txt_number)
                    val txt_cancel = block_call_dialog.findViewById<TextView>(R.id.txt_cancel)
                    val text_call = block_call_dialog.findViewById<TextView>(R.id.text_call)

                    txt_number.text = model.number

                    txt_cancel.setOnClickListener(View.OnClickListener {

                        block_call_dialog.dismiss()
                    })

                    text_call.setOnClickListener(View.OnClickListener {
                        block_call_dialog.dismiss()
                        Glob.call_pass = true
                        callPhoneClick(activity, model!!.number)
                    })


                } else {
                    Glob.call_pass = true
                    callPhoneClick(activity, model!!.number)

                }

            })

            img_more.setOnClickListener(View.OnClickListener {


                itemClickListener.onItemClick(model, img_more)


            })


        }
    }
    fun retrieveContactPhoto(
        context: FragmentActivity,
        recentModel: RecentModel,
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
    @RequiresApi(Build.VERSION_CODES.N)
    private fun isNumberBlocked(context: Context, number: String): Boolean {
        return try {
            val uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI
            val projection = arrayOf(BlockedNumberContract.BlockedNumbers.COLUMN_ID)
            val selection = "${BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER} = ?"
            val selectionArgs = arrayOf(number)

            context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                ?.use { cursor ->
                    cursor.moveToFirst()
                } ?: false
        } catch (e: SecurityException) {
            false
        }
    }

    fun String?.formatStringDate(inputFormat: String, outputFormat: String): String {
        return if (this.isNullOrEmpty()) {
            ""
        } else {
            val dateFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
            val date = dateFormatter.parse(this)
            date?.let { SimpleDateFormat(outputFormat, Locale.getDefault()).format(it) }.orEmpty()
        }
    }

    fun String?.getYesterdayToday(date: String, format: String): String {
        try {
            val formatter = SimpleDateFormat(format)
            val date = formatter.parse(date)
            val timeInMilliseconds = date.time

            return when {
                DateUtils.isToday(timeInMilliseconds) -> {
                    "today"
                }

                DateUtils.isToday(timeInMilliseconds + DateUtils.DAY_IN_MILLIS) -> {

                    "yesterday"
                }

                else -> {
                    ""
                }
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.d("date exception", it) }
        }
        return ""
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


}
