package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO

class GetLabNameAdapter (private var context: Context,
                         private var getLabNameArrayList: ArrayList<LabNameBO>,
                         val getLabName: GetLabNameSuperAdmin): RecyclerView.Adapter<GetLabNameAdapter.GetSurgeryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetSurgeryViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view:View=inflater.inflate(R.layout.row_get_slots,parent,false)
        return GetSurgeryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GetSurgeryViewHolder, position: Int) {
        holder.btnSelectLabName.text=context.resources.getString(R.string.select)

        holder.tvLabName.text=getLabNameArrayList[position].labname_c

        holder.constraintLayout.setOnClickListener {
            getLabName.getLabName(getLabNameArrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return getLabNameArrayList.size
    }

    class GetSurgeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLabName: TextView =itemView.findViewById(R.id.tv_slot_time)
        var btnSelectLabName: AppCompatButton =itemView.findViewById(R.id.btn_book_slot)
        var constraintLayout:ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
    }

    fun updateData(context: Context?, data: ArrayList<LabNameBO>) {
        this.context = context!!
        this.getLabNameArrayList = data
        notifyDataSetChanged()
    }

    interface GetLabNameSuperAdmin{
        fun getLabName(labNameBO: LabNameBO)
    }
}