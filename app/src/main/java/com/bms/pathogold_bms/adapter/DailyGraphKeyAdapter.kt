package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.GraphBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat
import java.util.*


class DailyGraphKeyAdapter(
    val context: Context,
    private val key: Array<Any>,
    private val getDateAmtMap: Map<String, ArrayList<GraphBO>>? = null,
    var days: Int,
) : RecyclerView.Adapter<DailyGraphKeyAdapter.DailyGraphKeyViewHolder>() {

    private val TAG = "DailyGraphKeyAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyGraphKeyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.row_daily_graph_key, parent, false)
        return DailyGraphKeyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyGraphKeyViewHolder, position: Int) {
        //for not updating graph..
        holder.setIsRecyclable(false)

        var sum = 0
        val formatter = DecimalFormat("#,###,###")
        for (i in 0 until getDateAmtMap!![key[position]]!!.size) {
            sum += getDateAmtMap[key[position]]!![i].stAmount.toInt()
        }

        if(position==0){
            holder.tvKey.text = "" + key[position]+" ("+sum+")"
        }else{
            holder.tvKey.text = "" + key[position]+" ("+CommonMethods.getPrefrence(context,AllKeys.CURRENCY_SYMBOL)+" "+formatter.format(sum)+")"
        }

        //intialise bar chart...
        initBarChart(holder,getDateAmtMap[key[position]]!!)

        //now draw bar chart with dynamic data
        val entries: ArrayList<BarEntry> = ArrayList()

        //you can replace this data object with  your custom object
        for (i in getDateAmtMap[key[position]]!!.indices) {
            val score = getDateAmtMap[key[position]]!![i]
            entries.add(BarEntry(i.toFloat(), score.stAmount.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)

        val data = BarData(barDataSet)

        holder.barChart?.data = data

        holder.barChart?.invalidate()
    }

    private fun initBarChart(holder: DailyGraphKeyViewHolder, graphBOList: ArrayList<GraphBO>) {
        //hide grid lines
        holder.barChart?.axisLeft?.setDrawGridLines(false)
        val xAxis: XAxis? = holder.barChart?.xAxis
        xAxis?.setDrawGridLines(false)
        xAxis?.setDrawAxisLine(false)

        //remove right y-axis
        holder.barChart?.axisRight?.isEnabled = false

        //remove legend
        holder.barChart?.legend?.isEnabled = false


        //remove description label
        holder.barChart?.description?.isEnabled = false

        //add animation
        holder.barChart?.animateY(1000)

        // to draw label on xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis?.valueFormatter = MyAxisFormatter(graphBOList)
        xAxis?.setDrawLabels(true)
        xAxis?.granularity = 1f
        xAxis?.labelRotationAngle = +90f
        xAxis?.textSize=14f

        //Y-Axis left side settings...
        val leftAxis: YAxis = holder.barChart?.axisLeft!!
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum=0f
    }

    override fun getItemCount(): Int {
        return key.size
    }

    class DailyGraphKeyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvKey: TextView = itemView.findViewById(R.id.tv_key)

        val barChart:BarChart?=itemView.findViewById(R.id.chart2)
    }

    inner class MyAxisFormatter(private val graphBOList: ArrayList<GraphBO>) : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            Log.d(TAG, "getAxisLabel: index $index")
            return if (index < graphBOList.size) {
                //here days are refer to minus -7 days  and -1 is for month from current date
                if(days==-7 || days == -1){
                    CommonMethods.parseDateToddMMyyyy(graphBOList[index].stDate, "dd/MM/yyyy", "dd,MMM")!!
                }else{
                    CommonMethods.parseDateToddMMyyyy(graphBOList[index].stDate, "dd/MM", "MMM")!!
                }
            } else {
                ""
            }
        }
    }
}