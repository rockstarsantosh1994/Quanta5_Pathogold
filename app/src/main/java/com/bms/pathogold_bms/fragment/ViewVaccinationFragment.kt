package com.bms.pathogold_bms.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.ReportsDetailsAdapter
import com.bms.pathogold_bms.adapter.VaccinationKeyAdapter
import com.bms.pathogold_bms.adapter.ViewVaccinationAdapter
import com.bms.pathogold_bms.model.report.ReportsBO
import com.bms.pathogold_bms.model.vaccination.VaccinationBO
import com.bms.pathogold_bms.model.vaccination.VaccinationResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.util.*
import kotlin.collections.ArrayList

class ViewVaccinationFragment : BaseFragment(), View.OnClickListener {

    //Views Declarations....
    private val TAG = "ViewVaccinationFragment"

    //Edittext declaration...
    private var etSearch: EditText? = null

    //Recyclerview declaration...
    private var rvViewVaccination: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    //LinearLayout declaration
    private var llNoVaccinationFound: LinearLayout? = null
    private var llVaccination: LinearLayout? = null

    //AppCompatButton declaration..
    private var btnAddNewVaccination: AppCompatButton? = null

    private val getVaccinationListMap: MutableMap<String, ArrayList<VaccinationBO>> = LinkedHashMap()
    private var viewVaccinationList = ArrayList<VaccinationBO>()

    override val activityLayout: Int
        get() = R.layout.fragment_view_vacination

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation..
        initViews(view)

        //get Vaccine List...
        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            getVaccineList()
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    private fun initViews(view: View) {
        (activity as DashBoardActivity).toolbar?.title =
            resources.getString(R.string.view_vaccination)

        //Editext binding.
        etSearch = view.findViewById(R.id.et_search_vaccination_name)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //Linearlayout binding..
        llVaccination = view.findViewById(R.id.ll_accepted_appointmnet)
        llNoVaccinationFound = view.findViewById(R.id.ll_no_data_found)

        //Recycler binding..
        rvViewVaccination = view.findViewById(R.id.rv_approved_appointment)
        val linearLayoutManager = LinearLayoutManager(mContext)
        rvViewVaccination?.layoutManager = linearLayoutManager

        //AppCompatButton binding...
        btnAddNewVaccination = view.findViewById(R.id.btn_add_new_vaccination)
        btnAddNewVaccination?.setOnClickListener(this)

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Consultant)) {
            btnAddNewVaccination?.visibility = View.VISIBLE
        } else {
            btnAddNewVaccination?.visibility = View.GONE
        }

        /*   etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
               filterTable(s.toString())
            }
        })*/
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_add_new_vaccination -> {
                (activity as DashBoardActivity).toolbar?.title =
                    resources.getString(R.string.add_vaccination)
                val bundle = Bundle()
                bundle.putString("vaccination_type", "new")
                (activity as DashBoardActivity).navController.navigate(R.id.addVaccinationFragment,
                    bundle)
            }
        }
    }

    private fun getVaccineList() {
        val params: MutableMap<String, String> = HashMap()
        params["companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getVaccineList: $params")

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getVaccineList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val vaccinationResponse = `object` as VaccinationResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $vaccinationResponse")
                    if (vaccinationResponse.ResponseCode == 200) {
                        //Calling Intent...
                        if (vaccinationResponse.ResultArray.size > 0) {
                            llVaccination?.visibility = View.VISIBLE
                            llNoVaccinationFound?.visibility = View.GONE

                            for (temp in vaccinationResponse.ResultArray) {
                                if (getVaccinationListMap.containsKey(temp.Age.trim())) {
                                    viewVaccinationList = getVaccinationListMap[temp.Age.trim()]!!
                                    viewVaccinationList.add(temp)
                                } else {
                                    viewVaccinationList = ArrayList()
                                    viewVaccinationList.add(temp)
                                    getVaccinationListMap[temp.Age.trim()] = viewVaccinationList
                                }
                            }

                            val vaccinationKeyAdapter = VaccinationKeyAdapter(mContext,
                                getVaccinationListMap.keys.toTypedArray(),
                                getVaccinationListMap)
                            rvViewVaccination?.adapter = vaccinationKeyAdapter

                        } else {
                            llNoVaccinationFound?.visibility = View.VISIBLE
                            llVaccination?.visibility = View.GONE
                        }
                    } else {
                        llNoVaccinationFound?.visibility = View.VISIBLE
                        llVaccination?.visibility = View.GONE
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }
}
   /* fun filterTable(text: String?) {
       // if (viewVaccinationList.size > 0) {
            val filteredList1: ArrayList<VaccinationBO> = ArrayList()
            for (item in viewVaccinationList) {
                if (text?.let { item.vaccinationName.toLowerCase(Locale.ROOT).contains(it) } == true
                    || text?.let { item.vaccinationName.toUpperCase(Locale.ROOT).contains(it) } == true
                    || text?.let { item.Age.toUpperCase(Locale.ROOT).contains(it) } == true
                ) {
                    filteredList1.add(item)
                }
            }
           // Log.e(TAG, "filter: size" + filteredList1.size)
           //  Log.e(TAG, "filter: List$filteredList1")
            viewVaccinationAdapter?.updateData(mContext, filteredList1)
       // }
    }*/

