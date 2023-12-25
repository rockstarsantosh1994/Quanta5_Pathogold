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
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.service.GetServiceBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.card.MaterialCardView
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import kotlin.math.roundToLong

class BMSCheckOutActivity : BaseActivity(), PaymentResultWithDataListener, View.OnClickListener {

    private val TAG = "BMSCheckOutActivity"

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

    //Business Object.
    private var getServiceBO:GetServiceBO?=null
    private var totalPayment="0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialisation..
        initViews()

        Checkout.preload(applicationContext)

        getServiceBO = intent?.getParcelableExtra("service_bo")
        totalPayment= intent?.getStringExtra("total_payment").toString()

        navigateToPaymentGateway(getServiceBO!!)
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
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
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

    private fun navigateToPaymentGateway(serviceBO: GetServiceBO) {
        //Calling Intent...
        val stAmount =totalPayment
        //val stAmount="1"
        val amount = (stAmount.toFloat()!! * 100).roundToLong()
        //Initialise RazorPay Checkout..
        val checkOut = Checkout()
        //SetKey ID
       // checkOut.setKeyID(AllKeys.MERCHANT_ID_RAZORPAY) //For testing purpose you can uncomment this...
        checkOut.setKeyID(serviceBO.merchantid)// live merchant id...
        //Intialise Json Object..
        try {
            val jsonObject = JSONObject()
            jsonObject.put("name", CommonMethods.getPrefrence(mContext!!,AllKeys.PERSON_NAME))
            jsonObject.put("description", CommonMethods.getPrefrence(mContext!!,AllKeys.ADDRESS))
            jsonObject.put("theme.color", resources.getColor(R.color.purple_700))
            jsonObject.put("currency", "INR")
            jsonObject.put("amount", amount)
            jsonObject.put("image", AllKeys.LOGO_URL)
            jsonObject.put("prefill.contact", CommonMethods.getPrefrence(mContext!!,AllKeys.MOBILE_NO))
            //jsonObject.put("prefill.contact", "9527956414")
            jsonObject.put("prefill.email", CommonMethods.getPrefrence(mContext!!, AllKeys.EMAIL))

            //OpenRazorPay Checkout Activity..
            checkOut.open(this@BMSCheckOutActivity, jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onPaymentSuccess(p0: String?, payementData: PaymentData?) {
        try{
            tvTransactionTime?.text = CommonMethods.getTodayDate("HH:mm:ss a dd, MMM yyyy")
            tvPaymentRs?.text = totalPayment
            tvTransactionIDValue?.text = p0.toString()
            tvPaidToLab?.text = resources.getString(R.string.app_name)
            tvPaidToLabDetails?.text = "Thanks, your payment of Rs." +totalPayment.toString() + ", your transaction id is " + p0.toString() + " , in case of difficulty, you can connect with support."

            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                insertUpdatedDetailsOfPayment(p0)
            } else {
                CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
            }

        }catch (e:Exception){
            Log.e(TAG, "onPaymentSuccess: "+e.printStackTrace() )
        }
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

    private fun insertUpdatedDetailsOfPayment(transactionID: String?) {
        val params: MutableMap<String, String> = HashMap()
        val amtPaid=totalPayment.toFloat().toInt()
        params["amtpaid"] =amtPaid.toString()
        params["labcode"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        params["servicecode"] = getServiceBO?.Servicecode.toString()
        //params["amtpaid"] ="1"
        params["servicename"] = getServiceBO?.Servicename.toString()
        params["modeofpay"]=AllKeys.RAZOR_PAY+"_"+transactionID

        Log.e(TAG, "insertUpdatedDetailsOfPayment: $params")

        val progress = ProgressDialog(this@BMSCheckOutActivity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertUpdatedAdvanceService(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "insertUpdatedDetailsOfPayment: $commonResponse")
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
                val intent = Intent(mContext, DashBoardActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                dialogInterface.dismiss()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }
}