package com.bms.pathogold_bms.fragment

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import java.util.*

class MyProfileEditFragment : BaseFragment(), View.OnClickListener {

    //Declaration....
    //Toolbar declaration...
    private var toolbar: Toolbar?=null

    //TextInputLayout declaration...
    private var tilName: TextInputLayout?=null
    private var tilDOB: TextInputLayout?=null
    private var tilMobileNo: TextInputLayout?=null
    private var tilEmail: TextInputLayout?=null
    private var tilAddress: TextInputLayout?=null
    private var tilLabName: TextInputLayout?=null

    //TextInputEditext declaration...
    private var etName: TextInputEditText?=null
    private var etDOB: TextInputEditText?=null
    private var etMobileNo: TextInputEditText?=null
    private var etEmail: TextInputEditText?=null
    private var etAddress: TextInputEditText?=null
    private var etLabName: TextInputEditText?=null

    //RadioButton and radio group declaration..
    private var rgGender: RadioGroup?=null
    private var rgMale: RadioButton?=null
    private var rgFemale: RadioButton?=null

    //AppCompatButton declaration..
    private var btnProfileUpdate: AppCompatButton?=null

    private val TAG = "MyProfileEditActivity"
    private var stGender:String?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation..
        initViews(view)

        //set Data to editext..
        setDefault()
    }

    override val activityLayout: Int
        get() = R.layout.fragment_my_profile_edite

    private fun initViews(view: View) {
        //toolbar binding..
        /*toolbar = view.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = resources.getString(R.string.update_profile)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        //toolbar.setTextAlignment(TEXT_ALIGNMENT_CENTER);*/
        (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.my_profile)
        //TextInputLayout binding..
        tilName=view.findViewById(R.id.til_name)
        tilDOB=view.findViewById(R.id.til_date_of_birth)
        tilMobileNo=view.findViewById(R.id.til_mobile_number)
        tilEmail=view.findViewById(R.id.til_email_address)
        tilAddress=view.findViewById(R.id.til_address)
        tilLabName=view.findViewById(R.id.til_remark)

        //TextEditText binding...
        etName=view.findViewById(R.id.et_name)
        etDOB=view.findViewById(R.id.et_date_of_birth)
        etMobileNo=view.findViewById(R.id.et_mobile_number)
        etEmail=view.findViewById(R.id.et_email_address)
        etAddress=view.findViewById(R.id.et_address)
        etLabName=view.findViewById(R.id.et_labname)

        //Radiogroup and radio button binding..
        rgGender=view.findViewById(R.id.radio_gender)
        rgMale=view.findViewById(R.id.rb_male)
        rgFemale=view.findViewById(R.id.rb_female)

        //AppCompatButton binding..
        btnProfileUpdate=view.findViewById(R.id.btn_profile_update)

        //Click Listerners..
        btnProfileUpdate?.setOnClickListener(this)
        etDOB?.setOnClickListener(this)
        etDOB?.isFocusable =true
        etDOB?.isFocusableInTouchMode =true

        rgGender?.setOnCheckedChangeListener { group, checkedId ->
            stGender = (rgGender?.checkedRadioButtonId
                ?.let { view.findViewById<View>(it) } as RadioButton).text.toString()

        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.et_date_of_birth->{
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->
                        //  etDate?.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                        etDOB?.setText("" + (monthOfYear + 1) + "/" + dayOfMonth.toString() + "/" + year)
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.btn_profile_update->{
                //profile updated..
                if(mContext?.let { CommonMethods.isNetworkAvailable(it) } == true){
                    if(isValidated()){
                        updateProfile()
                    }

                }else{
                    showDialog(AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        }
    }

    private fun updateProfile(){
        val params: MutableMap<String, String> = HashMap()
        params["HSC_ID"] = CommonMethods.getPrefrence(mContext!!,AllKeys.HSC_ID).toString()
        params["Name"] = etName?.text.toString()
        params["MobileNo"] = etMobileNo?.text.toString()
        params["Sex"] = stGender.toString()
        params["Dob"] = etDOB?.text.toString()
        params["Email"] = etEmail?.text.toString()
        params["Address"] = etAddress?.text.toString()

        Log.e(TAG, "updateProfile:\n $params ")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.labBoyProfileUpdate(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        //Calling Intent...
                        //Update shared preferences..
                        CommonMethods.setPreference(mContext!!, AllKeys.PERSON_NAME,etName?.text.toString())
                        CommonMethods.setPreference(mContext!!, AllKeys.EMAIL,etEmail?.text.toString())
                        CommonMethods.setPreference(mContext!!, AllKeys.DOB,etDOB?.text.toString())
                        CommonMethods.setPreference(mContext!!, AllKeys.MOBILE_NO,etMobileNo?.text.toString())
                        CommonMethods.setPreference(mContext!!, AllKeys.ADDRESS,etAddress?.text.toString())
                        CommonMethods.setPreference(mContext!!, AllKeys.SEX,stGender)

                        val intent = Intent(mContext, DashBoardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        requireActivity().finish()

                        Toast.makeText(mContext, "" + commonResponse.Message, Toast.LENGTH_SHORT).show()
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

    private fun setDefault() {
        etName?.setText(CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME))
        etEmail?.setText(CommonMethods.getPrefrence(mContext!!, AllKeys.EMAIL))
        etMobileNo?.setText(CommonMethods.getPrefrence(mContext!!, AllKeys.MOBILE_NO))
        etAddress?.setText(CommonMethods.getPrefrence(mContext!!, AllKeys.ADDRESS))
        etDOB?.setText(CommonMethods.getPrefrence(mContext!!, AllKeys.DOB))
        etLabName?.setText(CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME_C))

        if(CommonMethods.getPrefrence(mContext!!, AllKeys.SEX).equals("Male")){
            rgMale?.isChecked=true
        }else if(CommonMethods.getPrefrence(mContext!!, AllKeys.SEX).equals("Female")){
            rgFemale?.isChecked=true
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


    private fun isValidated(): Boolean {

        if(etName?.text.toString().isEmpty()){
            tilName?.error="Name Required!"
            tilName?.requestFocus()
            return false
        }else{
            tilName?.isErrorEnabled = false
        }

        if(etMobileNo?.text.toString().isEmpty()){
            tilMobileNo?.error="Mobile No. Required!"
            tilMobileNo?.requestFocus()
            return false
        }else{
            tilMobileNo?.isErrorEnabled = false
        }

        if(etMobileNo?.text.toString().length!=10){
            tilMobileNo?.error="Invalid Mobile No.!"
            tilMobileNo?.requestFocus()
            return false
        }else{
            tilMobileNo?.isErrorEnabled = false
        }

        return true
    }
}