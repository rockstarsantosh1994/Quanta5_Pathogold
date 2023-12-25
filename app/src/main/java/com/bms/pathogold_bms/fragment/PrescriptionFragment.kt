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
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.MultiAutoCompleteTextView
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.*
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.PrescriptionUploadBO
import com.bms.pathogold_bms.model.adddrug.AddDrugResponse
import com.bms.pathogold_bms.model.getcomplain.GetComplainBO
import com.bms.pathogold_bms.model.getdiagnosis.GetDiagnosisBO
import com.bms.pathogold_bms.model.getdosemaster.GetDoseMasterBO
import com.bms.pathogold_bms.model.getdrug.GetDrugBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getprovisionaldiagnosis.GetProvDiagnosisBO
import com.bms.pathogold_bms.model.getrefcc.GetRefCCBO
import com.bms.pathogold_bms.model.getsysexams.GetSysExamsBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.shreyaspatil.MaterialDialog.MaterialDialog
import io.paperdb.Paper
import java.util.*
import kotlin.collections.ArrayList

class PrescriptionFragment : BaseFragment(), View.OnClickListener ,SelectedTestAdapter.DeleteSelectedTest{

    //Declaration...
    private val TAG = "PrescriptionActivity"

    //Toolbar declaration...
    private var toolbar: Toolbar? = null

    //EditText Declaration.
    private var etPatientName: EditText? = null
    private var etAge: EditText? = null
    private var etSex: EditText? = null
    private var etFollowUpDate: EditText? = null
    private var etDoctorAdvice: EditText? = null
    private var etAddNewDrug: TextInputEditText? = null

    //TextInputLayout Declaration...
    private var tilDrugName: TextInputLayout? = null
    private var tilFrequency: TextInputLayout? = null
    private var tilDay: TextInputLayout? = null
    private var tilQty: TextInputLayout? = null
    private var tilNotes: TextInputLayout? = null
    private var tilAddNewDrug: TextInputLayout? = null
    private var tilComplaintsReasons:TextInputLayout?=null
    private var tilHistoryOfPresentIllness:TextInputLayout?=null
    private var tilProvisionalDiagnosis:TextInputLayout?=null
    private var tilSelectTest:TextInputLayout?=null
    private var tilDiagnosis:TextInputLayout?=null

    //AutoCompleteTextView Declaration..
    private var actDrugName: MaterialAutoCompleteTextView? = null
    private var actFrequency: MaterialAutoCompleteTextView? = null
    private var actDay: MaterialAutoCompleteTextView? = null
    private var actQty: MaterialAutoCompleteTextView? = null
    private var actNotes: MaterialAutoCompleteTextView? = null
    private var actComplaintsreasons: MultiAutoCompleteTextView? = null
    private var actHistoryOfPresentIllness: MultiAutoCompleteTextView? = null
    private var actProvisionnalDiagnosis: MultiAutoCompleteTextView? = null
    private var actDiagnosis:MultiAutoCompleteTextView?=null
    private var actSelectTest: MaterialAutoCompleteTextView? = null

    //AppCompatButton declaration...
    private var btnAddPrescription: AppCompatButton? = null
    private var btnSubmitDrugsDeatils: AppCompatButton? = null
    private var btnViewOldPrescription: AppCompatButton? = null
    private var btnAddNewDrug: AppCompatButton? = null

    //RecyclerView declaration..
    private var rvDrugs: RecyclerView? = null
    private var rvSelectedTest:RecyclerView?=null
    private var rvSystemExam:RecyclerView?=null

    //Textview declaration..
    private var tvTotalTestPrice: TextView?=null
    private var tvNoTestSelected: TextView?=null

    //LinearLayout Declaration..
    private var llSelectTestView:LinearLayout?=null

    private var getPatientListBO: GetPatientListBO? = null
    private var getDrugBO: GetDrugBO? = null
    private var getDoseMasterBO: GetDoseMasterBO? = null

    private var getDrugBOArrayList = ArrayList<GetDrugBO>()
    private var getDoseMasterArrayList = ArrayList<GetDoseMasterBO>()
    private var getComplainsArrayList=ArrayList<GetComplainBO>()
    private var getDiagnosisArrayList=ArrayList<GetDiagnosisBO>()
    private var getProvDiagnosisArrayList=ArrayList<GetProvDiagnosisBO>()
    private var prescriptionUploadArrayList = ArrayList<PrescriptionUploadBO>()
    private var selectedComplainBOList=ArrayList<GetComplainBO>()
    private var selectedDiagnosisBOList=ArrayList<GetDiagnosisBO>()
    private var selectedProDiagnosisBOList=ArrayList<GetProvDiagnosisBO>()
    private var getTestListBOArrayList = ArrayList<GetTestCodeBO>()

