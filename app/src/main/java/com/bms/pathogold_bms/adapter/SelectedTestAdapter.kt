package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.BookAppointmentActivity
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods

class SelectedTestAdapter(
    var context: Context,
    var getTestCodeList: ArrayList<GetTestCodeBO>,
    val deleteSelectedTest: DeleteSelectedTest,
    val deleteButtonVisibility: Int
): RecyclerView.Adapter<SelectedTestAdapter.SelectedTestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedTestViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_selected_test,parent,false)
        return SelectedTestViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SelectedTestViewHolder, position: Int) {
        holder.tvTestName.text=getTestCodeList[position].tlcode+"."+getTestCodeList[position].title
        holder.tvRate.text= CommonMethods.getPrefrence(context, AllKeys.CURRENCY_SYMBOL) + " " + getTestCodeList[position].rate+"/-"

        holder.tvDeleteTest.visibility=deleteButtonVisibility

        holder.tvDeleteTest.setOnClickListener {
            deleteSelectedTest.onDeleteTest(position)
        }
    }

    override fun getItemCount(): Int {
        return getTestCodeList.size
    }

    class SelectedTestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var tvTestName:TextView = itemView.findViewById(R.id.tv_test_name)
            var tvRate:TextView = itemView.findViewById(R.id.tv_test_rate)
            var tvDeleteTest:TextView = itemView.findViewById(R.id.tv_delete)
    }

    interface DeleteSelectedTest{
        fun onDeleteTest(position: Int)
    }
}