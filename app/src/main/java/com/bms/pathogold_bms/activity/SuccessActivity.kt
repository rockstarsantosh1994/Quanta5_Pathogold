package com.bms.pathogold_bms.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.razorpay.PaymentData

class SuccessActivity : BaseActivity() {

    private val TAG = "SuccessActivity"

    private var payementData:PaymentData?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        payementData= intent?.getParcelableExtra("payment_bo") as PaymentData?

        Toast.makeText(mContext, "Contact\n"+payementData?.userContact, Toast.LENGTH_LONG).show()
        Toast.makeText(mContext, "User Email \n"+payementData?.userEmail, Toast.LENGTH_LONG).show()
        Toast.makeText(mContext, "Payment Id \n"+payementData?.paymentId, Toast.LENGTH_LONG).show()
        Toast.makeText(mContext, "orderId Id \n"+payementData?.orderId, Toast.LENGTH_LONG).show()
        Toast.makeText(mContext, "signature \n"+payementData?.signature, Toast.LENGTH_LONG).show()
        Toast.makeText(mContext, "externalWallet \n"+payementData?.externalWallet, Toast.LENGTH_LONG).show()
        Toast.makeText(mContext, "data \n"+payementData?.data, Toast.LENGTH_LONG).show()

        Log.e(TAG, "onCreate: "+payementData?.userContact )
        Log.e(TAG, "onCreate: "+payementData?.userEmail )
        Log.e(TAG, "onCreate: "+payementData?.paymentId )
        Log.e(TAG, "onCreate: "+payementData?.orderId )
        Log.e(TAG, "onCreate: "+payementData?.signature )
        Log.e(TAG, "onCreate: "+payementData?.externalWallet )
        Log.e(TAG, "onCreate: "+payementData?.data )
    }

    override val activityLayout: Int
        get() = R.layout.activity_success
}