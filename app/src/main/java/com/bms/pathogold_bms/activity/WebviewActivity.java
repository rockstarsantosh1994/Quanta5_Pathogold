package com.bms.pathogold_bms.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bms.pathogold_bms.R;
import com.bms.pathogold_bms.utility.CommonMethods;

import java.io.File;

//import butterknife.BindView;
//import butterknife.ButterKnife;

public class WebviewActivity extends AppCompatActivity {

    private static final String TAG = "WebviewActivity";
   // @BindView(R.id.toolbar)
    Toolbar toolbar;
    Context mContext;
    //@BindView(R.id.webview)
    WebView webview;
   // @BindView(R.id.progressBar)
    ProgressBar progressBar;
    //@BindView(R.id.btn_share)
    AppCompatButton btnShare;

    private final String removePdfTopIcon = "javascript:(function() {" + "document.querySelector('[role=\"toolbar\"]').remove();})()";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
       // ButterKnife.bind(this);
            toolbar = findViewById(R.id.toolbar);
            webview = findViewById(R.id.webview);
            progressBar = findViewById(R.id.progressBar);
            btnShare = findViewById(R.id.btn_share);

            mContext = WebviewActivity.this;
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);
            toolbar.setTitle(getIntent().getStringExtra("patient_name")+" Report");
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

//          webview.setWebViewClient(new MyBrowser());
            String url = getIntent().getStringExtra("url");
            if(getIntent().getStringExtra("patient_name").equals("Privacy Policy")){
                 loadPrivacyUrl(url);
            }else{
                btnShare.setVisibility(View.VISIBLE);
                showPdfFile(url);
            }

            btnShare.setOnClickListener(view -> {
                downloadPdfAndShare(url);
            });

    }

    private void downloadPdfAndShare(String url) {
        String newString = url.substring(url.lastIndexOf("/")+1);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), newString);

        Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName()+".fileprovider",file);

        if (file.exists()) {
            Toast.makeText(mContext, "Already downloaded", Toast.LENGTH_SHORT).show();

            //Sharing data to using shareIntent
            shareIntent(uri);

        } else {
            Toast.makeText(mContext, "Downloading", Toast.LENGTH_SHORT).show();
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri1 = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri1);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            long downloadId = manager.enqueue(request);
            Log.e(TAG, "onBindViewHolder: ID " + downloadId);

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) != downloadId) {
                            // Quick exit - its not our download!
                            return;
                        }

                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downloadId);
                        Cursor c = manager.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                @SuppressLint("Range") String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                Uri uri = Uri.parse(uriString);
                                // Log.e(TAG, "onReceive: " + uri);

                                //Sharing data to using shareIntent
                                shareIntent(uri);
                            }

                        }
                        getApplicationContext().unregisterReceiver(this);
                    }
                }
            };

            getApplicationContext().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    private void shareIntent(Uri uri) {
        String shareBody="Hello user,\n Please find an Report of\n"+getIntent().getStringExtra("patient_name")+"\n \n and also find this application on playstore \n"+getString(R.string.app_link);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("pdf/*");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing pdf");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sendIntent, "Share via"));
    }

    private void loadPrivacyUrl(String url) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        webview.invalidate();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.loadUrl(url);
        //webview.loadUrl(imageString);
        webview.setWebViewClient(new WebViewClient() {
            boolean checkOnPageStartedCalled = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                checkOnPageStartedCalled = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (checkOnPageStartedCalled) {
                    webview.loadUrl(removePdfTopIcon);
                    progressBar.setVisibility(ProgressBar.GONE);
                } else {
                    showPdfFile(url);
                }
            }
        });
    }

    private void showPdfFile(final String imageString) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        webview.invalidate();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.loadUrl("https://docs.google.com/gview?embedded=true&url="+imageString);
        webview.setWebViewClient(new WebViewClient() {
            boolean checkOnPageStartedCalled = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                checkOnPageStartedCalled = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (checkOnPageStartedCalled) {
                    webview.loadUrl(removePdfTopIcon);
                    progressBar.setVisibility(ProgressBar.GONE);
                } else {
                    showPdfFile(imageString);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CommonMethods.Companion.hideSoftKeyboard(WebviewActivity.this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}