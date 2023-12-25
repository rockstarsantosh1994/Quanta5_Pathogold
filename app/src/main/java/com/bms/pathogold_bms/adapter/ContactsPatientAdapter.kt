package com.bms.pathogold_bms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import de.hdodenhof.circleimageview.CircleImageView

class ContactsPatientAdapter(
    private var context: Context,
    private var getPatientListBOArrayList: ArrayList<GetPatientListBO>,
    val getPatientContactDetails: GetPatientContactDetails
): RecyclerView.Adapter<ContactsPatientAdapter.ContactsViewHolder>() {

    private val TAG = "ContactsAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.row_contacts,parent,false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
            holder.tvDisplayName.text = getPatientListBOArrayList[position].PatientName

            holder.itemView.setOnClickListener {
                getPatientContactDetails.getPatientContactDetails(getPatientListBOArrayList[position])

              /*  val intent = Intent(context, SpecificChatActivity::class.java)
                intent.putExtra("name", getPatientListBOArrayList[position].PatientName)
                intent.putExtra("receiveruid", getPatientListBOArrayList[position].PePatID)
                intent.putExtra("imageuri", "")
                intent.putExtra("token",getPatientListBOArrayList[position].Token)
                context.startActivity(intent)*/
            }
    }

    override fun getItemCount(): Int {
        return getPatientListBOArrayList.size
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //TextView declaration..
        var tvDisplayName:TextView=itemView.findViewById(R.id.tv_name_of_user)
        var tvStatusOfUser:TextView=itemView.findViewById(R.id.tv_statusofuser)

        //ImageView declaration..
        var ivImageOfUser:CircleImageView=itemView.findViewById(R.id.iv_imageviewofuser)
    }
    
    fun updateDataForPatient(context: Context?, data: ArrayList<GetPatientListBO>) {
        this.context = context!!
        this.getPatientListBOArrayList = data
        notifyDataSetChanged()
    }

    interface GetPatientContactDetails{
        fun getPatientContactDetails(getPatientListBO: GetPatientListBO)
    }
}