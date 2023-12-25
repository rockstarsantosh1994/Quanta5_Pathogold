package com.bms.pathogold_bms.fragment

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.GetPatientAdapter
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getpatient.GetPatientBO
import com.bms.pathogold_bms.model.getpatient.GetPatientResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getslots.GetSlotsBO
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToLong

class BookAppointmentFragment : BaseFragment(), View.OnClickListener,PaymentResultListener {

    //Declration...
    //Toolbar declaration...
    private var toolbar: Toolbar? = null

    //Edittext Declaration
    private var etBookingFor: EditText? = null
    private var etMobileNo: EditText? = null
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etAge: EditText? = null
    private var etRemark: EditText? = null
    private var etType: EditText? = null

    //Radio Group declaration...
    private var rgGender: RadioGroup? = null
    private var rgMale: RadioButton? = null
    private var rgFemale: RadioButton? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    //RecyclerView declaration....
    private var rvGetPatient: RecyclerView? = null

    //AppCompatButton declaration...
    private var btnSubmit: AppCompatButton? = null
    private var btnGet: AppCompatButton? = null

    var stGender: String? = null
    private val TAG = "BookAppointmentActivity"
    var getSlotsBO: GetSlotsBO? = null
    var consultationBO: ConsultationBO? = null
    var stDate: String? = null
    var stAppointmentType: String? = null
    val typeArrayList = ArrayList<String>()
    private var getPatientListBO: GetPatientListBO? = null
    private var vaccinationPatientBO: VaccinationPatientBO? = null

    override val activityLayout: Int
        get() = R.layout.fragment_book_appointment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext=activity

        //Basic intialisation....
        initViews(view)

        val arguments = requireArguments()
        try {
            getSlotsBO = arguments.getParcelable("slot_bo")
            consultationBO = arguments.getParcelable("consultation_bo")
            stDate = arguments.getString("date")
            stAppointmentType = arguments.getString("appointment_flag_type")

            Log.e(TAG, "onViewCreated: "+stAppointmentType )

            //set Text if phelbo login....
            etBookingFor?.setText(consultationBO?.Name)

            Log.e(TAG, "\nonCreate consultationBO: $consultationBO" )
            Log.e(TAG, "\nonCreate: getSlotsBO $getSlotsBO")
            Log.e(TAG, "onCreate: stDate $stDate")
            Log.e(TAG, "\n\n\nonViewCreated: PatientBO$getPatientListBO")

            //When user is comming from selected patient list....GetPatientListFragment
            getPatientListBO = arguments.getParcelable("patient_bo")
            //When user is comming from selected patient list....VaccinationFragment
            vaccinationPatientBO = arguments.getParcelable("vaccination_patient_bo")

            when {
                getPatientListBO != null -> {
                    getPatientListBO?.let { setSelectedDataForPatient(it) }
                }
                vaccinationPatientBO != null -> {
                    vaccinationPatientBO?.let { setSelectedDataForVaccinePatient(it) }
                }
                else -> {
                    //Toast.makeText(mContext, "new", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "onViewCreated: new", )
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initViews(view: View) {
        (context as DashBoardActivity).toolbar?.title =
            requireActivity().resources.getString(R.string.book_appointment)

        /*//toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = resources.getString(R.string.book_appointment)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
*/
        //Edittext binding...
        etBookingFor = view.findViewById(R.id.et_bookingfor)
        etMobileNo = view.findViewById(R.id.et_mobile_no)
        etFirstName = view.findViewById(R.id.et_first_name)
        etLastName = view.findViewById(R.id.et_last_name)
        etAge = view.findViewById(R.id.et_age)
        etType = view.findViewById(R.id.et_type)
        etRemark = view.findViewById(R.id.et_remark)

        //RadioGroup, radio button binding..
        rgGender = view.findViewById(R.id.radio_gender)
        rgMale = view.findViewById(R.id.rb_male)
        rgFemale = view.findViewById(R.id.rb_female)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //AppCompatButton binding..
        btnSubmit = view.findViewById(R.id.btn_submit)
        btnGet = view.findViewById(R.id.btn_get)

        //Recycler view binding..
        rvGetPatient = view.findViewById(R.id.rv_get_patient)
        rvGetPatient?.layoutManager = LinearLayoutManager(mContext)

        //Click Listeners...
        btnSubmit?.setOnClickListener(this)
        btnGet?.setOnClickListener(this)
        etType?.setOnClickListener(this)

        rgGender?.setOnCheckedChangeListener { group, checkedId ->
            stGender = (rgGender?.checkedRadioButtonId
                ?.let { view.findViewById<View>(it) } as RadioButton).text.toString()

            // Toast.makeText(mContext, "" + stGender, Toast.LENGTH_SHORT).show()
        }

        typeArrayList.clear()
        typeArrayList.add("Sample collection")
        typeArrayList.add("Consultation")
        typeArrayList.add("Teleconsultation")
        typeArrayList.add("Vaccination")
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_submit -> {
                if (isValidated()) {
                    if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                        bookAppointment()
                    } else {
                        showDialog(AllKeys.NO_INTERNET_AVAILABLE)
                    }
                }
            }

            R.id.btn_get -> {
                if (isValidatedMobile()) {
                    if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
                        getPatientByMobileNo()
                    } else {
                        showDialog(AllKeys.NO_INTERNET_AVAILABLE)
                    }

                }
            }

            R.id.et_type -> {
                val spinnerDialogDistrict = SpinnerDialog(requireActivity(),
                    typeArrayList,
                    "Select Status",
                    R.style.DialogAnimations_SmileWindow,
                    "Close") // With 	Animation

                spinnerDialogDistrict.bindOnSpinerListener { item: String, position: Int ->
                    etType?.setText(item)
                    //stDistrict = item
                }
                spinnerDialogDistrict.showSpinerDialog()
            }
        }
    }

