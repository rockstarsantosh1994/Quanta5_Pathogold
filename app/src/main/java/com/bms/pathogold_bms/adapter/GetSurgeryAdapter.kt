package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.fragment.VitalsFragment
import com.bms.pathogold_bms.model.vitalsurgerlylist.VitalSurgeryListBO

class GetSurgeryAdapter (private var context: Context,
                         private var vitalSurgeryListBOArrayList: ArrayList<VitalSurgeryListBO>,
                         private val vitalsFragment: VitalsFragment
): RecyclerView.Adapter<GetSurgeryAdapter.GetSurgeryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetSurgeryViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view:View=inflater.inflate(R.layout.row_get_slots,parent,false)
        return GetSurgeryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GetSurgeryViewHolder, position: Int) {
        holder.btnSelectSurgery.text=context.resources.getString(R.string.select)

        holder.tvSurgeryTitle.text=vitalSurgeryListBOArrayList[position].SurgeryName

        holder.btnSelectSurgery.setOnClickListener {
            vitalsFragment.setDataToSurgeryEditText(vitalSurgeryListBOArrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return vitalSurgeryListBOArrayList.size
    }

    class GetSurgeryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSurgeryTitle: TextView =itemView.findViewById(R.id.tv_slot_time)
        var btnSelectSurgery: AppCompatButton =itemView.findViewById(R.id.btn_book_slot)
    }

    fun updateData(context: Context?, data: java.util.ArrayList<VitalSurgeryListBO>) {
        this.context = context!!
        this.vitalSurgeryListBOArrayList = data
        notifyDataSetChanged()
    }
}