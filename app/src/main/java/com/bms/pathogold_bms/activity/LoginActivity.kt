package com.bms.pathogold_bms.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.ui.text.toLowerCase
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import com.bms.pathogold_bms.model.login.LoginBO
import com.bms.pathogold_bms.model.login.LoginResponse
import com.bms.pathogold_bms.services.ApiRequestHelper.OnRequestComplete
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import java.util.*
import kotlin.collections.HashMap

//AllKeys.LABNAME:- code of the lab which is required to api in whole project.
//AllKeys.LABNAME_C:- full name of the lab.
//AllKeys.USERNAME:- username of the particular user which is required to api in whole project
//AllKeys.PATIENT_PROFILE_ID:- unique patient id. eg:- 12940 ......regno is different...

class LoginActivity: BaseActivity(), View.OnClickListener {

    private val TAG = "LoginActivity"

    //ImageView Declaration...
    private var ivDiagnosticsLogo:ImageView?=null
    private var ivUserLogo:ImageView?=null

    //TextInputLayout and Editext Declaration..
    private var tilUserName:TextInputLayout?=null
    private var etUserName:TextInputEditText?=null
    private var tilPassWord:TextInputLayout?=null
    private var etPassword:TextInputEditText?=null

    //CheckBox Declaration..
    private var chRememberMe:CheckBox?=null

    //TextView Declaration..
    private var tvForgotPassword:TextView?=null
    private var tvLoginTypeText:TextView?=null

    //AppCompatButton Declaration...
    private var btnLogin:AppCompatButton?=null
    private var btnLoginWithOtp:AppCompatButton?=null

    private var stLoginType:String?=null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stLoginType=intent.getStringExtra("login_type")

        //Basic intialisation...
        initViews()

