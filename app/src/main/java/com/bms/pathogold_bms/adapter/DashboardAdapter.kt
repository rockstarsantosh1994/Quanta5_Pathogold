package com.bms.pathogold_bms.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.*
import com.bms.pathogold_bms.fragment.*
import com.bms.pathogold_bms.model.DashBoardBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.material.card.MaterialCardView

class DashboardAdapter(var context: Context, var dashboardBoArrayList: ArrayList<DashBoardBO>) :
    RecyclerView.Adapter<DashboardAdapter.DashBoardViewHolder>() {

    private val TAG = "DashboardAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.row_dashboard, parent, false)
        return DashBoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {

        holder.tvDashBoardName.setText(dashboardBoArrayList[position].name)
        holder.ivDashBoardImage.setImageResource(dashboardBoArrayList[position].image)

        holder.cardView.setOnClickListener {
            when (position) {
                //My Profile...
                0 -> {
                    val intent = Intent(context, MyProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }

                //Book Appointment...
                1 -> {
                    val intent = Intent(context, CheckSlotFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("app_type", "new")
                    context.startActivity(intent)
                }

                //View Appointment...
                2 -> {
                    val intent = Intent(context, ViewAllAppointmentFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }

                //Vital and History...
                3 -> {
                    if (CommonMethods.getPrefrence(context, AllKeys.USERTYPE).equals(AllKeys.Lab_Technician)
                        || CommonMethods.getPrefrence(context, AllKeys.USERTYPE).equals(AllKeys.Administrator)
                    ) {
                        //don't have access...to this usertype
                        CommonMethods.showDialogForError(context,AllKeys.THIS_FEATURE_ONLY)
                    } else {
                        val intent = Intent(context, GetPatientListFragment::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                    }
                }

                //Image Upload...
                4 -> {
                    if (CommonMethods.getPrefrence(context, AllKeys.USERTYPE)
                            .equals(AllKeys.Patient)
                    ) {
                        //don't have access...
                        CommonMethods.showDialogForError(context,AllKeys.THIS_FEATURE_ONLY)
                    } else {
                        val intent = Intent(context, GetPatientListFragment::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                    }
                }

                //Video Conference...
                5 -> {
                    if (CommonMethods.getPrefrence(context, AllKeys.USERTYPE)
                            .equals(AllKeys.Lab_Technician)
                        || CommonMethods.getPrefrence(context, AllKeys.USERTYPE)
                            .equals(AllKeys.Administrator)
                    ) {
                        //don't have access...to this usertype
                        CommonMethods.showDialogForError(context,AllKeys.THIS_FEATURE_ONLY)
                    } else {
                        val intent = Intent(context, GetPatientListFragment::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                    }
                }

                //Patient Registration..
                6 -> {
                    if (CommonMethods.getPrefrence(context, AllKeys.USERTYPE).equals(AllKeys.Lab_Technician)
                        || CommonMethods.getPrefrence(context, AllKeys.USERTYPE).equals(AllKeys.Administrator)
                        || CommonMethods.getPrefrence(context, AllKeys.USERTYPE).equals(AllKeys.Patient)
                    ) {
                        //don't have access...to this usertype
                        CommonMethods.showDialogForError(context,AllKeys.THIS_FEATURE_ONLY)
                    } else {
                        val intent = Intent(context, PatientRegistrationFragment::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                    }
                }

                //Report Download...
                7 -> {
                    val intent = Intent(context, GetPatientListFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }

                //My Distance..
                8 -> {
                    if ((context.resources.getString(dashboardBoArrayList[position].name)) == (context.resources.getString(R.string.prescription))) {
                        //PrescriptionActivity Calling...
                        val intent = Intent(context, GetPatientListFragment::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                    }else if((context.resources.getString(dashboardBoArrayList[position].name)) == (context.resources.getString(R.string.view_treatment))){
                        //View treatment Activity Calling
                        val getPatientListBO=GetPatientListBO(
                            "",
                            CommonMethods.getPrefrence(context,AllKeys.PERSON_NAME).toString(),
                            CommonMethods.getPrefrence(context,AllKeys.USER_NAME).toString(),
                            "",
                            "",
                            "",
                            CommonMethods.getPrefrence(context,AllKeys.SEX).toString(),
                            CommonMethods.getPrefrence(context,AllKeys.MOBILE_NO).toString(),
                            "",
                            CommonMethods.getPrefrence(context,AllKeys.PATIENT_PROFILE_ID).toString(),
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "")

                        val intent = Intent(context, OldPrescFragment::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("patient_bo", getPatientListBO)
                        context.startActivity(intent)
                    } else {
                        //My Distance Activity Calling...
                        if (CommonMethods.getPrefrence(context, AllKeys.USERTYPE).equals(AllKeys.Phlebotomist)) {
                            val intent = Intent(context, MyDistanceFragment::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            context.startActivity(intent)
                        }else{
                            CommonMethods.showDialogForError(context,AllKeys.THIS_FEATURE_ONLY)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dashboardBoArrayList.size
    }

    class DashBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivDashBoardImage: ImageView = itemView.findViewById(R.id.iv_dashboard_img)
        var tvDashBoardName: TextView = itemView.findViewById(R.id.tv_dashboard_name)
        var cardView: MaterialCardView = itemView.findViewById(R.id.materialCardView2)

    }
}