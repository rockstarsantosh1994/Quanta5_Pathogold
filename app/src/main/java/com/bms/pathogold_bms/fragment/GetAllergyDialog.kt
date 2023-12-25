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
import com.bms.pathogold_bms.adapter.GetAllergyAdapter
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListBO
import java.util.*
import kotlin.collections.ArrayList

class GetAllergyDialog : DialogFragment(), View.OnClickListener,GetAllergyAdapter.GetAllergyNameInterface {

    //Views Declarations....
    private val TAG = "GetAllergyDialog"

    //EditText declaration..
    private var etSearch: EditText? = null

    //RecyclerView declaration...
    private var rvAllergy: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvCancel: TextView?=null
    private var tvSubmit: TextView?=null

    private val getAllergyListBOArrayList=ArrayList<GetAllergyListBO>()
    private var getAllergyAdapter:GetAllergyAdapter?=null

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
        //position=getArguments()?.getString("position")
        if (getArguments()?.getParcelableArrayList<GetAllergyListBO>("allergy_list") != null) {
            getAllergyListBOArrayList.addAll(arguments?.getParcelableArrayList("allergy_list")!!)
            getAllergyAdapter = context?.let { GetAllergyAdapter(it, getAllergyListBOArrayList,this) }
            rvAllergy?.adapter = getAllergyAdapter
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
        rvAllergy = view.findViewById(R.id.rv_allergy)
        val linearLayoutManager = LinearLayoutManager(context)
        rvAllergy?.layoutManager = linearLayoutManager

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

    override fun getAllergyName(getAllergyListBO: GetAllergyListBO) {
        //When you want to send data to fragment
        val i: Intent = Intent().putExtra("allergy_bo", getAllergyListBO)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)

        dismiss()
    }

    private fun filterTable(text: String) {
        if (getAllergyListBOArrayList.size > 0) {
            val filteredList1: java.util.ArrayList<GetAllergyListBO> =
                java.util.ArrayList<GetAllergyListBO>()
            for (item in getAllergyListBOArrayList) {
                if (text.let { item.AllergyName.lowercase(Locale.ROOT).contains(it) }
                    || text.let { item.AllergyName.uppercase(Locale.ROOT).contains(it) }
                ) {
                    filteredList1.add(item)
                }
            }
            getAllergyAdapter?.updateData(context, filteredList1)
        }
    }
}