package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods

class VaccinationPatientAdapter(
    private val context: Context,
    private val getVaccinationPatientList: ArrayList<VaccinationPatientBO>,
    private val getPatientListBO: GetPatientListBO?
):
    RecyclerView.Adapter<VaccinationPatientAdapter.VaccinationPatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): VaccinationPatientViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view:View=layoutInflater.inflate(R.layout.row_vaccination_patient,parent,false)
        return VaccinationPatientViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: VaccinationPatientViewHolder, position: Int) {

        holder.tvVaccinationName.text = getVaccinationPatientList[position].vaccinationName

        if(CommonMethods.getPrefrence(context, AllKeys.USERTYPE).equals(AllKeys.Patient)) {
            if(holder.btnDetails.text.equals(context.resources.getString(R.string.show_details))){
                holder.btnDetails.visibility=View.VISIBLE
            }else{
                holder.btnDetails.visibility=View.GONE
            }
        }

        when {
            getVaccinationPatientList[position].colourcode.contains("Green",true) -> {
                holder.tvVaccinationName.setTextColor(context.resources.getColor(R.color.green500))
                holder.btnDetails.text=context.resources.getString(R.string.show_details)
                holder.btnBookSlot.text=context.resources.getString(R.string.vaccinated)
            }
            getVaccinationPatientList[position].colourcode.contains("Red",true) -> {
                holder.tvVaccinationName.setTextColor(context.resources.getColor(R.color.red))
                holder.btnDetails.text=context.resources.getString(R.string.enter_details)
            }
            getVaccinationPatientList[position].colourcode.contains("Yellow",true) -> {
                holder.tvVaccinationName.setTextColor(context.resources.getColor(R.color.yellow700))
                holder.btnDetails.text=context.resources.getString(R.string.enter_details)
            }
            getVaccinationPatientList[position].colourcode.contains("White",true) -> {
                holder.tvVaccinationName.setTextColor(context.resources.getColor(R.color.black))
                holder.btnDetails.text=context.resources.getString(R.string.enter_details)
            }
        }

        holder.btnDetails.setOnClickListener {
            if(holder.btnDetails.text.equals(context.resources.getString(R.string.show_details))){
                showDialog(getVaccinationPatientList[position])
            }else if(holder.btnDetails.text.equals(context.resources.getString(R.string.enter_details))){
                insertVaccinationDetails(getVaccinationPatientList[position])
            }
        }

        holder.btnBookSlot.setOnClickListener {
            if(holder.btnBookSlot.text.equals(context.resources.getString(R.string.book_slot))){
                val bundle=Bundle()
                bundle.putParcelable("vaccination_patient_bo", getVaccinationPatientList[position])
                bundle.putString("app_type", "vaccine")
                (context as DashBoardActivity).navController.navigate(R.id.checkSlotFragment2,bundle)
            }
        }
    }

    override fun getItemCount(): Int {
        return getVaccinationPatientList.size
    }

    class VaccinationPatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //TextView declaration..
        var tvVaccinationName:TextView=itemView.findViewById(R.id.tv_vaccination_name)

        //AppCompatButton declaration...
        var btnDetails:AppCompatButton = itemView.findViewById(R.id.btn_detiails)
        var btnBookSlot:AppCompatButton = itemView.findViewById(R.id.btn_book_slot)
        //var btnVaccinated:AppCompatButton = itemView.findViewById(R.id.btn_vaccinated)
    }

    private fun showDialog(vaccinationPatientBO: VaccinationPatientBO) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Vaccination Details")
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customLayout: View = inflater.inflate(R.layout.row_vaccination_details_dialog, null)
        builder.setView(customLayout)
        val tilVaccinatioName = customLayout.findViewById<TextView>(R.id.tv_vaccination_name_value)
        val tilDate = customLayout.findViewById<TextView>(R.id.tv_date_value)
        val tilTime = customLayout.findViewById<TextView>(R.id.tv_time_value)
        val tvBatch = customLayout.findViewById<TextView>(R.id.tv_batch_value)
        val tvMake = customLayout.findViewById<TextView>(R.id.tv_make_value)

        tilVaccinatioName.text = vaccinationPatientBO.vaccinationName
        tilDate.text = CommonMethods.parseDateToddMMyyyy(vaccinationPatientBO.GivenTime,"MM/dd/yyyy HH:mm:ss a","dd, MMM yyyy")
        tilTime.text=  CommonMethods.parseDateToddMMyyyy(vaccinationPatientBO.GivenTime,"MM/dd/yyyy HH:mm:ss a","HH:mm:ss a")
        tvBatch.text = vaccinationPatientBO.Batch
        tvMake.text = vaccinationPatientBO.Make

        builder.setPositiveButton("CLOSE") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun insertVaccinationDetails(vaccinationPatientBO: VaccinationPatientBO) {
        val bundle= Bundle()
        bundle.putParcelable("vaccination_patient_bo", vaccinationPatientBO)
        bundle.putParcelable("patient_bo",getPatientListBO)
        (context as DashBoardActivity).navController.navigate(R.id.enterDetailsVaccinationFragment,bundle)
    }
}