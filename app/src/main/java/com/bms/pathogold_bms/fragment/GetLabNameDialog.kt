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
import com.bms.pathogold_bms.adapter.GetLabNameAdapter
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListBO
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import java.util.*
import kotlin.collections.ArrayList

class GetLabNameDialog : DialogFragment(),View.OnClickListener,GetLabNameAdapter.GetLabNameSuperAdmin {

    private val TAG = "GetLabNameDialog"

    //EditText declaration..
    private var etSearch: EditText? = null

    //RecyclerView declaration...
    private var rvLabName: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvCancel: TextView?=null
    private var tvSubmit: TextView?=null

    //ArrayList declaration.
    private var getLabNameArrayList=ArrayList<LabNameBO>()

    //Adapter declaration.
    private var getLabNameAdapter:GetLabNameAdapter?=null

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
        val view = inflater.inflate(R.layout.fragment_get_lab_name_dialog, container, false)

        //Compulsory light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //basic intialisation..
        initViews(view)

        val arguments = arguments
        if (BuildConfig.DEBUG && arguments == null) {
            error("Assertion failed")
        }

        if (getArguments()?.getParcelableArrayList<LabNameBO>("get_lab_name") != null) {
            getLabNameArrayList.addAll(arguments?.getParcelableArrayList("get_lab_name")!!)
            getLabNameAdapter = context?.let { GetLabNameAdapter(it, getLabNameArrayList,this) }
            rvLabName?.adapter = getLabNameAdapter
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
        rvLabName = view.findViewById(R.id.rv_lab_name)
        val linearLayoutManager = LinearLayoutManager(context)
        rvLabName?.layoutManager = linearLayoutManager

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

    private fun filterTable(text: String) {
        if (getLabNameArrayList.size > 0) {
            val filteredList1: ArrayList<LabNameBO> = ArrayList<LabNameBO>()
            for (item in getLabNameArrayList) {
                if (text.let { item.labname.lowercase(Locale.ROOT).contains(it) }
                    || text.let { item.labname.uppercase(Locale.ROOT).contains(it) }
                ) {
                    filteredList1.add(item)
                }
            }
            getLabNameAdapter?.updateData(context, filteredList1)
        }
    }

    override fun getLabName(labNameBO: LabNameBO) {
        //When you want to send data to fragment
        val i: Intent = Intent().putExtra("lab_name_bo", labNameBO)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
        dismiss()
    }

}