package com.bms.pathogold_bms.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R

class VideoConferenceActivity2: BaseActivity() {

    //Toolbar declaration
    private var toolbar: Toolbar?=null

    private val TAG = "VideoConferenceActivity2"
    private lateinit var webView: WebView
    private var url = "enter URL here"

    private var btnEnterPipMode:AppCompatButton?=null

    override val activityLayout: Int
        get() = R.layout.activity_video_conference

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //  supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.setTitleTextColor(Color.BLACK)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        //toolbar.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        btnEnterPipMode=findViewById(R.id.btn_pip_mode)
        btnEnterPipMode?.setOnClickListener {
            try{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    enterPictureInPictureMode()
                }else{
                    Toast.makeText(mContext, "Phone does'nt support PIP mode", Toast.LENGTH_SHORT).show()
                }
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }
        }

        WebView.setWebContentsDebuggingEnabled(true)
        webView = findViewById(R.id.webview)
        url= intent.getStringExtra("link").toString()
        //url= "https://vc.bestbrain.in/ng/demo"
        Log.e(TAG, "onCreate: url $url" )
        //Toast.makeText(mContext, url, Toast.LENGTH_LONG).show()
        //        WebSettings settings = mWebView.getSettings();
        webView.settings.userAgentString ="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36"
        webView.settings.javaScriptEnabled = true

        //Code added by santosh
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {

            override fun onPermissionRequest(request: PermissionRequest?) {
                if (request == null) {
                    Log.i(TAG, "no request returned")
                } else {
                    Log.i(TAG, request.toString())
                    request.grant(request.resources)
                }
            }
        }

        class myWebClient : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url.equals("www.google.com")){
                    startActivity(Intent(applicationContext, DashBoardActivity::class.java))
                    finish()
                }
            }
        }

//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                if ("myLogoutUrl".equals(url)){
//                    //do stuff
//                }
//                super.onPageStarted(view, url, favicon);
//            }
//        });

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                1
            )
        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                1
            )
        } else if(arePermissionsGranted()) {
            webView.loadUrl(url)
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    webView.loadUrl(url)
                } else {
                    Toast.makeText(
                        this,
                        "You Must Grant Permission To continue further, Please restart Application",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(TAG, "permission denied")
                }
                return
            }
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return (
                ActivityCompat.checkSelfPermission( this, "android.permission.CAMERA" ) == PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission( this, "android.permission.RECORD_AUDIO" ) == PERMISSION_GRANTED
                )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure want to close conference?")
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setCancelable(true)
        builder.setNegativeButton("No", null)
        builder.setPositiveButton("Yes") { _, i ->
            // Clear all the Application Cache, Web SQL Database and the HTML5 Web Storage
            WebStorage.getInstance().deleteAllData();

            // Clear all the cookies
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();

            webView.clearCache(true);
            webView.clearFormData();
            webView.clearHistory();
            webView.clearSslPreferences();
            webView.destroy()

            startActivity(Intent(applicationContext,DashBoardActivity::class.java))
            finish()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onUserLeaveHint() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                enterPictureInPictureMode()
            }else{
                Toast.makeText(mContext, "Phone does'nt support PIP mode", Toast.LENGTH_SHORT).show()
            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration, ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            // Hide the controls in picture-in-picture mode.
            toolbar?.visibility= View.GONE
            btnEnterPipMode?.visibility= View.GONE
        } else {
            // Show the video controls if the video is not playing
            toolbar?.visibility= View.VISIBLE
            btnEnterPipMode?.visibility= View.VISIBLE
        }
    }
}
