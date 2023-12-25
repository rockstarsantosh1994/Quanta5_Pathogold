package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.SelectedTestAdapter
import com.bms.pathogold_bms.model.testcode.GetTestCodeResponse
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.util.HashMap

class ViewTestFragment : BaseFragment(),SelectedTestAdapter.DeleteSelectedTest {

    private val TAG = "ViewTestFragment"

    //LinearLayout declaration..
    private var llViewTestView: LinearLayout? = null
    private var llNoDataFound: LinearLayout? = null

    //RecyclerView declaration..
    private var rvViewTest: RecyclerView? = null

    //TextView declaration..
    private var tvTotalTestPrice: TextView?=null

    private var viewAppointmentBO: ViewAppointmentBO? = null

    override val activityLayout: Int
        get() = R.layout.fragment_view_test

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation...
        initViews(view)

        viewAppointmentBO = arguments?.getParcelable("view_app_bo")

        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            viewSelectedTestDetails(viewAppointmentBO?.BookApp_Id.toString())
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    private fun initViews(view: View) {
        //Linearlayout binding..
        llViewTestView = view.findViewById(R.id.ll_view_test_view)
        llNoDataFound = view.findViewById(R.id.ll_no_data_found_view)

        //Textview binding...
        tvTotalTestPrice = view.findViewById(R.id.tv_total_test_price)

        //RecyclerView binding..
        rvViewTest = view.findViewById(R.id.rv_view_test)
        val linearLayoutManager = LinearLayoutManager(mContext!!)
        rvViewTest?.layoutManager = linearLayoutManager
    }

    private fun viewSelectedTestDetails(appointmentId: String) {
        val params: MutableMap<String, String> = HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        params["appointmentid"] = appointmentId

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.viewTestCode(params, object : ApiRequestHelper.OnRequestComplete {
                @SuppressLint("SetTextI18n")
                override fun onSuccess(`object`: Any) {
                    val getTestCodeResponse = `object` as GetTestCodeResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getTestCodeResponse")
                    if (getTestCodeResponse.ResponseCode == 200) {
                        if (getTestCodeResponse.ResultArray.size > 0) {
                            llViewTestView?.visibility = View.VISIBLE
                            llNoDataFound?.visibility = View.GONE

                            val selectedTextAdapter = SelectedTestAdapter(mContext!!, getTestCodeResponse.ResultArray,this@ViewTestFragment,View.GONE)
                            rvViewTest?.adapter = selectedTextAdapter

                            var testTotalRate=0
                            for (temp in getTestCodeResponse.ResultArray) {
                                //Total of all selected test rate..
                                testTotalRate += temp.rate.toInt()
                            }

                            tvTotalTestPrice?.text = "Total: $testTotalRate /-"
                        } else {
                            llViewTestView?.visibility = View.GONE
                            llNoDataFound?.visibility = View.VISIBLE
                        }
                    } else {
                        llViewTestView?.visibility = View.GONE
                        llNoDataFound?.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    override fun onDeleteTest(position: Int) {
        Log.e(TAG, "onDeleteTest: " )
    }
}