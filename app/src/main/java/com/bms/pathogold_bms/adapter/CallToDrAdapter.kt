package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.google.android.material.circularreveal.cardview.CircularRevealCardView

class CallToDrAdapter (private var context: Context,
                       private var consultationBOArrayList:ArrayList<ConsultationBO>,
                       private val onDrSelectInterFace: OnDrSelectInterFace?): RecyclerView.Adapter<CallToDrAdapter.ConsultationViewHolder>() {

    private val backgroundColors = intArrayOf(R.color.red100, R.color.purple100 , R.color.blue100 , R.color.teal100,R.color.green100)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_call_to_dr,parent,false)
        return ConsultationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        holder.tvConsultantName.text="Dr."+consultationBOArrayList[position].Name

        val bgColor = ContextCompat.getColor(context, backgroundColors[position % 5])
        holder.cardView.setBackgroundColor(bgColor)

        holder.cardView.setOnClickListener {
            onDrSelectInterFace?.onDrSelect(consultationBOArrayList[position])
        }
    }

    override fun getItemCount(): Int {
         return consultationBOArrayList.size
    }

    class ConsultationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvConsultantName:TextView= itemView.findViewById(R.id.tv_summary_name)
        val cardView: CircularRevealCardView =itemView.findViewById(R.id.cv_cardview)
    }

    fun updateData(context: Context?, data: ArrayList<ConsultationBO>) {
        this.context = context!!
        this.consultationBOArrayList = data
        notifyDataSetChanged()
    }

    interface OnDrSelectInterFace{
        fun onDrSelect(consultationBO: ConsultationBO)
    }
}