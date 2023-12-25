package com.bms.pathogold_bms

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
//import butterknife.ButterKnife
//import butterknife.Unbinder
import com.bms.pathogold_bms.services.DigiPath
import io.paperdb.Paper
import java.util.*


abstract class BaseActivity : AppCompatActivity() {

    var mContext: Context? = null
   // var unbinder: Unbinder? = null
    var digiPath: DigiPath? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityLayout)
     //   unbinder = ButterKnife.bind(this)
        digiPath = application as DigiPath
        mContext = this
        Paper.init(this)
        //Compulsory light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //mContext = LocaleHelper.setLocale(mContext, "fr")
        //mContext?.resources
        if(BuildConfig.FLAVOR == "pathogoldfrench"){
            val myLocale = Locale("fr")
            val res = resources
            val dm = res.displayMetrics
            val conf: Configuration = res.configuration
            conf.locale = myLocale
            res.updateConfiguration(conf, dm)
        }
    }

    protected abstract val activityLayout: Int

    override fun onDestroy() {
        super.onDestroy()
      //  unbinder!!.unbind()
    }
}