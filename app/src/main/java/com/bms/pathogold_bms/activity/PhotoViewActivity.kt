package com.bms.pathogold_bms.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.R
import com.github.chrisbanes.photoview.PhotoView

class PhotoViewActivity : BaseActivity() {

    //Declaration....
    private val TAG = "PhotoViewActivity"

    //Toolbar declaration...
    private var toolbar: Toolbar?=null

    //PhotoView declaration
    private var photoView: PhotoView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //basic intialisation
        initViews()
    }

    override val activityLayout: Int
        get() = R.layout.activity_photo_view

    private fun initViews(){
        //toolbar binding..
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = resources.getString(R.string.view_image)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        //toolbar.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        //photo view binding
        photoView=findViewById(R.id.photoview)

        var bitmap : Bitmap? =null
        if (intent.hasExtra("img_src")){
            //convert to bitmap
            val byteArray = intent.getByteArrayExtra("img_src")
            if (byteArray != null) {
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
            photoView?.setImageBitmap(bitmap)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}