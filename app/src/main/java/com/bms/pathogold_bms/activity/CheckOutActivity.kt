package com.bms.pathogold_bms.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.PayuSuccessResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.HashGenerationUtils
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import kotlin.math.roundToLong

class CheckOutActivity : BaseActivity(), PaymentResultWithDataListener, View.OnClickListener {

    private val TAG = "CheckOutActivity"

    private var getPatientListBO: GetPatientListBO? = null
    private var labNameBO: LabNameBO? = null

    //TextView declaration...
    private var tvPaymentRs: TextView? = null
    private var tvTransactionIDValue: TextView? = null
    private var tvTransactionCopy: TextView? = null
    private var tvPaidToLab: TextView? = null
    private var tvPaidToLabDetails: TextView? = null
    private var tvTransactionTime: TextView? = null

    //CardView declaration..
    private var cvContactSupport: MaterialCardView? = null

    //AppCompatButton declaration...
    private var btnReturnHome: AppCompatButton? = null

    //Relative layout declaration...
    private var rlMain: RelativeLayout? = null

    private var selectedArrayList=ArrayList<GetTestCodeBO>()

    private var amount:String="0"
    private var checkOutFor:String="0"
    private var discount:String="0"
    private var paymentGateway:String="0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkOutFor = intent?.getStringExtra("type")!!

        //basic intialisation..
        initViews()

        Checkout.preload(applicationContext)

        getPatientListBO = intent?.getParcelableExtra("patient_bo")
        paymentGateway= intent?.getStringExtra("payment_gateway")!!

        if(intent?.getParcelableArrayListExtra<GetTestCodeBO>("selected_test")!=null)
        selectedArrayList = intent?.getParcelableArrayListExtra("selected_test")!!

        discount= intent?.getStringExtra("discount").toString()

        Log.e(TAG, "onCreate: "+amount)

        amount = if(checkOutFor=="patient_registration_invoice"){
            intent?.getStringExtra("final_amount")!!
        }else {
            getPatientListBO?.balance.toString()
        }

