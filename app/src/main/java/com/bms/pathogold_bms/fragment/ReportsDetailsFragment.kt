package com.bms.pathogold_bms.fragment

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.ReportsDownloadAdapter
import com.bms.pathogold_bms.model.report.ReportsBO
import com.bms.pathogold_bms.model.reportdetails.ReportDetailsBO
import com.bms.pathogold_bms.model.reportdetails.ReportDetailsResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.ArrayList
import java.util.HashMap

class ReportsDetailsFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "ReportsDetailsFragment"

    //Views Declarations....
    //Edittext declaration...
    private var etSearch: EditText? = null
    private var etTestNameSpin:TextInputEditText?=null

    //TextInputLayout declaration..
    private var tilTestNameSpin:TextInputLayout?=null

    //Recyclerview declaration...
    private var rvViewReports: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    //LinearLayout declaration
    private var llNoDataFound: LinearLayout?=null
    private var llViewReports: LinearLayout?=null

    //private val getMainReportsDetailsMap:MutableMap<String,MutableMap<String, ArrayList<ReportDetailsBO>>> = HashMap()
    private var getReportsDetailsByTestNameMap: MutableMap<String, ArrayList<ReportDetailsBO>> = LinkedHashMap()
    private var getReportsDetailsByParameterNameMap: MutableMap<String, ArrayList<ReportDetailsBO>> = LinkedHashMap()
    private var reportsDetailsTemp: ArrayList<ReportDetailsBO>?=null
    private var reportsDetailsTemp1: ArrayList<ReportDetailsBO>?=null
    private var reportsBO:ReportsBO?=null
    private val testNameArrayList = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable...
        val arguments = requireArguments()
        reportsBO= arguments.getParcelable("report_bo")

        //basic intialisation..
        initViews(view)

        //get Accepted Appointment...
        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            getReportsDetails()
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_reports_details

    private fun initViews(view: View) {
        (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.view_report_details)
        //Editext binding.
        etSearch = view.findViewById(R.id.et_search)
        etTestNameSpin=view.findViewById(R.id.et_testname_spin)
        etTestNameSpin?.setOnClickListener(this)

        //TextInputLayout binding...
        tilTestNameSpin=view.findViewById(R.id.til_testname_spin)
        tilTestNameSpin?.setOnClickListener(this)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //Linearlayout binding..
        llViewReports=view.findViewById(R.id.ll_view_reports)

        //LinearLayout declaration
        llNoDataFound=view.findViewById(R.id.ll_no_data_found)

        //Recycler binding..
        rvViewReports = view.findViewById(R.id.rv_view_reports)
        val linearLayoutManager = LinearLayoutManager(mContext)
        rvViewReports?.layoutManager = linearLayoutManager

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
               // filterTable(s.toString())
            }
        })
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.et_testname_spin->{
                val spinnerDialogDistrict = SpinnerDialog(mContext!! as Activity?, testNameArrayList, "Select TestName", R.style.DialogAnimations_SmileWindow, "Close") // With 	Animation
                spinnerDialogDistrict.bindOnSpinerListener { item: String, position: Int ->
                    etTestNameSpin?.setText(item)

                    //appendData to recycler view..
                    setDataToRecyclerView()
                }
                spinnerDialogDistrict.showSpinerDialog()
            }
        }
    }

    private fun setDataToRecyclerView() {
        reportsDetailsTemp1?.clear()
        getReportsDetailsByParameterNameMap.clear()
        if (getReportsDetailsByTestNameMap.containsKey(etTestNameSpin?.text.toString())) {
            for (temp in getReportsDetailsByTestNameMap.getValue(etTestNameSpin?.text.toString())) {
                if (getReportsDetailsByParameterNameMap.containsKey(temp.parametername.trim())) {
                    reportsDetailsTemp1 = getReportsDetailsByParameterNameMap[temp.parametername.trim()]
                    reportsDetailsTemp1!!.add(temp)
                } else {
                    reportsDetailsTemp1 = ArrayList()
                    reportsDetailsTemp1!!.add(temp)
                    getReportsDetailsByParameterNameMap[temp.parametername.trim()] = reportsDetailsTemp1!!
                }
            }
        }

        val reportsKeyAdapter= ReportsDownloadAdapter(mContext,getReportsDetailsByParameterNameMap.keys.toTypedArray(),getReportsDetailsByParameterNameMap)
        rvViewReports?.adapter=reportsKeyAdapter
    }

    private fun getReportsDetails() {
        val params: MutableMap<String, String> = HashMap()
        //params["pepatid"] = "2730"
        params["pepatid"] = reportsBO?.Pepatid.toString()

        Log.e(TAG, "getReportsDetails: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getTestResultdetails(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val reportDetailsResponse = `object` as ReportDetailsResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $reportDetailsResponse")
                    if (reportDetailsResponse.ResponseCode == 200) {
                        //Calling Intent...
                        if (reportDetailsResponse.ResultArray.size > 0) {
                            for (temp in reportDetailsResponse.ResultArray) {
                                if (getReportsDetailsByTestNameMap.containsKey(temp.testname.trim())) {
                                    reportsDetailsTemp = getReportsDetailsByTestNameMap[temp.testname.trim()]
                                    reportsDetailsTemp!!.add(temp)
                                } else {
                                    reportsDetailsTemp = ArrayList()
                                    reportsDetailsTemp!!.add(temp)
                                    getReportsDetailsByTestNameMap[temp.testname.trim()] = reportsDetailsTemp!!
                                }
                            }

                            //Add data to testName ArrayList
                            testNameArrayList.addAll(getReportsDetailsByTestNameMap.keys.toTypedArray())

                            if(testNameArrayList.size>0){
                                etTestNameSpin?.setText(testNameArrayList[0])

                                //append data to recycler view..
                                setDataToRecyclerView()
                            }
                        }
                        llViewReports?.visibility = View.VISIBLE
                        llNoDataFound?.visibility = View.GONE
                    } else {
                        llViewReports?.visibility = View.GONE
                        llNoDataFound?.visibility = View.VISIBLE
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