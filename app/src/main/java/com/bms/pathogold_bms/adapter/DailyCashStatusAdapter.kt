package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterBO
import java.util.ArrayList

class DailyCashStatusAdapter(
    private val context: Context,
    private val key: Array<Any>,
    private val getReportsDetailsListMap: Map<String, ArrayList<DailyCashRegisterBO>>? = null,
): RecyclerView.Adapter<DailyCashStatusAdapter.DailyCashStatusViewHolder>() {

    private val backgroundColors = intArrayOf(R.color.teal100, R.color.blue100 , R.color.purple100 , R.color.red100,R.color.green100)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyCashStatusViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_daily_cash_status,parent,false)
        return DailyCashStatusViewHolder(view,context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyCashStatusViewHolder, position: Int) {
        holder.tvKey.text = "" + key[position]

        val bgColor = ContextCompat.getColor(context, backgroundColors[position % 5])
        holder.cardView.setBackgroundColor(bgColor)

        if(getReportsDetailsListMap?.size!!>0){
            if(getReportsDetailsListMap[key[position]] !=null){

                holder.tvNoOfRecordsCount.text="No. of records " + getReportsDetailsListMap[key[position]]!!.size

                val dailyCashRegisterAdapter=DailyCashRegisterAdapter(context, getReportsDetailsListMap[key[position]]!!,"no_name")
                holder.rvDailyCashStatus.adapter=dailyCashRegisterAdapter
            }
        }

        holder.cardView.setOnClickListener { view -> holder.rvDailyCashStatus.visibility = if (holder.rvDailyCashStatus.visibility === View.VISIBLE) View.GONE else View.VISIBLE }
    }

    override fun getItemCount(): Int {
        return key.size
    }

    class DailyCashStatusViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var tvKey:TextView=itemView.findViewById(R.id.tv_key)
        var tvNoOfRecordsCount:TextView=itemView.findViewById(R.id.tv_no_of_records_count)
        var cardView:CardView=itemView.findViewById(R.id.cardview)

        var rvDailyCashStatus:RecyclerView=itemView.findViewById(R.id.rv_daily_cash_status)

        init {
            val linearLayoutManager= LinearLayoutManager(context)
            rvDailyCashStatus.layoutManager=linearLayoutManager
        }
    }
}