    private fun bookAppointment() {
        val params: MutableMap<String, String> = HashMap()
        params["PatientName"] = etFirstName?.text.toString() + " " + etLastName?.text.toString()
        params["Age"] = etAge?.text.toString()
        params["Gender"] = stGender.toString()
        params["MobileNo"] = etMobileNo?.text.toString()
        params["Reason"] = etRemark?.text.toString()
        params["Status"] = "Pending"
        //params["Pno"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.HSC_ID) }.toString()
        params["Pno"] = consultationBO?.Pno.toString()
        params["IdOfSelectedslot"] = "0"
        params["AppointmentDatetime"] = stDate + " " + getSlotsBO?.Slot
        params["timeslot"] = getSlotsBO?.Slot.toString()
        params["Lab_code"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME).toString() }!!
        if (stAppointmentType.equals(context?.getString(R.string.diagnostics), true)) {
            params["AppointmentFlag"] = "Phlebo"
        } else if (stAppointmentType.equals(context?.getString(R.string.consultation), true)) {
            params["AppointmentFlag"] = "Consultant"
        }
        params["NewRescheduleFlag"] = "New"
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["AppointmentType"] = etType?.text.toString()
        if (getPatientListBO != null) {
            params["PepatId"] = getPatientListBO!!.PePatID
        } else if (vaccinationPatientBO != null) {
            params["PepatId"] = vaccinationPatientBO!!.pepatid
        } else {
            params["PepatId"] = ""
        }

