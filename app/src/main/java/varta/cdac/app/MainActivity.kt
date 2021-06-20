package varta.cdac.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.RationaleCallbacks, EasyPermissions.PermissionCallbacks {

    private var enterCode : Button? = null
    private var scanCode : Button? = null
    private var cv1 : CardView? = null
    private var cv2 : CardView? = null
    private var editCode : EditText? = null
    private var label : TextView? =null
    private var btnEnter : Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enterCode = findViewById(R.id.enter_code_button)
        scanCode = findViewById(R.id.scan_code_button)
        cv1 = findViewById(R.id.cardView1)
        cv2 = findViewById(R.id.cardView2)
        editCode = findViewById(R.id.code_ET)
        label = findViewById(R.id.helperTV)
        btnEnter = findViewById(R.id.codeEnter)

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

        btnEnter!!.setOnClickListener {
            if( editCode!!.text.toString().isEmpty()){
                Toast.makeText(this,"Please Enter Code", Toast.LENGTH_SHORT).show()
            }else{
                var value = editCode!!.text.toString()
                Toast.makeText(this,value, Toast.LENGTH_SHORT).show()
            }
        }
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
                editCode!!.setText("")
            }else{
                try{
                    cv1!!.visibility = View.VISIBLE
                    cv2!!.visibility = View.GONE
                    editCode!!.setText(result.contents.toString())
                }catch (exception: JSONException){
                    Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    editCode!!.setText("")

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