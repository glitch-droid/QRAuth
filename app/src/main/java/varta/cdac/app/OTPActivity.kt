package varta.cdac.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import varta.cdac.app.constants.ConfigValues
import varta.cdac.app.services.ApiService
import java.util.*

class OTPActivity : AppCompatActivity() {
    private var editTextOtp : EditText? = null
    private var loginBtn : MaterialButton? = null
    private val timer = Timer()
    private lateinit var param1:String
    private lateinit var param2:String
    private var loader:LottieAnimationView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        val qrCode = intent.getStringExtra("qr_code")
        editTextOtp = findViewById(R.id.edit_text_otp)
        loader=findViewById(R.id.login_loading)
        param1= qrCode.toString()
        loginBtn = findViewById(R.id.login_btn)
        startUserSession();
        loginBtn!!.setOnClickListener{
            loader!!.visibility=View.VISIBLE
            param2=editTextOtp!!.text.toString()
            val gson = GsonBuilder().setLenient().create()
            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(ConfigValues.BASE_URL)
                .build()

            val testApi = retrofitBuilder.create(ApiService::class.java)
            val call = testApi.login(param1,param2)

            call.enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if(response.code()==200){
                        timer.cancel()
                        Toast.makeText(applicationContext,"Login Success!",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@OTPActivity,HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else if(response.code()==400){
                        loader!!.visibility=View.INVISIBLE
                        Toast.makeText(applicationContext,"Please Enter Correct OTP",Toast.LENGTH_SHORT).show()
                    }else{
                        loader!!.visibility=View.INVISIBLE
                        Toast.makeText(applicationContext,"Error! Try Again",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    timer.cancel()
                    Toast.makeText(applicationContext,"An unexpected error occurred",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@OTPActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            })
        }

    }

    private fun startUserSession() {
        timer.schedule(object : TimerTask(){
            override fun run() {
                handleSessionExpired()
            }

        }, 25000)

    }

    private fun handleSessionExpired() {
        timer.cancel()
        val intent = Intent(this@OTPActivity,MainActivity::class.java);
        startActivity(intent)
        finish()

    }
}