package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.fragment.VitalsFragment
import com.bms.pathogold_bms.model.emrcheckboxlist.GetEMRCheckBoxListBO

class GetEMRCheckBoxDetailsAdapter(val context:Context,
                                    val getEMRCheckBoxListBOArrayList:ArrayList<GetEMRCheckBoxListBO>
                                    ,val vitalsFragment: VitalsFragment
)  :
    RecyclerView.Adapter<GetEMRCheckBoxDetailsAdapter.GetEMRDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetEMRDetailsViewHolder {
       val inflater=LayoutInflater.from(parent.context)
       val view=inflater.inflate(R.layout.row_get_emr_checkbox_details,parent,false)
       return GetEMRDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GetEMRDetailsViewHolder, position: Int) {
        holder.checkBox.text=getEMRCheckBoxListBOArrayList[position].HistoryName

        holder.checkBox.isChecked = getEMRCheckBoxListBOArrayList[position].getSelected()
        holder.checkBox.tag = position

        holder.checkBox.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            //set your object's last status
            val pos = holder.checkBox.tag
            if (getEMRCheckBoxListBOArrayList[pos as Int].getSelected()) {
                getEMRCheckBoxListBOArrayList[pos].setSelected(false)
            } else {
                getEMRCheckBoxListBOArrayList[pos].setSelected(true)
            }
        }
    }

    override fun getItemCount(): Int {
       return getEMRCheckBoxListBOArrayList.size
    }

    class GetEMRDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkBox:CheckBox=itemView.findViewById(R.id.cb_checkbox)
    }
}