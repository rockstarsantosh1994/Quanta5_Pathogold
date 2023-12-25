package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods


class GetTestCodeAdapter(
    private var context: Context,
    private var getTestPatientTestListBO: ArrayList<GetTestCodeBO>,
    private var addTestCodeArrayList: ArrayList<GetTestCodeBO>,
)    : RecyclerView.Adapter<GetTestCodeAdapter.GetTestCodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetTestCodeViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_get_test_code, parent, false)
        return GetTestCodeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetTestCodeViewHolder, position: Int) {
        var currency=CommonMethods.getPrefrence(context, AllKeys.CURRENCY_SYMBOL) ?: "Rs"
        holder.chCheckBox.text= getTestPatientTestListBO[position].tlcode+"."+getTestPatientTestListBO[position].title
        holder.tvTestRate.text = currency+" "+getTestPatientTestListBO[position].rate+"/-"
        holder.chCheckBox.isChecked = getTestPatientTestListBO[position].isSelected.toBoolean()
        holder.chCheckBox.tag = position

        holder.chCheckBox.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            //set your object's last status
            val pos = holder.chCheckBox.tag

            getTestPatientTestListBO[pos as Int].isSelected = (!getTestPatientTestListBO[pos].isSelected.toBoolean()).toString()
        }
    }

    override fun getItemCount(): Int {
        return getTestPatientTestListBO.size
    }

    fun updateData(context: Context?, data: ArrayList<GetTestCodeBO>) {
        this.context = context!!
        this.getTestPatientTestListBO = data
        notifyDataSetChanged()
    }

    class GetTestCodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvTestRate:TextView=itemView.findViewById(R.id.tv_test_rate)
        var chCheckBox:CheckBox=itemView.findViewById(R.id.cb_checbox)
    }

    interface GetTestCode{
        fun getTestCode(consultationBO: ConsultationBO)
    }
}