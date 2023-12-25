package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getrefcc.GetRefCCBO

class GetCenterAndDoctorAdapter (private var context: Context,
                                 private var getRefCCAndDrList: ArrayList<GetRefCCBO>,
                                 val getRefCCAndDr: GetRefCCAndDr): RecyclerView.Adapter<GetCenterAndDoctorAdapter.GetSurgeryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetSurgeryViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view:View=inflater.inflate(R.layout.row_get_slots,parent,false)
        return GetSurgeryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GetSurgeryViewHolder, position: Int) {
        holder.btnSelectCCAndDr.text=context.resources.getString(R.string.select)

        holder.tvRefCCAndDrName.text=getRefCCAndDrList[position].name

        holder.btnSelectCCAndDr.setOnClickListener {
            getRefCCAndDr.getRefCCAndDr(getRefCCAndDrList[position])
        }
    }

    override fun getItemCount(): Int {
        return getRefCCAndDrList.size
    }

    class GetSurgeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvRefCCAndDrName: TextView =itemView.findViewById(R.id.tv_slot_time)
        var btnSelectCCAndDr: AppCompatButton =itemView.findViewById(R.id.btn_book_slot)
    }

    fun updateData(context: Context?, data: java.util.ArrayList<GetRefCCBO>) {
        this.context = context!!
        this.getRefCCAndDrList = data
        notifyDataSetChanged()
    }

    interface GetRefCCAndDr{
        fun getRefCCAndDr(getRefCCBO: GetRefCCBO)
    }
}