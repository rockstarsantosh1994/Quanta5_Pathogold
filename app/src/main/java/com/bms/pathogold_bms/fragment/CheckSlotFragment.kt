package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.BookAppointmentActivity
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.ConsultationAdapter
import com.bms.pathogold_bms.adapter.GetSlotsAdapter
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getslots.GetSlotsBO
import com.bms.pathogold_bms.model.getslots.GetSlotsResponse
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientBO
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.HashMap

class CheckSlotFragment : BaseFragment(), View.OnClickListener, DatePickerListener,
    GetSlotsAdapter.GetSlotAdapterOperation,ConsultationAdapter.ConsultationAdapterOperation {

    //Declaration...
    //Toolbar declaration...
    private var toolbar: Toolbar? = null

    private val TAG = "CheckSlotActivity"

    //Editext declaration..
    private var etBookingFor: EditText? = null
    private var etDate: EditText? = null
    private var etSearch: EditText? = null

    //Switch Compat declaration...
    private var switchAppointmentType: SwitchCompat? = null

    //DatePicker declaration..
    private lateinit var datePicker: HorizontalPicker

    //Recyclerview declaration...
    private var rvGetSlots: RecyclerView? = null
    private var rvGetConsultation: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvNoDataFoundConsultation: TextView? = null
    private var tvSearchText: TextView? = null

    //LinearLayout declaration...
    private var llConsultation: LinearLayout? = null
    private var llSwitchGroup: LinearLayoutCompat? = null

    private var stAppointmentType: String? = null
   // private var stBookingType: String? = "Diagnostics"
    private var stBookingType: String? = AllKeys.CONSULTATION

    private var viewAppointmentBO: ViewAppointmentBO? = null

    private val consultationBOArrayList = ArrayList<ConsultationBO>()
    private val pheloboBOArrayList = ArrayList<ConsultationBO>()

    private var consultationBO: ConsultationBO? = null
    private var consultationAdapter: ConsultationAdapter? = null
    private var getPatientListBO: GetPatientListBO? = null
    private var vaccinationPatientBO: VaccinationPatientBO? = null

    private var appointmentDate: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable.
        val arguments = requireArguments()
        //Here stAppointment type is new, reschedule, vaccine appointment...
        stAppointmentType = arguments.getString("app_type")
        getPatientListBO = arguments.getParcelable("patient_bo")
        vaccinationPatientBO = arguments.getParcelable("vaccination_patient_bo")

        Log.e(TAG, "onViewCreated: StAppointment Type "+stAppointmentType )

        if (vaccinationPatientBO != null) {
            etDate?.setText(CommonMethods.parseDateToddMMyyyy(vaccinationPatientBO?.appointmentdate, "MM/dd/yyyy HH:mm:ss a", "dd/MM/yyyy"))
            appointmentDate=CommonMethods.parseDateToddMMyyyy(vaccinationPatientBO?.appointmentdate, "MM/dd/yyyy HH:mm:ss a", "dd/MM/yyyy")
        }

        Log.e(TAG, "onViewCreated: PatientBO $getPatientListBO")
        Log.e(TAG, "onViewCreated: vaccinationPatientBO $vaccinationPatientBO")

        if (stAppointmentType.equals("reschdeule", false)) {
            viewAppointmentBO = arguments.getParcelable("view_app_bo")
        }

        //basic intlaisation...
        initViews(view)

        if (CommonMethods.getPrefrence(requireActivity(), AllKeys.USERTYPE).equals(AllKeys.Consultant)) {
            stBookingType = context?.getString(R.string.consultation)
            llSwitchGroup?.visibility = View.GONE
        } else {
            llSwitchGroup?.visibility = View.VISIBLE
            if (activity?.let { CommonMethods.isNetworkAvailable(it) } == true) {

                //getPhlebo List..
                getPhleboData()

                //Get consultation list
                getConsultationList()

            } else {
                showDialog(AllKeys.NO_INTERNET_AVAILABLE)
            }
        }

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Consultant)) {
            stBookingType = context?.getString(R.string.consultation)
            llSwitchGroup?.visibility = View.GONE
        } else {
            llSwitchGroup?.visibility = View.GONE
            //stBookingType = context?.getString(R.string.diagnostics)

            //On select of diagnostics, and consultation..
            onSelectOfRadioButton(stBookingType.toString())

            //reset Existing Data
            resetRecyclerView()
        }
        Log.e(TAG, "switchAppointmentType: $stBookingType")
    }

    override val activityLayout: Int
        get() = R.layout.activity_check_slot

    @SuppressLint("WrongConstant", "SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews(view: View) {
        //Binding Views...
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.book_appointment)

        //Editext binding...
        etBookingFor = view.findViewById(R.id.et_bookingfor)
        etDate = view.findViewById(R.id.et_date)
        etDate?.isFocusableInTouchMode = true
        etSearch = view.findViewById(R.id.et_search)

        //DatePicker binding..
        datePicker = view.findViewById(R.id.datePicker)

        // initialize it and attach a listener
        datePicker.setListener(this).init()

        //Set todays date...
        datePicker.setDate(DateTime())

        //SwitchCompat type layout..
        switchAppointmentType = view.findViewById(R.id.switch_)
        switchAppointmentType?.isChecked = true
        //AppCompatButton binding...
        //btnGetSlots = view.findViewById(R.id.btn_get_slots)

        //TextView binding...
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)
        tvNoDataFoundConsultation = view.findViewById(R.id.tv_no_data_found_consulataion)
        tvSearchText = view.findViewById(R.id.tv_search_text)
        tvSearchText?.text = context?.getString(R.string.search_phelobo)

        //RecycleView binding..
        rvGetSlots = view.findViewById(R.id.rv_get_slots)
        val linearLayoutManager = object : LinearLayoutManager(activity) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvGetSlots?.layoutManager = linearLayoutManager

        rvGetConsultation = view.findViewById(R.id.rv_consultation)
        val linearLayoutManager1 = LinearLayoutManager(activity)
        rvGetConsultation?.layoutManager = linearLayoutManager1

        //LinearLayout binding..
        llConsultation = view.findViewById(R.id.ll_consultation)
        llSwitchGroup = view.findViewById(R.id.ll_switch_group)

        //Click Listeners....
        etDate?.setOnClickListener(this)
        etDate?.isFocusableInTouchMode = true

        etDate?.setText(CommonMethods.getTodayDate("dd/MM/yyyy"))

        //set Text if phelbo login....
        Log.e(TAG, "initViews: " + activity?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE).toString() })
        if (CommonMethods.getPrefrence(requireActivity(), AllKeys.USERTYPE) != (AllKeys.Patient)) {
            etBookingFor?.setText(mContext?.let {
                CommonMethods.getPrefrence(it,
                    AllKeys.PERSON_NAME)
            })
            consultationBO =
                ConsultationBO(CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString(),
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME).toString(),
                    "")

            if (isValidated()) {
                appointmentDate = CommonMethods.getTodayDate("MM/dd/yyyy")
                //get available slots...
                if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                    getSlots(appointmentDate)
                } else {
                    showDialog(AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        } else {
            etBookingFor?.setText(null)
        }

        switchAppointmentType?.setOnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            stBookingType = if (switchAppointmentType?.isChecked == true) {
                context?.getString(R.string.consultation)
            } else {
                context?.getString(R.string.diagnostics)
            }

            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Consultant)) {
                stBookingType = context?.getString(R.string.consultation)
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
        if (isValidated()) {
            //get available sltos...
            if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                getSlots(appointmentDate)
            } else {
                showDialog(AllKeys.NO_INTERNET_AVAILABLE)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            //On Click select date...
            R.id.et_date -> {
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->
                        //etDate?.setText("" + (monthOfYear + 1) + "/" + dayOfMonth.toString() + "/" + year)
                        etDate?.setText(dayOfMonth.toString() + "/" + String.format("%02d",
                            (monthOfYear + 1)) + "/" + year)
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog.show()
            }
        }
    }

    private fun getSlots(date: String?) {
        val params: MutableMap<String, String> = HashMap()
        params["Pno"] = consultationBO?.Pno.toString()
        params["DayDate"] = date.toString()
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getSlots: $params")

        val progress = ProgressDialog(activity)
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
                                    this@CheckSlotFragment,
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

    override fun onBookSlot(getSlotsBO: GetSlotsBO) {
        val intent = Intent(context, BookAppointmentActivity::class.java)
        intent.putExtra("slot_bo", getSlotsBO)
        intent.putExtra("consultation_bo", consultationBO)
        intent.putExtra("patient_bo", getPatientListBO)
        intent.putExtra("vaccination_patient_bo", vaccinationPatientBO)
        intent.putExtra("date", appointmentDate)
        intent.putExtra("appointment_flag_type", stBookingType)
        startActivity(intent)
    }

    override fun onPhelboSelect(consultationBO1: ConsultationBO) {
        consultationBO = consultationBO1
        etBookingFor?.setText(consultationBO1.Name)

        if (isValidated()) {
            //get available sltos...
            if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                getSlots(appointmentDate)
            } else {
                showDialog(AllKeys.NO_INTERNET_AVAILABLE)
            }
        }
        //reset Existing Data
        resetRecyclerView()
    }

    private fun resetRecyclerView() {
        tvNoDataFound?.visibility = View.VISIBLE
        rvGetSlots?.visibility = View.GONE
    }

    override fun reScheduleAppointment(getSlotsBO: GetSlotsBO) {
        val params: MutableMap<String, String> = HashMap()
        params["AppBookingId"] = viewAppointmentBO?.BookApp_Id.toString()
        //params["RescheduleDateTime"] = CommonMethods.parseDateToddMMyyyy(appointmentDate, "dd/MM/yyyy", "MM/dd/yyyy").toString() + " " + getSlotsBO.Slot
        params["RescheduleDateTime"] = appointmentDate.toString() + " " + getSlotsBO.Slot
        params["timeslot"] = getSlotsBO.Slot
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["Pno"] = consultationBO?.Pno.toString()
        params["TestName"] =  viewAppointmentBO?.Testname.toString()

        Log.e(TAG, "reScheduleAppointment: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Rescheduleing appointment...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.patientAppointmentCancelAndReschedule(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        showDialogForSuccess(commonResponse.Message)
                        Toast.makeText(mContext, commonResponse.Message, Toast.LENGTH_SHORT).show()
                    } else {
                        showDialog(commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getPhleboData() {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        params["Companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
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
                        pheloboBOArrayList.addAll(consultationResponse.ResultArray)

                        Log.e(TAG, "onSuccess:Phelobo\n\n $pheloboBOArrayList")

                        if (pheloboBOArrayList.size > 0) {
                            //On select of diagnostics, and consultation..
                            onSelectOfRadioButton(stBookingType.toString())
                        }
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
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME).toString() }!!
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

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
                        consultationBOArrayList.addAll(consultationResponse.ResultArray)
                        //On select of diagnostics, and consultation..
                        onSelectOfRadioButton(stBookingType.toString())
                        Log.e(TAG, "onSuccess:Consultation\n\n $consultationBOArrayList")
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
        if (stBookingTypeFor.equals(context?.getString(R.string.consultation), false)) {
            tvSearchText?.text = context?.getString(R.string.search_consultant)
            if (consultationBOArrayList.size > 0) {
                llConsultation?.visibility = View.VISIBLE
                tvNoDataFoundConsultation?.visibility = View.GONE
                consultationAdapter = mContext?.let {
                    ConsultationAdapter(it,
                        consultationBOArrayList,
                        this@CheckSlotFragment)
                }
                rvGetConsultation?.adapter = consultationAdapter
            } else {
                llConsultation?.visibility = View.GONE
                tvNoDataFoundConsultation?.visibility = View.VISIBLE
            }
        } else if (stBookingTypeFor.equals(context?.getString(R.string.diagnostics), false)) {
            tvSearchText?.text = context?.getString(R.string.search_phelobo)
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                    .equals(AllKeys.Consultant, false)
                || CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                    .equals(AllKeys.Phlebotomist, false)
            ) {
                llConsultation?.visibility = View.GONE
                etBookingFor?.setText(mContext?.let {
                    CommonMethods.getPrefrence(it,
                        AllKeys.PERSON_NAME)
                })
                consultationBO =
                    ConsultationBO(CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID)
                        .toString(),
                        CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME).toString(), "")
            } else {
                llConsultation?.visibility = View.VISIBLE
                tvNoDataFoundConsultation?.visibility = View.GONE
                if (pheloboBOArrayList.size > 0) {
                    consultationAdapter = mContext?.let {
                        ConsultationAdapter(it,
                            pheloboBOArrayList,
                            this@CheckSlotFragment)
                    }
                    rvGetConsultation?.adapter = consultationAdapter
                } else {
                    llConsultation?.visibility = View.GONE
                    tvNoDataFoundConsultation?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showDialog(message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(requireActivity())
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            //.setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    private fun showDialogForSuccess(message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(requireActivity())
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                (context as DashBoardActivity).navController.popBackStack(R.id.checkSlotFragment2,
                    true)
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    fun filterTable(text: String?) {
        if (stBookingType.equals(context?.getString(R.string.diagnostics), false)) {
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
        } else if (stBookingType.equals(context?.getString(R.string.consultation), false)) {
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

    private fun isValidated(): Boolean {
        if (etDate?.text.toString().isEmpty()) {
            etDate?.error = "Please select date!"
            etDate?.requestFocus()
            return false
        }

        return true
    }
}