package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.vaccination.VaccinationBO

class ViewVaccinationAdapter (
    var context: Context,
    private var vaccinationBOList:ArrayList<VaccinationBO>
    )
    : RecyclerView.Adapter<ViewVaccinationAdapter.ViewVaccinationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewVaccinationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_view_vaccination,parent,false)
        return ViewVaccinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewVaccinationViewHolder, position: Int) {
        holder.tvVaccinationName.text = vaccinationBOList[position].vaccinationName
        holder.tvVaccinationAge.visibility=View.GONE
        holder.tvVaccinationAge.text = vaccinationBOList[position].Age
    }

    override fun getItemCount(): Int {
        return vaccinationBOList.size
    }

    class ViewVaccinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvVaccinationName:TextView= itemView.findViewById(R.id.tv_vaccination_name)
        var tvVaccinationAge:TextView = itemView.findViewById(R.id.tv_age)
    }

    fun updateData(context: Context?, data: ArrayList<VaccinationBO>) {
        this.context = context!!
        this.vaccinationBOList = data
        notifyDataSetChanged()
    }
}