package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO

class ViewAppointmentConsultationAdapter (private var context: Context,
                                          private var consultationBOArrayList:ArrayList<ConsultationBO>,
                                          var getPhleboConsulataionDoctor: GetPhleboConsulataionDoctor
): RecyclerView.Adapter<ViewAppointmentConsultationAdapter.ConsultationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_consultation,parent,false)
        return ConsultationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        holder.tvConsultantName.text=consultationBOArrayList[position].Name

        holder.tvConsultantName.setOnClickListener {
            getPhleboConsulataionDoctor.getPhleboConsultationDoctor(consultationBOArrayList[position])
        }
    }

    override fun getItemCount(): Int {
         return consultationBOArrayList.size
    }

    class ConsultationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvConsultantName=itemView.findViewById(R.id.tv_consultant_name) as TextView
        var btnSelectConsultant=itemView.findViewById(R.id.btn_select_consultant) as AppCompatButton
    }

    fun updateData(context: Context?, data: ArrayList<ConsultationBO>) {
        this.context = context!!
        this.consultationBOArrayList = data
        notifyDataSetChanged()
    }

    interface GetPhleboConsulataionDoctor{
        fun getPhleboConsultationDoctor(consultationBO: ConsultationBO)
    }
}