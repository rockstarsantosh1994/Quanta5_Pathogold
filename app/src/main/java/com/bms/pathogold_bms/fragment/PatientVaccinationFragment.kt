package com.bms.pathogold_bms.fragment

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.VaccinationPatientKeyAdapter
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientBO
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class PatientVaccinationFragment : BaseFragment(), View.OnClickListener  {

    private val TAG = "VaccinationFragment"
    
    //TextView declaration..
    private var tvNoDataFound: TextView?=null
    private var tvPatientName: TextView?=null
    private var tvPatientMobileNo: TextView?=null
    private var tvPatientSex: TextView?=null
    private var tvPatientDOB:TextView?=null
    private var tvPatientAge:TextView?=null

    //RecyclerView declaration..
    private var rvVaccination: RecyclerView?=null

    //AppCompatButton declaration..
    private var btnAddNewVaccination : AppCompatButton?=null

    private var getPatientListBO: GetPatientListBO? = null
    private val getVaccPatientListMap: MutableMap<String, ArrayList<VaccinationPatientBO>> = LinkedHashMap()
    private var getVaccPatientTemp=ArrayList<VaccinationPatientBO>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable...
        val arguments = requireArguments()
        getPatientListBO = arguments.getParcelable("patient_bo")

        //basic intialisation..
        initViews(view)

        //setData of patient
        setData()

        //getPatientVaccineList of user...
        if(mContext?.let { CommonMethods.isNetworkAvailable(it) } == true){
            getPatientVaccineList()
        }else{
            activity?.let { CommonMethods.showDialogForError(it, AllKeys.NO_INTERNET_AVAILABLE) }
        }
    }

    override val activityLayout: Int
        get() =R.layout.fragment_vaccination

    private fun initViews(view: View){
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.vaccination)

        //TextView binding..
        tvNoDataFound=view.findViewById(R.id.tv_no_data_found)
        tvPatientName=view.findViewById(R.id.tv_patient_name)
        tvPatientMobileNo=view.findViewById(R.id.tv_patient_mobiles_value)
        tvPatientSex=view.findViewById(R.id.tv_sex)
        tvPatientDOB=view.findViewById(R.id.tv_patient_dob_values)
        tvPatientAge=view.findViewById(R.id.tv_age_value)

        //RecyclerView binding..
        rvVaccination=view.findViewById(R.id.rv_vaccination)
        val mLayoutManager: LinearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvVaccination?.layoutManager = mLayoutManager

        //AppCompatButton binding...
        btnAddNewVaccination = view.findViewById(R.id.btn_add_new_vaccination)
        btnAddNewVaccination?.setOnClickListener(this)

        if(CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Consultant)){
            btnAddNewVaccination?.visibility=View.VISIBLE
        }else{
            btnAddNewVaccination?.visibility=View.GONE
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_add_new_vaccination->{
                (activity as DashBoardActivity).toolbar?.title = resources.getString(R.string.add_vaccination)
                (activity as DashBoardActivity).navController.navigate(R.id.addVaccinationFragment)
            }
        }
    }

    private fun getPatientVaccineList() {
        val params: MutableMap<String, String> = HashMap()
        params["companyid"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["pepatid"] =getPatientListBO?.PePatID.toString()

        Log.e(TAG, "getPatientVaccineList: $params" )

        val progress = ProgressDialog(activity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getVaccineList_patient(params,object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val vaccinationPatientResponse = `object` as VaccinationPatientResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $vaccinationPatientResponse")
                if (vaccinationPatientResponse.ResponseCode == 200) {
                    getVaccPatientListMap.clear()
                    getVaccPatientTemp.clear()
                    if (vaccinationPatientResponse.ResultArray.size > 0) {
                        for (temp in vaccinationPatientResponse.ResultArray) {
                            if (getVaccPatientListMap.containsKey(temp.Age.trim())) {
                                getVaccPatientTemp = getVaccPatientListMap[temp.Age.trim()]!!
                                getVaccPatientTemp.add(temp)
                            } else {
                                getVaccPatientTemp = ArrayList()
                                getVaccPatientTemp.add(temp)
                                getVaccPatientListMap[temp.Age.trim()] = getVaccPatientTemp
                            }
                        }

                        rvVaccination?.visibility=View.VISIBLE
                        tvNoDataFound?.visibility=View.GONE

                        val vaccinationPatientAdapter= VaccinationPatientKeyAdapter(mContext,getVaccPatientListMap.keys.toTypedArray(),getVaccPatientListMap,getPatientListBO)
                        rvVaccination?.adapter=vaccinationPatientAdapter
                    }else{
                        rvVaccination?.visibility=View.GONE
                        tvNoDataFound?.visibility=View.VISIBLE
                    }
                } else {
                    rvVaccination?.visibility=View.GONE
                    tvNoDataFound?.visibility=View.VISIBLE
                    Toast.makeText(mContext, vaccinationPatientResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                //showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(){
        tvPatientSex?.text= getPatientListBO?.sex
        tvPatientMobileNo?.text= getPatientListBO?.PatientPhoneNo
        tvPatientName?.text=getPatientListBO?.PatientName
        tvPatientDOB?.text=CommonMethods.parseDateToddMMyyyy(getPatientListBO?.DOB,"MM/dd/yyyy HH:mm:ss","MM/dd/yyyy")
        tvPatientAge?.text=CommonMethods.convertToYearsMonthsDays(CommonMethods.getTodayDate("MM/dd/yyyy"),CommonMethods.parseDateToddMMyyyy(getPatientListBO?.DOB,"MM/dd/yyyy HH:mm:ss","MM/dd/yyyy"))
    }
}