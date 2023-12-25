package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.ImageUploadBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.williamww.silkysignature.views.SignaturePad
import com.williamww.silkysignature.views.SignaturePad.OnSignedListener
import java.io.ByteArrayOutputStream
import java.util.*

class ConsentFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "ConsentFragment"
    
    //Declaration..
    //TextView declaration...
    private var tvConsentText:TextView?=null

    //Signature Pad..
    private var signaturePad:SignaturePad?=null

    //CheckBox..
    private var chAcceptConsent:CheckBox?=null

    //AppcompatButton
    private var btnSubmitConsent:AppCompatButton?=null
    private var btnClearConsent:AppCompatButton?=null

    //BusinessObject declaration..
    private var getPatientListBO: GetPatientListBO? = null
    private var isSigned=false
    
    private var signatureBase64String:String=""

    override val activityLayout: Int
        get() = R.layout.fragment_consent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable.
        val arguments = requireArguments()
        getPatientListBO = arguments.getParcelable("patient_bo")

        //basic intialisation..
        initViews(view)
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(view: View){
        //Binding Views...
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.submit_consent)

        //Textview binding..
        tvConsentText=view.findViewById(R.id.tv_consent_text)
        tvConsentText?.text="I  Mr/Ms ${getPatientListBO?.PatientName} S/O D/O W/O ${getPatientListBO?.age} Years old giving consent for collection of my blood / Urine/ Stool / Other  sample to" +
                " ${CommonMethods.getPrefrence(mContext!!,AllKeys.LABNAME_C)} Lab to ${CommonMethods.getPrefrence(mContext!!,AllKeys.PERSON_NAME)}"

        //SignaturePad binding...
        signaturePad=view.findViewById(R.id.signature_pad)

        //CheckBox binding..
        chAcceptConsent=view.findViewById(R.id.cb_accept_consent)

        //AppCompatButton binding..
        btnSubmitConsent=view.findViewById(R.id.btn_submit_consent)
        btnClearConsent=view.findViewById(R.id.btn_clear)

        //SetOnClickListners..
        btnSubmitConsent?.setOnClickListener(this)
        btnClearConsent?.setOnClickListener(this)

        signaturePad?.setOnSignedListener(object : OnSignedListener {
            override fun onStartSigning() {
                isSigned=false
            }

            override fun onSigned() {
                isSigned=true
            }

            override fun onClear() {
                //Event triggered when the pad is cleared
                isSigned=false
            }
        })
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_submit_consent->{
                if(isValidated()){
                    val signatureBitmap=signaturePad?.signatureBitmap
                    val out = ByteArrayOutputStream()
                    if (BuildConfig.DEBUG && signatureBitmap == null) {
                        error("Assertion failed")
                    }
                    signatureBitmap!!.compress(Bitmap.CompressFormat.JPEG, 75, out)
                    //val decodedImage = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
                    val byteArray = out.toByteArray()

                    signatureBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                    //add data to imageUploadBo and arraylist reset all fields....
                    createImageUploadBO(signatureBitmap)
                }
            }

            R.id.btn_clear->{
                signaturePad?.clear()
            }
        }
    }

    private fun createImageUploadBO(thumbnail: Bitmap?) {
        val imageUploadBO = ImageUploadBO()
        imageUploadBO.setRegno(getPatientListBO?.regno.toString())
        imageUploadBO.setSeqno("1")
        imageUploadBO.setCompId(AllKeys.COMPANY_ID)
        imageUploadBO.setTestName("")
        imageUploadBO.setTlcode("")
        imageUploadBO.setBitmap(thumbnail)
        imageUploadBO.setF(signatureBase64String)
        imageUploadBO.setType("Signature")
        imageUploadBO.setRemark("Signature")
        imageUploadBO.setLocation("Signature")
        
        Log.e(TAG, "onActivityResult: $imageUploadBO")

        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            /*for (temp in imageUploadBOArrayList)
            {}*/
            uploadAllPhotos(imageUploadBO)
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    private fun uploadAllPhotos(imageUploadBO: ImageUploadBO) {
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
            ) { dialogInterface: com.shreyaspatil.MaterialDialog.interfaces.DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                (context as DashBoardActivity).navController.popBackStack()
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    private fun isValidated(): Boolean {
        if(!isSigned){
            CommonMethods.showDialogForError(mContext!!,"Please sign in signature pad!")
            return false
        }

        if(chAcceptConsent?.isChecked == false){
            CommonMethods.showDialogForError(mContext!!,"Please accept the consent!")
            return false
        }

        return true

    }
}