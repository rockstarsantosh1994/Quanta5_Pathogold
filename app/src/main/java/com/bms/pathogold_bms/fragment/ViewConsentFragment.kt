package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.GetClientAdapter
import com.bms.pathogold_bms.model.getclientuploadedfile.GetClientUploadedBO
import com.bms.pathogold_bms.model.getclientuploadedfile.GetClientUploadedResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bumptech.glide.Glide
import java.util.HashMap

class ViewConsentFragment : BaseFragment() {

    private val TAG = "ViewConsentFragment"

    //Declaration..
    //TextView declaration...
    private var tvConsentText: TextView?=null
    private var tvNoDataFound:TextView?=null

    //Imageview declaration
    private var ivSignatureView:ImageView?=null

    //BusinessObject declaration..
    private var getPatientListBO: GetPatientListBO? = null

    override val activityLayout: Int
        get() = R.layout.fragment_view_consent2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable.
        val arguments = requireArguments()
        getPatientListBO = arguments.getParcelable("patient_bo")

        //basic intialisation..
        initViews(view)

        if(CommonMethods.isNetworkAvailable(mContext!!)){
            getClientUploadedFile(getPatientListBO!!.regno)
        }else{
            CommonMethods.showDialogForError(mContext as Activity,AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    private fun initViews(view: View) {
        //Binding Views...
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.view_consent)

        //Textview binding..
        tvConsentText=view.findViewById(R.id.tv_consent_text)
        tvNoDataFound=view.findViewById(R.id.tv_no_data_found)

        //ImageView binding..
        ivSignatureView=view.findViewById(R.id.iv_sign)
    }

    private fun getClientUploadedFile(regNo: String){
        val params: MutableMap<String, String> = HashMap()
        params["Regno"] =regNo

        Log.e(TAG, "getPatientTestList: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getClientUploadedFile(params,
            object : ApiRequestHelper.OnRequestComplete {
                @SuppressLint("SetTextI18n")
                override fun onSuccess(`object`: Any) {
                    val getClientUploadedResponse = `object` as GetClientUploadedResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getClientUploadedResponse")
                    if (getClientUploadedResponse.ResponseCode == 200) {
                        if (getClientUploadedResponse.ResultArray.size > 0) {
                            var url=""
                            for (temp in getClientUploadedResponse.ResultArray) {
                                if(temp.type == "Signature"){
                                    url=temp.url
                                    break
                                }
                            }

                            showView()

                            tvConsentText?.text="I  Mr/Ms ${getPatientListBO?.PatientName} S/O  D/O W/O ${getPatientListBO?.age} Years old giving consent for collection of my blood / Urine/ Stool / Other  sample to" +
                                    " ${CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME_C)} Lab to ${CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)}"

                            Glide.with(requireContext()).load(url)
                                //.placeholder(R.drawable.ic_user)
                                .into(ivSignatureView!!)
                        } else {
                            hideView()
                        }
                    } else {
                        hideView()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun hideView(){
        tvConsentText?.visibility = View.GONE
        ivSignatureView?.visibility = View.GONE
        tvNoDataFound?.visibility = View.VISIBLE
    }

    private fun showView(){
        tvConsentText?.visibility = View.VISIBLE
        ivSignatureView?.visibility = View.VISIBLE
        tvNoDataFound?.visibility = View.GONE
    }
}