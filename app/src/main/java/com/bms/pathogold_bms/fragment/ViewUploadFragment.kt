package com.bms.pathogold_bms.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.GetClientAdapter
import com.bms.pathogold_bms.model.getclientuploadedfile.GetClientUploadedBO
import com.bms.pathogold_bms.model.getclientuploadedfile.GetClientUploadedResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import java.util.*

class ViewUploadFragment : BaseFragment() {

    private val TAG = "ViewUploadFragment"

    //TextInputLayout declaration..
    private var tilDate: TextInputLayout?=null

    //TextInputEditText declaration..
    private var etDate: TextInputEditText?=null

    //Seaerchable Spinner....
    private var ssPatientName: SearchableSpinner?=null

    //Recycler view declaration..
    private var rvViewPhotos:RecyclerView?=null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null


    private val getPatientListBOArrayList=ArrayList<GetPatientListBO>()
    private var getPatientListBO:GetPatientListBO?=null

    private var getClientMap: MutableMap<String, ArrayList<GetClientUploadedBO>> = HashMap<String, ArrayList<GetClientUploadedBO>>()
    private var temp1: ArrayList<GetClientUploadedBO>? = null


    override val activityLayout: Int
        get() =R.layout.fragment_view_upload

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation..
        initViews(view)

        val arguments = requireArguments()

        getPatientListBO=arguments.getParcelable("patient_bo")
        if(CommonMethods.isNetworkAvailable(mContext!!)){
            getClientUploadedFile(getPatientListBO!!.regno)
        }else{
            CommonMethods.showDialogForError(mContext as Activity,AllKeys.NO_INTERNET_AVAILABLE)
        }

    }

    private fun initViews(view: View){
        (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.view_file)

        //TextInputLayout binding...
        tilDate=view.findViewById(R.id.til_date)

        //TextInputEditText binding..
        etDate=view.findViewById(R.id.et_date)

        //Searchable spinner binding..
        ssPatientName=view.findViewById(R.id.ss_patient_name)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //Recyclerview binding..
        rvViewPhotos=view.findViewById(R.id.rv_view_photos)
        val linearLayoutManager=LinearLayoutManager(mContext)
        rvViewPhotos?.layoutManager=linearLayoutManager

        //Click listeners
        //etDate?.setOnClickListener(this)
    }

    private fun getClientUploadedFile(regNo: String){
        val params: MutableMap<String, String> = HashMap()
        params["Regno"] =regNo

        Log.e(TAG, "getPatientTestList: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getClientUploadedFile(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getClientUploadedResponse = `object` as GetClientUploadedResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getClientUploadedResponse")
                    if (getClientUploadedResponse.ResponseCode == 200) {
                        if (getClientUploadedResponse.ResultArray.size > 0) {

                            getClientUploadedResponse.ResultArray.reverse()
                            getClientMap.clear()
                            rvViewPhotos?.visibility = View.VISIBLE
                            tvNoDataFound?.visibility = View.GONE

                            for (temp in getClientUploadedResponse.ResultArray) {
                                if (getClientMap.containsKey(temp.type.trim())) {
                                    temp1 = getClientMap[temp.type.trim()]
                                    temp1?.add(temp)
                                } else {
                                    temp1 = ArrayList<GetClientUploadedBO>()
                                    temp1?.add(temp)
                                    getClientMap[temp.type.trim()] = temp1!!
                                }
                            }
                            val getClientAdapter = GetClientAdapter(mContext, getClientMap.keys.toTypedArray(), getClientMap,getPatientListBO,digiPath)
                            rvViewPhotos?.adapter = getClientAdapter

                            Log.e(TAG, "onSuccess: $getClientMap")
                        } else {
                            getClientMap.clear()
                            rvViewPhotos?.visibility = View.GONE
                            tvNoDataFound?.visibility = View.VISIBLE
                        }
                    } else {
                        getClientMap.clear()
                        rvViewPhotos?.visibility = View.GONE
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

}
