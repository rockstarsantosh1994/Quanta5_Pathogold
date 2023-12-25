package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.GetPhelboWalkAdapter
import com.bms.pathogold_bms.model.getwalkdistance.GetWalkDistanceResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.widget.ColorArcProgressBar
import java.text.SimpleDateFormat
import java.util.*

class MyDistanceFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "MyDistanceActivity"

    //Toolbar declaration...
    private var toolbar: Toolbar? = null

    private var colorArcProgressBar:ColorArcProgressBar?=null

    //TextView declaration..
    private var tvNoDataFound: TextView?=null

    //EditText declaration
    private var etFromDate: EditText?=null
    private var etToDate: EditText?=null

    //AppCompatButton declaration..
    private var btnSubmit: AppCompatButton?=null

    //RecyclerView declaration..
    private var rvDistance: RecyclerView?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation...
        initViews(view)

        //getPhleboDistance...
        if(CommonMethods.isNetworkAvailable(mContext!!)){
            getPhelboDistance()
        }else{
            CommonMethods.showDialogForError(activity!!,AllKeys.NO_INTERNET_AVAILABLE)
        }
    }
    override val activityLayout: Int
        get() = R.layout.fragment_my_distance

    private fun initViews(view: View) {
        (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.my_distance)
        /*//toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = resources.getString(R.string.my_distance)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)*/

        colorArcProgressBar=view.findViewById(R.id.progressBar)

         //TextView binding..
        tvNoDataFound=view.findViewById(R.id.tv_no_data_found)

        //EditText binding...
        etFromDate=view.findViewById(R.id.et_from_date)
        etToDate=view.findViewById(R.id.et_to_date)

        //setTodaysDate..
        etToDate?.setText(CommonMethods.getTodayDate("MM/dd/yyyy"))

        //set 1 month prior date from todays date..
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.DAY_OF_YEAR, -30)
        etFromDate?.setText(formatter.format(c.time))

        //AppCompatButton binding..
        btnSubmit=view.findViewById(R.id.btn_submit)

        //RecyclerView binding..
        rvDistance=view.findViewById(R.id.rv_reports)
        val mLayoutManager: LinearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvDistance?.layoutManager = mLayoutManager

        //click listeners..
        etFromDate?.setOnClickListener(this)
        etToDate?.setOnClickListener(this)
        btnSubmit?.setOnClickListener(this)

        if(CommonMethods.getPrefrence(mContext!!,AllKeys.TOTAL_DISTANCE).equals(AllKeys.DNF)){
           colorArcProgressBar?.setCurrentValues(0.0F)
        }else{
            CommonMethods.getPrefrence(mContext!!,AllKeys.TOTAL_DISTANCE)?.toFloat()?.let { colorArcProgressBar?.setCurrentValues(it) }
        }
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
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.btn_submit->{
                //getReportsDownload of user...
                if(mContext?.let { CommonMethods.isNetworkAvailable(it) } == true){
                    getPhelboDistance()
                }else{
                    mContext?.let { CommonMethods.showDialogForError(it,AllKeys.NO_INTERNET_AVAILABLE) }
                }
            }
        }
    }

    private fun getPhelboDistance(){
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        val params: MutableMap<String, String> = HashMap()
        params["FromDate"] =etFromDate?.text.toString()
        params["ToDate"] =etToDate?.text.toString()
        params["HSC_ID"] =CommonMethods.getPrefrence(mContext!!,AllKeys.HSC_ID).toString()

        digiPath?.getApiRequestHelper()?.getDistanceSheetOfCollectionBoy(params,object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val getWalkDistanceResponse = `object` as GetWalkDistanceResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $getWalkDistanceResponse")
                if (getWalkDistanceResponse.ResponseCode == 200) {
                    if (getWalkDistanceResponse.ResultArray.size > 0) {
                        getWalkDistanceResponse.ResultArray.reverse()
                        val getPhelboWalkAdapter=GetPhelboWalkAdapter(mContext!!,getWalkDistanceResponse.ResultArray)
                        rvDistance?.adapter=getPhelboWalkAdapter
                    }
                } else {
                    Toast.makeText(mContext, getWalkDistanceResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                //showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
}