package com.bms.pathogold_bms.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.PayuSuccessResponse
import com.bms.pathogold_bms.model.login.LoginBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.HashGenerationUtils
import com.google.gson.Gson
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface


class PayuCheckOutActivity : BaseActivity() {

    private val TAG = "PayuCheckOutActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount("1.0")
        .setIsProduction(isProduction = false)
        .setKey(AllKeys.merchantKey)
        .setProductInfo("BirlaMediSoft Pvt Ltd")
        .setPhone("9527956414")
        .setTransactionId(System.currentTimeMillis().toString())
        .setFirstName("Santosh Pardeshi")
        .setEmail("santoshpardeshi1994@gmail.com")
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
                    Log.e(TAG, "onPaymentSuccess:fooFromJson "+fooFromJson.toString() )
                    //Log.e(TAG, "onPaymentSuccess: "+payUResponse )
                    //Log.e(TAG, "onPaymentSuccess: "+merchantResponse )
                    showDialogForSuccess(mContext!!,"Payment Successful")
                }

                override fun onPaymentFailure(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]

                    val gson = Gson()
                    val fooFromJson: PayuSuccessResponse = gson.fromJson(payUResponse.toString(), PayuSuccessResponse::class.java)
                    Log.e(TAG, "onPaymentSuccess:fooFromJson "+fooFromJson.toString() )

                    Log.e(TAG, "onPaymentFailure: "+payUResponse )
                    Log.e(TAG, "onPaymentFailure: "+merchantResponse )
                    CommonMethods.showDialogForError(mContext!!,"Payment Failed")
                }


                override fun onPaymentCancel(isTxnInitiated:Boolean) {
                    Log.e(TAG, "onPaymentCancel: $isTxnInitiated")
                }


                override fun onError(errorResponse: ErrorResponse) {
                    val errorMessage: String
                    if (errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                        errorMessage = errorResponse.errorMessage!!
                    else
                        errorMessage = AllKeys
                            .SERVER_MESSAGE
                    Log.e(TAG, "onError: "+errorMessage.toString() )
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    //For setting webview properties, if any. Check Customized Integration section for more details on this
                }

                override fun generateHash(
                    valueMap: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener,
                ) {
                    if (valueMap.containsKey(CP_HASH_STRING) && valueMap.containsKey(CP_HASH_NAME)) {

                        val hashData = valueMap[CP_HASH_STRING]
                        val hashName = valueMap[CP_HASH_NAME]

                        //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                        val hash: String? = HashGenerationUtils.generateHashFromSDK(hashData.toString(),AllKeys.salt,"")
                        if (!TextUtils.isEmpty(hash)) {
                            val dataMap: HashMap<String, String?> = HashMap()
                            dataMap[hashName!!] = hash!!
                            hashGenerationListener.onHashGenerated(dataMap)
                        }
                    }
                }
            })
    }

    override val activityLayout: Int
        get() = R.layout.activity_payu_check_out

    fun showDialogForSuccess(context: Context, message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                //(context as DashBoardActivity).navController.popBackStack(R.id.patientRegistrationFragment, true)
                // (context as DashBoardActivity).navController.popBackStack(R.id.checkSlotFragment2, true)
                //context.finish()
                //Calling Intent...
                finish()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }
}