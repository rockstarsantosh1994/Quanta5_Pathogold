package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.service.GetServiceBO
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ServiceAdapter(private var context:Context,
                     private var getServiceBOList:ArrayList<GetServiceBO>,
                     private val serviceAdapterClick: ServiceAdapterClick
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    private val TAG = "ServiceAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_service_name,parent,false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.tvServiceName.text = getServiceBOList[position].Servicename

        holder.tvMakePayment.setOnClickListener { view -> holder.llMakePaymentView.visibility = if (holder.llMakePaymentView.visibility === View.VISIBLE) View.GONE else View.VISIBLE
            //calculate Amount and get Total.
            calculateAmount(holder)
        }

        holder.etEnterAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                //calculate Amount and get Total.
                calculateAmount(holder)
            }
        })

        holder.tvProceedToPay.setOnClickListener {
            if(isValidated(holder)){
                serviceAdapterClick.proceedToPay(getServiceBOList[position],holder.tvTotal.text.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateAmount(holder: ServiceViewHolder) {
        if(isValidated(holder)){
            holder.tvSubTotal.text=holder.etEnterAmount.text.toString()
            var gst = 0f
            gst = (holder.tvSubTotal.text.toString().toFloat()/ 100)* 18
            holder.tvGst.text=gst.toString()
            holder.tvTotal.text = (gst+holder.tvSubTotal.text.toString().toInt()).toString()
        }
    }

    override fun getItemCount(): Int {
        return getServiceBOList.size
    }

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvServiceName:TextView=itemView.findViewById(R.id.tv_service_name)
        val tvMakePayment:TextView=itemView.findViewById(R.id.tv_make_payment)

        val llMakePaymentView:LinearLayout= itemView.findViewById(R.id.ll_make_payment_view)

        val tilEnterAmount:TextInputLayout= itemView.findViewById(R.id.til_enter_amount)
        val etEnterAmount:TextInputEditText= itemView.findViewById(R.id.et_enter_amount)

        val tvSubTotal:TextView=itemView.findViewById(R.id.tv_sub_total)
        val tvGst:TextView=itemView.findViewById(R.id.tv_gst)
        val tvTotal:TextView=itemView.findViewById(R.id.tv_total)

        val tvProceedToPay:TextView=itemView.findViewById(R.id.tv_proceed_to_pay)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(context: Context?, data: ArrayList<GetServiceBO>) {
        this.context = context!!
        this.getServiceBOList = data
        notifyDataSetChanged()
    }

    interface ServiceAdapterClick{
        fun proceedToPay(getServiceBO: GetServiceBO, totalPayment: String)
    }

    @SuppressLint("SetTextI18n")
    private fun isValidated(holder: ServiceViewHolder): Boolean {
        if(holder.etEnterAmount.text.toString().isEmpty()){
            holder.tilEnterAmount.error ="Amount Required!"
            holder.tilEnterAmount.requestFocus()
            return false
        }else{
            holder.tilEnterAmount.isErrorEnabled = false
        }

        if(holder.etEnterAmount.text.toString().toInt()<100){
            holder.tilEnterAmount.error ="Min. amount should be 100!"
            holder.tilEnterAmount.requestFocus()
            return false
        }else{
            holder.tilEnterAmount.isErrorEnabled = false
        }

        return true
    }
}