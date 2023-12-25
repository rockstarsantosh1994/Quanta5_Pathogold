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
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientBO
import java.util.ArrayList

class VaccinationPatientKeyAdapter(
    private val context: Context? = null,
    private val key: Array<Any>,
    private val getVacinationBOListMap: Map<String, ArrayList<VaccinationPatientBO>>? = null,
    private val getPatientListBO: GetPatientListBO?,
) : RecyclerView.Adapter<VaccinationPatientKeyAdapter.VaccinationPatientKeyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccinationPatientKeyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_getemercheckbox, parent, false)
        return VaccinationPatientKeyViewHolder(view,context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VaccinationPatientKeyViewHolder, position: Int) {
        holder.tvKey.text = "" + key[position]

        if (getVacinationBOListMap != null) {
            if(getVacinationBOListMap[key[position]] !=null){
                val vaccinationPatientAdapter = context?.let { VaccinationPatientAdapter(it,getVacinationBOListMap[key[position]]!!,getPatientListBO) }
                holder.rvKey.adapter=vaccinationPatientAdapter
            }
        }
    }

    override fun getItemCount(): Int {
        return key.size
    }

    class VaccinationPatientKeyViewHolder(itemView: View, context: Context?) : RecyclerView.ViewHolder(itemView) {
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