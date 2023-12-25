package com.bms.pathogold_bms.model.getdosemaster

class GetDoseMasterBO (var SrNo:String,
                       var Dose:String,
                       var Qty:String,
                       var DoseDescription:String,
                       var StandardTime:String,
){
    override fun toString(): String {
        return "GetDrugMasterBO(SrNo='$SrNo', Dose='$Dose', Qty='$Qty', DoseDescription='$DoseDescription', StandardTime='$StandardTime')"
    }
}