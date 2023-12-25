package com.bms.pathogold_bms.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.ApprovedAppAdapter
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import java.util.*
import kotlin.collections.ArrayList

class ViewAllAppointmentFragment : BaseFragment(){

    //Views Declarations....
    //Edittext declaration...
    private var etSearch: EditText? = null

    //Recyclerview declaration...
    private var rvApprovedAppointment: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    //LinearLayout declaration
    private var llNoPatientFound: LinearLayout?=null
    private var llAppointment: LinearLayout?=null

    private var approvedAppAdapter: ApprovedAppAdapter? = null
    private val TAG = "ApprovedFragment"

    private var viewAppointmentArrayList = ArrayList<ViewAppointmentBO>()

    //fragment xml
    override val activityLayout: Int
        get() = R.layout.fragment_approved

    companion object {
        fun newInstance(): ViewAllAppointmentFragment {
            return ViewAllAppointmentFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = requireArguments()
        try{
               viewAppointmentArrayList=arguments.getParcelableArrayList("view_appointment_list")!!
        }catch (e:Exception){
            e.printStackTrace()
        }

        //basic intialisation..
        initViews(view)

        //Append data to recycler view..
        appendDataToRecyclerView(viewAppointmentArrayList)
    }

    private fun appendDataToRecyclerView(viewAppointmentArrayList: ArrayList<ViewAppointmentBO>) {
        if (viewAppointmentArrayList.size>0) {
            llAppointment?.visibility = View.VISIBLE
            llNoPatientFound?.visibility = View.GONE
            approvedAppAdapter = mContext?.let { ApprovedAppAdapter(it, viewAppointmentArrayList, "Approve", digiPath!!) }
            rvApprovedAppointment?.adapter = approvedAppAdapter
        } else {
            llAppointment?.visibility = View.VISIBLE
            llNoPatientFound?.visibility = View.GONE
        }
    }

    private fun initViews(view: View) {
       // (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.approve_appointment)
        //Editext binding.
        etSearch = view.findViewById(R.id.et_search)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //Linearlayout binding..
        llAppointment=view.findViewById(R.id.ll_accepted_appointmnet)

        //LinearLayout declaration
        llNoPatientFound=view.findViewById(R.id.ll_no_data_found)


        //Recycler binding..
        rvApprovedAppointment = view.findViewById(R.id.rv_approved_appointment)
        val linearLayoutManager = LinearLayoutManager(mContext)
        rvApprovedAppointment?.layoutManager = linearLayoutManager

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    fun filterTable(text: String?) {
        if (viewAppointmentArrayList.size > 0) {
            val filteredList1: ArrayList<ViewAppointmentBO> = ArrayList<ViewAppointmentBO>()
            for (item in viewAppointmentArrayList) {
                if (text?.let { item.PatientName.lowercase(Locale.ROOT).contains(it) } == true
                    || text?.let { item.PatientName.uppercase(Locale.ROOT).contains(it) } == true
                    || text?.let { item.Mobileno.uppercase(Locale.ROOT).contains(it) } == true
                ) {
                    filteredList1.add(item)
                }
            }
            //Log.e(TAG, "filter: size" + filteredList1.size());
            // Log.e(TAG, "filter: List" + filteredList1.toString());
            approvedAppAdapter?.updateData(mContext, filteredList1)
        }
    }
}