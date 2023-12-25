package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.BookAppointmentActivity
import com.bms.pathogold_bms.fragment.BookAppointmentFragment
import com.bms.pathogold_bms.model.getpatient.GetPatientBO

class GetPatientAdapterForActivity (val context:Context, val getPatientBOArrayList: ArrayList<GetPatientBO>, val bookAppointmentActivity: BookAppointmentActivity): RecyclerView.Adapter<GetPatientAdapterForActivity.GetPatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetPatientViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_get_all_patient,parent,false)
        return GetPatientViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetPatientViewHolder, position: Int) {
        holder.tvPatientName.text= getPatientBOArrayList[position].PatientName
        holder.tvAge.text= "Age:-" +getPatientBOArrayList[position].Age
        holder.tvPatientMobile.text= getPatientBOArrayList[position].MobileNo

        holder.btnSelect.setOnClickListener {
            bookAppointmentActivity.setSelectedData(getPatientBOArrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return getPatientBOArrayList.size
    }

    class GetPatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvPatientName:TextView=itemView.findViewById(R.id.tv_patient_name)
        var tvAge:TextView=itemView.findViewById(R.id.tv_age)
        var tvPatientMobile:TextView=itemView.findViewById(R.id.tv_patient_mobile)
        var btnSelect:AppCompatButton=itemView.findViewById(R.id.btn_select)

    }

}