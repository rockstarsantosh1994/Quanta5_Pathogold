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
import com.bms.pathogold_bms.fragment.CheckSlotFragment
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO

class ConsultationAdapter(
    private var context: Context,
    private var consultationBOArrayList: ArrayList<ConsultationBO>,
    private val consultationAdapterOperation: ConsultationAdapterOperation,
): RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder>() {

    private var lastCheckedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_consultation,parent,false)
        return ConsultationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        holder.tvConsultantName.text=consultationBOArrayList[position].Name

        holder.radioSelect.isChecked = position == lastCheckedPosition

        /*holder.btnSelectConsultant.setOnClickListener {
            val copyOfLastCheckedPosition: Int = lastCheckedPosition
            lastCheckedPosition = holder.adapterPosition
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(lastCheckedPosition)

            checkSlotActivity?.onPhelboSelect(consultationBOArrayList[position])
        }*/

        holder.constraintLayout.setOnClickListener {
            val copyOfLastCheckedPosition: Int = lastCheckedPosition
            lastCheckedPosition = holder.adapterPosition
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(lastCheckedPosition)

            consultationAdapterOperation.onPhelboSelect(consultationBOArrayList[position])
        }

        holder.radioSelect.setOnClickListener {
            val copyOfLastCheckedPosition: Int = lastCheckedPosition
            lastCheckedPosition = holder.adapterPosition
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(lastCheckedPosition)

            consultationAdapterOperation.onPhelboSelect(consultationBOArrayList[position])
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

    interface ConsultationAdapterOperation{
        fun onPhelboSelect(consultationBO: ConsultationBO)
    }
}