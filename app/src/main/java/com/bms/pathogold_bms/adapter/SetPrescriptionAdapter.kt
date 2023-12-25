package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.fragment.PrescriptionFragment
import com.bms.pathogold_bms.model.PrescriptionUploadBO

class SetPrescriptionAdapter(val context: Context,
                             private val prescriptionUploadArrayList: ArrayList<PrescriptionUploadBO>,
                             private val prescriptionFragment: PrescriptionFragment
) : RecyclerView.Adapter<SetPrescriptionAdapter.SetPrescriptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetPrescriptionViewHolder {
       val layoutInflater=LayoutInflater.from(parent.context)
       val view=layoutInflater.inflate(R.layout.row_set_prescription,parent,false)
        return SetPrescriptionViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SetPrescriptionViewHolder, position: Int) {

        holder.tvDrugName.text = prescriptionUploadArrayList[position].getDrugId()+"."+prescriptionUploadArrayList[position].getTDrugName()
        holder.tvDose.text=prescriptionUploadArrayList[position].getDose()
        holder.tvDay.text=prescriptionUploadArrayList[position].getDay()
        holder.tvQty.text=prescriptionUploadArrayList[position].getQty()

        holder.tvDelete.setOnClickListener{
            // Continue with delete operation
            prescriptionUploadArrayList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,prescriptionUploadArrayList.size)

            if(prescriptionUploadArrayList.size==0){
                prescriptionFragment.hideSubmitButton()
            }
        }
    }

    override fun getItemCount(): Int {
        return prescriptionUploadArrayList.size
    }

    class SetPrescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val tvDrugName:TextView=itemView.findViewById(R.id.tv_drug_name)
         val tvDose:TextView=itemView.findViewById(R.id.tv_dose)
         val tvDay:TextView=itemView.findViewById(R.id.tv_day)
         val tvQty:TextView=itemView.findViewById(R.id.tv_qty)
         val tvDelete:TextView=itemView.findViewById(R.id.tv_delete)
    }
}