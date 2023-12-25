package com.bms.pathogold_bms.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.BMSCheckOutActivity
import com.bms.pathogold_bms.adapter.ServiceAdapter
import com.bms.pathogold_bms.model.service.GetServiceBO
import com.bms.pathogold_bms.model.service.GetServiceResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.util.*
import kotlin.collections.ArrayList

class ServiceFragment : BaseFragment(),ServiceAdapter.ServiceAdapterClick {

    private val TAG = "ServiceFragment"

    //EditText declaration..
    private var etSearch:EditText?=null

    //RecyclerView declaration...
    private var rvServices:RecyclerView?=null

    //LinearLayout declaration...
    private var llServiceView:LinearLayout?=null

    //TextView declaration...
    private var tvNoDataFound:TextView?=null

    private var serviceAdapter:ServiceAdapter?=null
    private var serviceArrayList=ArrayList<GetServiceBO>()

    override val activityLayout: Int
        get() = R.layout.fragment_service

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic initialisation...
        initViews(view)

        if(CommonMethods.isNetworkAvailable(mContext!!)){
            getServiceDetails()
        }else{
            CommonMethods.showDialogForError(activity!!,AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    private fun initViews(view: View) {
        //EditText binding...
        etSearch=view.findViewById(R.id.et_search)

        //LinearLayout binding..
        llServiceView=view.findViewById(R.id.ll_service_view)

        //TextView binding...
        tvNoDataFound=view.findViewById(R.id.tv_no_data_found)

        //Recycler View binding...
        rvServices=view.findViewById(R.id.rv_service)
        val layoutManager=LinearLayoutManager(mContext!!)
        rvServices?.layoutManager=layoutManager

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    private fun getServiceDetails() {
        val params: MutableMap<String, String> = HashMap()
        params["labcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()

        Log.e(TAG, "getServiceDetails: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please Wait....")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getServiceName(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val getServiceResponse = `object` as GetServiceResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $getServiceResponse")
                if (getServiceResponse.ResponseCode == 200) {
                    //To be integrated...
                    if (getServiceResponse.ResultArray.size > 0) {
                        llServiceView?.visibility = View.VISIBLE
                        tvNoDataFound?.visibility = View.GONE

                        serviceArrayList= getServiceResponse.ResultArray
                        serviceAdapter = ServiceAdapter(mContext!!,serviceArrayList, this@ServiceFragment)
                        rvServices?.adapter = serviceAdapter
                    } else {
                        llServiceView?.visibility = View.GONE
                        tvNoDataFound?.visibility = View.VISIBLE
                    }
                } else {
                    llServiceView?.visibility = View.GONE
                    tvNoDataFound?.visibility = View.VISIBLE
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun filterTable(text: String) {
        if (serviceArrayList.size > 0) {
            val filteredList1: ArrayList<GetServiceBO> = ArrayList()
            for (item in serviceArrayList) {
                if (text.let { item.Servicename.contains(it, true) }
                    || text.let { item.Servicecode.contains(it, true) }
                ) {
                    filteredList1.add(item)
                }
            }
            serviceAdapter?.updateData(context, filteredList1)
        }
    }

    override fun proceedToPay(getServiceBO: GetServiceBO, totalPayment: String) {
        Log.e(TAG, "makePayment: $getServiceBO")
        if(getServiceBO.merchantid.isEmpty()){
            CommonMethods.showDialogForError(mContext!!,"Payment Gateway Not Available!")
        }else{
            showPictureDialog(getServiceBO,totalPayment)
        }

    }

    private fun showPictureDialog(getServiceBO: GetServiceBO, totalPayment: String) {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Payment Gateway")
        val pictureDialogItems =
            arrayOf("RazorPay", "PhonePe"/*, "Preview"*/)
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> openCheckOutActivity(getServiceBO,totalPayment)
                1 -> openCheckOutActivity(getServiceBO,totalPayment)
            }
        }
        pictureDialog.show()
    }

    private fun openCheckOutActivity(serviceBO: GetServiceBO, totalPayment: String) {
        val intent = Intent(context, BMSCheckOutActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("service_bo", serviceBO)
        intent.putExtra("total_payment", totalPayment)
        startActivity(intent)
    }
}