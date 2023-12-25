package com.bms.pathogold_bms.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.bms.pathogold_bms.adapter.ConsultationDialogAdapter
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import java.lang.ClassCastException
import java.util.*
import android.app.Activity

import android.text.format.DateUtils.getMonthString

import android.content.Intent
import android.text.format.DateUtils


class ConsultationDialog : DialogFragment(), ConsultationDialogAdapter.GetConsulataionDoctor {

    //Views Declarations....
    private val TAG = "ConsultationDialog"

    private var etSearch: EditText? = null
    private var rvConsultation: RecyclerView? = null
    var consultationAdapter: ConsultationDialogAdapter? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    private val consultationBOArrayList = ArrayList<ConsultationBO>()

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = 800
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_consultation_dialog, container, false)
        //Compulsory light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //basic intialisation..
        initViews(view)

        val arguments = arguments
        if (BuildConfig.DEBUG && arguments == null) {
            error("Assertion failed")
        }
        if (getArguments()?.getParcelableArrayList<ConsultationBO>("consultation_list") != null) {
            consultationBOArrayList.addAll(arguments?.getParcelableArrayList("consultation_list")!!)
            consultationAdapter = context?.let {
                ConsultationDialogAdapter(it, consultationBOArrayList,this)
            }
            rvConsultation?.adapter = consultationAdapter

        }
        return view
    }

    private fun initViews(view: View) {

        //Editext binding.
        etSearch = view.findViewById(R.id.et_search)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //Recycler binding..
        rvConsultation = view.findViewById(R.id.rv_approved_appointment)
        val linearLayoutManager = LinearLayoutManager(context)
        rvConsultation?.layoutManager = linearLayoutManager

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    private fun filterTable(text: String) {
        if (consultationBOArrayList.size > 0) {
            val filteredList1: ArrayList<ConsultationBO> = ArrayList<ConsultationBO>()
            for (item in consultationBOArrayList) {
                if (text.let {
                        item.Name.lowercase(Locale.ROOT).contains(it)
                    } || text.let {
                        item.Name.uppercase(Locale.ROOT).contains(it)
                    } || text.let { item.Pno.uppercase(Locale.ROOT).contains(it) }
                ) {
                    filteredList1.add(item)
                }
            }
            //Log.e(TAG, "filter: size" + filteredList1.size());
            // Log.e(TAG, "filter: List" + filteredList1.toString());
            consultationAdapter?.updateData(context, filteredList1)
        }
    }
    
    override fun getConsultationDoctor(consultationBO: ConsultationBO) {
        Log.e(TAG, "getConsulationDoctor: $consultationBO" )
        //when calling activity
        //val callingFragment: PatientRegistrationFragment? = fragmentManager as PatientRegistrationFragment?
        //callingFragment?.onConsultationDoctorSelect(consultationBO)

        //When you want to send data to fragment
        val i: Intent = Intent().putExtra("consultation_bo", consultationBO)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)

        dismiss()
    }
}