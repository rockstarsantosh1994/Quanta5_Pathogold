package com.bms.pathogold_bms.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BaseFragment
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.adapter.RecentChatAdapter
import com.bms.pathogold_bms.model.ChatDocument
import com.bms.pathogold_bms.model.chat.FireBaseBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : BaseFragment() {
    
    private val TAG = "ChatFragment"

    //RecyclerView declaration..
    private var rvRecentChat: RecyclerView? = null

    //Edittext declaration...
    private var etSearch: EditText? = null

    //TextView declaration..
    private var tvNoDataFound: TextView? = null

    //LinearLayout declaration..
    private var llRecentChats: LinearLayout? = null

    //Firebase declaration..
    private var firebaseFireStore: FirebaseFirestore? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var chatFireBaseBOList = ArrayList<FireBaseBO>()
    private var recentChatAdapter: RecentChatAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebase intialisation..
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()

        //close al keyboards...
        closeKeyboard(mContext!!)

        //basic intialisationn..
        initViews(view)
    }

    override val activityLayout: Int
        get() = R.layout.fragment_chat

    private fun initViews(view: View) {
        //Editext binding.
        etSearch = view.findViewById(R.id.et_search)

        //Textview binding..
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found)

        //Linear layout declaration..
        llRecentChats = view.findViewById(R.id.ll_recent_chat)

        //RecycleView binding..
        rvRecentChat = view.findViewById(R.id.rv_recent_chat)
        val linearLayoutManager = LinearLayoutManager(mContext)
        rvRecentChat?.layoutManager = linearLayoutManager

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterTable(s.toString())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val documentRefer: DocumentReference? =if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
            firebaseFireStore?.collection("Users")?.document(CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID).toString())
        }else{
            firebaseFireStore?.collection("Users")?.document(CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString())
        }
        documentRefer?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    if(document.toObject(ChatDocument::class.java)!!.recent_chat!=null){
                        val users: List<FireBaseBO> = document.toObject(ChatDocument::class.java)!!.recent_chat
                        Log.e(TAG, "onStart: "+users.size )
                        chatFireBaseBOList.clear()
                        chatFireBaseBOList.addAll(users)
                        if (chatFireBaseBOList.size > 0) {
                            tvNoDataFound?.visibility = View.GONE
                            llRecentChats?.visibility = View.VISIBLE
                            recentChatAdapter = RecentChatAdapter(mContext!!,chatFireBaseBOList)
                            rvRecentChat?.adapter = recentChatAdapter
                        } else {
                            tvNoDataFound?.visibility = View.VISIBLE
                            llRecentChats?.visibility = View.GONE
                        }
                    }else{
                        tvNoDataFound?.visibility = View.VISIBLE
                        llRecentChats?.visibility = View.GONE
                    }
                }
            }
        }

        val documentReference: DocumentReference =
            if (CommonMethods.getPrefrence(mContext!!,
                    AllKeys.USERTYPE) == AllKeys.Patient
            ) {
                firebaseFireStore?.collection("Users")!!.document(
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID)
                        .toString())
            } else {
                firebaseFireStore?.collection("Users")!!.document(
                    CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString())
            }
        // val documentReference: DocumentReference = firebaseFireStore?.collection("Users")!!.document(CommonMethods.getPrefrence(mContext!!,AllKeys.HSC_ID).toString())
        documentReference.update("status", "Online").addOnSuccessListener {
            Log.e(TAG, "onStop: OnLine")
        }

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()
        /* if (chatAdapter != null) {
             chatAdapter!!.stopListening()
         }*/
        val documentReference: DocumentReference =
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
                firebaseFireStore?.collection("Users")!!.document(
                    CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID).toString())
            } else {
                firebaseFireStore?.collection("Users")!!.document(
                    CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString())
            }
        //val documentReference: DocumentReference = firebaseFireStore?.collection("Users")!!.document( CommonMethods.getPrefrence(mContext!!,AllKeys.HSC_ID).toString())
        documentReference.update("status", "Offline").addOnSuccessListener {
            Log.e(TAG, "onStop: Offline")
        }
    }

    fun filterTable(text: String?) {
        if (chatFireBaseBOList.size > 0) {
            val filteredList1: ArrayList<FireBaseBO> = ArrayList()
            for (item in chatFireBaseBOList) {
                if (text?.let { item.name.lowercase(Locale.ROOT).contains(it) } == true
                    || text?.let { item.name.uppercase(Locale.ROOT).contains(it) } == true
                ) {
                    filteredList1.add(item)
                }
            }
            //Log.e(TAG, "filter: size" + filteredList1.size());
            // Log.e(TAG, "filter: List" + filteredList1.toString());
            recentChatAdapter?.updateData(mContext, filteredList1)
        }
    }

    fun closeKeyboard(ctx: Context) {
        val inputMethodManager =
            ctx.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

}