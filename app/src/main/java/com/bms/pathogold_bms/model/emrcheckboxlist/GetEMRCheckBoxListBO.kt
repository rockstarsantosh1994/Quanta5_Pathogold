package com.bms.pathogold_bms.model.emrcheckboxlist

class GetEMRCheckBoxListBO{

    var HistoryId: String? = null
    var HistoryName: String? = null
    var HistoryCategory: String? = null
    var isSelected:Boolean = false

    fun getSelected(): Boolean {
        return isSelected
    }

    @JvmName("setSelected1")
    fun setSelected(selected: Boolean) {
        isSelected = selected
    }


    @JvmName("getHistoryId1")
    fun getHistoryId(): String? {
        return HistoryId
    }

    @JvmName("setHistoryId1")
    fun setHistoryId(historyId: String) {
        HistoryId = historyId
    }

    @JvmName("getHistoryName1")
    fun getHistoryName(): String? {
        return HistoryName
    }

    @JvmName("setHistoryName1")
    fun setHistoryName(historyName: String) {
        HistoryName = historyName
    }

    @JvmName("getHistoryCategory1")
    fun getHistoryCategory(): String? {
        return HistoryCategory
    }

    @JvmName("setHistoryCategory1")
    fun setHistoryCategory(historyCategory: String) {
        HistoryCategory = historyCategory
    }

    override fun toString(): String {
        return "GetEMRCheckBoxListBO(HistoryId='$HistoryId', HistoryName='$HistoryName', HistoryCategory='$HistoryCategory')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GetEMRCheckBoxListBO) return false

        if (HistoryId != other.HistoryId) return false

        return true
    }

    override fun hashCode(): Int {
        return HistoryId.hashCode()
    }


}