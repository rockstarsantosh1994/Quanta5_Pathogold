package com.bms.pathogold_bms.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import me.ibrahimsn.lib.OnItemSelectedListener
import me.ibrahimsn.lib.SmoothBottomBar

class ImageUploadDashboardFragment : BaseFragment() {

    //FrameLayout Declaration..
    private var frameLayout: FrameLayout?=null

    //SmoothBar Declaration..
    private var smoothBottomBar: SmoothBottomBar?=null

    private var getPatientListBO:GetPatientListBO?=null

    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //intent parcelable...
        val arguments= requireArguments()
        getPatientListBO=arguments.getParcelable("patient_bo")

        //Getting the Navigation Controller
        navController=requireActivity().findNavController(R.id.view_image_graph)

        //basic intialistion..
        initViews(view)

        if(CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Consultant)
            || CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Patient)){
            //If patient or doctor login then default view fragment will be loaded...
            smoothBottomBar?.itemActiveIndex=1
            loadViewUploadFragment()
        }else{
            //if phelbo or lab-technician then default upload fragment will be loaded...
            //load Default Fragment..
            smoothBottomBar?.itemActiveIndex=0
            loadImageUploadFragment()
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_image_upload_dashboard

    private fun initViews(view: View) {
        (context as DashBoardActivity).toolbar?.title = requireActivity().resources.getString(R.string.image_upload)

        //binding all views...
        frameLayout=view.findViewById(R.id.frame_layout)
        smoothBottomBar=view.findViewById(R.id.bottomBar)

        smoothBottomBar?.itemActiveIndex=0

        smoothBottomBar?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelect(pos: Int): Boolean {
                when (pos) {
                    0 -> {
                        loadImageUploadFragment()
                    }
                    1 -> {
                        loadViewUploadFragment()
                    }
                }
                return false
            }
        }
    }

    fun loadImageUploadFragment() {
        val bundle = Bundle()
        bundle.putParcelable("patient_bo", getPatientListBO)
        bundle.putString("date",arguments?.getString("date"))
        navController.navigate(R.id.imageUploadFragment,bundle)
    }

    fun loadViewUploadFragment() {
        val bundle = Bundle()
        bundle.putParcelable("patient_bo", getPatientListBO)
        bundle.putString("date",arguments?.getString("date"))
        navController.navigate(R.id.viewUploadFragment,bundle)
    }

    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            val fragmentTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, fragment)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

}