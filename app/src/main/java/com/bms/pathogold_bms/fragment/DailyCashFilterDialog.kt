package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.DailyCashFilterAdapter
import com.bms.pathogold_bms.utility.AllKeys
import java.util.*

class DailyCashFilterDialog : DialogFragment(), View.OnClickListener, DailyCashFilterAdapter.DailyCashSelect {

    //Declaration....
    //AppCompatButton Declaration..
    private var btnByDrName: AppCompatButton? = null
    private var btnByCollectionCenter: AppCompatButton? = null
    private var btnByPatientName: AppCompatButton? = null
    private var btnByStatus: AppCompatButton? = null

    //LinearLayout Declaration...
    private var llRecyclerViewUI: LinearLayoutCompat? = null

    //EditText declaraion..
    private var etSearch: EditText? = null

    //RecyclerView declaration...
    private var rvByDrCollectionPatient: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    //List declration..
    private val drKeyList = ArrayList<String>()
    private val collectionCenterKeyList = ArrayList<String>()
    private val patientNameKeyList = ArrayList<String>()
    private val statusKeyList = ArrayList<String>()

    private var dailyCashFilterAdapter:DailyCashFilterAdapter?=null

    private var stSelectionType:String=AllKeys.BY_DOCTOR_NAME

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_daily_cash_filter_dialog, container, false)
        //Compulsory light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //basic intialisation..
        initViews(view)

        //background colors of buttons..
        handleDesignOfButton(
            R.color.white,
            R.color.grey200,
            R.color.grey200,
            R.color.grey200,)

        val arguments = arguments
        if (BuildConfig.DEBUG && arguments == null) {
            error("Assertion failed")
        }

        drKeyList.addAll(arguments?.getStringArrayList("drname_list")!!)
        collectionCenterKeyList.addAll(arguments.getStringArrayList("collection_center_list")!!)
        patientNameKeyList.addAll(arguments.getStringArrayList("patient_name_list")!!)
        statusKeyList.addAll(arguments.getStringArrayList("status_list")!!)

        if(drKeyList.size>0){
            tvNoDataFound?.visibility=View.GONE
            llRecyclerViewUI?.visibility=View.VISIBLE
            dailyCashFilterAdapter=DailyCashFilterAdapter(requireContext(),drKeyList,this)
            rvByDrCollectionPatient?.adapter=dailyCashFilterAdapter
        }else{
            tvNoDataFound?.visibility=View.VISIBLE
            llRecyclerViewUI?.visibility=View.GONE
        }

        return view
    }

    private fun initViews(view: View?) {
        //AppCompatButton binding..
        btnByCollectionCenter = view?.findViewById(R.id.btn_sort_by_collection_center)
        btnByDrName = view?.findViewById(R.id.btn_sort_by_dr_name)
        btnByPatientName = view?.findViewById(R.id.btn_sort_by_patient_name)
        btnByStatus = view?.findViewById(R.id.btn_sort_by_status)

        //EditText binding....
        etSearch = view?.findViewById(R.id.et_search)

        //LinearLayout binding..
        llRecyclerViewUI = view?.findViewById(R.id.ll_recycler_view_ui)

        //TextView binding...
        tvNoDataFound = view?.findViewById(R.id.tv_no_data_found)

        //RecyclerView binding....
        rvByDrCollectionPatient = view?.findViewById(R.id.rv_by_dr_collection_patient)
        val linearLayoutManager = LinearLayoutManager(context)
        rvByDrCollectionPatient?.layoutManager = linearLayoutManager

        //ClickListeners...
        btnByCollectionCenter?.setOnClickListener(this)
        btnByDrName?.setOnClickListener(this)
        btnByPatientName?.setOnClickListener(this)
        btnByStatus?.setOnClickListener(this)

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_sort_by_collection_center -> {
                stSelectionType=AllKeys.BY_COLLECTION_CENTER
                handleDesignOfButton(
                    R.color.grey200,
                    R.color.white,
                    R.color.grey200,
                    R.color.grey200,)

                if(collectionCenterKeyList.size>0){
                    tvNoDataFound?.visibility=View.GONE
                    llRecyclerViewUI?.visibility=View.VISIBLE
                    dailyCashFilterAdapter=DailyCashFilterAdapter(requireContext(),collectionCenterKeyList,this)
                    rvByDrCollectionPatient?.adapter=dailyCashFilterAdapter
                }else{
                    tvNoDataFound?.visibility=View.VISIBLE
                    llRecyclerViewUI?.visibility=View.GONE
                }
            }

            R.id.btn_sort_by_dr_name -> {
                stSelectionType=AllKeys.BY_DOCTOR_NAME

                handleDesignOfButton(
                    R.color.white,
                    R.color.grey200,
                    R.color.grey200,
                    R.color.grey200,)

                if(drKeyList.size>0){
                    tvNoDataFound?.visibility=View.GONE
                    llRecyclerViewUI?.visibility=View.VISIBLE
                    dailyCashFilterAdapter=DailyCashFilterAdapter(requireContext(),drKeyList,this)
                    rvByDrCollectionPatient?.adapter=dailyCashFilterAdapter
                }else{
                    tvNoDataFound?.visibility=View.VISIBLE
                    llRecyclerViewUI?.visibility=View.GONE
                }
            }

            R.id.btn_sort_by_patient_name -> {
                stSelectionType=AllKeys.BY_PATIENT_NAME

                handleDesignOfButton(
                    R.color.grey200,
                    R.color.grey200,
                    R.color.white,
                    R.color.grey200,)

                if(patientNameKeyList.size>0){
                    tvNoDataFound?.visibility=View.GONE
                    llRecyclerViewUI?.visibility=View.VISIBLE
                    dailyCashFilterAdapter=DailyCashFilterAdapter(requireContext(),patientNameKeyList,this)
                    rvByDrCollectionPatient?.adapter=dailyCashFilterAdapter
                }else{
                    tvNoDataFound?.visibility=View.VISIBLE
                    llRecyclerViewUI?.visibility=View.GONE
                }
            }

            R.id.btn_sort_by_status -> {
                stSelectionType=AllKeys.BY_STATUS

                handleDesignOfButton(
                    R.color.grey200,
                    R.color.grey200,
                    R.color.grey200,
                    R.color.white)

                if(patientNameKeyList.size>0){
                    tvNoDataFound?.visibility=View.GONE
                    llRecyclerViewUI?.visibility=View.VISIBLE
                    dailyCashFilterAdapter=DailyCashFilterAdapter(requireContext(),statusKeyList,this)
                    rvByDrCollectionPatient?.adapter=dailyCashFilterAdapter
                }else{
                    tvNoDataFound?.visibility=View.VISIBLE
                    llRecyclerViewUI?.visibility=View.GONE
                }
            }
        }
    }

    private fun handleDesignOfButton(
        btnByDrNameColor: Int,
        btnByCollectionCenterColor: Int,
        btnByPatientNameColor: Int,
        btnByStatsColor:Int) {
        btnByDrName?.backgroundTintList = ColorStateList.valueOf(resources.getColor(btnByDrNameColor))
        btnByCollectionCenter?.backgroundTintList = ColorStateList.valueOf(resources.getColor(btnByCollectionCenterColor))
        btnByPatientName?.backgroundTintList = ColorStateList.valueOf(resources.getColor(btnByPatientNameColor))
        btnByStatus?.backgroundTintList = ColorStateList.valueOf(resources.getColor(btnByStatsColor))
    }

    override fun getDailyCashSelectName(stSelectName: String) {
        //When you want to send data to fragment
        val i = Intent()
        i.putExtra("sort_string_name", stSelectName)
        i.putExtra("st_selection_type",stSelectionType)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)

        dismiss()
    }

    private fun filterTable(text: String) {
        when(stSelectionType){
            AllKeys.BY_DOCTOR_NAME->{
                if (drKeyList.size > 0) {
                    val filteredList1: ArrayList<String> = ArrayList<String>()
                    for (item in drKeyList) {
                        if (text.let { item.lowercase(Locale.ROOT).contains(it) } || text.let { item.uppercase(
                                Locale.ROOT)
                                .contains(it) }) {
                            filteredList1.add(item)
                        }
                    }
                    //Log.e(TAG, "filter: size" + filteredList1.size());
                    // Log.e(TAG, "filter: List" + filteredList1.toString());
                    dailyCashFilterAdapter?.updateData(context, filteredList1)
                }
            }

            AllKeys.BY_PATIENT_NAME->{
                if (patientNameKeyList.size > 0) {
                    val filteredList1: ArrayList<String> = ArrayList<String>()
                    for (item in patientNameKeyList) {
                        if (text.let { item.lowercase(Locale.ROOT).contains(it) } || text.let { item.uppercase(
                                Locale.ROOT)
                                .contains(it) }) {
                            filteredList1.add(item)
                        }
                    }
                    //Log.e(TAG, "filter: size" + filteredList1.size());
                    // Log.e(TAG, "filter: List" + filteredList1.toString());
                    dailyCashFilterAdapter?.updateData(context, filteredList1)
                }
            }

            AllKeys.BY_COLLECTION_CENTER->{
                if (collectionCenterKeyList.size > 0) {
                    val filteredList1: ArrayList<String> = ArrayList<String>()
                    for (item in collectionCenterKeyList) {
                        if (text.let { item.lowercase(Locale.ROOT).contains(it) } || text.let { item.uppercase(
                                Locale.ROOT)
                                .contains(it) }) {
                            filteredList1.add(item)
                        }
                    }
                    //Log.e(TAG, "filter: size" + filteredList1.size());
                    // Log.e(TAG, "filter: List" + filteredList1.toString());
                    dailyCashFilterAdapter?.updateData(context, filteredList1)
                }
            }

            AllKeys.BY_STATUS->{
                if (collectionCenterKeyList.size > 0) {
                    val filteredList1: ArrayList<String> = ArrayList<String>()
                    for (item in collectionCenterKeyList) {
                        if (text.let { item.lowercase(Locale.ROOT).contains(it) } || text.let { item.uppercase(
                                Locale.ROOT)
                                .contains(it) }) {
                            filteredList1.add(item)
                        }
                    }
                    //Log.e(TAG, "filter: size" + filteredList1.size());
                    // Log.e(TAG, "filter: List" + filteredList1.toString());
                    dailyCashFilterAdapter?.updateData(context, filteredList1)
                }
            }
        }
    }
}