        Log.e(TAG, "bookAppointment:\n $params ")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)
        digiPath?.getApiRequestHelper()
            ?.patientBookAppointment(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {

                        //Calling Intent...
                        val stAmount="100"
                        val amount= (stAmount.toFloat() * 100).roundToLong()
                        //Initialise RazorPay Checkout..
                        val checkOut= Checkout()
                        //SetKey ID
                        checkOut.setKeyID(AllKeys.MERCHANT_ID_RAZORPAY)
                        //Intialise Json Object..
                        try {
                            val jsonObject = JSONObject()
                            jsonObject.put("name",
                                etFirstName?.text.toString() + " " + etLastName?.text.toString())
                            jsonObject.put("description", etRemark?.text.toString())
                            jsonObject.put("theme.color", "#0093DD")
                            jsonObject.put("currency", "INR")
                            jsonObject.put("amount", amount)
                            jsonObject.put("image", AllKeys.LOGO_URL)
                            jsonObject.put("prefill.contact", etMobileNo?.text.toString())
                            jsonObject.put("prefill.email", CommonMethods.getPrefrence(mContext!!, AllKeys.EMAIL))

                            //OpenRazorPay Checkout Activity..
                            checkOut.open(context as Activity?,jsonObject)
                        }catch (e:JSONException){
                            e.printStackTrace()
                        }



                        /*val intent = Intent(mContext, DashBoardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)*/

                        Toast.makeText(mContext, "" + commonResponse.Message, Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        showDialog(commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    //Get all patient data...
    private fun getPatientByMobileNo() {
        val params: MutableMap<String, String> = HashMap()
        params["MobileNo"] = etMobileNo?.text.toString()

        Log.e(TAG, "getPatientByMobileNo: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getPatientByMobileNo(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getPatientResponse = `object` as GetPatientResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getPatientResponse")
                    if (getPatientResponse.ResponseCode == 200) {
                        //Calling Intent...
                        // Toast.makeText(mContext, "" + getPatientResponse.Message, Toast.LENGTH_SHORT).show()
                        val getPatientAdapter = mContext?.let {
                            GetPatientAdapter(it,
                                getPatientResponse.ResultArray,
                                this@BookAppointmentFragment)
                        }
                        rvGetPatient?.adapter = getPatientAdapter
                        rvGetPatient?.visibility = View.VISIBLE
                        tvNoDataFound?.visibility = View.GONE
                    } else {
                        rvGetPatient?.visibility = View.GONE
                        tvNoDataFound?.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    fun setSelectedData(getPatientBO: GetPatientBO) {
        try {
            etAge?.setText(getPatientBO.Age)
            etRemark?.setText(getPatientBO.Reason)
            if (getPatientBO.Gender.equals("male", true)) {
                rgMale?.isChecked = true
            } else {
                rgFemale?.isChecked = true
            }
            etFirstName?.setText(getPatientBO.PatientName.split(" ")[0])
            etLastName?.setText(getPatientBO.PatientName.split(" ")[1])
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSelectedDataForPatient(getPatientListBO: GetPatientListBO) {
        etAge?.setText(getPatientListBO.age)
        //etRemark?.setText(getPatientBO.Reason)
        if (getPatientListBO.sex.equals("male", true)) {
            rgMale?.isChecked = true
        } else {
            rgFemale?.isChecked = true
        }
        etMobileNo?.setText(getPatientListBO.PatientPhoneNo)
        etFirstName?.setText(getPatientListBO.PatientName.split(" ")[0])
        etLastName?.setText(getPatientListBO.PatientName.split(" ")[1])
    }

    private fun setSelectedDataForVaccinePatient(vaccinationPatientBO: VaccinationPatientBO) {
        etAge?.setText(vaccinationPatientBO.Age)
        //etRemark?.setText(getPatientBO.Reason)
        if (vaccinationPatientBO.sex.equals("male", true)) {
            rgMale?.isChecked = true
        } else {
            rgFemale?.isChecked = true
        }
        etMobileNo?.setText(vaccinationPatientBO.Mob_no)
        etFirstName?.setText(vaccinationPatientBO.FirstName)
        etLastName?.setText(vaccinationPatientBO.LastName)
        etType?.setText("Vaccination")
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

    private fun isValidated(): Boolean {
        if (etMobileNo?.text.toString().isEmpty()) {
            etMobileNo?.error = "Mobile No. required!"
            etMobileNo?.requestFocus()
            return false
        }

        if (etMobileNo?.text?.length != 10) {
            etMobileNo?.error = "Invalid mobile number!"
            etMobileNo?.requestFocus()
            return false
        }

        if (etFirstName?.text.toString().isEmpty()) {
            etFirstName?.error = "First Name required!"
            etFirstName?.requestFocus()
            return false
        }

        if (etLastName?.text.toString().isEmpty()) {
            etLastName?.error = "Last Name required!"
            etLastName?.requestFocus()
            return false
        }

        if (etAge?.text.toString().isEmpty()) {
            etAge?.error = "Age required!"
            etAge?.requestFocus()
            return false
        }

        if (stGender == null) {
            showDialog("Please select gender!")
            return false
        }

        if (etRemark?.text.toString().isEmpty()) {
            etRemark?.error = "Remark/Reason required!"
            etRemark?.requestFocus()
            return false
        }

        if (etType?.text.toString().isEmpty()) {
            etType?.error = "Type required!"
            etType?.requestFocus()
            return false
        }

        return true
    }

    /* override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
             android.R.id.home -> {
                 finish()
             }
         }
         return super.onOptionsItemSelected(item)
     }*/

    private fun isValidatedMobile(): Boolean {

        if (etMobileNo?.text.toString().isEmpty()) {
            etMobileNo?.error = "Mobile No. required!"
            etMobileNo?.requestFocus()
            return false
        }

        if (etMobileNo?.text?.length != 10) {
            etMobileNo?.error = "Invalid mobile number!"
            etMobileNo?.requestFocus()
            return false
        }

        return true
    }

    override fun onPaymentSuccess(p0: String?) {
        showDialogForSuccess(mContext!!,"Payment Successful\n\nTransaction PaymentID\n $p0")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        CommonMethods.showDialogForError(mContext!!,p1.toString())
    }

    fun showDialogForSuccess(context: Context, message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                 context.finish()
                (context as DashBoardActivity).navController.popBackStack(R.id.checkSlotFragment2, true)

            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    /*override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                // handle back button's click listener
                (context as DashBoardActivity).navController.popBackStack()
                return@OnKeyListener true
            }
            false
        })
    }*/
}