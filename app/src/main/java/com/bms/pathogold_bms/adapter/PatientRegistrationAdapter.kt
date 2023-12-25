package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.utility.CommonMethods
import java.util.*

class PatientRegistrationAdapter(
    var context: Context,
    private var getPatientListBOArrayList: ArrayList<GetPatientListBO>,
    val getPatientRegistrationBOInterFace: GetPatientRegistrationBOInterFace
) : RecyclerView.Adapter<PatientRegistrationAdapter.GetPatientViewHolder>() {

    private val TAG = "PatientRegistrationAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetPatientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.row_patient_registration_list, parent, false)
        return GetPatientViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetPatientViewHolder, position: Int) {
        holder.tvPatientName.text = getPatientListBOArrayList[position].PatientName
        holder.tvAge.text = CommonMethods.convertToYearsMonthsDays(CommonMethods.getTodayDate("MM/dd/yyyy"),CommonMethods.parseDateToddMMyyyy(getPatientListBOArrayList[position].DOB,"MM/dd/yyyy HH:mm:ss","MM/dd/yyyy"))
        holder.tvSex.text = getPatientListBOArrayList[position].sex
        holder.tvDOB.text=CommonMethods.parseDateToddMMyyyy(getPatientListBOArrayList[position].DOB,"MM/dd/yyyy HH:mm:ss","MM/dd/yyyy")

        holder.tvSelect.setOnClickListener {
            getPatientRegistrationBOInterFace.getOnPatientSelected(getPatientListBOArrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return getPatientListBOArrayList.size
    }

    class GetPatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPatientName: TextView = itemView.findViewById(R.id.tv_patient_name)
        var tvAge: TextView = itemView.findViewById(R.id.tv_age_value)
        var tvSex: TextView = itemView.findViewById(R.id.tv_sex)
        var tvDOB:TextView=itemView.findViewById(R.id.tv_patient_dob_value)

        var tvSelect:TextView = itemView.findViewById(R.id.tv_select)
    }

    fun updateData(context: Context?, data: ArrayList<GetPatientListBO>) {
        this.context = context!!
        this.getPatientListBOArrayList = data
        notifyDataSetChanged()
    }

    interface GetPatientRegistrationBOInterFace{
        fun getOnPatientSelected(getPatientListBO: GetPatientListBO)
    }
}