    private val getTestId = ArrayList<String>()
    private val getTestName = ArrayList<String>()
    private var selectedTestArrayList = ArrayList<GetTestCodeBO>()

    private var getSysExamsMap: MutableMap<String,ArrayList<GetSysExamsBO>> = LinkedHashMap()

    private var getDrugMasterAdapter: GetDrugMasterAdapter? = null
    private var getDoseMasterAdapter: GetDoseMasterAdapter? = null

    private var selectedTestAdapter: SelectedTestAdapter? = null

    private var testTotalRate = 0

    private var TEST_CODE_DATA_DIALOG=1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Paper.init(mContext)
        //intent parcelable...
        val arguments = requireArguments()
        getPatientListBO = arguments.getParcelable("patient_bo")

        //Add data from paperDB
        getDrugBOArrayList = Paper.book().read("drug_master")
        getDoseMasterArrayList = Paper.book().read("dose_master")
        getComplainsArrayList= Paper.book().read("complains_master")
        getDiagnosisArrayList= Paper.book().read("diagnosis_master")
        getProvDiagnosisArrayList=Paper.book().read("prov_diagnosis_master")
        getTestListBOArrayList=Paper.book().read("test_list")
        getSysExamsMap=Paper.book().read("sys_exams")

        //basic intalisation...
        initViews(view)

        //Set patient data
        setPatientData(getPatientListBO)

