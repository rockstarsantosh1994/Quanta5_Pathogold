package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.CheckOutActivity
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.AllPatientListAdapter
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListResponse
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import com.bms.pathogold_bms.model.lab_super_admin.LabNameResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.view_models.GetAllPatientViewModel
import com.bms.pathogold_bms.view_models.ViewAllAppointmentModel
import com.google.android.material.card.MaterialCardView
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DashBoardFragment : BaseFragment(), View.OnClickListener,
    AllPatientListAdapter.OpenPaymentGateway {

    private val TAG = "DashBoardFragment"

    //Edittext declaration..
    private var etSearch: EditText? = null
    private var etSelectLab: EditText? = null

    //CardView declaration..
    private var cvPatientList: MaterialCardView? = null
    private var cvViewAppointment: MaterialCardView? = null
    private var cvBookAppointment: MaterialCardView? = null

    //Recyclerview declaration..
    private var rvGetPatientList: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvTitle: TextView? = null
    private var tvLabName:TextView?=null
    private var tvAdvertisementText: TextView? = null

    //LinearLayout declaration
    private var llNoPatientFound: LinearLayout? = null
    private var llSelectLabView: LinearLayout? = null
    private var llButtonView: LinearLayout? = null

    //Swipe to refresh layout...
    private var swipeToRefreshLaout: SwipeRefreshLayout? = null

    //ArrayList declaration..
    private val getPatientListBOArrayList = ArrayList<GetPatientListBO>()
    private var getLabNameArrayList = ArrayList<LabNameBO>()

    //Adapter declaration..
    private var allPatientListAdapter: AllPatientListAdapter? = null

    //GetAllPatientViewModel...
    private lateinit var getAllPatientViewModel: GetAllPatientViewModel
    private lateinit var viewAllAppointmentModel: ViewAllAppointmentModel

    private val GET_LAB_NAME = 1

    //Business object declaration..
    private var labNameBO: LabNameBO? = null

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAllPatientViewModel =
            ViewModelProvider(requireActivity())[GetAllPatientViewModel::class.java] // init view model

        viewAllAppointmentModel = ViewModelProvider(requireActivity())[ViewAllAppointmentModel::class.java] // init view model

        //basic intialisation..
        initViews(view)

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            llSelectLabView?.visibility = View.VISIBLE
            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                getSuperAdminLabs()
            } else {
                CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
            }
        } else {
            //Get Patient data Data based on codition..
            getPatientListApiData()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(view: View) {
        (activity as DashBoardActivity).toolbar?.title = CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)

        //Cardview declaration..
        cvPatientList = view.findViewById(R.id.cv_patient_list)
        cvViewAppointment = view.findViewById(R.id.cv_view_appointments)
        cvBookAppointment = view.findViewById(R.id.cv_book)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)
        tvTitle = view.findViewById(R.id.tv_todays_patient)
        tvLabName = view.findViewById(R.id.tv_lab_name)
        tvLabName?.text="LabName : "+CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME_C)

        //Recycler binding..
        rvGetPatientList = view.findViewById(R.id.rv_get_patient)

        //LinearLayout binding..
        llNoPatientFound = view.findViewById(R.id.ll_no_data_found)
        llSelectLabView = view.findViewById(R.id.ll_search_labname_view)
        llButtonView = view.findViewById(R.id.ll_buttons_view)

        //Swipe to refresh layout binding...
        swipeToRefreshLaout = view.findViewById(R.id.swipe_to_refresh)
        swipeToRefreshLaout?.setColorSchemeResources(R.color.purple_700)

        //Click Listeners
        cvPatientList?.setOnClickListener(this)
        cvViewAppointment?.setOnClickListener(this)
        cvBookAppointment?.setOnClickListener(this)

        //Edittext declaration..
        etSearch = view.findViewById(R.id.et_search)
        etSelectLab = view.findViewById(R.id.et_select_lab)
        etSelectLab?.setOnClickListener(this)

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })

        swipeToRefreshLaout?.setOnRefreshListener {
            //Get Data based on codition..
            getPatientListApiData()
            swipeToRefreshLaout?.isRefreshing = false
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_dash_board

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun getPatientListApiData() {
        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            val linearLayoutManager = LinearLayoutManager(mContext)
            rvGetPatientList?.layoutManager = linearLayoutManager
            //rvGetPatientList?.layoutManager = object : LinearLayoutManager(mContext!!){ override fun canScrollVertically(): Boolean { return false } }
            tvTitle?.text = "Today's Patient"

            val yearFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy")
            val calYear = Calendar.getInstance()
            val todayDate = yearFormat.format(calYear.time)

            getPatientList(todayDate,
                CommonMethods.getPrefrence(mContext!!, AllKeys.MOBILE_NO).toString())
        } else {
            CommonMethods.showDialogForError(mContext as Activity, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cv_patient_list -> {
                getAllPatientViewModel.list.value!!.clear()
                if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("get_lab_name", getLabNameArrayList)
                    bundle.putString("launch_type", AllKeys.IS_PATIENT_LIST_LAUNCH)
                    (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.all_patient)
                    (activity as DashBoardActivity).navController.navigate(R.id.getPatientListFragment, bundle)
                } else {
                    val bundle = Bundle()
                    bundle.putString("launch_type", AllKeys.IS_PATIENT_LIST_LAUNCH)
                    (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.all_patient)
                    (activity as DashBoardActivity).navController.navigate(R.id.getPatientListFragment,bundle)
                }
            }

            R.id.cv_book -> {
               //if (CommonMethods.getPrefrence(mContext!!, AllKeys.appointment).equals("true")) {
                    val bundle = Bundle()
                    bundle.putString("app_type", "new")
                    (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.view_slot)
                    (activity as DashBoardActivity).navController.navigate(R.id.checkSlotFragment2, bundle)
                /*} else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.SERVICE_NOT_AVAILABLE)
                }*/
            }

            R.id.cv_view_appointments -> {
                viewAllAppointmentModel.list.value!!.clear()
                (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.view_appointment)
                //(activity as DashBoardActivity).navController.navigate(R.id.viewAppointmentFragment)
                (activity as DashBoardActivity).navController.navigate(R.id.viewAppointmentFragment2)
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

    private fun getPatientList(stDate: String, stMobileNo: String) {
        val params: MutableMap<String, String> = HashMap()
        params["fromdate"] = stDate
        params["todate"] = stDate

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
            params["LabCode"] = labNameBO?.labname.toString()
        } else {
            params["LabCode"] =
                mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        }

        params["UserType"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE) }.toString()
        params["UserName"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USER_NAME) }.toString()
        if (mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE) }.toString()
                .equals(AllKeys.Patient, false)
        ) {
            params["PatientName"] = ""
            params["PatientMobileNo"] = stMobileNo
        } else {
            params["PatientName"] = ""
            params["PatientMobileNo"] = ""
        }
        if (mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE) }.toString()
                .equals(AllKeys.Consultant, false)
        ) {
            params["Pno"] =
                mContext?.let { CommonMethods.getPrefrence(it, AllKeys.HSC_ID) }.toString()
        } else {
            params["Pno"] = ""
        }

        Log.e(TAG, "getPatientList: $params")

      /*  val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        digiPath?.getApiRequestHelper()?.getPatientList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getPatientListResponse = `object` as GetPatientListResponse
                    Log.e(TAG, "onSuccess: $getPatientListResponse")
                    if (getPatientListResponse.ResponseCode == 200) {
                        if (getPatientListResponse.ResultArray.size > 0) {
                            getPatientListBOArrayList.clear()
                            getPatientListBOArrayList.addAll(getPatientListResponse.ResultArray)
                            rvGetPatientList?.visibility = View.VISIBLE
                            llNoPatientFound?.visibility = View.GONE

                            allPatientListAdapter = mContext?.let {
                                AllPatientListAdapter(it,
                                    getPatientListBOArrayList,
                                    stDate,
                                    this@DashBoardFragment)
                            }
                            rvGetPatientList?.adapter = allPatientListAdapter
                        } else {
                            rvGetPatientList?.visibility = View.GONE
                            llNoPatientFound?.visibility = View.VISIBLE
                        }
                    } else {
                        rvGetPatientList?.visibility = View.GONE
                        llNoPatientFound?.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(apiResponse: String) {
                   // progress.dismiss()
                    rvGetPatientList?.visibility = View.GONE
                    llNoPatientFound?.visibility = View.VISIBLE
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getSuperAdminLabs() {
        val params: MutableMap<String, String> = HashMap()
        params["Pno"] = CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()

        Log.e(TAG, "getSuperAdminLabs: $params")

       /* val progress = ProgressDialog(activity)
        progress.setMessage("Please Wait....")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        digiPath?.getApiRequestHelper()
            ?.getLabSuperAdmin(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getLabNameResponse = `object` as LabNameResponse
                   // progress.dismiss()
                    Log.e(TAG, "onSuccess: $getLabNameResponse")
                    if (getLabNameResponse.ResponseCode == 200) {
                        getLabNameArrayList = getLabNameResponse.ResultArray

                        labNameBO = LabNameBO(
                            getLabNameArrayList[0].labname,
                            getLabNameArrayList[0].labname_c)

                        etSelectLab?.setText(labNameBO?.labname_c)

                        //Get Patient data Data based on codition..
                        getPatientListApiData()
                    } else {
                        Toast.makeText(mContext!!, getLabNameResponse.Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                   // progress.dismiss()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    @Deprecated("Deprecated in Java")
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
                    getPatientListApiData()
                }
            }
        }
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

    override fun openPaymentGateway(getPatientListBO: GetPatientListBO) {
        val message = "Please pay due amount of " + CommonMethods.getPrefrence(mContext!!,
            AllKeys.CURRENCY_SYMBOL) + " " + getPatientListBO.balance + " /-"
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(true)
            //.setAnimation(R.raw.success_exploration)
            .setPositiveButton("Make Payment") { dialogInterface: DialogInterface, which: Int ->
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
}