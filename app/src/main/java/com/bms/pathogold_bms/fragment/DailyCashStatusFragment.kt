package com.bms.pathogold_bms.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.adapter.DailyCashStatusAdapter
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterBO
import java.util.ArrayList
import java.util.LinkedHashMap

class DailyCashStatusFragment : BaseFragment() {

    //Declaration...
    private val TAG = "DailyCashStatusFragment"

    //RecyclerView declaration...
    private var rvDailyStatus:RecyclerView?=null

    //TextView declaration..
    private var tvNoDataFound:TextView?=null

    //Arraylist declaration...
    private var getDailyCashRegisterList: ArrayList<DailyCashRegisterBO>?= ArrayList()
    private var getStatusList:ArrayList<DailyCashRegisterBO>?= ArrayList()

    //Map declaration..
    private val getByStatusMap: MutableMap<String, ArrayList<DailyCashRegisterBO>> = LinkedHashMap()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //basic intialisation..
        initViews(view)

        try{
            val arguments = arguments
            if (BuildConfig.DEBUG && arguments == null) {
                error("Assertion failed")
            }

            getDailyCashRegisterList?.clear()
            getByStatusMap.clear()
            getStatusList?.clear()
            getDailyCashRegisterList=arguments?.getParcelableArrayList("daily_cash_register_list")

            Log.e(TAG, "onViewCreated: "+getDailyCashRegisterList?.size)

            //By Status Name
            for(temp in getDailyCashRegisterList!!){
                if (getByStatusMap.containsKey(temp.status.trim())) {
                    getStatusList = getByStatusMap[temp.status.trim()]
                    getStatusList!!.add(temp)
                } else {
                    getStatusList = ArrayList()
                    getStatusList!!.add(temp)
                    getByStatusMap[temp.status.trim()] = getStatusList!!
                }
            }

            if(getDailyCashRegisterList?.size!!>0){
                rvDailyStatus?.visibility=View.VISIBLE
                tvNoDataFound?.visibility=View.GONE

                val dailyCashStatusAdapter=DailyCashStatusAdapter(mContext!!,getByStatusMap.keys.toTypedArray(),getByStatusMap)
                rvDailyStatus?.adapter=dailyCashStatusAdapter
            }else{
                rvDailyStatus?.visibility=View.GONE
                tvNoDataFound?.visibility=View.VISIBLE
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override val activityLayout: Int
        get() = R.layout.fragment_daily_cash_status

    private fun initViews(view: View) {
        //Recyclerview binding..
        rvDailyStatus=view.findViewById(R.id.rv_daily_cash_status)
        val linearLayoutManager= LinearLayoutManager(mContext)
        rvDailyStatus?.layoutManager=linearLayoutManager

        //TextView binding..
        tvNoDataFound=view.findViewById(R.id.tv_no_data_found)
    }

}