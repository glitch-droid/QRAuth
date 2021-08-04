package varta.cdac.app.services

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/users/getotp")
    @FormUrlEncoded
    fun getOtp(@Field("QRtoken") tokenVal : String ): Call<String>


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/users/login")
    @FormUrlEncoded
    fun login(@Field("QRtoken") tokenVal : String, @Field("otptoken") otpcode:String): Call<String>

}