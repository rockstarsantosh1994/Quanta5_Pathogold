package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getemrtreatment.GetEMRTreatmentBO
import java.util.*

class GetEMRTreatmentAdapter(
    private val context: Context? = null,
    private val key: Array<Any>,
    private val getEMRTreatmentMap: Map<String, ArrayList<GetEMRTreatmentBO>>? = null,
) : RecyclerView.Adapter<GetEMRTreatmentAdapter.ReportsKeyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsKeyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_getemercheckbox, parent, false)
        return ReportsKeyViewHolder(view,context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReportsKeyViewHolder, position: Int) {
        holder.tvKey.text = "" + key[position]
        if (getEMRTreatmentMap != null) {
            if(getEMRTreatmentMap[key[position]] !=null){
                val getEMRTreatmentDataAdapter=GetEMRTreatmentDataAdapter(context!!, getEMRTreatmentMap[key[position]]!!)
                holder.rvKey.adapter=getEMRTreatmentDataAdapter
            }
        }
    }

    override fun getItemCount(): Int {
       return key.size
    }

    class ReportsKeyViewHolder(itemView: View,context: Context?) : RecyclerView.ViewHolder(itemView) {
        val tvKey: TextView = itemView.findViewById(R.id.tv_key)
        val rvKey: RecyclerView = itemView.findViewById(R.id.rv_key_data)

        init {
            val mLayoutManager: LinearLayoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            rvKey.layoutManager = mLayoutManager
        }
    }
}