package com.bms.pathogold_bms.model.adddrug

class AddDrugBO (val drugid:String,
                 val drugname:String,){

    override fun toString(): String {
        return "AddDrugBO(drugid='$drugid', drugname='$drugname')"
    }
}