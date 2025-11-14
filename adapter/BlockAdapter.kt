package com.phonecontactscall.contectapp.phonedialerr.adapter

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.phonecontactscall.contectapp.phonedialerr.activity.BlockContectActivity.Companion.relData
import com.phonecontactscall.contectapp.phonedialerr.activity.BlockContectActivity.Companion.relEmptyAll
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDao
import com.phonecontactscall.contectapp.phonedialerr.Database.ContactDatabase
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Model.BlockContect
import com.phonecontactscall.contectapp.phonedialerr.Model.ContactModel
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.activity.BlockContectActivity.Companion.constrain_adview
import com.phonecontactscall.contectapp.phonedialerr.databinding.ItemBlockBinding
import java.io.File


class BlockAdapter(
    private val activity: Activity,
    private val arrayList: MutableList<BlockContect>
) : RecyclerView.Adapter<BlockAdapter.ViewHolder>() {
     lateinit var contactDao: ContactDao
    lateinit var menu_dialog: Dialog
    val  contactHelper : ContentResolver
    init {

        val database = ContactDatabase.getDatabase(activity)
        contactDao = database.contactDao()
        contactHelper = activity.contentResolver
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolder(private val binding: ItemBlockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: BlockContect) {
            val test = model.number
            val first = test[0]

            if(model.contactName==null){
                binding.tvName.text = first.toString()

            }else{
                binding.tvName.text = model.contactName

            }
            binding.tvNumber.text = model.number



            if (position % 7 == 0) {
                binding.ivImage.setImageResource(R.drawable.ic_contact_1);
            } else if (position %7 == 1) {
                binding.ivImage.setImageResource(R.drawable.ic_contact_2);

            }else if (position % 7 == 2) {
                binding.ivImage.setImageResource(R.drawable.ic_contact_3);

            }else if (position % 7 ==3) {
                binding.ivImage.setImageResource(R.drawable.ic_contact_4);

            }else if (position % 7 == 4) {
                binding.ivImage.setImageResource(R.drawable.ic_contact_5);

            }else if (position % 7 == 5) {
                binding.ivImage.setImageResource(R.drawable.ic_contact_6);

            }else if (position % 7 == 6) {
                binding.ivImage.setImageResource(R.drawable.ic_contact_7);

            }
            binding.relContect.setOnClickListener(View.OnClickListener {

                val delete_dialog = Dialog(activity)

                delete_dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                delete_dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                delete_dialog.setContentView(R.layout.dialog_unblockk)

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
                val txt_ok = delete_dialog.findViewById<TextView>(R.id.txt_ok)
                val tv_number_dialog =     delete_dialog.findViewById<View>(R.id.tv_number_dialog) as TextView

                tv_number_dialog.text = model.number

                txt_ok.setOnClickListener {
                    try {
                        val unBlock = Glob.removeFromBlock(activity, model.number)
                        if (unBlock) {
                            arrayList.remove(model)
                            notifyDataSetChanged()

                            Glob.showToast(activity, activity.getString(R.string.toast_remove_sucess));


                        }

                        try {
                            if(arrayList.isEmpty()||arrayList.size.equals(0)){

                                relEmptyAll.visibility = View.VISIBLE
                                relData.visibility = View.GONE
                                constrain_adview.visibility = View.GONE
                            }else{
                                relEmptyAll.visibility = View.GONE
                                relData.visibility = View.VISIBLE
                                constrain_adview.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                        }
                    } catch (e: SecurityException) {
                    }
                    delete_dialog.dismiss()
                }
                img_close_delete.setOnClickListener {
                    delete_dialog.dismiss()

                }

            })
        }
    }



    fun createVcf(contact: ContactModel): File {
        val vcfContent = """
        BEGIN:VCARD
        VERSION:3.0
        FN:${contact.name}
        TEL:${contact.number}
        END:VCARD
    """.trimIndent()

        val fileName = "${contact.name}.vcf"
        val file = File(activity.cacheDir, fileName)
        file.writeText(vcfContent)

        return file
    }
    fun shareContact(
        contact: ContactModel

    ) {

        val vcfFile = createVcf(contact)

        val uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", vcfFile)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/x-vcard"
            putExtra(Intent.EXTRA_SUBJECT, activity.resources.getString(R.string.app_name))
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        activity.startActivity(Intent.createChooser(shareIntent, "Share Contact"))

    }



}
