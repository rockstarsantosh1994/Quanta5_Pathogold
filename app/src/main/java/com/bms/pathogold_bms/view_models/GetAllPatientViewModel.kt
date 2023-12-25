package com.bms.pathogold_bms.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO

class GetAllPatientViewModel :ViewModel() {

    // This list will keep all values for Your ListView
    private val _list: MutableLiveData<ArrayList<GetPatientListBO>> = MutableLiveData()

    val list: LiveData<ArrayList<GetPatientListBO>>
        get() = _list

    init
    {
        _list.value = ArrayList()
    }

    // function which will add new values to Your list
    fun addNewValue(getPatientList:ArrayList<GetPatientListBO>)
    {
         _list.value=ArrayList()
         _list.value!!.addAll(getPatientList)
         _list.value = _list.value //this will notify observers
    }

}