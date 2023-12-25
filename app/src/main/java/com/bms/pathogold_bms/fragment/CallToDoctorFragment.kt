package com.bms.pathogold_bms.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.VideoConferenceActivity2
import com.bms.pathogold_bms.adapter.CallToDrAdapter
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.ConfigUrl
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class CallToDoctorFragment : BaseFragment(),CallToDrAdapter.OnDrSelectInterFace {

    private val TAG = "CallToDrACtivity"

    //LinearLayout declaration..
    private var llGetDoctorList: LinearLayout?=null

    //RecyclerView declaration...
    private var rvGetDoctorList: RecyclerView?=null

    //EditText declaration.
    private var etSearch: EditText?=null

    //TextView declaration..
    private var tvNoDataFound: TextView?=null

    var callToDrAdapter: CallToDrAdapter? = null
    val consultationBOArrayList = ArrayList<ConsultationBO>()

    override val activityLayout: Int
        get() = R.layout.fragment_call_to_doctor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation..
        initViews(view)

        if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
            //Get consultation list
            getConsultationList()
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    private fun initViews(view: View) {
        //Linear layout declaration...
        llGetDoctorList=view.findViewById(R.id.ll_get_doctor_list)

        //RecyclerView declaration...
        rvGetDoctorList=view.findViewById(R.id.rv_get_dr_list)
        val gridLayoutManager= GridLayoutManager(mContext,2)
        rvGetDoctorList?.layoutManager=gridLayoutManager

        //EditText declaration....
        etSearch=view.findViewById(R.id.et_search)

        //TextView declaration...
        tvNoDataFound=view.findViewById(R.id.tv_no_data_found)

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    private fun getConsultationList() {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.LABNAME).toString() }!!
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getConsultationList: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getConsultantList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val consultationResponse = `object` as ConsultationResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $consultationResponse")
                    if (consultationResponse.ResponseCode == 200) {
                        if (consultationResponse.ResultArray.size > 0) {
                            rvGetDoctorList?.visibility = View.VISIBLE
                            tvNoDataFound?.visibility = View.GONE
                            consultationBOArrayList.addAll(consultationResponse.ResultArray)
                            callToDrAdapter = mContext?.let { CallToDrAdapter(it, consultationBOArrayList,this@CallToDoctorFragment) }
                            rvGetDoctorList?.adapter = callToDrAdapter
                        } else {
                            llGetDoctorList?.visibility = View.GONE
                            tvNoDataFound?.visibility = View.VISIBLE
                        }
                    }else{
                        llGetDoctorList?.visibility = View.GONE
                        tvNoDataFound?.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    override fun onDrSelect(consultationBO: ConsultationBO) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setMessage("Video Call Dr."+consultationBO.Name+"?")
        builder.setPositiveButton("CALL") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            if (consultationBO.token.isNotEmpty() && consultationBO.token != AllKeys.DNF) {
                sendNotification(mContext!!, consultationBO)
            } else {
                CommonMethods.showDialogForError(requireActivity(), "Dr."+consultationBO.Name + " " + resources.getString(
                    R.string.not_available_for_video_call))
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("NO") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            dialog.cancel()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun sendNotification(context: Context, consultationBO: ConsultationBO) {
        //  Log.e(TAG, "sendNotification: $getPatientListBO")
        val title = CommonMethods.getPrefrence(context, AllKeys.PERSON_NAME) + " is Video Calling you...."
        val link = ConfigUrl.VIDEO_CALL_BASE_URL+ consultationBO.Pno
        var message = "Hi.. " + CommonMethods.getPrefrence(context, AllKeys.PERSON_NAME) + " is requesting you a video call...please receive it"
        val notificationData = JSONObject()
        val notificationData1 = JSONObject()
        val notification = JSONObject()
        try {
            //parameter sending for notification key...
            notificationData1.put("title", title)
            notificationData1.put("body", link)
            notificationData1.put("text", title)
            notificationData1.put("click_action", "VIDEOCALL")

            //parameter sending for data key....
            notificationData.put("title", title)
            notificationData.put("message", link)
            notificationData.put("body", link)
            notificationData.put("text", link)
            notificationData.put("extra_information", link)
            notificationData.put("click_action", "VIDEOCALL")

            notification.put("to", consultationBO.token)
            //notification.put("notification", notificationData1)
            notification.put("data", notificationData)

            val jsonObjectRequest: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, ConfigUrl.FCM_API, notification,
                    Response.Listener { response: JSONObject ->
                        Log.e("mytag", "Success \n sendNotification: $response")

                        if (response.getString("success").equals("1")) {
                            val intent = Intent(context, VideoConferenceActivity2::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            intent.putExtra("link", link)
                            context.startActivity(intent)
                        }else{
                            CommonMethods.showDialogForError(context,
                                AllKeys.UNABLE_TO_CONNECT)
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
                        // Log.e("mytag", "error: " + error);
                        val response = error.networkResponse
                        if (error is ServerError && response != null) {
                            try {
                                val res = String(response.data,
                                    Charset.forName(HttpHeaderParser.parseCharset(response.headers,
                                        "utf-8")))
                                // Now you can use any deserializer to make sense of data
                                val obj = JSONObject(res)

                                CommonMethods.showDialogForError(context,
                                    "Unable to connect...Try after some time! ")
                            } catch (e1: UnsupportedEncodingException) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace()
                                // Log.e("mytag", "sendNotification erro: e1 " + e1);
                            } catch (e2: JSONException) {
                                // returned data is not JSONObject?
                                e2.printStackTrace()
                                //  Log.e("mytag", "sendNotification erro e2: " + e2);
                            } catch (e:Exception){
                                CommonMethods.showDialogForError(context,
                                    "Unable to connect...Try after some time! ")
                            }
                        }
                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Authorization"] = ConfigUrl.AUTHORIZATION
                        params["Content-Type"] = "application/json"
                        return params
                    }
                }
            Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
            //mQueue.add(jsonObjectRequest);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun filterTable(text: String?) {
        if (consultationBOArrayList.size > 0) {
            val filteredList1: ArrayList<ConsultationBO> = ArrayList<ConsultationBO>()
            for (item in consultationBOArrayList) {
                if (text?.let { item.Name.toLowerCase(Locale.ROOT).contains(it) } == true
                    || text?.let { item.Name.toUpperCase(Locale.ROOT).contains(it) } == true
                    || text?.let { item.Pno.toUpperCase(Locale.ROOT).contains(it) } == true
                ) {
                    filteredList1.add(item)
                }
            }
            //Log.e(TAG, "filter: size" + filteredList1.size());
            // Log.e(TAG, "filter: List" + filteredList1.toString());
            callToDrAdapter?.updateData(mContext, filteredList1)
        }
    }
}