package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.card.MaterialCardView

class DailyCashRegisterAdapter(
    private val context: Context,
    private var dailyCashRegisterBOList: ArrayList<DailyCashRegisterBO>,
    private var stName: String,
) : RecyclerView.Adapter<DailyCashRegisterAdapter.DailyCashViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyCashViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.row_daily_cash_register, parent, false)
        return DailyCashViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyCashViewHolder, position: Int) {
        holder.tvPatientExamDate.text =
            CommonMethods.parseDateToddMMyyyy(dailyCashRegisterBOList[position].exam_date,
                "MM/dd/yyyy HH:mm:ss a",
                "dd, MMM yyyy")
        holder.tvPatientBillNo.text =
            context.resources.getString(R.string.billno) + "  " + dailyCashRegisterBOList[position].BillNo
        holder.tvPatientRegNo.text =
            context.resources.getString(R.string.regno) + "  " + dailyCashRegisterBOList[position].RegNo
        holder.tvPatientName.text =
            dailyCashRegisterBOList[position].intial + " " + dailyCashRegisterBOList[position].FirstName + " " + dailyCashRegisterBOList[position].LastName
        if (dailyCashRegisterBOList[position].TelNo.isEmpty()) {
            holder.tvPatientMobileNo.visibility = View.GONE
            holder.tvPatientMobile.visibility = View.GONE
            holder.tvPatientMobileNo.text = dailyCashRegisterBOList[position].TelNo
        } else {
            holder.tvPatientMobileNo.visibility = View.VISIBLE
            holder.tvPatientMobile.visibility = View.VISIBLE
            holder.tvPatientMobileNo.text = dailyCashRegisterBOList[position].TelNo
        }
        if (dailyCashRegisterBOList[position].Modeofpay.isEmpty()) {
            holder.tvModeOfPay.visibility = View.GONE
            holder.tvModeOfPayText.visibility = View.GONE
            holder.tvModeOfPay.text = dailyCashRegisterBOList[position].Modeofpay
        } else {
            holder.tvModeOfPay.visibility = View.VISIBLE
            holder.tvModeOfPayText.visibility = View.VISIBLE
            holder.tvModeOfPay.text = dailyCashRegisterBOList[position].Modeofpay
        }

        holder.tvPatientTestName.text = dailyCashRegisterBOList[position].testname
        holder.tvPatientStatus.text = dailyCashRegisterBOList[position].status

        when(dailyCashRegisterBOList[position].status) {
            "Registered", "Cash" -> {
                holder.tvPatientStatus.setTextColor(context.getColor(R.color.key_color))
                holder.tvModeOfPay.setTextColor(context.getColor(R.color.key_color))
            }

            "Tested","Cheque","cheque" -> {
                holder.tvPatientStatus.setTextColor(context.getColor(R.color.property_color))
                holder.tvModeOfPay.setTextColor(context.getColor(R.color.property_color))
            }

            "Authorized", "Partial Authorized" , "Online" -> {
                holder.tvPatientStatus.setTextColor(context.getColor(R.color.personal_color))
                holder.tvModeOfPay.setTextColor(context.getColor(R.color.personal_color))
            }

            "Printed","UPI" -> {
                holder.tvPatientStatus.setTextColor(context.getColor(R.color.business_color))
                holder.tvModeOfPay.setTextColor(context.getColor(R.color.business_color))
            }
        }

        when(dailyCashRegisterBOList[position].Modeofpay) {
            "Cash","cash"-> {
                holder.tvModeOfPay.setTextColor(context.getColor(R.color.key_color))
            }

            "Cheque","cheque" -> {
                holder.tvModeOfPay.setTextColor(context.getColor(R.color.property_color))
            }

            "Online","online" -> {
                holder.tvModeOfPay.setTextColor(context.getColor(R.color.personal_color))
            }

            "UPI" -> {
                holder.tvModeOfPay.setTextColor(context.getColor(R.color.business_color))
            }
        }

        holder.tvBillAmt.text =
            context.resources.getString(R.string.bill_amt) + " " + CommonMethods.getPrefrence(context, AllKeys.CURRENCY_SYMBOL) + " " + dailyCashRegisterBOList[position].BillAmt
        holder.tvNetAmt.text =
            context.resources.getString(R.string.rec_amt) + " " + CommonMethods.getPrefrence(context,
                AllKeys.CURRENCY_SYMBOL) + " " + dailyCashRegisterBOList[position].NetPayment
        holder.tvBalance.text =
            context.resources.getString(R.string.bal) + " " + CommonMethods.getPrefrence(context,
                AllKeys.CURRENCY_SYMBOL) + " " + dailyCashRegisterBOList[position].Balance
        holder.tvDiscount.text =
            context.resources.getString(R.string.dis) + " " + CommonMethods.getPrefrence(context,
                AllKeys.CURRENCY_SYMBOL) + " " + dailyCashRegisterBOList[position].Discount

        if(dailyCashRegisterBOList[position].BillNo=="0"){
            holder.tvPatientBillNo.setTextColor(context.getColor(R.color.red))
        }else{
            holder.tvPatientBillNo.setTextColor(context.getColor(R.color.purple_700))
        }

        when (stName) {
            AllKeys.TEST_CHARGES -> {
                if (dailyCashRegisterBOList[position].TestCharges == "0") {
                    holder.cvMaterialCardView.visibility = View.GONE
                } else {
                    holder.cvMaterialCardView.visibility = View.VISIBLE
                }
            }

            AllKeys.UNBILLED_AMT ->{
                if (dailyCashRegisterBOList[position].BillNo == "0") {
                    holder.cvMaterialCardView.visibility = View.VISIBLE
                } else {
                    holder.cvMaterialCardView.visibility = View.GONE
                }
            }

            AllKeys.BILL_AMT -> {
                if (dailyCashRegisterBOList[position].BillNo != "0") {
                    holder.cvMaterialCardView.visibility = View.VISIBLE
                } else {
                    holder.cvMaterialCardView.visibility = View.GONE
                }
            }

            //which is name as NetPayment in api
            AllKeys.RECEIVED_AMT->{
                if (dailyCashRegisterBOList[position].NetPayment == "0") {
                    holder.cvMaterialCardView.visibility = View.GONE
                } else {
                    holder.cvMaterialCardView.visibility = View.VISIBLE
                }
            }

            AllKeys.DISCOUNT -> {
                if (dailyCashRegisterBOList[position].Discount == "0") {
                    holder.cvMaterialCardView.visibility = View.GONE
                } else {
                    holder.cvMaterialCardView.visibility = View.VISIBLE
                }
            }

            AllKeys.BALANCE -> {
                if (dailyCashRegisterBOList[position].Balance == "0") {
                    holder.cvMaterialCardView.visibility = View.GONE
                } else {
                    holder.cvMaterialCardView.visibility = View.VISIBLE
                }
            }

            AllKeys.OTHER_CHARGES->{
                if (dailyCashRegisterBOList[position].Othercharges == "0") {
                    holder.cvMaterialCardView.visibility = View.GONE
                } else {
                    holder.cvMaterialCardView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dailyCashRegisterBOList.size
    }

    class DailyCashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPatientExamDate: TextView = itemView.findViewById(R.id.tv_patient_exam_date)
        var tvPatientBillNo: TextView = itemView.findViewById(R.id.tv_patient_bill_no)
        var tvPatientRegNo: TextView = itemView.findViewById(R.id.tv_patient_reg_no)
        var tvPatientName: TextView = itemView.findViewById(R.id.tv_patient_name)
        var tvPatientMobileNo: TextView = itemView.findViewById(R.id.tv_patient_mobile_value)
        var tvPatientMobile: TextView = itemView.findViewById(R.id.tv_patient_mobile)
        var tvModeOfPay: TextView = itemView.findViewById(R.id.tv_mode_of_pay)
        var tvModeOfPayText: TextView = itemView.findViewById(R.id.tv_mob_of_pay_text)
        var tvPatientTestName: TextView = itemView.findViewById(R.id.tv_patient_testname)
        var tvPatientStatus: TextView = itemView.findViewById(R.id.tv_patient_status_value)

        var cvMaterialCardView: MaterialCardView = itemView.findViewById(R.id.cv_patient_details)

        var tvBillAmt: TextView = itemView.findViewById(R.id.tv_bill_amt)
        var tvNetAmt: TextView = itemView.findViewById(R.id.tv_net_amt)
        var tvBalance: TextView = itemView.findViewById(R.id.tv_balance)
        var tvDiscount: TextView = itemView.findViewById(R.id.tv_discount)
    }
}