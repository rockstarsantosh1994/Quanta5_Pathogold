package com.bms.pathogold_bms.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods


class MyProfileActivity : BaseActivity(), View.OnClickListener {

    //Declaration....
    //Toolbar declaration...
    private var toolbar:Toolbar?=null

    //ImageView declaration...
    private var ivEdit:ImageView?=null
    private var ivProfilePic:ImageView?=null

    //TextView declaration...
    private var tvName:TextView?=null
    private var tvEmail:TextView?=null
    private var tvMobileNo:TextView?=null
    private var tvAddress:TextView?=null
    private var tvGender:TextView?=null
    private var tvDOB:TextView?=null
    private var tvLabName:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialisation....
        initViews()

        //setData to all Textviews...
        setData()
    }

    override val activityLayout: Int
        get() = R.layout.activity_my_profile

    private fun initViews() {
        //toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = "My Profile"
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        //toolbar.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        //imageview binding..
        ivProfilePic=findViewById(R.id.iv_profilepic)
        ivEdit=findViewById(R.id.iv_edit_profile)

        //textview binding...
        tvName=findViewById(R.id.tv_name)
        tvEmail=findViewById(R.id.tv_email)
        tvDOB=findViewById(R.id.tv_date_of_birth)
        tvMobileNo=findViewById(R.id.tv_mobilenumber)
        tvAddress=findViewById(R.id.tv_address)
        tvGender=findViewById(R.id.tv_gender)
        tvLabName=findViewById(R.id.tv_lab_name)

        //Click Listeners..
        ivEdit?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.iv_edit_profile->{
                //Calling edit profile activity..

            }
        }
    }

    private fun setData() {
        tvName?.text = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.PERSON_NAME) }
        tvEmail?.text = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.EMAIL) }
        tvMobileNo?.text = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.MOBILE_NO) }
        tvAddress?.text = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.ADDRESS) }
        tvGender?.text = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.SEX) }
        tvDOB?.text = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.DOB) }
        tvLabName?.text = mContext?.let { CommonMethods.getPrefrence(it,AllKeys.LABNAME) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}