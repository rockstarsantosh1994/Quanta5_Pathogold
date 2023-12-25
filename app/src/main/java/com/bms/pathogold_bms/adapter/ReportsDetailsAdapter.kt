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
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.report.ReportsBO
import java.util.*

class ReportsDetailsAdapter(
    private val context: Context? = null,
    private val key: Array<Any>,
    private val getReportsListMap: Map<String, ArrayList<ReportsBO>>? = null,
    private val getPatientListBO: GetPatientListBO?,
) : RecyclerView.Adapter<ReportsDetailsAdapter.ReportsKeyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsKeyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_getemercheckbox, parent, false)
        return ReportsKeyViewHolder(view,context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReportsKeyViewHolder, position: Int) {
        holder.tvKey.text = "" + key[position]

        if (getReportsListMap != null) {
            if(getReportsListMap[key[position]] !=null){
                val reportsKeyDetailsAdapter=ReportsKeyDetailsAdapter(context!!, getReportsListMap[key[position]]!!,getPatientListBO)
                holder.rvKey.adapter=reportsKeyDetailsAdapter
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