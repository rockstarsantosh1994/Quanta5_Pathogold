package com.bms.pathogold_bms.fragment

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.transition.TransitionManager
import android.util.Base64.DEFAULT
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.GetPatientTestListAdapter
import com.bms.pathogold_bms.adapter.ImageAdapter
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.ImageUploadBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.patienttestlist.GetPatientTestListBO
import com.bms.pathogold_bms.model.patienttestlist.GetPatientTestListResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.FileBase64
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shreyaspatil.MaterialDialog.MaterialDialog
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ImageUploadFragment : BaseFragment(), View.OnClickListener {

    //Declaration...
    private val TAG = "ImageUploadFragment"

    //TextInputLayout declaration..
    private var tilDate: TextInputLayout? = null
    private var tilTypeSpin: TextInputLayout? = null
    private var tilLocation: TextInputLayout? = null
    private var tilRemark: TextInputLayout? = null
    private var tilPatientName: TextInputLayout? = null

    //TextInputEditText declaration..
    private var etDate: TextInputEditText? = null
    private var etTypeSpin: TextInputEditText? = null
    private var etLocation: TextInputEditText? = null
    private var etRemark: TextInputEditText? = null
    private var etPatientName: TextInputEditText? = null

    //TextView declaration.
    private var tvLocation: TextView? = null
    private var tvTestName: TextView? = null

    //Seaerchable Spinner....
    private var ssPatientName: SearchableSpinner? = null
    private var ssTestName: SearchableSpinner? = null

    //AppCompatButton declaration..
    private var btnCapture: AppCompatButton? = null
    private var btnUploadAllPhotos: AppCompatButton? = null

    //RecyclerView Declaration...
    private var rvCaputrePhotos: RecyclerView? = null

    //LinearLayout declaration..
    private var llLocation: LinearLayout? = null
    private var llTest: LinearLayout? = null

    private val typeArrayList = ArrayList<String>()
    private val soundArrayList = ArrayList<String>()
    private var getPatientListBO: GetPatientListBO? = null
    private var getPatientTestListBO: GetPatientTestListBO? = null
    private val getPatientListBOArrayList = ArrayList<GetPatientListBO>()
    private val getPatientTestListBOArrayList = ArrayList<GetPatientTestListBO>()
    private var CAMERA: Int = 2
    private var imageBase64String: String? = null
    private val imageUploadBOArrayList = ArrayList<ImageUploadBO>()
    private var hitApi: Boolean = true

    private var chronometer: Chronometer? = null
    private var imageViewRecord: ImageView? = null
    private var imageViewPlay: ImageView? = null
    private var imageViewStop: ImageView? = null
    private var seekBar: SeekBar? = null
    private var linearLayoutRecorder: LinearLayout? = null
    private var linearLayoutPlay: LinearLayout? = null
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileName: String? = null
    private var lastProgress = 0
    private var mHandler: Handler = Handler()
    private var isPlaying = false
    private var isAudioSave = false

    var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1
    var mCurrentPhotoPath: String? = null

    override val activityLayout: Int
        get() = R.layout.fragment_image_upload

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation...
        initViews(view)

        val arguments = requireArguments()

        getPatientListBO = arguments.getParcelable("patient_bo")
        etDate?.setText(CommonMethods.getTodayDate("MM/dd/yyyy"))
        etPatientName?.setText(getPatientListBO?.PatientName)

        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            getPatientTestList(getPatientListBO!!.regno)
        } else {
            CommonMethods.showDialogForError(mContext as Activity, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(view: View) {
        (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.image_upload)

        //TextInputLayout binding...
        tilDate = view.findViewById(R.id.til_date)
        tilTypeSpin = view.findViewById(R.id.til_type_spin)
        tilLocation = view.findViewById(R.id.til_location)
        tilRemark = view.findViewById(R.id.til_remark)
        tilPatientName = view.findViewById(R.id.til_patient_name)

        //TextInputEditText binding..
        etDate = view.findViewById(R.id.et_date)
        etTypeSpin = view.findViewById(R.id.et_type_spin)
        etLocation = view.findViewById(R.id.et_location)
        etRemark = view.findViewById(R.id.et_remark)
        etPatientName = view.findViewById(R.id.et_patient_name)

        //TextView binding..
        tvLocation = view.findViewById(R.id.tv_location)
        tvTestName = view.findViewById(R.id.tv_test_name)

        //Searchable spinner binding..
        ssPatientName = view.findViewById(R.id.ss_patient_name)
        ssTestName = view.findViewById(R.id.ss_test_name)

        //AppCompatButton binding...
        btnCapture = view.findViewById(R.id.btn_capture)
        btnUploadAllPhotos = view.findViewById(R.id.btn_upload_all_photos)

        //LinearLayout binding...
        llLocation = view.findViewById(R.id.ll_location)
        llTest = view.findViewById(R.id.ll_test)

        //RecyclerView binding...
        rvCaputrePhotos = view.findViewById(R.id.rv_capture_photos)
        val gridLayoutManager = GridLayoutManager(mContext, 3)
        rvCaputrePhotos?.layoutManager = gridLayoutManager

        //Click listerners...
        btnCapture?.setOnClickListener(this)
        btnUploadAllPhotos?.setOnClickListener(this)
        //etDate?.setOnClickListener(this)
        etTypeSpin?.setOnClickListener(this)

        //AddDataTo typeArrayList
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Lab_Technician)
            || CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Administrator)
        ) {
            typeArrayList.add("Test Result")
            etTypeSpin?.setText("Test Result")
            etTypeSpin?.isClickable = false
            etTypeSpin?.isFocusableInTouchMode = true
            llLocation?.visibility = View.GONE
            llTest?.visibility = View.VISIBLE
        } else {
            typeArrayList.add("Clinical Image")
            typeArrayList.add("ECG")
            typeArrayList.add("Fitness Certificate")
            typeArrayList.add("Sound")
            typeArrayList.add("Other")
            llLocation?.visibility = View.VISIBLE
            llTest?.visibility = View.GONE
            getPatientTestListBO = null
        }

        //AddData to soundArrayList
        soundArrayList.add("Aortic")
        soundArrayList.add("Pulmonary")
        soundArrayList.add("Tricuspid")
        soundArrayList.add("Mitral")
        soundArrayList.add("Erb's point")

      /*  //Back pressed event for fragment..
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true *//* enabled by default *//*) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    AlertDialog.Builder(context)
                        .setMessage("Are you sure want to cancel uploading?")
                        .setPositiveButton("Yes") { arg0, arg1 ->
                            // do something when the button is clicked
                            activity!!.finish()
                            //close();
                        }
                        .setNegativeButton("No"
                        ) // do something when the button is clicked
                        { arg0, arg1 -> }
                        .show()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)*/
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            //Date picker...
            /*  R.id.et_date -> {
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
                              getPatientList(etDate?.text.toString())
                          }
                          //Date currentDate = new Date();
                      }, mYear, mMonth, mDay)
                  datePickerDialog.show()
              }*/

            //Spin data...
            R.id.et_type_spin -> {
                val spinnerDialogDistrict = SpinnerDialog(mContext!! as Activity?, typeArrayList, "Select Type", R.style.DialogAnimations_SmileWindow, "Close") // With 	Animation

                spinnerDialogDistrict.bindOnSpinerListener { item: String, position: Int ->
                    etTypeSpin?.setText(item)
                    if (etTypeSpin?.text.toString().equals("Test Result", false)) {
                        llLocation?.visibility = View.GONE
                        llTest?.visibility = View.VISIBLE
                    } else {
                        llLocation?.visibility = View.VISIBLE
                        llTest?.visibility = View.GONE
                        getPatientTestListBO = null
                    }

                    if (etTypeSpin?.text.toString().equals("Sound", false)) {
                        tvLocation?.text = "Auscultation"
                        etLocation?.isFocusable = false
                        etLocation?.isClickable = true

                        etLocation?.setOnClickListener {
                            val spinnerDialogSound = SpinnerDialog(mContext!! as Activity?,
                                soundArrayList,
                                "Select Sound",
                                R.style.DialogAnimations_SmileWindow,
                                "Close") // With 	Animation
                            spinnerDialogSound.bindOnSpinerListener { item: String, position: Int ->
                                etLocation?.setText(item)
                            }
                            spinnerDialogSound.showSpinerDialog()
                        }
                    } else {
                        tvLocation?.text = "Location"
                        etLocation?.isFocusableInTouchMode = true
                        etLocation?.isClickable = false
                    }
                }
                spinnerDialogDistrict.showSpinerDialog()
            }

            //Caputre data...
            R.id.btn_capture -> {
                if (imageUploadBOArrayList.size == 3) {
                    CommonMethods.showDialogForError(mContext!!,
                        "You have reached maximum limit of capturing image...")
                } else {
                    if (isValidated()) {
                        requestPermissions()
                    }
                }
            }

            //Upload all data
            R.id.btn_upload_all_photos -> {
                if (CommonMethods.isNetworkAvailable(mContext!!)) {
                    /*for (temp in imageUploadBOArrayList)
                    {}*/
                    for (i in 0 until imageUploadBOArrayList.size) {
                        uploadAllPhotos(i)
                    }
                } else {
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        }
    }

    private fun uploadAllPhotos(position: Int) {
        val params: MutableMap<String, String> = HashMap()
        params["regno"] = imageUploadBOArrayList[position].getRegno().toString()
        params["tlcode"] = imageUploadBOArrayList[position].getTlcode().toString()
        params["TestName"] = imageUploadBOArrayList[position].getTestName().toString()
        params["remark"] = imageUploadBOArrayList[position].getRemark().toString()
        params["f"] = imageUploadBOArrayList[position].getF().toString()
        params["CompId"] = AllKeys.COMPANY_ID
        params["Seqno"] = imageUploadBOArrayList[position].getSeqno().toString()
        params["type"] = imageUploadBOArrayList[position].getType().toString()
        params["Location"] = imageUploadBOArrayList[position].getLocation().toString()

        Log.e(TAG, "uploadAllPhotos: $params")
        val progress = ProgressDialog(context)
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
                        Log.e(TAG, "before onSuccess position: $position")
                        Log.e(TAG, "before  onSuccess: size" + imageUploadBOArrayList.size)
                        if (position == imageUploadBOArrayList.size - 1) {
                            Log.e(TAG, "in onSuccess position: $position")
                            Log.e(TAG, "in  onSuccess: size" + imageUploadBOArrayList.size)
                            imageUploadBOArrayList.clear()
                            showDialogForSuccess(mContext!!, commonResponse.Message)
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

    fun showDialogForSuccess(context: Context, message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: com.shreyaspatil.MaterialDialog.interfaces.DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                activity?.finish()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    private fun getPatientTestList(regNo: String) {
        val params: MutableMap<String, String> = HashMap()
        params["Regno"] = regNo

        Log.e(TAG, "getPatientTestList: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getPatientTestList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getPatientTestListResponse = `object` as GetPatientTestListResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getPatientTestListResponse")
                    if (getPatientTestListResponse.ResponseCode == 200) {
                        if (getPatientTestListResponse.ResultArray.size > 0) {
                            getPatientTestListBOArrayList.clear()
                            getPatientTestListBOArrayList.addAll(getPatientTestListResponse.ResultArray)

                            val getPatientTestListAdapter = GetPatientTestListAdapter(mContext,
                                getPatientTestListBOArrayList,
                                0)
                            ssTestName?.setAdapter(getPatientTestListAdapter)
                            ssTestName?.setOnItemSelectedListener(mOnItemSelectedListenerTest)
                            ssTestName?.setStatusListener(object : IStatusListener {
                                override fun spinnerIsOpening() {
                                    ssTestName?.hideEdit()
                                }

                                override fun spinnerIsClosing() {}
                            })
                        } else {
                            noDataFoundForPatientTestList()
                        }
                    } else {
                        noDataFoundForPatientTestList()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun noDataFoundForPatientTestList() {
        getPatientTestListBOArrayList.clear()
        getPatientTestListBOArrayList.add(GetPatientTestListBO("",
            "No Data Found"))
        val getPatientTestListAdapter = GetPatientTestListAdapter(mContext,
            getPatientTestListBOArrayList,
            0)
        ssTestName?.setAdapter(getPatientTestListAdapter)
        ssTestName?.setOnItemSelectedListener(mOnItemSelectedListenerTest)
        ssTestName?.setStatusListener(object : IStatusListener {
            override fun spinnerIsOpening() {
                ssTestName?.hideEdit()
            }

            override fun spinnerIsClosing() {}
        })
    }

    //PatientTestList Searchable Spinner....
    private val mOnItemSelectedListenerTest: OnItemSelectedListener =
        object : OnItemSelectedListener {
            override fun onItemSelected(view: View, position: Int, id: Long) {
                if (ssTestName?.selectedItem != null) {
                    getPatientTestListBO = ssTestName?.selectedItem as GetPatientTestListBO

                    Log.e(TAG, "onItemSelected: $getPatientTestListBO")
                }
            }

            override fun onNothingSelected() {
                Toast.makeText(mContext, "Nothing Selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun requestPermissions() {
        Dexter.withActivity(mContext as Activity?)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        // Toast.makeText(mContext, "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        if (etTypeSpin?.text.toString() == "Sound") {
                            //Open sound dialog...
                            openSoundDialog()
                        } else {
                            //open camera..
                            takePhotoFromCamera()
                        }
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        Toast.makeText(mContext,
                            "Permissions denied!",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken,
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                Toast.makeText(mContext,
                    "Some Error! ",
                    Toast.LENGTH_SHORT).show()
            }
            .onSameThread()
            .check()
    }

    private fun takePhotoFromCamera() {
        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)*/
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile()
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        mContext!!,
                        context?.packageName+".fileprovider",
                        photoFile!!
                    )
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
                displayMessage(mContext!!, ex.message.toString())
            }

       /* } else {
            displayMessage(mContext!!, "Null")
        }*/
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val thumbnail = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            val out = ByteArrayOutputStream()
            if (BuildConfig.DEBUG && thumbnail == null) {
                error("Assertion failed")
            }
            thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 75, out)
            //val decodedImage = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
            val byteArray = out.toByteArray()

            imageBase64String = android.util.Base64.encodeToString(byteArray, DEFAULT)

            //add data to imageUploadBo and arraylist reset all fields....
            createImageUploadBO(thumbnail)
        } else {
            displayMessage(mContext!!, "Request cancelled or something went wrong.")
        }
        /*if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            val thumbnail = Objects.requireNonNull(data!!.extras)!!["data"] as Bitmap?
            val out = ByteArrayOutputStream()
            if (BuildConfig.DEBUG && thumbnail == null) {
                error("Assertion failed")
            }
            thumbnail!!.compress(Bitmap.CompressFormat.PNG, 100, out)
            //val decodedImage = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
            val byteArray = out.toByteArray()

            imageBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

            //add data to imageUploadBo and arraylist reset all fields....
            createImageUploadBO(thumbnail)
        }*/
    }

    private fun createImageUploadBO(thumbnail: Bitmap?) {
        val imageUploadBO = ImageUploadBO()
        imageUploadBO.setRegno(getPatientListBO?.regno.toString())
        imageUploadBO.setSeqno((imageUploadBOArrayList.size + 1).toString() + "" + SimpleDateFormat(
            "HHmmss",
            Locale.getDefault()).format(Date()))
        imageUploadBO.setCompId(AllKeys.COMPANY_ID)
        if (getPatientTestListBO == null) {
            imageUploadBO.setTestName("")
            imageUploadBO.setTlcode("")
        } else {
            imageUploadBO.setTestName(getPatientTestListBO!!.Title)
            imageUploadBO.setTlcode(getPatientTestListBO!!.TLCode)
        }
        imageUploadBO.setBitmap(thumbnail)
        imageUploadBO.setF(imageBase64String)
        imageUploadBO.setType(etTypeSpin?.text.toString())
        imageUploadBO.setRemark(etRemark?.text.toString())
        imageUploadBO.setLocation(etLocation?.text.toString())

        if (imageUploadBOArrayList.size == 3) {
            CommonMethods.showDialogForError(mContext!!,
                "You have reached maximum limit of capturing image...")
        } else {
            imageUploadBOArrayList.add(imageUploadBO)
            val imageAdapter = ImageAdapter(mContext!!, imageUploadBOArrayList)
            rvCaputrePhotos?.adapter = imageAdapter

            //reset all fields...
            resetFields()
        }
        Log.e(TAG, "onActivityResult: $imageUploadBO")
    }

    private fun resetFields() {
        hitApi = false
        //etTypeSpin?.text = null
        //etLocation?.text = null
        //etRemark?.text = null
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Lab_Technician)
            || CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Administrator)
        ) {
            llTest?.visibility = View.VISIBLE
        } else {
            llTest?.visibility = View.GONE
            getPatientTestListBO = null
        }
        btnUploadAllPhotos?.visibility = View.VISIBLE
        isAudioSave = false
    }

    private fun openSoundDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(resources.getString(R.string.record_audio))
        val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customLayout: View = inflater.inflate(R.layout.row_record_sound, null)
        builder.setCancelable(false)
        builder.setView(customLayout)
        //val tilRemark = customLayout.findViewById<TextInputLayout>(R.id.til_remark)
        //LinearLayout binding..
        linearLayoutRecorder = customLayout.findViewById(R.id.linearLayoutRecorder)
        linearLayoutPlay = customLayout.findViewById(R.id.linearLayoutPlay)

        //Chronometer binding....
        chronometer = customLayout.findViewById(R.id.chronometerTimer)
        chronometer?.base = SystemClock.elapsedRealtime()

        //ImageView binding....
        imageViewRecord = customLayout.findViewById(R.id.imageViewRecord)
        imageViewStop = customLayout.findViewById(R.id.imageViewStop)
        imageViewPlay = customLayout.findViewById(R.id.imageViewPlay)

        //SeekBar binding..
        seekBar = customLayout.findViewById(R.id.seekBar)

        //Click Listeners...
        imageViewRecord?.setOnClickListener {
            prepareforRecording()
            startRecording()
            isAudioSave = false
        }

        imageViewStop?.setOnClickListener {
            prepareforStop()
            stopRecording()
            isAudioSave = true
        }

        imageViewPlay?.setOnClickListener {
            if (!isPlaying && fileName != null) {
                isPlaying = true
                startPlaying()
            } else {
                isPlaying = false
                stopPlaying()
            }
        }

        builder.setPositiveButton(resources.getString(R.string.submit_audio)) { dialog, which -> // send data from the
            // AlertDialog to the Activity
            if (fileName != null && fileName!!.isNotEmpty()) {
                prepareforStop()
                stopRecording()
            }
        }

        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> // send data from the
            // AlertDialog to the Activity
            //To don't create object we need to is isAudioSave true
            isAudioSave = true
            prepareforStop()
            stopRecording()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun prepareforRecording() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder)
        imageViewRecord!!.visibility = View.GONE
        imageViewStop!!.visibility = View.VISIBLE
        linearLayoutPlay!!.visibility = View.GONE
    }

    private fun startRecording() {
        //we use the MediaRecorder class to record
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        //Output format in Mp3
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        /**In the lines below, we create a directory named VoiceRecorderSimplifiedCoding/Audios in the phone storage
         * and the audios are being stored in the Audios folder  */
        val root: File = Environment.getExternalStorageDirectory()
        val file = File(root.absolutePath.toString() + "/VoiceRecorderSimplifiedCoding/Audios")
        if (!file.exists()) {
            file.mkdirs()
        }
        fileName =
            root.absolutePath.toString() + "/VoiceRecorderSimplifiedCoding/Audios/" + (System.currentTimeMillis()
                .toString() + ".mp3")
        Log.d("filename", fileName!!)
        mRecorder!!.setOutputFile(fileName)
        //Playing in Mp3 Formats
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        try {
            mRecorder!!.prepare()
            mRecorder!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        lastProgress = 0
        seekBar!!.progress = 0
        stopPlaying()
        //starting the chronometer
        chronometer!!.base = SystemClock.elapsedRealtime()
        chronometer!!.start()
    }

    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mPlayer = null
        //showing the play button
        imageViewPlay!!.setImageResource(R.drawable.ic_play)
        chronometer!!.stop()
    }

    private fun prepareforStop() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder)
        imageViewRecord!!.visibility = View.VISIBLE
        imageViewStop!!.visibility = View.GONE
        linearLayoutPlay!!.visibility = View.VISIBLE
    }

    private fun stopRecording() {
        try {
            mRecorder!!.stop()
            mRecorder!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        //starting the chronometer
        chronometer!!.stop()
        chronometer!!.base = SystemClock.elapsedRealtime()

        //Creating BO
        if (!isAudioSave) {
            imageBase64String = FileBase64.encodeBase64File(fileName)
            createImageUploadBO(null)
            //showing the play button
            Toast.makeText(mContext, "Recording saved successfully.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPlaying() {
        mPlayer = MediaPlayer()
        Log.d("instartPlaying", fileName!!)
        try {
            mPlayer!!.setDataSource(fileName)
            mPlayer!!.prepare()
            mPlayer!!.start()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }
        //making the imageview pause button
        imageViewPlay!!.setImageResource(R.drawable.ic_pause)
        seekBar!!.progress = lastProgress
        mPlayer!!.seekTo(lastProgress)
        seekBar!!.max = mPlayer!!.duration
        seekUpdation()
        chronometer!!.start()
        /** once the audio is complete, timer is stopped here */
        mPlayer!!.setOnCompletionListener {
            imageViewPlay!!.setImageResource(R.drawable.ic_play)
            isPlaying = false
            chronometer!!.stop()
        }
        /** moving the track as per the seekBar's position */
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mPlayer != null && fromUser) {
                    //here the track's progress is being changed as per the progress bar
                    mPlayer!!.seekTo(progress)
                    //timer is being updated as per the progress of the seekbar
                    chronometer!!.base = SystemClock.elapsedRealtime() - mPlayer!!.currentPosition
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    var runnable = Runnable { seekUpdation() }

    private fun seekUpdation() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer!!.currentPosition
            seekBar!!.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }


    private fun isValidated(): Boolean {
        if (etDate?.text.toString().isEmpty()) {
            tilDate?.error = "Please select date!"
            tilDate?.requestFocus()
            return false
        } else {
            tilDate?.isErrorEnabled = false
        }

        if (getPatientListBO == null || getPatientListBO?.PatientName.equals("No Data Found",
                false)
        ) {
            CommonMethods.showDialogForError(mContext!!, "Please select patient name!")
            return false
        }

        if (etTypeSpin?.text.toString().equals("Test Result", false)) {
            if (getPatientTestListBO == null || getPatientTestListBO?.Title.equals("No Data Found",
                    false)
            ) {
                CommonMethods.showDialogForError(mContext!!,
                    "Please select patient test list name/code!")
                return false
            }
        }

        if (etTypeSpin?.text.toString().isEmpty()) {
            tilTypeSpin?.error = "Type required!"
            tilTypeSpin?.requestFocus()
            return false
        } else {
            tilTypeSpin?.isErrorEnabled = false
        }

        if (!etTypeSpin?.text.toString().equals("Test Result", false)) {
            if (etLocation?.text.toString().isEmpty()) {
                tilLocation?.error = "Location required!"
                tilLocation?.requestFocus()
                return false
            } else {
                tilLocation?.isErrorEnabled = false
            }
        }

        if (etRemark?.text.toString().isEmpty()) {
            tilRemark?.error = "Remark required!"
            tilRemark?.requestFocus()
            return false
        } else {
            tilRemark?.isErrorEnabled = false
        }

        return true
    }
}