package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.DailyCashRegisterAdapter
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterBO
import com.bms.pathogold_bms.utility.AllKeys
import java.util.*
import android.widget.Toast
import com.bms.pathogold_bms.activity.DashBoardActivity


class DailyCashRegisterFragment : BaseFragment(), View.OnClickListener {

    //Declaration...
    private val TAG = "DailyCashRegisterFragme"

    //EditText declaration..
    private var etSearch:EditText?=null

    //AppCompatButton declaration...
    private var btnApplyFilter:AppCompatButton?=null

    //RecyclerView declaration..
    private var rvDailyCashRegister:RecyclerView?=null

    //TextView declaration..
    private var tvNoDataFound:TextView?=null
    private var tvNoOfRecordsFound:TextView?=null

    private val DAILY_CASH_FILTER_DIALOG=1

    var linearLayoutManager:LinearLayoutManager?=null

    //Map list..
    private val getByDrMap: MutableMap<String, ArrayList<DailyCashRegisterBO>> = HashMap()
    private val getByCollectionCenterMap: MutableMap<String, ArrayList<DailyCashRegisterBO>> = HashMap()
    private val getByPatientNameMap: MutableMap<String, ArrayList<DailyCashRegisterBO>> = HashMap()
    private val getByStatusMap: MutableMap<String, ArrayList<DailyCashRegisterBO>> = HashMap()

    private var getByDrList:ArrayList<DailyCashRegisterBO>?= ArrayList()
    private var getByCollectionCenterList:ArrayList<DailyCashRegisterBO>?= ArrayList()
    private var getByPatientNameList:ArrayList<DailyCashRegisterBO>?= ArrayList()
    private var getStatusList:ArrayList<DailyCashRegisterBO>?= ArrayList()

    private var getDailyCashRegisterList:ArrayList<DailyCashRegisterBO>?= ArrayList()
    private var stName:String="no_name"
    private var isBackPressed:String="0"

    private var dailyCashRegisterAdapter:DailyCashRegisterAdapter?=null

    override val activityLayout: Int
        get() = R.layout.fragment_daily_cash_register

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Change android system's buttons color
        requireActivity().window.navigationBarColor = ContextCompat.getColor(mContext!!, R.color.purple_700)

        //basic intialisation...
        initViews(view)

