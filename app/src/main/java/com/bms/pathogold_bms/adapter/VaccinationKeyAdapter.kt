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
import com.bms.pathogold_bms.model.vaccination.VaccinationBO
import java.util.*


class VaccinationKeyAdapter(
    private val context: Context? = null,
    private val key: Array<Any>,
    private val getVacinationBOListMap: Map<String, ArrayList<VaccinationBO>>? = null,
) : RecyclerView.Adapter<VaccinationKeyAdapter.ReportsKeyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsKeyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_getemercheckbox, parent, false)
        return ReportsKeyViewHolder(view,context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReportsKeyViewHolder, position: Int) {
        holder.tvKey.text = "" + key[position]

        if (getVacinationBOListMap != null) {
            if(getVacinationBOListMap[key[position]] !=null){
                val viewVaccinationAdapter = context?.let { ViewVaccinationAdapter(it,getVacinationBOListMap[key[position]]!!) }
                holder.rvKey.adapter=viewVaccinationAdapter
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