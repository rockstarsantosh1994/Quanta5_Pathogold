package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.ReportsDetailsAdapter
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.report.ReportResponse
import com.bms.pathogold_bms.model.report.ReportsBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReportsFragment : BaseFragment(), View.OnClickListener {

    //Declaration...
    private val TAG = "ReportsActivity"

    //Toolbar declaration...
    private var toolbar: Toolbar? = null

    //TextView declaration..
    private var tvNoDataFound:TextView?=null
    private var tvPatientName:TextView?=null
    private var tvPatientMobileNo:TextView?=null
    private var tvPatientSex:TextView?=null
    private var tvPatientRegNo:TextView?=null
    private var tvPatientRegDate:TextView?=null

    //EditText declaration
    private var etFromDate:EditText?=null
    private var etToDate:EditText?=null

    //AppCompatButton declaration..
    private var btnSubmit:AppCompatButton?=null

    //RecyclerView declaration..
    private var rvReports:RecyclerView?=null

    private var getPatientListBO: GetPatientListBO? = null
    private val getReportsListMap: MutableMap<String, ArrayList<ReportsBO>> = HashMap()
    private var getReportsTemp=ArrayList<ReportsBO>()

    @SuppressLint("FragmentBackPressedCallback")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable...
        val arguments = requireArguments()
        getPatientListBO = arguments.getParcelable("patient_bo")

        //basic intialisation..
        initViews(view)

        //setData of patient
        setData()

        //getReportsDownload of user...
        if(mContext?.let { CommonMethods.isNetworkAvailable(it) } == true){
            getReportsDownload()
        }else{
            activity?.let { CommonMethods.showDialogForError(it,AllKeys.NO_INTERNET_AVAILABLE) }
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_reports

    @SuppressLint("SimpleDateFormat")
    private fun initViews(view: View) {
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.report)

        //TextView binding..
        tvNoDataFound=view.findViewById(R.id.tv_no_data_found)
        tvPatientName=view.findViewById(R.id.tv_patient_name)
        tvPatientMobileNo=view.findViewById(R.id.tv_patient_mobiles_value)
        tvPatientSex=view.findViewById(R.id.tv_sex)
        tvPatientRegDate=view.findViewById(R.id.tv_patient_reg_date)
        tvPatientRegNo=view.findViewById(R.id.tv_patient_reg_no)

        //EditText binding...
        etFromDate=view.findViewById(R.id.et_from_date)
        etToDate=view.findViewById(R.id.et_to_date)

        //setTodaysDate..
        etToDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))

        //set 1 month prior date from todays date..
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.DAY_OF_YEAR, -30)
        etFromDate?.setText(formatter.format(c.time))

        //AppCompatButton binding..
        btnSubmit=view.findViewById(R.id.btn_submit)

        //RecyclerView binding..
        rvReports=view.findViewById(R.id.rv_reports)
        val mLayoutManager: LinearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvReports?.layoutManager = mLayoutManager

        //click listeners..
        etFromDate?.setOnClickListener(this)
        etToDate?.setOnClickListener(this)
        btnSubmit?.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.et_from_date->{
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        etFromDate?.setText(dayOfMonth.toString() + "/" + String.format("%02d",(monthOfYear + 1))  + "/" + year)
                        //etFromDate?.setText(String.format("%02d",(monthOfYear + 1))  + "/" + dayOfMonth.toString()  + "/" + year)
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.et_to_date->{
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        etToDate?.setText(dayOfMonth.toString() + "/" + String.format("%02d",(monthOfYear + 1))  + "/" + year)
                       // etToDate?.setText(String.format("%02d",(monthOfYear + 1))  + "/" + dayOfMonth.toString()  + "/" + year)
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.btn_submit->{
                //getReportsDownload of user...
                if(mContext?.let { CommonMethods.isNetworkAvailable(it) } == true){
                    getReportsDownload()
                }else{
                    activity?.let { CommonMethods.showDialogForError(it,AllKeys.NO_INTERNET_AVAILABLE) }
                }

            }
        }
    }

    private fun getReportsDownload(){
        val params: MutableMap<String, String> = HashMap()
        params["labcode"] =CommonMethods.getPrefrence(mContext!!,AllKeys.LABNAME).toString()
        params["PatientName"] =""
        if(CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Patient)){
            params["patient_mobileno"] =CommonMethods.getPrefrence(mContext!!,AllKeys.MOBILE_NO).toString()
        }else{
            params["patient_mobileno"] =""
        }
        params["pepatid"] =getPatientListBO?.PePatID.toString()
        params["usertype"] =CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).toString()
        if(CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Consultant)){
            params["pno"] =CommonMethods.getPrefrence(mContext!!,AllKeys.HSC_ID).toString()
        }else{
            params["pno"] =""
        }
        params["username"] =CommonMethods.getPrefrence(mContext!!,AllKeys.USER_NAME).toString()
        params["fromdate"] =CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),"dd/MM/yyyy","MM/dd/yyyy").toString()
        params["todate"] =CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),"dd/MM/yyyy","MM/dd/yyyy").toString()

        Log.e(TAG, "getReportsDownload: $params" )

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getReportDownload(params,object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val reportResponse = `object` as ReportResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $reportResponse")
                if (reportResponse.ResponseCode == 200) {
                    if (reportResponse.ResultArray.size > 0) {
                        getReportsListMap.clear()
                        getReportsTemp.clear()
                        for (temp in reportResponse.ResultArray) {
                            if (getReportsListMap.containsKey(temp.type.trim())) {
                                getReportsTemp = getReportsListMap[temp.type.trim()]!!
                                getReportsTemp.add(temp)
                            } else {
                                getReportsTemp = ArrayList()
                                getReportsTemp.add(temp)
                                getReportsListMap[temp.type.trim()] = getReportsTemp
                            }
                        }

                        rvReports?.visibility=View.VISIBLE
                        tvNoDataFound?.visibility=View.GONE

                        val reportsKeyAdapter=ReportsDetailsAdapter(mContext,getReportsListMap.keys.toTypedArray(),getReportsListMap,getPatientListBO)
                        rvReports?.adapter=reportsKeyAdapter
                    }else{
                        rvReports?.visibility=View.GONE
                        tvNoDataFound?.visibility=View.VISIBLE
                    }
                } else {
                    rvReports?.visibility=View.GONE
                    tvNoDataFound?.visibility=View.VISIBLE
                    Toast.makeText(mContext, reportResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                //showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData(){
        tvPatientSex?.text= getPatientListBO?.sex
        tvPatientMobileNo?.text= getPatientListBO?.PatientPhoneNo
        tvPatientName?.text=getPatientListBO?.PatientName
        tvPatientRegNo?.text= context?.resources?.getString(R.string.regno) + "  " +getPatientListBO?.regno
        tvPatientRegDate?.text=CommonMethods.parseDateToddMMyyyy(getPatientListBO?.Entrydate,
            "MM/dd/yyyy HH:mm:ss a",
            "dd, MMM yyyy")
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