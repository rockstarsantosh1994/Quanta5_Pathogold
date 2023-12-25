package com.bms.pathogold_bms.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.GetCenterAndDoctorAdapter
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListBO
import com.bms.pathogold_bms.model.getrefcc.GetRefCCBO
import java.util.*
import kotlin.collections.ArrayList

class CenterAndDoctorDialog  : DialogFragment(), View.OnClickListener, GetCenterAndDoctorAdapter.GetRefCCAndDr{

    //Views Declarations....
    private val TAG = "CenterAndDoctorDialog"

    //EditText declaration..
    private var etSearch: EditText? = null

    //RecyclerView declaration...
    private var rvCCAndDr: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvCancel: TextView?=null
    private var tvSubmit: TextView?=null

    private val getRefCCBOList=ArrayList<GetRefCCBO>()
    private var getCenterAndDoctorAdapter: GetCenterAndDoctorAdapter?=null

    private var stReferenceType:String?=null

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_get_allergy_dialog, container, false)

        //Compulsory light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //basic intialisation..
        initViews(view)

        val arguments = arguments
        if (BuildConfig.DEBUG && arguments == null) {
            error("Assertion failed")
        }

        stReferenceType=getArguments()?.getString("ref_type")

        when(stReferenceType){
            "CC"->{
                etSearch?.hint="Search By Collection Center Name"
            }

            "DR"->{
                etSearch?.hint="Search By Doctor Reference Name"
            }
        }

        if (getArguments()?.getParcelableArrayList<GetAllergyListBO>("getreflist") != null) {
            getRefCCBOList.addAll(arguments?.getParcelableArrayList("getreflist")!!)
            getCenterAndDoctorAdapter = context?.let { GetCenterAndDoctorAdapter(it, getRefCCBOList,this) }
            rvCCAndDr?.adapter = getCenterAndDoctorAdapter
        }
        return view
    }

    private fun initViews(view: View){
        //Editext binding.
        etSearch = view.findViewById(R.id.et_search)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)
        tvCancel=view.findViewById(R.id.tv_cancel)
        tvSubmit=view.findViewById(R.id.tv_submit)

        //Recycler binding..
        rvCCAndDr = view.findViewById(R.id.rv_allergy)
        val linearLayoutManager = LinearLayoutManager(context)
        rvCCAndDr?.layoutManager = linearLayoutManager

        //Click Listeners..
        tvCancel?.setOnClickListener(this)
        tvSubmit?.setOnClickListener(this)

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_cancel -> { dismiss() }
        }
    }

    override fun getRefCCAndDr(getRefCCBO: GetRefCCBO) {
        when(stReferenceType){
            "CC"->{
                //When you want to send data to fragment
                val i: Intent = Intent().putExtra("get_ref_cc_bo", getRefCCBO)
                targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
                dismiss()
            }

            "DR"->{
                //When you want to send data to fragment
                val i: Intent = Intent().putExtra("get_ref_cc_bo", getRefCCBO)
                targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
                dismiss()
            }
        }
    }

    private fun filterTable(text: String) {
        if (getRefCCBOList.size > 0) {
            val filteredList1: ArrayList<GetRefCCBO> = ArrayList<GetRefCCBO>()
            for (item in getRefCCBOList) {
                if (text.let { item.name.lowercase(Locale.ROOT).contains(it) }
                    || text.let { item.name.uppercase(Locale.ROOT).contains(it) }
                ) {
                    filteredList1.add(item)
                }
            }
            getCenterAndDoctorAdapter?.updateData(context, filteredList1)
        }
    }
}