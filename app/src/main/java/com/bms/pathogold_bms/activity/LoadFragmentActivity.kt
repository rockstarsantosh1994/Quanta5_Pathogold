package com.bms.pathogold_bms.activity

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods

class LoadFragmentActivity : BaseActivity() {

    private var toolbar: Toolbar? = null

    lateinit var navController: NavController

    private var stFragmentClass: String? = null

    override val activityLayout: Int
        get() = R.layout.activity_load_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //basic intialisation..
        initViews()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white),
            PorterDuff.Mode.SRC_ATOP)
        toolbar?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        setSupportActionBar(toolbar)

        //Getting the Navigation Controller
        navController = Navigation.findNavController(this, R.id.load_fragment_activity)

        stFragmentClass = intent.getStringExtra("fragment")

        if (stFragmentClass?.equals("getPatientListFragment") == true) {
            toolbar?.title = resources.getString(R.string.all_patient)
            val bundle = Bundle()
            bundle.putString("launch_type", AllKeys.IS_PATIENT_LIST_LAUNCH)
            navController.navigate(R.id.getPatientListFragment,bundle)
        }else if(stFragmentClass?.equals("imageUploadDashboardFragment") == true){
            val bundle = Bundle()
            bundle.putParcelable("patient_bo", intent.getParcelableExtra("patient_bo"))
            bundle.putString("date", CommonMethods.getTodayDate("MM/dd/yyyy"))
            navController.navigate(R.id.imageUploadDashboardFragment, bundle)
        }
    }
}