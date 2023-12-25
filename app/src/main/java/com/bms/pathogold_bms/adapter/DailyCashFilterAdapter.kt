package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO

class DailyCashFilterAdapter(
    var context: Context,
    var stringList:ArrayList<String>,
    var getDailyCaseSelect:DailyCashSelect
 ): RecyclerView.Adapter<DailyCashFilterAdapter.DailyCashViewHolder1>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): DailyCashViewHolder1 {
        val layoutInflater= LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_consultation, parent, false)
        return DailyCashViewHolder1(view)
    }

    override fun onBindViewHolder(holder: DailyCashViewHolder1, position: Int, ) {
        holder.radioSelect.visibility=View.GONE

        holder.tvName.text=stringList[position]

        holder.constraintLayout.setOnClickListener {
            getDailyCaseSelect.getDailyCashSelectName(stringList[position])
        }
    }

    override fun getItemCount(): Int {
        return stringList.size
    }


    class DailyCashViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName=itemView.findViewById(R.id.tv_consultant_name) as TextView
        var btnSelectConsultant=itemView.findViewById(R.id.btn_select_consultant) as AppCompatButton
        var constraintLayout=itemView.findViewById(R.id.constraintLayout) as ConstraintLayout
        var radioSelect=itemView.findViewById(R.id.radio_select_consultant) as RadioButton
    }

    fun updateData(context: Context?, data: ArrayList<String>) {
        this.context = context!!
        this.stringList = data
        notifyDataSetChanged()
    }

    interface DailyCashSelect{
        fun getDailyCashSelectName(stSelectName:String)
    }
}