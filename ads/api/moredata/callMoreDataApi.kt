package com.phonecontactscall.contectapp.phonedialerr.ads.api.moredata

import com.phonecontactscall.contectapp.phonedialerr.ads.model.moredata.DataMoreApi
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface callMoreDataApi {

    @FormUrlEncoded
    @POST("index.php")
    fun FetchMoreList(
        @Field("account_name") accountName: String
    ): Call<DataMoreApi>

}