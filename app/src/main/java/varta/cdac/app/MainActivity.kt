package varta.cdac.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import varta.cdac.app.constants.ConfigValues
import varta.cdac.app.model.LoginResponse
import varta.cdac.app.services.ApiService
import java.lang.Exception


class MainActivity : AppCompatActivity(), EasyPermissions.RationaleCallbacks, EasyPermissions.PermissionCallbacks {

    private var scanCode : Button? = null
    private var cv : CardView? = null
    private var label : TextView? =null
    private var done : LottieAnimationView?=null
    private var error : LottieAnimationView?=null
    private var loading : LottieAnimationView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanCode = findViewById(R.id.scan_code_button)
        cv = findViewById(R.id.cardView)
        label = findViewById(R.id.helperTV)
        done = findViewById(R.id.anim_done)
        error = findViewById(R.id.anim_error)
        loading = findViewById(R.id.anim_loading)

        cv!!.visibility = View.VISIBLE

        scanCode!!.setOnClickListener {
            scanCode!!.isEnabled
            cv!!.visibility = View.VISIBLE
            label!!.text = "Tap on icon to scan code"
            error!!.visibility = View.GONE
            done!!.visibility = View.GONE
            loading!!.visibility = View.GONE

        }
        cv!!.setOnClickListener{
            cameraTask()
        }
    }

    private fun setUpCall(value: String) {
        scanCode!!.isEnabled = false
        val gson = GsonBuilder().setLenient().create()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(ConfigValues.BASE_URL)
            .build()

        val testApi = retrofitBuilder.create(ApiService::class.java)
        val modifiedValue = value;
        val call = testApi.login(modifiedValue)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText(this@MainActivity, "Response Code " + response.code().toString(), Toast.LENGTH_LONG).show()
                if(response.code()==200){
                    label!!.text="Success!"
                    loading!!.visibility=View.INVISIBLE
                    done!!.visibility = View.VISIBLE
                    val intent = Intent(this@MainActivity, OTPActivity::class.java)
                    startActivity(intent)
                    finish()
                    Log.d("A",response.message() );
                    scanCode!!.isEnabled = true
                }
                if(response.code() == 400)
                {
                    label!!.text = "Error!"
                    loading!!.visibility = View.INVISIBLE
                    error!!.visibility = View.VISIBLE
                    Log.d("A", response.message());
                    scanCode!!.isEnabled = true
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                label!!.text="Error!"
                loading!!.visibility=View.GONE
                error!!.visibility = View.VISIBLE
                Log.d("A",t.message.toString() );
                Toast.makeText(this@MainActivity, "Error in sending QR code " + t.message.toString(),Toast.LENGTH_LONG).show()
                scanCode!!.isEnabled = true
            }
        })
    }

    private fun hasCameraAccess() : Boolean
    {
        return EasyPermissions.hasPermissions(this,android.Manifest.permission.CAMERA)
    }

    private fun cameraTask(){
        if(hasCameraAccess()){
            val qrScanner = IntentIntegrator(this).setBarcodeImageEnabled(true)
            qrScanner.setPrompt("Scan a QR code")
                .setCameraId(0)
                .setOrientationLocked(true)
                .setBeepEnabled(true)
            qrScanner.captureActivity = CaptureActivity::class.java
            qrScanner.initiateScan()
        }else{
            EasyPermissions.requestPermissions(this,
                "This app needs access to your camera",
                123,
                android.Manifest.permission.CAMERA)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result!=null){
            if(result.contents==null){
                Toast.makeText(this,"QR Code not detected", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    loading!!.visibility = View.VISIBLE
                    cv!!.visibility  =View.INVISIBLE
                    label!!.text="Verifying..."
                    setUpCall(result.contents.toString())
                }catch (e:Exception){
                    Log.e("Error",e.toString())
                }
            }
        }else{
            Toast.makeText(this,"An error occurred", Toast.LENGTH_SHORT).show()

        }
        if(requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        cameraTask()
    }

}