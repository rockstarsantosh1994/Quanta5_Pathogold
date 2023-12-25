package com.bms.pathogold_bms.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.ConsultationAdapter
import com.bms.pathogold_bms.adapter.GetSlotsAdapter
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse
import com.bms.pathogold_bms.model.getslots.GetSlotsBO
import com.bms.pathogold_bms.model.getslots.GetSlotsResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.HashMap

class CheckSlotForBookAppActivity : BaseActivity(), DatePickerListener,
    GetSlotsAdapter.GetSlotAdapterOperation, ConsultationAdapter.ConsultationAdapterOperation {

    private val TAG = "CheckSlotForBookAppActi"

    var toolbar: Toolbar? = null

    //EditText declaration..
    private var etSearch: EditText? = null

    //DatePicker declaration..
    private lateinit var datePicker: HorizontalPicker

    private var appointmentDate: String? = null

    //Recyclerview declaration...
    private var rvGetSlots: RecyclerView? = null
    private var rvGetConsultation: RecyclerView? = null

    //LinearLayout declaration...
    private var llConsultation: LinearLayout? = null
    private var llSwitchGroup: LinearLayoutCompat? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvNoDataFoundConsultation: TextView? = null
    private var tvSearchText: TextView? = null

    private var stAppointmentType: String? = "new"

    //Switch Compat declaration...
    private var switchAppointmentType: SwitchCompat? = null

    private var consultationBO: ConsultationBO? = null
    private var consultationAdapter: ConsultationAdapter? = null

    private var getLabDetailsBO: GetLabDetailsBO? = null

    private val consultationBOArrayList = ArrayList<ConsultationBO>()
    private val pheloboBOArrayList = ArrayList<ConsultationBO>()

    private var stBookingType:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialisation..
        initViews()

        getLabDetailsBO = intent?.getParcelableExtra("lab_detail_bo")

        when (getLabDetailsBO?.app_type?.replace(" ","")) {
            //"P" for Phlebo data will be work
            "P", "p" -> {
                llSwitchGroup?.visibility=View.GONE
                llConsultation?.visibility=View.GONE
                stBookingType=getString(R.string.diagnostics)
                if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                    //getPhlebo List..
                    getPhleboData()
                } else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
                }
            }

            //"C" for Consultation data will be loaded
            "C" -> {
                llSwitchGroup?.visibility=View.GONE
                llConsultation?.visibility=View.GONE
                stBookingType=getString(R.string.consultation)
                if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                    //Get consultation list
                    getConsultationList()
                } else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
                }
            }

            //"B" for both data will be loaded...
            "B" -> {
                llSwitchGroup?.visibility=View.VISIBLE
                stBookingType=getString(R.string.diagnostics)
                if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                    //getPhlebo List..
                    getPhleboData()

                    //Get consultation list
                    getConsultationList()

                } else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        }
    }

    override val activityLayout: Int
        get() = R.layout.activity_check_slot

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        toolbar?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        toolbar?.title = resources.getString(R.string.book_appointment)
        setSupportActionBar(toolbar)
        toolbar?.visibility=View.VISIBLE

        //EditText declaration..
        etSearch = findViewById(R.id.et_search)

        //DatePicker binding..
        datePicker = findViewById(R.id.datePicker)

        // initialize it and attach a listener
        datePicker.setListener(this).init()

        //Set todays date...
        datePicker.setDate(DateTime())

        //TextView binding...
        tvNoDataFound = findViewById(R.id.tv_no_data_found)
        tvNoDataFoundConsultation = findViewById(R.id.tv_no_data_found_consulataion)
        tvSearchText = findViewById(R.id.tv_search_text)
        tvSearchText?.text = resources.getString(R.string.search_phelobo)

        //RecycleView binding..
        rvGetSlots = findViewById(R.id.rv_get_slots)
        val linearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvGetSlots?.layoutManager = linearLayoutManager

        rvGetConsultation = findViewById(R.id.rv_consultation)
        val linearLayoutManager1 = LinearLayoutManager(mContext)
        rvGetConsultation?.layoutManager = linearLayoutManager1

        //LinearLayout binding..
        llConsultation = findViewById(R.id.ll_consultation)
        llSwitchGroup = findViewById(R.id.ll_switch_group)

        //SwitchCompat type layout..
        switchAppointmentType = findViewById(R.id.switch_)

        switchAppointmentType?.setOnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            stBookingType = if (switchAppointmentType?.isChecked == true) {
                getString(R.string.consultation)
            } else {
                getString(R.string.diagnostics)
            }

            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Consultant)) {
                stBookingType = getString(R.string.consultation)
                llSwitchGroup?.visibility = View.GONE
            } else {
                llSwitchGroup?.visibility = View.VISIBLE
                //stBookingType = context?.getString(R.string.diagnostics)

                //On select of diagnostics, and consultation..
                onSelectOfRadioButton(stBookingType.toString())

                //reset Existing Data
                resetRecyclerView()
            }
            Log.e(TAG, "switchAppointmentType: $stBookingType")
        }

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    override fun onDateSelected(dateSelected: DateTime?) {
        appointmentDate = CommonMethods.parseDateToddMMyyyy(dateSelected.toString(),
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "MM/dd/yyyy")
        Log.e(TAG, "onDateSelected: $appointmentDate")
        Log.e(TAG, "onDateSelected: ${dateSelected.toString()}")
        //get available sltos...
        if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
            getSlots(appointmentDate)
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    private fun getSlots(date: String?) {
        val params: MutableMap<String, String> = HashMap()
        params["Pno"] = consultationBO?.Pno.toString()
        params["DayDate"] = date.toString()

        Log.e(TAG, "getSlots: $params")

        val progress = ProgressDialog(this@CheckSlotForBookAppActivity)
        progress.setMessage("Please Wait....")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.getSlots(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getSlotsResponse = `object` as GetSlotsResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getSlotsResponse")
                    if (getSlotsResponse.ResponseCode == 200) {
                        tvNoDataFound?.visibility = View.GONE
                        rvGetSlots?.visibility = View.VISIBLE
                        val getSlotAdapter = mContext?.let {
                            stAppointmentType?.let { it1 ->
                                GetSlotsAdapter(it,
                                    getSlotsResponse.ResultArray,
                                    this@CheckSlotForBookAppActivity,
                                    it1)
                            }
                        }
                        rvGetSlots?.adapter = getSlotAdapter
                    } else {
                        tvNoDataFound?.visibility = View.VISIBLE
                        rvGetSlots?.visibility = View.GONE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getPhleboData() {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["LabCode"] = getLabDetailsBO?.labcode.toString()
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getPHleboData: $params")

        val progress = ProgressDialog(this@CheckSlotForBookAppActivity)
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
                        pheloboBOArrayList.addAll(consultationResponse.ResultArray)

                        Log.e(TAG, "onSuccess:Phelobo\n\n $pheloboBOArrayList")

                        if (pheloboBOArrayList.size > 0) {
                            //On select of diagnostics, and consultation..
                            onSelectOfRadioButton(stBookingType)
                        }
                    }else{
                        llConsultation?.visibility = View.GONE
                        tvNoDataFoundConsultation?.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getConsultationList() {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["LabCode"] = getLabDetailsBO?.labcode.toString()
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getConsultationList: $params")

        val progress = ProgressDialog(this@CheckSlotForBookAppActivity)
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
                        consultationBOArrayList.addAll(consultationResponse.ResultArray)
                        //On select of diagnostics, and consultation..
                        onSelectOfRadioButton(stBookingType)
                        Log.e(TAG, "onSuccess:Consultation\n\n $consultationBOArrayList")
                    }else{
                        llConsultation?.visibility = View.GONE
                        tvNoDataFoundConsultation?.visibility = View.VISIBLE
                    }

                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun onSelectOfRadioButton(stBookingTypeFor: String) {
        if (stBookingTypeFor.equals(getString(R.string.consultation), true)) {
            tvSearchText?.text = getString(R.string.search_consultant)
            if (consultationBOArrayList.size > 0) {
                llConsultation?.visibility = View.VISIBLE
                tvNoDataFoundConsultation?.visibility = View.GONE
                consultationAdapter = mContext?.let {
                    ConsultationAdapter(it,
                        consultationBOArrayList,
                        this)
                }
                rvGetConsultation?.adapter = consultationAdapter
            } else {
                llConsultation?.visibility = View.GONE
                tvNoDataFoundConsultation?.visibility = View.VISIBLE
            }
        } else if (stBookingTypeFor.equals(getString(R.string.diagnostics), true) ) {
            tvSearchText?.text = getString(R.string.search_phelobo)
            llConsultation?.visibility = View.VISIBLE
            tvNoDataFoundConsultation?.visibility = View.GONE
            if (pheloboBOArrayList.size > 0) {
                consultationAdapter = mContext?.let {
                    ConsultationAdapter(it, pheloboBOArrayList, this)
                }
                rvGetConsultation?.adapter = consultationAdapter
            } else {
                llConsultation?.visibility = View.GONE
                tvNoDataFoundConsultation?.visibility = View.VISIBLE
            }
        }
    }

    override fun onPhelboSelect(consultationBO1: ConsultationBO) {
        consultationBO = consultationBO1
        //get available sltos...
        if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
            getSlots(appointmentDate)
        } else {
            CommonMethods.showDialogForError(mContext!!,AllKeys.NO_INTERNET_AVAILABLE)
        }

        //reset Existing Data
        resetRecyclerView()
    }

    private fun resetRecyclerView() {
        tvNoDataFound?.visibility = View.VISIBLE
        rvGetSlots?.visibility = View.GONE
    }

    override fun onBookSlot(getSlotsBO: GetSlotsBO) {
        val intent = Intent(mContext!!, BookAppointmentActivity::class.java)
        intent.putExtra("slot_bo", getSlotsBO)
        intent.putExtra("consultation_bo", consultationBO)
        //intent.putExtra("patient_bo", getPatientListBO)
        //intent.putExtra("vaccination_patient_bo", vaccinationPatientBO)
        intent.putExtra("date", appointmentDate)
        intent.putExtra("appointment_flag_type", stBookingType)
        intent.putExtra("lab_details_bo",getLabDetailsBO)
        intent.putExtra("login_type",getString(R.string.book_app))
        startActivity(intent)
    }

    fun filterTable(text: String?) {
        if (stBookingType.equals(getString(R.string.diagnostics), false)) {
            if (pheloboBOArrayList.size > 0) {
                val filteredList1: ArrayList<ConsultationBO> = ArrayList<ConsultationBO>()
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
                consultationAdapter?.updateData(mContext, filteredList1)
            }
        } else if (stBookingType.equals(getString(R.string.consultation), false)) {
            if (consultationBOArrayList.size > 0) {
                val filteredList1: ArrayList<ConsultationBO> = ArrayList<ConsultationBO>()
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
                consultationAdapter?.updateData(mContext, filteredList1)
            }
        }
    }

    override fun reScheduleAppointment(getSlotsBO: GetSlotsBO) {
        Log.e(TAG, "reScheduleAppointment: ")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}