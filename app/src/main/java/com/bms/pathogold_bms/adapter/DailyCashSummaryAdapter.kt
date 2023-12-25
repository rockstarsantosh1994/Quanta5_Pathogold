package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterSummaryBO
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import androidx.core.content.ContextCompat
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import java.text.DecimalFormat

class DailyCashSummaryAdapter(
    private var context: Context,
    private var dailyCashRegisterSummaryList: ArrayList<DailyCashRegisterSummaryBO>,
    val getPatientRegisterDetails: GetPatientRegisterDetails,
) : RecyclerView.Adapter<DailyCashSummaryAdapter.DailyCashSummaryViewHolder>() {

    private val backgroundColors = intArrayOf(R.color.red100, R.color.purple100 , R.color.blue100 , R.color.teal100,R.color.green100)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyCashSummaryViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_daily_cash_summary,parent,false)
        return DailyCashSummaryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyCashSummaryViewHolder, position: Int) {
        val formatter = DecimalFormat("#,###,###")
        if(position==0){
            holder.tvSummaryCount.text=dailyCashRegisterSummaryList[position].stCount
        }else{
            holder.tvSummaryCount.text=CommonMethods.getPrefrence(context,AllKeys.CURRENCY_SYMBOL)+" "+formatter.format(dailyCashRegisterSummaryList[position].stCount.toInt())
        }

        holder.tvSummaryName.text=dailyCashRegisterSummaryList[position].stName

        val bgColor = ContextCompat.getColor(context, backgroundColors[position % 5])
        holder.cardView.setBackgroundColor(bgColor)

        holder.cardView.setOnClickListener{
            if(dailyCashRegisterSummaryList[position].stCount == "0"){
                CommonMethods.showDialogForError(context,"No Data Found!")
            }else{
                getPatientRegisterDetails.getPatientRegisterDetails(dailyCashRegisterSummaryList[position].stName)
            }
        }
    }

    override fun getItemCount(): Int {
        return dailyCashRegisterSummaryList.size
    }

    class DailyCashSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSummaryCount:TextView= itemView.findViewById(R.id.tv_summary_count)
        val tvSummaryName:TextView= itemView.findViewById(R.id.tv_summary_name)
        val cardView:CircularRevealCardView=itemView.findViewById(R.id.cv_cardview)
    }

    interface GetPatientRegisterDetails{
        fun getPatientRegisterDetails(stName: String)
    }
}