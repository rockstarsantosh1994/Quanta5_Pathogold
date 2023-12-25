package com.bms.pathogold_bms.activity

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.fragment.ChatFragment
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods

class SupportActivity : BaseActivity() {

    //Toolbar declaration
    private var toolbar: Toolbar?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialistion..
        initViews()

        //load Chat Fragment.....
        loadFragment(ChatFragment())
    }

    private fun initViews() {
        //toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
       // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.setTitleTextColor(Color.BLACK)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        toolbar?.title =CommonMethods.getPrefrence(mContext!!,AllKeys.PERSON_NAME)
        //toolbar.setTextAlignment(TEXT_ALIGNMENT_CENTER);
    }

    override val activityLayout: Int
        get() = R.layout.activity_support

    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, fragment)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }
}