package com.bms.pathogold_bms.model.getpatientvital

class GetPatientVitalBO (val Pulse:String,
                         val BP:String,
                         val SysBP:String,
                         val DiaBP:String,
                         val BMI:String,
                         val Height:String,
                         val Weight:String,
                         val SPO2:String,
                         val Temp:String,
                         val Respiration:String,
                         val Surgery:String,
                         val DateOfSurgery:String,
                         val FollowUpDate:String,
                         val DoctorAdvice:String,
                         val BloodGroup:String,
                         val BloodSugar:String
                         ) {
    override fun toString(): String {
        return "GetPatientVitalBO(Pulse='$Pulse', BP='$BP', SysBP='$SysBP', DiaBP='$DiaBP', BMI='$BMI', Height='$Height', Weight='$Weight', SPO2='$SPO2', Temp='$Temp', Respiration='$Respiration', Surgery='$Surgery', DateOfSurgery='$DateOfSurgery', FollowUpDate='$FollowUpDate', DoctorAdvice='$DoctorAdvice', BloodGroup='$BloodGroup', BloodSugar='$BloodSugar')"
    }
}