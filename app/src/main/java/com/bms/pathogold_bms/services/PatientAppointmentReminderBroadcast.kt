package com.bms.pathogold_bms.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.ConfigUrl
import com.google.gson.Gson
import java.util.HashMap

class PatientAppointmentReminderBroadcast : BroadcastReceiver() {

    private val TAG = "PatientAppointmentRemin"

    override fun onReceive(context: Context?, p1: Intent?) {
        //hit PatientAppointmentReminder Api...
        patientReminderHit(context)
    }

    private fun patientReminderHit(context: Context?){
        val stringRequest=object : StringRequest(Method.POST,ConfigUrl.BASE_URL+"PatientAppointmentRemainder",
            Response.Listener {
                response->
            val gson= Gson()
            Log.e(TAG, "patientReminderHit$response")
            val commonResponse:CommonResponse=gson.fromJson(response,CommonResponse::class.java)
                Log.e(TAG, "onSuccess: $commonResponse")
                if (commonResponse.ResponseCode == 200) {
                    Toast.makeText(context, commonResponse.Message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, commonResponse.Message, Toast.LENGTH_SHORT).show()
                }
        },Response.ErrorListener {
            Log.e(TAG,"error\n $it")
        }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params=HashMap<String,String>()

                params["companyid"] =  context?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
                Log.e(TAG,"patientReminderHit\n $params")
                return params
            }
        }
        val mQueue= Volley.newRequestQueue(context)
        mQueue.add(stringRequest)
    }
}