        try{
            val arguments = arguments
            if (BuildConfig.DEBUG && arguments == null) {
                error("Assertion failed")
            }

            getDailyCashRegisterList?.clear()
            getDailyCashRegisterList=arguments?.getParcelableArrayList("daily_cash_register_list")
            //To show latest records show data on top
            getDailyCashRegisterList?.reverse()

            stName= arguments?.getString("stname").toString()
            isBackPressed=arguments?.getString("isBackPressed").toString()

            //if list has data then call below function..
            sortData()

            Log.e(TAG, "onViewCreated: "+getDailyCashRegisterList?.size )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun initViews(view: View) {
        //EditText binding..
        etSearch=view.findViewById(R.id.et_search)

        //AppCompatButton binding..
        btnApplyFilter=view.findViewById(R.id.btn_apply_filter)

        //Click Listeners..
        btnApplyFilter?.setOnClickListener(this)

        //TextView binding..
        tvNoDataFound=view.findViewById(R.id.tv_no_data_found)
        tvNoOfRecordsFound=view.findViewById(R.id.tv_no_of_records_count)

        //Recycler view binding...
        rvDailyCashRegister=view.findViewById(R.id.rv_daily_cash_register)
        linearLayoutManager=LinearLayoutManager(mContext)
        rvDailyCashRegister?.layoutManager=linearLayoutManager
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_apply_filter->{
                //fetch all keys from map...and set to list..
                val drKeyList=ArrayList<String>()
                val collectionCenterKeyList=ArrayList<String>()
                val patientNameKeyList=ArrayList<String>()
                val statusKeyList=ArrayList<String>()

                drKeyList.addAll(getByDrMap.keys.toTypedArray())
                collectionCenterKeyList.addAll(getByCollectionCenterMap.keys.toTypedArray())
                patientNameKeyList.addAll(getByPatientNameMap.keys.toTypedArray())
                statusKeyList.addAll(getByStatusMap.keys.toTypedArray())

                val dailyCashFilterDialog = DailyCashFilterDialog()
                val bundle = Bundle()
                bundle.putStringArrayList("drname_list",drKeyList)
                bundle.putStringArrayList("collection_center_list", collectionCenterKeyList)
                bundle.putStringArrayList("patient_name_list", patientNameKeyList)
                bundle.putStringArrayList("status_list", statusKeyList)

                dailyCashFilterDialog.arguments = bundle
                dailyCashFilterDialog.setTargetFragment(this, DAILY_CASH_FILTER_DIALOG)
                dailyCashFilterDialog.show(fragmentManager?.beginTransaction()!!, "DailyCashFilterDialog")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sortData(){
        if (getDailyCashRegisterList?.size!! > 0) {
            rvDailyCashRegister?.visibility=View.VISIBLE
            tvNoDataFound?.visibility=View.GONE

            tvNoOfRecordsFound?.text="No. of records "+getDailyCashRegisterList?.size

            dailyCashRegisterAdapter=DailyCashRegisterAdapter(mContext!!, getDailyCashRegisterList!!,stName)
            rvDailyCashRegister?.adapter=dailyCashRegisterAdapter

            Log.e(TAG, "sortData: visible ITemCount${rvDailyCashRegister?.adapter?.itemCount}")
            Log.e(TAG, "sortData: totalItemCount${rvDailyCashRegister?.childCount}")

            val lastVisiblePos: Int = linearLayoutManager?.findLastCompletelyVisibleItemPosition()!!
            val firstVisiblePosition: Int = linearLayoutManager?.findFirstVisibleItemPosition()!!

            Log.e(TAG, "sortData:lastVisiblePos $lastVisiblePos")
            Log.e(TAG, "sortData:firstVisiblePosition $firstVisiblePosition")

            for (temp in getDailyCashRegisterList!!) {
                //Map by DrName.
                if (getByDrMap.containsKey(temp.DocName.trim())) {
                    getByDrList = getByDrMap[temp.DocName.trim()]
                    getByDrList!!.add(temp)
                } else {
                    getByDrList = ArrayList()
                    getByDrList!!.add(temp)
                    getByDrMap[temp.DocName.trim()] = getByDrList!!
                }

                //Map By Collection Center
                if (getByCollectionCenterMap.containsKey(temp.Collection_Center.trim())) {
                    getByCollectionCenterList = getByCollectionCenterMap[temp.Collection_Center.trim()]
                    getByCollectionCenterList!!.add(temp)
                } else {
                    getByCollectionCenterList = ArrayList()
                    getByCollectionCenterList!!.add(temp)
                    getByCollectionCenterMap[temp.Collection_Center.trim()] = getByCollectionCenterList!!
                }

                //By Patient Name
                if (getByPatientNameMap.containsKey(temp.FirstName.trim()+" "+temp.LastName.trim())) {
                    getByPatientNameList = getByPatientNameMap[temp.FirstName.trim()+" "+temp.LastName.trim()]
                    getByPatientNameList!!.add(temp)
                } else {
                    getByPatientNameList = ArrayList()
                    getByPatientNameList!!.add(temp)
                    getByPatientNameMap[temp.FirstName.trim()+" "+temp.LastName.trim()] = getByPatientNameList!!
                }

                //By Status Name
                if (getByStatusMap.containsKey(temp.status.trim())) {
                    getStatusList = getByStatusMap[temp.status.trim()]
                    getStatusList!!.add(temp)
                } else {
                    getStatusList = ArrayList()
                    getStatusList!!.add(temp)
                    getByStatusMap[temp.status.trim()] = getStatusList!!
                }
            }
        }else{
            rvDailyCashRegister?.visibility=View.GONE
            tvNoDataFound?.visibility=View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            DAILY_CASH_FILTER_DIALOG->{
                if(resultCode == Activity.RESULT_OK) {
                    val bundle = data?.extras
                    val stSelectionType=bundle?.getString("st_selection_type").toString()
                    val stSortStringName=bundle?.getString("sort_string_name").toString()
                    etSearch?.setText("Filter By: $stSortStringName")
                        when(stSelectionType){
                            AllKeys.BY_COLLECTION_CENTER->{
                                val dailyCashRegisterAdapter= getByCollectionCenterMap[stSortStringName]?.let { DailyCashRegisterAdapter(mContext!!, it, stName) }
                                rvDailyCashRegister?.adapter=dailyCashRegisterAdapter

                                tvNoOfRecordsFound?.text="No. of records "+getByCollectionCenterMap[stSortStringName]?.size
                            }

                            AllKeys.BY_DOCTOR_NAME->{
                                val dailyCashRegisterAdapter= getByDrMap[stSortStringName]?.let { DailyCashRegisterAdapter(mContext!!, it, stName) }
                                rvDailyCashRegister?.adapter=dailyCashRegisterAdapter

                                tvNoOfRecordsFound?.text="No. of records "+getByDrMap[stSortStringName]?.size
                            }

                            AllKeys.BY_PATIENT_NAME->{
                                val dailyCashRegisterAdapter= getByPatientNameMap[stSortStringName]?.let { DailyCashRegisterAdapter(mContext!!, it, stName) }
                                rvDailyCashRegister?.adapter=dailyCashRegisterAdapter

                                tvNoOfRecordsFound?.text="No. of records "+getByPatientNameMap[stSortStringName]?.size
                            }

                            AllKeys.BY_STATUS->{
                                val dailyCashRegisterAdapter= getByStatusMap[stSortStringName]?.let { DailyCashRegisterAdapter(mContext!!, it, stName) }
                                rvDailyCashRegister?.adapter=dailyCashRegisterAdapter

                                tvNoOfRecordsFound?.text="No. of records "+getByStatusMap[stSortStringName]?.size
                            }
                        }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if(dailyCashRegisterAdapter!=null){
            dailyCashRegisterAdapter?.notifyDataSetChanged()
        }

        if(isBackPressed == "1"){
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
        }
    }
}