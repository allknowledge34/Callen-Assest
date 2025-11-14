package com.phonecontactscall.contectapp.phonedialerr.Fragment

import android.R.attr.label
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.BlockedNumberContract
import android.provider.CallLog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.CLIPBOARD_SERVICE
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonecontactscall.contectapp.phonedialerr.CursorKt
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDao
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDatabase
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Glob.call_pass
import com.phonecontactscall.contectapp.phonedialerr.Glob.delte_number
import com.phonecontactscall.contectapp.phonedialerr.Glob.isRPlus
import com.phonecontactscall.contectapp.phonedialerr.Model.DateHeader
import com.phonecontactscall.contectapp.phonedialerr.Model.RecentModel
import com.phonecontactscall.contectapp.phonedialerr.Model.RecyclerViewItem
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.activity.CallHistoryActivity
import com.phonecontactscall.contectapp.phonedialerr.adapter.RecentCallAdapter
import com.phonecontactscall.contectapp.phonedialerr.databinding.FragmentCallBinding
import com.google.gson.Gson
import com.phonecontactscall.contectapp.phonedialerr.activity.RecentContectDetialActivty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class Call_Fragment : BaseFragment<FragmentCallBinding>(), RecentCallAdapter.OnItemClickListener {
    lateinit var callLogs: ArrayList<RecyclerViewItem>
    private lateinit var callLogAdapter: RecentCallAdapter
    lateinit  var  contactHelper : ContentResolver
    val callLogList = ArrayList<RecyclerViewItem>()
    lateinit var contactDao: ContactDao
    var main_popup: PopupWindow? = null

    override fun getViewBinding(): FragmentCallBinding {
     return FragmentCallBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initView() {

        val database = ContactDatabase.getDatabase(requireActivity())
        contactDao = database.contactDao()


         callLogs = getRecentList()

        contactHelper = requireActivity().contentResolver
        binding.revRecentcontect.layoutManager = LinearLayoutManager(requireActivity())
        callLogAdapter = RecentCallAdapter(activity, this,callLogs)

        binding.revRecentcontect.adapter = callLogAdapter





        if(callLogs.isEmpty() || callLogs.size==0){
            binding.relEmptyAll.visibility = View.VISIBLE
        }else{
            binding.relEmptyAll.visibility = View.GONE
        }


    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun isNumberBlocked(context: Context, number: String): Boolean {
        return try {
            val uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI
            val projection = arrayOf(BlockedNumberContract.BlockedNumbers.COLUMN_ID)
            val selection = "${BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER} = ?"
            val selectionArgs = arrayOf(number)

            context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                cursor.moveToFirst()
            } ?: false
        } catch (e: SecurityException) {
            false
        }
    }
    override fun initObserve() {
    }


    private fun getRecentList(): ArrayList<RecyclerViewItem> {
        callLogList.clear()
        val uri = CallLog.Calls.CONTENT_URI
        val projection = arrayOf(
            "_id", "number", "name", "photo_uri", "date", "duration", "type", "subscription_id", "phone_account_address"
        )
        val simMap = HashMap<String, Int>()
        val availableSIMs = Glob.getAvailableSIMCardLabels( requireActivity())

        for (simAccount in availableSIMs) {
            simMap[simAccount.handle.id] = simAccount.id
        }

        val sortOrder = if (!isRPlus()) {
            "_id DESC LIMIT 100"
        } else {
            Bundle().apply {
                putStringArray("android:query-arg-sort-columns", arrayOf("_id"))
                putInt("android:query-arg-sort-direction", 1)
                putInt("android:query-arg-limit", 100)
            }
        }

        val cursor: Cursor? = if (sortOrder is String) {
          requireActivity().contentResolver.query(uri, projection, null, null, sortOrder)
        } else {
            requireActivity().contentResolver.query(uri, projection, sortOrder as Bundle, null)
        }

        val callLogMap = mutableMapOf<String, MutableList<RecentModel>>()

        cursor?.use {
            if (it.moveToFirst()) {
                val str: String? = null
                do {
                    val id = CursorKt.getIntValue(it, "_id")
                    val number = CursorKt.getStringValue(it, "number")
                    val name = CursorKt.getStringValue(it, "name").takeIf { !it.isNullOrEmpty() } ?: number
                    val photoUri = CursorKt.getStringValue(it, "photo_uri").takeIf { !it.isNullOrEmpty() } ?: ""
                    val date = CursorKt.getLongValue(it, "date")
                    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(date))

                    val formattedDateTime = SimpleDateFormat("MMM dd,  hh:mm a", Locale.getDefault()).format(Date(date))
                    val duration = CursorKt.getIntValue(it, "duration")
                    val type = CursorKt.getIntValue(it, "type")
                    val subscriptionId = CursorKt.getStringValue(it, "subscription_id")
                    val simId = simMap[subscriptionId] ?: -1
                    val recentModel = RecentModel(id.toLong(), number, name, photoUri, date.toInt(), 0, type, simId, 0, formattedDate, formattedDateTime,false)
                    val logsForDate = callLogMap.getOrPut(formattedDate) { mutableListOf() }



                    val existingModel = logsForDate.find { it.number == number }
                    if (existingModel != null) {
                        existingModel.duration += duration
                        existingModel.count += 1
                    } else {
                        recentModel.duration = duration
                        recentModel.count = 1
                        logsForDate.add(recentModel)
                    }

                } while (it.moveToNext())
            }
        }



        val now = LocalDate.now()
        val today = now.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        val yesterday = now.minusDays(1).format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

        callLogMap.forEach { (date, logs) ->
            val header = when (date) {
                today -> requireActivity().getString(R.string.str_today)
                yesterday -> requireActivity().getString(R.string.str_yesterday)
                else -> requireActivity().getString(R.string.str_older)
            }
            if (callLogList.none { it is DateHeader && it.date == header }) {
                callLogList.add(DateHeader(header))
            }
            callLogList.addAll(logs)
        }

        return callLogList
    }




    fun showPopup(v: View, callLog: RecentModel) {

        val inflater: LayoutInflater = layoutInflater

        val view: View = inflater.inflate(R.layout.popup_layout_main, null)
        main_popup = PopupWindow(context)
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

        val displayMetrics = requireActivity().resources.displayMetrics
        val height = (displayMetrics.heightPixels * 2) / 3.5
        println("Height:$height")

        if (positionOfIcon > height) {
            main_popup!!.showAsDropDown(v, 0, -550)
        } else {
            main_popup!!.showAsDropDown(v, 0, 0)
        }
        val tv_block: TextView = view.findViewById(R.id.tv_block)
        val rel_contect_detail: RelativeLayout = view.findViewById(R.id.rel_contect_detail)
        val rel_edit_contect: RelativeLayout = view.findViewById(R.id.rel_edit_contect)
        val rel_history: RelativeLayout = view.findViewById(R.id.rel_history)
        val rel_delete: RelativeLayout = view.findViewById(R.id.rel_delete)
        val rel_copy: RelativeLayout = view.findViewById(R.id.rel_copy)
        val rel_block: RelativeLayout = view.findViewById(R.id.rel_block)
        val img_share: ImageView = view.findViewById(R.id.img_share)


        val isBlocked = isNumberBlocked(requireContext(), callLog.number)
        if (isBlocked) {
            tv_block.text =  getString(R.string.str_un_block)
            img_share.setImageResource(R.drawable.ic_remove_unblock)
        } else {
            img_share.setImageResource(R.drawable.ic_block_number)
            tv_block.text =  getString(R.string.str_block)
        }

        rel_contect_detail.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()



            val gson = Gson()
            val intent = Intent(requireActivity(), RecentContectDetialActivty::class.java)
            intent.putExtra("identifier", gson.toJson(callLog))
            startActivity(intent)


        })

        rel_edit_contect.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()

        })

        rel_history.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()
            val gson = Gson()
            val intent = Intent(requireActivity(), CallHistoryActivity::class.java)
            intent.putExtra("identifier", gson.toJson(callLog))
            startActivity(intent)

        })


        rel_delete.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()

            val delete_dialog = Dialog(requireActivity())
            delete_dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            delete_dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            delete_dialog.setContentView(R.layout.dialog_delete)

            delete_dialog.window!!.setGravity(Gravity.CENTER)
            delete_dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            delete_dialog.window!!.getAttributes().windowAnimations = R.style.dialog_theme
            delete_dialog.setCancelable(true)
            delete_dialog.setCanceledOnTouchOutside(true)
            delete_dialog.show()
            val img_close_delete = delete_dialog.findViewById<TextView>(R.id.txt_cancel)
            val btn_delete = delete_dialog.findViewById<TextView>(R.id.text_ok)
            btn_delete.setOnClickListener {
                delete_dialog.dismiss()

                try {
                    deleteContactByPhoneNumber(callLog.number)
                    CoroutineScope(Dispatchers.IO).launch {
                        val position = callLogList.indexOf(callLog)
                        if (position != -1) {
                            callLogList.removeAt(position)
                            withContext(Dispatchers.Main) {
                                callLogAdapter.notifyItemRemoved(position)
                            }
                        }

                        withContext(Dispatchers.Main) {
                            Glob.showToast(context, requireActivity().getString(R.string.toast_delete));
                        }



                    }
                } catch (e: SecurityException) {
                }

            }
            img_close_delete.setOnClickListener {
                delete_dialog.dismiss()
            }



        })

        rel_copy.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()
            val clipboard: ClipboardManager = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label.toString(), callLog.number)
            clipboard.setPrimaryClip(clip)
            Glob.showToast(requireActivity(), requireActivity().getString(R.string.toast_copy));

        })

        rel_block.setOnClickListener(View.OnClickListener {
            main_popup!!.dismiss()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                blockSelectedNumber(callLog!!)
            }
        })




    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        try {
            if(delte_number || call_pass){
                delte_number = false
                call_pass = false
                callLogs = getRecentList()
                binding.revRecentcontect.layoutManager = LinearLayoutManager(requireActivity())

                callLogAdapter = RecentCallAdapter(activity, this,callLogs)
                binding.revRecentcontect.adapter = callLogAdapter

                if(callLogs.isEmpty() || callLogs.size==0){
                    binding.relEmptyAll.visibility = View.VISIBLE
                }else{
                    binding.relEmptyAll.visibility = View.GONE
                }
            }


        } catch (e: Exception) {
        }
    }
    override fun onItemClick(callLog: RecentModel, itemView: View) {


        showPopup(v = itemView,callLog)
    }



    // BLock Number
    @RequiresApi(Build.VERSION_CODES.N)
    private fun blockSelectedNumber(selectedConversations: RecentModel) {
        val allBlockedIdList = mutableListOf<Long>()
        try {

            val numberWithoutSpaces = selectedConversations.number.replace(" ", "")


            val isAlreadyBlocked = isNumberBlocked(requireActivity(), numberWithoutSpaces)
            if (isAlreadyBlocked) {


                Glob.removeFromBlock(requireActivity(), selectedConversations.number)

                Glob.showToast(requireActivity(), getString(R.string.toast_remove_sucess))

            }else {
                val isBlock = Glob.addToBlock(
                    requireActivity(), selectedConversations.number
                )

                if (isBlock) {
                    allBlockedIdList.add(selectedConversations.id!!.toLong())

                }

            }
        } catch (e: Exception) {
        }
        callLogAdapter.notifyDataSetChanged()
        Glob.showToast(requireActivity(), requireActivity().getString(R.string.toast_added_to_blocklist));


    }
    fun deleteContactByPhoneNumber(phoneNumber: String) {
        val uri = CallLog.Calls.CONTENT_URI
        val selection = "${CallLog.Calls.NUMBER} = ?"
        val selectionArgs = arrayOf(phoneNumber)
        delte_number = true

        val rowsDeleted = requireActivity().contentResolver.delete(uri, selection, selectionArgs)

        if (rowsDeleted > 0) {
        } else {
        }


        if(callLogList.isEmpty() || callLogList.size==2){

            binding.relEmptyAll.visibility = View.VISIBLE
            binding.revRecentcontect.visibility = View.GONE
        }else{
            binding.relEmptyAll.visibility = View.GONE
            binding.revRecentcontect.visibility = View.VISIBLE
        }
    }




}