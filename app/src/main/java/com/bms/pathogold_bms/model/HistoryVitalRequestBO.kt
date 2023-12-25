package com.bms.pathogold_bms.model

class HistoryVitalRequestBO {

    private var PepatId: String? = null
    private var OpdNo: String? = null
    private var Companyid: String? = null
    private var HistoryName: String? = null
    private var HistCatId: String? = null

    fun getPepatId(): String? {
        return PepatId
    }

    fun setPepatId(pepatId: String?) {
        PepatId = pepatId
    }

    fun getOpdNo(): String? {
        return OpdNo
    }

    fun setOpdNo(opdNo: String?) {
        OpdNo = opdNo
    }

    fun getCompanyid(): String? {
        return Companyid
    }

    fun setCompanyid(companyid: String?) {
        Companyid = companyid
    }

    fun getHistoryName(): String? {
        return HistoryName
    }

    fun setHistoryName(historyName: String?) {
        HistoryName = historyName
    }

    fun getHistCatId(): String? {
        return HistCatId
    }

    fun setHistCatId(histCatId: String?) {
        HistCatId = histCatId
    }
}