package com.bms.pathogold_bms.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.services.DigiPath
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ApprovedAppAdapter(
    private var context: Context,
    private var viewAppointmentArrayList: ArrayList<ViewAppointmentBO>,
    private val stType: String,
    val digiPath: DigiPath,
) : RecyclerView.Adapter<ApprovedAppAdapter.ApprovedViewHolder>() {

    private val TAG = "ApprovedAppAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApprovedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.row_approved_appointment, parent, false)
        return ApprovedViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApprovedViewHolder, position: Int) {
        if(CommonMethods.getPrefrence(context,AllKeys.USERTYPE).equals(AllKeys.Patient)){
            holder.tvView.visibility=View.GONE
            holder.tvComplete.visibility=View.GONE
            holder.tvRegisterPatient.visibility=View.GONE
        }else{
            holder.tvView.visibility=View.VISIBLE
            holder.tvComplete.visibility=View.VISIBLE
            holder.tvRegisterPatient.visibility=View.VISIBLE
        }

        holder.tvName.text = viewAppointmentArrayList[position].PatientName
        holder.tvDate.text = viewAppointmentArrayList[position].Day
        holder.tvTime.text = viewAppointmentArrayList[position].Timeslot
        holder.tvMobileNumber.text = viewAppointmentArrayList[position].Mobileno

        holder.tvView.setOnClickListener {
            //Here stType is Approved, Pending
            val bundle= Bundle()
            bundle.putParcelable("view_appoint", viewAppointmentArrayList[position])
            bundle.putString("type", stType)
            (context as DashBoardActivity).navController.navigate(R.id.viewAppDetailedFragment,bundle)
        }

        holder.tvCancel.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Cancel Request")
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customLayout: View = inflater.inflate(R.layout.row_cancel_dialog, null)
            builder.setView(customLayout)
            val tilRemark = customLayout.findViewById<TextInputLayout>(R.id.til_remark)
            val etRemark = customLayout.findViewById<TextInputEditText>(R.id.et_remark)
            etRemark.setText(viewAppointmentArrayList[position].Remark)
            builder.setPositiveButton("Submit") { dialog, which -> // send data from the
                // AlertDialog to the Activity
                if (etRemark.text.toString().isEmpty()) {
                    tilRemark?.error = "Remark Required!"
                    tilRemark?.requestFocus()
                } else {
                    if (CommonMethods.isNetworkAvailable(context)) {
                        cancelAppointment(viewAppointmentArrayList[position].BookApp_Id)
                    } else {
                        CommonMethods.showDialogForError(context, AllKeys.NO_INTERNET_AVAILABLE)
                    }
                }
            }
            builder.setNegativeButton("Cancel") { dialog, which -> // send data from the
                // AlertDialog to the Activity
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        holder.tvComplete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Complete Request")
            builder.setPositiveButton("Submit") { dialog, which -> // send data from the
                // AlertDialog to the Activity
                if (CommonMethods.isNetworkAvailable(context)) {
                    completeAppointment(viewAppointmentArrayList[position].BookApp_Id)
                } else {
                    CommonMethods.showDialogForError(context, AllKeys.NO_INTERNET_AVAILABLE)
                }

            }
            builder.setNegativeButton("Cancel") { dialog, which -> // send data from the
                // AlertDialog to the Activity
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        holder.tvReschedule.setOnClickListener {
            (context as DashBoardActivity).toolbar?.title = context.resources.getString(R.string.view_slot)
            val bundle = Bundle()
            bundle.putString("app_type", "reschdeule")
            bundle.putParcelable("view_app_bo", viewAppointmentArrayList[position])
            (context as DashBoardActivity).navController.navigate(R.id.checkSlotFragment2, bundle)
        }

        holder.tvViewTest.setOnClickListener {
            (context as DashBoardActivity).toolbar?.title = context.resources.getString(R.string.view_test)
            val bundle = Bundle()
            bundle.putParcelable("view_app_bo", viewAppointmentArrayList[position])
            (context as DashBoardActivity).navController.navigate(R.id.viewTestFragment, bundle)
        }
        
        holder.tvRegisterPatient.setOnClickListener {
            val getPatientListBO= GetPatientListBO("",
                viewAppointmentArrayList[position].PatientName,
                "",
                "",
                viewAppointmentArrayList[position].Age,
                viewAppointmentArrayList[position].MDY,
                viewAppointmentArrayList[position].Gender,
                viewAppointmentArrayList[position].Mobileno,
                "",
                "",
                "",
                "",
                "",
                viewAppointmentArrayList[position].Status,
                "",
                viewAppointmentArrayList[position].Remark,
                "",
                "",
                "")
            (context as DashBoardActivity).toolbar?.title = context.resources.getString(R.string.patient_registration)
            val bundle = Bundle()
            bundle.putString("launch_type","view_appointment")
            bundle.putParcelable("appointment_details",viewAppointmentArrayList[position])
            bundle.putParcelable("patient_bo", getPatientListBO)
            (context as DashBoardActivity).navController.navigate(R.id.patientRegistrationFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return viewAppointmentArrayList.size
    }

    class ApprovedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvDate: TextView = itemView.findViewById(R.id.tv_date)
        var tvTime: TextView = itemView.findViewById(R.id.tv_time)
        var tvMobileNumber: TextView = itemView.findViewById(R.id.tv_mobilenumber)
        var tvView: TextView = itemView.findViewById(R.id.tv_view)
        var tvCancel: TextView = itemView.findViewById(R.id.tv_cancel)
        var tvReschedule: TextView = itemView.findViewById(R.id.tv_resschedule)
        var tvComplete: TextView = itemView.findViewById(R.id.tv_complete)
        var tvViewTest:TextView = itemView.findViewById(R.id.tv_view_test)
        var tvRegisterPatient:TextView = itemView.findViewById(R.id.tv_register_patient)
    }

    fun updateData(context: Context?, data: ArrayList<ViewAppointmentBO>) {
        this.context = context!!
        this.viewAppointmentArrayList = data
        notifyDataSetChanged()
    }

    //Cancel Appointment....
    private fun cancelAppointment(appointmentId: String) {
        val params: MutableMap<String, String> = HashMap()
        params["AppBookingId"] = appointmentId
        params["Companyid"] =  context.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        val progress = ProgressDialog(context)
        progress.setMessage("Cancelling appointment...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath.getApiRequestHelper()
            ?.patientAppointmentCancel(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        CommonMethods.showDialogForSuccess(context, commonResponse.Message)
                    } else {
                        CommonMethods.showDialogForError(context, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    //Complete Appointment....
    private fun completeAppointment(appointmentId: String) {
        val params: MutableMap<String, String> = HashMap()
        params["AppBookingId"] = appointmentId
        params["Companyid"] =context.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        val progress = ProgressDialog(context)
        progress.setMessage("Completing appointment...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath.getApiRequestHelper()
            ?.patientAppointmentComplete(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        CommonMethods.showDialogForSuccess(context, commonResponse.Message)
                    } else {
                        CommonMethods.showDialogForError(context, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }
}
