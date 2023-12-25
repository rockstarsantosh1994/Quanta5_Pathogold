package com.bms.pathogold_bms.model.getemrtreatment

class GetEMRTreatmentBO
    (val TreatmentNo:String,
     val TId:String,
     val DrugId:String,
     val IPDSrno:String,
     val PePatid:String,
     val OPDNo:String,
     val TDate:String,
     val TTime:String,
     val Dose:String,
     val Days:String,
     val Note:String,
     val FinancialYearID:String,
     val username:String,
     val Qty:String,
     val Drug_flag:String,
     val DrugName:String, )
{
    override fun toString(): String {
        return "GetEMRTreatmentBO(TreatmentNo='$TreatmentNo', TId='$TId', DrugId='$DrugId', IPDSrno='$IPDSrno', PePatid='$PePatid', OPDNo='$OPDNo', TDate='$TDate', TTime='$TTime', Dose='$Dose', Days='$Days', Note='$Note', FinancialYearID='$FinancialYearID', username='$username', Qty='$Qty', Drug_flag='$Drug_flag', DrugName='$DrugName')"
    }
}
