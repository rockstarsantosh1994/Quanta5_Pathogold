package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getconsultation.ConsultationBO
import de.hdodenhof.circleimageview.CircleImageView

class ContactsAdapter(
    private var context: Context,
    private var consultationBOList: ArrayList<ConsultationBO>,
    val getContactDetails:GetContactDetails
) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    private val TAG = "ContactsAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.row_contacts, parent, false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.tvDisplayName.text = consultationBOList[position].Name

        holder.itemView.setOnClickListener {
            getContactDetails.getContactDetails(consultationBOList[position])
        }
    }

    override fun getItemCount(): Int {
        return consultationBOList.size
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //TextView declaration..
        var tvDisplayName: TextView = itemView.findViewById(R.id.tv_name_of_user)
        var tvStatusOfUser: TextView = itemView.findViewById(R.id.tv_statusofuser)

        //ImageView declaration..
        var ivImageOfUser: CircleImageView = itemView.findViewById(R.id.iv_imageviewofuser)
    }

    fun updateData(context: Context?, data: ArrayList<ConsultationBO>) {
        this.context = context!!
        this.consultationBOList = data
        notifyDataSetChanged()
    }

    interface GetContactDetails{
        fun getContactDetails(consultationBO: ConsultationBO)
    }
}