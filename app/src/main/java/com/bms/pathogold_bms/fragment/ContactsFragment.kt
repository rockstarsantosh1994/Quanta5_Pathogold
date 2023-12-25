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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.activity.SpecificChatActivity
import com.bms.pathogold_bms.adapter.ContactsAdapter
import com.bms.pathogold_bms.adapter.ContactsPatientAdapter
import com.bms.pathogold_bms.model.chat.FireBaseBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import io.paperdb.Paper
import java.lang.Exception
import kotlin.collections.ArrayList

class  ContactsFragment : BaseFragment(), View.OnClickListener, ContactsAdapter.GetContactDetails,
    ContactsPatientAdapter.GetPatientContactDetails {

    private val TAG = "ContactsFragment"

    //Editext declaration..
    var etSearch: EditText? = null

    //Radio Group and button declaration..
    private var radioGroup: RadioGroup? = null
    private var rbDiagnostic: RadioButton? = null
    private var rbConsultaion: RadioButton? = null
    private var rbPatient: RadioButton? = null

    //TextInputLayout declaration..
    private var tilDate: TextInputLayout? = null

    //TextInputEditText declaration..
    private var etDate: TextInputEditText? = null

    //RecyclerView declaration..
    private var rvRecyclerView: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    //LinearLayout declaration..
    private var llRecyclerView: LinearLayout? = null
    private var llPatientDateEditText: LinearLayout? = null

    //String declaration...
    private var stUserType: String = "Patients"

    //ArrayList declaration..
    private var consultationBOArrayList = ArrayList<ConsultationBO>()
    private var pheloboBOArrayList = ArrayList<ConsultationBO>()
    private val getPatientListBOArrayList = ArrayList<GetPatientListBO>()

    //Adapters declaration..
    private var phelboContactsAdapter: ContactsAdapter? = null
    private var consultationContactsAdapter: ContactsAdapter? = null
    private var contactsPatientAdapter: ContactsPatientAdapter? = null

    //Chat FireBase declaration...
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var firebaseFirestore: FirebaseFirestore? = null

    //ArrayList declaration...
    private var chatFireBaseBOList = ArrayList<FireBaseBO>()

    override fun onStart() {
        super.onStart()
        //getData from Bundle which was send from PagerAdapter...
        val arguments = requireArguments()
        consultationBOArrayList = arguments.getParcelableArrayList("consultation_list")!!
        pheloboBOArrayList = arguments.getParcelableArrayList("phlebo_list")!!

        Log.e(TAG, "onStart: pheloboBOArrayList " + pheloboBOArrayList.size)
        Log.e(TAG, "onStart: consultationBOArrayList  " + consultationBOArrayList.size)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Paper.init(mContext)
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage?.reference
        firebaseFirestore = FirebaseFirestore.getInstance()

        try {
            chatFireBaseBOList = Paper.book().read("chat_db")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //basic intialisation...
        initViews(view)

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Patient)) {
            rbPatient?.visibility = View.GONE
            llPatientDateEditText?.visibility = View.GONE
            radioGroup?.check(R.id.rb_diagnostics)
            stUserType = "Diagnostics"
            //On select of diagnostics, and consultation..
            onSelectOfRadioButton(stUserType)

        } else {
            rbPatient?.visibility = View.VISIBLE
            llPatientDateEditText?.visibility = View.VISIBLE
            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                val yearFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy")
                val calYear = Calendar.getInstance()
                val todayDate = yearFormat.format(calYear.time)
                radioGroup?.check(R.id.rb_patient)
                etDate?.setText(todayDate)

                getPatientList(todayDate,
                    CommonMethods.getPrefrence(mContext!!, AllKeys.MOBILE_NO).toString())
            } else {
                CommonMethods.showDialogForError(mContext as Activity,
                    AllKeys.NO_INTERNET_AVAILABLE)
            }
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_contacts

    private fun initViews(view: View) {
        //Editext binding...
        etSearch = view.findViewById(R.id.et_search)

        //TextInputLayout binding...
        tilDate = view.findViewById(R.id.til_date)

        //TextInputEditText binding..
        etDate = view.findViewById(R.id.et_date)

        //RadioGroup binding...
        radioGroup = view.findViewById(R.id.radio_group)

        //RadioButton binding..
        rbDiagnostic = view.findViewById(R.id.rb_diagnostics)
        rbConsultaion = view.findViewById(R.id.rb_consultation)
        rbPatient = view.findViewById(R.id.rb_patient)

        //TextView binding...
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //LinearLayout binding..
        llRecyclerView = view.findViewById(R.id.ll_recycler_view)
        llPatientDateEditText = view.findViewById(R.id.ll_patient_date)

        //RecycleView binding..
        rvRecyclerView = view.findViewById(R.id.rv_patient_diagnos_consult)
        val linearLayoutManager = LinearLayoutManager(mContext)
        rvRecyclerView?.layoutManager = linearLayoutManager

        //Click listeners
        etDate?.setOnClickListener(this)

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            stUserType = (radioGroup?.checkedRadioButtonId?.let { view.findViewById<View>(it) } as RadioButton).text.toString()

            //On select of diagnostics, and consultation..
            onSelectOfRadioButton(stUserType)
        }

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            //Date picker...
            R.id.et_date -> {
                //10/29/2020 MM/dd/yyyy
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(mContext!!,
                    { view, year, monthOfYear, dayOfMonth ->
                        //  etDate?.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                        etDate?.setText("" + (monthOfYear + 1) + "/" + dayOfMonth.toString() + "/" + year)

                        if (CommonMethods.isNetworkAvailable(mContext!!)) {
                            getPatientList(etDate?.text.toString(),
                                CommonMethods.getPrefrence(mContext!!, AllKeys.MOBILE_NO)
                                    .toString())
                        } else {
                            CommonMethods.showDialogForError(mContext as Activity,
                                AllKeys.NO_INTERNET_AVAILABLE)
                        }
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.show()
            }
        }
    }

    private fun onSelectOfRadioButton(stUserType: String) {
        Log.e(TAG, "onSelectOfRadioButton: $stUserType")
        if (stUserType.equals("Consultation", false)) {
            etSearch?.hint = resources.getString(R.string.search_consultation)
            llPatientDateEditText?.visibility = View.GONE
            if (consultationBOArrayList.size > 0) {
                showRecyclerView()
                consultationContactsAdapter =
                    ContactsAdapter(mContext!!, consultationBOArrayList, this)
                rvRecyclerView?.adapter = consultationContactsAdapter
            } else {
                hideRecyclerView()
            }

        } else if (stUserType.equals("Diagnostics", false)) {
            etSearch?.hint = resources.getString(R.string.search_phelobo)
            llPatientDateEditText?.visibility = View.GONE
            if (pheloboBOArrayList.size > 0) {
                showRecyclerView()
                phelboContactsAdapter = ContactsAdapter(mContext!!, pheloboBOArrayList, this)
                rvRecyclerView?.adapter = phelboContactsAdapter
            } else {
                hideRecyclerView()
            }

        } else if (stUserType.equals("Patients", false)) {
            etSearch?.hint = resources.getString(R.string.search_patient)
            llPatientDateEditText?.visibility = View.VISIBLE
            if (getPatientListBOArrayList.size > 0) {
                showRecyclerView()
                contactsPatientAdapter =
                    ContactsPatientAdapter(mContext!!, getPatientListBOArrayList, this)
                rvRecyclerView?.adapter = contactsPatientAdapter

            } else {
                /*llRecyclerView?.visibility = View.GONE
                tvNoDataFound?.visibility = View.VISIBLE*/
                hideRecyclerView()
            }
        }
    }

    private fun getPatientList(stDate: String, stMobileNo: String) {
        val params: MutableMap<String, String> = HashMap()
        params["fromdate"] = stDate
        params["todate"] = stDate
        params["LabCode"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
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
                            showRecyclerView()
                            //append to recycler view..
                            appendRecyclerView(getPatientListBOArrayList)
                        } else {
                            hideRecyclerView()
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

    private fun appendRecyclerView(patientListBOArrayList: ArrayList<GetPatientListBO>) {
        contactsPatientAdapter = ContactsPatientAdapter(mContext!!, patientListBOArrayList, this)
        rvRecyclerView?.adapter = contactsPatientAdapter
    }

    private fun hideRecyclerView() {
        tvNoDataFound?.visibility = View.VISIBLE
        rvRecyclerView?.visibility = View.GONE
        etSearch?.visibility = View.GONE
    }

    private fun showRecyclerView() {
        rvRecyclerView?.visibility = View.VISIBLE
        etSearch?.visibility = View.VISIBLE
        tvNoDataFound?.visibility = View.GONE
    }

    fun filterTable(text: String?) {
        if (stUserType.equals("Diagnostics", false)) {
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
                phelboContactsAdapter?.updateData(mContext, filteredList1)
            }
        } else if (stUserType.equals("Consultation", false)) {
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
                consultationContactsAdapter?.updateData(mContext, filteredList1)
            }

        } else if (stUserType.equals("Patient", false)) {
            if (getPatientListBOArrayList.size > 0) {
                val filteredList1: ArrayList<GetPatientListBO> = ArrayList<GetPatientListBO>()
                for (item in getPatientListBOArrayList) {
                    if (text?.let { item.PatientName.lowercase(Locale.ROOT).contains(it) } == true
                        || text?.let {
                            item.PatientPhoneNo.uppercase(Locale.ROOT).contains(it)
                        } == true
                        || text?.let { item.Pno.uppercase(Locale.ROOT).contains(it) } == true
                    ) {
                        filteredList1.add(item)
                    }
                }
                //Log.e(TAG, "filter: size" + filteredList1.size());
                // Log.e(TAG, "filter: List" + filteredList1.toString());
                contactsPatientAdapter?.updateDataForPatient(mContext, filteredList1)
            }
        }
    }

    override fun getContactDetails(consultationBO: ConsultationBO) {
        if(consultationBO.token.isEmpty() || consultationBO.token == AllKeys.DNF){
            CommonMethods.showDialogForError(mContext!!,"Dr.${consultationBO.Name } is not available for chat")
        }else{
            val fireBaseBO = FireBaseBO(
                consultationBO.Name,
                "",
                consultationBO.Pno,
                "Offline",
                consultationBO.token,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                consultationBO.token,
                "none")

            //Store user recent chat to in database to show recent chat as per user
            sendDataToCloudStore(fireBaseBO)

            val intent = Intent(activity, SpecificChatActivity::class.java)
            intent.putExtra("name", consultationBO.Name)
            intent.putExtra("receiveruid", consultationBO.Pno)
            intent.putExtra("imageuri", "")
            intent.putExtra("token", consultationBO.token)
            intent.putExtra("firebase_bo",fireBaseBO)
            startActivity(intent)
        }
    }

    override fun getPatientContactDetails(getPatientListBO: GetPatientListBO) {
        Log.e(TAG, "getPatientContactDetails: $getPatientListBO")
        if(getPatientListBO.Token.isEmpty() || getPatientListBO.Token==AllKeys.DNF){
            CommonMethods.showDialogForError(mContext!!,"${getPatientListBO.PatientName } is not available for chat")
        }else{
            val fireBaseBO = FireBaseBO(
                getPatientListBO.PatientName,
                "",
                getPatientListBO.PePatID,
                "Offline",
                getPatientListBO.Token,
                getPatientListBO.regno,
                getPatientListBO.PatientName,
                getPatientListBO.UserName,
                getPatientListBO.Pno,
                getPatientListBO.age,
                getPatientListBO.MDY,
                getPatientListBO.sex,
                getPatientListBO.PatientPhoneNo,
                getPatientListBO.Dr_name,
                getPatientListBO.PePatID,
                getPatientListBO.Samplestatus,
                getPatientListBO.Token,
                AllKeys.Patient,
            )

            //Store user recent chat to in database to show recent chat as per user
            sendDataToCloudStore(fireBaseBO)

            val intent = Intent(context, SpecificChatActivity::class.java)
            intent.putExtra("name", getPatientListBO.PatientName)
            intent.putExtra("receiveruid", getPatientListBO.PePatID)
            intent.putExtra("imageuri", "")
            intent.putExtra("token", getPatientListBO.Token)
            intent.putExtra("firebase_bo",fireBaseBO)
            startActivity(intent)
        }
    }

    private fun sendDataToCloudStore(firebaseBO: FireBaseBO) {
        //first insert in our recent chat to show list in Chat Fragment....
        val documentReference: DocumentReference =
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
                firebaseFirestore?.collection("Users")!!
                    .document(CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID)
                        .toString())
            } else {
                firebaseFirestore?.collection("Users")!!
                    .document(CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString())
            }
        val params: MutableMap<String, Any> = HashMap()

        params[AllKeys.recent_chat] = FieldValue.arrayUnion(firebaseBO)

        documentReference.set(params).addOnSuccessListener {
            // Toast.makeText(mContext, "Data on Cloud FireStore success", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "sendDataToCloudStore: \"Data on Cloud FireStore success\"" )
        }.addOnFailureListener {
            Log.e(TAG, "sendDataToCloudStore: $it")
        }

        //then also add in other user chat also that they can also view who has messaged him
        addInReceiverUserDataBase(firebaseBO.uid)
    }

    private fun addInReceiverUserDataBase(receiver_uid: String) {
        val documentReference1: DocumentReference =
            firebaseFirestore?.collection("Users")!!.document(receiver_uid)
        val params1: MutableMap<String, Any> = HashMap()

        val firebaseBO1 =
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
                FireBaseBO(
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID),
                    "Offline",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME),
                    CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME),
                    "",
                    "",
                    "",
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.MOBILE_NO),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
                    CommonMethods.getPrefrence(mContext!!, AllKeys.Patient),
                )
            } else {
                FireBaseBO(
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID),
                    "Offline",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
                    "",
                )
            }

        params1[AllKeys.recent_chat] = FieldValue.arrayUnion(firebaseBO1)

        documentReference1.set(params1).addOnSuccessListener {
            //Toast.makeText(mContext, "Data on Cloud FireStore success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e(TAG, "sendDataToCloudStore: $it")
        }
    }
}
