package com.bms.pathogold_bms.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser.parseCharset
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.DashBoardActivity
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class CommonMethods {
    companion object {

        val PRE_NAME = "appname_prefs"

        fun setPreference(context: Context, key: String?, value: String?) {
            val sharedPref = context.getSharedPreferences(PRE_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getPrefrence(context: Context, key: String?): String? {
            val prefs = context.getSharedPreferences(PRE_NAME, 0)
            return prefs.getString(key, "DNF")
        }

        fun showDialogWindow(activity: Activity?, title: String?, message: String?) {
            val mDialog = MaterialDialog.Builder(activity!!)
                .setTitle(title!!)
                .setMessage(message!!)
                .setCancelable(false)
                .setPositiveButton(
                    "Ok"
                ) { dialogInterface: DialogInterface, which: Int ->
                    // Delete Operation
                    dialogInterface.dismiss()
                } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .build()

            // Show Dialog
            mDialog.show()
        }

        fun IPAdressValidator(ip: String?): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            //final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)(\\.[A-Za-z]{welcomeone,})$";
            val IP_ADDRESS =
                ("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9]))")
            pattern = Pattern.compile(IP_ADDRESS)
            matcher = pattern.matcher(ip)
            return matcher.matches()
        }

        fun isNetworkAvailable(ctx: Context): Boolean {
            val connectivityManager =
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }


        /**
         * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
         */
        fun checkConnection(context: Context): Boolean {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connMgr.activeNetworkInfo
            if (activeNetworkInfo != null) { // connected to the internet
                //Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

                // connected to the mobile provider's data plan
                return if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    true
                } else activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
            } else {
                Toast.makeText(context, AllKeys.NO_INTERNET_AVAILABLE, Toast.LENGTH_SHORT).show()
            }
            return false
        }




        fun showKeyboard(ctx: Context, v: View) {
            v.requestFocus()
            val inputMethodManager =
                ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun closeKeyboard(ctx: Context) {
            val inputMethodManager =
                ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        fun hideSoftKeyboard(activity: Activity) {
            if (activity.currentFocus != null) {
                val inputMethodManager = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity
                        .currentFocus!!.windowToken, 0
                )
            }
        }

        fun openBrowser(context: Context, url: String) {
            var url = url
            if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://$url"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        }

        fun getYmdString(date: Date?): String? {
            return SimpleDateFormat("yyyy-MM-dd").format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun getTodayDate(pattern:String): String {
            val yearFormat: DateFormat = SimpleDateFormat(pattern)
            val calYear = Calendar.getInstance()
            val todayDate = yearFormat.format(calYear.time)

            return todayDate
        }

        fun getYmdDate(dateString: String?): Date? {
            try {
                return SimpleDateFormat("yyyy-MM-dd").parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }

        @SuppressLint("SimpleDateFormat")
        fun parseDateToddMMyyyy(time: String?,inputPattern:String?,outPutPattern:String?): String? {
            val inputPattern = inputPattern
            val outputPattern = outPutPattern
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertToYearsMonthsDays(todayDate: String, DOB: String?): String {
            val format = SimpleDateFormat("MM/dd/yyyy",
                Locale.getDefault())
            val startdate: Date = format.parse(DOB)
            val enddate: Date = format.parse(todayDate)
            var n = enddate.time - startdate.time
            var year: Long?=null
            var month: Long?=null
            var day: Long?=null
            var hour: Long?=null
            var minute: Long?=null
            var second: Long?=null

            if (n > 0) {

                // year = 365 days
                year = n / 31536000000
                if (year != 0L) n -= year * 31536000000
                // month = 30 Days
                month = n / 2592000000
                if (month != 0L) n -= month * 2592000000
                day = n / 86400000
                if (day != 0L) n -= day * 86400000
                hour = n / 3600000
                if (hour != 0L) n -= hour * 3600000
                minute = n / 60000
                if (minute != 0L) n -= minute * 60000
                second = n / 1000
            }

            return "$year Year $month Month $day Days"
        }

        fun showDialogForSuccess(context: Context,message: String) {
            val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
                .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
                .setCancelable(false)
                .setAnimation(R.raw.success_exploration)
                .setPositiveButton("Ok"
                ) { dialogInterface: DialogInterface, which: Int ->
                    // Delete Operation
                    dialogInterface.dismiss()
                    //(context as DashBoardActivity).navController.popBackStack(R.id.patientRegistrationFragment, true)
                   // (context as DashBoardActivity).navController.popBackStack(R.id.checkSlotFragment2, true)
                    //context.finish()
                    //Calling Intent...
                    val intent = Intent(context, DashBoardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    context.finish()
                } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .build()

            // Show Dialog
            mDialog.show()
        }

        fun showDialogForError(context: Context,message: String) {
            val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
                .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
                .setCancelable(false)
                //.setAnimation(R.raw.success_exploration)
                .setPositiveButton("Ok") { dialogInterface: DialogInterface, which: Int ->
                    // Delete Operation
                    dialogInterface.dismiss()
                } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .build()

            // Show Dialog
            mDialog.show()
        }

        private fun sendNotification(context: Context,getPatientListBO: GetPatientListBO) {
            var message="Hi.. "+getPatientListBO.PatientName+" is requesting you a video call...please receive it"
            val notificationData = JSONObject()
            val notificationData1 = JSONObject()
            val notification = JSONObject()
            try {
                //parameter sending for notification key...
                notificationData1.put("title", R.string.app_name)
                notificationData1.put("body", message)

                //parameter sending for data key....
                notificationData.put("title", R.string.app_name)
                notificationData.put("message", message)
                notificationData.put("key_1", ConfigUrl.VIDEO_CALL_BASE_URL+getPatientListBO.Pno)
                notification.put("to", getPrefrence(context, AllKeys.GCM_TOKEN))
                notification.put("notification", notificationData1)
                notification.put("data", notificationData)
                val jsonObjectRequest: JsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, ConfigUrl.FCM_API, notification,
                        Response.Listener { response: JSONObject ->
                            Log.e("mytag",
                                "sendNotification: $response")
                        },
                        Response.ErrorListener { error: VolleyError ->
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
                            // Log.e("mytag", "error: " + error);
                            val response = error.networkResponse
                            if (error is ServerError && response != null) {
                                try {
                                    val res = String(response.data,
                                        Charset.forName(parseCharset(response.headers, "utf-8")))
                                    // Now you can use any deserializer to make sense of data
                                    val obj = JSONObject(res)
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

        fun calculateNoOfColumns(
            context: Context,
            columnWidthDp: Float,
        ): Int { // For example columnWidthdp=180
            val displayMetrics = context.resources.displayMetrics
            val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
            return (screenWidthDp / columnWidthDp + 0.5).toInt() // +0.5 for correct rounding to int.
        }
    }


}