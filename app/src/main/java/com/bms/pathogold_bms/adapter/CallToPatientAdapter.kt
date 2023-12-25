package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import kotlin.collections.ArrayList

class CallToPatientAdapter(
    var context: Context,
    private var getPatientListBOArrayList: ArrayList<GetPatientListBO>,
    private var stDate: String,
    val videoCallPatient:VideoCallPatient
) : RecyclerView.Adapter<CallToPatientAdapter.GetPatientViewHolder>() {

    private val backgroundColors = intArrayOf(R.color.red100, R.color.purple100 , R.color.blue100 , R.color.teal100,R.color.green100)

    private val TAG = "AllPatientListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetPatientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.row_call_to_dr, parent, false)
        return GetPatientViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetPatientViewHolder, position: Int) {
        holder.tvPatientName.text = getPatientListBOArrayList[position].PatientName

        val bgColor = ContextCompat.getColor(context, backgroundColors[position % 5])
        holder.cardView.setBackgroundColor(bgColor)

        holder.cardView.setOnClickListener {
            videoCallPatient.videoCallToPatient(getPatientListBOArrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return getPatientListBOArrayList.size
    }

    class GetPatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPatientName:TextView= itemView.findViewById(R.id.tv_summary_name)
        val cardView: CircularRevealCardView =itemView.findViewById(R.id.cv_cardview)
    }

    fun updateData(context: Context?, data: ArrayList<GetPatientListBO>) {
        this.context = context!!
        this.getPatientListBOArrayList = data
        notifyDataSetChanged()
    }

    interface VideoCallPatient{
        fun videoCallToPatient(getPatientListBO: GetPatientListBO)
    }
}