package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.fragment.CheckSlotFragment
import com.bms.pathogold_bms.model.getslots.GetSlotsBO

class GetSlotsAdapter (var context:Context,
                       var getSlotBOArrayList:ArrayList<GetSlotsBO>,
                       var getSlotAdapterOperation:GetSlotAdapterOperation,
                       val stAppointmentType:String): RecyclerView.Adapter<GetSlotsAdapter.GetSlotsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetSlotsViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_get_slots,parent,false)
        return GetSlotsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetSlotsViewHolder, position: Int) {
        holder.tvSlotTime.text=getSlotBOArrayList.get(position).ShiftName+" "+ getSlotBOArrayList[position].Slot

        holder.constraintLayout.setOnClickListener {
            appointmentBookingOperation(position)
        }

        holder.btnBookSlot.setOnClickListener{
            appointmentBookingOperation(position)
        }
    }

    private fun appointmentBookingOperation(position: Int) {
        //stAppointmentType will have appointment type i.e: new, reschdeule
        when {
            stAppointmentType.equals("new",false) -> {
                getSlotAdapterOperation.onBookSlot(getSlotBOArrayList[position])
            }
            stAppointmentType.equals("reschdeule",false) -> {
                getSlotAdapterOperation.reScheduleAppointment(getSlotBOArrayList[position])
            }
            stAppointmentType.equals("vaccine",false) -> {
                getSlotAdapterOperation.onBookSlot(getSlotBOArrayList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return getSlotBOArrayList.size
    }

    class GetSlotsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSlotTime:TextView=itemView.findViewById(R.id.tv_slot_time)
        var btnBookSlot:AppCompatButton=itemView.findViewById(R.id.btn_book_slot)
        var constraintLayout:ConstraintLayout=itemView.findViewById(R.id.constraintLayout)
    }

    interface GetSlotAdapterOperation{
        fun onBookSlot(getSlotsBO: GetSlotsBO)
        fun reScheduleAppointment(getSlotsBO: GetSlotsBO)
    }
}