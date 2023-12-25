package com.bms.pathogold_bms

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
//import butterknife.ButterKnife
//import butterknife.Unbinder
import com.bms.pathogold_bms.services.DigiPath
import com.bms.pathogold_bms.widget.LocaleHelper
import io.paperdb.Paper
import java.util.*

abstract class BaseFragment : Fragment() {
    var mContext: Context? = null
    //var unbinder: Unbinder? = null
    var digiPath: DigiPath? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        digiPath = requireActivity().application as DigiPath
        mContext = activity
        Paper.init(context)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val inflate = inflater.inflate(activityLayout, container, false)
       // unbinder = ButterKnife.bind(this, inflate)
        return inflate
    }

    protected abstract val activityLayout: Int

    override fun onDestroy() {
        super.onDestroy()
        //if (unbinder != null) unbinder!!.unbind()
    }


}
