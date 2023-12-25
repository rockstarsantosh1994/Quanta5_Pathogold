package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterBO
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterResponse
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterSummaryBO
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import com.bms.pathogold_bms.model.lab_super_admin.LabNameResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.view_models.DailyCashRegisterViewModel
import me.ibrahimsn.lib.OnItemSelectedListener
import me.ibrahimsn.lib.SmoothBottomBar
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DailyCashDashBoardFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "DailyCashDashBoardFragm"

    //EditText declaration..
    private var etDate: EditText? = null

    //EditText declaration..
    private var etFromDate: EditText? = null
    private var etToDate: EditText? = null
    private var etSelectLab: EditText? = null

    //FrameLayout Declaration..
    private var frameLayout: FrameLayout? = null

    //SmoothBar Declaration..
    private var smoothBottomBar: SmoothBottomBar? = null

    lateinit var navController: NavController

    //Constraint layout..
    var fromToDateConstraintLayout: ConstraintLayout? = null
    var llSelectLabView: LinearLayout? = null

    //Active index
    var activeIndex: Int = 0

    //declaration arraylist..
    private var getDailyCashRegisterList: ArrayList<DailyCashRegisterBO>? = ArrayList()
    private var getDailyCashRegisterRecList: ArrayList<DailyCashRegisterBO>? = ArrayList()
    private var dailyCashRegisterSummaryBOList: ArrayList<DailyCashRegisterSummaryBO>? = ArrayList()

    //DailyCashRegisterViewModel...
    private lateinit var getDailyCashViewModel: DailyCashRegisterViewModel

    //ArrayList declaration...
    private var getLabNameArrayList = ArrayList<LabNameBO>()

    private val GET_LAB_NAME = 1

    //Business object declaration..
    private var labNameBO: LabNameBO? = null

    override val activityLayout: Int
        get() = R.layout.fragment_daily_cash_dash_board2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intialise view model
        getDailyCashViewModel =
            ViewModelProvider(requireActivity())[DailyCashRegisterViewModel::class.java] // init view model

        //Getting the Navigation Controller
        navController = requireActivity().findNavController(R.id.view_image_graph)

        //basic intialisation..
        initViews(view)

        smoothBottomBar?.itemActiveIndex = 1

        //store activeIndex value...
        activeIndex = smoothBottomBar?.itemActiveIndex!!

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            llSelectLabView?.visibility = View.VISIBLE
            if (getDailyCashViewModel.dailyCashRegisterSummarylist.value?.size == 0 || getDailyCashViewModel.dailyCashRegisterlist.value?.size == 0) {
                if (CommonMethods.isNetworkAvailable(mContext!!)) {
                    getSuperAdminLabs()
                } else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        } else {
            llSelectLabView?.visibility = View.GONE
        }

        //GetDailyCash Register....
        if (getDailyCashViewModel.dailyCashRegisterSummarylist.value?.size == 0 || getDailyCashViewModel.dailyCashRegisterlist.value?.size == 0) {
            loadDataOnTodaysDate()
        }

        // set up observer. Every time You add sth to list, ListView will be updated
        getDailyCashViewModel.dailyCashRegisterSummarylist.observe(viewLifecycleOwner) {

            if (dailyCashRegisterSummaryBOList?.size == 0) {
                dailyCashRegisterSummaryBOList?.addAll(getDailyCashViewModel.dailyCashRegisterSummarylist.value!!)
            }
            Log.e(TAG, "In view model observe...." + dailyCashRegisterSummaryBOList?.size)
            //load default fragment...
            //show fragment based on Index..
            showActiveFragment()
        }

        getDailyCashViewModel.dailyCashRegisterlist.observe(viewLifecycleOwner) {
            if (getDailyCashRegisterList?.size == 0) {
                getDailyCashRegisterList?.addAll(getDailyCashViewModel.dailyCashRegisterlist.value!!)
            }
            Log.e(TAG,
                "In view model observe...." + getDailyCashViewModel.dailyCashRegisterlist.value!!.size)
            //load default fragment...
            //show fragment based on Index..
            showActiveFragment()
        }

        //handle visibility of edittext...
        handleVisibility()
    }

    private fun initViews(view: View) {
        (context as DashBoardActivity).toolbar?.title =
            requireActivity().resources.getString(R.string.dashboard)

        //EditText binding..
        etFromDate = view.findViewById(R.id.et_from_date)
        etToDate = view.findViewById(R.id.et_to_date)
        etFromDate?.setOnClickListener(this)
        etToDate?.setOnClickListener(this)

        //EditText binding..
        etDate = view.findViewById(R.id.et_date)
        etDate?.setOnClickListener(this)
        etSelectLab = view.findViewById(R.id.et_select_lab)
        etSelectLab?.setOnClickListener(this)

        //Constratint Layout binding...
        fromToDateConstraintLayout = view.findViewById(R.id.nested_constraint2_2)
        llSelectLabView = view.findViewById(R.id.ll_search_labname_view)

        //setTodaysDate..
        etDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))

        //binding all views...
        frameLayout = view.findViewById(R.id.frame_layout)
        smoothBottomBar = view.findViewById(R.id.bottomBar)

        smoothBottomBar?.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelect(pos: Int): Boolean {
                when (pos) {
                    0 -> {
                        (context as DashBoardActivity).toolbar?.title =
                            requireActivity().resources.getString(R.string.dashboard)
                        loadDashGraphFragment()

                        //store activeIndex value
                        activeIndex = smoothBottomBar?.itemActiveIndex!!

                        //handle visibility of Date EditText..
                        handleVisibility()
                    }

                    1 -> {
                        (context as DashBoardActivity).toolbar?.title =
                            requireActivity().resources.getString(R.string.daily_cash_summary)
                        loadDailyCashSummaryFragment()

                        //store activeIndex value
                        activeIndex = smoothBottomBar?.itemActiveIndex!!

                        //handle visibility of Date EditText..
                        handleVisibility()
                    }

                    2 -> {
                        (context as DashBoardActivity).toolbar?.title =
                            requireActivity().resources.getString(R.string.daily_cash_register)
                        loadDailyCashRegisterFragment()

                        //store activeIndex value
                        activeIndex = smoothBottomBar?.itemActiveIndex!!

                        //handle visibility of Date EditText..
                        handleVisibility()
                    }

                    3 -> {
                        (context as DashBoardActivity).toolbar?.title =
                            requireActivity().resources.getString(R.string.daily_status)
                        loadDailyCashStatusFragment()

                        //store activeIndex value
                        activeIndex = smoothBottomBar?.itemActiveIndex!!

                        //handle visibility of Date EditText..
                        handleVisibility()
                    }
                }
                return false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.et_from_date -> {
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
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        //etFromDate?.setText(String.format("%02d", (monthOfYear + 1)) + "/" + dayOfMonth.toString() + "/" + year)
                        etToDate?.setText("$dayOfMonth/" + String.format("%02d",
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

    @SuppressLint("SimpleDateFormat")
    private fun loadDataOnTodaysDate() {
        etToDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))
        etFromDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))
        /*var sdf = SimpleDateFormat("dd/MM/yyyy")
        val c = Calendar.getInstance()
        c.time = sdf.parse(etToDate?.text.toString())
        c.add(Calendar.DATE, -7)
        sdf = SimpleDateFormat("dd/MM/yyyy")
        val resultdate = Date(c.timeInMillis)
        val stToDate = sdf.format(resultdate)

        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val toDate: Date? = dateFormat.parse(stToDate)

        val dateFormat1: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val strDate = dateFormat1.format(toDate)
        etFromDate?.setText(strDate)*/

        //GetDailyCash Register....
        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            //getDailyCashRegister detailed of lab
            getDailyCashRegister(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                "dd/MM/yyyy",
                "MM/dd/yyyy"),
                CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                    "dd/MM/yyyy",
                    "MM/dd/yyyy"))

            //getDailyCashSummary of lab
            getDailyCashSummary(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                "dd/MM/yyyy",
                "MM/dd/yyyy"),
                CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                    "dd/MM/yyyy",
                    "MM/dd/yyyy"))

            getDailyCashRegister_Rec(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                "dd/MM/yyyy",
                "MM/dd/yyyy"),
                CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                    "dd/MM/yyyy",
                    "MM/dd/yyyy"))
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
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
        // val toDate: Date? = dateFormat.parse(stToDate)
        // val todaysDate: Date? = dateFormat.parse(CommonMethods.getTodayDate("dd/MM/yyyy"))

        val fromDate: Date? = dateFormat.parse(etFromDate?.text.toString())
        val toDate: Date? = dateFormat.parse(etToDate?.text.toString())

        try {

            val daysDifference = printDifference(fromDate!!, toDate!!)

            if (daysDifference > 7) {
                etFromDate?.text = null
                //etFromDate?.setText("")
                //etToDate?.setText("")
                CommonMethods.showDialogForError(mContext!!,
                    "Select date range within 7 days only.")
            } else if (toDate.before(fromDate)) {
                //etToDate?.setText("")
                CommonMethods.showDialogForError(mContext!!,
                    "Please select to date after from date.")
            } else if (fromDate.after(toDate)) {
                //etFromDate?.setText("")
                CommonMethods.showDialogForError(mContext!!,
                    "Please select from date before to date.")
            } else {
                //val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                //val strDate = dateFormat.format(toDate)
                //etToDate?.setText(strDate)

                //GetDailyCash Register....
                if (CommonMethods.isNetworkAvailable(mContext!!)) {
                    //getDailyCashRegister detailed of lab
                    getDailyCashRegister(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                        "dd/MM/yyyy",
                        "MM/dd/yyyy"),
                        CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                            "dd/MM/yyyy",
                            "MM/dd/yyyy"))

                    //getDailyCashSummary of lab
                    getDailyCashSummary(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                        "dd/MM/yyyy",
                        "MM/dd/yyyy"),
                        CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                            "dd/MM/yyyy",
                            "MM/dd/yyyy"))

                    getDailyCashRegister_Rec(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                        "dd/MM/yyyy",
                        "MM/dd/yyyy"),
                        CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                            "dd/MM/yyyy",
                            "MM/dd/yyyy"))
                } else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
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

    private fun loadDashGraphFragment() {
        val bundle = Bundle()
        bundle.putParcelableArrayList("labname_list", getLabNameArrayList)
        navController.navigate(R.id.dailyCashGraphFragment, bundle)
    }

    fun loadDailyCashRegisterFragment() {
        val bundle = Bundle()
        bundle.putParcelableArrayList("daily_cash_register_list", getDailyCashRegisterList)
        bundle.putString("stname", "no_name")
        bundle.putString("isBackPressed", "0")
        navController.navigate(R.id.dailyCashRegisterFragment, bundle)

        Log.e(TAG, "loadDailyCashRegisterFragment: ${getDailyCashRegisterList?.size}")
    }

    fun loadDailyCashSummaryFragment() {
        val bundle = Bundle()
        bundle.putParcelableArrayList("daily_cash_register_list", getDailyCashRegisterList)
        bundle.putParcelableArrayList("daily_cash_register_rec_list", getDailyCashRegisterRecList)
        bundle.putParcelableArrayList("daily_cash_summary_list", dailyCashRegisterSummaryBOList)

        Log.e(TAG, "loadDailyCashSummaryFragment: " + getDailyCashRegisterList?.size)
        Log.e(TAG, "loadDailyCashSummaryFragment: " + dailyCashRegisterSummaryBOList?.size)

        navController.navigate(R.id.dailyCashSummaryFragment, bundle)
    }

    fun loadDailyCashStatusFragment() {
        val bundle = Bundle()
        bundle.putParcelableArrayList("daily_cash_register_list", getDailyCashRegisterList)
        navController.navigate(R.id.dailyCashStatusFragment, bundle)
    }

    private fun getDailyCashRegister(toDate: String?, fromDate: String?) {
        val params: MutableMap<String, String> = HashMap()
        params["companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            params["labcode"] = labNameBO?.labname.toString()
        } else {
            params["labcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()
        }
        params["fromdate"] = fromDate.toString()
        params["todate"] = toDate.toString()
        params["usertype"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)) {
            params["CCcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()
        }else{
            params["CCcode"] = ""
        }

        Log.e(TAG, "getDailyCashRegister: $params")
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.dailyCashRegister(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val dailyCashRegisterResponse = `object` as DailyCashRegisterResponse
                    progress.dismiss()
                    if (dailyCashRegisterResponse.ResponseCode == 200) {
                        if (dailyCashRegisterResponse.ResultArray?.size!! > 0) {
                            //first clear the list
                            getDailyCashRegisterList?.clear()
                            getDailyCashRegisterList?.addAll(dailyCashRegisterResponse.ResultArray!!)

                            //Add data in view model also...
                            getDailyCashViewModel.adddailyCashRegisterList(getDailyCashRegisterList!!)
                        } else {
                            //first clear the list
                            getDailyCashRegisterList?.clear()
                        }
                    } else {
                        //first clear the list
                        getDailyCashRegisterList?.clear()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    //first clear the list
                    getDailyCashRegisterList?.clear()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getDailyCashRegister_Rec(toDate: String?, fromDate: String?) {
        val params: MutableMap<String, String> = HashMap()
        params["companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            params["labcode"] = labNameBO?.labname.toString()
        } else {
            params["labcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()
        }
        params["fromdate"] = fromDate.toString()
        params["todate"] = toDate.toString()
        params["usertype"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)) {
            params["CCcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()
        }else{
            params["CCcode"] = ""
        }

        Log.e(TAG, "getDailyCashRegister_Rec: $params")
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.dailycashRegisterr_rec(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val dailyCashRegisterResponse = `object` as DailyCashRegisterResponse
                    progress.dismiss()
                    if (dailyCashRegisterResponse.ResponseCode == 200) {
                        if (dailyCashRegisterResponse.ResultArray?.size!! > 0) {
                            //first clear the list
                            getDailyCashRegisterRecList?.clear()
                            getDailyCashRegisterRecList?.addAll(dailyCashRegisterResponse.ResultArray!!)

                            //Add data in view model also...
                            getDailyCashViewModel.adddailyCashRegisterList(getDailyCashRegisterRecList!!)
                        } else {
                            //first clear the list
                            getDailyCashRegisterRecList?.clear()
                        }
                    } else {
                        //first clear the list
                        getDailyCashRegisterRecList?.clear()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    //first clear the list
                    getDailyCashRegisterRecList?.clear()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getDailyCashSummary(toDate: String?, fromDate: String?) {
        val params: MutableMap<String, String> = HashMap()
        params["companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            params["labcode"] = labNameBO?.labname.toString()
        } else {
            params["labcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()
        }

        params["fromdate"] = fromDate.toString()
        params["todate"] = toDate.toString()
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
                            //Add data to list..
                            //First clear the existing data of list...
                            dailyCashRegisterSummaryBOList?.clear()

                            var patientCount = 0
                            var testCharges = 0
                            var billAmt = 0
                            var unbillAmt = 0
                            var netPayment = 0
                            var discount = 0
                            var balance = 0
                            var otherCharges = 0
                            var rec_total = 0

                            for (i in 0 until dailyCashRegisterResponse.ResultArray!!.size) {
                                patientCount += dailyCashRegisterResponse.ResultArray!![i].patientcount!!.toInt()
                                testCharges += dailyCashRegisterResponse.ResultArray!![i].TestCharges.toInt()
                                billAmt += dailyCashRegisterResponse.ResultArray!![i].BillAmt.toInt()
                                unbillAmt += dailyCashRegisterResponse.ResultArray!![i].unbillamt!!.toInt()
                                netPayment += dailyCashRegisterResponse.ResultArray!![i].NetPayment!!.toInt()
                                discount += dailyCashRegisterResponse.ResultArray!![i].Discount.toInt()
                                balance += dailyCashRegisterResponse.ResultArray!![i].Balance.toInt()
                                otherCharges += dailyCashRegisterResponse.ResultArray!![i].Othercharges.toInt()
                                rec_total += dailyCashRegisterResponse.ResultArray!![i].rec_total?.toInt() ?: 0
                            }

                            dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(
                                patientCount.toString(),
                                AllKeys.PATIENT_COUNT))
                            /*dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(
                                testCharges.toString(),
                                AllKeys.TEST_CHARGES))*/
                            dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(
                                rec_total.toString(),
                                AllKeys.TODAYS_COLLECTION))
                            dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(billAmt.toString(),
                                AllKeys.BILL_AMT))
                            dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(unbillAmt.toString(),
                                AllKeys.UNBILLED_AMT))
                            dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(
                                netPayment.toString(),
                                AllKeys.RECEIVED_AMT))
                            dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(discount.toString(),
                                AllKeys.DISCOUNT))
                            dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(balance.toString(),
                                AllKeys.BALANCE))
                            dailyCashRegisterSummaryBOList?.add(DailyCashRegisterSummaryBO(
                                otherCharges.toString(),
                                AllKeys.OTHER_CHARGES))

                            //add data in viewmodel...
                            getDailyCashViewModel.adddailyCashSummaryList(dailyCashRegisterSummaryBOList!!)
                        } else {
                            //First clear the existing data of list...
                            dailyCashRegisterSummaryBOList?.clear()
                        }
                    } else {
                        //First clear the existing data of list...
                        dailyCashRegisterSummaryBOList?.clear()
                    }
                    //show fragment based on Index..
                    showActiveFragment()
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //First clear the existing data of list...
                    dailyCashRegisterSummaryBOList?.clear()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun showActiveFragment() {
        when (activeIndex) {
            0 -> {
                Log.e(TAG, "onClick: In Graph Fragment")
                (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.dashboard)
                loadDashGraphFragment()

                //store activeIndex value
                activeIndex = smoothBottomBar?.itemActiveIndex!!

                //handle visibility of Date EditText..
                handleVisibility()
            }

            1 -> {
                Log.e(TAG, "onClick: In Summary Fragment")
               // (activity as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.daily_cash_summary)
                loadDailyCashSummaryFragment()

                //store activeIndex value
                activeIndex = smoothBottomBar?.itemActiveIndex!!

                //handle visibility of Date EditText..
                handleVisibility()
            }

            2 -> {
                Log.e(TAG, "onClick: In Register Fragment")
                //(activity as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.daily_cash_register)
                loadDailyCashRegisterFragment()
                //store activeIndex value
                activeIndex = smoothBottomBar?.itemActiveIndex!!

                //handle visibility of Date EditText..
                handleVisibility()
            }

            3 -> {
                (activity as DashBoardActivity).toolbar?.title =
                    requireActivity().resources.getString(R.string.daily_status)
                loadDailyCashStatusFragment()

                //store activeIndex value
                activeIndex = smoothBottomBar?.itemActiveIndex!!

                //handle visibility of Date EditText..
                handleVisibility()
            }
        }
    }

    private fun handleVisibility() {
        if (activeIndex == 0) {
            fromToDateConstraintLayout?.visibility = View.GONE
            llSelectLabView?.visibility = View.GONE
        } else {
            fromToDateConstraintLayout?.visibility = View.VISIBLE
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                    .equals(AllKeys.SuperAdmin)
            ) {
                llSelectLabView?.visibility = View.VISIBLE
            }
        }
    }

    private fun getSuperAdminLabs() {
        val params: MutableMap<String, String> = HashMap()
        params["Pno"] = CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()

        Log.e(TAG, "getSuperAdminLabs: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please Wait....")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.getLabSuperAdmin(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getLabNameResponse = `object` as LabNameResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getLabNameResponse")
                    if (getLabNameResponse.ResponseCode == 200) {
                        getLabNameArrayList = getLabNameResponse.ResultArray

                        labNameBO = LabNameBO(getLabNameArrayList[0].labname,
                            getLabNameArrayList[0].labname_c)

                        etSelectLab?.setText(labNameBO?.labname_c)

                        //Get Patient data Data based on codition..
                        //GetDailyCash Register....
                        if (CommonMethods.isNetworkAvailable(mContext!!)) {
                            //getDailyCashRegister detailed of lab
                            getDailyCashRegister(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                                "dd/MM/yyyy",
                                "MM/dd/yyyy"),
                                CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                                    "dd/MM/yyyy",
                                    "MM/dd/yyyy"))

                            //getDailyCashSummary of lab
                            getDailyCashSummary(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                                "dd/MM/yyyy",
                                "MM/dd/yyyy"),
                                CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                                    "dd/MM/yyyy",
                                    "MM/dd/yyyy"))
                        } else {
                            CommonMethods.showDialogForError(mContext!!,
                                AllKeys.NO_INTERNET_AVAILABLE)
                        }
                    } else {
                        Toast.makeText(mContext!!, getLabNameResponse.Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
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

                    //Get Patient data Data based on codition..
                    //GetDailyCash Register....
                    if (CommonMethods.isNetworkAvailable(mContext!!)) {
                        //getDailyCashRegister detailed of lab
                        getDailyCashRegister(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                            "dd/MM/yyyy",
                            "MM/dd/yyyy"),
                            CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                                "dd/MM/yyyy",
                                "MM/dd/yyyy"))

                        //getDailyCashSummary of lab
                        getDailyCashSummary(CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(),
                            "dd/MM/yyyy",
                            "MM/dd/yyyy"),
                            CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(),
                                "dd/MM/yyyy",
                                "MM/dd/yyyy"))
                    } else {
                        CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
                    }
                }
            }
        }
    }
}