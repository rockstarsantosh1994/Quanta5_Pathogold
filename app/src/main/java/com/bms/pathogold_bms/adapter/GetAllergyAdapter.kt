package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListBO

class GetAllergyAdapter (private var context: Context,
                         private var getAllergyListArrayList: ArrayList<GetAllergyListBO>,
                         val getAllergyNameInterface: GetAllergyNameInterface): RecyclerView.Adapter<GetAllergyAdapter.GetSurgeryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetSurgeryViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view:View=inflater.inflate(R.layout.row_get_slots,parent,false)
        return GetSurgeryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GetSurgeryViewHolder, position: Int) {
        holder.btnSelectAllergy.text=context.resources.getString(R.string.select)

        holder.tvAllergyTitle.text=getAllergyListArrayList[position].AllergyName

        holder.btnSelectAllergy.setOnClickListener {
            getAllergyNameInterface.getAllergyName(getAllergyListArrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return getAllergyListArrayList.size
    }

    class GetSurgeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAllergyTitle: TextView =itemView.findViewById(R.id.tv_slot_time)
        var btnSelectAllergy: AppCompatButton =itemView.findViewById(R.id.btn_book_slot)
    }

    fun updateData(context: Context?, data: java.util.ArrayList<GetAllergyListBO>) {
        this.context = context!!
        this.getAllergyListArrayList = data
        notifyDataSetChanged()
    }

    interface GetAllergyNameInterface{
        fun getAllergyName(getAllergyListBO: GetAllergyListBO)
    }
}