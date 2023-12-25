package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.NotificationManagerCompat
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AddVaccinationFragment : BaseFragment(), View.OnClickListener {

    //Declaration..
    private val TAG = "AddVaccinationFragment"

    //TextInputLayout declaration...
    private var tilVaccinationName: TextInputLayout? = null
    private var tilLowerAge: TextInputLayout? = null
    private var tilUpperAge: TextInputLayout? = null
    private var tilBWWY: TextInputLayout? = null

    //TextInputEditText declaration..
    private var etVaccinationName: TextInputEditText? = null
    private var etLowerAge: TextInputEditText? = null
    private var etUpperAge: TextInputEditText? = null

    //AutoCompleteTextView declaration..
    private var actBWMY: AutoCompleteTextView? = null

    //AppCompatButton declaration..
    private var btnAddNewVaccination: AppCompatButton? = null

    private var vaccinationPatientBO: VaccinationPatientBO? = null

    private val bwmy = arrayOf("Birth", "Weeks", "Months", "Years")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation..
        initViews(view)
    }

    override val activityLayout: Int
        get() = R.layout.fragment_add_vaccination

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews(view: View) {
        (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.vaccination)

        //TextInputLayout binding..
        tilVaccinationName = view.findViewById(R.id.til_vaccination_name)
        tilUpperAge = view.findViewById(R.id.til_upper_age)
        tilLowerAge = view.findViewById(R.id.til_lower_age)
        tilBWWY = view.findViewById(R.id.til_bwmy)

        //TextInputEditText binding..
        etVaccinationName = view.findViewById(R.id.et_vaccination_name)
        etUpperAge = view.findViewById(R.id.et_upper_age)
        etLowerAge = view.findViewById(R.id.et_lower_age)

        //AppCompatButton binding...
        btnAddNewVaccination = view.findViewById(R.id.btn_add_new_vaccination)
        btnAddNewVaccination?.setOnClickListener(this)

        //AutoCompleteTextView binding..
        actBWMY = view.findViewById(R.id.actv_bwmy)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, bwmy)
        actBWMY?.setAdapter(adapter)
        actBWMY?.keyListener = null
        actBWMY?.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_add_new_vaccination -> {
                if (isValidated()) {
                    addVaccination()
                }
            }
        }
    }

    private fun addVaccination() {
        val params: MutableMap<String, String> = HashMap()
        params["VaccinationId"] = "0"
        params["GivenTime"] = ""
        params["make"] = ""
        params["batch"] = ""
        params["Remark"] = ""
        params["dateofvisit"] = CommonMethods.getTodayDate("MM/dd/yyyy")
        params["vaccinationName"] = etVaccinationName?.text.toString()
        params["LowerAge"] = etLowerAge?.text.toString()
        params["UpperAge"] = etUpperAge?.text.toString()
        params["BWMY"] = actBWMY?.text.toString()
        params["username"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME).toString()
        params["CompanyId"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "addVaccination: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertupdatevaccinemaster(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
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

    private fun setData() {
        etVaccinationName?.setText(vaccinationPatientBO?.vaccinationName)
        val parts = vaccinationPatientBO?.Age?.split("-")!!.toTypedArray()
        etLowerAge?.setText(parts[0])
        val stUpperAge = parts[1].split(" ").toTypedArray()
        etUpperAge?.setText(stUpperAge[0])
        val stactBWMY = vaccinationPatientBO?.Age?.split(" ")!!.toTypedArray()
        actBWMY?.setText(stactBWMY[1])

        //make disable all edittext
        etVaccinationName?.isEnabled = false
        etLowerAge?.isEnabled = false
        etUpperAge?.isEnabled = false
        actBWMY?.isEnabled = false
        actBWMY?.isClickable = false
    }

   /* override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                // handle back button's click listener
                (context as DashBoardActivity).navController.popBackStack()
                return@OnKeyListener true
            }
            false
        })
    }*/

    private fun isValidated(): Boolean {

        if (etVaccinationName?.text.toString().isEmpty()) {
            tilVaccinationName?.error = "Vaccination Name required!"
            tilVaccinationName?.requestFocus()
            return false
        } else {
            tilVaccinationName?.isErrorEnabled = false
        }

        if (etLowerAge?.text.toString().isEmpty()) {
            tilLowerAge?.error = "Lower Age required!"
            tilLowerAge?.requestFocus()
            return false
        } else {
            tilLowerAge?.isErrorEnabled = false
        }

        if (etUpperAge?.text.toString().isEmpty()) {
            tilUpperAge?.error = "Upper Age required!"
            tilUpperAge?.requestFocus()
            return false
        } else {
            tilUpperAge?.isErrorEnabled = false
        }

        if (actBWMY?.text.toString().isEmpty()) {
            tilBWWY?.error = "Please select age type!"
            tilBWWY?.requestFocus()
            return false
        } else {
            tilBWWY?.isErrorEnabled = false
        }


        return true
    }
}