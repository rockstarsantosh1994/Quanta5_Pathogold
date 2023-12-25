package com.bms.pathogold_bms.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class LabServiceDetailDialog : DialogFragment() {

    private val TAG = "LabServiceDetailDialog"

    //Material CardView declaration..
    private var cvServiceDetailView: MaterialCardView? = null
    private var cvServiceImageView: MaterialCardView? = null

    //TextView declaration
    private var tvServiceDetails: TextView? = null

    //ImageView Declaration...
    private var ivServiceImage: ImageView? = null
    private var ivClose: ImageView? = null

    //BusinessObject declaration...
    private var getLabDetailsBO:GetLabDetailsBO?=null

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
        val view = inflater.inflate(R.layout.fragment_lab_service_detail_dialog, container, false)

        //basic intalisation...
        initViews(view)

        val arguments = arguments
        if (BuildConfig.DEBUG && arguments == null) {
            error("Assertion failed")
        }

        if (getArguments()?.getParcelable<GetLabDetailsBO>("lab_detail_bo") != null) {
            getLabDetailsBO=getArguments()?.getParcelable("lab_detail_bo")

            if(getLabDetailsBO?.Service_detail?.isNotEmpty() == true){
                cvServiceDetailView?.visibility=View.VISIBLE
                cvServiceImageView?.visibility= View.GONE

                tvServiceDetails?.text=getLabDetailsBO?.Service_detail

            }else if(getLabDetailsBO?.service_image?.isNotEmpty() == true){
                cvServiceDetailView?.visibility=View.GONE
                cvServiceImageView?.visibility= View.VISIBLE

                Glide.with(requireContext()).load(getLabDetailsBO?.service_image)
                    //.placeholder(R.drawable.ic_user)
                    .into(ivServiceImage!!)
            }
        }
        return view
    }

    private fun initViews(view: View?) {
        //MaterialCardView Binding...
        cvServiceImageView = view?.findViewById(R.id.cv_service_image_view)
        cvServiceDetailView = view?.findViewById(R.id.cv_service_details_view)

        //TextView binding..
        tvServiceDetails = view?.findViewById(R.id.tv_service_details)

        //ImageView binding..
        ivClose = view?.findViewById(R.id.iv_close)
        ivServiceImage = view?.findViewById(R.id.iv_service_detail_image)

        ivClose?.setOnClickListener { dismiss() }
    }
}