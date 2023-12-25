package com.bms.pathogold_bms.activity

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.GetPatientAdapterForActivity
import com.bms.pathogold_bms.adapter.SelectedTestAdapter
import com.bms.pathogold_bms.fragment.TestCodeDialog
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.PayuSuccessResponse
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getpatient.GetPatientBO
import com.bms.pathogold_bms.model.getpatient.GetPatientResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getslots.GetSlotsBO
import com.bms.pathogold_bms.model.otp.OtpResponse
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeResponse
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.HashGenerationUtils
import com.google.gson.Gson
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToLong

class BookAppointmentActivity : BaseActivity(), View.OnClickListener, PaymentResultWithDataListener,
    SelectedTestAdapter.DeleteSelectedTest {
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
    private var etOtp: EditText? = null

    //Radio Group declaration...
    private var rgGender: RadioGroup? = null
    private var rgMale: RadioButton? = null
    private var rgFemale: RadioButton? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvSelectedTestText: TextView? = null
    private var tvSelectPatient: TextView? = null
    private var tvTotalTestPrice: TextView? = null
    private var tvNoTestSelected: TextView? = null

    //RecyclerView declaration....
    private var rvGetPatient: RecyclerView? = null
    private var rvSelectedTest: RecyclerView? = null

    //AppCompatButton declaration...
    private var btnSubmit: AppCompatButton? = null
    private var btnGet: AppCompatButton? = null
    private var btnSelectTest: AppCompatButton? = null

    //LinearLayout declaration....
    private var llSelectedTestView: LinearLayout? = null
    private var llSubSelectedTestView: LinearLayout? = null
    private var llOtpView: LinearLayout? = null

    private var stGender: String? = null
    private val TAG = "BookAppointmentActivity"
    private var getSlotsBO: GetSlotsBO? = null
    private var consultationBO: ConsultationBO? = null
    private var stDate: String? = null
    private var stAppointmentType: String? = null
    private var getPatientListBO: GetPatientListBO? = null
    private var vaccinationPatientBO: VaccinationPatientBO? = null

    //ArrayList declarations..
    private val typeArrayList = ArrayList<String>()
    private val getPatientTestListBOArrayList = ArrayList<GetTestCodeBO>()
    private val selectedTestArrayList = ArrayList<GetTestCodeBO>()
    private val getTestId = ArrayList<String>()
    private val getTestName = ArrayList<String>()
    private var getPatientList = ArrayList<GetPatientBO>()

    private var testTotalRate = 0
    private var appointmentID: String? = null
    private var stLoginType: String = ""
    private var getLabDetailsBO: GetLabDetailsBO? = null
    private var stOtp: String = ""

    //Adapter declaration..
    private var selectedTestAdapter: SelectedTestAdapter? = null

    //Declare timer
    private var cTimer: CountDownTimer? = null

    override val activityLayout: Int
        get() = R.layout.fragment_book_appointment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Basic intialisation....
        initViews()

        try {
            getSlotsBO = intent.getParcelableExtra("slot_bo")
            consultationBO = intent.getParcelableExtra("consultation_bo")
            stDate = intent.getStringExtra("date")
            stAppointmentType = intent.getStringExtra("appointment_flag_type")

            stLoginType = intent.getStringExtra("login_type").toString()

            getLabDetailsBO = intent.getParcelableExtra("lab_details_bo")

            if (stLoginType == getString(R.string.book_app)) {
                btnGet?.text = getString(R.string.get_otp)
                llOtpView?.visibility = View.VISIBLE
            } else {
                llOtpView?.visibility = View.GONE
            }

            toolbar?.title = "Slot Time:- " + getSlotsBO?.Slot

            Log.e(TAG, "onCreate: stAppointmentType$stAppointmentType")

            //set Text if phelbo login....
            etBookingFor?.setText(consultationBO?.Name)

            Log.e(TAG, "\nonCreate consultationBO: $consultationBO")
            Log.e(TAG, "\nonCreate: getSlotsBO $getSlotsBO")
            Log.e(TAG, "onCreate: stDate $stDate")
            Log.e(TAG, "\n\n\nonViewCreated: PatientBO$getPatientListBO")

            //When user is comming from selected patient list....GetPatientListFragment
            getPatientListBO = intent.getParcelableExtra("patient_bo")
            //When user is comming from selected patient list....VaccinationFragment
            vaccinationPatientBO = intent.getParcelableExtra("vaccination_patient_bo")

            when {
                getPatientListBO != null -> {
                    getPatientListBO?.let { setSelectedDataForPatient(it) }
                }
                vaccinationPatientBO != null -> {
                    vaccinationPatientBO?.let { setSelectedDataForVaccinePatient(it) }
                }
                else -> {
                    //Toast.makeText(mContext, "new", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "onViewCreated: new")
                }
            }

            //handle Visiblity of selected test view
            handleTestViewVisibility()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    private fun initViews() {
        //(context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.book_appointment)

        //toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white),
            PorterDuff.Mode.SRC_ATOP)

        //Edittext binding...
        etBookingFor = findViewById(R.id.et_bookingfor)
        etMobileNo = findViewById(R.id.et_mobile_no)
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etAge = findViewById(R.id.et_age)
        etType = findViewById(R.id.et_type)
        etRemark = findViewById(R.id.et_remark)
        etOtp = findViewById(R.id.et_otp)

        //RadioGroup, radio button binding..
        rgGender = findViewById(R.id.radio_gender)
        rgMale = findViewById(R.id.rb_male)
        rgFemale = findViewById(R.id.rb_female)

        //Textview binding..
        tvNoDataFound = findViewById(R.id.tv_no_data_found)
        tvSelectPatient = findViewById(R.id.tv_select_patient)
        tvSelectedTestText = findViewById(R.id.tv_selected_test_text)
        tvTotalTestPrice = findViewById(R.id.tv_total_test_price)
        tvNoTestSelected = findViewById(R.id.tv_no_test_selected)

        //AppCompatButton binding..
        btnSubmit = findViewById(R.id.btn_submit)
        btnGet = findViewById(R.id.btn_get)
        btnSelectTest = findViewById(R.id.btn_select_test)

        //Recycler view binding..
        rvGetPatient = findViewById(R.id.rv_get_patient)
        rvGetPatient?.layoutManager = LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false)

        rvSelectedTest = findViewById(R.id.rv_selected_test)
        rvSelectedTest?.layoutManager = LinearLayoutManager(mContext)

        //Linearlayout view binding...
        llSelectedTestView = findViewById(R.id.ll_selected_test_view)
        llSubSelectedTestView = findViewById(R.id.ll_selected_test_view_2)
        llOtpView = findViewById(R.id.ll_otp_view)

        //Click Listeners...
        btnSubmit?.setOnClickListener(this)
        btnGet?.setOnClickListener(this)
        etType?.setOnClickListener(this)
        btnSelectTest?.setOnClickListener(this)

        rgGender?.setOnCheckedChangeListener { group, checkedId ->
            stGender = (rgGender?.checkedRadioButtonId
                ?.let { findViewById<View>(it) } as RadioButton).text.toString()

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
                        if (getPatientList.size == 0) {
                            getPatientByMobileNo()
                        }

                        if (stLoginType == getString(R.string.book_app)) {
                            sendOtp()
                        }
                    } else {
                        showDialog(AllKeys.NO_INTERNET_AVAILABLE)
                    }
                }
            }

            R.id.et_type -> {
                val spinnerDialogDistrict = SpinnerDialog(this,
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

            R.id.btn_select_test -> {
                val ft1 = supportFragmentManager.beginTransaction()
                val testCodeDialog = TestCodeDialog()
                val bundle = Bundle()
                bundle.putString("open_via", "activity")
                bundle.putParcelableArrayList("testcode_list", getPatientTestListBOArrayList)
                testCodeDialog.arguments = bundle
                testCodeDialog.show(ft1, "Tag")
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

        params["Lab_code"] = if (stLoginType.equals(getString(R.string.book_app))) {
            getLabDetailsBO?.labcode.toString()
        } else {
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME).toString() }!!
        }

        if (stAppointmentType.equals(applicationContext.getString(R.string.diagnostics), true)) {
            params["AppointmentFlag"] = "Phlebo"
        } else if (stAppointmentType.equals(applicationContext.getString(R.string.consultation),
                true)
        ) {
            params["AppointmentFlag"] = "Consultant"
        }
        params["NewRescheduleFlag"] = "New"
        params["Companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["AppointmentType"] = etType?.text.toString()
        params["testname"] = getTestId.toString().replace("[", "").replace("]", "")
        when {
            getPatientListBO != null -> {
                params["PepatId"] = getPatientListBO!!.PePatID
            }
            vaccinationPatientBO != null -> {
                params["PepatId"] = vaccinationPatientBO!!.pepatid
            }
            else -> {
                params["PepatId"] = ""
            }
        }

        Log.e(TAG, "bookAppointment:\n $params ")

        val progress = ProgressDialog(this@BookAppointmentActivity)
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
                        appointmentID = commonResponse.ResultArray[0].appointmentid

                        if (CommonMethods.getPrefrence(mContext!!, AllKeys.MERCHANT_ID_RAZORPAY)
                                .equals(AllKeys.DNF) ||
                            CommonMethods.getPrefrence(mContext!!, AllKeys.PAYU_MERCHANT_KEY)
                                .equals(AllKeys.DNF)
                        ) {
                            showDialogForError(mContext!!, "Payment Gateway Not Available!")
                        } else {
                            //Select options...
                            showPictureDialog()
                        }

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

    private fun showPictureDialog() {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(this@BookAppointmentActivity)
        pictureDialog.setTitle("Select Payment Gateway")
        val pictureDialogItems =
            arrayOf("RazorPay", "PayU Money"/*, "Preview"*/)
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> if (CommonMethods.getPrefrence(mContext!!, AllKeys.MERCHANT_ID_RAZORPAY).equals(AllKeys.DNF)) {
                    showDialogForError(mContext!!, "Payment Gateway Not Available!")
                }else{
                    openRazorPay()
                }

                1 ->if (CommonMethods.getPrefrence(mContext!!, AllKeys.PAYU_MERCHANT_KEY).equals(AllKeys.DNF)
                    ||  CommonMethods.getPrefrence(mContext!!, AllKeys.PAYU_MERCHANT_SALT).equals(AllKeys.DNF)) {
                    showDialogForError(mContext!!, "Payment Gateway Not Available!")
                }else{
                    openPayU()
                }

            }
        }
        pictureDialog.show()
    }

    private fun openPayU() {
        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount(testTotalRate.toString())
            .setIsProduction(true)
            .setKey(CommonMethods.getPrefrence(mContext!!, AllKeys.PAYU_MERCHANT_KEY))
            .setProductInfo(etRemark?.text.toString())
            .setPhone(etMobileNo?.text.toString())
            .setTransactionId(System.currentTimeMillis().toString())
            .setFirstName(etFirstName?.text.toString() + " " + etLastName?.text.toString())
            .setEmail(CommonMethods.getPrefrence(mContext!!, AllKeys.EMAIL))
            .setSurl(AllKeys.surl)
            .setFurl(AllKeys.furl)
            //.setUserCredential("")
            //.setAdditionalParams(<HashMap<String,Any?>>) //Optional, can contain any additional PG params
            .build()

        PayUCheckoutPro.open(
            this, payUPaymentParams,
            object : PayUCheckoutProListener {

                override fun onPaymentSuccess(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]

                    Log.e(TAG, "onPaymentSuccess: " + payUResponse)
                    Log.e(TAG, "onPaymentSuccess: " + merchantResponse)

                    val gson = Gson()
                    val fooFromJson: PayuSuccessResponse = gson.fromJson(payUResponse.toString(), PayuSuccessResponse::class.java)

                    if (CommonMethods.isNetworkAvailable(mContext!!)) {
                        insertUpdatedAdvance(fooFromJson.txnid,AllKeys.PAYU)
                    } else {
                        Toast.makeText(mContext!!,
                            AllKeys.NO_INTERNET_AVAILABLE,
                            Toast.LENGTH_SHORT).show()
                    }
                    showDialogForSuccess(mContext!!, "PaymentID\n")
                }

                override fun onPaymentFailure(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]

                    Log.e(TAG, "onPaymentFailure: " + payUResponse)
                    Log.e(TAG, "onPaymentFailure: " + merchantResponse)
                    CommonMethods.showDialogForError(mContext!!, "Payment Failed")
                }


                override fun onPaymentCancel(isTxnInitiated: Boolean) {
                    Log.e(TAG, "onPaymentCancel: $isTxnInitiated")

                }


                override fun onError(errorResponse: ErrorResponse) {
                    val errorMessage: String
                    if (errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                        errorMessage = errorResponse.errorMessage!!
                    else
                        errorMessage = AllKeys
                            .SERVER_MESSAGE
                    Log.e(TAG, "onError: " + errorMessage.toString())
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    //For setting webview properties, if any. Check Customized Integration section for more details on this
                }

                override fun generateHash(
                    valueMap: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener,
                ) {
                    if (valueMap.containsKey(PayUCheckoutProConstants.CP_HASH_STRING) && valueMap.containsKey(
                            PayUCheckoutProConstants.CP_HASH_NAME)
                    ) {

                        val hashData = valueMap[PayUCheckoutProConstants.CP_HASH_STRING]
                        val hashName = valueMap[PayUCheckoutProConstants.CP_HASH_NAME]

                        //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                        val hash: String? =
                            HashGenerationUtils.generateHashFromSDK(hashData.toString(),
                                CommonMethods.getPrefrence(mContext!!, AllKeys.PAYU_MERCHANT_SALT),
                                "")
                        if (!TextUtils.isEmpty(hash)) {
                            val dataMap: HashMap<String, String?> = HashMap()
                            dataMap[hashName!!] = hash!!
                            hashGenerationListener.onHashGenerated(dataMap)
                        }
                    }
                }
            })
    }

    private fun openRazorPay() {
        Checkout.preload(applicationContext)
        //Calling Intent...
        val stAmount = testTotalRate
        val amount = (stAmount.toFloat() * 100).roundToLong()
        //Initialise RazorPay Checkout..
        val checkOut = Checkout()
        //SetKey ID
        // checkOut.setKeyID(AllKeys.MERCHANT_ID_RAZORPAY) //For testing purpose you can uncomment this...
        checkOut.setKeyID(CommonMethods.getPrefrence(mContext!!, AllKeys.MERCHANT_ID_RAZORPAY))// live merchant id...

        Log.e(TAG,
            "onSuccess: " + CommonMethods.getPrefrence(mContext!!, AllKeys.MERCHANT_ID_RAZORPAY))

        //Intialise Json Object..
        try {
            val jsonObject = JSONObject()
            jsonObject.put("name", etFirstName?.text.toString() + " " + etLastName?.text.toString())
            jsonObject.put("description", etRemark?.text.toString())
            jsonObject.put("theme.color", "#001833")
            jsonObject.put("currency", "INR")
            jsonObject.put("amount", amount)
            jsonObject.put("image", CommonMethods.getPrefrence(mContext!!, AllKeys.LOGO_PATH))
            jsonObject.put("prefill.contact", etMobileNo?.text.toString())
            jsonObject.put("prefill.email", CommonMethods.getPrefrence(mContext!!, AllKeys.EMAIL))

            //OpenRazorPay Checkout Activity..
            checkOut.open(this@BookAppointmentActivity, jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    //Get all patient data...
    private fun getPatientByMobileNo() {
        val params: MutableMap<String, String> = HashMap()
        params["MobileNo"] = etMobileNo?.text.toString()

        Log.e(TAG, "getPatientByMobileNo: $params")

        val progress = ProgressDialog(this@BookAppointmentActivity)
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
                        getPatientList = getPatientResponse.ResultArray
                        //Calling Intent...
                        // Toast.makeText(mContext, "" + getPatientResponse.Message, Toast.LENGTH_SHORT).show()
                        val getPatientAdapter = mContext?.let {
                            GetPatientAdapterForActivity(it,
                                getPatientList,
                                this@BookAppointmentActivity)
                        }
                        rvGetPatient?.adapter = getPatientAdapter
                        rvGetPatient?.visibility = View.VISIBLE
                        tvSelectPatient?.visibility = View.VISIBLE
                        tvNoDataFound?.visibility = View.GONE
                    } else {
                        rvGetPatient?.visibility = View.GONE
                        tvSelectPatient?.visibility = View.GONE
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

    private fun getTestList() {
        val params: MutableMap<String, String> = java.util.HashMap()
        if (stLoginType.equals(getString(R.string.book_app))) {
            params["LabCode"] = getLabDetailsBO?.labcode.toString()
        } else {
            params["LabCode"] =
                mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        }

        val progress = ProgressDialog(this@BookAppointmentActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.getTestCode(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getTestCodeResponse = `object` as GetTestCodeResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getTestCodeResponse")
                    if (getTestCodeResponse.ResponseCode == 200) {
                        if (getTestCodeResponse.ResultArray.size > 0) {
                            getPatientTestListBOArrayList.addAll(getTestCodeResponse.ResultArray)
                        }
                    } else {
                        Toast.makeText(mContext, getTestCodeResponse.Message, Toast.LENGTH_SHORT)
                            .show()
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
    fun onTestCodeSelect(getTestArrayList: ArrayList<GetTestCodeBO>) {
        selectedTestArrayList.clear()
        selectedTestArrayList.addAll(getTestArrayList)
        getTestId.clear()
        getTestName.clear()

        testTotalRate = 0
        for (temp in selectedTestArrayList) {
            getTestId.add(temp.tlcode)
            getTestName.add(temp.title)

            //Total of all selected test rate..
            testTotalRate += temp.rate.toInt()
        }

        tvTotalTestPrice?.text = "Total: $testTotalRate /-"
        Log.e(TAG, "onTestCodeSelect: $getTestId")

        llSubSelectedTestView?.visibility = View.VISIBLE
        tvNoTestSelected?.visibility = View.GONE
        selectedTestAdapter =
            SelectedTestAdapter(mContext!!, selectedTestArrayList, this, View.VISIBLE)
        rvSelectedTest?.adapter = selectedTestAdapter
    }

    //On Delete of selected test..
    @SuppressLint("SetTextI18n")
    override fun onDeleteTest(position: Int) {
        selectedTestArrayList.removeAt(position)
        selectedTestAdapter?.notifyItemRemoved(position)
        selectedTestAdapter?.notifyItemRangeChanged(position, selectedTestArrayList.size)
        try {
            var sum = 0
            for (i in 0 until selectedTestArrayList.size) sum += selectedTestArrayList[i].rate.toInt()
            testTotalRate = sum
            tvTotalTestPrice?.text = "Total: $testTotalRate/-"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (selectedTestArrayList.size == 0) {
            llSubSelectedTestView?.visibility = View.GONE
            tvNoTestSelected?.visibility = View.VISIBLE
        }
    }

    fun setSelectedData(getPatientBO: GetPatientBO) {
        try {
            rvGetPatient?.visibility = View.GONE
            etAge?.setText(getPatientBO.Age)
            etRemark?.setText(getPatientBO.Reason)
            if (getPatientBO.Gender.equals("male", true)) {
                rgMale?.isChecked = true
            } else {
                rgFemale?.isChecked = true
            }
            etFirstName?.setText(getPatientBO.PatientName.split(" ")[0])
            etLastName?.setText(getPatientBO.PatientName.split(" ")[1])
            tvSelectPatient?.visibility = View.GONE
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
        val mDialog: MaterialDialog = MaterialDialog.Builder(this)
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

        if (stAppointmentType.equals(applicationContext.getString(R.string.diagnostics), true)) {
            if (selectedTestArrayList.size == 0) {
                CommonMethods.showDialogForError(mContext!!,
                    applicationContext.getString(R.string.please_select_test))
                return false
            }
        }

        if (stLoginType == getString(R.string.book_app)) {
            if (etOtp?.text.toString().isEmpty()) {
                etOtp?.error = "Otp Required!"
                etOtp?.requestFocus()
                return false
            }

            if (etOtp?.text.toString().length != 4) {
                etOtp?.error = "Invalid Otp!"
                etOtp?.requestFocus()
                return false
            }

            if (etOtp?.text.toString() != stOtp) {
                etOtp?.error = "Incorrect Otp Enter!"
                etOtp?.requestFocus()
                return false
            }
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

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            insertUpdatedAdvance(p0.toString(), AllKeys.PAYU)
        } else {
            Toast.makeText(mContext!!, AllKeys.NO_INTERNET_AVAILABLE, Toast.LENGTH_SHORT).show()
        }
        showDialogForSuccess(mContext!!, "PaymentID\n $p0")
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            val obj = JSONObject(p1!!)
            val nestedObj = obj.getJSONObject("error")
            Log.e(TAG, "onPaymentError: $obj")
            Log.e("description value ", nestedObj.getString("description"))

            CommonMethods.showDialogForError(mContext!!, nestedObj.getString("description"))

        } catch (tx: Throwable) {
            Log.e("My App", "Could not parse malformed JSON: \"" + p1.toString() + "\"")
        }
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
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertUpdatedAdvance(txnid: String, paymentGateway: String) {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["amtpaid"] = testTotalRate.toString()
        params["appointmentid"] = appointmentID.toString()
        params["modeofpay"]=paymentGateway+"_"+txnid

        Log.e(TAG, "insertUpdatedAdvance: $params")

        val progress = ProgressDialog(this@BookAppointmentActivity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertUpdatedAdvance(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "insertVaccinationPatient: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        //showDialogForSuccess(mContext!!, "Payment Successful. \n$transactionID")
                        Toast.makeText(mContext!!, commonResponse.Message, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "insertVaccinationPatient: $apiResponse")
                }
            })
    }

    private fun handleTestViewVisibility() {
        if (stAppointmentType.equals(applicationContext.getString(R.string.diagnostics))) {
            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                getTestList()
            } else {
                Toast.makeText(mContext!!, AllKeys.NO_INTERNET_AVAILABLE, Toast.LENGTH_SHORT).show()
            }

            llSelectedTestView?.visibility = View.VISIBLE
            llSubSelectedTestView?.visibility = View.GONE
            tvNoTestSelected?.visibility = View.VISIBLE
        } else {
            llSelectedTestView?.visibility = View.GONE
        }
    }

    private fun sendOtp() {
        val params: MutableMap<String, String> = HashMap()
        params["mobile"] = etMobileNo?.text.toString()

        Log.e(TAG, "getDailyCashSummary: $params")
        val progress = ProgressDialog(this@BookAppointmentActivity)
        progress.setMessage("Sending Otp...Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.verifyPatientByOtp(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val otpResponse = `object` as OtpResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $otpResponse")
                    if (otpResponse.ResponseCode == 200) {
                        if (otpResponse.ResultArray.size > 0) {
                            stOtp = otpResponse.ResultArray[0].otp

                            //start timer function
                            cTimer = object : CountDownTimer(30000, 1000) {
                                @SuppressLint("SetTextI18n")
                                override fun onTick(millisUntilFinished: Long) {
                                    btnGet?.isEnabled = false
                                    btnGet?.text =
                                        getString(R.string.resend_otp) + "\n" + TimeUnit.MILLISECONDS.toSeconds(
                                            millisUntilFinished) + " sec"
                                }

                                override fun onFinish() {
                                    btnGet?.isEnabled = true
                                    btnGet?.text = getString(R.string.resend_otp)
                                }
                            }
                            cTimer!!.start()
                        } else {
                            CommonMethods.showDialogForError(mContext!!, otpResponse.Message)
                        }
                    } else {
                        CommonMethods.showDialogForError(mContext!!, otpResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cTimer != null)
            cTimer!!.cancel()
    }

    fun showDialogForError(context: Context, message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            //.setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok") { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                finish()
                dialogInterface.dismiss()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }
}