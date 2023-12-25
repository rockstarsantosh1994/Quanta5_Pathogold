package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsBO
import com.bms.pathogold_bms.model.otp.OtpResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.services.DigiPath
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.TimeUnit

class LabContactDetailsDialog : DialogFragment(), View.OnClickListener {

    private val TAG = "LabContactDetailsDialog"

    //TextInputLayout declaration..
    private var tilMobileNo: TextInputLayout? = null
    private var tilOtp: TextInputLayout? = null
    private var tilMessage: TextInputLayout? = null

    //TextInputEditText declaration..
    private var etMobileNo: TextInputEditText? = null
    private var etOtp: TextInputEditText? = null
    private var etMessage: TextInputEditText? = null

    //ImageView declaration..
    private var ivClose: ImageView? = null

    //AppCompatButton declaration..
    private var btnGetOtp: AppCompatButton? = null
    private var btnSubmit: AppCompatButton? = null

    private var stOtp: String? = null

    private var digiPath: DigiPath? = null

    //Declare timer
    private var cTimer: CountDownTimer? = null

    //BusinessObject declaration...
    private var getLabDetailsBO: GetLabDetailsBO? = null

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        digiPath = requireActivity().application as DigiPath
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lab_contact_details_dialog, container, false)

        //basic intialisation..
        initViews(view)

        val arguments = arguments
        if (BuildConfig.DEBUG && arguments == null) {
            error("Assertion failed")
        }

        if (getArguments()?.getParcelable<GetLabDetailsBO>("lab_detail_bo") != null) {
            getLabDetailsBO = getArguments()?.getParcelable("lab_detail_bo")
        }
        return view
    }

    private fun initViews(view: View?) {
        //TextInputLayout binding..
        tilMobileNo = view?.findViewById(R.id.til_mobile_number)
        tilOtp = view?.findViewById(R.id.til_otp)
        tilMessage = view?.findViewById(R.id.til_message)

        //TextEditText binding..
        etMobileNo = view?.findViewById(R.id.et_mobile_number)
        etOtp = view?.findViewById(R.id.et_otp)
        etMessage = view?.findViewById(R.id.et_message)

        //AppcompatButton binding..
        btnGetOtp = view?.findViewById(R.id.btn_get_otp)
        btnSubmit = view?.findViewById(R.id.btn_submit_message)

        //ImageView binding..
        ivClose = view?.findViewById(R.id.iv_close)

        //Click Listeners..
        btnGetOtp?.setOnClickListener(this)
        btnSubmit?.setOnClickListener(this)
        ivClose?.setOnClickListener(this)

       /* etOtp?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                if(stOtp?.equals(etOtp?.text.toString()) == true){
                    etMobileNo?.isEnabled=false
                    etOtp?.isEnabled=false
                    etOtp?.append("$stOtp (Verified)")
                }else{
                    etMobileNo?.isEnabled=true
                    etOtp?.isEnabled=true
                }
            }
        })*/
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_close->{
                if(cTimer!=null)
                    cTimer!!.cancel()
                dismiss()
            }

            R.id.btn_get_otp -> {
                if (isValidatedMobile()) {
                    sendOtp()
                }
            }

            R.id.btn_submit_message -> {
                if(CommonMethods.isNetworkAvailable(requireContext())){
                    if(isValidated()){
                        sendMessageToLab()
                    }
                }else{
                    CommonMethods.showDialogForError(requireContext(),AllKeys.NO_INTERNET_AVAILABLE)
                }
            }
        }
    }

    private fun sendMessageToLab() {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["labmobile"] = getLabDetailsBO?.labphone.toString()
        params["Message"] = etMessage?.text.toString()
        params["labcode"] = getLabDetailsBO?.labcode.toString()
        params["usermobile"] =etMobileNo?.text.toString()
        params["labname"] = getLabDetailsBO?.labname.toString()

        Log.e(TAG, "sendMessageToLab: $params")

        val progress = ProgressDialog(context)
        progress.setMessage("Sending message...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.sendMsgByLab(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "sendMessageToLab: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        Toast.makeText(requireContext(), "Message send successfully", Toast.LENGTH_LONG).show()
                        dismiss()
                    } else {
                        CommonMethods.showDialogForError(requireContext(), commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "insertVaccinationPatient: $apiResponse")
                }
            })
    }

    private fun sendOtp() {
        val params: MutableMap<String, String> = HashMap()
        params["mobile"] = etMobileNo?.text.toString()

        Log.e(TAG, "getDailyCashSummary: $params")
        val progress = ProgressDialog(context)
        progress.setMessage("Sending Otp...Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.verifyPatientByOtp(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val otpResponse = `object` as OtpResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $otpResponse")
                    if (otpResponse.ResponseCode == 200) {
                        if (otpResponse.ResultArray.size > 0) {
                            stOtp = otpResponse.ResultArray[0].otp

                            //start timer function
                            cTimer = object : CountDownTimer(30000, 1000) {
                                @SuppressLint("SetTextI18n")
                                override fun onTick(millisUntilFinished: Long) {
                                    btnGetOtp?.isEnabled = false
                                    btnGetOtp?.text = context?.getString(R.string.resend_otp)+"\n"+TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)+" sec"
                                }

                                override fun onFinish() {
                                    btnGetOtp?.isEnabled = true
                                    btnGetOtp?.text = context?.getString(R.string.resend_otp)
                                }
                            }
                            cTimer!!.start()
                        } else {
                            CommonMethods.showDialogForError(context!!, otpResponse.Message)
                        }
                    } else {
                        CommonMethods.showDialogForError(context!!, otpResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun isValidatedMobile(): Boolean {
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

        return true
    }

    private fun isValidated(): Boolean {
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

        if (etOtp?.text.toString().isEmpty()) {
            tilOtp?.error = "Otp Required!"
            tilOtp?.requestFocus()
            return false
        } else {
            tilOtp?.isErrorEnabled = false
        }

        if (etOtp?.text.toString().length != 4) {
            tilOtp?.error = "Invalid Otp!"
            tilOtp?.requestFocus()
            return false
        } else {
            tilOtp?.isErrorEnabled = false
        }

        if (etOtp?.text.toString() != stOtp) {
            tilOtp?.error = "Incorrect Otp Enter!"
            tilOtp?.requestFocus()
            return false
        } else {
            tilOtp?.isErrorEnabled = false
        }

        if (etMessage?.text.toString().isEmpty()) {
            tilMessage?.error = "Please type your message!"
            tilMessage?.requestFocus()
            return false
        } else {
            tilMessage?.isErrorEnabled = false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(cTimer!=null)
            cTimer!!.cancel()
    }
}