package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.google.android.material.tabs.TabLayout

import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.bms.pathogold_bms.adapter.DynamicFragmentAdapter
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.ViewAppointmentConsultationAdapter
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse
import com.bms.pathogold_bms.view_models.ViewAllAppointmentModel

class ViewAppointmentDashBoardFragment : BaseFragment(), View.OnClickListener , ViewAppointmentConsultationAdapter.GetPhleboConsulataionDoctor {

    private val TAG = "ViewAppointmentFragment"

    //EditText declaration..
    private var etFromDate: EditText? = null
    private var etToDate: EditText? = null
    private var etSearchPhleboConsultant: EditText? = null

    //Switch Compat declaration...
    private var switchAppointmentType:SwitchCompat?=null

    //Linearlayout declaration...
    private var llConsultation: LinearLayout? = null
    private var llPhleboConsultantView: LinearLayout? = null
    private var llNoAppointmentFound:LinearLayout?=null

    //Recycler view declaration...
    private var rvGetConsultation: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFoundConsultation: TextView? = null

    private var viewPager: ViewPager? = null
    private var mTabLayout: TabLayout? = null

    private var viewAppointmentAllMap: MutableMap<String, ArrayList<ViewAppointmentBO>> = LinkedHashMap()
    private var viewAppointmentList = ArrayList<ViewAppointmentBO>()

    private var isFirstTime=true

    //String declaration..
    private var stBookingType: String? = "Diagnostics"

    //Adapter declaration...
    private var viewAppointmentConsultationAdapter:ViewAppointmentConsultationAdapter?=null

    //ArrayList declaration...
    private var consultationBOArrayList = ArrayList<ConsultationBO>()
    private var pheloboBOArrayList = ArrayList<ConsultationBO>()

    //BusinessObject declaration...
    private var consultationBO:ConsultationBO?=null

    companion object{
        var tabName:String=""
    }

    private var mDynamicFragmentAdapter:DynamicFragmentAdapter?=null

    //View model declaration..
    private lateinit var viewAllAppointmentModel: ViewAllAppointmentModel

    override val activityLayout: Int
        get() =R.layout.fragment_view_appointment2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation...
        initViews(view)

        viewAllAppointmentModel = ViewModelProvider(requireActivity())[ViewAllAppointmentModel::class.java] // init view model

        consultationBO = ConsultationBO(mContext?.let { CommonMethods.getPrefrence(it, AllKeys.HSC_ID) }.toString()
            ,mContext?.let { CommonMethods.getPrefrence(it, AllKeys.PERSON_NAME) }.toString()
            ,mContext?.let { CommonMethods.getPrefrence(it, AllKeys.GCM_TOKEN) }.toString())

