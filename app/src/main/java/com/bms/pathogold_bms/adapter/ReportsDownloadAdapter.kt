package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.reportdetails.ReportDetailsBO
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class ReportsDownloadAdapter(
    private val context: Context? = null,
    private val key: Array<Any>,
    private val getReportsDetailsListMap: Map<String, ArrayList<ReportDetailsBO>>? = null,
) : RecyclerView.Adapter<ReportsDownloadAdapter.ReportsKeyViewHolder>() {

    private val TAG = "ReportsDownloadAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsKeyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_download, parent, false)
        return ReportsKeyViewHolder(view, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReportsKeyViewHolder, position: Int) {
        //for not updating graph..
        holder.setIsRecyclable(false)

        holder.tvKey.text = "" + key[position]

        if (getReportsDetailsListMap != null) {
            if (getReportsDetailsListMap[key[position]] != null) {
                val reportsDownloadDetailsAdapter = ReportsDownloadDetailsAdapter(context!!,
                    getReportsDetailsListMap[key[position]]!!)
                holder.rvKey.adapter = reportsDownloadDetailsAdapter
            }
        }

        //intialise line chart...
        initLineChart(holder, getReportsDetailsListMap?.get(key[position])!!)

        //Set data to line chart...
        setDataToLineChart(holder, getReportsDetailsListMap.get(key[position])!!)
    }

    private fun initLineChart(
        holder: ReportsKeyViewHolder,
        reportDetailsBOList: ArrayList<ReportDetailsBO>,
    ) {
        //hide grid lines
        holder.lineChart?.axisLeft?.setDrawGridLines(false)
        val xAxis: XAxis = holder.lineChart?.xAxis!!
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        holder.lineChart.axisRight.isEnabled = false

        //remove legend
        holder.lineChart.legend.isEnabled = false

        //remove description label
        holder.lineChart.description.isEnabled = false

        //add animation
        holder.lineChart.animateX(1000, Easing.EaseInSine)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
       // xAxis.valueFormatter = MyAxisFormatter(reportDetailsBOList)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f

        //Y-Axis left side settings...
        val leftAxis: YAxis = holder.lineChart.axisLeft!!
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 0f
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToLineChart(holder: ReportsKeyViewHolder, reportDetailsBOList: ArrayList<ReportDetailsBO>, ) {
        //now draw line chart with dynamic data
        val entries: ArrayList<Entry> = ArrayList()

        //you can replace this data object with  your custom object
        for (i in reportDetailsBOList.indices) {
            val score = reportDetailsBOList[i]

            if (reportDetailsBOList[i].EntryDate.isNotEmpty()) {
                holder.tvNormalValue.visibility = View.VISIBLE
                holder.tvNormalValue.text = "NORMAL RANGE :" + reportDetailsBOList[i].normalRange
            } else {
                holder.tvNormalValue.visibility = View.GONE
            }

            if (reportDetailsBOList[i].EntryDate.isNotEmpty() && reportDetailsBOList[i].result.isNotEmpty()) {
                if (!isLetters(reportDetailsBOList[i].result) && !isLetters(reportDetailsBOList[i].normalRange)) {
                    entries.add(Entry(i.toFloat(), score.result.toFloat()))
                    holder.lineChart?.visibility = View.VISIBLE
                } else {
                    holder.lineChart?.visibility = View.GONE
                }
            } else {
                holder.lineChart?.visibility = View.GONE
            }
        }

        val lineDataSet = LineDataSet(entries, "")
        val data = LineData(lineDataSet)
        lineDataSet.lineWidth=5f
        lineDataSet.color= context?.resources?.getColor(R.color.red)!!
        holder.lineChart?.data = data

        holder.lineChart?.invalidate()
    }

    override fun getItemCount(): Int {
        return key.size
    }

    class ReportsKeyViewHolder(itemView: View, context: Context?) :
        RecyclerView.ViewHolder(itemView) {
        val tvKey: TextView = itemView.findViewById(R.id.tv_key)
        val rvKey: RecyclerView = itemView.findViewById(R.id.rv_key_data)
        val lineChart: LineChart? = itemView.findViewById(R.id.chart1)
        val tvNormalValue: TextView = itemView.findViewById(R.id.tv_normal_value)

        init {
            val mLayoutManager: LinearLayoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            rvKey.layoutManager = mLayoutManager
        }
    }

    /*inner class MyAxisFormatter(private val reportDetailsBOList: ArrayList<ReportDetailsBO>) : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?) {
            val index = value.toInt()
            return (if (index < reportDetailsBOList.size) {
                if (reportDetailsBOList[index].EntryDate.isEmpty()) {
                } else {
                    CommonMethods.parseDateToddMMyyyy(reportDetailsBOList[index].EntryDate,"MM/dd/yyyy HH:mm:ss a","dd, MMM yyyy")
                }
            } else {
                ""
            }) as Unit
        }
    }*/

    private fun isLetters(string: String): Boolean {
        for (c in string) {
            if (c !in 'A'..'Z' && c !in 'a'..'z') {
                return false
            }
        }
        return true
    }
}