package com.bms.pathogold_bms.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.PreViewActivity
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.getclientuploadedfile.GetClientUploadedBO
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.services.DigiPath
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bumptech.glide.Glide
import java.io.IOException


class GetClientImageDetailsAdapter2(
    private val context: Context,
    getClientUploadedBOArrayList: java.util.ArrayList<GetClientUploadedBO>,
    private val getPatientListBO: GetPatientListBO,
    private val digiPath: DigiPath,
) : RecyclerView.Adapter<GetClientImageDetailsAdapter2.GetClientImageDetailsViewHolder>() {

    private var getClientUploadedBOArrayList = ArrayList<GetClientUploadedBO>()
    private var mPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var last_index = -1

    private val TAG = "GetClientImageDetailsAd"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GetClientImageDetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.row_get_client_image_details, parent, false)
        return GetClientImageDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GetClientImageDetailsViewHolder, position: Int) {
        if (getClientUploadedBOArrayList[position].type.equals("Sound", ignoreCase = true)) {
            holder.rlSound.visibility = View.VISIBLE
            holder.llImage.visibility = View.GONE
            setUpData(holder, position)
        } else {
            holder.rlSound.visibility = View.GONE
            holder.llImage.visibility = View.VISIBLE
            Glide.with(context).load(getClientUploadedBOArrayList[position].url)
                .into(holder.ivImage)
        }
        holder.ivImage.setOnClickListener { view: View? ->
            val intent = Intent(context, PreViewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("img_src", getClientUploadedBOArrayList[position].url)
            context.startActivity(intent)
        }

        holder.tvRemoveImage.setOnClickListener { view: View? ->
            showAlertBox(getClientUploadedBOArrayList[position],position)
        }

        holder.tvRemoveSound.setOnClickListener { view: View? ->
            showAlertBox(getClientUploadedBOArrayList[position],position)
        }
    }

    private fun showAlertBox(getClientUploadedBO: GetClientUploadedBO, position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setMessage("Do you want to Delete?")
        builder.setPositiveButton("Yes") { dialog, which -> // send data from the
            //remove file from list
            removeFile(getClientUploadedBO,position)
        }
        builder.setNegativeButton("No") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            dialog.cancel()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun removeFile(getClientUploadedBO: GetClientUploadedBO, position: Int) {
        val parts: Array<String> = getClientUploadedBO.url.split("/").toTypedArray()
        val imageString = parts[parts.size-1]

        val params: MutableMap<String, String> = java.util.HashMap()
        params["labcode"] =CommonMethods.getPrefrence(context, AllKeys.LABNAME).toString()
        params["regno"] = getPatientListBO.regno
        params["image_name"] = imageString

        Log.e(TAG, "insertUpdatedAdvance: $params")

        val progress = ProgressDialog(context)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath.getApiRequestHelper()?.deleteImage(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "insertVaccinationPatient: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        //showDialogForSuccess(mContext!!, "Payment Successful. \n$transactionID")
                        Toast.makeText(context, commonResponse.Message, Toast.LENGTH_LONG).show()
                        getClientUploadedBOArrayList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, getClientUploadedBOArrayList.size)
                    } else {
                        CommonMethods.showDialogForError(context, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "insertVaccinationPatient: $apiResponse")
                }
            })
    }

    private fun setUpData(holder: GetClientImageDetailsViewHolder, position: Int) {
        val recording = getClientUploadedBOArrayList[position]
        holder.textViewName.text = recording.note
        if (recording.isPlaying) {
            holder.imageViewPlay.setImageResource(R.drawable.ic_pause)
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup)
            holder.seekBar.visibility = View.VISIBLE
            holder.seekUpdation(holder)
        } else {
            holder.imageViewPlay.setImageResource(R.drawable.ic_play)
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup)
            holder.seekBar.visibility = View.GONE
        }
        holder.manageSeekBar(holder)
    }

    override fun getItemCount(): Int {
        return getClientUploadedBOArrayList.size
    }

    inner class GetClientImageDetailsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView
        val llImage: LinearLayout
        val rlSound: RelativeLayout
        val textViewName: TextView
        val tvRemoveImage: TextView
        val tvRemoveSound: TextView
        var imageViewPlay: ImageView
        var seekBar: SeekBar
        private var recordingUri: String? = null
        private val mHandler = Handler()
        var holder: GetClientImageDetailsViewHolder? = null
        fun manageSeekBar(holder: GetClientImageDetailsViewHolder) {
            holder.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (mPlayer != null && fromUser) {
                        mPlayer!!.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }

        private fun markAllPaused() {
            for (i in getClientUploadedBOArrayList.indices) {
                getClientUploadedBOArrayList[i].isPlaying = false
                getClientUploadedBOArrayList[i] = getClientUploadedBOArrayList[i]
            }
            notifyDataSetChanged()
        }

        var runnable = Runnable { seekUpdation(holder) }
        fun seekUpdation(holder: GetClientImageDetailsViewHolder?) {
            this.holder = holder
            if (mPlayer != null) {
                val mCurrentPosition = mPlayer!!.currentPosition
                holder!!.seekBar.max = mPlayer!!.duration
                holder.seekBar.progress = mCurrentPosition
            }
            mHandler.postDelayed(runnable, 100)
        }

        private fun stopPlaying() {
            try {
                mPlayer!!.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mPlayer = null
            isPlaying = false
        }

        private fun startPlaying(audio: GetClientUploadedBO, position: Int) {
            mPlayer = MediaPlayer()
            try {
                mPlayer!!.setDataSource(recordingUri)
                mPlayer!!.prepare()
                mPlayer!!.start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }
            //showing the pause button
            seekBar.max = mPlayer!!.duration
            isPlaying = true
            mPlayer!!.setOnCompletionListener { mp: MediaPlayer? ->
                audio.isPlaying = false
                notifyItemChanged(position)
            }
        }

        init {
            ivImage = itemView.findViewById(R.id.iv_client_image)
            llImage = itemView.findViewById(R.id.ll_image)
            rlSound = itemView.findViewById(R.id.rl_sound)
            imageViewPlay = itemView.findViewById(R.id.imageViewPlay)
            seekBar = itemView.findViewById(R.id.seekBar)
            textViewName = itemView.findViewById(R.id.textViewRecordingname)
            tvRemoveImage = itemView.findViewById(R.id.tv_remove_image)
            tvRemoveSound = itemView.findViewById(R.id.tv_remove_sound)
            imageViewPlay.setOnClickListener { view: View? ->
                val position = adapterPosition
                val getClientUploadedBO =
                    getClientUploadedBOArrayList[position]
                recordingUri = getClientUploadedBO.url
                if (isPlaying) {
                    stopPlaying()
                    if (position == last_index) {
                        getClientUploadedBO.isPlaying = false
                        stopPlaying()
                        notifyItemChanged(position)
                    } else {
                        markAllPaused()
                        getClientUploadedBO.isPlaying = true
                        notifyItemChanged(position)
                        startPlaying(getClientUploadedBO, position)
                        last_index = position
                    }
                } else {
                    startPlaying(getClientUploadedBO, position)
                    getClientUploadedBO.isPlaying = true
                    seekBar.max = mPlayer!!.duration
                    Log.d("isPlayin", "False")
                    notifyItemChanged(position)
                    last_index = position
                }
            }
        }
    }

    init {
        this.getClientUploadedBOArrayList = getClientUploadedBOArrayList
    }
}
