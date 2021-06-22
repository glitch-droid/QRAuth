package varta.cdac.app.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import varta.cdac.app.model.Data

interface TestApi {

    @POST("posts")
    fun sendData(
        @Body data : Data
    ) : Call<Data>
}