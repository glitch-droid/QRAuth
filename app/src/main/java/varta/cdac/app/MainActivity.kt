package varta.cdac.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import varta.cdac.app.model.LoginResponse
import varta.cdac.app.services.ApiService


class MainActivity : AppCompatActivity(), EasyPermissions.RationaleCallbacks, EasyPermissions.PermissionCallbacks {

    private var enterCode : Button? = null
    private var scanCode : Button? = null
    private var cv1 : CardView? = null
    private var cv2 : CardView? = null
    private var scannedCode : TextView? = null
    private var label : TextView? =null
    private var btnSend : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enterCode = findViewById(R.id.enter_code_button)
        scanCode = findViewById(R.id.scan_code_button)
        cv1 = findViewById(R.id.cardView1)
        cv2 = findViewById(R.id.cardView2)
        scannedCode = findViewById(R.id.code_TV)
        label = findViewById(R.id.helperTV)
        btnSend = findViewById(R.id.sendCode)

        label!!.text = "Scan Code Here"
        cv2!!.visibility = View.VISIBLE

        scanCode!!.setOnClickListener {
            cv2!!.visibility = View.VISIBLE
            cv1!!.visibility = View.GONE
            label!!.text = "Scan Code Here"

        }
        cv2!!.setOnClickListener{

            cameraTask()
        }
        enterCode!!.setOnClickListener {
            cv2!!.visibility = View.GONE
            cv1!!.visibility = View.VISIBLE
            label!!.text = "Enter Code Here"
        }

        btnSend!!.setOnClickListener {
            if( scannedCode!!.text.toString().isEmpty()){
                Toast.makeText(this,"QR code not readeable or invalid", Toast.LENGTH_SHORT).show()
            }else{
                val value = scannedCode!!.text.toString()
                //Toast.makeText(this,value, Toast.LENGTH_SHORT).show()
                setUpCall(value);
            }
        }
    }

    private fun setUpCall(value: String) {
        val gson = GsonBuilder().setLenient().create()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://cdacvarta.herokuapp.com/")
            .build()

        val testApi = retrofitBuilder.create(ApiService::class.java)
        //val data = Data(1, 1, "QR Auth", value);
        //val paramObject = JSONObject()
        //paramObject.put("QRtoken")
        val modified_value = "{$value}";
        val tokenObject = LoginResponse(modified_value)
        val call = testApi.login(value)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText(this@MainActivity, "Response Code " + response.code().toString(), Toast.LENGTH_LONG).show()
                if(response.code() == 400)
                {

                    Log.d("A",response.message() );
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("A",t.message.toString() );
                Toast.makeText(this@MainActivity, "Error in sending QR code " + t.message.toString(),Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun hasCameraAccess() : Boolean
    {
        return EasyPermissions.hasPermissions(this,android.Manifest.permission.CAMERA)
    }

    private fun cameraTask(){
        if(hasCameraAccess()){
            var qrScanner = IntentIntegrator(this)
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
                Toast.makeText(this,"Not found", Toast.LENGTH_SHORT).show()
                scannedCode!!.setText("")
            }else{
                try{
                    cv1!!.visibility = View.VISIBLE
                    cv2!!.visibility = View.GONE
                    scannedCode!!.setText(result.contents.toString())
                }catch (exception: JSONException){
                    Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    scannedCode!!.setText("")

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
        TODO("Not yet implemented")
    }

}