package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.GetEMRCheckBoxAdapter
import com.bms.pathogold_bms.adapter.GetSurgeryAdapter
import com.bms.pathogold_bms.model.AllergyRequestBO
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.HistoryVitalRequestBO
import com.bms.pathogold_bms.model.emrcheckboxlist.GetEMRCheckBoxListBO
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListBO
import com.bms.pathogold_bms.model.getcomplaintsallergies.GetComplaintsAllergiesBO
import com.bms.pathogold_bms.model.getcomplaintsallergies.GetComplaintsAllergiesResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getpatientvital.GetPatientVitalBO
import com.bms.pathogold_bms.model.getpatientvital.GetPatientVitalResponse
import com.bms.pathogold_bms.model.vitalsurgerlylist.VitalSurgeryListBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.paperdb.Paper
import java.util.*
import kotlin.collections.ArrayList

@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class VitalsFragment : BaseFragment(), View.OnClickListener {

    //Declaration...
    private val TAG = "VitalsActivity"

    //Toolbar declaration...
    private var toolbar: Toolbar? = null

    //Circular Image View declaration..
    private var civPatientPic: CircleImageView? = null

    //TextView declaration...
    private var tvPatientName: TextView? = null

    //Edittext declaration..
    private var etPulse: EditText? = null
    private var etSpo2: EditText? = null
    private var etSystolicBP: EditText? = null
    private var etDiastolicBP: EditText? = null
    private var etTemp: EditText? = null
    private var etRespiration: EditText? = null
    private var etHeight: EditText? = null
    private var etWeight: EditText? = null
    private var etBloodSugar: EditText? = null
    private var etSelectSurgery: EditText? = null
    private var etSurgeryDate: EditText? = null
    private var etEnvironmentalAllergy: EditText? = null
    private var etFoodALlergy: EditText? = null
    private var etDrugAllergy: EditText? = null
    private var etSearchSurgery: EditText? = null

    //AutoCompleteTextView declaration...
    private var actBloodGroup: MaterialAutoCompleteTextView? = null

    //Recyclerview declaration...
    private var rvGetPatientHistory: RecyclerView? = null
    private var rvSurgeryList: RecyclerView? = null

    //AppCompatButton declaration
    private var btnSave: AppCompatButton? = null
    private var btnCancel: AppCompatButton? = null

    private var getPatientListBO: GetPatientListBO? = null
    private val bloodgroup = arrayOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    private var getEMRCheckBoxMap: MutableMap<String, ArrayList<GetEMRCheckBoxListBO>> = HashMap()
    private var getAllergyListMap: MutableMap<String, ArrayList<GetAllergyListBO>> = HashMap()
    private var vitalSurgeryListBOArrayList = ArrayList<VitalSurgeryListBO>()
    private var getSurgeryAdapter:GetSurgeryAdapter?=null

    private var getEMRCheckBoxList=ArrayList<GetEMRCheckBoxListBO>()

    private var allergyID=ArrayList<String>()
    private var allergyName=ArrayList<String>()
    private var allergyCatID=ArrayList<String>()
    private var patientHistoryId=ArrayList<String>()
    private var patientHistoryName=ArrayList<String>()
    private var temp1: ArrayList<GetEMRCheckBoxListBO>? = null
    private var getEMRCheckBoxListMain=ArrayList<GetEMRCheckBoxListBO>()

    //to show only envionementallergylist in edittext
    private val environmentAllergyList=ArrayList<String>()
    //to show only foodAllergyList in edittext of food allergy
    private val foodAllergyList=ArrayList<String>()
    //to show on drugallergylist in ediitext of drugallergy
    private val drugAllergyList=ArrayList<String>()

    private val ENVIRONMENTAL_ALLERGY=1
    private val FOOD_ALLERGY=2
    private val DRUG_ALLERGY=3

    override val activityLayout: Int
        get() = R.layout.fragment_vitals

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable...
        val arguments = requireArguments()
        getPatientListBO = arguments.getParcelable("patient_bo")

        //Get all data which is hit in api by Dashboard Activity...which is stored in paperDB
        getEMRCheckBoxListMain = Paper.book().read("emr_checkbox_master")
        getAllergyListMap = Paper.book().read("allergy_master")
        vitalSurgeryListBOArrayList = Paper.book().read("surgery_master")

        Log.e(TAG, "onCreate getEMRCheckBoxMap: ${getEMRCheckBoxMap.size}")
        Log.e(TAG, "onCreate: getAllergyListMap ${getAllergyListMap.size}")
        Log.e(TAG, "onCreate: vitalSurgerList ${vitalSurgeryListBOArrayList.size}")

        //basic initialisation..
        initViews(view)

        //setDataToAdapter...
        setDataToAdapter()

        //IF already vital is saved then data will be pre-filled...
        if(CommonMethods.isNetworkAvailable(mContext!!)){
            //getPatient vital details last inserted..
            getPatientVitalDetails()

            //getPatient compalints, allergies examination etc
            getComplaintsExaminationHistoryAllergiesVitalPatientDetails()
        }else{
            CommonMethods.showDialogForError(requireActivity(),AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews(view: View) {
        /*//toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = resources.getString(R.string.vital)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)*/
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.vital)

        //Circular ImageView binding
        civPatientPic = view.findViewById(R.id.civ_patient_pic)

        //TextView binding...
        tvPatientName = view.findViewById(R.id.tv_patient_name)
        tvPatientName?.text = getPatientListBO?.PatientName

        //EditText binding...
        etPulse = view.findViewById(R.id.et_pulse)
        etSpo2 = view.findViewById(R.id.et_spo2)
        etSystolicBP = view.findViewById(R.id.et_systolic_bp)
        etDiastolicBP = view.findViewById(R.id.et_diastolic_bp)
        etTemp = view.findViewById(R.id.et_temp)
        etRespiration = view.findViewById(R.id.et_respiration)
        etHeight = view.findViewById(R.id.et_height)
        etWeight = view.findViewById(R.id.et_weight)
        etBloodSugar = view.findViewById(R.id.et_blood_sugar)
        etSelectSurgery = view.findViewById(R.id.et_select_surgery)
        etSurgeryDate = view.findViewById(R.id.et_select_surgery_date)
        etEnvironmentalAllergy = view.findViewById(R.id.et_environmental_allergy)
        etFoodALlergy = view.findViewById(R.id.et_food_allergy)
        etDrugAllergy = view.findViewById(R.id.et_drug_allergy)
        etSearchSurgery=view.findViewById(R.id.et_search_surgery)

        //AutoCompleteTextView declaration...
        actBloodGroup = view.findViewById(R.id.act_blood_group)

        //RecyclerView declaration...
        rvGetPatientHistory = view.findViewById(R.id.rv_get_patient_history)
        val lm: LinearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvGetPatientHistory?.layoutManager = lm

        rvSurgeryList = view.findViewById(R.id.rv_surgery)
        val lm1 = LinearLayoutManager(mContext)
        rvSurgeryList?.layoutManager = lm1

        //AppCompatButton declaration..
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)

        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, bloodgroup)
        actBloodGroup?.setAdapter(adapter)
        actBloodGroup?.keyListener = null
        actBloodGroup?.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }

        //ClickListeners..
        clickListeners()

        etSearchSurgery?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })

        //if Patient login then disable all fields of vitals
        if(CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Patient)){
            disableFieldsForVitals()
        }
    }

    private fun clickListeners() {
        btnSave?.setOnClickListener(this)
        btnCancel?.setOnClickListener(this)
        etSelectSurgery?.setOnClickListener(this)
        etSurgeryDate?.setOnClickListener(this)
        etEnvironmentalAllergy?.setOnClickListener(this)
        etFoodALlergy?.setOnClickListener(this)
        etDrugAllergy?.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.et_select_surgery -> {
                if(rvSurgeryList?.visibility==View.VISIBLE && etSearchSurgery?.visibility==View.VISIBLE){
                    rvSurgeryList?.visibility=View.GONE
                    etSearchSurgery?.visibility=View.GONE
                }else{
                    rvSurgeryList?.visibility=View.VISIBLE
                    etSearchSurgery?.visibility=View.VISIBLE
                }
            }

            R.id.et_select_surgery_date -> {
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->
                        etSurgeryDate?.setText("" +String.format("%02d",(monthOfYear + 1)) + "/"+ year)
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.et_environmental_allergy -> {
                val getAllergyDialog = GetAllergyDialog()
                val bundle = Bundle()
                bundle.putParcelableArrayList("allergy_list", getAllergyListMap["1"])
                getAllergyDialog.arguments = bundle
                getAllergyDialog.setTargetFragment(this, ENVIRONMENTAL_ALLERGY)
                getAllergyDialog.show(fragmentManager?.beginTransaction()!!, "ENVIRONMENTAL_ALLERGY")
            }

            R.id.et_food_allergy -> {
                val getAllergyDialog = GetAllergyDialog()
                val bundle = Bundle()
                bundle.putParcelableArrayList("allergy_list", getAllergyListMap["2"])
                getAllergyDialog.arguments = bundle
                getAllergyDialog.setTargetFragment(this, FOOD_ALLERGY)
                getAllergyDialog.show(fragmentManager?.beginTransaction()!!, "FOOD_ALLERGY")
            }

            R.id.et_drug_allergy -> {
                val getAllergyDialog = GetAllergyDialog()
                val bundle = Bundle()
                bundle.putParcelableArrayList("allergy_list", getAllergyListMap["3"])
                getAllergyDialog.arguments = bundle
                getAllergyDialog.setTargetFragment(this, DRUG_ALLERGY)
                getAllergyDialog.show(fragmentManager?.beginTransaction()!!, "DRUG_ALLERGY")
            }

            R.id.btn_save -> {
                //if (isValidated()) {
                    saveVital()
               //}
            }

            R.id.btn_cancel -> {
                (context as DashBoardActivity).navController.popBackStack(R.id.vitalsFragment, true)
            }
        }
    }

    private fun saveVital(){
        val params: MutableMap<String, String> = HashMap()
        params["PepatId"] =getPatientListBO?.PePatID.toString()
        params["OpdNo"] =getPatientListBO?.regno.toString()
        params["Pulse"] =etPulse?.text.toString()
        params["SPO2"] =etSpo2?.text.toString()
        params["SysBP"] =etSystolicBP?.text.toString()
        params["DiaBP"] =etDiastolicBP?.text.toString()
        params["Temp"] =etTemp?.text.toString()
        params["Resp"] =etRespiration?.text.toString()
        params["Ht"] =etHeight?.text.toString()
        params["Wt"] =etWeight?.text.toString()
        params["BMI"] ="0"
        if(etSelectSurgery?.text.toString().isEmpty()){
            params["Surgery"] =""
        }else{
            params["Surgery"] =etSelectSurgery?.text.toString()
        }
        if(etSurgeryDate?.text.toString().isEmpty()){
            params["DateOfSurgery"] ="01/01/1900"
        }else{
            params["DateOfSurgery"] =etSurgeryDate?.text.toString()
        }
        params["Username"] =CommonMethods.getPrefrence(mContext!!,AllKeys.USER_NAME).toString()
        params["CompanyId"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["BloodGroup"] =actBloodGroup?.text.toString()
        params["Blood_Sugar"] =etBloodSugar?.text.toString()
        params["VisitType"] =""

        Log.e(TAG, "saveVital: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Saving vital...Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertVitalDetailsofpatient(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {

                        //insertVitalAllergy...
                         if(allergyName.size>0){
                             insertVitalAllergy()
                         }

                        //insert Paitent history...
                        insertPatientVitalHistory()

                        CommonMethods.showDialogForSuccess(activity!!, commonResponse.Message)
                    } else {
                        CommonMethods.showDialogForError(activity!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun insertPatientVitalHistory(){
        //first get all data in arraylist
        for (key in getEMRCheckBoxMap.keys.toTypedArray()) {
            // println("key : $key")
            //Log.e(TAG, "onCreate: "+ getEMRCheckBoxMap[key])
            getEMRCheckBoxMap[key]?.let { getEMRCheckBoxList.addAll(it) }
        }

        for(temp in getEMRCheckBoxList){
            if(temp.getSelected()){
                patientHistoryId.add(temp.HistoryId.toString())
                patientHistoryName.add(temp.HistoryName.toString())
            }
        }

        val historyVitalRequestBOList=ArrayList<HistoryVitalRequestBO>()
        var historyVitalRequestBO=HistoryVitalRequestBO()
        for (i in 0 until patientHistoryName.size) {
            historyVitalRequestBO.setPepatId(getPatientListBO?.PePatID.toString())
            historyVitalRequestBO.setOpdNo(getPatientListBO?.regno.toString())
            historyVitalRequestBO.setCompanyid(mContext?.let { CommonMethods.getPrefrence(it,AllKeys.COMPANY_ID) })
            historyVitalRequestBO.setHistoryName(patientHistoryName[i].replace("[", "").replace("]", ""))
            historyVitalRequestBO.setHistCatId(patientHistoryId[i].replace("[", "").replace("]", ""))

            historyVitalRequestBOList.add(historyVitalRequestBO)
            //reintialise the object
            historyVitalRequestBO=HistoryVitalRequestBO()
        }

        val data = Gson().toJson(historyVitalRequestBOList)

        val params: MutableMap<String, String> = HashMap()
        params["Jsonarray"] =data

        Log.e(TAG, "insertPatientHistory: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Saving vital...Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertVitalHistory(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val commonResponse = `object` as CommonResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $commonResponse")
                if (commonResponse.ResponseCode == 200) {
                    Toast.makeText(mContext, commonResponse.Message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, commonResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                // showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun insertVitalAllergy(){
        val allergyRequestBOList=ArrayList<AllergyRequestBO>()
        var allergyRequestBO=AllergyRequestBO()
        for (i in 0 until allergyID.size) {
            allergyRequestBO.setPepatId(getPatientListBO?.PePatID.toString())
            allergyRequestBO.setOpdNo(getPatientListBO?.regno.toString())
            allergyRequestBO.setCompanyid(mContext?.let { CommonMethods.getPrefrence(it,AllKeys.COMPANY_ID) })
            allergyRequestBO.setAllergyId(allergyID[i].replace("[", "").replace("]", ""))
            allergyRequestBO.setAllergyName(allergyName[i].replace("[", "").replace("]", ""))
            allergyRequestBO.setAllergyCatId(allergyCatID[i].replace("[", "").replace("]", ""))
            allergyRequestBOList.add(allergyRequestBO)
            //reintialise the object
            allergyRequestBO=AllergyRequestBO()
        }

        val data = Gson().toJson(allergyRequestBOList)

        val params: MutableMap<String, String> = HashMap()
        params["Jsonarray"] =data

        Log.e(TAG, "insertVitalAllergy: $params")
        val progress = ProgressDialog(activity)
        progress.setMessage("Saving vital...Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertVitalAllergies(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val commonResponse = `object` as CommonResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $commonResponse")
                if (commonResponse.ResponseCode == 200) {
                    Toast.makeText(mContext, commonResponse.Message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, commonResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                // showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun getPatientVitalDetails(){
        val params: MutableMap<String, String> = HashMap()
        params["PepatId"] =getPatientListBO?.PePatID.toString()
        params["OpdNo"] =getPatientListBO?.regno.toString()
        params["Companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getPatientVitalDetails: $params")
        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getPatientVitalDetails(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val getPatientVitalResponse = `object` as GetPatientVitalResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $getPatientVitalResponse")
                if (getPatientVitalResponse.ResponseCode == 200) {
                    //setAlreadyPatientVital details..
                    setPatientVitalData(getPatientVitalResponse.ResultArray[0])
                    Toast.makeText(mContext, getPatientVitalResponse.Message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, getPatientVitalResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                // showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun getComplaintsExaminationHistoryAllergiesVitalPatientDetails(){
        val params: MutableMap<String, String> = HashMap()
        params["PepatId"] =getPatientListBO?.PePatID.toString()
        params["OpdNo"] =getPatientListBO?.regno.toString()
        params["Companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getComplaintsExaminationHistoryAllergiesVitalPatientDetails: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getComplaintsExaminationHistoryAllergiesVitalPatientDetails(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val getComplaintsAllergiesResponse = `object` as GetComplaintsAllergiesResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $getComplaintsAllergiesResponse")
                if (getComplaintsAllergiesResponse.ResponseCode == 200) {
                    for(i in 0 until getComplaintsAllergiesResponse.ResultArray.size){
                        if(getComplaintsAllergiesResponse.ResultArray[i].AllergyName != ""){
                            allergyID.add(getComplaintsAllergiesResponse.ResultArray[i].AllergyId)
                            allergyName.add(getComplaintsAllergiesResponse.ResultArray[i].AllergyName)
                            allergyCatID.add(getComplaintsAllergiesResponse.ResultArray[i].AllergyCategoryId)

                            when (getComplaintsAllergiesResponse.ResultArray[i].AllergyCategoryId) {
                                "1" -> {
                                    environmentAllergyList.add(getComplaintsAllergiesResponse.ResultArray[i].AllergyName)
                                }
                                "2" -> {
                                    foodAllergyList.add(getComplaintsAllergiesResponse.ResultArray[i].AllergyName)
                                }
                                "3" -> {
                                    drugAllergyList.add(getComplaintsAllergiesResponse.ResultArray[i].AllergyName)
                                }
                            }
                        }
                    }

                    //setSelectedComplaints,examination
                    getSelectedComlaintsHistory(getComplaintsAllergiesResponse.ResultArray)

                    etEnvironmentalAllergy?.setText(environmentAllergyList.toString().replace("[", "").replace("]", ""))
                    etFoodALlergy?.setText(foodAllergyList.toString().replace("[", "").replace("]", ""))
                    etDrugAllergy?.setText(drugAllergyList.toString().replace("[", "").replace("]", ""))

                    Toast.makeText(mContext, getComplaintsAllergiesResponse.Message, Toast.LENGTH_SHORT).show()
                } else {
                    //Set default emr checklist then..
                    setEmrCheckListData()
                    Toast.makeText(mContext, getComplaintsAllergiesResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                // showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun getSelectedComlaintsHistory(resultArray: ArrayList<GetComplaintsAllergiesBO>) {
        for(i in 0 until getEMRCheckBoxListMain.size){
            for(j in 0 until resultArray.size){
                if(resultArray[j].HistoryId!="" && getEMRCheckBoxListMain[i].getHistoryId().equals(resultArray[j].HistoryId)){
                    val getEMRCheckBoxListBO=GetEMRCheckBoxListBO()
                    getEMRCheckBoxListBO.setHistoryId(resultArray[j].HistoryId)
                    getEMRCheckBoxListBO.setHistoryName(resultArray[j].HistoryName)
                    getEMRCheckBoxListBO.setHistoryCategory(resultArray[j].HistoryCategory)
                    getEMRCheckBoxListBO.setSelected(true)
                    getEMRCheckBoxListMain[i] = getEMRCheckBoxListBO
                }
            }
        }
        setEmrCheckListData()
    }

    private fun setPatientVitalData(getPatientVitalBO: GetPatientVitalBO){
        etPulse?.setText(getPatientVitalBO.Pulse)
        etSpo2?.setText(getPatientVitalBO.SPO2)
        etSystolicBP?.setText(getPatientVitalBO.SysBP)
        etDiastolicBP?.setText(getPatientVitalBO.DiaBP)
        etTemp?.setText(getPatientVitalBO.Temp)
        etRespiration?.setText(getPatientVitalBO.Respiration)
        etHeight?.setText(getPatientVitalBO.Height)
        etWeight?.setText(getPatientVitalBO.Weight)
        actBloodGroup?.setText(getPatientVitalBO.BloodGroup)
        etBloodSugar?.setText(getPatientVitalBO.BloodSugar)
        //etSelectSurgery?.setText(getPatientVitalBO.Surgery)
        //etSurgeryDate?.setText(getPatientVitalBO.DateOfSurgery)
    }

    fun setDataToSurgeryEditText(vitalSurgeryListBO: VitalSurgeryListBO){
        etSelectSurgery?.setText(vitalSurgeryListBO.SurgeryName)
        rvSurgeryList?.visibility=View.GONE
        etSearchSurgery?.visibility=View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var getAllergyListBO:GetAllergyListBO?=null

        if (resultCode == Activity.RESULT_OK) {
            val bundle = data?.extras
            getAllergyListBO = bundle?.getParcelable("allergy_bo")!!
        }

        when(requestCode){
            ENVIRONMENTAL_ALLERGY->{
                    if(!allergyID.contains(getAllergyListBO?.AllergyId)){
                        allergyID.add(getAllergyListBO!!.AllergyId)
                        allergyName.add(getAllergyListBO.AllergyName)
                        allergyCatID.add(getAllergyListBO.AllergyCategoryId)

                        environmentAllergyList.add(getAllergyListBO.AllergyName)
                    }
                    etEnvironmentalAllergy?.setText(environmentAllergyList.toString().replace("[", "").replace("]", ""))
            }

            FOOD_ALLERGY->{
                if(!allergyID.contains(getAllergyListBO?.AllergyId)){
                    allergyID.add(getAllergyListBO!!.AllergyId)
                    allergyName.add(getAllergyListBO.AllergyName)
                    allergyCatID.add(getAllergyListBO.AllergyCategoryId)

                    foodAllergyList.add(getAllergyListBO.AllergyName)
                }

                etFoodALlergy?.setText(foodAllergyList.toString().replace("[", "").replace("]", ""))
            }

            DRUG_ALLERGY->{
                if(!allergyID.contains(getAllergyListBO?.AllergyId)){
                    allergyID.add(getAllergyListBO!!.AllergyId)
                    allergyName.add(getAllergyListBO.AllergyName)
                    allergyCatID.add(getAllergyListBO.AllergyCategoryId)

                    drugAllergyList.add(getAllergyListBO.AllergyName)
                }

                etDrugAllergy?.setText(drugAllergyList.toString().replace("[", "").replace("]", ""))
            }
        }
    }

    private fun setDataToAdapter(){
        if(vitalSurgeryListBOArrayList.isNotEmpty()){
            getSurgeryAdapter=GetSurgeryAdapter(mContext!!,vitalSurgeryListBOArrayList,this)
            rvSurgeryList?.adapter=getSurgeryAdapter
        }
    }
    
    private fun setEmrCheckListData(){
        for (temp in getEMRCheckBoxListMain) {
            if (getEMRCheckBoxMap.containsKey(temp.HistoryCategory!!.trim())) {
                temp1 = getEMRCheckBoxMap[temp.HistoryCategory!!.trim()]
                temp1!!.add(temp)
            } else {
                temp1 = ArrayList()
                temp1!!.add(temp)
                getEMRCheckBoxMap[temp.HistoryCategory!!.trim()] = temp1!!
            }
        }

        if(getEMRCheckBoxMap.isNotEmpty()){
            val getEMRCheckBoxAdapter=GetEMRCheckBoxAdapter(mContext,getEMRCheckBoxMap.keys.toTypedArray(),getEMRCheckBoxMap,this)
            rvGetPatientHistory?.adapter=getEMRCheckBoxAdapter
        }
    }

    private fun filterTable(text: String) {
        if (vitalSurgeryListBOArrayList.size > 0) {
            val filteredList1: java.util.ArrayList<VitalSurgeryListBO> = java.util.ArrayList<VitalSurgeryListBO>()
            for (item in vitalSurgeryListBOArrayList) {
                if (text.let { item.SurgeryName.lowercase(Locale.ROOT).contains(it) }
                    || text.let { item.SurgeryName.uppercase(Locale.ROOT).contains(it) }
                ) {
                    filteredList1.add(item)
                }
            }
            getSurgeryAdapter?.updateData(mContext, filteredList1)
        }
    }

    private fun disableFieldsForVitals() {
        etPulse?.isEnabled=false
        etSpo2?.isEnabled=false
        etSystolicBP?.isEnabled=false
        etDiastolicBP?.isEnabled=false
        etTemp?.isEnabled=false
        etRespiration?.isEnabled=false
        etHeight?.isEnabled=false
        etWeight?.isEnabled=false
        actBloodGroup?.isEnabled=false
        actBloodGroup?.isClickable=false
        etBloodSugar?.isEnabled=false
        etSelectSurgery?.isEnabled=false
        etSelectSurgery?.isClickable=false
        etSurgeryDate?.isEnabled=false
        etSurgeryDate?.isClickable=false
        etEnvironmentalAllergy?.isClickable=false
        etFoodALlergy?.isClickable=false
        etDrugAllergy?.isClickable=false
        btnSave?.isClickable=false
    }

    private fun isValidated(): Boolean {

        if (etPulse?.text.toString().isEmpty()) {
            etPulse?.error = "Pulse required!"
            etPulse?.requestFocus()
            return false
        }

        if (etSpo2?.text.toString().isEmpty()) {
            etSpo2?.error = "Spo2 required!"
            etSpo2?.requestFocus()
            return false
        }

        if (etSystolicBP?.text.toString().isEmpty()) {
            etSystolicBP?.error = "SystolicBP required!"
            etSystolicBP?.requestFocus()
            return false
        }

        if (etDiastolicBP?.text.toString().isEmpty()) {
            etDiastolicBP?.error = "DiastolicBP required!"
            etDiastolicBP?.requestFocus()
            return false
        }

        if (etTemp?.text.toString().isEmpty()) {
            etTemp?.error = "Temp. required!"
            etTemp?.requestFocus()
            return false
        }

        if (etRespiration?.text.toString().isEmpty()) {
            etRespiration?.error = "Respiration required!"
            etRespiration?.requestFocus()
            return false
        }

        if (etHeight?.text.toString().isEmpty()) {
            etHeight?.error = "Height required!"
            etHeight?.requestFocus()
            return false
        }

        if (etWeight?.text.toString().isEmpty()) {
            etWeight?.error = "Weight required!"
            etWeight?.requestFocus()
            return false
        }

        if (actBloodGroup?.text.toString().isEmpty()) {
            actBloodGroup?.error = "Blood Group required!"
            actBloodGroup?.requestFocus()
            return false
        } else {
            actBloodGroup?.setError("", null)
        }

        /*  if (etBloodSugar?.text.toString().isEmpty()) {
              etBloodSugar?.error = "Blood Sugar required!"
              etBloodSugar?.requestFocus()
              return false
          }

          if (etSelectSurgery?.text.toString().isEmpty()) {
              etSelectSurgery?.error = "Select Surgery!"
              etSelectSurgery?.requestFocus()
              return false
          } else {
              etSelectSurgery?.setError("", null)
          }

          if (etSurgeryDate?.text.toString().isEmpty()) {
              etSurgeryDate?.error = "Select Surgery Date!"
              etSurgeryDate?.requestFocus()
              return false
          } else {
              etSurgeryDate?.setError("", null)
          }

          if (etEnvironmentalAllergy?.text.toString().isEmpty()) {
              etEnvironmentalAllergy?.error = "Select Environmental Allergy!"
              etEnvironmentalAllergy?.requestFocus()
              return false
          } else {
              etEnvironmentalAllergy?.setError("", null)
          }

          if (etFoodALlergy?.text.toString().isEmpty()) {
              etFoodALlergy?.error = "Select Food Allergy!"
              etFoodALlergy?.requestFocus()
              return false
          } else {
              etFoodALlergy?.setError("", null)
          }

          if (etDrugAllergy?.text.toString().isEmpty()) {
              etDrugAllergy?.error = "Select Drug Allergy!"
              etDrugAllergy?.requestFocus()
              return false
          } else {
              etDrugAllergy?.setError("", null)
          }*/

        return true
    }
}