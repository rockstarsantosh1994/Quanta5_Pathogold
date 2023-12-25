package com.bms.pathogold_bms.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import android.media.AudioAttributes
import com.google.gson.Gson
import android.content.ContentResolver
import android.net.Uri
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.SpecificChatActivity
import com.bms.pathogold_bms.activity.VideoConferenceActivity2
import com.bms.pathogold_bms.model.chat.FireBaseBO

@SuppressLint("MissingFirebaseInstanceTokenRefresh", "Registered")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val ADMINCHANNELID = "admin_channel"
    private val TAG = "MyFirebaseMessagingService"
    var intent: Intent? = null

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val clickAction=remoteMessage.data["click_action"]

        Log.e(TAG, "onMessageReceived: $data")

        /* Create an Intent that will start the Menu-Activity. */
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val notificationID = Random().nextInt(3000)
        val title: String? =
            if (data["title"] == null || data["title"].toString().isEmpty()) {
                remoteMessage.notification?.title
            } else {
                data["title"].toString()
            }
        val message: String? =
            if (data["message"] == null || data["message"].toString().isEmpty()) {
                remoteMessage.notification?.body
            } else {
                data["message"].toString()
            }
        if (clickAction.equals("CHAT",false)) {
            intent = Intent(this, SpecificChatActivity::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            val gson = Gson()
            val fireBaseBO: FireBaseBO = gson.fromJson( remoteMessage.data["extra_information"], FireBaseBO::class.java)
            intent?.putExtra("name", fireBaseBO.name)
            intent?.putExtra("receiveruid",  fireBaseBO.uid)
            intent?.putExtra("imageuri",  fireBaseBO.image)
            intent?.putExtra("token", fireBaseBO.token)
            intent?.putExtra("firebase_bo",fireBaseBO)
        }else{
            intent = Intent(this, VideoConferenceActivity2::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            intent!!.putExtra("link", remoteMessage.data["extra_information"])
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        ///val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
      */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager,clickAction.toString())
        }

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val notificationSoundUri =if (clickAction.equals("CHAT",false)) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }else{
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        }

        val notificationBuilder = NotificationCompat.Builder(this, ADMINCHANNELID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            //.setDefaults(Notification.DEFAULT_ALL)
            .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +applicationContext.packageName +"/"+R.raw.zapsplat_multimedia_ringtone))
            .setOnlyAlertOnce(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        notificationManager?.notify(notificationID, notificationBuilder.build())

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = resources.getColor(R.color.purple_700)
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        }

        assert(notificationManager != null)
        notificationManager!!.notify(notificationID, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?, clickAction: String) {
        val soundUri: Uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.zapsplat_multimedia_ringtone)

        val notificationSoundUri =if (clickAction.equals("CHAT",false)) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }else{
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        }

       // val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val adminChannelName: CharSequence = "New notification"
        val adminChannelDescription = "Device to devie notification"
        val adminChannel = NotificationChannel(ADMINCHANNELID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        adminChannel.setSound(soundUri, audioAttributes)
        notificationManager?.createNotificationChannel(adminChannel)
    }
}