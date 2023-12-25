package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getemrtreatment.GetEMRTreatmentBO

class GetEMRTreatmentDataAdapter(private val context:Context,
                                 private val getEMRTreatmentBOList: ArrayList<GetEMRTreatmentBO>) : RecyclerView.Adapter<GetEMRTreatmentDataAdapter.GetEMRTreatementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetEMRTreatementViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.row_get_emr_treatment_data,parent,false)
        return GetEMRTreatementViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetEMRTreatementViewHolder, position: Int) {
        holder.tvDrugName.text=""+getEMRTreatmentBOList[position].Qty+" * "+getEMRTreatmentBOList[position].DrugName
        holder.tvNote.text=getEMRTreatmentBOList[position].Note
    }

    override fun getItemCount(): Int {
        return getEMRTreatmentBOList.size
    }

    class GetEMRTreatementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDrugName:TextView=itemView.findViewById(R.id.tv_drug_name)
        var tvNote:TextView=itemView.findViewById(R.id.tv_note)
    }
}