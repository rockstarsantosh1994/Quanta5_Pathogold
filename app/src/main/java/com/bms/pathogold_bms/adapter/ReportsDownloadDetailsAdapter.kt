package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.reportdetails.ReportDetailsBO
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReportsDownloadDetailsAdapter(
    var context: Context,
    var reportsDetialsArrayList: ArrayList<ReportDetailsBO>,
)
    : RecyclerView.Adapter<ReportsDownloadDetailsAdapter.ReportsDownloadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsDownloadViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_reprots_download_details,parent,false)
        return ReportsDownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportsDownloadViewHolder, position: Int) {
        holder.tvValue.text=reportsDetialsArrayList[position].result
        holder.tvEntryDate.text=parseDateToddMMyyyy(reportsDetialsArrayList[position].EntryDate)
    }

    override fun getItemCount(): Int {
        return reportsDetialsArrayList.size
    }

    class ReportsDownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvValue:TextView= itemView.findViewById(R.id.tv_value)
        var tvEntryDate:TextView = itemView.findViewById(R.id.tv_date)
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDateToddMMyyyy(time: String?): String? {
        val inputPattern = "MM/dd/yyyy HH:mm:ss a"
        val outputPattern = "dd, MMM yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }
}