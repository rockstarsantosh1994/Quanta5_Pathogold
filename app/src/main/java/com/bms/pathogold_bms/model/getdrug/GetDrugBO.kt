package com.bms.pathogold_bms.model.getdrug

class GetDrugBO (var DrugId:String,
                 var DrugName:String){

    override fun toString(): String {
        return "GetDrugBO(DrugId='$DrugId', DrugName='$DrugName')"
    }
}