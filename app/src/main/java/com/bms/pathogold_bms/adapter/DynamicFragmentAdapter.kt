package com.bms.pathogold_bms.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bms.pathogold_bms.fragment.ViewAllAppointmentFragment
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO

class DynamicFragmentAdapter internal constructor(
    fm: FragmentManager?,
    private val mNumOfTabs: Int,
    private var viewAppointmentAllMap: MutableMap<String, ArrayList<ViewAppointmentBO>>? = null,
    private val tabName: String = "",
    val pheloboBOArrayList: ArrayList<ConsultationBO>,
    val consultationBOArrayList: ArrayList<ConsultationBO>,
) : FragmentStatePagerAdapter(fm!!) {
   private val TAG = "DynamicFragmentAdapter"

    // get the current item with position number
    override fun getItem(position: Int): Fragment {
        val b = Bundle()
        b.putParcelableArrayList("phlebo_list",pheloboBOArrayList)
        b.putParcelableArrayList("consultant_list",pheloboBOArrayList)
        b.putParcelableArrayList("view_appointment_list", viewAppointmentAllMap?.get(tabName))
        b.putInt("position", position)
        val frag: Fragment = ViewAllAppointmentFragment.newInstance()
        frag.arguments = b
        return frag
    }

    // get total number of tabs
    override fun getCount(): Int {
        return mNumOfTabs
    }
}