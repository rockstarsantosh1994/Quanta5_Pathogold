package com.bms.pathogold_bms.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterBO
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterSummaryBO

class DailyCashRegisterViewModel  : ViewModel() {

    // This list will keep all values for Your ListView
    private val _dailyCashRegisterlist: MutableLiveData<ArrayList<DailyCashRegisterBO>> = MutableLiveData()
    private val _dailyCashRegisterSummarylist: MutableLiveData<ArrayList<DailyCashRegisterSummaryBO>> = MutableLiveData()

    val dailyCashRegisterlist: LiveData<ArrayList<DailyCashRegisterBO>>
        get() = _dailyCashRegisterlist

    val dailyCashRegisterSummarylist: LiveData<ArrayList<DailyCashRegisterSummaryBO>>
        get() = _dailyCashRegisterSummarylist

    init
    {
        _dailyCashRegisterlist.value = ArrayList()
        _dailyCashRegisterSummarylist.value = ArrayList()
    }

    // function which will add new values to Your list
    fun adddailyCashRegisterList(dailyCashRegisterList:ArrayList<DailyCashRegisterBO>)
    {
        _dailyCashRegisterlist.value?.clear()
        _dailyCashRegisterlist.value=ArrayList()
        _dailyCashRegisterlist.value!!.addAll(dailyCashRegisterList)
        _dailyCashRegisterlist.value = _dailyCashRegisterlist.value //this will notify observers
    }

    // function which will add new values to Your list
    fun adddailyCashSummaryList(dailyCashSummaryBOList:ArrayList<DailyCashRegisterSummaryBO>)
    {
        _dailyCashRegisterSummarylist.value?.clear()
        _dailyCashRegisterSummarylist.value=ArrayList()
        _dailyCashRegisterSummarylist.value!!.addAll(dailyCashSummaryBOList)
        _dailyCashRegisterSummarylist.value = _dailyCashRegisterSummarylist.value //this will notify observers
    }
}