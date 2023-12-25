package com.bms.pathogold_bms.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.PagerAdapter
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import java.util.ArrayList

class ChatDashBoardFragment : BaseFragment() {

    private val TAG = "ChatActivity"
    private var tabLayout: TabLayout? = null
    private var mChat: TabItem? = null
    private var mContacts: TabItem? = null
    private var viewPager: ViewPager? = null
    private var pagerAdapter: PagerAdapter? = null

    private val consultationBOArrayList = ArrayList<ConsultationBO>()
    private val pheloboBOArrayList = ArrayList<ConsultationBO>()

    override val activityLayout: Int
        get() = R.layout.fragment_chat_dash_board

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mContext?.let { CommonMethods.isNetworkAvailable(it) } == true) {
            //getPhlebo List..
            getPhleboData()

            //Get consultation list
            getConsultationList()

        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }

        //basic intialisation..
        initViews(view)
    }

    private fun initViews(view: View) {
        tabLayout = view.findViewById(R.id.include)
        mChat = view.findViewById(R.id.tab_chat)
        mContacts = view.findViewById(R.id.tab_contacts)
        viewPager = view.findViewById(R.id.fragmentcontainer)

        pagerAdapter = PagerAdapter(childFragmentManager,mContext, tabLayout?.tabCount!!,consultationBOArrayList,pheloboBOArrayList)
        viewPager?.adapter=pagerAdapter

        tabLayout?.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position
                if (tab.position == 0 || tab.position == 1 || tab.position == 2) {
                    pagerAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }

    private fun getPhleboData() {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        // params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.USERTYPE).toString() }!!

        Log.e(TAG, "getPHleboData: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getPhleboList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val consultationResponse = `object` as ConsultationResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $consultationResponse")
                    if (consultationResponse.ResponseCode == 200) {
                        //Calling Intent...
                        // Toast.makeText(mContext, "" + getPatientResponse.Message, Toast.LENGTH_SHORT).show()
                        pheloboBOArrayList.addAll(consultationResponse.ResultArray)

                        Log.e(TAG, "onSuccess:Phelobo\n\n ${pheloboBOArrayList.size}")
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getConsultationList() {
        val params: MutableMap<String, String> = java.util.HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.LABNAME).toString() }!!
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getConsultationList: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getConsultantList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val consultationResponse = `object` as ConsultationResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $consultationResponse")
                    if (consultationResponse.ResponseCode == 200) {
                        //Calling Intent...
                        // Toast.makeText(mContext, "" + getPatientResponse.Message, Toast.LENGTH_SHORT).show()
                        consultationBOArrayList.addAll(consultationResponse.ResultArray)
                        Log.e(TAG, "onSuccess:Consultation\n\n ${consultationBOArrayList.size}")
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    /*override fun onResume() {
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
}