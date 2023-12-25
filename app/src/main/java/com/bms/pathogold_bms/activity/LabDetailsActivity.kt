package com.bms.pathogold_bms.activity

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.GetLabDetailsAdapter
import com.bms.pathogold_bms.fragment.LabContactDetailsDialog
import com.bms.pathogold_bms.fragment.LabServiceDetailDialog
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsBO
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.LocationClass
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*


class LabDetailsActivity : BaseActivity(), View.OnClickListener,
    GetLabDetailsAdapter.GetLabDetailAdapterOperation{

    private val TAG = "LabDetailsActivity"

    private var toolbar: Toolbar? = null

    //LinearLayout declaration..
    private var llLabDetailsView: LinearLayout? = null

    //EditText declaration..
    private var etSearch: EditText? = null

    //RecyclerView declaration...
    private var rvAllLab: RecyclerView? = null

    //TextView declaration..
    private var tvCurrentCity: TextView? = null
    private var tvApplyFilter: TextView? = null
    private var tvNoDataFound: TextView? = null

    //List declaration..
    private var labCityList = ArrayList<String>()
    private var getLabDetailsAdapter: GetLabDetailsAdapter? = null

    //Map
    private val getLabDetailMap: MutableMap<String, ArrayList<GetLabDetailsBO>> = LinkedHashMap()
    private var getLabDetailTempList: ArrayList<GetLabDetailsBO>? = null
    private var city: String? = null

    var isLocationDataSet=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialisation...
        initViews()

        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            getLabDetails()
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentLatLongLocation() {
        val locationClass= LocationClass(applicationContext,this@LabDetailsActivity)

        if(locationClass.isGPSEnabled){
            val coderr=Geocoder(this@LabDetailsActivity)
            val address2:List<Address>
            try {
                address2= coderr.getFromLocation(locationClass.latitude,locationClass.longitude,5 ) as List<Address>
               // Log.e(TAG, "getCurrentLatLongLocation: "+address2[0].getAddressLine(0) )
                //Log.e(TAG, "getCurrentLatLongLocation: "+address2[0].locality )
              //  Log.e(TAG, "getCurrentLatLongLocation: "+address2[0].adminArea )
                //Log.e(TAG, "getCurrentLatLongLocation: "+address2[0].postalCode )
                //Log.e(TAG, "getCurrentLatLongLocation: "+address2[0].getAddressLine(0) )

                city=address2[0].locality.uppercase(Locale.getDefault())
                tvCurrentCity?.text = "Current Location:-$city," +address2[0].adminArea.uppercase(Locale.getDefault())

                setDataToRecyclerView()

            }catch (e:Exception){
                e.printStackTrace()
            }
        }else{
            Toast.makeText(this@LabDetailsActivity, "Gps is off. Please switch it on & retry.", Toast.LENGTH_SHORT).show()
        }
    }

    override val activityLayout: Int
        get() = R.layout.activity_lab_details

    private fun initViews() {
        //Toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = resources.getString(R.string.labs_clinic)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white),
            PorterDuff.Mode.SRC_ATOP)
        toolbar?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        setSupportActionBar(toolbar)

        //LinearLayout binding..
        llLabDetailsView = findViewById(R.id.ll_lab_details_view)

        //Recyler view binding..
        rvAllLab = findViewById(R.id.rv_all_lab)
        val linearLayoutManager = LinearLayoutManager(mContext!!)
        rvAllLab?.layoutManager = linearLayoutManager

        //TextView binding..
        tvNoDataFound = findViewById(R.id.tv_no_data_found)
        tvCurrentCity = findViewById(R.id.tv_current_city)
        tvApplyFilter = findViewById(R.id.tv_apply_filter)

        //EditText binding..
        etSearch = findViewById(R.id.et_search)
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })

        //Click Listerners..
        tvApplyFilter?.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_apply_filter -> {
                val spinnerDialogDistrict = SpinnerDialog(mContext!! as Activity?,
                    labCityList,
                    "Select City",
                    R.style.DialogAnimations_SmileWindow,
                    "Close") // With 	Animation

                spinnerDialogDistrict.bindOnSpinerListener { item: String, position: Int ->
                    tvCurrentCity?.text = "Selected City:- $item"
                    //set selected item to city....
                    city = item

                    //set data to recycler view..
                    setDataToRecyclerView()
                }
                spinnerDialogDistrict.showSpinerDialog()
            }
        }
    }

    private fun getLabDetails() {
        val params: MutableMap<String, String> = HashMap()
        params["labcode"] = AllKeys.STATIC_LAB_CODE
        Log.e(TAG, "getLabDetails: $params")

        val progress = ProgressDialog(this@LabDetailsActivity)
        progress.setMessage("Please Wait....")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()
            ?.getLabDetail(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getLabDetailsResponse = `object` as GetLabDetailsResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getLabDetailsResponse")
                    if (getLabDetailsResponse.ResponseCode == 200) {
                        //To be integrated...
                        if (getLabDetailsResponse.ResultArray?.size!! > 0) {
                            for (temp in getLabDetailsResponse.ResultArray!!) {
                                if (temp.city.isNotEmpty()) {
                                    if (getLabDetailMap.containsKey(temp.city.uppercase().trim())) {
                                        getLabDetailTempList = getLabDetailMap[temp.city.uppercase().trim()]
                                        getLabDetailTempList!!.add(temp)
                                    } else {
                                        getLabDetailTempList = ArrayList()
                                        getLabDetailTempList!!.add(temp)
                                        getLabDetailMap[temp.city.uppercase().trim()] = getLabDetailTempList!!
                                    }
                                }
                            }

                            //get Location latlong
                            getCurrentLatLongLocation()

                            //Add all city in list from map
                            labCityList.addAll(getLabDetailMap.keys)
                            Collections.sort(labCityList, String.CASE_INSENSITIVE_ORDER)
                        }
                    } else {
                        Toast.makeText(mContext!!, getLabDetailsResponse.Message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun setDataToRecyclerView() {
        if (city?.isNotEmpty() == true) {
            if (getLabDetailMap.containsKey(city)) {
                getLabDetailsAdapter = GetLabDetailsAdapter(mContext!!, getLabDetailMap[city]!!, this)
                rvAllLab?.adapter = getLabDetailsAdapter
            }else{
                CommonMethods.showDialogForError(mContext!!,"City Not Found. \n Please select your current city.")
            }
        }
    }

    fun filterTable(text: String?) {
        if (getLabDetailMap[city]!!.size > 0) {
            val filteredList1: ArrayList<GetLabDetailsBO> = ArrayList()
            for (item in getLabDetailMap[city]!!) {
                if (text?.let { item.labname.contains(it, true) } == true
                    || text?.let { item.labphone.contains(it, true) } == true
                    || text?.let { item.labaddress.contains(it, true) } == true
                    || text?.let { item.labpincode.contains(it, true) } == true
                    || text?.let { item.city.contains(it, true) } == true
                    || text?.let { item.state.contains(it, true) } == true
                    || text?.let { item.country.contains(it, true) } == true
                ) {
                    filteredList1.add(item)
                }
            }
            getLabDetailsAdapter?.updateData(mContext, filteredList1)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun contactLabOperation(getLabDetailsBO: GetLabDetailsBO) {
        val ft1 = supportFragmentManager.beginTransaction()
        val labContactDetailsDialog = LabContactDetailsDialog()
        val bundle = Bundle()
        bundle.putParcelable("lab_detail_bo", getLabDetailsBO)
        labContactDetailsDialog.arguments = bundle
        labContactDetailsDialog.show(ft1, "Tag")
    }

    override fun serviceDetailOperation(getLabDetailsBO: GetLabDetailsBO) {
        if (getLabDetailsBO.Service_detail.isEmpty() && getLabDetailsBO.service_image.isEmpty()) {
            CommonMethods.showDialogForError(mContext!!, "No Details Found")
        } else {
            val ft1 = supportFragmentManager.beginTransaction()
            val labServiceDetailDialog = LabServiceDetailDialog()
            val bundle = Bundle()
            bundle.putParcelable("lab_detail_bo", getLabDetailsBO)
            labServiceDetailDialog.arguments = bundle
            labServiceDetailDialog.show(ft1, "Tag")
        }
    }

    override fun bookAppointmentOperation(getLabDetailsBO: GetLabDetailsBO) {
        //Set payment gateway id for razor pay....
        if(getLabDetailsBO.Razorpayid.isNotEmpty()){
            CommonMethods.setPreference(mContext!!,AllKeys.MERCHANT_ID_RAZORPAY,getLabDetailsBO.Razorpayid)
        }else{
            CommonMethods.setPreference(mContext!!,AllKeys.MERCHANT_ID_RAZORPAY,AllKeys.DNF)
        }

        if(getLabDetailsBO.Payu_marchantid.isNotEmpty() || getLabDetailsBO.Payu_marchantsaltid.isNotEmpty()){
            CommonMethods.setPreference(mContext!!,AllKeys.PAYU_MERCHANT_KEY,getLabDetailsBO.Payu_marchantid)
            CommonMethods.setPreference(mContext!!,AllKeys.PAYU_MERCHANT_SALT,getLabDetailsBO.Payu_marchantsaltid)
        }else{
            CommonMethods.setPreference(mContext!!,AllKeys.PAYU_MERCHANT_KEY,AllKeys.DNF)
            CommonMethods.setPreference(mContext!!,AllKeys.PAYU_MERCHANT_SALT,AllKeys.DNF)
        }

        val intent = Intent(this@LabDetailsActivity, CheckSlotForBookAppActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("lab_detail_bo", getLabDetailsBO)
        startActivity(intent)
    }
}