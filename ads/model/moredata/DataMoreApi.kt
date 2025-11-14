package com.phonecontactscall.contectapp.phonedialerr.ads.model.moredata

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataMoreApi(

    @SerializedName("MESSAGE")
    @Expose
    val app_message: String? = null,

    @SerializedName("STATUS")
    @Expose
    val app_status: Int? = null,

    @SerializedName("DATA")
    @Expose
    val app_moreAppData: List<MoreApp>? = null

)