        //Set AutoCompleteTextView data..of drug, complaints, provisional diagnosis, diagnosis
        setAutoCompleteTextViewData()
    }

    override val activityLayout: Int
        get() = R.layout.fragment_prescription

    private fun initViews(view: View) {
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.prescription)

        //TextView binding..
        tvNoTestSelected=view.findViewById(R.id.tv_no_test_selected)
        tvTotalTestPrice=view.findViewById(R.id.tv_total_test_price)

        //LinearLayout binding..
        llSelectTestView=view.findViewById(R.id.ll_selected_test_view)

        //Edittext binding..
        etPatientName = view.findViewById(R.id.et_patient_name)
        etAge = view.findViewById(R.id.et_age)
        etSex = view.findViewById(R.id.et_sex)
        etFollowUpDate = view.findViewById(R.id.et_followup_date)
        etDoctorAdvice = view.findViewById(R.id.et_doctor_advice)

        //TextInputLayout binding..
        tilDrugName = view.findViewById(R.id.til_drug_name)
        tilFrequency = view.findViewById(R.id.til_frequency)
        tilDay = view.findViewById(R.id.til_day)
        tilQty = view.findViewById(R.id.til_qty)
        tilNotes = view.findViewById(R.id.til_notes)
        tilComplaintsReasons = view.findViewById(R.id.til_complaints_reason)
        tilHistoryOfPresentIllness= view.findViewById(R.id.til_history_of_present_illness)
        tilProvisionalDiagnosis= view.findViewById(R.id.til_provisional_diagnosis)
        tilDiagnosis= view.findViewById(R.id.til_diagnosis)
        tilSelectTest= view.findViewById(R.id.til_select_test)

        //Material Auto Complete TextView binding..
        actDrugName = view.findViewById(R.id.act_drug_name)
        actFrequency = view.findViewById(R.id.act_freqency)
        actDay = view.findViewById(R.id.act_day)
        actQty = view.findViewById(R.id.act_qty)
        actNotes = view.findViewById(R.id.act_notes)
        actComplaintsreasons= view.findViewById(R.id.act_complaints_reason)
        actHistoryOfPresentIllness= view.findViewById(R.id.act_history_of_present_illness)
        actProvisionnalDiagnosis= view.findViewById(R.id.act_provisional_diagnosis)
        actDiagnosis= view.findViewById(R.id.act_diagnosis)
        actSelectTest= view.findViewById(R.id.act_select_test)

        //RecyclerView declaration...
        rvDrugs = view.findViewById(R.id.rv_drugs)
        val lm: LinearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvDrugs?.layoutManager = lm

        rvSelectedTest=view.findViewById(R.id.rv_selected_test)
        val lm1: LinearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvSelectedTest?.layoutManager = lm1

        rvSystemExam=view.findViewById(R.id.rv_system_exam)
        val lm2: LinearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvSystemExam?.layoutManager = lm2

        //AppCompatButton binding..
        btnAddPrescription = view.findViewById(R.id.btn_add_prescription)
        btnSubmitDrugsDeatils = view.findViewById(R.id.btn_submit_drugs)
        btnViewOldPrescription = view.findViewById(R.id.btn_view_old_prescription)
        btnAddNewDrug = view.findViewById(R.id.btn_add_new_drug)

        //Click Listeners..
        etFollowUpDate?.setOnClickListener(this)
        btnAddPrescription?.setOnClickListener(this)
        btnSubmitDrugsDeatils?.setOnClickListener(this)
        btnViewOldPrescription?.setOnClickListener(this)
        btnAddNewDrug?.setOnClickListener(this)
        tilSelectTest?.setOnClickListener(this)
        actSelectTest?.setOnClickListener(this)

        //Set todays date
        etFollowUpDate?.setText(CommonMethods.getTodayDate("MM/dd/yyyy"))

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.viewprescription).equals("true", true)) {
            btnViewOldPrescription?.visibility = View.VISIBLE
        } else {
            btnViewOldPrescription?.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.et_followup_date -> {
                val tempSysExamList= ArrayList<GetSysExamsBO>()
                val tempSelected=ArrayList<String>()
                //first get all data in arraylist
                for (key in getSysExamsMap.keys.toTypedArray()) {
                    // println("key : $key")
                    //Log.e(TAG, "onCreate: "+ getEMRCheckBoxMap[key])
                    getSysExamsMap[key]?.let { tempSysExamList.addAll(it) }
                }

                for(temp in tempSysExamList){
                    if(temp.isSelected){
                        tempSelected.add(temp.sysexam_detail)
                    }
                }
                Log.e(TAG, "onClick: $tempSelected")
               /* // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        //  etDate?.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                        etFollowUpDate?.setText("" + (monthOfYear + 1) + "/" + dayOfMonth.toString() + "/" + year)
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog.show()*/
            }

            R.id.btn_add_prescription -> {
                //add data to Prescription BO
                addPrescription()
            }

            R.id.btn_add_new_drug -> {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Add New Drug")
                builder.setCancelable(false)
                val inflater =
                    context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val customLayout: View = inflater.inflate(R.layout.row_add_new_drug_dialog, null)
                builder.setView(customLayout)
                tilAddNewDrug = customLayout.findViewById(R.id.til_add_new_drug)
                etAddNewDrug = customLayout.findViewById(R.id.et_add_new_drug)

                builder.setPositiveButton("Add") { dialog, which -> // send data from the
                    if (isValidatedDrugName()) {
                        //Add new drug..
                        addNewdrug(etAddNewDrug?.text.toString())
                        dialog.dismiss()
                    }
                }

                builder.setNegativeButton("Cancel") { dialog, which -> // send data from the
                    // AlertDialog to the Activity
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }

            R.id.btn_submit_drugs -> {
                if (CommonMethods.isNetworkAvailable(mContext!!)) {
                    savePrescription()
                } else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
                }
            }

            R.id.btn_view_old_prescription -> {
                val bundle = Bundle()
                bundle.putParcelable("patient_bo", getPatientListBO)
                (context as DashBoardActivity).navController.navigate(R.id.oldPrescActivity, bundle)
            }

            R.id.til_select_test,R.id.act_select_test->{
                if(getTestListBOArrayList.size==0){
                    CommonMethods.showDialogForError(mContext!!,AllKeys.DATA_NOT_FOUND)
                }else{
                    val testCodeDialog = TestCodeDialog()
                    val bundle = Bundle()
                    bundle.putString("open_via", "fragment")
                    bundle.putParcelableArrayList("testcode_list", getTestListBOArrayList)
                    testCodeDialog.arguments = bundle
                    testCodeDialog.setTargetFragment(this, TEST_CODE_DATA_DIALOG)
                    testCodeDialog.show(fragmentManager?.beginTransaction()!!, "TestCodeDialog")
                }
            }
        }
    }

    private fun isValidatedDrugName(): Boolean {
        if (etAddNewDrug?.text.toString().isEmpty()) {
            tilAddNewDrug?.error = "Drug name required!"
            tilAddNewDrug?.requestFocus()
            return false
        } else {
            tilAddNewDrug?.isErrorEnabled = false
        }
        return true
    }

    private fun savePrescription() {
        val data = Gson().toJson(prescriptionUploadArrayList)
        //Log.e(TAG, "addPrescription: $data" )
        val params: MutableMap<String, String> = HashMap()
        params["Jsonarray"] = data

        Log.e(TAG, "savePrescription: $params")
        val progress = ProgressDialog(activity)
        progress.setMessage("Saving prescription...Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertEMRTreatmentDetailsofPaitent(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        showDialogForSuccess(mContext!!, commonResponse.Message)
                    } else {
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun showDialogForSuccess(context: Context,message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: com.shreyaspatil.MaterialDialog.interfaces.DialogInterface, which: Int ->
                // Delete Operation
                (context as DashBoardActivity).navController.popBackStack(R.id.prescriptionFragment, true)
                dialogInterface.dismiss()
                //context.finish()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    private fun addPrescription() {
        if (isValidated()) {
            val prescriptionUploadBO = PrescriptionUploadBO()
            //prescriptionUploadBO.setPepatid(getPatientListBO?.regno)
            prescriptionUploadBO.setPepatid(getPatientListBO?.PePatID)
            prescriptionUploadBO.setPharmacyDayabaseName("no")
            //prescriptionUploadBO.setOpdNo("0")
            prescriptionUploadBO.setOpdNo(getPatientListBO?.regno)
            prescriptionUploadBO.setIpdNo("0")
            prescriptionUploadBO.setGeneric_Id("0")
            prescriptionUploadBO.setDrugId(getDrugBO?.DrugId)
            prescriptionUploadBO.setDose(getDoseMasterBO?.Dose)
            prescriptionUploadBO.setDay(actDay?.text.toString())
            prescriptionUploadBO.setQty(actQty?.text.toString())
            prescriptionUploadBO.setNote(actNotes?.text.toString())
            prescriptionUploadBO.setCompanyid(AllKeys.COMPANY_ID)
            prescriptionUploadBO.setUserName(CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME).toString())
            prescriptionUploadBO.setTId("0")
            prescriptionUploadBO.setAction("1")
            prescriptionUploadBO.setFinancialYearID("0")
            prescriptionUploadBO.setTreatmentNo("0")
            prescriptionUploadBO.setItemId("0")
            prescriptionUploadBO.setHivNo("0")
            prescriptionUploadBO.setClinicNo("0")
            prescriptionUploadBO.setRouteID(getDoseMasterBO?.SrNo)
            prescriptionUploadBO.setNoteid("0")
            prescriptionUploadBO.setTDrugName(getDrugBO?.DrugName)

            //add data to arraylist..
            if (!prescriptionUploadArrayList.contains(prescriptionUploadBO)) {
                prescriptionUploadArrayList.add(prescriptionUploadBO)
            }

            if (prescriptionUploadArrayList.size > 0) {
                btnSubmitDrugsDeatils?.visibility = View.VISIBLE
                val setPrescriptionAdapter = SetPrescriptionAdapter(mContext!!,
                    prescriptionUploadArrayList,
                    this@PrescriptionFragment)
                rvDrugs?.adapter = setPrescriptionAdapter
            } else {
                btnSubmitDrugsDeatils?.visibility = View.GONE
            }

            Log.e(TAG, "addPrescription: $prescriptionUploadArrayList")
            //resetAllFields..
            resetFields()
        }
    }

    private fun addNewdrug(stDrugName: String) {
        val params: MutableMap<String, String> = HashMap()
        params["username"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME).toString()
        params["CompanyId"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["drugname"] = stDrugName

        Log.e(TAG, "addNewdrug: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertupdatedrugmaster(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val addDrugResponse = `object` as AddDrugResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $addDrugResponse")
                    if (addDrugResponse.ResponseCode == 200) {
                        getDrugBOArrayList.add(GetDrugBO(addDrugResponse.ResultArray[0].drugid, addDrugResponse.ResultArray[0].drugname))
                        getDrugBOArrayList.addAll(Paper.book().read("drug_master"))
                        showDialogForSuccessAddDrug(mContext!!, addDrugResponse.Message)
                    } else {
                        CommonMethods.showDialogForError(mContext!!, addDrugResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun showDialogForSuccessAddDrug(context: Context,message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: com.shreyaspatil.MaterialDialog.interfaces.DialogInterface, which: Int ->
                // Delete Operation
                Paper.book().delete("drug_master")
                Paper.book().write("drug_master", getDrugBOArrayList)

                getDrugMasterAdapter?.notifyDataSetChanged()
                //notifiy updater
                if (getDrugBOArrayList.size > 0) {
                    getDrugMasterAdapter = GetDrugMasterAdapter(mContext!!, getDrugBOArrayList)
                    actDrugName?.threshold = 0
                    actDrugName?.setAdapter(getDrugMasterAdapter)

                    actDrugName?.onItemClickListener =
                        OnItemClickListener { adapterView, view, i, l -> //Log.e(TAG, "onItemClick: actownername " + adapterView.getItemAtPosition(i));
                            getDrugBO = adapterView.getItemAtPosition(i) as GetDrugBO
                        }
                }
                dialogInterface.dismiss()
                //context.finish()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    fun hideSubmitButton() {
        if (prescriptionUploadArrayList.size > 0) {
            btnSubmitDrugsDeatils?.visibility = View.VISIBLE
        } else {
            btnSubmitDrugsDeatils?.visibility = View.GONE
        }
    }

    private fun setPatientData(patientListBO: GetPatientListBO?) {
        etPatientName?.setText(patientListBO?.PatientName)
        etAge?.setText(patientListBO?.age)
        etSex?.setText(patientListBO?.sex)
    }

    private fun setAutoCompleteTextViewData() {
        if (getDrugBOArrayList.size > 0) {
            getDrugMasterAdapter = GetDrugMasterAdapter(mContext!!, getDrugBOArrayList)
            actDrugName?.threshold = 0
            actDrugName?.setAdapter(getDrugMasterAdapter)

            actDrugName?.onItemClickListener =
                OnItemClickListener { adapterView, view, i, l -> //Log.e(TAG, "onItemClick: actownername " + adapterView.getItemAtPosition(i));
                    getDrugBO = adapterView.getItemAtPosition(i) as GetDrugBO
                }
        }

        if (getDoseMasterArrayList.size > 0) {
            getDoseMasterAdapter = GetDoseMasterAdapter(mContext!!, getDoseMasterArrayList)
            actFrequency?.threshold = 0
            actFrequency?.setAdapter(getDoseMasterAdapter)

            actFrequency?.onItemClickListener =
                OnItemClickListener { adapterView, view, i, l -> //Log.e(TAG, "onItemClick: actownername " + adapterView.getItemAtPosition(i));
                    getDoseMasterBO = adapterView.getItemAtPosition(i) as GetDoseMasterBO

                    actDay?.setText("1")
                    actQty?.setText(getDoseMasterBO!!.Qty)
                    actNotes?.setText(getDoseMasterBO!!.DoseDescription)
                }
        }

        actDay?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if(actDay?.text.toString().isNotEmpty() && actQty?.text.toString().isNotEmpty()){
                    val qty:Int= actDay?.text.toString().toInt() * getDoseMasterBO!!.Qty.toInt()
                    actQty?.setText(qty.toString())
                }
            }
        })

        if(getComplainsArrayList.size>0){
            val getComplainMasterAdapter = GetComplainMasterAdapter(mContext!!, getComplainsArrayList)
            actComplaintsreasons?.threshold = 0
            actComplaintsreasons?.setAdapter(getComplainMasterAdapter)
            actComplaintsreasons?.setTokenizer(CommaTokenizer())

            actComplaintsreasons?.onItemClickListener =
                OnItemClickListener { adapterView, view, i, l ->
                    if (!selectedComplainBOList.contains(adapterView.getItemAtPosition(i) as GetComplainBO)) {
                        selectedComplainBOList.add(adapterView.getItemAtPosition(i) as GetComplainBO)
                    }
                    actComplaintsreasons?.setText(selectedComplainBOList.toString().replace("[", "")
                        .replace("]", ""))
                }
        }

        if(getDiagnosisArrayList.size>0){
            val getDiagnosisMasterAdapter=GetDiagnosisMasterAdapter(mContext!!,getDiagnosisArrayList)
            actDiagnosis?.threshold = 0
            actDiagnosis?.setAdapter(getDiagnosisMasterAdapter)
            actDiagnosis?.setTokenizer(CommaTokenizer())

            actDiagnosis?.onItemClickListener =
                OnItemClickListener { adapterView, view, i, l ->
                    if (!selectedDiagnosisBOList.contains(adapterView.getItemAtPosition(i) as GetDiagnosisBO)) {
                        selectedDiagnosisBOList.add(adapterView.getItemAtPosition(i) as GetDiagnosisBO)
                    }
                    actDiagnosis?.setText(selectedDiagnosisBOList.toString().replace("[", "")
                        .replace("]", ""))
                }
        }

        if(getProvDiagnosisArrayList.size>0){
            val getProvDiagnosisMasterAdapter=GetProvDiagnosisMasterAdapter(mContext!!,getProvDiagnosisArrayList)
            actProvisionnalDiagnosis?.threshold = 0
            actProvisionnalDiagnosis?.setAdapter(getProvDiagnosisMasterAdapter)
            actProvisionnalDiagnosis?.setTokenizer(CommaTokenizer())

            actProvisionnalDiagnosis?.onItemClickListener =
                OnItemClickListener { adapterView, view, i, l ->
                    if (!selectedProDiagnosisBOList.contains(adapterView.getItemAtPosition(i) as GetProvDiagnosisBO)) {
                        selectedProDiagnosisBOList.add(adapterView.getItemAtPosition(i) as GetProvDiagnosisBO)
                    }
                    actProvisionnalDiagnosis?.setText(selectedProDiagnosisBOList.toString().replace("[", "")
                        .replace("]", ""))
                }
        }

        if(getSysExamsMap.isNotEmpty()){
            val getSysExamCheckBoxAdapter=GetSysExamCheckBoxAdapter(mContext,getSysExamsMap.keys.toTypedArray(),getSysExamsMap)
            rvSystemExam?.adapter=getSysExamCheckBoxAdapter
        }
    }

    private fun resetFields() {
        actDrugName?.text = null
        actFrequency?.text = null
        actDay?.text = null
        actQty?.text = null
        actNotes?.text = null

        //make this object also null to avoid miss entry while click on addprescription...
        getDrugBO = null
        getDoseMasterBO = null
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            TEST_CODE_DATA_DIALOG -> {
                selectedTestArrayList = ArrayList()
                val bundle = data?.extras
                selectedTestArrayList.clear()
                selectedTestArrayList.addAll(bundle?.getParcelableArrayList("test_code_list")!!)
                getTestId.clear()
                getTestName.clear()

                //attach RecyclerView of Selected Test..
                attachSelectedTestRecyclerView(selectedTestArrayList)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun attachSelectedTestRecyclerView(resultArray: ArrayList<GetTestCodeBO>) {
        if (resultArray.size > 0) {
            llSelectTestView?.visibility = View.VISIBLE
            tvNoTestSelected?.visibility = View.GONE
            selectedTestAdapter = SelectedTestAdapter(mContext!!,
                selectedTestArrayList,
                this@PrescriptionFragment,
                View.VISIBLE)
            rvSelectedTest?.adapter = selectedTestAdapter
        } else {
            llSelectTestView?.visibility = View.GONE
            tvNoTestSelected?.visibility = View.VISIBLE
        }

        var sum = 0
        for (temp in selectedTestArrayList) {
            //Total of all selected test rate..
            sum += temp.rate.toInt()
            getTestId.add(temp.tlcode)
            getTestName.add(temp.title)
        }
        testTotalRate = sum
        tvTotalTestPrice?.text = "Total: $testTotalRate /-"
    }

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
            llSelectTestView?.visibility = View.GONE
            tvNoTestSelected?.visibility = View.VISIBLE
        }
    }

    private fun isValidated(): Boolean {
        if (actDrugName?.text.toString().isEmpty()) {
            tilDrugName?.error = "DrugName Required!"
            tilDrugName?.requestFocus()
            return false
        } else {
            tilDrugName?.isErrorEnabled = false
        }

        if (actFrequency?.text.toString().isEmpty()) {
            tilFrequency?.error = "Frequency Required!"
            tilFrequency?.requestFocus()
            return false
        } else {
            tilFrequency?.isErrorEnabled = false
        }

        if (actDay?.text.toString().isEmpty()) {
            tilDay?.error = "Day Required!"
            tilDay?.requestFocus()
            return false
        } else {
            tilDay?.isErrorEnabled = false
        }

        if (actQty?.text.toString().isEmpty()) {
            tilQty?.error = "Qty Required!"
            tilQty?.requestFocus()
            return false
        } else {
            tilQty?.isErrorEnabled = false
        }

        if (actNotes?.text.toString().isEmpty()) {
            tilNotes?.error = "Notes Required!"
            tilNotes?.requestFocus()
            return false
        } else {
            tilNotes?.isErrorEnabled = false
        }

        if (getDrugBO == null) {
            CommonMethods.showDialogForError(mContext!!, "Please select valid drug!")
            return false
        }

        if (getDoseMasterBO == null) {
            CommonMethods.showDialogForError(mContext!!, "Please select valid frequency!")
            return false
        }
        return true
    }

}