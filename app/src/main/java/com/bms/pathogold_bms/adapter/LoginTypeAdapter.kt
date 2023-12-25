package com.bms.pathogold_bms.adapter

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.*
import com.bms.pathogold_bms.model.DashBoardBO
import com.bms.pathogold_bms.utility.AllKeys

class LoginTypeAdapter(private val context:Context,
                       private val dashboardBOArrayList:ArrayList<DashBoardBO>,
                       private val loginTypeClicks: LoginTypeClicks
) : RecyclerView.Adapter<LoginTypeAdapter.LoginTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginTypeViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_login_type,parent,false)
        return LoginTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoginTypeViewHolder, position: Int) {
        holder.ivLoginTypeImage.setImageResource(dashboardBOArrayList[position].image)
        holder.tvLoginTypeName.setText(dashboardBOArrayList[position].name)

        holder.cardView.setOnClickListener{
            when(dashboardBOArrayList[position].name){
                R.string.laboratory , R.string.admin  ->{
                    val intent=Intent(context,LogInWithOtpActivty::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("login_type",AllKeys.Administrator)
                    context.startActivity(intent)
                }

                R.string.collection_center->{
                    val intent=Intent(context,LogInWithOtpActivty::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("login_type",AllKeys.CollectionCenter)
                    context.startActivity(intent)
                }

                R.string.book_app->{
                    loginTypeClicks.onClickBookAppointment()
                }

                R.string.consultant_doctor->{
                    val intent=Intent(context,LogInWithOtpActivty::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("login_type",AllKeys.Consultant)
                    context.startActivity(intent)
                }

                R.string.reference_doctor->{
                    val intent=Intent(context,LogInWithOtpActivty::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("login_type",AllKeys.Reference_Doctor)
                    context.startActivity(intent)
                }

                R.string.patient-> {
                    val intent = Intent(context, LogInWithOtpActivty::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("login_type", AllKeys.Patient)
                    context.startActivity(intent)
                }

                R.string.phlebotomist->{
                    val intent= Intent(context, LogInWithOtpActivty::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("login_type", AllKeys.Phlebotomist)
                    context.startActivity(intent)
                }

                R.string.super_admin->{
                    val intent=Intent(context,LogInWithOtpActivty::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("login_type",AllKeys.SuperAdmin)
                    context.startActivity(intent)
                }

                R.string.payu->{
                    val intent=Intent(context,PayuCheckOutActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }

                R.string.support->{
                    val intent=Intent(context,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("login_type",AllKeys.Support)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dashboardBOArrayList.size
    }

    class LoginTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cardView: CardView = itemView.findViewById(R.id.cardview)
        val ivLoginTypeImage:ImageView= itemView.findViewById(R.id.iv_login_type)
        val tvLoginTypeName:TextView = itemView.findViewById(R.id.tv_login_type_name)

    }

    interface LoginTypeClicks{
        fun onClickBookAppointment()
    }
}