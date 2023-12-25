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

class ConsultationDialogAdapter(
    private var context: Context,
    private var consultationBOArrayList: ArrayList<ConsultationBO>,
    var getConsulataionDoctor: GetConsulataionDoctor
): RecyclerView.Adapter<ConsultationDialogAdapter.ConsultationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_consultation, parent, false)
        return ConsultationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {

        holder.radioSelect.visibility=View.GONE
        
        holder.tvConsultantName.text=consultationBOArrayList[position].Name

        holder.constraintLayout.setOnClickListener {
            getConsulataionDoctor.getConsultationDoctor(consultationBOArrayList[position])
        }
    }

    override fun getItemCount(): Int {
         return consultationBOArrayList.size
    }

    class ConsultationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvConsultantName=itemView.findViewById(R.id.tv_consultant_name) as TextView
        var btnSelectConsultant=itemView.findViewById(R.id.btn_select_consultant) as AppCompatButton
        var constraintLayout=itemView.findViewById(R.id.constraintLayout) as ConstraintLayout
        var radioSelect=itemView.findViewById(R.id.radio_select_consultant) as RadioButton
    }

    fun updateData(context: Context?, data: ArrayList<ConsultationBO>) {
        this.context = context!!
        this.consultationBOArrayList = data
        notifyDataSetChanged()
    }

    interface GetConsulataionDoctor{
        fun getConsultationDoctor(consultationBO: ConsultationBO)
    }
}