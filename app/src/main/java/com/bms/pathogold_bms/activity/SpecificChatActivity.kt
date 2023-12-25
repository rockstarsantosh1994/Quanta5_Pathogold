package com.bms.pathogold_bms.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

import android.widget.ImageButton

import android.widget.TextView
import androidx.cardview.widget.CardView
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.NestedScrollView
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.MessagesAdapter
import com.bms.pathogold_bms.model.chat.FireBaseBO
import com.bms.pathogold_bms.model.chat.Messages
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.ConfigUrl
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SpecificChatActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "SpecificChatActivity"
    var mgetmessage: EditText? = null
    var msendmessagebutton: ImageButton? = null

    var msendmessagecardview: CardView? = null
    var mtoolbarofspecificchat: Toolbar? = null
    var mimageviewofspecificuser: ImageView? = null
    var mnameofspecificuser: TextView? = null

    var nestedScrollView: NestedScrollView? = null

    //VideoCall Feature...
    var ivVideoCall: ImageView? = null
    var ivUploadFileOfPatient: ImageView? = null

    private var enteredmessage: String? = null
    var mrecievername: String? = null
    var sendername: kotlin.String? = null
    var mrecieveruid: kotlin.String? = null
    var msenderuid: kotlin.String? = null

    //private var firebaseAuth: FirebaseAuth? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var senderroom: String? = null
    var recieverroom: String? = null
    var notificationToken: String? = null

    var mbackbuttonofspecificchat: ImageButton? = null

    var mmessagerecyclerview: RecyclerView? = null

    var currenttime: String? = null
    var calendar: Calendar? = null
    var simpleDateFormat: SimpleDateFormat? = null

    var messagesAdapter: MessagesAdapter? = null
    var messagesArrayList: ArrayList<Messages>? = null

    private var firebaseBO: FireBaseBO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialisation....
        initViews()

        val databaseReference = firebaseDatabase!!.reference.child("chats").child(senderroom!!).child("messages")
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val messages = dataSnapshot.getValue(Messages::class.java)
                if (messages != null) {
                    messagesArrayList?.add(messages)
                }
                messagesAdapter = MessagesAdapter(mContext, messagesArrayList,msenderuid)
                mmessagerecyclerview?.adapter = messagesAdapter
                //messagesAdapter?.notifyDataSetChanged()

                mmessagerecyclerview?.smoothScrollToPosition(mmessagerecyclerview?.adapter!!.itemCount)
                nestedScrollView?.post {
                    //nestedScrollView.scrollTo(0,0);
                    nestedScrollView?.fullScroll(View.FOCUS_DOWN)
                    //rvComments.scrollToPosition(commentsAdapter.getItemCount()-1);
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override val activityLayout: Int
        get() = R.layout.activity_specific_chat

    @SuppressLint("SimpleDateFormat")
    private fun initViews() {
        mgetmessage = findViewById(R.id.getmessage)
        msendmessagecardview = findViewById(R.id.carviewofsendmessage)
        msendmessagebutton = findViewById(R.id.imageviewsendmessage)
        mtoolbarofspecificchat = findViewById(R.id.toolbarofspecificchat)
        mnameofspecificuser = findViewById(R.id.Nameofspecificuser)
        mimageviewofspecificuser = findViewById(R.id.specificuserimageinimageview)
        mbackbuttonofspecificchat = findViewById(R.id.backbuttonofspecificchat)

        //Nested ScrollViewDeclaration....
        nestedScrollView = findViewById(R.id.nestedScrollView)

        //ImageView declaration...
        ivVideoCall = findViewById(R.id.iv_video_call)
        ivUploadFileOfPatient = findViewById(R.id.iv_upload_view_file_of_patient)
        ivVideoCall?.setOnClickListener(this)
        ivUploadFileOfPatient?.setOnClickListener(this)

        messagesArrayList = ArrayList()
        mmessagerecyclerview = findViewById(R.id.recyclerviewofspecific)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        mmessagerecyclerview?.layoutManager = linearLayoutManager
        mmessagerecyclerview?.isNestedScrollingEnabled = false

        nestedScrollView?.post {
            //nestedScrollView.scrollTo(0,0);
            nestedScrollView?.fullScroll(View.FOCUS_DOWN)
            //rvComments.scrollToPosition(commentsAdapter.getItemCount()-1);
        }

        //hideKeyboard(mContext as Activity)
        //messagesAdapter = MessagesAdapter(mContext, messagesArrayList)
        // mmessagerecyclerview?.adapter = messagesAdapter

        setSupportActionBar(mtoolbarofspecificchat)
        mtoolbarofspecificchat?.setOnClickListener {
            //Todo operation..
            Log.e(TAG, "initViews: Toolbar is pressed")
        }

        //firebaseAuth=FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("hh:mm a")
        msenderuid =
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
                CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID).toString()
            } else {
                CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()
            }

        //msenderuid=CommonMethods.getPrefrence(mContext!!,AllKeys.GCM_TOKEN)
        mrecieveruid = intent.getStringExtra("receiveruid")
        mrecievername = intent.getStringExtra("name")
        notificationToken = intent.getStringExtra("token")
        firebaseBO = intent.getParcelableExtra("firebase_bo")

        senderroom = msenderuid + mrecieveruid
        recieverroom = mrecieveruid + msenderuid

        mbackbuttonofspecificchat?.setOnClickListener { finish() }

        mnameofspecificuser?.text = mrecievername
        val uri = intent.getStringExtra("imageuri")
        if (uri!!.isEmpty()) {
            Log.e(TAG, "initViews: No image found")
        } else {
            Picasso.get().load(uri).into(mimageviewofspecificuser)
        }

        //send message onClickListener...
        msendmessagebutton?.setOnClickListener {
            enteredmessage = mgetmessage?.text.toString()
            if (enteredmessage?.isEmpty() == true) {
                Toast.makeText(applicationContext, "Enter message first", Toast.LENGTH_SHORT).show()
            } else {
                try{
                    if(messagesArrayList!!.size>0){
                        if(messagesArrayList!![messagesArrayList!!.size-1].message.equals("end",ignoreCase = true)
                            && CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN) != messagesArrayList!![messagesArrayList!!.size-1].senderId){

                            CommonMethods.showDialogForError(mContext!!,"Chat is ended.")
                        }else{
                            sendMessage()
                        }
                    } else{
                        sendMessage()
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun sendMessage() {
        val date = Date()
        currenttime = simpleDateFormat!!.format(calendar?.time)
        val messages = Messages(enteredmessage, CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN), date.time, currenttime)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase!!.reference.child("chats")
            .child(senderroom!!)
            .child("messages")
            .push().setValue(messages).addOnCompleteListener {
                firebaseDatabase!!.reference
                    .child("chats")
                    .child(recieverroom!!)
                    .child("messages")
                    .push()
                    .setValue(messages).addOnCompleteListener { }
            }

        sendNotificationForChat(mContext!!, mgetmessage?.text.toString())

        hideKeyboard(mContext!! as Activity)

        mgetmessage?.text = null

        nestedScrollView?.post {
            //nestedScrollView.scrollTo(0,0);
            nestedScrollView?.fullScroll(View.FOCUS_DOWN)
            //rvComments.scrollToPosition(commentsAdapter.getItemCount()-1);
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_video_call -> {
                /* val mDialog = MaterialDialog.Builder(this@SpecificChatActivity)
                     // .setTitle("Start Job?")
                     .setMessage("Start video call?")
                     .setCancelable(false)
                     .setPositiveButton("CALL") { dialogInterface: DialogInterface, which: Int ->
                         //startService..
                         sendNotificationForVideoCall(mContext!!)
                         dialogInterface.dismiss()
                     }
                     .setNegativeButton("CANCEL") { dialogInterface: DialogInterface, which: Int ->
                         // Delete Operation
                         dialogInterface.dismiss()
                     }
                     .build()

                 // Show Dialog
                 mDialog.show()*/
                Toast.makeText(mContext!!, "Not Available", Toast.LENGTH_SHORT).show()
            }

            R.id.iv_upload_view_file_of_patient -> {
               /* if (firebaseBO?.pePatID == null || firebaseBO?.pePatID.isNullOrEmpty()) {
                    val intent = Intent(mContext, GetPatientListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } else {
                    val getPatientListBO = GetPatientListBO(
                        firebaseBO?.regno.toString(),
                        firebaseBO?.patientName.toString(),
                        firebaseBO?.userName.toString(),
                        firebaseBO?.pno.toString(),
                        firebaseBO?.age.toString(),
                        firebaseBO?.mdy.toString(),
                        firebaseBO?.sex.toString(),
                        firebaseBO?.patientPhoneNo.toString(),
                        firebaseBO?.dr_name.toString(),
                        firebaseBO?.pePatID.toString(),
                        firebaseBO?.status.toString(),
                        firebaseBO?.samplestatus.toString(),
                        firebaseBO?.token.toString(),
                        "")

                    val intent = Intent(mContext, ImageUploadActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("patient_bo", getPatientListBO)
                    intent.putExtra("date", CommonMethods.getTodayDate("MM/dd/yyyy"))
                    startActivity(intent)
                }*/
            }
        }
    }

    private fun sendNotificationForChat(context: Context, message: String) {
        val notificationData = JSONObject()
        val notificationData1 = JSONObject()
        val notification = JSONObject()
        val firebaseBO1 =
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
                FireBaseBO(
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID),
                    "Offline",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME),
                    CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME),
                    "",
                    "",
                    "",
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.MOBILE_NO),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
                    CommonMethods.getPrefrence(mContext!!, AllKeys.Patient),
                )
            } else {
                FireBaseBO(
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME),
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID),
                    "Offline",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
                    "",
                )
            }
        try {
            //parameter sending for notification key...
            notificationData1.put("title",
                CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME))
            notificationData1.put("body", message)
            notificationData1.put("text", message)
            notificationData1.put("click_action", "CHAT")

            //parameter sending for data key....
            notificationData.put("title",
                CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME))
            notificationData.put("message", message)
            notificationData.put("body", message)
            notificationData.put("text", message)
            notificationData.put("extra_information", firebaseBO1)
            notificationData.put("click_action", "CHAT")
            //notificationData.put("sound", "default")

            notification.put("to", firebaseBO?.token)
            //notification.put("notification", notificationData1)
            notification.put("data", notificationData)
            Log.e(TAG, "sendNotificationForVideoCall: $notification")
            val jsonObjectRequest: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, ConfigUrl.FCM_API, notification,
                    Response.Listener { response: JSONObject ->
                        Log.e("mytag", "Success \n sendNotification: $response")

                    },
                    Response.ErrorListener { error: VolleyError ->
                        // Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
                        // Log.e("mytag", "error: " + error);
                        val response = error.networkResponse
                        if (error is ServerError && response != null) {
                            try {
                                val res = String(response.data,
                                    Charset.forName(HttpHeaderParser.parseCharset(response.headers,
                                        "utf-8")))
                                // Now you can use any deserializer to make sense of data
                                val obj = JSONObject(res)

                                CommonMethods.showDialogForError(context,
                                    "Unable to connect...Try after some time! ")
                            } catch (e1: UnsupportedEncodingException) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace()
                                // Log.e("mytag", "sendNotification erro: e1 " + e1);
                            } catch (e2: JSONException) {
                                // returned data is not JSONObject?
                                e2.printStackTrace()
                                //  Log.e("mytag", "sendNotification erro e2: " + e2);
                            }
                        }
                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Authorization"] = ConfigUrl.AUTHORIZATION
                        params["Content-Type"] = "application/json"
                        return params
                    }
                }
            Volley.newRequestQueue(Objects.requireNonNull(context)).add(jsonObjectRequest)
            //mQueue.add(jsonObjectRequest);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendNotificationForVideoCall(context: Context) {
        val title =
            CommonMethods.getPrefrence(context, AllKeys.PERSON_NAME) + " is Video Calling you...."
        //val link = "https://vc.bestbrain.in/ng/$mrecieveruid"
        val link = ConfigUrl.VIDEO_CALL_BASE_URL+mrecieveruid
        var message = "Hi.. " + CommonMethods.getPrefrence(context,
            AllKeys.PERSON_NAME) + " is requesting you a video call...please receive it"
        val notificationData = JSONObject()
        val notificationData1 = JSONObject()
        val notification = JSONObject()
        try {
            //parameter sending for notification key...
            notificationData1.put("title", title)
            notificationData1.put("body", link)
            notificationData1.put("text", title)
            notificationData1.put("click_action", "VIDEOCALL")

            //parameter sending for data key....
            notificationData.put("title", title)
            notificationData.put("message", link)
            notificationData.put("body", link)
            notificationData.put("text", link)
            notificationData.put("extra_information", link)
            notificationData.put("click_action", "VIDEOCALL")

            notification.put("to", notificationToken)
            //notification.put("notification", notificationData1)
            notification.put("data", notificationData)

            Log.e(TAG, "sendNotificationForVideoCall: $notification")
            val jsonObjectRequest: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, ConfigUrl.FCM_API, notification,
                    Response.Listener { response: JSONObject ->
                        Log.e("mytag", "Success \n sendNotification: $response")

                        if (response.getString("success").equals("1")) {
                            val intent = Intent(context, VideoConferenceActivity2::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            intent.putExtra("link", link)
                            context.startActivity(intent)
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        //Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
                        // Log.e("mytag", "error: " + error);
                        val response = error.networkResponse
                        if (error is ServerError && response != null) {
                            try {
                                val res = String(response.data,
                                    Charset.forName(HttpHeaderParser.parseCharset(response.headers,
                                        "utf-8")))
                                // Now you can use any deserializer to make sense of data
                                val obj = JSONObject(res)

                                CommonMethods.showDialogForError(context,
                                    "Unable to connect...Try after some time! ")
                            } catch (e1: UnsupportedEncodingException) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace()
                                // Log.e("mytag", "sendNotification erro: e1 " + e1);
                            } catch (e2: JSONException) {
                                // returned data is not JSONObject?
                                e2.printStackTrace()
                                //  Log.e("mytag", "sendNotification erro e2: " + e2);
                            }
                        }
                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Authorization"] = ConfigUrl.AUTHORIZATION
                        params["Content-Type"] = "application/json"
                        return params
                    }
                }
            Volley.newRequestQueue(Objects.requireNonNull(context)).add(jsonObjectRequest)
            //mQueue.add(jsonObjectRequest);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        /*val databaseReference = firebaseDatabase!!.reference.child("chats").child(senderroom!!).child("messages")
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val messages = dataSnapshot.getValue(Messages::class.java)

                if (messages != null) {
                    messagesArrayList?.add(messages)
                }
                messagesAdapter = MessagesAdapter(mContext, messagesArrayList)
                mmessagerecyclerview?.adapter = messagesAdapter
                //messagesAdapter?.notifyDataSetChanged()

                mmessagerecyclerview?.smoothScrollToPosition(mmessagerecyclerview?.adapter!!.itemCount)
                nestedScrollView?.post {
                    //nestedScrollView.scrollTo(0,0);
                    nestedScrollView?.fullScroll(View.FOCUS_DOWN)
                    //rvComments.scrollToPosition(commentsAdapter.getItemCount()-1);
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })*/
    }
}