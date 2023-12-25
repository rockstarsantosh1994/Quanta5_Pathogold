package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.text.SimpleDateFormat
import java.util.*
import android.app.ProgressDialog
import android.content.Intent
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.adapter.DailyGraphKeyAdapter
import com.bms.pathogold_bms.model.GraphBO
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterResponse
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import java.text.DateFormat
import kotlin.collections.LinkedHashMap
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bms.pathogold_bms.utility.RelativeRadioGroup

class DailyCashGraphFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "DailyCashGraphFragment"

    //EditText declaration..
    private var etFromDate: EditText? = null
    private var etToDate: EditText? = null
    private var etSelectLab: EditText? = null

    //RecyclerView declaration..
    private var rvGraph: RecyclerView? = null

    //Linearlayout declaration...
    var llSelectLabView: LinearLayout? = null

    //TextView declaration...
    var tvNoDataFound: TextView? = null

    //CardView declaration...
    private var cvLast7Days: CardView? = null
    private var cvDetailedStatement: CardView? = null

    //TextView declaration..
    private var tvLast7Days:TextView? = null
    private var tvDetailedStatement:TextView?=null

    //RadioGroup declaration..
    private var rgSelectPeriod1:RelativeRadioGroup?=null

    //RadioButton declaration..
    private var rbLastMonth:RadioButton?=null
    private var rbLast3Month:RadioButton?=null
    private var rbLast6Month:RadioButton?=null
    private var rbLast1Year:RadioButton?=null

    //intialisae arraylist...
    private var billAmtList: ArrayList<GraphBO>? = ArrayList()
    private var recAmtList: ArrayList<GraphBO>? = ArrayList()
    private var balanceList: ArrayList<GraphBO>? = ArrayList()
    private var discountList: ArrayList<GraphBO>? = ArrayList()
    private var patientCountList: ArrayList<GraphBO>? = ArrayList()

    //ConstraintLayout declaration..
    private var clDateView:ConstraintLayout?=null

    //intialisae map...
    private var dateAmtMap: MutableMap<String, ArrayList<GraphBO>> = LinkedHashMap()

    //ArrayList declaration...
    private var getLabNameArrayList = ArrayList<LabNameBO>()

    private val GET_LAB_NAME = 1

    //Business object declaration..
    private var labNameBO: LabNameBO? = null

    //String declaraion...
    private var stSelectPeriod:String="Last Month"

    override val activityLayout: Int
        get() = R.layout.fragment_daily_cash_graph

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation...
        initViews(view)

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            llSelectLabView?.visibility = View.VISIBLE
            getLabNameArrayList.clear()
            getLabNameArrayList = arguments?.getParcelableArrayList("labname_list")!!
            labNameBO = LabNameBO(getLabNameArrayList[0].labname, getLabNameArrayList[0].labname_c)
            etSelectLab?.setText(labNameBO?.labname_c)
            loadDataOnTodaysDate(-7)
        } else {
            //load data according to date
            loadDataOnTodaysDate(-7)
        }
    }

    private fun initViews(view: View) {
        //EditText binding..
        etFromDate = view.findViewById(R.id.et_from_date)
        etToDate = view.findViewById(R.id.et_to_date)
        etToDate?.setOnClickListener(this)
        etFromDate?.setOnClickListener(this)
        etSelectLab = view.findViewById(R.id.et_select_lab)
        etSelectLab?.setOnClickListener(this)

        //LinearLayout binding..
        llSelectLabView = view.findViewById(R.id.ll_search_labname_view)

        //TextView binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //CardView binding...
        cvLast7Days=view.findViewById(R.id.cv_last_7_days_transaction)
        cvDetailedStatement=view.findViewById(R.id.cv_detailed_statement)

        //TextView binding..
        tvLast7Days=view.findViewById(R.id.tv_last_7_days_transaction)
        tvDetailedStatement=view.findViewById(R.id.tv_detailed_statement)

        //ConstraintLayout binding..
        clDateView=view.findViewById(R.id.nested_constraint2_2)

        //RadioGroup binding..
        rgSelectPeriod1=view.findViewById(R.id.rg_select_period1)

        //RadioButton binding...
        rbLastMonth = view.findViewById(R.id.rb_last_month)
        rbLast3Month = view.findViewById(R.id.rb_last_3_months)
        rbLast6Month = view.findViewById(R.id.rb_last_6_months)
        rbLast1Year= view.findViewById(R.id.rb_last_1_year)

        //ClickListeners...
        cvLast7Days?.setOnClickListener(this)
        cvDetailedStatement?.setOnClickListener(this)

        //Recycler view declaration..
        rvGraph = view.findViewById(R.id.rv_graph)
        val linearLayoutManager = LinearLayoutManager(mContext!!)
        rvGraph?.layoutManager = linearLayoutManager

        rgSelectPeriod1?.setOnCheckedChangeListener { group, checkedId ->
            stSelectPeriod = (rgSelectPeriod1?.checkedRadioButtonId?.let { view.findViewById<View>(it) } as RadioButton).text.toString()

            selectOnRadioButton(stSelectPeriod)
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cv_last_7_days_transaction->{
                //load last 7 days records..
                loadDataOnTodaysDate(-7)

                handleVisiblity(mContext!!.resources.getColor(R.color.white),
                    mContext!!.resources.getColor(R.color.purple_700),
                    mContext!!.resources.getColor(R.color.purple_700),
                    mContext!!.resources.getColor(R.color.white),
                    View.GONE
                   )
            }

            R.id.cv_detailed_statement->{
                //On Select Radio button
                selectOnRadioButton(stSelectPeriod)

                handleVisiblity(mContext!!.resources.getColor(R.color.purple_700),
                    mContext!!.resources.getColor(R.color.white),
                    mContext!!.resources.getColor(R.color.white),
                    mContext!!.resources.getColor(R.color.purple_700),
                    View.VISIBLE,)
            }

            R.id.et_from_date -> {
                try{
                    rbLastMonth?.isChecked=false
                    rbLast3Month?.isChecked=false
                    rbLast6Month?.isChecked=false
                    rbLast1Year?.isChecked=false
                    rgSelectPeriod1?.clearCheck()
                }catch (e:Exception){
                    e.printStackTrace()
                }
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        //etFromDate?.setText(String.format("%02d", (monthOfYear + 1)) + "/" + dayOfMonth.toString() + "/" + year)
                        etFromDate?.setText("$dayOfMonth/" + String.format("%02d",
                            (monthOfYear + 1)) + "/" + year)
                        //load data according to date
                        setDateAndLoadData()

                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.et_to_date -> {
                try{
                    rbLastMonth?.isChecked=false
                    rbLast3Month?.isChecked=false
                    rbLast6Month?.isChecked=false
                    rbLast1Year?.isChecked=false
                    rgSelectPeriod1?.clearCheck()
                }catch (e:Exception){
                    e.printStackTrace()
                }
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        //etFromDate?.setText(String.format("%02d", (monthOfYear + 1)) + "/" + dayOfMonth.toString() + "/" + year)
                        etToDate?.setText(dayOfMonth.toString() + "/" + String.format("%02d",
                            (monthOfYear + 1)) + "/" + year)
                        //load data according to date
                        setDateAndLoadData()

                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.et_select_lab -> {
                val getLabNameDialog = GetLabNameDialog()
                val bundle = Bundle()
                bundle.putParcelableArrayList("get_lab_name", getLabNameArrayList)
                getLabNameDialog.arguments = bundle
                getLabNameDialog.setTargetFragment(this, GET_LAB_NAME)
                getLabNameDialog.show(fragmentManager?.beginTransaction()!!, "GET_LAB_NAME")
            }
        }
    }

    private fun selectOnRadioButton(stSelectPeriod: String) {
        when(stSelectPeriod){
            context?.getString(R.string.last_month)->{
                hitApiOnSelectMonths(-1)
            }

            context?.getString(R.string.last_3_months)->{
                hitApiOnSelectMonths(-2)
            }

            context?.getString(R.string.last_6_months)->{
                hitApiOnSelectMonths(-5)
            }

            context?.getString(R.string.last_1_year)->{
                hitApiOnSelectMonths(-12)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun hitApiOnSelectMonths(months: Int) {
        // etFromDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))
        etToDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))
        var sdf = SimpleDateFormat("dd/MM/yyyy")
        val c = Calendar.getInstance()
        c.time = sdf.parse(etToDate?.text.toString())
        c.add(Calendar.MONTH, months)
        sdf = SimpleDateFormat("dd/MM/yyyy")
        val resultdate = Date(c.timeInMillis)
        val stToDate = sdf.format(resultdate)

        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val toDate: Date? = dateFormat.parse(stToDate)

        val dateFormat1: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val strDate = dateFormat1.format(toDate)
        etFromDate?.setText(strDate)

        //GetDailyCash Register....
        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            // getDailyCashSummary6Months(stFromDate,CommonMethods.getTodayDate("MM/dd/yyyy"))

            if(months==-1){
                getDailyCashSummary(CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                    "dd/MM/yyyy",
                    "MM/dd/yyyy"),
                    CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                        "dd/MM/yyyy",
                        "MM/dd/yyyy"),
                    months)
            }else{
                getDailyCashSummaryMonthlyWise(CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                    "dd/MM/yyyy",
                    "MM/dd/yyyy"),
                    CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                        "dd/MM/yyyy",
                        "MM/dd/yyyy"),months)
            }

        } else {
            CommonMethods.showDialogForError(mContext!!,
                AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadDataOnTodaysDate(days: Int) {
        // etFromDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))
        etToDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))
        var sdf = SimpleDateFormat("dd/MM/yyyy")
        val c = Calendar.getInstance()
        c.time = sdf.parse(etToDate?.text.toString())
        c.add(Calendar.DATE, days)
        sdf = SimpleDateFormat("dd/MM/yyyy")
        val resultdate = Date(c.timeInMillis)
        val stToDate = sdf.format(resultdate)

        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val toDate: Date? = dateFormat.parse(stToDate)

        val dateFormat1: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val strDate = dateFormat1.format(toDate)
        etFromDate?.setText(strDate)
        //GetDailyCash Register....
        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            // getDailyCashSummary6Months(stFromDate,CommonMethods.getTodayDate("MM/dd/yyyy"))

            getDailyCashSummary(CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                "dd/MM/yyyy",
                "MM/dd/yyyy"),
                CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                    "dd/MM/yyyy",
                    "MM/dd/yyyy"),days)
        } else {
            CommonMethods.showDialogForError(mContext!!,
                AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDateAndLoadData() {
        /*var sdf = SimpleDateFormat("dd/MM/yyyy")
        val c = Calendar.getInstance()
        c.time = sdf.parse(etFromDate?.text.toString())
        c.add(Calendar.DATE, 7)
        sdf = SimpleDateFormat("dd/MM/yyyy")
        val resultdate = Date(c.timeInMillis)
        val stToDate = sdf.format(resultdate)*/

        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        //val toDate: Date? = dateFormat.parse(stToDate)
        //val todaysDate: Date? = dateFormat.parse(CommonMethods.getTodayDate("dd/MM/yyyy"))
        val fromDate: Date? = dateFormat.parse(etFromDate?.text.toString())
        val toDate: Date? = dateFormat.parse(etToDate?.text.toString())

        try {
            val daysDifference = printDifference(fromDate!!, toDate!!)
            /*if (daysDifference > 7) {
                etFromDate?.text = null
                //etFromDate?.setText("")
                //etToDate?.setText("")
                CommonMethods.showDialogForError(mContext!!,
                    "Select date range within 7 days only.")
            } else*/
            if (toDate.before(fromDate)) {
                //etToDate?.setText("")
                CommonMethods.showDialogForError(mContext!!,
                    "Please select to date after from date.")
            } else if (fromDate.after(toDate)) {
                //etFromDate?.setText("")
                CommonMethods.showDialogForError(mContext!!,
                    "Please select from date before to date.")
            } else {
                // val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                // val strDate = dateFormat.format(toDate)
                // etToDate?.setText(strDate)

                //GetDailyCash Register....
                if (CommonMethods.isNetworkAvailable(mContext!!)) {
                    //getDailyCashSummary of lab
                    getDailyCashSummaryMonthlyWise(CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                        "dd/MM/yyyy",
                        "MM/dd/yyyy"),
                        CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                            "dd/MM/yyyy",
                            "MM/dd/yyyy"),
                        0)
                } else {
                    CommonMethods.showDialogForError(mContext!!,
                        AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun printDifference(startDate: Date, endDate: Date): Long {
        //milliseconds
        var different = endDate.time - startDate.time
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli
        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli
        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
            elapsedDays,
            elapsedHours,
            elapsedMinutes,
            elapsedSeconds)

        return elapsedDays
        // Log.e(TAG, "%d days, %d hours, %d minutes, %d seconds%n"elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds )
    }

    private fun getDailyCashSummary(stFromDate: String?, stToDate: String?, days: Int) {
        val params: MutableMap<String, String> = HashMap()
        params["companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            params["labcode"] = labNameBO?.labname.toString()
        } else {
            params["labcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()
        }
        params["fromdate"] = stFromDate.toString()
        params["todate"] = stToDate.toString()
        params["usertype"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)) {
            params["CCcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()
        }else{
            params["CCcode"] = ""
        }

        Log.e(TAG, "getDailyCashSummary: $params")
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.dailyCashSummary(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val dailyCashRegisterResponse = `object` as DailyCashRegisterResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $dailyCashRegisterResponse")
                    if (dailyCashRegisterResponse.ResponseCode == 200) {
                        if (dailyCashRegisterResponse.ResultArray!!.size > 0) {
                            tvNoDataFound?.visibility = View.GONE
                            rvGraph?.visibility = View.VISIBLE
                            //First clear existing data..
                            //Clear data of list and map
                            clearData()

                            for (temp in dailyCashRegisterResponse.ResultArray!!) {
                                billAmtList?.add(GraphBO(temp.exam_date, temp.BillAmt))
                                recAmtList?.add(GraphBO(temp.exam_date, temp.NetPayment!!))
                                balanceList?.add(GraphBO(temp.exam_date, temp.Balance))
                                discountList?.add(GraphBO(temp.exam_date, temp.Discount))
                                patientCountList?.add(GraphBO(temp.exam_date, temp.patientcount!!))
                            }

                            //add data in map...
                            dateAmtMap["Patient Count"] = patientCountList!!
                            dateAmtMap["Total Bill Amount"] = billAmtList!!
                            dateAmtMap["Received Amount"] = recAmtList!!
                            dateAmtMap["Balance"] = balanceList!!
                            dateAmtMap["Discount"] = discountList!!

                            //append data to adapter....
                            val dailyGraphKeyAdapter = DailyGraphKeyAdapter(mContext!!,
                                dateAmtMap.keys.toTypedArray(),
                                dateAmtMap,days)
                            rvGraph?.adapter = dailyGraphKeyAdapter

                        } else {
                            //Clear data of list and map
                            clearData()
                            tvNoDataFound?.visibility = View.VISIBLE
                            rvGraph?.visibility = View.GONE
                        }
                    } else {
                        //Clear data of list and map
                        clearData()
                        tvNoDataFound?.visibility = View.VISIBLE
                        rvGraph?.visibility = View.GONE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getDailyCashSummaryMonthlyWise(stFromDate: String?, stToDate: String?, months: Int) {
        val params: MutableMap<String, String> = HashMap()
        params["companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            params["labcode"] = labNameBO?.labname.toString()
        } else {
            params["labcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()
        }
        params["fromdate"] = stFromDate.toString()
        params["todate"] = stToDate.toString()
        params["usertype"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)) {
            params["CCcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()
        }else{
            params["CCcode"] = ""
        }

        Log.e(TAG, "getDailyCashSummaryMonthlyWise: $params")
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.dailyCashSummaryMonthlyWise(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val dailyCashRegisterResponse = `object` as DailyCashRegisterResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $dailyCashRegisterResponse")
                    if (dailyCashRegisterResponse.ResponseCode == 200) {
                        if (dailyCashRegisterResponse.ResultArray!!.size > 0) {
                            tvNoDataFound?.visibility = View.GONE
                            rvGraph?.visibility = View.VISIBLE
                            //First clear existing data..
                            //Clear data of list and map
                            clearData()

                            for (temp in dailyCashRegisterResponse.ResultArray!!) {
                                billAmtList?.add(GraphBO(temp.exam_date, temp.BillAmt))
                                recAmtList?.add(GraphBO(temp.exam_date, temp.NetPayment!!))
                                balanceList?.add(GraphBO(temp.exam_date, temp.Balance))
                                discountList?.add(GraphBO(temp.exam_date, temp.Discount))
                                patientCountList?.add(GraphBO(temp.exam_date, temp.patientcount!!))
                            }

                            //add data in map...
                            dateAmtMap["Patient Count"] = patientCountList!!
                            dateAmtMap["Total Bill Amount"] = billAmtList!!
                            dateAmtMap["Received Amount"] = recAmtList!!
                            dateAmtMap["Balance"] = balanceList!!
                            dateAmtMap["Discount"] = discountList!!

                            //append data to adapter....
                            val dailyGraphKeyAdapter = DailyGraphKeyAdapter(mContext!!,
                                dateAmtMap.keys.toTypedArray(),
                                dateAmtMap,
                                months)
                            rvGraph?.adapter = dailyGraphKeyAdapter

                        } else {
                            //Clear data of list and map
                            clearData()
                            tvNoDataFound?.visibility = View.VISIBLE
                            rvGraph?.visibility = View.GONE
                        }
                    } else {
                        //Clear data of list and map
                        clearData()
                        tvNoDataFound?.visibility = View.VISIBLE
                        rvGraph?.visibility = View.GONE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun clearData() {
        billAmtList?.clear()
        recAmtList?.clear()
        balanceList?.clear()
        patientCountList?.clear()
        discountList?.clear()
        dateAmtMap.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GET_LAB_NAME -> {
                if (resultCode == Activity.RESULT_OK) {
                    val bundle = data?.extras
                    labNameBO = bundle?.getParcelable("lab_name_bo")
                    etSelectLab?.setText(labNameBO?.labname_c)
                    Log.e(TAG, "onActivityResult: $labNameBO")
                    //GetDailyCash Register....
                    if (CommonMethods.isNetworkAvailable(mContext!!)) {
                        //getDailyCashSummary of lab
                        getDailyCashSummary(CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                            "dd/MM/yyyy",
                            "MM/dd/yyyy"),
                            CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                                "dd/MM/yyyy",
                                "MM/dd/yyyy"),
                            -7)
                    } else {
                        CommonMethods.showDialogForError(mContext!!,
                            AllKeys.NO_INTERNET_AVAILABLE)
                    }
                }
            }
        }
    }

    //Handle visiblility of radio buttons...
    private fun handleVisiblity(
        cvLast7DaysColor: Int,
        cvDetailedStatementColor: Int,
        tvLast7DaysColor: Int,
        tvDetailedStatementColor: Int,
        clDateViewVisibility: Int,
    ) {
        cvLast7Days?.setCardBackgroundColor(cvLast7DaysColor)
        cvDetailedStatement?.setCardBackgroundColor(cvDetailedStatementColor)
        tvLast7Days?.setTextColor(tvLast7DaysColor)
        tvDetailedStatement?.setTextColor(tvDetailedStatementColor)
        clDateView?.visibility = clDateViewVisibility
    }
}