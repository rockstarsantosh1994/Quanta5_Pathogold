package com.bms.pathogold_bms.services

import android.app.Application
import android.content.Context

class DigiPath : Application() {
    private var apiRequestHelper: ApiRequestHelper? = null
    var context: Context? = null
    override fun onCreate() {
        super.onCreate()
        doInit()
    }

    private fun doInit() {
        apiRequestHelper = ApiRequestHelper.init(this)
    }

    @Synchronized
    fun getApiRequestHelper(): ApiRequestHelper? {
        return apiRequestHelper
    }
}