package com.bms.pathogold_bms.model.reportdetails

class ReportDetailsBO (var testname:String,
                       var parametername:String,
                       var result:String,
                       var normalRange:String,
                       var EntryDate:String){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReportDetailsBO) return false

        if (testname != other.testname) return false
        if (parametername != other.parametername) return false
        if (result != other.result) return false
        if (normalRange != other.normalRange) return false
        if (EntryDate != other.EntryDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = testname.hashCode()
        result1 = 31 * result1 + parametername.hashCode()
        result1 = 31 * result1 + result.hashCode()
        result1 = 31 * result1 + normalRange.hashCode()
        result1 = 31 * result1 + EntryDate.hashCode()
        return result1
    }
}