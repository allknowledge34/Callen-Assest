package com.phonecontactscall.contectapp.phonedialerr.ads.model.moredata

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MoreApp(

    @SerializedName("id")
    @Expose
    val id: Int?= null,

    @SerializedName("app_name")
    @Expose
    val more_appName: String? = null,

    @SerializedName("app_description")
    @Expose
    val more_appDescription: String? = null,

    @SerializedName("app_link")
    @Expose
    val more_appLink: String? = null,

    @SerializedName("app_icon")
    @Expose
    val more_appIcon: String? = null,

    @SerializedName("app_banner")
    @Expose
    val more_appBanner: String? = null,

    @SerializedName("app_screenshot")
    @Expose
    val more_appScreenshot: String? = null

) {
    override fun toString(): String {
        return """
            ---id---$id
            ---appName----$more_appName
            ---appDescription----$more_appDescription
            ---appLink----$more_appLink
            ------appIcon---$more_appIcon
            -----appBanner----$more_appBanner
            ----appScreenshot-----$more_appScreenshot
            """.trimIndent();
    }
}
