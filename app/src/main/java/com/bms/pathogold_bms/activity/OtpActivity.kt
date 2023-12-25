package com.bms.pathogold_bms.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import com.bms.pathogold_bms.model.login.LoginBO
import com.bms.pathogold_bms.model.login.LoginResponse
import com.bms.pathogold_bms.model.otp.OtpResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.gms.common.internal.service.Common
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class OtpActivity : BaseActivity(), View.OnClickListener {

    //Declaration.
    private val TAG = "OtpActivity"

    //TextView declaration..
    private var tvMobileNoText:TextView?=null
    private var tvResendOtp:TextView?=null

    //TextInputLayout declaration...
    private var tilOtp:TextInputLayout?=null

    //TextInputEditText declaration..
    private var etOtp:TextInputEditText?=null

    //AppCompatButton declaration...
    private var btnVerfiyOtp:AppCompatButton?=null

    //TextView declaration..
    private var tvLoginTypeText: TextView?=null

    //String declaration..
    private var stMobileNo:String?=null
    private var stOtp:String?=null
    private var stLoginType:String?=null

    //Declare timer
    private var cTimer: CountDownTimer? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialisation..
        initViews()

        //getIntent Extra..
        stMobileNo=intent?.getStringExtra("mobile")
        stOtp=intent?.getStringExtra("otp")
        stLoginType=intent?.getStringExtra("login_type")

        tvLoginTypeText?.text= "$stLoginType login"

        //Set mobile number to text..
        tvMobileNoText?.text = resources.getString(R.string.enter_the_otp_send_to_n)+"\n"+stMobileNo
    }

    override val activityLayout: Int
        get() = R.layout.activity_otp

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        //TextView binding..
        tvMobileNoText=findViewById(R.id.tv_mobile_number)
        tvResendOtp=findViewById(R.id.tv_resend_otp)

        //TextInputLayout binding..
        tilOtp=findViewById(R.id.til_otp)

        //TextInputEditText binding..
        etOtp=findViewById(R.id.et_otp)

        //AppCompatButton declaration...
        btnVerfiyOtp=findViewById(R.id.btn_verify_otp)

        //TextView binding..
        tvLoginTypeText=findViewById(R.id.tv_login_type_text)

        //Click Listeners...
        btnVerfiyOtp?.setOnClickListener(this)
        tvResendOtp?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_verify_otp->{
                if(isValidated()){
                     verfiyOtp()
                }
            }

            R.id.tv_resend_otp->{
                if(CommonMethods.isNetworkAvailable(mContext!!)){
                    resendOtp()
                }else{
                    CommonMethods.showDialogForError(mContext!!,AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        }
    }

    private fun verfiyOtp() {
        val params: MutableMap<String, String> = HashMap()
        params["mobile"] = stMobileNo.toString()
        params["Token"] = CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN).toString()
        params["usertype"] = stLoginType.toString()

        val progress = ProgressDialog(this@OtpActivity)
        progress.setMessage("Logging In...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.pateintLoginByMobile(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val loginResponse = `object` as LoginResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $loginResponse")
                if (loginResponse.ResponseCode == 200) {
                    if(stLoginType==loginResponse.ResultArray[0].UserType || AllKeys.Administrator == loginResponse.ResultArray[0].UserType){
                        //Saving credentials informartion in shared preference...
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.HSC_ID,loginResponse.ResultArray[0].HSC_ID) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.MOBILE_NO,loginResponse.ResultArray[0].MobileNo) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.LABNAME,loginResponse.ResultArray[0].LabName) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.PERSON_NAME,loginResponse.ResultArray[0].PersonName) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.SEX,loginResponse.ResultArray[0].Sex)}
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.DOB,loginResponse.ResultArray[0].Dob) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.EMAIL,loginResponse.ResultArray[0].Email) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.ADDRESS,loginResponse.ResultArray[0].Address) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.USERTYPE,loginResponse.ResultArray[0].UserType) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.LABNAME_C,loginResponse.ResultArray[0].LabName_C) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.USER_NAME,loginResponse.ResultArray[0].UserName) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.appointment,loginResponse.ResultArray[0].appointment) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.patientregistration, loginResponse.ResultArray[0].patientregistration.toString()) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.vital,loginResponse.ResultArray[0].vital.toString()) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.fileupload,loginResponse.ResultArray[0].fileupload.toString()) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.report_download,loginResponse.ResultArray[0].report_download.toString()) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.my_distance,loginResponse.ResultArray[0].my_distance.toString()) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.Prescription,loginResponse.ResultArray[0].Prescription.toString()) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.videocall,loginResponse.ResultArray[0].videocall.toString()) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.vaccination,loginResponse.ResultArray[0].vaccination.toString()) }
                        mContext?.let { CommonMethods.setPreference(it, AllKeys.viewprescription,loginResponse.ResultArray[0].viewprescription.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.CURRENCY_SYMBOL,loginResponse.ResultArray[0].currency.toString())}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.dailycashregister,loginResponse.ResultArray[0].Dailycashregister.toString())}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.reportdetails,loginResponse.ResultArray[0].Reportdetail.toString())}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.MERCHANT_ID_RAZORPAY,loginResponse.ResultArray[0].marchantid)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.PATH,loginResponse.ResultArray[0].Path)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.SUPPORT_ID,loginResponse.ResultArray[0].supportuserid)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.SUPPORT_TOKEN,loginResponse.ResultArray[0].support_token)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.LOGO_PATH,loginResponse.ResultArray[0].logoPath)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.PAYU_MERCHANT_KEY,loginResponse.ResultArray[0].Payu_marchantid)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.PAYU_MERCHANT_SALT,loginResponse.ResultArray[0].Payu_marchantsaltid)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.COMPANY_ID,loginResponse.ResultArray[0].companyid)}

                        if(loginResponse.ResultArray[0].UserType == AllKeys.Patient){
                            mContext?.let { CommonMethods.setPreference(it,
                                AllKeys.PATIENT_PROFILE_ID,loginResponse.ResultArray[0].PatientProfileID) }
                        }

                        if(loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()) == AllKeys.ACTIVE || loginResponse.ResultArray[0].UserType == AllKeys.Patient || loginResponse.ResultArray[0].balance == "0") {
                            //Calling Intent...
                            mContext?.let { CommonMethods.setPreference(it,AllKeys.LOGIN_VIA,AllKeys.VIA_OTP)}

                            val intent = Intent(mContext, DashBoardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }else{
                            showDialogForError(mContext!!,AllKeys.ACCOUNT_IS_NOT_ACTIVATED,loginResponse.ResultArray[0])
                        }

                        Toast.makeText(mContext, "" + loginResponse.Message, Toast.LENGTH_SHORT).show()
                    }else{
                        mContext?.let { CommonMethods.showDialogForError(it,resources.getString(R.string.you_don_have_authority_to_login)) }
                        navigateToPaymentGateway(loginResponse.ResultArray[0])
                    }
                } else {
                    CommonMethods.showDialogForError(mContext!!,loginResponse.Message)
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                CommonMethods.showDialogForError(mContext!!,apiResponse)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    fun showDialogForError(context: Context, message: String, loginBO: LoginBO) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            //.setAnimation(R.raw.success_exploration)
            .setPositiveButton("Make Payment") { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                navigateToPaymentGateway(loginBO)
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    private fun navigateToPaymentGateway(loginBO: LoginBO) {
        //Please consider loginBO object as getPatientList it is not a patient data..
        val getPatientListBO=GetPatientListBO(loginBO.HSC_ID
            ,loginBO.PersonName
            ,loginBO.UserName
            ,"",
            "",
            "",
            "",
            loginBO.MobileNo,"",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            loginBO.balance.replace("-",""),
            loginBO.balance.replace("-",""),
            "")

        val labNameBO=LabNameBO(loginBO.LabName,loginBO.LabName_C)

        Log.e(TAG, "navigateToPaymentGateway: $getPatientListBO")
        Log.e(TAG, "navigateToPaymentGateway: "+labNameBO )
        showPictureDialog(getPatientListBO,labNameBO,loginBO)
    }

    private fun showPictureDialog(
        getPatientListBO: GetPatientListBO,
        labNameBO: LabNameBO,
        loginBO: LoginBO
    ) {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Payment Gateway")
        val pictureDialogItems = arrayOf("RazorPay", "PayU Money"/*, "Preview"*/)
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 ->
                    if(loginBO.marchantid.isEmpty()){
                        CommonMethods.showDialogForError(mContext!!,"Payment Gateway not available!")
                    }else{
                        // Delete Operation
                        val intent = Intent(mContext!!, CheckOutActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("patient_bo", getPatientListBO)
                        intent.putExtra("labname_bo", labNameBO)
                        intent.putExtra("type", "cc_invoice")
                        intent.putExtra("payment_gateway",AllKeys.RAZOR_PAY)
                        startActivity(intent)
                    }
                1 ->
                    if(loginBO.Payu_marchantid.isEmpty() || loginBO.Payu_marchantsaltid.isEmpty()){
                        CommonMethods.showDialogForError(mContext!!,"Payment Gateway not available!")
                    }else{
                        // Delete Operation
                        val intent = Intent(mContext!!, CheckOutActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("patient_bo", getPatientListBO)
                        intent.putExtra("labname_bo", labNameBO)
                        intent.putExtra("type", "cc_invoice")
                        intent.putExtra("payment_gateway",AllKeys.PAYU)
                        startActivity(intent)
                    }
            }
        }
        pictureDialog.show()
    }

    private fun resendOtp() {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["mobile"] = stMobileNo.toString()

        Log.e(TAG, "getDailyCashSummary: $params", )
        val progress = ProgressDialog(this@OtpActivity)
        progress.setMessage("Resending Otp...Please wait...")
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
                        stOtp=otpResponse.ResultArray[0].otp
                        //start timer function
                        cTimer = object : CountDownTimer(30000, 1000) {
                            @SuppressLint("SetTextI18n")
                            override fun onTick(millisUntilFinished: Long) {
                                tvResendOtp?.isEnabled = false
                                tvResendOtp?.text = mContext?.getString(R.string.resend_otp)+"\n"+ TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)+" sec"
                            }

                            override fun onFinish() {
                                tvResendOtp?.isEnabled = true
                                tvResendOtp?.text = mContext?.getString(R.string.resend_otp)
                            }
                        }
                        cTimer!!.start()
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
        if(etOtp?.text.toString().isEmpty()){
            tilOtp?.error="Otp Required!"
            tilOtp?.requestFocus()
            return false
        }else{
            tilOtp?.isErrorEnabled = false
        }

        if(etOtp?.text.toString().length!=4){
            tilOtp?.error="Invalid Otp!"
            tilOtp?.requestFocus()
            return false
        }else{
            tilOtp?.isErrorEnabled = false
        }

        if(etOtp?.text.toString()!=stOtp){
            tilOtp?.error="Incorrect Otp enter!"
            tilOtp?.requestFocus()
            return false
        }else{
            tilOtp?.isErrorEnabled = false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if(cTimer!=null)
            cTimer!!.cancel()
    }
}