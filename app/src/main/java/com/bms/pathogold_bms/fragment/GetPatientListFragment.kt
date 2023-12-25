package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.CheckOutActivity
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.activity.VideoConferenceActivity2
import com.bms.pathogold_bms.adapter.AllPatientListAdapter
import com.bms.pathogold_bms.adapter.CallToPatientAdapter
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListResponse
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.ConfigUrl
import com.bms.pathogold_bms.view_models.GetAllPatientViewModel
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class GetPatientListFragment : BaseFragment(), View.OnClickListener,AllPatientListAdapter.OpenPaymentGateway,CallToPatientAdapter.VideoCallPatient{

    //Declaration....
    private val TAG = "GetPatientListActivity"

    //TextInputLayout declaration..
    //private var tilDate: TextInputLayout?=null

    //TextInputEditText declaration..
    private var etDate: EditText? = null

    //Edittext declaration..
    private var etSearch: EditText? = null
    private var etMobileNo: EditText? = null
    private var etFromDate: EditText? = null
    private var etToDate: EditText? = null
    private var etSelectLab: EditText? = null

    //Recyclerview declaration..
    private var rvGetPatientList: RecyclerView? = null

    //LinearLayout declaration
    private var llNoPatientFound: LinearLayout? = null
    private var llSelectLabView: LinearLayout? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvSearchByDate: TextView? = null
    private var tvSearchByMobile: TextView? = null
    private var tvSearchDateText: TextView? = null
    private var tvSearch: TextView? = null
    private var tvByDefaultText:TextView?=null

    //LinearLayout declaration
    private var llMobileNo: LinearLayout? = null
    private var llGetPatientList: LinearLayout? = null
    private var llDateSearch: LinearLayout? = null
    private var llMobileSearch: LinearLayout? = null
    private var llRadioOperation: LinearLayout? = null

    //AppCompatButton declaration...
    private var btnGet: AppCompatButton? = null

    //CardView declaration...
    private var cvSearchByDate: CardView? = null
    private var cvSearchByMobile: CardView? = null

    //Arraylist declaration..
    private val getPatientListBOArrayList = ArrayList<GetPatientListBO>()
    private var getLabNameArrayList = ArrayList<LabNameBO>()

    //Adapter declaration....
    private var allPatientListAdapter: AllPatientListAdapter? = null
    private var callToPatientAdapter: CallToPatientAdapter? = null

    //View model declaration..
    private lateinit var getAllPatientViewModel: GetAllPatientViewModel

    private val GET_LAB_NAME = 1

    //Business object declaration..
    private var labNameBO: LabNameBO? = null

    private var stSearchBy=context?.resources?.getString(R.string.search_by_date).toString()

    private var isLaunchType:String=AllKeys.IS_PATIENT_LIST_LAUNCH

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isLaunchType= arguments?.getString("launch_type").toString()

        Log.e(TAG, "onViewCreated: "+isLaunchType )

        //basic intialisation..
        initViews(view)

        getAllPatientViewModel = ViewModelProvider(requireActivity())[GetAllPatientViewModel::class.java] // init view model

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {

            val arguments = arguments
            if (BuildConfig.DEBUG && arguments == null) {
                error("Assertion failed")
            }

            llSelectLabView?.visibility = View.VISIBLE
            if (getArguments()?.getParcelableArrayList<LabNameBO>("get_lab_name") != null) {
                getLabNameArrayList.addAll(arguments?.getParcelableArrayList("get_lab_name")!!)

                if(getLabNameArrayList.size>0){
                    labNameBO = LabNameBO(getLabNameArrayList[0].labname, getLabNameArrayList[0].labname_c)

                    etSelectLab?.setText(labNameBO?.labname_c)
                }
            }
        } else {
            llSelectLabView?.visibility = View.GONE
        }

        if (mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE) }.toString().equals(AllKeys.Patient, false)) {
            llRadioOperation?.visibility = View.GONE
        } else {
            llRadioOperation?.visibility = View.VISIBLE
        }

        if (getAllPatientViewModel.list.value!!.size == 0) {
            loadDataOnTodaysDate()
        }

        // set up observer. Every time You add sth to list, ListView will be updated
        getAllPatientViewModel.list.observe(viewLifecycleOwner) {
            Log.e(TAG, "In view model observe....")
            Log.e(TAG, "In view model observe: " + getAllPatientViewModel.list.value)
            if (getAllPatientViewModel.list.value!!.size > 0) {
                llGetPatientList?.visibility = View.VISIBLE
                llNoPatientFound?.visibility = View.GONE
                if (isLaunchType == AllKeys.IS_VIDEO_CALL_LAUNCH) {
                    callToPatientAdapter = mContext?.let {
                        CallToPatientAdapter(it,
                            getAllPatientViewModel.list.value!!,
                            etDate?.text.toString(),
                            this@GetPatientListFragment)
                    }
                    rvGetPatientList?.adapter = callToPatientAdapter
                } else {
                    allPatientListAdapter = mContext?.let {
                        AllPatientListAdapter(it,
                            getAllPatientViewModel.list.value!!,
                            etDate?.text.toString(),
                            this@GetPatientListFragment)
                    }
                    rvGetPatientList?.adapter = allPatientListAdapter
                }
                rvGetPatientList?.adapter = allPatientListAdapter
            } else {
                llGetPatientList?.visibility = View.GONE
                llNoPatientFound?.visibility = View.VISIBLE
            }
        }
    }

    override val activityLayout: Int
        get() = R.layout.activity_get_patient_list

    private fun initViews(view: View) {
        (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.all_patient)
        /*//toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = resources.getString(R.string.patient_list)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        //toolbar.setTextAlignment(TEXT_ALIGNMENT_CENTER);*/

        //TextInputLayout binding...
        //tilDate=findViewById(R.id.til_date)

        //LinearLayout declaration
        llNoPatientFound = view.findViewById(R.id.ll_no_data_found)
        llSelectLabView = view.findViewById(R.id.ll_search_labname_view)

        //TextInputEditText binding..
        etDate = view.findViewById(R.id.et_date)
        etToDate = view.findViewById(R.id.et_to_date)
        etFromDate = view.findViewById(R.id.et_from_date)

        //Editext binding.
        etSearch = view.findViewById(R.id.et_search)
        etMobileNo = view.findViewById(R.id.et_mobile_no)
        etSelectLab = view.findViewById(R.id.et_select_lab)
        etSelectLab?.setOnClickListener(this)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)
        tvSearchByDate = view.findViewById(R.id.tv_search_by_date)
        tvSearchByMobile = view.findViewById(R.id.tv_search_by_mobile)
        tvSearchDateText = view.findViewById(R.id.tv_search_text)
        tvSearch = view.findViewById(R.id.tv_search)
        tvByDefaultText = view.findViewById(R.id.tv_by_default_text)

        //CardView binding...
        cvSearchByDate = view.findViewById(R.id.cv_search_by_date)
        cvSearchByMobile = view.findViewById(R.id.cv_search_by_mobile)

        //AppCompatButton binding..
        btnGet = view.findViewById(R.id.btn_get)

        //LinearLayout binding...
        llMobileNo = view.findViewById(R.id.ll_mobile)
        llGetPatientList = view.findViewById(R.id.ll_get_patient_list)
        llDateSearch = view.findViewById(R.id.ll_date_search)
        llMobileSearch = view.findViewById(R.id.ll_mobile_search)
        llRadioOperation = view.findViewById(R.id.ll_radio_operation)
        llSelectLabView = view.findViewById(R.id.ll_search_labname_view)

        //Recycler binding..
        rvGetPatientList = view.findViewById(R.id.rv_get_patient)
        if(isLaunchType == AllKeys.IS_VIDEO_CALL_LAUNCH){
            val gridLayoutManager = GridLayoutManager(mContext,2)
            rvGetPatientList?.layoutManager = gridLayoutManager
        }else{
            val linearLayoutManager = LinearLayoutManager(mContext)
            rvGetPatientList?.layoutManager = linearLayoutManager
        }

        //ClickListeners...
        btnGet?.setOnClickListener(this)
        cvSearchByMobile?.setOnClickListener(this)
        cvSearchByDate?.setOnClickListener(this)
        etDate?.setOnClickListener(this)
        etToDate?.setOnClickListener(this)
        etFromDate?.setOnClickListener(this)

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString().toLowerCase(Locale.getDefault()))
            }
        })
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.et_to_date -> {
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        //  etDate?.setText(String.format("%02d", (monthOfYear + 1)) + "/" + dayOfMonth.toString() + "/" + year)
                        etToDate?.setText("$dayOfMonth/" + String.format("%02d",
                            (monthOfYear + 1)) + "/" + year)

                        val sdf = SimpleDateFormat("dd/MM/yyyy")
                        val c = Calendar.getInstance()
                        c.time = sdf.parse(etToDate?.text.toString()) as Date // Using today's date
                        c.add(Calendar.DATE, -7) // minus 7 days
                        val output = sdf.format(c.time)
                        etFromDate?.setText(output)

                        //getPatientListApi...
                        hitPatientListApi()
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

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

                        val sdf = SimpleDateFormat("dd/MM/yyyy")
                        val c = Calendar.getInstance()
                        c.time = sdf.parse(etFromDate?.text.toString()) as Date // Using today's date
                        c.add(Calendar.DATE, 7) // minus 5 days
                        val output = sdf.format(c.time)
                        etToDate?.setText(output)

                        //getPatientListApi...
                        hitPatientListApi()
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.cv_search_by_date -> {
                handleVisiblity(mContext!!.resources.getColor(R.color.white),
                    mContext!!.resources.getColor(R.color.purple_700),
                    mContext!!.resources.getColor(R.color.purple_700),
                    mContext!!.resources.getColor(R.color.white),
                    mContext!!.resources.getString(R.string.select_date),
                    View.VISIBLE,
                    View.GONE,
                    View.VISIBLE)

                stSearchBy=context?.resources?.getString(R.string.search_by_date).toString()
            }

            R.id.cv_search_by_mobile -> {
                handleVisiblity(mContext!!.resources.getColor(R.color.purple_700),
                    mContext!!.resources.getColor(R.color.white),
                    mContext!!.resources.getColor(R.color.white),
                    mContext!!.resources.getColor(R.color.purple_700),
                    mContext!!.resources.getString(R.string.enter_mobile),
                    View.GONE,
                    View.VISIBLE,
                    View.GONE)

                stSearchBy=context?.resources?.getString(R.string.search_by_mobile).toString()
            }

            R.id.btn_get -> {
                if (isValidatedMobile()) {
                    if (CommonMethods.isNetworkAvailable(mContext!!)) {
                        getPatientList("", "",
                            etMobileNo?.text.toString(),
                            context?.resources?.getString(R.string.search_by_mobile).toString())
                    } else {
                        CommonMethods.showDialogForError(mContext as Activity,
                            AllKeys.NO_INTERNET_AVAILABLE)
                    }
                }
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
        var sdf = SimpleDateFormat("dd/MM/yyyy")
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
        etFromDate?.setText(strDate)

        //hit patientlist api..
        hitPatientListApi()
    }

    private fun getPatientList(
        stFromDate: String,
        stToDate: String,
        stMobileNo: String,
        stSearchBy: String,
    ) {
        val params: MutableMap<String, String> = HashMap()
        params["fromdate"] = stFromDate
        params["todate"] = stToDate
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin) && labNameBO!=null) {
            params["LabCode"] = labNameBO?.labname.toString()
        } else {
            params["LabCode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()
        }
        params["UserType"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE) }.toString()
        params["UserName"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USER_NAME) }.toString()
        when {
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE) }.toString()
                .equals(AllKeys.Patient, false) -> {
                params["PatientName"] = ""
                params["PatientMobileNo"] = stMobileNo
            }
            stSearchBy == context?.resources?.getString(R.string.search_by_mobile) -> {
                params["PatientName"] = ""
                params["PatientMobileNo"] = stMobileNo
            }
            else -> {
                params["PatientName"] = ""
                params["PatientMobileNo"] = ""
            }
        }
        if (mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE) }.toString().equals(AllKeys.Consultant, false)) {
            params["Pno"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.HSC_ID) }.toString()
        } else {
            params["Pno"] = ""
        }

        Log.e(TAG, "getPatientList: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getPatientList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getPatientListResponse = `object` as GetPatientListResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getPatientListResponse")
                    if (getPatientListResponse.ResponseCode == 200) {
                        if (getPatientListResponse.ResultArray.size > 0) {
                            getPatientListBOArrayList.clear()
                            getPatientListBOArrayList.addAll(getPatientListResponse.ResultArray)
                            llGetPatientList?.visibility = View.VISIBLE
                            llNoPatientFound?.visibility = View.GONE

                            //add data in view model...
                            getAllPatientViewModel.addNewValue(getPatientListBOArrayList)

                            if(isLaunchType == AllKeys.IS_VIDEO_CALL_LAUNCH){
                                callToPatientAdapter = mContext?.let { CallToPatientAdapter(it, getPatientListBOArrayList, etDate?.text.toString(), this@GetPatientListFragment) }
                                rvGetPatientList?.adapter = callToPatientAdapter
                            }else{
                                allPatientListAdapter = mContext?.let { AllPatientListAdapter(it, getPatientListBOArrayList, etDate?.text.toString(), this@GetPatientListFragment) }
                                rvGetPatientList?.adapter = allPatientListAdapter
                            }
                        } else {
                            llGetPatientList?.visibility = View.GONE
                            llNoPatientFound?.visibility = View.VISIBLE
                        }
                    } else {
                        llGetPatientList?.visibility = View.GONE
                        llNoPatientFound?.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    //Handle visiblility of radio buttons...
    private fun handleVisiblity(
        cvSearchByDateColor: Int,
        cvSearchByMobileColor: Int,
        tvSearchByDateColor: Int,
        tvSearchByMobileColor: Int,
        stSearchDateText: String,
        llDateVisibility: Int,
        llMobileSearchVisibility: Int,
        tvSearchVisibility: Int,
    ) {
        cvSearchByDate?.setCardBackgroundColor(cvSearchByDateColor)
        cvSearchByMobile?.setCardBackgroundColor(cvSearchByMobileColor)
        tvSearchByDate?.setTextColor(tvSearchByDateColor)
        tvSearchByMobile?.setTextColor(tvSearchByMobileColor)
        tvSearchDateText?.text = stSearchDateText
        llMobileSearch?.visibility = llMobileSearchVisibility
        llDateSearch?.visibility = llDateVisibility
        tvSearch?.visibility = tvSearchVisibility
        tvByDefaultText?.visibility = tvSearchVisibility

        llGetPatientList?.visibility = View.GONE
        llNoPatientFound?.visibility = View.VISIBLE
    }

    private fun filterTable(text: String) {
        if (getPatientListBOArrayList.size > 0) {
            val filteredList1: ArrayList<GetPatientListBO> = ArrayList<GetPatientListBO>()
            for (item in getPatientListBOArrayList) {
                if (text.let { item.PatientName.lowercase(Locale.ROOT).contains(it) }
                    || text.let { item.PatientName.uppercase(Locale.ROOT).contains(it) }
                    || text.let { item.Pno.uppercase(Locale.ROOT).contains(it) }
                    || text.let { item.PatientPhoneNo.uppercase(Locale.ROOT).contains(it) }
                ) {
                    filteredList1.add(item)
                }
            }
            //Log.e(TAG, "filter: size" + filteredList1.size());
            // Log.e(TAG, "filter: List" + filteredList1.toString());
            allPatientListAdapter?.updateData(mContext, filteredList1)
        }
    }

    private fun hitPatientListApi() {
            if(stSearchBy == context?.resources?.getString(R.string.search_by_mobile).toString()){
                if (isValidatedMobile()) {
                    if (CommonMethods.isNetworkAvailable(mContext!!)) {
                        getPatientList("", "",
                            etMobileNo?.text.toString(),
                            context?.resources?.getString(R.string.search_by_mobile).toString())
                    } else {
                        CommonMethods.showDialogForError(mContext as Activity,
                            AllKeys.NO_INTERNET_AVAILABLE)
                    }
                }
            }else{
                if (CommonMethods.isNetworkAvailable(mContext!!)) {
                    getPatientList(CommonMethods.parseDateToddMMyyyy(etFromDate?.text.toString(), "dd/MM/yyyy", "MM/dd/yyyy").toString(),
                        CommonMethods.parseDateToddMMyyyy(etToDate?.text.toString(), "dd/MM/yyyy", "MM/dd/yyyy").toString(),
                        CommonMethods.getPrefrence(mContext!!, AllKeys.MOBILE_NO).toString(), "")
                }else {
                    CommonMethods.showDialogForError(mContext as Activity, AllKeys.NO_INTERNET_AVAILABLE)
                }

            }
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
                    hitPatientListApi()
                }
            }
        }
    }

    private fun isValidatedMobile(): Boolean {
        if (etMobileNo?.text.toString().isEmpty()) {
            etMobileNo?.error = "Mobile No. required!"
            etMobileNo?.requestFocus()
            return false
        }

        if (etMobileNo?.text?.length!! > 12) {
            etMobileNo?.error = "Invalid mobile number!"
            etMobileNo?.requestFocus()
            return false
        }
        return true
    }

    private fun isValidated(): Boolean {
        if (etDate?.text.toString().isEmpty()) {
            etDate?.error = "Please select date!"
            etDate?.requestFocus()
            return false
        }
        return true
    }

    override fun openPaymentGateway(getPatientListBO: GetPatientListBO) {
        val message="Please pay due amount of "+CommonMethods.getPrefrence(mContext!!,AllKeys.CURRENCY_SYMBOL)+" "+getPatientListBO.balance+" /-"
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(true)
            //.setAnimation(R.raw.success_exploration)
            .setPositiveButton("Make Payment") { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
               showPictureDialog(getPatientListBO)
                dialogInterface.dismiss()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    private fun showPictureDialog(getPatientListBO: GetPatientListBO) {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Payment Gateway")
        val pictureDialogItems =
            arrayOf("RazorPay", "PayU Money"/*, "Preview"*/)
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 ->
                    if(CommonMethods.getPrefrence(requireContext(), AllKeys.MERCHANT_ID_RAZORPAY).equals(AllKeys.DNF)){
                        CommonMethods.showDialogForError(requireContext(),"Internal Server Error!")
                    }else{
                        // Delete Operation
                        val intent = Intent(context, CheckOutActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("patient_bo", getPatientListBO)
                        intent.putExtra("labname_bo", labNameBO)
                        intent.putExtra("type", "report_invoice")
                        intent.putExtra("payment_gateway",AllKeys.RAZOR_PAY)
                        startActivity(intent)
                    }
                1 ->
                    if(CommonMethods.getPrefrence(requireContext(), AllKeys.PAYU_MERCHANT_KEY).equals(AllKeys.DNF) ||
                        CommonMethods.getPrefrence(requireContext(), AllKeys.PAYU_MERCHANT_SALT).equals(AllKeys.DNF)){
                        CommonMethods.showDialogForError(requireContext(),"Internal Server Error!")
                    }else{
                        // Delete Operation
                        val intent = Intent(context, CheckOutActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("patient_bo", getPatientListBO)
                        intent.putExtra("labname_bo", labNameBO)
                        intent.putExtra("type", "report_invoice")
                        intent.putExtra("payment_gateway",AllKeys.PAYU)
                        startActivity(intent)
                    }
            }
        }
        pictureDialog.show()
    }

    override fun videoCallToPatient(getPatientListBO: GetPatientListBO) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setMessage("Video Call "+getPatientListBO.PatientName+"?")
        builder.setPositiveButton("CALL") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            if (getPatientListBO.Token.isNotEmpty() && getPatientListBO.Token != AllKeys.DNF) {
                sendNotification(mContext!!, getPatientListBO)
            } else {
                //CommonMethods.paymentGatewayNavigation(context, getPatientListBOArrayList[position].PatientName + " " + context.resources.getString(R.string.not_available_for_video_call))
                CommonMethods.showDialogForError(mContext!!,
                    "You do not have appointment with doctor booked on video call.")
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("NO") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            dialog.cancel()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun sendNotification(context: Context, getPatientListBO: GetPatientListBO) {
        Log.e(TAG, "sendNotification: $getPatientListBO")

        val title =
            CommonMethods.getPrefrence(context, AllKeys.PERSON_NAME) + " is Video Calling you...."
        val link = ConfigUrl.VIDEO_CALL_BASE_URL + getPatientListBO.regno
        var message = "Hi.. " + CommonMethods.getPrefrence(context,
            AllKeys.PERSON_NAME) + " is requesting you a video call...please receive it"
        val notificationData = JSONObject()
        val notificationData1 = JSONObject()
        val notification = JSONObject()
        try {
            //parameter sending for notification key...
            notificationData1.put("title", title)
            notificationData1.put("body", link)
            notificationData1.put("text", title)
            notificationData1.put("click_action", "VIDEOCALL")

            //parameter sending for data key....
            notificationData.put("title", title)
            notificationData.put("message", link)
            notificationData.put("body", link)
            notificationData.put("text", link)
            notificationData.put("extra_information", link)
            notificationData.put("click_action", "VIDEOCALL")

            notification.put("to", getPatientListBO.Token)
            //notification.put("notification", notificationData1)
            notification.put("data", notificationData)
            val jsonObjectRequest: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, ConfigUrl.FCM_API, notification,
                    Response.Listener { response: JSONObject ->
                        Log.e("mytag", "Success \n sendNotification: $response")

                        if (response.getString("success").equals("1")) {
                            val intent = Intent(context, VideoConferenceActivity2::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            intent.putExtra("link", link)
                            context.startActivity(intent)
                        }else{
                            CommonMethods.showDialogForError(mContext!!,AllKeys.UNABLE_TO_CONNECT)
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
                        // Log.e("mytag", "error: " + error);
                        val response = error.networkResponse
                        if (error is ServerError && response != null) {
                            try {
                                val res = String(response.data,
                                    Charset.forName(HttpHeaderParser.parseCharset(response.headers,
                                        "utf-8")))
                                // Now you can use any deserializer to make sense of data
                                val obj = JSONObject(res)

                                CommonMethods.showDialogForError(context,
                                    "Unable to connect...Try after some time! ")
                            } catch (e1: UnsupportedEncodingException) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace()
                                // Log.e("mytag", "sendNotification erro: e1 " + e1);
                            } catch (e2: JSONException) {
                                // returned data is not JSONObject?
                                e2.printStackTrace()
                                //  Log.e("mytag", "sendNotification erro e2: " + e2);
                            }
                        }
                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Authorization"] = ConfigUrl.AUTHORIZATION
                        params["Content-Type"] = "application/json"
                        return params
                    }
                }
            Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
            //mQueue.add(jsonObjectRequest);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}