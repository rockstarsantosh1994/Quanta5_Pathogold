package com.bms.pathogold_bms.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterResponse
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterSummaryBO
import com.bms.pathogold_bms.model.otp.OtpResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.HashMap

class LogInWithOtpActivty : BaseActivity(), View.OnClickListener {

    private val TAG = "LogInWithOtpActivty"

    //basic declaration...
    //TextInputLayout declaration..
    private var tilMobileNo:TextInputLayout?=null
    
    //TextInputEditText declaration..
    private var etMobileNo:TextInputEditText?=null

    //TextView declaration..
    private var tvLoginTypeText: TextView?=null

    //AppCompatButton declaration..
    private var btnGetOtp:AppCompatButton?=null
    private var btnLoginWithUserPwd:AppCompatButton?=null

    private var stLoginType:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stLoginType=intent.getStringExtra("login_type")

        //Basic intialisation...
        initViews()
    }

    override val activityLayout: Int
        get() = R.layout.activity_log_in_with_otp_activty

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        //TextInputLayout binding..
        tilMobileNo=findViewById(R.id.til_mobile_number)

        //TextEditText binding..
        etMobileNo=findViewById(R.id.et_mobile_number)

        //TextView binding..
        tvLoginTypeText=findViewById(R.id.tv_login_type_text)

        //AppcompatButton binding..
        btnGetOtp=findViewById(R.id.btn_get_otp)
        btnLoginWithUserPwd=findViewById(R.id.btn_login_with_user_pwd)

        //Click Listeners..
        btnGetOtp?.setOnClickListener(this)
        btnLoginWithUserPwd?.setOnClickListener(this)

        if(stLoginType.equals(AllKeys.Administrator)){
            tvLoginTypeText?.text= "Lab/Admin login"
        }else{
            tvLoginTypeText?.text= "$stLoginType login"
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_get_otp->{
                if(isValidated()){
                    sendOtp()
                }
            }

            R.id.btn_login_with_user_pwd->{
                val intent=Intent(mContext!!,LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("login_type",stLoginType)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun sendOtp() {
        val params: MutableMap<String, String> = HashMap()
        params["mobile"] = etMobileNo?.text.toString()

        Log.e(TAG, "getDailyCashSummary: $params" )
        val progress = ProgressDialog(this@LogInWithOtpActivty)
        progress.setMessage("Sending Otp...Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.verifyPatientByOtp(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val otpResponse = `object` as OtpResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $otpResponse")
                if (otpResponse.ResponseCode == 200) {
                    if (otpResponse.ResultArray.size > 0) {
                        val intent = Intent(mContext, OtpActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("otp",otpResponse.ResultArray[0].otp)
                        intent.putExtra("mobile",etMobileNo?.text.toString())
                        intent.putExtra("login_type",getIntent().getStringExtra("login_type"))
                        startActivity(intent)
                    }else{
                        CommonMethods.showDialogForError(mContext!!,AllKeys.SERVER_MESSAGE)
                    }
                }else{
                    CommonMethods.showDialogForError(mContext!!,AllKeys.SERVER_MESSAGE)
                }
            }
            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun isValidated(): Boolean {
        if(etMobileNo?.text.toString().isEmpty()){
            tilMobileNo?.error="Mobile No. Required!"
            tilMobileNo?.requestFocus()
            return false
        }else{
            tilMobileNo?.isErrorEnabled = false
        }

        if(etMobileNo?.text.toString().length!=10){
            tilMobileNo?.error="Invalid Mobile No.!"
            tilMobileNo?.requestFocus()
            return false
        }else{
            tilMobileNo?.isErrorEnabled = false
        }

        return true
    }
}