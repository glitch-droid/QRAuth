package varta.cdac.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import java.util.*

class OTPActivity : AppCompatActivity() {
    private var editTextOtp : EditText? = null
    private var loginBtn : MaterialButton? = null
    private val timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        editTextOtp = findViewById(R.id.edit_text_otp)
        loginBtn = findViewById(R.id.login_btn)
        startUserSession();
        loginBtn!!.setOnClickListener{
            timer.cancel()
            val intent = Intent(this@OTPActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun startUserSession() {
        timer.schedule(object : TimerTask(){
            override fun run() {
                handleSessionExpired()
            }

        }, 30000)

    }

    private fun handleSessionExpired() {
        timer.cancel()
        val intent = Intent(this@OTPActivity,MainActivity::class.java);
        startActivity(intent)
        finish()

    }
}