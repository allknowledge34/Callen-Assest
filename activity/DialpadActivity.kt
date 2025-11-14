package com.phonecontactscall.contectapp.phonedialerr.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper
import com.phonecontactscall.contectapp.phonedialerr.Util.CallerHelper.callPhoneClick
import com.phonecontactscall.contectapp.phonedialerr.CursorKt
import com.phonecontactscall.contectapp.phonedialerr.Glob
import com.phonecontactscall.contectapp.phonedialerr.Glob.all_contect_list
import com.phonecontactscall.contectapp.phonedialerr.Glob.edit_number
import com.phonecontactscall.contectapp.phonedialerr.Glob.isOreoPlus
import com.phonecontactscall.contectapp.phonedialerr.Glob.isRPlus
import com.phonecontactscall.contectapp.phonedialerr.Model.ContactModel
import com.phonecontactscall.contectapp.phonedialerr.Model.RecentModel
import com.phonecontactscall.contectapp.phonedialerr.Model.RecyclerViewItem
import com.phonecontactscall.contectapp.phonedialerr.Model.SpeedDial
import com.phonecontactscall.contectapp.phonedialerr.MyApplication
import com.phonecontactscall.contectapp.phonedialerr.R
import com.phonecontactscall.contectapp.phonedialerr.Util.TelecomUtils.isDefaultDialer
import com.phonecontactscall.contectapp.phonedialerr.Util.ContactRepository
import com.phonecontactscall.contectapp.phonedialerr.Util.EditTextKt
import com.phonecontactscall.contectapp.phonedialerr.Util.ToneGeneratorHelper
import com.phonecontactscall.contectapp.phonedialerr.adapter.DailAdapter
import com.phonecontactscall.contectapp.phonedialerr.databinding.ActivityDialpadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.jvm.internal.Intrinsics
import kotlin.math.roundToInt


class DialpadActivity : BaseActivity<ActivityDialpadBinding>() {
    var TAG = "Dialpad_Act"
    var adapter: DailAdapter? = null
    private var toneGeneratorHelper: ToneGeneratorHelper? = null

    private var speedDialValues = ArrayList<SpeedDial>()
    private val pressedKeys: MutableList<Char> = mutableListOf()
    var z: Boolean = true
    private val longPressHandler = Handler(Looper.getMainLooper())
    private val longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()

    private var isAskingPermissions: Boolean = false
    private var actionOnPermission: Function1<Boolean, Unit>? = null
    private lateinit var defaultDialerResultLauncher: ActivityResultLauncher<Intent>
    private val ADD_CONTACT_REQUEST = 1
    private var contactRepository = ContactRepository()
    val callLogList = ArrayList<RecyclerViewItem>()
    override fun getViewBinding(): ActivityDialpadBinding {
        return ActivityDialpadBinding.inflate(layoutInflater)
    }

