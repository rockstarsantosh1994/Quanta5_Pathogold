package com.bms.pathogold_bms.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
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
import com.bms.pathogold_bms.activity.BookAppointmentActivity
import com.bms.pathogold_bms.adapter.GetTestCodeAdapter
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import java.util.*
import kotlin.collections.ArrayList

class TestCodeDialog : DialogFragment(), View.OnClickListener {

    //Views Declarations....
    private val TAG = "TestCodeDialog"

    private var etSearch: EditText? = null
    private var rvTestCode: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null
    private var tvCancel:TextView?=null
    private var tvSubmit:TextView?=null

    private val getPatientTestListBOArrayList = ArrayList<GetTestCodeBO>()
    private val addTestCodeArrayList=ArrayList<GetTestCodeBO>()
    private var getTestCodeAdapter:GetTestCodeAdapter?=null

    private var stOpenVia:String?=null

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
        val view= inflater.inflate(R.layout.fragment_test_code_dialog, container, false)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //basic intialisation..
        initViews(view)

        val arguments = arguments
        if (BuildConfig.DEBUG && arguments == null) {
            error("Assertion failed")
        }

        //Check whether user come from activity class or fragment class
        stOpenVia=getArguments()?.getString("open_via")

        if (getArguments()?.getParcelableArrayList<GetTestCodeBO>("testcode_list") != null) {
            getPatientTestListBOArrayList.addAll(arguments?.getParcelableArrayList("testcode_list")!!)
            getPatientTestListBOArrayList.reverse()
            getTestCodeAdapter = context?.let { GetTestCodeAdapter(it, getPatientTestListBOArrayList,addTestCodeArrayList) }
            rvTestCode?.adapter = getTestCodeAdapter
        }

        return view
    }

    private fun initViews(view: View) {
        //Editext binding.
        etSearch = view.findViewById(R.id.et_search)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)
        tvCancel=view.findViewById(R.id.tv_cancel)
        tvSubmit=view.findViewById(R.id.tv_submit)

        //Recycler binding..
        rvTestCode = view.findViewById(R.id.rv_approved_appointment)
        val linearLayoutManager = LinearLayoutManager(context)
        rvTestCode?.layoutManager = linearLayoutManager

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

    private fun filterTable(text: String) {
        if (getPatientTestListBOArrayList.size > 0) {
            val filteredList1: ArrayList<GetTestCodeBO> = ArrayList()
            for (item in getPatientTestListBOArrayList) {
                if (text.let { item.title.lowercase(Locale.ROOT).contains(it) }
                    || text.let { item.tlcode.lowercase(Locale.ROOT).contains(it) }
                    || text.let { item.tlcode.uppercase(Locale.ROOT).contains(it) }
                    || text.let { item.title.uppercase(Locale.ROOT).contains(it) }
                ) {
                    filteredList1.add(item)
                }
            }
            //Log.e(TAG, "filter: size" + filteredList1.size());
            // Log.e(TAG, "filter: List" + filteredList1.toString());
            getTestCodeAdapter?.updateData(context, filteredList1)
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_cancel -> { dismiss() }

            R.id.tv_submit -> {
                addTestCodeArrayList.clear()
                for(temp in getPatientTestListBOArrayList){
                    if(temp.isSelected=="true"){
                       addTestCodeArrayList.add(temp)
                    }
                }

                Log.e(TAG, "onClick: "+addTestCodeArrayList.size )
                Log.e(TAG, "onClick: $addTestCodeArrayList")

                //When you want to send data to fragment
                when(stOpenVia){
                    "fragment"->{
                        val i: Intent = Intent().putExtra("test_code_list", addTestCodeArrayList)
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
                        dismiss()
                    }

                    "activity"->{
                        val callingActivity: BookAppointmentActivity? = activity as BookAppointmentActivity?
                        callingActivity?.onTestCodeSelect(addTestCodeArrayList)
                        dismiss()
                    }
                }
            }
        }
    }
}