package com.bms.pathogold_bms.model.getwalkdistance

class GetWalkDistanceBO(val Pno:String,
                        val Distance:String,
                        val Entrydate:String,
                        val starttime:String,
                        val endtime:String,
                        val username:String,
)
{
    override fun toString(): String {
        return "GetWalkDistanceBO(Pno='$Pno', Distance='$Distance', Entrydate='$Entrydate', starttime='$starttime', endtime='$endtime', username='$username')"
    }
}