    override fun initView() {

        val color = Glob.getResourceFromAttr(R.attr.lang_bg_color, this)
        val color1 = Glob.getResourceFromAttr(R.attr.navigation_bg_color, this)
//
        window.statusBarColor = color
        window.navigationBarColor = color1

        this.toneGeneratorHelper = ToneGeneratorHelper(this, 150L)

        Glob.SetStatusbarColor(window)

        val intent = intent
        if (intent.data != null) {
            CallerHelper.callPhoneClick(this, intent.data.toString().substring(4))
            finish()
            return
        }


        if (Build.VERSION.SDK_INT < 29) {
            if (Checkpermission()) {
                getContactList()

            } else {

                finish()

            }


        } else {
            if (!Glob.isDefaultDialer(this)) {
                finish()
            } else {

                getContactList()


            }
        }




        binding.fabCall.setOnClickListener(View.OnClickListener { view ->
            val editText: EditText = binding.etDialpadInput


            if (editText.text.equals(" ") || editText.text.isEmpty()) {

                val isReadContactsGranted = ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED

                val isWriteContactsGranted = ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED

                if (isReadContactsGranted && isWriteContactsGranted) {
                    val recentNumber = getRecentList().firstOrNull()?.let {
                        if (it is RecentModel) it.number else null
                    } ?: "No recent calls"

                    editText.setText(recentNumber)
                    dialpadValueChanged(recentNumber.toString());
                    if (binding.etDialpadInput.text.isEmpty()) {
                        binding.txtAddNumber.visibility = View.GONE
                    } else {
                        binding.txtAddNumber.visibility = View.VISIBLE
                    }
                    contactExists(this, binding.etDialpadInput.text.toString())
                    val pos = editText.text.length
                    editText.setSelection(pos)
                }


            } else {

                callPhoneClick(this, editText.text.toString())
            }


        })

        binding.clEditText.setOnClickListener { v ->

        }

        if (binding.etDialpadInput.text.isEmpty()) {
            binding.txtAddNumber.visibility = View.GONE
        } else {
            binding.txtAddNumber.visibility = View.VISIBLE
        }

        binding.txtAddNumber.setOnClickListener(View.OnClickListener {

            val isReadContactsGranted = ContextCompat.checkSelfPermission(
                this@DialpadActivity,
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val isWriteContactsGranted = ContextCompat.checkSelfPermission(
                this@DialpadActivity,
                android.Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            if (isReadContactsGranted && isWriteContactsGranted) {
                val exist = contactExists(this, binding.etDialpadInput.text.toString())
                if (exist) {
                    val contactId = getContactId(this, binding.etDialpadInput.text.toString())
                    if (contactId != null) {
                        val editIntent = Intent(Intent.ACTION_EDIT)
                        editIntent.data =
                            Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId)
                        startActivityForResult(editIntent, ADD_CONTACT_REQUEST)
                    }
                } else {
                    val addIntent = Intent(Intent.ACTION_INSERT)
                    addIntent.type = ContactsContract.Contacts.CONTENT_TYPE
                    addIntent.putExtra(
                        ContactsContract.Intents.Insert.PHONE,
                        binding.etDialpadInput.text.toString()
                    )
                    startActivityForResult(addIntent, ADD_CONTACT_REQUEST)
                }


            }


        })


        binding.etDialpadInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val isReadContactsGranted = ContextCompat.checkSelfPermission(
                    this@DialpadActivity,
                    android.Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED

                val isWriteContactsGranted = ContextCompat.checkSelfPermission(
                    this@DialpadActivity,
                    android.Manifest.permission.WRITE_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED

                if (isReadContactsGranted && isWriteContactsGranted) {
                    dialpadValueChanged(s.toString());

                }

            }
        })

        setupnumberClick(binding.dialpadInclude.dialpad0Holder, '0', false)
        setupnumberClick(binding.dialpadInclude.dialpad1Holder, '1', false)
        setupnumberClick(binding.dialpadInclude.dialpad2Holder, '2', false)
        setupnumberClick(binding.dialpadInclude.dialpad3Holder, '3', false)
        setupnumberClick(binding.dialpadInclude.dialpad4Holder, '4', false)
        setupnumberClick(binding.dialpadInclude.dialpad5Holder, '5', false)
        setupnumberClick(binding.dialpadInclude.dialpad6Holder, '6', false)
        setupnumberClick(binding.dialpadInclude.dialpad7Holder, '7', false)
        setupnumberClick(binding.dialpadInclude.dialpad8Holder, '8', false)
        setupnumberClick(binding.dialpadInclude.dialpad9Holder, '9', false)
        setupnumberClick(binding.dialpadInclude.dialpadAsteriskHolder, '*', false)
        setupnumberClick(binding.dialpadInclude.dialpadHashtagHolder, '#', false)

        defaultDialerResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                val isDefaultDialer = (result.resultCode == RESULT_OK)
                actionOnPermission?.invoke(isDefaultDialer)
                isAskingPermissions = false
            }


