package com.bms.pathogold_bms.model.getcomplain

data class GetComplainBO(var ComName:String,
                         var ComCode:String
){
    override fun toString(): String {
        return " $ComName"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GetComplainBO) return false

        if (ComName != other.ComName) return false
        if (ComCode != other.ComCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ComName.hashCode()
        result = 31 * result + ComCode.hashCode()
        return result
    }


}