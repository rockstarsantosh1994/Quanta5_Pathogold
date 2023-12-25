package com.bms.pathogold_bms.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : BaseActivity() {

    private val TAG = "SplashActivity"

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Change android system's buttons color
        window.navigationBarColor = ContextCompat.getColor(this@SplashActivity, R.color.purple_700)
        //Get Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.e(TAG, "onCreate: $token" )
            CommonMethods.setPreference(this@SplashActivity, AllKeys.GCM_TOKEN, token)
        })

        val SPLASHDISPLAYDURATION = 2000

        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
                if (CommonMethods.getPrefrence(this@SplashActivity, AllKeys.HSC_ID).equals(AllKeys.DNF)
                    && CommonMethods.getPrefrence(this@SplashActivity, AllKeys.PATIENT_PROFILE_ID).equals(AllKeys.DNF)) {
                    // check if permissions are given
                    val intent = Intent(this@SplashActivity, LoginTypeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@SplashActivity, DashBoardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
        }, SPLASHDISPLAYDURATION.toLong())
    }

    override val activityLayout: Int
        get() = R.layout.activity_main
}