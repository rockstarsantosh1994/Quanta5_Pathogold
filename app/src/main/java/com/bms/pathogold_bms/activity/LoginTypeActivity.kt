package com.bms.pathogold_bms.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.LoginTypeAdapter
import com.bms.pathogold_bms.model.DashBoardBO
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*

////Login Types......
////Consultant:- it is "Doctor" cardview
////Administrator or Lab Technician:- it is "Laboratorist" cardview
//Phlebotomist:- It is "Phlebotomist" cardview
//Patient:- It is "Patient" cardview..
class LoginTypeActivity : BaseActivity() ,LoginTypeAdapter.LoginTypeClicks{
    private  val TAG = "LoginTypeActivity"

    private val PERMISSION_ID = 44

    //ImageView declaration...
    private lateinit var ivLogo: ImageView

    //RecyclerView declaration...
    private var rvLoginType:RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialisation..
        initViews()

        //request run time permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions()
        }
        /* setContent {
             AccompanistSampleTheme {
                 val multiplePermissionsState = rememberMultiplePermissionsState(
                     listOf(
                         android.Manifest.permission.READ_EXTERNAL_STORAGE,
                         android.Manifest.permission.CAMERA,
                     )
                 )
                 Sample(multiplePermissionsState)
             }
         }*/
    }

    override val activityLayout: Int
        get() = R.layout.activity_login_type

    private fun initViews() {
        //ImageView intialisation..
        ivLogo=findViewById(R.id.iv_logo)

        //Recycler view binding..
        rvLoginType=findViewById(R.id.rv_login_type)
        //val gridLayoutManager=GridLayoutManager(mContext!!,2)
        //rvLoginType?.layoutManager=gridLayoutManager
        rvLoginType?.layoutManager = object : GridLayoutManager(mContext!!,2){ override fun canScrollVertically(): Boolean { return false } }

        //Declare arraylist...
        val dashboardBOArrayList=ArrayList<DashBoardBO>()
        when (BuildConfig.FLAVOR) {
            "pathogoldfrench" -> {
                dashboardBOArrayList.add(DashBoardBO(R.drawable.patient_icon,R.string.patient))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.lab_icon,R.string.laboratory))
            }

            "quanta5" -> {
                dashboardBOArrayList.add(DashBoardBO(R.drawable.patient_icon,R.string.patient))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.lab_icon,R.string.admin)) //It is same Lab/admin
                dashboardBOArrayList.add(DashBoardBO(R.drawable.ic_administrator,R.string.super_admin))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.ic_chat_support,R.string.support))
            }

            else -> {
                dashboardBOArrayList.add(DashBoardBO(R.drawable.patient_icon,R.string.patient))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.book_appointment,R.string.book_app))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.doctor_icon,R.string.consultant_doctor))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.doctor_icon,R.string.reference_doctor))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.ic_money_collection,R.string.collection_center))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.phlebotomist_icon,R.string.phlebotomist))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.lab_icon,R.string.laboratory))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.ic_administrator,R.string.super_admin))
                dashboardBOArrayList.add(DashBoardBO(R.drawable.ic_chat_support,R.string.support))
            }
        }

       // dashboardBOArrayList.add(DashBoardBO(R.drawable.patient_icon,R.string.phonepe))
       // dashboardBOArrayList.add(DashBoardBO(R.drawable.patient_icon,R.string.payu))

        //set To adapter
        val loginTypeAdapter=LoginTypeAdapter(mContext!!,dashboardBOArrayList,this)
        rvLoginType?.adapter=loginTypeAdapter
    }

    // method to request for permissions
    @RequiresApi(33)
    private fun requestPermissions() {
         ActivityCompat.requestPermissions(
             this, arrayOf(
                 Manifest.permission.ACCESS_COARSE_LOCATION,
                 Manifest.permission.ACCESS_FINE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                 Manifest.permission.CAMERA,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE,
                 Manifest.permission.READ_EXTERNAL_STORAGE,
                 Manifest.permission.RECORD_AUDIO,
                 Manifest.permission.CALL_PHONE,
                 Manifest.permission.POST_NOTIFICATIONS
             ),
             PERMISSION_ID)
    }

    // If everything is alright then
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@LoginTypeActivity, "All Permsission granted...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClickBookAppointment() {
        checkLocation()
    }

    private fun checkLocation(){
        if(!isLocationEnabled()){
            showAlert()
           // mContext?.let { displayLocationSettingsRequest(it) }
        }else{
            val intent= Intent(mContext!!,LabDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) /*||
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)*/
    }

    private fun showAlert() {
        val dialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("""
    Your Locations Settings is set to 'Off'.
    Please Enable Location to use this feature.
    """.trimIndent())
            .setPositiveButton("Location Settings"
            ) { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(myIntent,2)
            }
            .setNegativeButton("Cancel"
            ) { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> Log.i(TAG,
                    "All location settings are satisfied.")
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(TAG,
                        "Location settings are not satisfied. Show the user a dialog to upgrade location settings ")
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this@LoginTypeActivity,
                            1001)
                    } catch (e: SendIntentException) {
                        Log.i(TAG, "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(TAG,
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==2){
            val intent= Intent(mContext!!,LabDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }

}