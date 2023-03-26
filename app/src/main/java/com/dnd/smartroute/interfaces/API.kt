package com.dnd.smartroute.interfaces

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Api {
    @Multipart
    @POST("find_matching")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ResponseBody>
}