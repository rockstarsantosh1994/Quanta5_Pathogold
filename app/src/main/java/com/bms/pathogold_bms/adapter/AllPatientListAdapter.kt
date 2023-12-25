package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.*
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.report.ReportsBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import kotlin.collections.ArrayList

class AllPatientListAdapter(
    var context: Context,
    private var getPatientListBOArrayList: ArrayList<GetPatientListBO>,
    private var stDate: String,
    val openPaymentGateway: OpenPaymentGateway
) : RecyclerView.Adapter<AllPatientListAdapter.GetPatientViewHolder>() {

    private val TAG = "AllPatientListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetPatientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.row_all_patient_list, parent, false)
        return GetPatientViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetPatientViewHolder, position: Int) {
        holder.tvPatientName.text = getPatientListBOArrayList[position].PatientName
        // holder.tvAge.text = getPatientListBOArrayList[position].age
        holder.tvAge.text =
            CommonMethods.convertToYearsMonthsDays(CommonMethods.getTodayDate("MM/dd/yyyy"),
                CommonMethods.parseDateToddMMyyyy(getPatientListBOArrayList[position].DOB,
                    "MM/dd/yyyy HH:mm:ss",
                    "MM/dd/yyyy"))
        holder.tvSex.text = getPatientListBOArrayList[position].sex
        holder.tvPatientMobileValue.text = getPatientListBOArrayList[position].PatientPhoneNo
        holder.tvDOBValue.text =
            CommonMethods.parseDateToddMMyyyy(getPatientListBOArrayList[position].DOB,
                "MM/dd/yyyy HH:mm:ss",
                "dd/MM/yyyy")

        holder.tvPatientRegNo.text = context.resources.getString(R.string.regno) + "  " +getPatientListBOArrayList[position].regno
        holder.tvPatientRegDate.text =
            CommonMethods.parseDateToddMMyyyy(getPatientListBOArrayList[position].Entrydate,
                "MM/dd/yyyy HH:mm:ss a",
                "dd, MMM yyyy")

        if(getPatientListBOArrayList[position].DOB.isEmpty()){
            holder.tvDOBValue.visibility=View.GONE
            holder.tvDOB.visibility=View.GONE
        }else{
            holder.tvDOBValue.visibility=View.VISIBLE
            holder.tvDOB.visibility=View.VISIBLE
        }

        if(getPatientListBOArrayList[position].PatientPhoneNo.isNotEmpty()){
            holder.tvPatientMobileValue.visibility=View.VISIBLE
            holder.tvPatientMobile.visibility=View.VISIBLE
        }else{
            holder.tvPatientMobileValue.visibility=View.GONE
            holder.tvPatientMobile.visibility=View.GONE
        }

        if(CommonMethods.getPrefrence(context, AllKeys.LOGIN_TYPE).equals(AllKeys.Phlebotomist) || CommonMethods.getPrefrence(context, AllKeys.LOGIN_TYPE).equals(AllKeys.Administrator)){
            holder.tvViewConsent.visibility=View.VISIBLE
            holder.tvSubmitConsent.visibility=View.VISIBLE
        }else{
            holder.tvViewConsent.visibility=View.VISIBLE
            holder.tvSubmitConsent.visibility=View.VISIBLE
        }
        if (getPatientListBOArrayList[position].finalteststatus.isNotEmpty()) {
            holder.tvPatientStatus.visibility = View.VISIBLE
            holder.tvPatientStatusValue.visibility = View.VISIBLE

            holder.tvPatientStatusValue.text = getPatientListBOArrayList[position].finalteststatus

            when(getPatientListBOArrayList[position].finalteststatus){
                "Registered"->{
                    holder.tvPatientStatusValue.setTextColor(context.getColor(R.color.key_color))
                }

                "Tested"->{
                    holder.tvPatientStatusValue.setTextColor(context.getColor(R.color.property_color))
                }

                "Authorized","Partial Authorized" ->{
                    holder.tvPatientStatusValue.setTextColor(context.getColor(R.color.personal_color))
                }

                "Printed"->{
                    holder.tvPatientStatusValue.setTextColor(context.getColor(R.color.business_color))
                }
            }

        } else {
            holder.tvPatientStatus.visibility = View.GONE
            holder.tvPatientStatusValue.visibility = View.GONE
        }

        //Appointment button visibility...
        if (CommonMethods.getPrefrence(context, AllKeys.appointment).equals("true", true)) {
            holder.tvBookAppointment.visibility = View.VISIBLE
        } else {
            holder.tvBookAppointment.visibility = View.GONE
        }

        //vital button visibility...
        if (CommonMethods.getPrefrence(context, AllKeys.vital).equals("true", true)) {
            holder.tvVitals.visibility = View.VISIBLE
        } else {
            holder.tvVitals.visibility = View.GONE
        }

        //file upload button visibility..
        if (CommonMethods.getPrefrence(context, AllKeys.fileupload).equals("true", true)) {
            holder.tvFileUpload.visibility = View.VISIBLE
        } else {
            holder.tvFileUpload.visibility = View.GONE
        }

        //prescription button visibility..
        if (CommonMethods.getPrefrence(context, AllKeys.Prescription).equals("true", true)) {
            holder.tvPrescription.visibility = View.VISIBLE
        } else {
            holder.tvPrescription.visibility = View.GONE
        }

        /*//video button visibility..
        if (CommonMethods.getPrefrence(context, AllKeys.videocall).equals("true", true)) {
            holder.tvVideoCall.visibility = View.VISIBLE
        } else {
            holder.tvVideoCall.visibility = View.GONE
        }*/

        //vaccination button visibility..
        if (CommonMethods.getPrefrence(context, AllKeys.vaccination).equals("true", true)) {
            holder.tvVaccinationBook.visibility = View.VISIBLE
        } else {
            holder.tvVaccinationBook.visibility = View.GONE
        }

        //report_download button visibility...
        if (CommonMethods.getPrefrence(context, AllKeys.report_download).equals("true", true)) {
            holder.tvReports.visibility = View.VISIBLE
        } else {
            holder.tvReports.visibility = View.GONE
        }

        //Reports details button visiblity
        if (CommonMethods.getPrefrence(context, AllKeys.reportdetails).equals("true", true)) {
            holder.tvViewReportsDetails.visibility = View.VISIBLE
        } else {
            holder.tvViewReportsDetails.visibility = View.GONE
        }


        if(getPatientListBOArrayList[position].balance == "0" || getPatientListBOArrayList[position].balance.isEmpty()){
            holder.tvPatientBalance.visibility=View.GONE
            holder.tvPatientBalanceValue.visibility=View.GONE
        }else{
            holder.tvPatientBalance.visibility=View.VISIBLE
            holder.tvPatientBalanceValue.visibility=View.VISIBLE

            holder.tvPatientBalanceValue.text = CommonMethods.getPrefrence(context,AllKeys.CURRENCY_SYMBOL)+" "+getPatientListBOArrayList[position].balance+"/-"
        }

        if (CommonMethods.getPrefrence(context, AllKeys.USERTYPE).equals(AllKeys.Consultant)) {
            when (getPatientListBOArrayList[position].UserName) {
                "1" -> {
                    holder.tvAppointmentStatus.visibility = View.VISIBLE
                    holder.tvAppointmentStatus.text = "Completed"
                }
                "0" -> {
                    holder.tvAppointmentStatus.visibility = View.VISIBLE
                    holder.tvAppointmentStatus.text = "Unread"
                    holder.tvAppointmentStatus.setTextColor(context.resources.getColor(R.color.red))
                }
                else -> {
                    holder.tvAppointmentStatus.visibility = View.GONE
                }
            }
        }


        holder.tvVitals.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("patient_bo", getPatientListBOArrayList[position])
            (context as DashBoardActivity).navController.navigate(R.id.vitalsFragment, bundle)
        }

        holder.tvFileUpload.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("patient_bo", getPatientListBOArrayList[position])
            bundle.putString("date", stDate)
            (context as DashBoardActivity).navController.navigate(R.id.imageUploadDashboardFragment,
                bundle)
        }

        holder.tvPrescription.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("patient_bo", getPatientListBOArrayList[position])
            (context as DashBoardActivity).navController.navigate(R.id.prescriptionFragment, bundle)
        }

        holder.tvReports.setOnClickListener {
            if(getPatientListBOArrayList[position].balance == "0" ){
                val bundle = Bundle()
                bundle.putParcelable("patient_bo", getPatientListBOArrayList[position])
                (context as DashBoardActivity).navController.navigate(R.id.reportsFragment, bundle)
            }else if(getPatientListBOArrayList[position].balance.isEmpty()){
                CommonMethods.showDialogForError(context,AllKeys.YOUR_BILL_IS_NOT_PREPARED)
            }else if(CommonMethods.getPrefrence(context,AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)){
                CommonMethods.showDialogForError(context,"Please pay from recharge tab.")
            } else{
                if (CommonMethods.getPrefrence(context, AllKeys.MERCHANT_ID_RAZORPAY).equals(AllKeys.DNF)
                    || CommonMethods.getPrefrence(context, AllKeys.MERCHANT_ID_RAZORPAY).isNullOrEmpty()
                   // ||  !CommonMethods.getPrefrence(context, AllKeys.MERCHANT_ID_RAZORPAY)!!.contains("rzp")
                ) {
                    CommonMethods.showDialogForError(context, AllKeys.PAYMENT_SERVICE_NOT_AVAILABLE)
                } else {
                    openPaymentGateway.openPaymentGateway(getPatientListBOArrayList[position])
                }
            }
        }

        holder.tvViewReportsDetails.setOnClickListener {
            if(getPatientListBOArrayList[position].balance == "0") {
                val bundle = Bundle()
                val reportsBO = ReportsBO(
                    "",
                    "",
                    AllKeys.COMPANY_ID,
                    getPatientListBOArrayList[position].regno,
                    "",
                    getPatientListBOArrayList[position].PePatID,
                    "",
                    "",
                    "",
                    getPatientListBOArrayList[position].PatientName,
                    getPatientListBOArrayList[position].PatientName,
                    getPatientListBOArrayList[position].sex,
                    getPatientListBOArrayList[position].MDY,
                    "",
                    "",
                    getPatientListBOArrayList[position].Pno,
                    getPatientListBOArrayList[position].Dr_name,
                    getPatientListBOArrayList[position].PePatID,
                    getPatientListBOArrayList[position].PatientPhoneNo,
                    getPatientListBOArrayList[position].balance,
                    getPatientListBOArrayList[position].Entrydate
                )
                bundle.putParcelable("report_bo", reportsBO)
                (context as DashBoardActivity).navController.navigate(R.id.reportsDetailsFragment, bundle)
            }else if(getPatientListBOArrayList[position].balance.isEmpty()){
                CommonMethods.showDialogForError(context,AllKeys.YOUR_BILL_IS_NOT_PREPARED)
            }else{
                if(CommonMethods.getPrefrence(context,AllKeys.MERCHANT_ID_RAZORPAY).equals(AllKeys.DNF) || CommonMethods.getPrefrence(context,AllKeys.MERCHANT_ID_RAZORPAY).isNullOrEmpty() || getPatientListBOArrayList[position].equals("0")){
                    CommonMethods.showDialogForError(context,AllKeys.PAYMENT_SERVICE_NOT_AVAILABLE)
                }else{
                    openPaymentGateway.openPaymentGateway(getPatientListBOArrayList[position])
                }
            }
        }

        holder.tvVaccinationBook.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("patient_bo", getPatientListBOArrayList[position])
            (context as DashBoardActivity).navController.navigate(R.id.vaccinationFragment, bundle)
        }

        holder.tvBookAppointment.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("patient_bo", getPatientListBOArrayList[position])
            bundle.putString("app_type", "new")
            (context as DashBoardActivity).navController.navigate(R.id.checkSlotFragment2, bundle)
        }

        holder.tvSubmitConsent.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("patient_bo", getPatientListBOArrayList[position])
            (context as DashBoardActivity).navController.navigate(R.id.consentFragment, bundle)
        }

        holder.tvViewConsent.setOnClickListener{
            val bundle = Bundle()
            bundle.putParcelable("patient_bo", getPatientListBOArrayList[position])
            (context as DashBoardActivity).navController.navigate(R.id.viewConsentFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return getPatientListBOArrayList.size
    }

    class GetPatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPatientName: TextView = itemView.findViewById(R.id.tv_patient_name)
        var tvAge: TextView = itemView.findViewById(R.id.tv_age_value)
        var tvSex: TextView = itemView.findViewById(R.id.tv_sex)
        var tvPatientMobileValue: TextView = itemView.findViewById(R.id.tv_patient_mobile_value)
        var tvPatientMobile: TextView = itemView.findViewById(R.id.tv_patient_mobile)
        var tvPrescription: TextView = itemView.findViewById(R.id.tv_prescription)
        var tvAppointmentStatus: TextView = itemView.findViewById(R.id.tv_status)
        var tvPatientStatus: TextView = itemView.findViewById(R.id.tv_patient_status)
        var tvPatientStatusValue: TextView = itemView.findViewById(R.id.tv_patient_status_value)
        var tvDOBValue: TextView = itemView.findViewById(R.id.tv_patient_dob_value)
        var tvDOB: TextView = itemView.findViewById(R.id.tv_patient_dob)
        var tvPatientBalance: TextView = itemView.findViewById(R.id.tv_patient_balance)
        var tvPatientBalanceValue: TextView = itemView.findViewById(R.id.tv_patient_balance_value)
        var tvPatientRegNo: TextView = itemView.findViewById(R.id.tv_patient_reg_no)
        var tvPatientRegDate: TextView = itemView.findViewById(R.id.tv_patient_reg_date)

        var tvVitals: TextView = itemView.findViewById(R.id.tv_vitals)
        var tvFileUpload: TextView = itemView.findViewById(R.id.tv_image_upload)
        var tvVideoCall: TextView = itemView.findViewById(R.id.tv_video_call)
        var tvReports: TextView = itemView.findViewById(R.id.tv_reports)
        var tvBookAppointment: TextView = itemView.findViewById(R.id.tv_book_appointment)
        var tvVaccinationBook: TextView = itemView.findViewById(R.id.tv_vaccination_book)
        var tvViewReportsDetails: TextView = itemView.findViewById(R.id.tv_view_reports_details)
        var tvSubmitConsent:TextView = itemView.findViewById(R.id.tv_submit_consent)
        var tvViewConsent:TextView= itemView.findViewById(R.id.tv_view_consent)
    }

    fun updateData(context: Context?, data: ArrayList<GetPatientListBO>) {
        this.context = context!!
        this.getPatientListBOArrayList = data
        notifyDataSetChanged()
    }



    interface OpenPaymentGateway{
        fun openPaymentGateway(getPatientListBO: GetPatientListBO)
    }
}