package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.DailyCashSummaryAdapter
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterBO
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterSummaryBO
import java.lang.Exception
import kotlin.collections.ArrayList
import android.widget.AbsListView
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.Fragment
import com.bms.pathogold_bms.utility.AllKeys

class DailyCashSummaryFragment : BaseFragment(), DailyCashSummaryAdapter.GetPatientRegisterDetails {

    private val TAG = "DailyCashSummaryFragmen"

    //RecyclerView declaration..
    private var rvDailyCashSummary: RecyclerView? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    //declaration arraylist..
    private var dailyCashRegisterSummaryBOList: ArrayList<DailyCashRegisterSummaryBO>? = ArrayList()
    private var getDailyCashRegisterList: ArrayList<DailyCashRegisterBO>? = ArrayList()
    private var getDailyCashRegisterRecList: ArrayList<DailyCashRegisterBO>? = ArrayList()
    private var dailyCashSummaryAdapter: DailyCashSummaryAdapter? = null

    private var dailyCashDashBoardFragment:DailyCashDashBoardFragment?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation...
        initViews(view)

        try {
            val arguments = arguments
            if (BuildConfig.DEBUG && arguments == null) {
                error("Assertion failed")
            }

            getDailyCashRegisterList?.clear()
            getDailyCashRegisterList = arguments?.getParcelableArrayList("daily_cash_register_list")
            getDailyCashRegisterRecList = arguments?.getParcelableArrayList("daily_cash_register_rec_list")

            dailyCashRegisterSummaryBOList?.clear()
            dailyCashRegisterSummaryBOList = arguments?.getParcelableArrayList("daily_cash_summary_list")

            //setData...
            setDataToRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_daily_cash_summary

    private fun initViews(view: View) {
        //TextView binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //Recycler view binding...
        rvDailyCashSummary = view.findViewById(R.id.rv_daily_cash_summary)
        val linearLayoutManager = GridLayoutManager(mContext, 2)
        rvDailyCashSummary?.layoutManager = linearLayoutManager

        /*rvDailyCashSummary?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scrolling up  visiblity gone
                    dailyCashDashBoardFragment.llSelectLabView?.visibility = View.GONE
                    (fragmentManager as DailyCashDashBoardFragment).fromToDateConstraintLayout?.visibility = View.GONE
                    dailyCashDashBoardFragment.
                } else {
                    // Scrolling down visibility visible
                    (fragmentManager as DailyCashDashBoardFragment).llSelectLabView?.visibility = View.VISIBLE
                    (fragmentManager as DailyCashDashBoardFragment).fromToDateConstraintLayout?.visibility = View.VISIBLE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    AbsListView.OnScrollListener.SCROLL_STATE_FLING -> {
                        // Do something
                    }
                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {
                        // Do something
                    }
                    else -> {
                        // Do something
                    }
                }
            }
        })*/
    }

    private fun setDataToRecyclerView() {
        if (dailyCashRegisterSummaryBOList?.size!! > 0) {
            tvNoDataFound?.visibility = View.GONE
            rvDailyCashSummary?.visibility = View.VISIBLE
            dailyCashSummaryAdapter =
                DailyCashSummaryAdapter(mContext!!, dailyCashRegisterSummaryBOList!!, this)
            rvDailyCashSummary?.adapter = dailyCashSummaryAdapter
        } else {
            tvNoDataFound?.visibility = View.VISIBLE
            rvDailyCashSummary?.visibility = View.GONE
        }
    }

    override fun getPatientRegisterDetails(stName: String) {
        val bundle = Bundle()
        if(stName.equals(AllKeys.TODAYS_COLLECTION, ignoreCase = true)){
            bundle.putParcelableArrayList("daily_cash_register_list", getDailyCashRegisterRecList)
        }else{
            bundle.putParcelableArrayList("daily_cash_register_list", getDailyCashRegisterList)
        }
        bundle.putString("stname", stName)
        bundle.putString("isBackPressed", "1")
        (context as DashBoardActivity).navController.navigate(R.id.dailyCashRegisterFragment, bundle)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (dailyCashSummaryAdapter != null) {
            dailyCashSummaryAdapter?.notifyDataSetChanged()
        }

    }
}