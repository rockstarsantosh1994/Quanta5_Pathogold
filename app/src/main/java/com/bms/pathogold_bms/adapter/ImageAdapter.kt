package com.bms.pathogold_bms.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.PhotoViewActivity
import com.bms.pathogold_bms.model.ImageUploadBO
import java.io.ByteArrayOutputStream

class ImageAdapter(val context: Context, private val imageUploadBOArrayList: ArrayList<ImageUploadBO>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
       val layoutInflater=LayoutInflater.from(parent.context)
       val view:View=layoutInflater.inflate(R.layout.row_image,parent,false)
       return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        if(imageUploadBOArrayList[position].getBitmap()!=null){
            holder.ivView.setImageBitmap(imageUploadBOArrayList[position].getBitmap())
        }else{
            holder.ivView.setImageResource(R.drawable.ic_song)
        }

        holder.ivView.setOnClickListener{
            val intent = Intent(context, PhotoViewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            val bStream  =  ByteArrayOutputStream()
            imageUploadBOArrayList[position].getBitmap()?.compress(Bitmap.CompressFormat.PNG, 45, bStream)
            val byteArray = bStream.toByteArray()
            intent.putExtra("img_src", byteArray)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return imageUploadBOArrayList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivView: ImageView =itemView.findViewById(R.id.iv_view)
    }
}