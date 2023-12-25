package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getwalkdistance.GetWalkDistanceBO

class GetPhelboWalkAdapter (private val context: Context,
                            private val getWalkDistanceBOList: ArrayList<GetWalkDistanceBO>
): RecyclerView.Adapter<GetPhelboWalkAdapter.GetPhelboViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetPhelboViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.row_get_phelbo_walk_distance,parent,false)
        return GetPhelboViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetPhelboViewHolder, position: Int) {
        holder.tvEntryDate.text=getWalkDistanceBOList[position].Entrydate
        holder.tvStartTime.text=getWalkDistanceBOList[position].starttime
        holder.tvEndTime.text=getWalkDistanceBOList[position].endtime
        holder.tvTotalDistance.text="Total Distance: "+getWalkDistanceBOList[position].Distance+"/km"
    }

    override fun getItemCount(): Int {
        return getWalkDistanceBOList.size
    }

    class GetPhelboViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEntryDate:TextView=itemView.findViewById(R.id.tv_entry_date)
        val tvStartTime:TextView=itemView.findViewById(R.id.tv_start_time_value)
        val tvEndTime:TextView=itemView.findViewById(R.id.tv_end_time_value)
        val tvTotalDistance:TextView=itemView.findViewById(R.id.tv_total_distance)
    }
}