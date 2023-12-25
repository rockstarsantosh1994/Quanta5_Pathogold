package com.bms.pathogold_bms.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsBO
import com.bms.pathogold_bms.widget.CircleImageView2
import com.bumptech.glide.Glide
import java.net.URLEncoder


class GetLabDetailsAdapter(
    private var context: Context,
    private var getLabDetailsList: ArrayList<GetLabDetailsBO>,
    private var getLabDetailAdapterOperation: GetLabDetailAdapterOperation,
) : RecyclerView.Adapter<GetLabDetailsAdapter.GetLabDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetLabDetailsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder=layoutInflater.inflate(R.layout.row_get_lab_details,parent,false)
        return GetLabDetailsViewHolder(viewHolder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GetLabDetailsViewHolder, position: Int) {
        Glide.with(context).load(getLabDetailsList[position].logo_path)
            //.placeholder(R.drawable.background)
            .into(holder.ivLabLogo)

        holder.tvLabName.text=getLabDetailsList[position].labname
        holder.tvMobileNumber.text=getLabDetailsList[position].labphone
        holder.tvEmail.text=getLabDetailsList[position].labemail
        holder.tvAddress.text = getLabDetailsList[position].labaddress+"\n"+
                getLabDetailsList[position].city+" ,"+getLabDetailsList[position].state+" ,"+getLabDetailsList[position].country+".\n" +
                "PinCode:- "+getLabDetailsList[position].labpincode

        holder.tvContactLab.setOnClickListener {
            getLabDetailAdapterOperation.contactLabOperation(getLabDetailsList[position])
        }

        holder.tvServiceDetails.setOnClickListener {
            getLabDetailAdapterOperation.serviceDetailOperation(getLabDetailsList[position])
        }

        holder.tvBookAppointment.setOnClickListener {
            getLabDetailAdapterOperation.bookAppointmentOperation(getLabDetailsList[position])
        }

        holder.ivWhatsApp.setOnClickListener {
            openWhatsapp(getLabDetailsList[position])
        }

        holder.ivMail.setOnClickListener {
            openMail(getLabDetailsList[position])
        }
    }

    private fun openMail(getLabDetailsBO: GetLabDetailsBO) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + getLabDetailsBO.labemail))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry")
            intent.putExtra(Intent.EXTRA_TEXT, "your_text")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            //TODO smth
            Log.e("TAG", "openMail: "+e )
        }
    }

    private fun openWhatsapp(getLabDetailsBO: GetLabDetailsBO) {
        val packageManager = context.packageManager
        val i = Intent(Intent.ACTION_VIEW)

        try {
            val url =
                "https://api.whatsapp.com/send?phone=" + getLabDetailsBO.labphone + "&text=" + URLEncoder.encode(
                    "",
                    "UTF-8")
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return getLabDetailsList.size
    }

    class GetLabDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivLabLogo: CircleImageView2 = itemView.findViewById(R.id.iv_lab_logo)
        val tvLabName: TextView = itemView.findViewById(R.id.tv_lab_name)
        val tvMobileNumber: TextView = itemView.findViewById(R.id.tv_mobile_number)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)

        val tvContactLab: TextView = itemView.findViewById(R.id.tv_contact_lab)
        val tvServiceDetails: TextView = itemView.findViewById(R.id.tv_service_detail)
        val tvBookAppointment: TextView = itemView.findViewById(R.id.tv_book_appointment)

        val ivWhatsApp: ImageView = itemView.findViewById(R.id.iv_whatsapp)
        val ivMail: ImageView = itemView.findViewById(R.id.iv_mail)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(context: Context?, data: ArrayList<GetLabDetailsBO>) {
        this.context = context!!
        this.getLabDetailsList= data
        notifyDataSetChanged()
    }

    interface GetLabDetailAdapterOperation{
        fun contactLabOperation(getLabDetailsBO: GetLabDetailsBO)
        fun serviceDetailOperation(getLabDetailsBO: GetLabDetailsBO)
        fun bookAppointmentOperation(getLabDetailsBO: GetLabDetailsBO)
    }
}