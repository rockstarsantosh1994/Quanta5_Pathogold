package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.activity.WebviewActivity
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.report.ReportsBO
import com.bms.pathogold_bms.utility.CommonMethods


class ReportsKeyDetailsAdapter(
    private val context: Context,
    private val reportsBOArrayList: ArrayList<ReportsBO>,
    private val getPatientListBO: GetPatientListBO?,
): RecyclerView.Adapter<ReportsKeyDetailsAdapter.ReportsKeyDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsKeyDetailsViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.row_reports_key_details,parent,false)
        return ReportsKeyDetailsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReportsKeyDetailsViewHolder, position: Int) {
        holder.tvTestName.text=reportsBOArrayList[position].testname

        when(reportsBOArrayList[position].type){
            "TestReport"->{
                holder.tvDate.text= "Report date:-"+reportsBOArrayList[position].entrydate
                holder.tvViewReports.text=context.getString(R.string.view_report)
                holder.tvTestName.visibility=View.VISIBLE
                holder.tvTrendAnalysis.visibility=View.VISIBLE
            }

            "Invoice" ->{
                holder.tvDate.text= "Invoice date:-"+reportsBOArrayList[position].entrydate
                holder.tvViewReports.text=context.getString(R.string.invoice_report)
                holder.tvTestName.visibility=View.GONE
                holder.tvTrendAnalysis.visibility=View.GONE
            }
        }

        holder.tvViewReports.setOnClickListener{
            if(reportsBOArrayList[position].balance == "0"){
                val intent = Intent(context, WebviewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("url",reportsBOArrayList[position].url)
                //intent.putExtra("patient_name",reportsBOArrayList[position].intial+""+getPatientListBO?.PatientName)
                intent.putExtra("patient_name",getPatientListBO?.PatientName)
                context.startActivity(intent)
            }else{
                CommonMethods.showDialogForError(context,"Please make payment to download Report!")
            }
        }

        holder.tvTrendAnalysis.setOnClickListener{
            val bundle= Bundle()
            bundle.putParcelable("report_bo", reportsBOArrayList[position])
            (context as DashBoardActivity).navController.navigate(R.id.reportsDetailsFragment,bundle)
        }
    }

    override fun getItemCount(): Int {
        return reportsBOArrayList.size
    }

    class ReportsKeyDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvTestName:TextView=itemView.findViewById(R.id.tv_testname_value)
        var tvDate:TextView=itemView.findViewById(R.id.tv_date)

        var tvViewReports:TextView=itemView.findViewById(R.id.tv_view_pdf)
        var tvTrendAnalysis:TextView=itemView.findViewById(R.id.tv_view_details)
    }

}