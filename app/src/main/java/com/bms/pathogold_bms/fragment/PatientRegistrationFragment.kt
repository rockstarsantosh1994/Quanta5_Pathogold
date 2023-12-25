package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.*
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.activity.PhotoViewActivity
import com.bms.pathogold_bms.adapter.PatientRegistrationAdapter
import com.bms.pathogold_bms.adapter.SelectedTestAdapter
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.ImageUploadBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse
import com.bms.pathogold_bms.model.getpatient.GetPatientBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListResponse
import com.bms.pathogold_bms.model.getrefcc.GetRefCCBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeResponse
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import de.hdodenhof.circleimageview.CircleImageView
import io.paperdb.Paper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PatientRegistrationFragment : BaseFragment(), View.OnClickListener,
    PatientRegistrationAdapter.GetPatientRegistrationBOInterFace,
    SelectedTestAdapter.DeleteSelectedTest {

    //Declaration....
    //Toolbar declaration...
    private val TAG = "PatientRegistrationActi"

    //TextInputLayout declaration...
    private var tilIntial: TextInputLayout? = null
    private var tilFirstName: TextInputLayout? = null
    private var tilLastName: TextInputLayout? = null
    private var tilAge: TextInputLayout? = null
    private var tilMDY: TextInputLayout? = null
    private var tilDOB: TextInputLayout? = null
    private var tilMobileNo: TextInputLayout? = null
    private var tilRemark: TextInputLayout? = null
    private var tilEmail: TextInputLayout? = null
    private var tilConsultantDoctor: TextInputLayout? = null
    private var tilTest: TextInputLayout? = null
    private var tilCollectionCenter: TextInputLayout? = null
    private var tilReferringDoctor: TextInputLayout? = null

    //TextInputEditText declaration
    private var etFirstName: TextInputEditText? = null
    private var etLastName: TextInputEditText? = null
    private var etAge: TextInputEditText? = null
    private var etDOB: TextInputEditText? = null
    private var etMobileNo: TextInputEditText? = null
    private var etRemark: TextInputEditText? = null
    private var etEmail: TextInputEditText? = null
    private var etConsultantDoctor: TextInputEditText? = null
    private var etTest: TextInputEditText? = null
    private var etCollectionCenter: TextInputEditText? = null
    private var etReferringDoctor: TextInputEditText? = null

    //Edittext declaration..
    private var etMobileNoSearch: EditText? = null

    //TextView Declaration..
    private var tvOtherDocuments: TextView? = null
    private var tvNoDataFound: TextView? = null
    private var tvReset: TextView? = null
    private var tvTotalTestPrice: TextView? = null
    private var tvNoTestFound: TextView? = null

    //RadioButton and radio group declaration..
    private var rgGender: RadioGroup? = null
    private var rgMale: RadioButton? = null
    private var rgFemale: RadioButton? = null

    //ImageView Declaration...
    private var ivProfilePic: CircleImageView? = null
    private var ivOtherDocumentsPreview: ImageView? = null

    //AutoCompleteTextView declaration....
    private var spinInital: AutoCompleteTextView? = null
    private var spinMDY: AutoCompleteTextView? = null

    //AppCompatButton declaration..
    private var btnSubmit: AppCompatButton? = null
    private var btnGetPatientList: AppCompatButton? = null

    //RecyclerView declaration..
    private var rvGetPatientList: RecyclerView? = null
    private var rvViewTest: RecyclerView? = null

    //LinearLayout declaration..
    private var llViewTestView: LinearLayout? = null

    private val initials = arrayOf("Mr.", "Mrs.", "Miss.")
    private val mdy = arrayOf("Year", "Month", "Days")
    private var stGender: String? = null

    private val CONSULTATION_DATA_DIALOG = 1
    private val TEST_CODE_DATA_DIALOG = 2
    private val COLLECTION_CENTER = 3
    private val REFERRING_DOCTOR = 4

    private val getPatientListBOArrayList = ArrayList<GetPatientListBO>()
    private lateinit var testCodeBOTemp: GetTestCodeBO
    private val getPatientTestListMap: MutableMap<String, GetTestCodeBO> = LinkedHashMap()
    private var consultationBOArrayList = ArrayList<ConsultationBO>()
    private var consultationBO: ConsultationBO? = null
    private val getTestId = ArrayList<String>()
    private val getTestName = ArrayList<String>()
    private var getRefCCList: ArrayList<GetRefCCBO>? = ArrayList()
    private var getRefDrList: ArrayList<GetRefCCBO>? = ArrayList()
    private var getTestArrayList = ArrayList<GetTestCodeBO>()
    private var selectedTestArrayList = ArrayList<GetTestCodeBO>()

    private var getRefCollectionCenter: GetRefCCBO? = null
    private var getRefDoctorReference: GetRefCCBO? = null

    private var patientRegistrationAdapter: PatientRegistrationAdapter? = null

    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 101
    private val GALLERY = 102
    private var mCurrentPhotoPath: String? = null
    private var profilePicBase64String: String? = null
    private var otherDocumentBase64String: String? = null
    private var picType: String? = null

    private var profilePicBitMap: Bitmap? = null
    private var otherDocPicBitmap: Bitmap? = null

    private var selectedTestAdapter: SelectedTestAdapter? = null

    private var testTotalRate = 0

    private var viewAppointmnetBO:ViewAppointmentBO?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get PaperDB data which was executed in DashBoardActivity...
        getRefCCList = Paper.book().read("getref_cc")
        getRefDrList = Paper.book().read("getref_dr")
        consultationBOArrayList = Paper.book().read("consultation_list")
        //Consultation Doctor edittext set
        if (consultationBOArrayList.size == 1) {
            val temp = ConsultationBO(consultationBOArrayList[0].Pno,
                consultationBOArrayList[0].Name,
                "")
            consultationBO = temp
            etConsultantDoctor?.setText(consultationBO?.Name)
        }

        getTestArrayList = Paper.book().read("test_list")
        for (temp in getTestArrayList) {
            testCodeBOTemp = GetTestCodeBO(temp.tlcode, temp.title, temp.rate, "false")
            getPatientTestListMap[temp.tlcode.trim()] = testCodeBOTemp
        }

        //basic intilaisation..
        initViews(view)

        //Collection Center editext set..
        if (getRefCCList?.size == 0) {
            val getRefCCBO = GetRefCCBO("Self", "Self", "", "")
            getRefCollectionCenter = getRefCCBO
            etCollectionCenter?.setText((getRefCollectionCenter?.name))
        } else if (getRefCCList?.size!! >0) {
            val getRefCCBO = GetRefCCBO(
                getRefCCList!![0].code,
                getRefCCList!![0].name,
                getRefCCList!![0].defautflag,
                getRefCCList!![0].Check_Flag,
            )
            getRefCollectionCenter = getRefCCBO
            etCollectionCenter?.setText((getRefCollectionCenter?.name))
        }

        //Referring Doctor edittext set..
        if (getRefDrList?.size == 0) {
            val getRefCCBO = GetRefCCBO("Self", "Self", "", "")
            getRefDoctorReference = getRefCCBO
            etReferringDoctor?.setText((getRefDoctorReference?.name))
        } else if (getRefDrList?.size == 1) {
            val getRefCCBO = GetRefCCBO(
                getRefCCList!![0].code,
                getRefCCList!![0].name,
                getRefCCList!![0].defautflag,
                getRefCCList!![0].Check_Flag,
            )
            getRefDoctorReference = getRefCCBO
            etReferringDoctor?.setText((getRefDoctorReference?.name))
        }

        //Comming view appointment flow...to set data..
        if (arguments?.getString("launch_type").equals("view_appointment")) {
            setDataToEditText(arguments?.getParcelable("patient_bo")!!)

            //get pre selected test to set the test in patient registration fragment.
            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                viewAppointmnetBO=arguments?.getParcelable("appointment_details")
                viewSelectedTestDetails(viewAppointmnetBO?.BookApp_Id.toString())
            } else {
                CommonMethods.showDialogForError(requireActivity(), AllKeys.NO_INTERNET_AVAILABLE)
            }
        }
    }

    override val activityLayout: Int
        get() = R.layout.activity_patient_registration

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews(view: View) {
        /*  //toolbar binding..
          toolbar = findViewById(R.id.toolbar)
          setSupportActionBar(toolbar)
          supportActionBar?.setDisplayHomeAsUpEnabled(true)
          supportActionBar?.setDisplayShowTitleEnabled(false)
          toolbar?.title = resources.getString(R.string.patient_registration)
          toolbar?.setTitleTextColor(Color.WHITE)
          toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white),
              PorterDuff.Mode.SRC_ATOP);
          //toolbar.setTextAlignment(TEXT_ALIGNMENT_CENTER);*/
        (activity as DashBoardActivity).toolbar?.title =
            resources.getString(R.string.patient_registration)

        //Linearlayout binding..
        llViewTestView = view.findViewById(R.id.ll_view_test_view)

        //TextInputLayout binding...
        tilIntial = view.findViewById(R.id.til_intial)
        tilFirstName = view.findViewById(R.id.til_first_name)
        tilLastName = view.findViewById(R.id.til_last_name)
        tilAge = view.findViewById(R.id.til_age)
        tilMDY = view.findViewById(R.id.til_mdy)
        tilDOB = view.findViewById(R.id.til_date_of_birth)
        tilMobileNo = view.findViewById(R.id.til_mobile_number)
        tilRemark = view.findViewById(R.id.til_remark)
        tilEmail = view.findViewById(R.id.til_email_address)
        tilConsultantDoctor = view.findViewById(R.id.til_consultant_doctor)
        tilTest = view.findViewById(R.id.til_test)
        tilCollectionCenter = view.findViewById(R.id.til_collection_center)
        tilReferringDoctor = view.findViewById(R.id.til_referring_doctor)

        //TextInputEditText binding
        etFirstName = view.findViewById(R.id.et_first_name)
        etLastName = view.findViewById(R.id.et_last_name)
        etAge = view.findViewById(R.id.et_age)
        etDOB = view.findViewById(R.id.et_date_of_birth)
        etMobileNo = view.findViewById(R.id.et_mobile_number)
        etRemark = view.findViewById(R.id.et_remark)
        etEmail = view.findViewById(R.id.et_email_address)
        etConsultantDoctor = view.findViewById(R.id.et_consultant_doctor)
        etTest = view.findViewById(R.id.et_test)
        etCollectionCenter = view.findViewById(R.id.et_collection_center)
        etReferringDoctor = view.findViewById(R.id.et_referring_doctor)

        //EditText binding..
        etMobileNoSearch = view.findViewById(R.id.et_mobile_no_search)

        //RecyclerView binding..
        rvGetPatientList = view.findViewById(R.id.rv_get_patient)
        val layoutManager = LinearLayoutManager(context)
        rvGetPatientList?.layoutManager = layoutManager

        //TextView declaration..
        tvOtherDocuments = view.findViewById(R.id.tv_other_documents)
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)
        tvReset = view.findViewById(R.id.tv_reset)
        tvTotalTestPrice = view.findViewById(R.id.tv_total_test_price)
        tvNoTestFound = view.findViewById(R.id.tv_no_test_found)

        //Radiogroup and radio button binding..
        rgGender = view.findViewById(R.id.radio_gender)
        rgMale = view.findViewById(R.id.rb_male)
        rgFemale = view.findViewById(R.id.rb_female)

        //ImageView binding..
        ivProfilePic = view.findViewById(R.id.iv_profilepic)
        ivOtherDocumentsPreview = view.findViewById(R.id.iv_other_document_preview)

        //AppCompatButton binding..
        btnSubmit = view.findViewById(R.id.btn_submit)
        btnGetPatientList = view.findViewById(R.id.btn_get_patient_list)

        //Click listeners..
        etDOB?.setOnClickListener(this)
        etConsultantDoctor?.setOnClickListener(this)
        etTest?.setOnClickListener(this)
        btnSubmit?.setOnClickListener(this)
        etCollectionCenter?.setOnClickListener(this)
        etReferringDoctor?.setOnClickListener(this)
        ivProfilePic?.setOnClickListener(this)
        tvOtherDocuments?.setOnClickListener(this)
        ivOtherDocumentsPreview?.setOnClickListener(this)
        btnGetPatientList?.setOnClickListener(this)
        tvReset?.setOnClickListener(this)

        //Spinner binding..
        spinInital = view.findViewById(R.id.spin_inital)
        spinMDY = view.findViewById(R.id.spin_mdy)

        //add data to spinIntial and spinMDY
        addDataToIntialAndMDY()

        //Get gender value in stGender..
        rgGender?.setOnCheckedChangeListener { group, checkedId ->
            stGender = (rgGender?.checkedRadioButtonId
                ?.let { view.findViewById<View>(it) } as RadioButton).text.toString()

        }

        //RecyclerView binding..
        rvViewTest = view.findViewById(R.id.rv_view_test)
        val linearLayoutManager = LinearLayoutManager(mContext!!)
        rvViewTest?.layoutManager = linearLayoutManager

        //based on age birthdate will be set..
        etAge?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etAge?.text.toString().isEmpty()) {
                    etDOB?.text = null
                }
            }

            @SuppressLint("SetTextI18n", "SimpleDateFormat")
            override fun afterTextChanged(s: Editable) {
                try {
                    val yearFormat: DateFormat = SimpleDateFormat("yyyy")
                    val calYear = Calendar.getInstance()
                    val year = yearFormat.format(calYear.time)

                    val birthYear =
                        Integer.parseInt(year) - Integer.parseInt(etAge?.text.toString())

                    val dateFormat: DateFormat = SimpleDateFormat("MM/dd/")
                    val cal = Calendar.getInstance()
                    val currentDate = dateFormat.format(cal.time)

                    Log.e(TAG, "afterTextChanged: $birthYear")
                    etDOB?.setText("" + currentDate + "" + birthYear)

                } catch (e: Exception) {
                    e.printStackTrace()
                    if (etAge?.text.toString().isEmpty()) {
                        etDOB?.text = null
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    if (etAge?.text.toString().isEmpty()) {
                        etDOB?.text = null
                    }
                }
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.et_date_of_birth -> {
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->
                        //  etDate?.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                        etDOB?.setText("" + (monthOfYear + 1) + "/" + dayOfMonth.toString() + "/" + year)

                        val age = Calendar.getInstance()[Calendar.YEAR] - year
                        etAge?.setText(StringBuilder().append("").append(age).append(""))

                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.et_consultant_doctor -> {
                val consultationDialog = ConsultationDialog()
                val bundle = Bundle()
                bundle.putParcelableArrayList("consultation_list", consultationBOArrayList)
                consultationDialog.arguments = bundle
                consultationDialog.setTargetFragment(this, CONSULTATION_DATA_DIALOG)
                consultationDialog.show(fragmentManager?.beginTransaction()!!, "ConsultationDialog")
            }

            R.id.et_test -> {
                for (temp in selectedTestArrayList) {
                    if (getPatientTestListMap.containsKey(temp.tlcode)) {
                        getPatientTestListMap.remove(temp.tlcode)
                        getPatientTestListMap[temp.tlcode] =
                            GetTestCodeBO(temp.tlcode, temp.title, temp.rate, "true")
                    }
                }
                val testCodeDialog = TestCodeDialog()
                val bundle = Bundle()
                bundle.putString("open_via", "fragment")
                val values: ArrayList<GetTestCodeBO> = ArrayList(getPatientTestListMap.values)
                bundle.putParcelableArrayList("testcode_list", values)
                testCodeDialog.arguments = bundle
                testCodeDialog.setTargetFragment(this, TEST_CODE_DATA_DIALOG)
                testCodeDialog.show(fragmentManager?.beginTransaction()!!, "TestCodeDialog")
            }

            R.id.et_collection_center -> {
                val centerAndDoctorDialog = CenterAndDoctorDialog()
                val bundle = Bundle()
                bundle.putParcelableArrayList("getreflist", getRefCCList)
                bundle.putString("ref_type", "CC")
                centerAndDoctorDialog.arguments = bundle
                centerAndDoctorDialog.setTargetFragment(this, COLLECTION_CENTER)
                centerAndDoctorDialog.show(fragmentManager?.beginTransaction()!!, "COLLECTION_CENTER")
            }

            R.id.et_referring_doctor -> {
                val centerAndDoctorDialog = CenterAndDoctorDialog()
                val bundle = Bundle()
                bundle.putParcelableArrayList("getreflist", getRefDrList)
                bundle.putString("ref_type", "DR")
                centerAndDoctorDialog.arguments = bundle
                centerAndDoctorDialog.setTargetFragment(this, REFERRING_DOCTOR)
                centerAndDoctorDialog.show(fragmentManager?.beginTransaction()!!,
                    "REFERRING_DOCTOR")
            }

            R.id.iv_profilepic -> {
                showPictureDialog("Profile")
            }

            R.id.tv_other_documents -> {
                showPictureDialog("Other")
            }

            R.id.btn_get_patient_list -> {
                if (isValidatedMobile()) {
                    if (CommonMethods.isNetworkAvailable(mContext!!)) {
                        getPatientList(etMobileNoSearch?.text.toString())
                    } else {
                        CommonMethods.showDialogForError(mContext as Activity,
                            AllKeys.NO_INTERNET_AVAILABLE)
                    }
                }
            }

            R.id.tv_reset -> {
                resetAllFields()
            }

            R.id.iv_other_document_preview -> {
                /*if(otherDocumentBase64String!=null){
                    val intent = Intent(mContext, PhotoViewActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    val bStream  =  ByteArrayOutputStream()
                    otherDocPicBitmap?.compress(Bitmap.CompressFormat.JPEG, 50, bStream)
                    val byteArray = bStream.toByteArray()
                    intent.putExtra("img_src", byteArray)
                    startActivity(intent)
                }else{
                    Toast.makeText(mContext, "Please capture image.", Toast.LENGTH_LONG).show()
                }*/
            }

            R.id.btn_submit -> {
                if (isValidated()) {
                   registerPatient()
                   //showDialogForSuccess(mContext!!, "12345","123456")
                }
            }
        }
    }

    private fun getPatientList(stMobileNo: String) {
        val params: MutableMap<String, String> = HashMap()
        params["fromdate"] = ""
        params["todate"] = ""
        params["LabCode"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        params["UserType"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE) }.toString()
        params["UserName"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USER_NAME) }.toString()
        params["PatientName"] = ""
        params["PatientMobileNo"] = stMobileNo
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

                            if (getPatientListBOArrayList.size == 1) {
                                rvGetPatientList?.visibility = View.GONE
                                tvNoDataFound?.visibility = View.GONE

                                val getPatientListBO =
                                    GetPatientListBO(getPatientListBOArrayList[0].regno,
                                        getPatientListBOArrayList[0].PatientName,
                                        getPatientListBOArrayList[0].UserName,
                                        getPatientListBOArrayList[0].Pno,
                                        getPatientListBOArrayList[0].age,
                                        getPatientListBOArrayList[0].MDY,
                                        getPatientListBOArrayList[0].sex,
                                        getPatientListBOArrayList[0].PatientPhoneNo,
                                        getPatientListBOArrayList[0].Dr_name,
                                        getPatientListBOArrayList[0].PePatID,
                                        getPatientListBOArrayList[0].finalteststatus,
                                        getPatientListBOArrayList[0].DOB,
                                        getPatientListBOArrayList[0].Token,
                                        getPatientListBOArrayList[0].status,
                                        getPatientListBOArrayList[0].Samplestatus,
                                        getPatientListBOArrayList[0].Remark,
                                        getPatientListBOArrayList[0].balance,
                                        getPatientListBOArrayList[0].billamount,
                                        getPatientListBOArrayList[0].Entrydate)

                                //SetData to fields..
                                setDataToEditText(getPatientListBO)
                            } else {
                                rvGetPatientList?.visibility = View.VISIBLE
                                tvNoDataFound?.visibility = View.GONE

                                patientRegistrationAdapter = context?.let {
                                    PatientRegistrationAdapter(it,
                                        getPatientListBOArrayList,
                                        this@PatientRegistrationFragment)
                                }
                                rvGetPatientList?.adapter = patientRegistrationAdapter
                            }

                        } else {
                            rvGetPatientList?.visibility = View.GONE
                            tvNoDataFound?.visibility = View.VISIBLE
                        }
                    } else {
                        rvGetPatientList?.visibility = View.GONE
                        tvNoDataFound?.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun showPictureDialog(type: String) {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        //to get is it profile or other file.
        picType = type
        when (type) {
            "Profile" -> {
                val pictureDialogItems =
                    arrayOf("Capture photo from camera", "Select photo from gallery"/*, "Preview"*/)
                pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> takePhotoFromCamera()
                        1 -> choosePhotoFromGallary()
                        2 -> profilePicPreview()
                    }
                }
            }

            "Other" -> {
                val pictureDialogItems =
                    arrayOf("Capture photo from camera", "Select photo from gallery")
                pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> takePhotoFromCamera()
                        1 -> choosePhotoFromGallary()
                    }
                }
            }
        }

        pictureDialog.show()
    }

    private fun profilePicPreview() {
        if (profilePicBase64String != null) {
            val intent = Intent(mContext, PhotoViewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            val bStream = ByteArrayOutputStream()
            val decodedString: ByteArray = Base64.decode(profilePicBase64String, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            decodedByte?.compress(Bitmap.CompressFormat.PNG, 50, bStream)
            val byteArray = bStream.toByteArray()
            intent.putExtra("img_src", byteArray)
            startActivity(intent)
        } else {
            Toast.makeText(mContext, "Please capture image", Toast.LENGTH_LONG).show()
        }

    }

    private fun takePhotoFromCamera() {
        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)*/
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create the File where the photo should go
        try {
            photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(mContext!!, context?.packageName + ".fileprovider", photoFile!!)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
            }
        } catch (ex: Exception) {
            // Error occurred while creating the File
            displayMessage(mContext!!, ex.message.toString())
        }
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val thumbnail = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            val out = ByteArrayOutputStream()
            if (BuildConfig.DEBUG && thumbnail == null) {
                error("Assertion failed")
            }
            thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 50, out)
            //val decodedImage = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
            val byteArray = out.toByteArray()
            if (picType.equals("Profile")) {
                profilePicBitMap = thumbnail
                ivProfilePic!!.setImageBitmap(thumbnail)
                profilePicBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            } else if (picType.equals("Other")) {
                otherDocPicBitmap = thumbnail
                ivOtherDocumentsPreview!!.setImageBitmap(thumbnail)
                otherDocumentBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            }

        } else if (requestCode == GALLERY && resultCode == RESULT_OK) {
            val contentURI: Uri = data?.data ?: return
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                //String path = saveImage(bitmap);
                val out = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 65, out)
                val byteArray = out.toByteArray()
                //val compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                if (picType.equals("Profile")) {
                    profilePicBitMap = bitmap
                    ivProfilePic!!.setImageBitmap(bitmap)
                    profilePicBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                } else if (picType.equals("Other")) {
                    otherDocPicBitmap = bitmap
                    ivOtherDocumentsPreview!!.setImageBitmap(bitmap)
                    otherDocumentBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                }

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }

        when (requestCode) {
            CONSULTATION_DATA_DIALOG -> {
                if (resultCode == RESULT_OK) {
                    val bundle = data?.extras
                    consultationBO = bundle?.getParcelable("consultation_bo")
                    etConsultantDoctor?.setText(consultationBO?.Name)
                    Log.e(TAG, "onActivityResult: $consultationBO")
                }
            }

            TEST_CODE_DATA_DIALOG -> {
                selectedTestArrayList = ArrayList()
                val bundle = data?.extras
                selectedTestArrayList.clear()
                selectedTestArrayList.addAll(bundle?.getParcelableArrayList("test_code_list")!!)
                getTestId.clear()
                getTestName.clear()

                //attach RecyclerView of Selected Test..
                attachSelectedTestRecyclerView(selectedTestArrayList)
                //etTest?.setText(getTestName.toString().replace("[", "").replace("]", ""))
            }

            COLLECTION_CENTER -> {
                val bundle = data?.extras
                getRefCollectionCenter = bundle?.getParcelable("get_ref_cc_bo")
                etCollectionCenter?.setText((getRefCollectionCenter?.name))
            }

            REFERRING_DOCTOR -> {
                val bundle = data?.extras
                getRefDoctorReference = bundle?.getParcelable("get_ref_cc_bo")
                etReferringDoctor?.setText(getRefDoctorReference?.name)
            }

            RESULT_CANCELED -> {
                Toast.makeText(mContext, "No Data Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createProfilePicBO(regno: String) {
        val imageUploadBO = ImageUploadBO()
        imageUploadBO.setRegno(regno)
        //imageUploadBO.setSeqno(regno+"_"+CommonMethods.getPrefrence(mContext!!,AllKeys.LABNAME)+"_"+SimpleDateFormat("MM/dd/yyyy_HHmmss",Locale.getDefault()).format(Date()))
        imageUploadBO.setSeqno("2")
        imageUploadBO.setCompId(AllKeys.COMPANY_ID)
        imageUploadBO.setTestName("")
        imageUploadBO.setTlcode("")
        imageUploadBO.setF(profilePicBase64String)
        imageUploadBO.setType("reg")
        imageUploadBO.setRemark("")
        imageUploadBO.setLocation("Profilepic")

        Log.e(TAG, "createProfilePicBO: $imageUploadBO")

        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            uploadPhoto(imageUploadBO)
        }
    }

    private fun createOtherPicBO(regno: String) {
        val imageUploadBO = ImageUploadBO()
        imageUploadBO.setRegno(regno)
        imageUploadBO.setSeqno("2")
        imageUploadBO.setCompId(AllKeys.COMPANY_ID)
        imageUploadBO.setTestName("")
        imageUploadBO.setTlcode("")
        imageUploadBO.setF(otherDocumentBase64String)
        imageUploadBO.setType("reg")
        imageUploadBO.setRemark("")
        imageUploadBO.setLocation("IDproof")

        Log.e(TAG, "createOtherPicBO: $imageUploadBO")

        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            uploadPhoto(imageUploadBO)
        }
    }

    private fun uploadPhoto(imageUploadBO: ImageUploadBO) {
        val params: MutableMap<String, String> = HashMap()
        params["regno"] = imageUploadBO.getRegno().toString()
        params["tlcode"] = imageUploadBO.getTlcode().toString()
        params["TestName"] = imageUploadBO.getTestName().toString()
        params["remark"] = imageUploadBO.getRemark().toString()
        params["f"] = imageUploadBO.getF().toString()
        params["CompId"] = AllKeys.COMPANY_ID
        params["Seqno"] = imageUploadBO.getSeqno().toString()
        params["type"] = imageUploadBO.getType().toString()
        params["Location"] = imageUploadBO.getLocation().toString()

        Log.e(TAG, "uploadAllPhotos: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Uploading Image...Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.uploadLabClientImage(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        Toast.makeText(mContext, commonResponse.Message, Toast.LENGTH_LONG).show()
                    } else {
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: Upload Photo $apiResponse")
                }
            })
    }

    @SuppressLint("SimpleDateFormat")
    private fun registerPatient() {
        val params: MutableMap<String, String> = HashMap()
        val dateFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        val cal = Calendar.getInstance()
        val todaysDate = dateFormat.format(cal.time)
        params["DateOfEntry"] = todaysDate
        params["intial"] = spinInital?.text.toString()
        params["FirstName"] = etFirstName?.text.toString()
        params["LastName"] = etLastName?.text.toString()
        params["sex"] = stGender.toString()
        params["Age"] = etAge?.text.toString()
        params["Mob_no"] = etMobileNo?.text.toString()
        params["Remark"] = etRemark?.text.toString()
        params["Email"] = etEmail?.text.toString()
        params["MDY"] = spinMDY?.text.toString()
        params["DateofBirth"] = etDOB?.text.toString()
        params["CompanyId"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["Username"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USER_NAME).toString() }!!
        params["Usertype"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.USERTYPE).toString() }!!
        params["TestName"] = getTestName.toString().replace("[", "").replace("]", "")
        params["lab_code"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME).toString() }!!
        params["TLCode"] = getTestId.toString().replace("[", "").replace("]", "")
        if (consultationBO == null) {
            params["pno"] = "0"
            params["DoctorName"] = ""
        } else {
            params["pno"] = consultationBO?.Pno.toString()
            params["DoctorName"] = consultationBO?.Name.toString()
        }
        params["coll_code"] = getRefCollectionCenter?.code.toString()
        params["dr_code"] = getRefDoctorReference?.code.toString()

        Log.e(TAG, "registerPatient: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Registering...Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.contactInformationForCCNewAPI(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        showDialogForSuccess(mContext!!, commonResponse.Message+"\n Your Reg No:  "+commonResponse.ResultArray[0].Regno,commonResponse.ResultArray[0].Regno)

                        if (profilePicBase64String != null) {
                            createProfilePicBO(commonResponse.ResultArray[0].Regno)
                        }

                        if (otherDocumentBase64String != null) {
                            createOtherPicBO(commonResponse.ResultArray[0].Regno)
                        }


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

    fun showDialogForSuccess(context: Context,message: String,regno: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Make Payment"
            ) { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()

                val getPatientListBO=GetPatientListBO(regno,
                    spinInital?.text.toString()+""+etFirstName?.text.toString()+" "+etLastName?.text.toString(),
                    "",
                    "",
                    etAge?.text.toString(),
                    spinMDY?.text.toString(),
                    stGender.toString(),
                    etMobileNo?.text.toString(),
                    "","","","","","","","","","",""
                )
                val patientRegistrationInvoiceDialog = PatientRegistrationInvoiceDialog()
                val bundle = Bundle()
                bundle.putParcelable("patient_bo",getPatientListBO)
                bundle.putParcelableArrayList("test_list", selectedTestArrayList)
                bundle.putParcelable("appointment_details",viewAppointmnetBO)
                patientRegistrationInvoiceDialog.arguments = bundle
                patientRegistrationInvoiceDialog.setTargetFragment(this, REFERRING_DOCTOR)
                patientRegistrationInvoiceDialog.show(fragmentManager?.beginTransaction()!!, "PATIENT_INVOICE")
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    override fun getOnPatientSelected(getPatientListBO: GetPatientListBO) {
        Log.e(TAG, "getOnPatientSelected: $getPatientListBO")
        rvGetPatientList?.visibility = View.GONE
        setDataToEditText(getPatientListBO)
    }

    private fun setDataToEditText(getPatientListBO: GetPatientListBO) {
        val parts = getPatientListBO.PatientName.split(" ").toTypedArray()
        if (arguments?.getString("launch_type").equals("view_appointment")) {
            etFirstName?.setText(parts[0])
            if (parts.size == 2) {
                etLastName?.setText(parts[1])
            }
        } else {
            spinInital?.setText(parts[0])
            etFirstName?.setText(parts[1])
            etLastName?.setText(parts[2])
        }
        if (getPatientListBO.sex.equals("male", true)) {
            rgMale?.isChecked = true
        } else {
            rgFemale?.isChecked = true
        }
        etAge?.setText(getPatientListBO.age)
        spinMDY?.setText(getPatientListBO.MDY)
        etDOB?.setText(getPatientListBO.DOB)
        etMobileNo?.setText(getPatientListBO.PatientPhoneNo)

        //Again add data to adapter to intial and MDY....
        addDataToIntialAndMDY()
    }

    private fun resetAllFields() {
        spinInital?.setText("")
        etFirstName?.setText("")
        etLastName?.setText("")
        rgMale?.isChecked = false
        rgFemale?.isChecked = false
        etAge?.setText("")
        spinMDY?.setText("")
        etDOB?.setText("")
        etMobileNo?.setText("")
        etEmail?.setText("")
        etRemark?.setText("")
        etConsultantDoctor?.setText("")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addDataToIntialAndMDY() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, initials)
        spinInital?.setAdapter(adapter)
        spinInital?.keyListener = null
        spinInital?.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }

        val adapterMDY = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mdy)
        spinMDY?.setAdapter(adapterMDY)
        spinMDY?.keyListener = null
        spinMDY?.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }
    }

    private fun viewSelectedTestDetails(appointmentId: String) {
        val params: MutableMap<String, String> = HashMap()
        params["LabCode"] =
            mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        params["appointmentid"] = appointmentId

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.viewTestCode(params, object : ApiRequestHelper.OnRequestComplete {
                @RequiresApi(Build.VERSION_CODES.N)
                @SuppressLint("SetTextI18n")
                override fun onSuccess(`object`: Any) {
                    val getTestCodeResponse = `object` as GetTestCodeResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getTestCodeResponse")
                    if (getTestCodeResponse.ResponseCode == 200) {
                        if (getTestCodeResponse.ResultArray.size > 0) {
                            llViewTestView?.visibility = View.VISIBLE
                            tvNoTestFound?.visibility = View.GONE
                            selectedTestArrayList.addAll(getTestCodeResponse.ResultArray)

                            //attach RecyclerView of Selected Test..
                            attachSelectedTestRecyclerView(getTestCodeResponse.ResultArray)

                            /*for(temp in selectedTestArrayList){
                                if(getPatientTestListMap.containsKey(temp.tlcode)){
                                    getPatientTestListMap.remove(temp.tlcode)
                                    getPatientTestListMap[temp.tlcode] = GetTestCodeBO(temp.tlcode,temp.title,temp.rate,"true")
                                }
                            }*/

                            Log.e(TAG, "onSuccess: ")

                        } else {
                            llViewTestView?.visibility = View.GONE
                            tvNoTestFound?.visibility = View.VISIBLE
                        }
                    } else {
                        llViewTestView?.visibility = View.GONE
                        tvNoTestFound?.visibility = View.VISIBLE
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
    private fun attachSelectedTestRecyclerView(resultArray: ArrayList<GetTestCodeBO>) {
        if (resultArray.size > 0) {
            llViewTestView?.visibility = View.VISIBLE
            tvNoTestFound?.visibility = View.GONE
            selectedTestAdapter = SelectedTestAdapter(mContext!!,
                selectedTestArrayList,
                this@PatientRegistrationFragment,
                View.VISIBLE)
            rvViewTest?.adapter = selectedTestAdapter
        } else {
            llViewTestView?.visibility = View.GONE
            tvNoTestFound?.visibility = View.VISIBLE
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

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
             android.R.id.home -> {
                 finish()
             }
         }
         return super.onOptionsItemSelected(item)
     }*/

    private fun isValidatedMobile(): Boolean {
        if (etMobileNoSearch?.text.toString().isEmpty()) {
            etMobileNoSearch?.error = "Mobile No. required!"
            etMobileNoSearch?.requestFocus()
            return false
        }

        if (etMobileNoSearch?.text?.length!! > 12) {
            etMobileNoSearch?.error = "Invalid mobile number!"
            etMobileNoSearch?.requestFocus()
            return false
        }
        return true
    }

    private fun isValidated(): Boolean {
        if (spinInital?.text.toString().isEmpty()) {
            tilIntial?.error = "Please select initials!"
            tilIntial?.requestFocus()
            return false
        } else {
            tilIntial?.isErrorEnabled = false
        }

        if (etFirstName?.text.toString().isEmpty()) {
            tilFirstName?.error = "FirstName required!"
            tilFirstName?.requestFocus()
            return false
        } else {
            tilFirstName?.isErrorEnabled = false
        }

        if (etLastName?.text.toString().isEmpty()) {
            tilLastName?.error = "LastName required!"
            tilLastName?.requestFocus()
            return false
        } else {
            tilLastName?.isErrorEnabled = false
        }

        if (etAge?.text.toString().isEmpty()) {
            tilAge?.error = "Age required!"
            tilAge?.requestFocus()
            return false
        } else {
            tilAge?.isErrorEnabled = false
        }

        if (etMobileNo?.text.toString().isEmpty()) {
            tilMobileNo?.error = "Mobile No. Required!"
            tilMobileNo?.requestFocus()
            return false
        } else {
            tilMobileNo?.isErrorEnabled = false
        }

        if (etMobileNo?.text.toString().length != 10) {
            tilMobileNo?.error = "Invalid Mobile No.!"
            tilMobileNo?.requestFocus()
            return false
        } else {
            tilMobileNo?.isErrorEnabled = false
        }

        if (etRemark?.text.toString().isEmpty()) {
            tilRemark?.error = "Remark Required!"
            tilRemark?.requestFocus()
            return false
        } else {
            tilRemark?.isErrorEnabled = false
        }

        /*if (etEmail?.text.toString().isEmpty()) {
            tilEmail?.error = "Email required!"
            tilEmail?.isFocusable = true
            tilEmail?.requestFocus()
            return false
        } else {
            tilEmail?.isErrorEnabled = false
        }*/

        if (etEmail?.text.toString().isNotEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(etEmail?.text.toString()).matches()) {
                tilEmail?.error = "Invalid Email!"
                tilEmail?.isFocusable = true
                tilEmail?.requestFocus()
                return false
            } else {
                tilEmail?.isErrorEnabled = false
            }
        } else {
            tilEmail?.isErrorEnabled = false
        }

        if(getTestId.size==0){
            tilTest?.error = "Please Select Test!"
            tilTest?.isFocusable = true
            tilTest?.requestFocus()
            return false
        }else {
            tilTest?.isErrorEnabled = false
        }

        return true
    }

    //User can delete selected test.....
    @SuppressLint("SetTextI18n")
    override fun onDeleteTest(position: Int) {
        val temp = selectedTestArrayList[position]
        if (getPatientTestListMap.containsKey(temp.tlcode)) {
            getPatientTestListMap.remove(temp.tlcode)
            getPatientTestListMap[temp.tlcode] = GetTestCodeBO(temp.tlcode, temp.title, temp.rate, "false")
        }
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
            llViewTestView?.visibility = View.GONE
            tvNoTestFound?.visibility = View.VISIBLE
        }
    }
}