        labNameBO = if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            intent?.getParcelableExtra("labname_bo")
        } else {
            LabNameBO(CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString(), CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME_C).toString())
        }

        if(intent?.getStringExtra("payment_gateway").equals(AllKeys.RAZOR_PAY)){
            navigateToRazorPaymentGateway()
        }else{
            Log.e(TAG, "onCreate: Inside Pay U Money")
            navigateToPayuPaymentGateway()
        }
    }

    override val activityLayout: Int
        get() = R.layout.activity_check_out

    private fun initViews() {
        //TextView binding...
        tvPaymentRs = findViewById(R.id.tv_payment_rupeees)
        tvTransactionIDValue = findViewById(R.id.tv_transactionid_value)
        tvTransactionTime = findViewById(R.id.tv_transaction_time)
        tvTransactionCopy = findViewById(R.id.tv_copy_transaction_value)
        tvPaidToLab = findViewById(R.id.tv_paid_to_labname)
        tvPaidToLabDetails = findViewById(R.id.tv_paid_to_details)

        //CardView binding..
        cvContactSupport = findViewById(R.id.cv_contact_support)

        //AppCompatButton binding...
        btnReturnHome = findViewById(R.id.btn_return)

        //Relative layout binding..
        rlMain = findViewById(R.id.rl_main)

        //ClickListerners..
        btnReturnHome?.setOnClickListener(this)
        tvTransactionCopy?.setOnClickListener(this)
        cvContactSupport?.setOnClickListener(this)
    }

    @SuppressLint("ServiceCast")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_return -> {
                val intent = Intent(mContext, DashBoardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

            R.id.tv_copy_transaction_value -> {
                val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", tvTransactionIDValue?.text.toString())
                manager.setPrimaryClip(clipData)
                Toast.makeText(applicationContext, "Copied :)", Toast.LENGTH_SHORT).show()
            }

            R.id.cv_contact_support -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+91 90110 26090"))
                startActivity(intent)
            }
        }
    }

    private fun navigateToRazorPaymentGateway() {
        //Calling Intent...
        //val stAmount="10"
        val amount = (amount.toFloat() * 100).roundToLong()
        //Initialise RazorPay Checkout..
        val checkOut = Checkout()
        //SetKey ID
        //checkOut.setKeyID(AllKeys.MERCHANT_ID_RAZORPAY_TEST) //For testing purpose you can use this uncomment this...
        checkOut.setKeyID(CommonMethods.getPrefrence(mContext!!, AllKeys.MERCHANT_ID_RAZORPAY))// live merchant id...
        //Intialise Json Object..
        try {
            val jsonObject = JSONObject()
            jsonObject.put("name", getPatientListBO?.PatientName)
            jsonObject.put("description", getPatientListBO?.Remark)
            jsonObject.put("theme.color", resources.getColor(R.color.purple_700))
            jsonObject.put("currency", "INR")
            jsonObject.put("amount", amount)
            jsonObject.put("image", CommonMethods.getPrefrence(mContext!!, AllKeys.LOGO_PATH))
            jsonObject.put("prefill.contact", getPatientListBO?.PatientPhoneNo)
            //jsonObject.put("prefill.contact", "9527956414")
            jsonObject.put("prefill.email", CommonMethods.getPrefrence(mContext!!, AllKeys.EMAIL))

            //OpenRazorPay Checkout Activity..
            checkOut.open(this@CheckOutActivity, jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun navigateToPayuPaymentGateway() {
        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount(amount)
            .setIsProduction(true)
            .setKey(CommonMethods.getPrefrence(mContext!!,AllKeys.PAYU_MERCHANT_KEY))
            .setProductInfo("Payment of lab")
            .setPhone(getPatientListBO?.PatientPhoneNo)
            .setTransactionId(System.currentTimeMillis().toString())
            .setFirstName(getPatientListBO?.PatientName)
            .setEmail(CommonMethods.getPrefrence(mContext!!, AllKeys.EMAIL))
            .setSurl(AllKeys.surl)
            .setFurl(AllKeys.furl)
            //.setUserCredential("")
            //.setAdditionalParams(<HashMap<String,Any?>>) //Optional, can contain any additional PG params
            .build()

        PayUCheckoutPro.open(
            this, payUPaymentParams,
            object : PayUCheckoutProListener {
                override fun onPaymentSuccess(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]

                    val gson = Gson()
                    val fooFromJson: PayuSuccessResponse = gson.fromJson(payUResponse.toString(), PayuSuccessResponse::class.java)

                    //called success ui
                    successUIOfPaymentSuccess(fooFromJson.txnid)
                }

                override fun onPaymentFailure(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]

                    val gson = Gson()
                    val fooFromJson: PayuSuccessResponse = gson.fromJson(payUResponse.toString(), PayuSuccessResponse::class.java)
                    Log.e(TAG, "onPaymentSuccess:fooFromJson "+fooFromJson.toString() )


                    rlMain?.visibility = View.GONE
                    showDialogForError(mContext!!, "Payment Failed!")
                }


                override fun onPaymentCancel(isTxnInitiated:Boolean) {
                    Log.e(TAG, "onPaymentCancel: $isTxnInitiated")
                    rlMain?.visibility = View.GONE
                    showDialogForError(mContext!!, "Payment Cancelled By User!")
                }


                override fun onError(errorResponse: ErrorResponse) {
                    val errorMessage: String
                    if (errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                        errorMessage = errorResponse.errorMessage!!
                    else
                        errorMessage = AllKeys.SERVER_MESSAGE
                    Log.e(TAG, "onError: "+errorMessage.toString() )
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    //For setting webview properties, if any. Check Customized Integration section for more details on this
                }

                override fun generateHash(
                    valueMap: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener,
                ) {
                    if (valueMap.containsKey(PayUCheckoutProConstants.CP_HASH_STRING) && valueMap.containsKey(
                            PayUCheckoutProConstants.CP_HASH_NAME)) {

                        val hashData = valueMap[PayUCheckoutProConstants.CP_HASH_STRING]
                        val hashName = valueMap[PayUCheckoutProConstants.CP_HASH_NAME]

                        //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                        val hash: String? = HashGenerationUtils.generateHashFromSDK(hashData.toString(),CommonMethods.getPrefrence(mContext!!,AllKeys.PAYU_MERCHANT_SALT),"")
                        if (!TextUtils.isEmpty(hash)) {
                            val dataMap: HashMap<String, String?> = HashMap()
                            dataMap[hashName!!] = hash!!
                            hashGenerationListener.onHashGenerated(dataMap)
                        }
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    override fun onPaymentSuccess(p0: String?, payementData: PaymentData?) {
        successUIOfPaymentSuccess(p0)
    }

    override fun onPaymentError(p0: Int, p1: String?, paymentData: PaymentData?) {
        try {
            val obj = JSONObject(p1!!)
            val nestedObj = obj.getJSONObject("error")
            Log.e(TAG, "onPaymentError: $obj")
            Log.e("description value ", nestedObj.getString("description"))

            rlMain?.visibility = View.GONE
            showDialogForError(mContext!!, nestedObj.getString("description"))

        } catch (tx: Throwable) {
            Log.e("My App", "Could not parse malformed JSON: \"" + p1.toString() + "\"")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun successUIOfPaymentSuccess(transactionID: String?) {
        rlMain?.visibility=View.VISIBLE
        tvTransactionTime?.text = CommonMethods.getTodayDate("HH:mm:ss a dd, MMM yyyy")
        tvPaymentRs?.text = amount
        tvTransactionIDValue?.text = transactionID.toString()
        tvPaidToLab?.text = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME_C).toString()
        tvPaidToLabDetails?.text =
            "Thanks, your payment of Rs." + amount + ", your transaction id is " + transactionID.toString() + " , in case of difficulty, you can connect with lab."

        if(checkOutFor=="patient_registration_invoice"){
            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                patientInvoiceApi(transactionID)
            } else {
                CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
            }
        }else if(checkOutFor=="cc_invoice"){
            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                insertUpdatedClientPaymentDetails(transactionID)
            } else {
                CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
            }
        }else{
            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                insertUpdatedPayment(transactionID)
            } else {
                CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
            }
        }
    }

    private fun insertUpdatedClientPaymentDetails(transactionID: String?) {
        val params: MutableMap<String, String> = HashMap()
        params["labcode"] = labNameBO?.labname.toString()
        params["cccode"] = getPatientListBO?.regno.toString()
        params["paymentmode"] =paymentGateway+"_"+transactionID
        params["amount"] = amount
        params["companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["username"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME).toString()

        Log.e(TAG, "insertUpdatedClientPaymentDetails: $params")

        val progress = ProgressDialog(this@CheckOutActivity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertUpdateClientPaymentDetail(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "insertVaccinationPatient: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        //showDialogForSuccess(mContext!!, "Payment Successful. \n$transactionID")
                    } else {
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "insertVaccinationPatient: $apiResponse")
                }
            })
    }

    private fun insertUpdatedPayment(transactionID: String?){
        val params: MutableMap<String, String> = HashMap()
        params["labcode"] = labNameBO?.labname.toString()
        params["regno"] = getPatientListBO?.regno.toString()
        params["amtpaid"] = getPatientListBO?.balance.toString()
        // params["amtpaid"] ="1"
        params["billamount"] = getPatientListBO?.billamount.toString()
        params["modeofpay"] = paymentGateway+"_"+transactionID
        params["tranasctionid"] = transactionID.toString()
        params["username"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME).toString()

        Log.e(TAG, "insertVaccinationPatient: $params")

        val progress = ProgressDialog(this@CheckOutActivity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertUpdatedPayment(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "insertVaccinationPatient: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        //showDialogForSuccess(mContext!!, "Payment Successful. \n$transactionID")
                        val intent = Intent(mContext, DashBoardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "insertVaccinationPatient: $apiResponse")
                }
            })
    }

    private fun patientInvoiceApi(transactionID: String?) {
        val params: MutableMap<String, String> = HashMap()
        params["lab_code"] = labNameBO?.labname.toString()
        params["BillDate"] = CommonMethods.getTodayDate("MM/dd/yyyy")
        params["Remark"] = "Patient Payment"
        params["Modeofpay"] =paymentGateway+"_"+transactionID
        params["Discount"] = discount
        params["billamount"] =intent?.getStringExtra("total")!!
        params["PatRegNo"] =getPatientListBO?.regno.toString()
        params["patName"] =getPatientListBO?.PatientName.toString()
        val getTestId=ArrayList<String>()
        for(temp in selectedArrayList){
            getTestId.add(temp.tlcode)
        }
        params["Pattest"] =getTestId.toString().replace("[", "").replace("]", "")
        params["AmtPaid"] = amount
        params["Balance"] = amount
        params["username"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME).toString()
        params["Othercharges"] ="0"
        params["transid"] =transactionID.toString()
        params["companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["mobile"] =getPatientListBO?.PatientPhoneNo.toString()

        Log.e(TAG, "insertUpdatedDetailsOfPayment: $params")

        val progress = ProgressDialog(this@CheckOutActivity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.patientInvoiceNewAPI(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "insertUpdatedDetailsOfPayment: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        //showDialogForSuccess(mContext!!, "Payment Successful. \n$transactionID")
                        Toast.makeText(mContext, commonResponse.Message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "onSuccess: "+commonResponse.Message )
                    } else {
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "insertUpdatedDetailsOfPayment: $apiResponse")
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
                // Success Operation

                // val bundle=Bundle()
                //bundle.putParcelable("patient_bo", getPatientListBO)
                //Calling Intent...
                val intent = Intent(mContext, DashBoardActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                dialogInterface.dismiss()
                //context.finish()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    fun showDialogForError(context: Context, message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            //.setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok") { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                if(checkOutFor == "cc_invoice"){
                    if(intent?.getStringExtra("isLogin").equals("no")){
                        val intent = Intent(mContext, LoginTypeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(mContext, DashBoardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }else{
                    val intent = Intent(mContext, DashBoardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }
}