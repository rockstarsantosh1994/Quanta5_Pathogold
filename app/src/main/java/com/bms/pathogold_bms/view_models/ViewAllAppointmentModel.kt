package com.bms.pathogold_bms.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import java.util.LinkedHashMap

class ViewAllAppointmentModel  : ViewModel(){

    // This list will keep all values for Your ListView
    private val _list: MutableLiveData<MutableMap<String, ArrayList<ViewAppointmentBO>>> = MutableLiveData()

    val list: LiveData<MutableMap<String, ArrayList<ViewAppointmentBO>>>
        get() = _list

    init
    {
        _list.value = LinkedHashMap()
    }

    // function which will add new values to Your list
    fun addNewValue(viewAppointmentAllMap: MutableMap<String, ArrayList<ViewAppointmentBO>>)
    {
        _list.value=LinkedHashMap()
        _list.value!!.putAll(viewAppointmentAllMap)
        _list.value = _list.value //this will notify observers
    }
}