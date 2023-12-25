package com.bms.pathogold_bms.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import android.app.AlertDialog
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.SpecificChatActivity
import com.bms.pathogold_bms.model.chat.FireBaseBO
import io.paperdb.Paper

class RecentChatAdapter(
    var context: Context,
    var chatFireBaseBOList: ArrayList<FireBaseBO>,
) : RecyclerView.Adapter<RecentChatAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view:View=layoutInflater.inflate(R.layout.row_contacts,parent,false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(noteViewHolder: NoteViewHolder, position: Int) {
        noteViewHolder.particularusername.text = chatFireBaseBOList[position].name
        val uri: String = chatFireBaseBOList[position].image
        // Picasso.get().load(uri).into(noteViewHolder.mimageviewofuser)
        if (chatFireBaseBOList[position].status == "Online") {
            noteViewHolder.statusofuser.text = chatFireBaseBOList[position].status
            noteViewHolder.statusofuser.setTextColor(Color.GREEN)
        } else {
            noteViewHolder.statusofuser.text =   chatFireBaseBOList[position].status
        }

        noteViewHolder.itemView.setOnClickListener {
            val intent = Intent(context, SpecificChatActivity::class.java)
            intent.putExtra("name", chatFireBaseBOList[position].name)
            intent.putExtra("receiveruid",  chatFireBaseBOList[position].uid)
            intent.putExtra("imageuri",  chatFireBaseBOList[position].image)
            intent.putExtra("token", chatFireBaseBOList[position].token)
            intent.putExtra("firebase_bo",chatFireBaseBOList[position])
            context.startActivity(intent)
        }

        noteViewHolder.itemView.setOnLongClickListener(View.OnLongClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Are you sure want to delete chat?")
            builder.setPositiveButton("Yes") { dialog, which -> // send data from the
                // AlertDialog to the Activity
                chatFireBaseBOList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, chatFireBaseBOList.size)
                Paper.book().write("chat_db", chatFireBaseBOList)
            }
            builder.setNegativeButton("No") { dialog, which -> // send data from the
                // AlertDialog to the Activity
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
            true
        })
    }

    override fun getItemCount(): Int {
        return chatFireBaseBOList.size
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val particularusername: TextView = itemView.findViewById(R.id.tv_name_of_user)
        val statusofuser: TextView = itemView.findViewById(R.id.tv_statusofuser)
        val mimageviewofuser: CircleImageView = itemView.findViewById(R.id.iv_imageviewofuser)
    }

    fun updateData(context: Context?, data: ArrayList<FireBaseBO>) {
        this.context = context!!
        this.chatFireBaseBOList = data
        notifyDataSetChanged()
    }
}