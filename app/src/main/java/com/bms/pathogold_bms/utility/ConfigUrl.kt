package com.bms.pathogold_bms.utility

class ConfigUrl {
    companion object{
        //var BASE_URL = "http://pathogold.pathogold.com/PatientWSCId.asmx/" //Pathogold Production URL.
        var BASE_URL = "https://pathogoldcloud.com/apk/PatientWSCId.asmx/" //Pathogold secured LIVE URL.
        //var BASE_URL = "https://maxim-lis.com/app/PatientWSCId.asmx/" //Maxim-Lis secured LIVE URL.
        const val FCM_API = "https://fcm.googleapis.com/fcm/send"
        const val AUTHORIZATION="key=AAAAfDf9Noo:APA91bEQTgOadrgYgAF0rqHzXEIvICCfGJWhCTcagP-acbEpdhrl10TJ5F-GKre2so46yomtMWA2jtHL0xBsVWO_nVz40LgMN87lK1SPhKINJTnZdm1qSIzkHmINdVhC-6ccJz1iPpex"
        const val IMAGE_URL="https://pathogoldcloud.com/labs/PrintReport/"
        const val VIDEO_CALL_BASE_URL="https://vc.fastmeets.com/ng/";
    }
}