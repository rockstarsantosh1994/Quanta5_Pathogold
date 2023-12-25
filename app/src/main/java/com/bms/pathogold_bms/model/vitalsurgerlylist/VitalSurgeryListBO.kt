package com.bms.pathogold_bms.model.vitalsurgerlylist

class VitalSurgeryListBO (val Id:String,
                          val SurgeryName:String) {
    override fun toString(): String {
        return "VitalSurgeryListBO(Id='$Id', SurgeryName='$SurgeryName')"
    }
}