        //If laboratory or administrator or collection_center...then only this condition
        if(CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Lab_Technician) || CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Administrator)
            || CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)){
            if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                //getPhlebo List..
                getPhleboData()

                //Get consultation list
                getConsultationList()

            } else {
                CommonMethods.showDialogForError(requireActivity(), AllKeys.NO_INTERNET_AVAILABLE)
            }
        }else{
            llPhleboConsultantView?.visibility=View.GONE
        }

        if (viewAllAppointmentModel.list.value!!.isEmpty()) {
            if(CommonMethods.isNetworkAvailable(mContext!!)){
                etFromDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))
                var sdf = SimpleDateFormat("dd/MM/yyyy")
                val c = Calendar.getInstance()
                c.time = sdf.parse(etFromDate?.text.toString())
                c.add(Calendar.MONTH,1)
                sdf = SimpleDateFormat("dd/MM/yyyy")
                val resultdate = Date(c.timeInMillis)
                val stToDate = sdf.format(resultdate)

                val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val toDate: Date? = dateFormat.parse(stToDate)

                val dateFormat1: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                val strDate = dateFormat1.format(toDate)
                etToDate?.setText(strDate)

                //viewAppointmentAll(mContext?.let { CommonMethods.getPrefrence(it, AllKeys.HSC_ID) }.toString())
                viewAppointmentAll(mContext?.let { CommonMethods.getPrefrence(it, AllKeys.HSC_ID) }.toString(), CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(), "dd/MM/yyyy", "MM/dd/yyyy").toString(),
                    CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(), "dd/MM/yyyy", "MM/dd/yyyy").toString())

            }else{
                CommonMethods.showDialogForError(mContext!!,AllKeys.NO_INTERNET_AVAILABLE)
            }
        }

        viewAllAppointmentModel.list.observe(viewLifecycleOwner) {
            (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.view_appointment)
            viewAppointmentAllMap= viewAllAppointmentModel.list.value!!
            //Load dynamic tab layouts
            setDynamicFragmentToTabLayout(0)
            isFirstTime=false
        }
    }

    private fun initViews(view: View) {
        etFromDate = view.findViewById(R.id.et_from_date)
        etToDate = view.findViewById(R.id.et_to_date)
        etToDate?.setOnClickListener(this)
        etFromDate?.setOnClickListener(this)
        etSearchPhleboConsultant= view.findViewById(R.id.et_search_phlebo_consultant)

        //SwitchCompat type layout..
        switchAppointmentType=view.findViewById(R.id.switch_)

        //TextView...
        tvNoDataFoundConsultation=view.findViewById(R.id.tv_no_data_found_consulataion)

        //Linearlayout binding..
        llPhleboConsultantView = view.findViewById(R.id.ll_phlebo_consultant)
        llConsultation = view.findViewById(R.id.ll_consultation)
        llNoAppointmentFound=view.findViewById(R.id.ll_no_data_found)

        rvGetConsultation = view.findViewById(R.id.rv_consultation)
        val linearLayoutManager1 = LinearLayoutManager(mContext)
        rvGetConsultation?.layoutManager = linearLayoutManager1

        // initialise the layout
        viewPager = view.findViewById(R.id.viewpager);
        mTabLayout = view.findViewById(R.id.tabs);

        // setOffscreenPageLimit means number
        // of tabs to be shown in one page
        viewPager?.offscreenPageLimit = 5

        viewPager?.addOnPageChangeListener(TabLayoutOnPageChangeListener(mTabLayout))
        mTabLayout?.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // setCurrentItem as the tab position
                viewPager?.currentItem = tab.position
                tabName = tab.text as String
                if(!isFirstTime){
                    setDynamicFragmentToTabLayout(tab.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                tabName= tab.text as String
            }
        })

        switchAppointmentType?.setOnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            if (switchAppointmentType?.isChecked == true) {
                stBookingType = context?.getString(R.string.consultation)
            } else {
                stBookingType = context?.getString(R.string.diagnostics)
            }
            //call function based on button select
            onSelectOfRadioButton(stBookingType.toString())
        }

        etSearchPhleboConsultant?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTableForPhleboConsultant(s.toString())
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.et_from_date -> {
                //llConsultation?.visibility=View.GONE
                (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.view_appointment)+"("+consultationBO!!.Name+")"
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
                    }, mYear, mMonth, mDay)
                //datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.et_to_date -> {
                //llConsultation?.visibility=View.GONE
                (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.view_appointment)+"("+consultationBO!!.Name+")"
                //Current Date
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
                    }, mYear, mMonth, mDay)
                //datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDateAndLoadData() {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val fromDate: Date? = dateFormat.parse(etFromDate?.text.toString())
        val toDate: Date? = dateFormat.parse(etToDate?.text.toString())

        try {
            if (toDate?.before(fromDate) == true) {
                CommonMethods.showDialogForError(mContext!!, "Please select to date after from date.")
            } else if (fromDate?.after(toDate) == true) {
                CommonMethods.showDialogForError(mContext!!, "Please select from date before to date.")
            } else {
                //GetDailyCash Register....
                if (CommonMethods.isNetworkAvailable(mContext!!)) {
                    //getDailyCashSummary of lab
                    viewAppointmentAll(consultationBO!!.Pno, CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(), "dd/MM/yyyy", "MM/dd/yyyy").toString(),
                        CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(), "dd/MM/yyyy", "MM/dd/yyyy").toString())
                } else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun viewAppointmentAll(HSC_ID: String, fromDate: String, toDate: String) {
        val params: MutableMap<String, String> = HashMap()
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
            params["HSC_ID"] = "0"
            params["PatientMobilNo"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.MOBILE_NO) }.toString()
        } else {
            params["HSC_ID"] =HSC_ID
            params["PatientMobilNo"] = "0"
        }
        params["labcode"]=CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()
        params["fromdate"]=fromDate
        params["todate"]=toDate

        Log.e(TAG, "getPatientByMobileNo: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.viewAppointmentAll(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val viewAppointmentResponse = `object` as ViewAppointmentResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $viewAppointmentResponse")
                    viewAppointmentAllMap.clear()
                    viewAppointmentList.clear()
                    if (viewAppointmentResponse.ResponseCode == 200) {
                        llNoAppointmentFound?.visibility=View.GONE
                        viewPager?.visibility=View.VISIBLE
                        if(viewAppointmentResponse.ResultArray.size>0){
                            viewAppointmentResponse.ResultArray.reverse()
                            for (temp in viewAppointmentResponse.ResultArray) {
                                if (viewAppointmentAllMap.containsKey(temp.Status.trim())) {
                                    viewAppointmentList = viewAppointmentAllMap[temp.Status.trim()]!!
                                    viewAppointmentList.add(temp)
                                } else {
                                    viewAppointmentList = ArrayList()
                                    viewAppointmentList.add(temp)
                                    viewAppointmentAllMap[temp.Status.trim()] = viewAppointmentList
                                }
                            }

                            viewAllAppointmentModel.addNewValue(viewAppointmentAllMap)
                            //Load dynamic tab layouts
                            setDynamicFragmentToTabLayout(0)
                            isFirstTime=false

                        }else{
                            hideAppointmentView()
                        }
                    } else {
                        hideAppointmentView()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun hideAppointmentView() {
        llNoAppointmentFound?.visibility=View.VISIBLE
        viewPager?.visibility=View.GONE
        viewAppointmentAllMap.clear()
        viewAppointmentList.clear()
    }

    // show all the tab using DynamicFragmnetAdapter
    private fun setDynamicFragmentToTabLayout(position:Int) {
        // here we have given 10 as the tab number
        // you can give any number here
        for (i in viewAppointmentAllMap.keys) {
            // set the tab name as "Page: " + i
                if(!isMatching(i)){
                    mTabLayout!!.addTab(mTabLayout!!.newTab().setText(""+i))
                }
        }

    //    Log.e(TAG, "setDynamicFragmentToTabLayout: "+mTabLayout!!.get )

        mDynamicFragmentAdapter = DynamicFragmentAdapter(childFragmentManager, mTabLayout!!.tabCount,viewAppointmentAllMap,tabName,pheloboBOArrayList,consultationBOArrayList)

        // set the adapter
        viewPager!!.adapter = mDynamicFragmentAdapter

        // set the current item as 0 (when app opens for first time)
        viewPager!!.currentItem = position
    }

    private fun isMatching(tabString: String?): Boolean {
        var match = false
        for (i in 0 until mTabLayout?.tabCount!!) {
            if (mTabLayout?.getTabAt(i)?.text.toString() == tabString) {
                match = true
            }
        }
        return match
    }

    private fun onSelectOfRadioButton(stBookingTypeFor: String) {
        if (stBookingTypeFor.equals("Consultation", false)) {
            if (consultationBOArrayList.size > 0) {
                llConsultation?.visibility = View.VISIBLE
                tvNoDataFoundConsultation?.visibility = View.GONE
                viewAppointmentConsultationAdapter = mContext?.let {
                    ViewAppointmentConsultationAdapter(it,
                        consultationBOArrayList,
                        this)
                }
                rvGetConsultation?.adapter = viewAppointmentConsultationAdapter
            } else {
                llConsultation?.visibility = View.GONE
                tvNoDataFoundConsultation?.visibility = View.VISIBLE
            }
        } else if (stBookingTypeFor.equals("Diagnostics", false)) {
            llConsultation?.visibility = View.VISIBLE
            tvNoDataFoundConsultation?.visibility = View.GONE
            if (pheloboBOArrayList.size > 0) {
                viewAppointmentConsultationAdapter = mContext?.let {
                    ViewAppointmentConsultationAdapter(it,
                        pheloboBOArrayList,
                        this)
                }
                rvGetConsultation?.adapter = viewAppointmentConsultationAdapter
            } else {
                llConsultation?.visibility = View.GONE
                tvNoDataFoundConsultation?.visibility = View.VISIBLE
            }
        }
    }

    override fun getPhleboConsultationDoctor(consultationBO1: ConsultationBO) {
        consultationBO=consultationBO1
        (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.view_appointment)+"("+consultationBO!!.Name+")"
        //get Appointment...
        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            viewAppointmentAll(consultationBO!!.Pno,CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(), "dd/MM/yyyy", "MM/dd/yyyy").toString(),
                CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(), "dd/MM/yyyy", "MM/dd/yyyy").toString())
            llConsultation?.visibility=View.GONE
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    fun filterTableForPhleboConsultant(text: String?) {
        if (stBookingType.equals("Diagnostics", false)) {
            if (pheloboBOArrayList.size > 0) {
                val filteredList1: java.util.ArrayList<ConsultationBO> =
                    java.util.ArrayList<ConsultationBO>()
                for (item in pheloboBOArrayList) {
                    if (text?.let { item.Name.lowercase(Locale.ROOT).contains(it) } == true
                        || text?.let { item.Name.uppercase(Locale.ROOT).contains(it) } == true
                        || text?.let { item.Pno.uppercase(Locale.ROOT).contains(it) } == true
                    ) {
                        filteredList1.add(item)
                    }
                }
                //Log.e(TAG, "filter: size" + filteredList1.size());
                // Log.e(TAG, "filter: List" + filteredList1.toString());
                viewAppointmentConsultationAdapter?.updateData(mContext, filteredList1)
            }
        } else if (stBookingType.equals("Consultation", false)) {
            if (consultationBOArrayList.size > 0) {
                val filteredList1: java.util.ArrayList<ConsultationBO> =
                    java.util.ArrayList<ConsultationBO>()
                for (item in consultationBOArrayList) {
                    if (text?.let { item.Name.lowercase(Locale.ROOT).contains(it) } == true
                        || text?.let { item.Name.uppercase(Locale.ROOT).contains(it) } == true
                        || text?.let { item.Pno.uppercase(Locale.ROOT).contains(it) } == true
                    ) {
                        filteredList1.add(item)
                    }
                }
                //Log.e(TAG, "filter: size" + filteredList1.size());
                // Log.e(TAG, "filter: List" + filteredList1.toString());
                viewAppointmentConsultationAdapter?.updateData(mContext, filteredList1)
            }
        }
    }

    private fun getPhleboData() {
        val params: MutableMap<String, String> = HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        // params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.USERTYPE).toString() }!!

        Log.e(TAG, "getPHleboData: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getPhleboList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val consultationResponse = `object` as ConsultationResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $consultationResponse")
                    if (consultationResponse.ResponseCode == 200) {
                        //Calling Intent...
                        // Toast.makeText(mContext, "" + getPatientResponse.Message, Toast.LENGTH_SHORT).show()
                        pheloboBOArrayList.clear()
                        pheloboBOArrayList.addAll(consultationResponse.ResultArray)
                        Log.e(TAG, "onSuccess:Phelobo\n\n $pheloboBOArrayList")
                    }
                    //On select of diagnostics, and consultation..
                    onSelectOfRadioButton(stBookingType.toString())
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getConsultationList() {
        val params: MutableMap<String, String> =HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.LABNAME).toString() }!!
        params["Companyid"] =   mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

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
                        //Calling Intent...
                        // Toast.makeText(mContext, "" + getPatientResponse.Message, Toast.LENGTH_SHORT).show()
                        consultationBOArrayList.clear()
                        consultationBOArrayList.addAll(consultationResponse.ResultArray)
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