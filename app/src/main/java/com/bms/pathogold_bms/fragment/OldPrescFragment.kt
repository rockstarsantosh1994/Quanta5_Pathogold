package com.bms.pathogold_bms.fragment

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.GetEMRTreatmentAdapter
import com.bms.pathogold_bms.model.getemrtreatment.GetEMRTreatmentBO
import com.bms.pathogold_bms.model.getemrtreatment.GetEMRTreatmentResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class OldPrescFragment : BaseFragment() {

    //Declaration...
    private val TAG = "OldPrescActivity"

    //Toolbar declaration...
    private var toolbar: Toolbar? = null

    //RecyclerView declaration..
    private var rvOldPrescription:RecyclerView?=null

    private var getPatientListBO: GetPatientListBO? = null
    private val getEMRTreatmentMap: MutableMap<String, ArrayList<GetEMRTreatmentBO>> = LinkedHashMap()
    private var getEMRTreatmentBOTemp=ArrayList<GetEMRTreatmentBO>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable...
        val arguments = requireArguments()
        getPatientListBO = arguments.getParcelable("patient_bo")

        //basic intialisation..
        initViews(view)

        //getOldPrescription...of patient..
        if(CommonMethods.isNetworkAvailable(mContext!!)){
            getEMRTreatmentlist()
        }else{
            CommonMethods.showDialogForError(mContext!!,AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_old_presc

    private fun initViews(view: View) {
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.old_prescription)

        //RecyclerView declaration...
        rvOldPrescription = view.findViewById(R.id.rv_old_prescription)
        val layoutManager = LinearLayoutManager(mContext)
        rvOldPrescription?.layoutManager = layoutManager
    }

    private fun getEMRTreatmentlist(){
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        val params: MutableMap<String, String> = HashMap()
        params["CompanyID"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["Pepatid"] =getPatientListBO?.PePatID.toString()

        digiPath?.getApiRequestHelper()?.getEMRTreatmentlist(params,object : ApiRequestHelper.OnRequestComplete {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onSuccess(`object`: Any) {
                val getEMRTreatmentResponse = `object` as GetEMRTreatmentResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $getEMRTreatmentResponse")
                if (getEMRTreatmentResponse.ResponseCode == 200) {
                    if (getEMRTreatmentResponse.ResultArray.size > 0) {
                        getEMRTreatmentResponse.ResultArray.reverse()
                        for (temp in getEMRTreatmentResponse.ResultArray) {
                            if (getEMRTreatmentMap.containsKey(temp.TTime.trim())) {
                                getEMRTreatmentBOTemp = getEMRTreatmentMap[temp.TTime.trim()]!!
                                getEMRTreatmentBOTemp.add(temp)
                            } else {
                                getEMRTreatmentBOTemp = ArrayList()
                                getEMRTreatmentBOTemp.add(temp)
                                getEMRTreatmentMap[temp.TTime.trim()] = getEMRTreatmentBOTemp
                            }
                        }
                        val getEMRTreatmentAdapter=GetEMRTreatmentAdapter(mContext!!,getEMRTreatmentMap.keys.toTypedArray(),getEMRTreatmentMap)
                        rvOldPrescription?.adapter=getEMRTreatmentAdapter
                    }
                } else {
                    Toast.makeText(mContext, getEMRTreatmentResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                //showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }


   /* override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
}