        if (!checkDialIntent()) {
            val editText: EditText = binding.etDialpadInput
            if (EditTextKt.getValue(editText).equals(0)) {
                z = false
            }
            if (!z) {
                return
            }

            val isReadContactsGranted = ContextCompat.checkSelfPermission(
                this@DialpadActivity,
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val isWriteContactsGranted = ContextCompat.checkSelfPermission(
                this@DialpadActivity,
                android.Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            if (isReadContactsGranted && isWriteContactsGranted) {
                dialpadValueChanged("")


            }
        }



        binding.dialpadClearChar.setOnClickListener(View.OnClickListener { view ->

            if (binding.etDialpadInput.text.isEmpty() || binding.etDialpadInput.text.equals(" ")) {
                binding.txtAddNumber.visibility = View.GONE
            } else {
                binding.txtAddNumber.visibility = View.VISIBLE
            }
            dialpadValueChanged(binding.etDialpadInput.text.toString());
            binding.etDialpadInput.dispatchKeyEvent(EditTextKt.getKeyEvent(67))
        })

        binding.dialpadClearChar.setOnLongClickListener { view ->
            if (binding.etDialpadInput.text.isEmpty() || binding.etDialpadInput.text.equals(" ")) {
                binding.txtAddNumber.visibility = View.GONE
            } else {
                binding.txtAddNumber.visibility = View.VISIBLE

            }
            handleLongClick(view)
        }



        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.e("Contect_Event--", "Dialpad_Act_onBackpress")
                MyApplication.mFirebaseAnalytics?.logEvent("Dialpad_Act_onBackpress", Bundle())
                finish()
            }

        })

        binding.imgBack.setOnClickListener(View.OnClickListener { view ->
            onBackPressedDispatcher.onBackPressed()

        })
        disableKeyboardPopping()


        Log.e("Contect_Event--", "Dialpad_Act_onCreate")
        MyApplication.mFirebaseAnalytics?.logEvent("Dialpad_Act_onCreate", Bundle())
    }


    private fun Checkpermission(): Boolean {
        if (Build.VERSION.SDK_INT <= 29) {
            val result = let { ContextCompat.checkSelfPermission(it!!, Manifest.permission.READ_CONTACTS) }
            val result1 =
                let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_PHONE_STATE) }
            val result2 = let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CALL_LOG) }
            val result3 =
               let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_CALL_LOG) }
            val result4 =let { ContextCompat.checkSelfPermission(it, Manifest.permission.CALL_PHONE) }
            if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED) {
                true
            } else false
        }
        return true
    }



    private fun getRecentList(): ArrayList<RecyclerViewItem> {
        callLogList.clear()
        val uri = CallLog.Calls.CONTENT_URI
        val projection = arrayOf(
            "_id",
            "number",
            "name",
            "photo_uri",
            "date",
            "duration",
            "type",
            "subscription_id",
            "phone_account_address"
        )
        val simMap = HashMap<String, Int>()
        val availableSIMs = Glob.getAvailableSIMCardLabels(this)

        for (simAccount in availableSIMs) {
            simMap[simAccount.handle.id] = simAccount.id
        }


        val sortOrder = if (!isRPlus()) {
            "_id DESC LIMIT 1"
        } else {
            Bundle().apply {
                putStringArray("android:query-arg-sort-columns", arrayOf("date"))
                putInt("android:query-arg-sort-direction", 1)
                putInt("android:query-arg-limit", 1)
            }
        }

        val cursor: Cursor? = if (sortOrder is String) {
            contentResolver.query(uri, projection, null, null, sortOrder)
        } else {
            contentResolver.query(uri, projection, sortOrder as Bundle, null)
        }


        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = CursorKt.getIntValue(it, "_id")
                    val number = CursorKt.getStringValue(it, "number")
                    val name =
                        CursorKt.getStringValue(it, "name").takeIf { !it.isNullOrEmpty() } ?: number
                    val photoUri =
                        CursorKt.getStringValue(it, "photo_uri").takeIf { !it.isNullOrEmpty() }
                            ?: ""
                    val date = CursorKt.getLongValue(it, "date")
                    val formattedDate =
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(date))
                    val formattedDateTime =
                        SimpleDateFormat("MMM dd,  hh:mm a", Locale.getDefault()).format(Date(date))
                    val duration = CursorKt.getIntValue(it, "duration")
                    val type = CursorKt.getIntValue(it, "type")
                    val subscriptionId = CursorKt.getStringValue(it, "subscription_id")
                    val simId = simMap[subscriptionId] ?: -1


                    val recentModel = RecentModel(
                        id = id.toLong(),
                        number = number,
                        name = "",
                        photoUri = "",
                        date = 0,
                        duration = 0,
                        type = 0,
                        simId = simId,
                        count = 1,
                        formattedDate = "",
                        formattedDatetime = formattedDateTime,
                        isFavorite = false
                    )
                    callLogList.add(recentModel)
                } while (it.moveToNext())
            }
        }

        return callLogList
    }

    fun contactExists(context: Context, number: String?): Boolean {
        val lookupUri = Uri.withAppendedPath(
            PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val mPhoneNumberProjection =
            arrayOf(PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME)
        val cur = context.contentResolver.query(lookupUri, mPhoneNumberProjection, null, null, null)
        try {
            if (cur!!.moveToFirst()) {


                binding.txtAddNumber.setText(getString(R.string.str_edit_contect))


                return true
            } else {
                binding.txtAddNumber.setText(getString(R.string.str_add_number))

            }
        } finally {
            cur?.close()
        }
        return false
    }


    private fun getContactId(context: Context, number: String?): String? {
        val lookupUri = Uri.withAppendedPath(
            PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val projection = arrayOf(PhoneLookup._ID)
        val cursor = context.contentResolver.query(lookupUri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(PhoneLookup._ID))
            }
        }
        return null
    }

    private fun setupnumberClick(view: View, c: Char, z: Boolean) {
        view.isClickable = true
        view.isLongClickable = true
        view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {

                if (binding.etDialpadInput.text.isEmpty() || binding.etDialpadInput.text.equals(" ")) {
                    binding.txtAddNumber.visibility = View.GONE
                } else {
                    binding.txtAddNumber.visibility = View.VISIBLE
                }
                if (event.action == 0) {
                    dialpadPressed(c)
                    startDialpadTone(c)
                    if (z) {
                        longPressHandler.removeCallbacksAndMessages(null)
                        longPressHandler.postDelayed(
                            Runnable
                            {
                                performLongClick(c)
                            }, longPressTimeout
                        )
                    }
                } else {
                    if (event.action != 1) {
                        if (event.action == 2) {
                            if (!(if ((java.lang.Float.isNaN(event.rawX) || java.lang.Float.isNaN(event.rawY))) false else Glob.getBoundingBox(view).contains(event.rawX.roundToInt(), event.rawY.roundToInt()))) {
                                stopDialpadTone(c)
                                if (z) {
                                    stopDialpadTone(c)
                                    longPressHandler.removeCallbacksAndMessages(null)
                                }
                            }

                            stopDialpadTone(c)
                        }
                    }
                    stopDialpadTone(c)
                    if (z) {
                        longPressHandler.removeCallbacksAndMessages(null)
                    }
                }
                return false
            }
        })
    }

    private fun handleLongClick(view: View): Boolean {
        binding.etDialpadInput.setText("");

        return true
    }




    private fun checkDialIntent(): Boolean {
        if ((Intrinsics.areEqual(
                intent.action,
                "android.intent.action.DIAL"
            ) || Intrinsics.areEqual(
                intent.action, "android.intent.action.VIEW"
            )) && intent.data != null
        ) {
            val dataString = intent.dataString
            if ((dataString != null)

            ) {
                return false
            }
            val decode = Uri.decode(intent.dataString)
            Intrinsics.checkNotNullExpressionValue(decode, "decode(intent.dataString)")
            binding.etDialpadInput.setText(decode)
            binding.etDialpadInput.setSelection(decode.length)
            return true
        }
        return false
    }

    private fun disableKeyboardPopping() {
        binding.etDialpadInput.setShowSoftInputOnFocus(false)
    }


    @SuppressLint("Range")
    private fun getContactList() {


        contactRepository = ContactRepository()
        lifecycleScope.launch {

            if (Glob.receivenotification) {
                contactRepository.fetchContacts(this@DialpadActivity)
            }

            contactRepository.contactsStateFlow.collect { contactItemList ->
                speedDialValues = Glob.getSpeedDialValues(this@DialpadActivity)
                binding.recyclerView.layoutManager = LinearLayoutManager(this@DialpadActivity)
                adapter = DailAdapter(this@DialpadActivity, all_contect_list)
                binding.recyclerView.adapter = adapter
                adapter?.notifyDataSetChanged()
                updateGUi(all_contect_list)
                all_contect_list.sortBy { it.name.toUpperCase() }


            }
        }




    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun dialpadValueChanged(str: String) {
        toggleClearButtonVisibility(str)
        var z = false
        if (str.length > 8 && str.startsWith("*#*#") && str.endsWith("#*#*")) {
            val substring = str.substring(4, str.length - 4)

            if (isOreoPlus()) {
                if (isDefaultDialer(this)) {
                    val telephonyManager = getSystemService(TelephonyManager::class.java)
                    telephonyManager?.sendDialerSpecialCode(substring)
                    return
                } else {
                    askDefaultDialerPermission { aBoolean ->
                        if (aBoolean) {
                            val telephonyManager = getSystemService(TelephonyManager::class.java)
                            telephonyManager?.sendDialerSpecialCode(substring)
                        }
                    }
                    return
                }
            } else {
                sendBroadcast(
                    Intent(
                        "android.provider.Telephony.SECRET_CODE",
                        Uri.parse("android_secret_code://$substring")
                    )
                )
                return
            }
        }

        adapter?.textChanged(str)
        z = str.trim().isNotEmpty()

        if (z) {

            filter(str)
        } else {
            updateGUi(all_contect_list)
        }
    }

    fun filter(text: String) {
        val normalizedText = text.replace(" ", "").lowercase()
        val withoutCountryCode = normalizedText.replace("^\\+91".toRegex(), "")

        CoroutineScope(Dispatchers.Default).launch {
            val filterList = ArrayList<ContactModel>()

            val contactListCopy = ArrayList(all_contect_list)
            val contactListCopy1 = ArrayList(contactListCopy)

            val targetList =
                if (contactListCopy1.isNotEmpty()) contactListCopy1 else contactListCopy
            for (item in targetList) {
                val normalizedNumber = item.number.replace(" ", "").lowercase()
                if (normalizedNumber.contains(withoutCountryCode)) {
                    filterList.add(item)
                }
            }

            withContext(Dispatchers.Main) {
                try {
                    contactExists(this@DialpadActivity, binding.etDialpadInput.text.toString())
                    adapter?.filterlist(filterList)

                    if (filterList.isEmpty()) {
                        binding.relEmptyAll.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.relEmptyAll.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


    private fun updateGUi(arrayList: ArrayList<ContactModel>) {


        if (arrayList.isEmpty() || arrayList.size == 0) {
            binding.relEmptyAll.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.relEmptyAll.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DailAdapter(this, all_contect_list)
        binding.recyclerView.adapter = adapter
        adapter?.notifyDataSetChanged()
    }




    private fun askDefaultDialerPermission(callback: Function1<Boolean, Unit>) {
        Intrinsics.checkNotNullParameter(callback, "callback")
        this.isAskingPermissions = true
        this.actionOnPermission = callback

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            Intrinsics.checkNotNull(roleManager, "roleManager")

            if (!roleManager.isRoleAvailable(RoleManager.ROLE_DIALER) || roleManager.isRoleHeld(
                    RoleManager.ROLE_DIALER
                )
            ) {
                callback.invoke(true)
                return
            }

            val createRequestRoleIntent =
                roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            Intrinsics.checkNotNullExpressionValue(
                createRequestRoleIntent,
                "roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)"
            )

            defaultDialerResultLauncher.launch(createRequestRoleIntent)
            return
        }

        val putExtra = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
            TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
            packageName
        )

        try {
            defaultDialerResultLauncher.launch(putExtra)
        } catch (e: ActivityNotFoundException) {
        } catch (e: Exception) {
            val string = getString(R.string.somwthing_went_wrong)
            Intrinsics.checkNotNullExpressionValue(
                string,
                "getString(R.string.something_went_wrong)"
            )

            Glob.showToast(this, string)

        }
    }

    private fun toggleClearButtonVisibility(str: String) {
        if (str.length > 0) {
            val imageView: ImageView = binding.dialpadClearChar
            imageView.visibility = View.VISIBLE
            return
        }
        binding.txtAddNumber.visibility = View.GONE
        val imageView2: ImageView = binding.dialpadClearChar
        imageView2.visibility = View.INVISIBLE
    }




    private fun dialpadPressed(char: Char) {
        EditTextKt.addCharacter(binding.etDialpadInput, char)
        Log.d(TAG, "dialpadPressed: " + char)
    }

    private fun clearChar() {
        binding.etDialpadInput.dispatchKeyEvent(EditTextKt.getKeyEvent(67))
    }

    private fun clearInput() {
        binding.etDialpadInput.setText("")
    }


    private fun initCall(str: String) {
        if (str.length > 0) {
            startCallIntent(str)
        }
    }

    fun startCallIntent(recipient: String?) {
        launchCallIntent(recipient, null)
    }

    fun launchCallIntent(recipient: String?, phoneAccountHandle: PhoneAccountHandle?) {

        val intent = Intent(if (true) Intent.ACTION_CALL else Intent.ACTION_DIAL).apply {
            setClass(this@DialpadActivity, CallActivity::class.java)
            data = Uri.fromParts("tel", recipient, null)
            putExtra("phone_account_handle", phoneAccountHandle)
        }
        startActivity(intent)


    }


    private fun speedDial(i: Int): Boolean {
        var obj: Any?
        var z: Boolean
        val editText: EditText = binding.etDialpadInput
        Intrinsics.checkNotNullExpressionValue(editText, "binding.etDialpadInput")
        if (EditTextKt.getValue(editText).equals(0)) {
            val it: Iterator<SpeedDial> = speedDialValues.iterator()
            while (true) {
                if (!it.hasNext()) {
                    obj = null
                    break
                }
                obj = it.next()
                if ((obj as SpeedDial).id == i) {
                    z = true
                    continue
                } else {
                    z = false
                    continue
                }
                if (z) {
                    break
                }
            }
            val speedDial = obj as SpeedDial?
            if (speedDial != null && speedDial.isValid()) {
                initCall(speedDial.number)
                return true
            }
        }
        return false
    }

    private fun startDialpadTone(c: Char) {
        pressedKeys.add(Character.valueOf(c))
        val toneGeneratorHelper = this.toneGeneratorHelper
        toneGeneratorHelper?.startTone(c)
    }

    private fun stopDialpadTone(c: Char) {
        if (!pressedKeys.remove(Character.valueOf(c))) {
            return
        }
        if (pressedKeys.isEmpty()) {
            val toneGeneratorHelper = this.toneGeneratorHelper ?: return
            toneGeneratorHelper.stopTone()
            return
        }
    }


    private fun performLongClick(c: Char) {
        if (c == '0') {
            clearChar()
            dialpadPressed('+')
        } else if (!speedDial(c.digitToInt())) {
        } else {
            stopDialpadTone(c)
            clearChar()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == ADD_CONTACT_REQUEST) {
            if (resultCode === RESULT_OK) {

                binding.etDialpadInput.setText("")
                getContactList()

                edit_number = true

            }
        }
    }

}