        //Get Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.e(TAG, "onCreate: $token" )
            CommonMethods.setPreference(mContext!!, AllKeys.GCM_TOKEN, token)
        })

        if(stLoginType.equals(AllKeys.Administrator)){
            tvLoginTypeText?.text= "Lab/Admin login"
        }else{
            tvLoginTypeText?.text= "$stLoginType login"
        }
    }

    private fun initViews() {
        //Binding all views...
        //ImageView binding..
        ivDiagnosticsLogo=findViewById(R.id.iv_diagnostics_logo)
        ivUserLogo=findViewById(R.id.iv_user_logo)

        //TextView binding..
        tvForgotPassword=findViewById(R.id.tv_forgot_password)
        tvLoginTypeText=findViewById(R.id.tv_login_type_text)

        //CheckBox binding..
        chRememberMe=findViewById(R.id.ch_remember_me)

        //TextInputLayout and Editext binding...
        tilUserName=findViewById(R.id.til_username)
        etUserName=findViewById(R.id.et_username)
        tilPassWord=findViewById(R.id.til_password)
        etPassword=findViewById(R.id.et_password)

        //AppCompatButton declaration...
        btnLogin=findViewById(R.id.btn_signin)
        btnLoginWithOtp = findViewById(R.id.btn_login_with_otp)

        //setOnClickListeners..
        btnLogin?.setOnClickListener(this)
        btnLoginWithOtp?.setOnClickListener(this)
        chRememberMe?.setOnClickListener(this)
        tvForgotPassword?.setOnClickListener(this)

        if(mContext?.let { CommonMethods.getPrefrence(it,AllKeys.REMEMBER_ME).equals("1") } == true){
            when(stLoginType.equals(CommonMethods.getPrefrence(mContext!!,AllKeys.REMEMBER_ME_LOGIN_TYPE))){
                true -> {
                    chRememberMe?.isChecked=true
                    etUserName?.setText(CommonMethods.getPrefrence(mContext!!,AllKeys.ET_USERNAME))
                    etPassword?.setText(CommonMethods.getPrefrence(mContext!!,AllKeys.ET_PASSWORD))
                }

                false ->{
                    chRememberMe?.isChecked=false
                    etUserName?.setText("")
                    etPassword?.setText("")
                }
            }
        }
    }

    override val activityLayout: Int get() = R.layout.activity_login

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_signin -> {
                if (isValidated()) {
                    //authenticate User...
                        if(CommonMethods.isNetworkAvailable(mContext!!)){
                            login()
                        }else{
                            CommonMethods.showDialogForError(mContext!!,AllKeys.NO_INTERNET_AVAILABLE)
                        }
                }
            }

            R.id.btn_login_with_otp->{
                val intent = Intent(mContext, LogInWithOtpActivty::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("login_type",stLoginType)
                startActivity(intent)
            }

            R.id.ch_remember_me -> {
                if(isValidated()){
                    if(chRememberMe?.isChecked == true){
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.REMEMBER_ME,"1") }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.ET_USERNAME,etUserName?.text.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.ET_PASSWORD,etPassword?.text.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.REMEMBER_ME_LOGIN_TYPE,stLoginType) }
                    }else{
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.REMEMBER_ME,"0") }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.ET_USERNAME,AllKeys.DNF) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.ET_PASSWORD,AllKeys.DNF) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.REMEMBER_ME_LOGIN_TYPE,AllKeys.DNF) }
                    }
                }
            }
        }
    }

    private fun login(){
        val params: MutableMap<String, String> = HashMap()
        params["UserId"] = etUserName!!.text.toString()
        params["Password"] = etPassword!!.text.toString()
        params["Token"] = CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN).toString()
        params["usertype"]=stLoginType.toString()

        Log.e(TAG, "login: $params")

        val progress = ProgressDialog(this@LoginActivity)
        progress.setMessage("Logging In...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.login(params, object : OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val loginResponse = `object` as LoginResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $loginResponse")
                if (loginResponse.ResponseCode == 200) {
                    if(stLoginType.equals(loginResponse.ResultArray[0].UserType, ignoreCase = true) || AllKeys.Administrator == loginResponse.ResultArray[0].UserType){
                        //Saving credentials informartion in shared preference...
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.HSC_ID,loginResponse.ResultArray[0].HSC_ID) }
                        if(loginResponse.ResultArray[0].UserType == AllKeys.Patient){
                            mContext?.let { CommonMethods.setPreference(it,AllKeys.PATIENT_PROFILE_ID,loginResponse.ResultArray[0].PatientProfileID) }
                        }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.MOBILE_NO,loginResponse.ResultArray[0].MobileNo) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.LABNAME,loginResponse.ResultArray[0].LabName) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.PERSON_NAME,loginResponse.ResultArray[0].PersonName) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.SEX,loginResponse.ResultArray[0].Sex)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.DOB,loginResponse.ResultArray[0].Dob) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.EMAIL,loginResponse.ResultArray[0].Email) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.ADDRESS,loginResponse.ResultArray[0].Address) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.USERTYPE,loginResponse.ResultArray[0].UserType) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.LABNAME_C,loginResponse.ResultArray[0].LabName_C) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.USER_NAME,loginResponse.ResultArray[0].UserName) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.appointment,loginResponse.ResultArray[0].appointment) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.patientregistration, loginResponse.ResultArray[0].patientregistration.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.vital,loginResponse.ResultArray[0].vital.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.fileupload,loginResponse.ResultArray[0].fileupload.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.report_download,loginResponse.ResultArray[0].report_download.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.my_distance,loginResponse.ResultArray[0].my_distance.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.Prescription,loginResponse.ResultArray[0].Prescription.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.videocall,loginResponse.ResultArray[0].videocall.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.vaccination,loginResponse.ResultArray[0].vaccination.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.viewprescription,loginResponse.ResultArray[0].viewprescription.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.CURRENCY_SYMBOL,loginResponse.ResultArray[0].currency)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.dailycashregister,loginResponse.ResultArray[0].Dailycashregister.toString())}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.reportdetails,loginResponse.ResultArray[0].Reportdetail.toString())}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.MERCHANT_ID_RAZORPAY,loginResponse.ResultArray[0].marchantid)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.PATH,loginResponse.ResultArray[0].Path)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.SUPPORT_ID,loginResponse.ResultArray[0].supportuserid)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.LOGO_PATH,loginResponse.ResultArray[0].logoPath)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.PAYU_MERCHANT_KEY,loginResponse.ResultArray[0].Payu_marchantid)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.PAYU_MERCHANT_SALT,loginResponse.ResultArray[0].Payu_marchantsaltid)}
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.COMPANY_ID,loginResponse.ResultArray[0].companyid)}

                        mContext?.let { CommonMethods.setPreference(it,AllKeys.ET_USERNAME,etUserName?.text.toString()) }
                        mContext?.let { CommonMethods.setPreference(it,AllKeys.ET_PASSWORD,etPassword?.text.toString()) }

                        if(loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()) == AllKeys.ACTIVE || loginResponse.ResultArray[0].UserType == AllKeys.Patient || loginResponse.ResultArray[0].balance == "0") {
                            //Calling Intent...
                            mContext?.let { CommonMethods.setPreference(it,AllKeys.LOGIN_VIA,AllKeys.VIA_USERNAME_PASSWORD)}
                            val intent = Intent(mContext, DashBoardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }else{
                            showDialogForError(mContext!!,AllKeys.ACCOUNT_IS_NOT_ACTIVATED,loginResponse.ResultArray[0])
                        }
                        //Toast.makeText(mContext, "" + loginResponse.Message, Toast.LENGTH_SHORT).show()
                    }else{
                        mContext?.let { CommonMethods.showDialogForError(it,resources.getString(R.string.you_don_have_authority_to_login)) }
                    }
                } else {
                    showDialog(loginResponse.Message)
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                showDialog(AllKeys.SERVER_MESSAGE)
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
                        intent.putExtra("isLogin","no")
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
                        intent.putExtra("isLogin","no")
                        intent.putExtra("payment_gateway",AllKeys.PAYU)
                        startActivity(intent)
                    }
            }
        }
        pictureDialog.show()
    }

    private fun showDialog(message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(this@LoginActivity)
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

    private fun isValidated():Boolean{

        if(etUserName?.text.toString().isEmpty()){
            tilUserName?.error="Username Required!"
            tilUserName?.requestFocus()
            return false
        }

        if(etPassword?.text.toString().isEmpty()){
            tilPassWord?.error="Password required!"
            tilPassWord?.requestFocus()
            return false
        }
        return true
    }
}