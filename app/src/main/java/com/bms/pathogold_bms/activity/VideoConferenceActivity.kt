package com.bms.pathogold_bms.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R

class VideoConferenceActivity : BaseActivity() {
    //Declaration....
    //Toolbar declaration...
    private var toolbar: Toolbar?=null

    private val TAG = "VideoConferenceActivity"
    private lateinit var webView: WebView
    private var url=""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        url= intent.getStringExtra("link").toString()
        Toast.makeText(mContext, url, Toast.LENGTH_LONG).show()
        //toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = "Video Conference"
        toolbar?.setTitleTextColor(Color.BLACK)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)

        val browserIntent = Intent (Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)

        /*WebView.setWebContentsDebuggingEnabled(true)
        webView = findViewById(R.id.webview)
        //WebSettings settings = mWebView.getSettings();
        webView.settings.userAgentString ="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36"

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false

        //url= intent.getStringExtra("link").toString()
        Toast.makeText(mContext, url, Toast.LENGTH_LONG).show()
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
                    startActivity(Intent(applicationContext,DashBoardActivity::class.java))
                    finish()
                }
            }
        }*/

        //        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                if ("myLogoutUrl".equals(url)){
//                    //do stuff
//                }
//                super.onPageStarted(view, url, favicon);
//            }
//        });

    /*    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
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
        }*/
    }

    override val activityLayout: Int
        get() = R.layout.activity_video_conference

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    webView.loadUrl(url)
                } else {
                    Toast.makeText(this, "You Must Grant Permission To continue further, Please restart Application", Toast.LENGTH_LONG).show()
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
    }*/


    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure want to close conference?")
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setCancelable(true)
        builder.setNegativeButton("No", null)
        builder.setPositiveButton(
            "Yes"
        ) { _, i -> startActivity(Intent(applicationContext,DashBoardActivity::class.java))
            finish()}
        val alertDialog = builder.create()
        alertDialog.show()
    }

}