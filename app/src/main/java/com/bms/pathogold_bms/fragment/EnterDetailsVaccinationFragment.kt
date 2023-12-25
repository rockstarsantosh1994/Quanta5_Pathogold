package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.system.Os.remove
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import java.util.*

class EnterDetailsVaccinationFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "EnterDetailsVaccination"

    //Declaration...
    //TextInputLayout Declaration...
    private var tilVaccinationName:TextInputLayout?=null
    private var tilGivenTime:TextInputLayout?=null
    private var tilDateOfVisit:TextInputLayout?=null
    private var tilMake:TextInputLayout?=null
    private var tilBatch:TextInputLayout?=null
    private var tilRemark:TextInputLayout?=null

    //TextInputEditText Declaration...
    private var etVaccinationName:TextInputEditText?=null
    private var etGivenTime:TextInputEditText?=null
    private var etDateOfVisit:TextInputEditText?=null
    private var etMake:TextInputEditText?=null
    private var etBatch:TextInputEditText?=null
    private var etRemark:TextInputEditText?=null

    //AppCompatButton declaration
    private var btnSubmit:AppCompatButton?=null

    private var getPatientListBO: GetPatientListBO? = null
    private var vaccinationPatientBO:VaccinationPatientBO?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable...
        val arguments = requireArguments()
        getPatientListBO = arguments.getParcelable("patient_bo")
        vaccinationPatientBO = arguments.getParcelable("vaccination_patient_bo")

        Log.e(TAG, "onViewCreated: getPatientListBO \n $getPatientListBO" )
        Log.e(TAG, "onViewCreated: vaccinationPatientBo \n $vaccinationPatientBO" )

        //basic intialisatoin..
        initViews(view)
    }

    override val activityLayout: Int
        get() = R.layout.fragment_enter_details_vaccination

    private fun initViews(view: View) {
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.enter_details)

        //TextInputLayout binding...
        tilVaccinationName=view.findViewById(R.id.til_vaccination_name)
        tilGivenTime = view.findViewById(R.id.til_given_time)
        tilDateOfVisit= view.findViewById(R.id.til_date_of_visit)
        tilMake = view.findViewById(R.id.til_make)
        tilBatch = view.findViewById(R.id.til_batch)
        tilRemark = view.findViewById(R.id.til_remark)

        //TextEditText binding
        etVaccinationName = view.findViewById(R.id.et_vaccination_name)
        etGivenTime = view.findViewById(R.id.et_given_time)
        etDateOfVisit = view.findViewById(R.id.et_date_of_visit)
        etMake = view.findViewById(R.id.et_make)
        etBatch = view.findViewById(R.id.et_batch)
        etRemark = view.findViewById(R.id.et_remark)

        //AppCompatButton binding..
        btnSubmit = view.findViewById(R.id.btn_submit)

        //Click Listeners..
        btnSubmit?.setOnClickListener(this)
        etGivenTime?.setOnClickListener(this)
        etDateOfVisit?.setOnClickListener(this)

        //setToday's date to EditText fields
        etVaccinationName?.setText(vaccinationPatientBO?.vaccinationName)
        etGivenTime?.setText(CommonMethods.getTodayDate("MM/dd/yyyy HH:mm:ss"))
        etDateOfVisit?.setText(CommonMethods.getTodayDate("MM/dd/yyyy HH:mm:ss"))
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.et_given_time->{
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        etGivenTime?.setText(String.format("%02d",(monthOfYear + 1)) + "/" + String.format("%02d",(dayOfMonth))  + "/" + year+" "+CommonMethods.getTodayDate("HH:mm:ss"))
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.et_date_of_visit->{
                // Get Current Date
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        etDateOfVisit?.setText(String.format("%02d",(monthOfYear + 1)) + "/" + String.format("%02d",(dayOfMonth))  + "/" + year+" "+CommonMethods.getTodayDate("HH:mm:ss"))
                        //Date currentDate = new Date();
                    }, mYear, mMonth, mDay)
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            R.id.btn_submit->{
                if(isValidated()){
                    insertVaccinationPatient()
                }
            }

        }
    }

    private fun insertVaccinationPatient() {
        val params: MutableMap<String, String> = HashMap()

        params["pepatid"] = getPatientListBO?.PePatID.toString()
        params["VaccinationId"] = vaccinationPatientBO?.VaccinationId.toString()
        params["GivenTime"] = etGivenTime?.text.toString()
        params["dateofvisit"] = etDateOfVisit?.text.toString()
        params["make"] = etMake?.text.toString()
        params["batch"] = etBatch?.text.toString()
        params["Remark"] = etRemark?.text.toString()
        params["username"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME).toString()
        params["CompanyId"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "insertVaccinationPatient: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertUpdateVaccinationPatient(params,
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

    fun showDialogForSuccess(context: Context, message: String) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            .setAnimation(R.raw.success_exploration)
            .setPositiveButton("Ok"
            ) { dialogInterface: DialogInterface, which: Int ->
                // Success Operation

                // val bundle=Bundle()
                //bundle.putParcelable("patient_bo", getPatientListBO)
                (context as DashBoardActivity).navController.popBackStack(R.id.enterDetailsVaccinationFragment, true)
                dialogInterface.dismiss()
                //context.finish()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }



    private fun isValidated(): Boolean {
        if (etVaccinationName?.text.toString().isEmpty()) {
            tilVaccinationName?.error = "Vaccination Name required!"
            tilVaccinationName?.requestFocus()
            return false
        } else {
            tilVaccinationName?.isErrorEnabled = false
        }

        if (etGivenTime?.text.toString().isEmpty()) {
            tilGivenTime?.error = "Given Time required!"
            tilGivenTime?.requestFocus()
            return false
        } else {
            tilGivenTime?.isErrorEnabled = false
        }

        if (etDateOfVisit?.text.toString().isEmpty()) {
            tilDateOfVisit?.error = "Date of visit required!"
            tilDateOfVisit?.requestFocus()
            return false
        } else {
            tilDateOfVisit?.isErrorEnabled = false
        }

        if (etMake?.text.toString().isEmpty()) {
            tilMake?.error = "Make required!"
            tilMake?.requestFocus()
            return false
        } else {
            tilMake?.isErrorEnabled = false
        }

        if (etBatch?.text.toString().isEmpty()) {
            tilBatch?.error = "Batch required!"
            tilBatch?.requestFocus()
            return false
        } else {
            tilBatch?.isErrorEnabled = false
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