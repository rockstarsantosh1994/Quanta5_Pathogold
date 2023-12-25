package com.bms.pathogold_bms.fragment

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface

class ViewAppDetailedFragment : BaseFragment(), View.OnClickListener {

    //Declaration....
    private val TAG = "ViewAppDetailedActivity"

    //Toolbar declaration...
    private var toolbar: Toolbar? = null

    //TextView declaration...
    private var tvAddress:TextView?=null
    private var tvTestName:TextView?=null
    private var tvDay:TextView?=null
    private var tvTimeSlot:TextView?=null
    private var tvPrice:TextView?=null

    //TextInputLayout declaration..
    private var tilStatus:TextInputLayout?=null
    private var tilRemark:TextInputLayout?=null

    //TextInputEditText declaration..
    private var etStatus:TextInputEditText?=null
    private var etRemark:TextInputEditText?=null

    //AppCompatButton declaration..
    private var btnUpdateAppointment:AppCompatButton?=null

    val statusArrayList=ArrayList<String>()
    var viewAppointmentBO:ViewAppointmentBO?=null
    var stType:String?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments=requireArguments()
        viewAppointmentBO=arguments.getParcelable("view_appoint")
        stType=arguments.getString("type")
        Log.e(TAG, "onCreate: $viewAppointmentBO")

        //basic intialisation....
        initViews(view)

        //setData to textviews..
        if(viewAppointmentBO!=null){
            setData()
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_view_app_detailed

    @SuppressLint("SetTextI18n")
    private fun initViews(view: View) {

        //Textview binding..
        tvAddress=view.findViewById(R.id.tv_address)
        tvDay=view.findViewById(R.id.tv_day)
        tvTestName=view.findViewById(R.id.tv_test_name)
        tvPrice=view.findViewById(R.id.tv_price)
        tvTimeSlot=view.findViewById(R.id.tv_time_slot)

        //TextInputLayout declaration..
        tilStatus=view.findViewById(R.id.til_status)
        tilRemark=view.findViewById(R.id.til_remark)

        //TextInputEditText declaration..
        etStatus=view.findViewById(R.id.et_status)
        etRemark=view.findViewById(R.id.et_remark)

        //AppCompatButton declaration
        btnUpdateAppointment=view.findViewById(R.id.btn_update_status)

        //Click Listeneres...
        btnUpdateAppointment?.setOnClickListener(this)
        etStatus?.setOnClickListener(this)

        when {
            stType?.equals("Approve",true)==true -> {
                etStatus?.setText("Accepted")
                statusArrayList.clear()
                statusArrayList.add("Accepted")
                statusArrayList.add("Pending")
                statusArrayList.add("Declined By Patient")
                statusArrayList.add("Declined By Lab Boy")
                statusArrayList.add("Complete")
                statusArrayList.add("Cancel")
            }
            stType?.equals("Pending",true)==true -> {
                statusArrayList.clear()
                etStatus?.setText("Pending")
                statusArrayList.add("Pending")
                statusArrayList.add("Accepted")
                statusArrayList.add("Declined By Patient")
                statusArrayList.add("Declined By Lab Boy")
                statusArrayList.add("Complete")
                statusArrayList.add("Cancel")
            }
            else -> {
                statusArrayList.add("Accepted")
                statusArrayList.add("Pending")
                statusArrayList.add("Declined By Patient")
                statusArrayList.add("Declined By Lab Boy")
                statusArrayList.add("Complete")
                statusArrayList.add("Cancel")
            }
        }
    }

    override fun onClick(p0: View?) {
       when(p0?.id){
           R.id.et_status -> {
               val spinnerDialogDistrict = SpinnerDialog(requireActivity(),
                   statusArrayList,
                   "Select Status",
                   R.style.DialogAnimations_SmileWindow,
                   "Close") // With 	Animation

               spinnerDialogDistrict.bindOnSpinerListener { item: String, position: Int ->
                   etStatus?.setText(item)
                   //stDistrict = item
               }
               spinnerDialogDistrict.showSpinerDialog()
           }

           R.id.btn_update_status -> {
                updateAppointmentStatus()
           }
       }
    }

    private fun updateAppointmentStatus() {
        val params: MutableMap<String, String> = HashMap()
        params["BookApp_Id"] = viewAppointmentBO?.BookApp_Id.toString()
        params["Status"] = etStatus?.text.toString()
        params["Remark"] = etRemark?.text.toString()

        Log.e(TAG, "updateAppointmentStatus:\n $params ")
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.updateViewAppointment(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        if(etStatus?.text.toString().equals("Accepted",true)){
                            Toast.makeText(mContext, "" + commonResponse.Message, Toast.LENGTH_LONG).show()
                            patientAppointmentConfirm(viewAppointmentBO?.BookApp_Id.toString())
                        }else{
                           // finish()
                            Toast.makeText(mContext, "" + commonResponse.Message, Toast.LENGTH_LONG).show()
                        }

                    } else {
                        showDialog(commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun patientAppointmentConfirm(appointmentId:String){
        val params: MutableMap<String, String> = HashMap()
        params["AppBookingId"] =appointmentId
        params["Companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "patientAppointmentConfirm:\n $params ")
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.patientAppointmentConfirm(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val commonResponse = `object` as CommonResponse
                progress.dismiss()
                Log.e(TAG, "patientAppointmentConfirm onSuccess: $commonResponse")
                if (commonResponse.ResponseCode == 200) {
                    mContext?.let { showDialogForSuccess(it,commonResponse.Message) }
                } else {
                    mContext?.let { CommonMethods.showDialogForError(it,commonResponse.Message) }
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    fun showDialogForSuccess(context: Context, message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                (context as DashBoardActivity).navController.popBackStack(R.id.viewAppDetailedFragment, true)
                //context.finish()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    private fun setData(){
        tvAddress?.text=viewAppointmentBO?.FullAddress
        tvTestName?.text=viewAppointmentBO?.Testname
        tvDay?.text=viewAppointmentBO?.Day
        tvTimeSlot?.text=viewAppointmentBO?.Timeslot
        tvPrice?.text=viewAppointmentBO?.Price
        etRemark?.setText(viewAppointmentBO?.Remark)
    }

    private fun showDialog(message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(requireActivity())
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            //.setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }*/


}