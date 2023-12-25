package com.bms.pathogold_bms.services

import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.adddrug.AddDrugResponse
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsResponse
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterResponse
import com.bms.pathogold_bms.model.emrcheckboxlist.GetEMRCheckBoxListResponse
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListResponse
import com.bms.pathogold_bms.model.getclientuploadedfile.GetClientUploadedResponse
import com.bms.pathogold_bms.model.getcomplain.GetComplainResponse
import com.bms.pathogold_bms.model.getcomplaintsallergies.GetComplaintsAllergiesResponse
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse
import com.bms.pathogold_bms.model.getdiagnosis.GetDiagnosisResponse
import com.bms.pathogold_bms.model.getdosemaster.GetDoseMasterResponse
import com.bms.pathogold_bms.model.getdrug.GetDrugResponse
import com.bms.pathogold_bms.model.getemrtreatment.GetEMRTreatmentResponse
import com.bms.pathogold_bms.model.getpatient.GetPatientResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListResponse
import com.bms.pathogold_bms.model.getpatientvital.GetPatientVitalResponse
import com.bms.pathogold_bms.model.getprovisionaldiagnosis.GetProvisionalDiagnosisResponse
import com.bms.pathogold_bms.model.getrefcc.GetRefCCResponse
import com.bms.pathogold_bms.model.getslots.GetSlotsResponse
import com.bms.pathogold_bms.model.getsysexams.GetSysExamsResponse
import com.bms.pathogold_bms.model.getwalkdistance.GetWalkDistanceResponse
import com.bms.pathogold_bms.model.lab_super_admin.LabNameResponse
import com.bms.pathogold_bms.model.login.LoginResponse
import com.bms.pathogold_bms.model.otp.OtpResponse
import com.bms.pathogold_bms.model.patienttestlist.GetPatientTestListResponse
import com.bms.pathogold_bms.model.report.ReportResponse
import com.bms.pathogold_bms.model.reportdetails.ReportDetailsResponse
import com.bms.pathogold_bms.model.service.GetServiceResponse
import com.bms.pathogold_bms.model.testcode.GetTestCodeResponse
import com.bms.pathogold_bms.model.vaccination.VaccinationResponse
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientResponse
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentResponse
import com.bms.pathogold_bms.model.vitalsurgerlylist.VitalSurgeryListResponse
import retrofit2.Call
import retrofit2.http.*

interface DigiPathServices {
    //login api..
    @FormUrlEncoded
    @POST("UserIdPasswordValidation")
    fun login(@FieldMap params:Map<String,String>): Call<LoginResponse?>?

    //login api..
    @FormUrlEncoded
    @POST("loginbymobile")
    fun pateintLoginByMobile(@FieldMap params:Map<String,String>): Call<LoginResponse?>?

    //Get available slots...
    @FormUrlEncoded
    @POST("GetPhleboAvailableTimeSlots")
    fun getSlots(@FieldMap params:Map<String,String>): Call<GetSlotsResponse?>?

    //Booking appointment.
    @FormUrlEncoded
    @POST("PatientBookAppointment")
    fun patientBookAppointment(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    @FormUrlEncoded
    @POST("GetPatientListByMobileNo")
    fun getPatientByMobileNo(@FieldMap params:Map<String,String>): Call<GetPatientResponse?>?

    //Update my profile section
    @FormUrlEncoded
    @POST("LabBoyProfileUpdate")
    fun labBoyProfileUpdate(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //ViewAppointment.java Api
    @FormUrlEncoded
    @POST("ViewAppointment_all")
    fun viewAppointmentAll(@FieldMap params:Map<String,String>): Call<ViewAppointmentResponse?>?

    //ViewAppointment.java Api
    @FormUrlEncoded
    @POST("ViewAppointmentAccepted")
    fun viewAppointmentAccepted(@FieldMap params:Map<String,String>): Call<ViewAppointmentResponse?>?

    //ViewAppointment.java Api
    @FormUrlEncoded
    @POST("ViewAppointmentPending")
    fun viewAppointmentPending(@FieldMap params:Map<String,String>): Call<ViewAppointmentResponse?>?

    //Update ViewAppDetailedActivity.java  Appointment
    @FormUrlEncoded
    @POST("UpdateViewAppointment")
    fun updateViewAppointment(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //Accepted appointment api
    @FormUrlEncoded
    @POST("PatientAppointmentConfirm")
    fun patientAppointmentConfirm(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //Reschedule Appointment
    @FormUrlEncoded
    @POST("PatientAppointmentCancelAndReschedule")
    fun patientAppointmentCancelAndReschedule(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //get phelobo list
    @FormUrlEncoded
    @POST("GetPhleboList")
    fun getPhleboList(@FieldMap params:Map<String,String>): Call<ConsultationResponse?>?

    //get consulatation list
    @FormUrlEncoded
    @POST("GetConsultantList")
    fun getConsultantList(@FieldMap params:Map<String,String>): Call<ConsultationResponse?>?

    //For appointment cancel api
    @FormUrlEncoded
    @POST("PatientAppointmentCancel")
    fun patientAppointmentCancel(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //For appointment complete api
    @FormUrlEncoded
    @POST("PatientAppointmentComplete")
    fun patientAppointmentComplete(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //getPatientList based on conditions login wise..
    @FormUrlEncoded
    @POST("GetPatientList")
    fun getPatientList(@FieldMap params:Map<String,String>): Call<GetPatientListResponse?>?

    @FormUrlEncoded
    @POST("GetPatientTestList")
    fun getPatientTestList(@FieldMap params:Map<String,String>): Call<GetPatientTestListResponse?>?

    //file i.e sound ,image uploading...
    @FormUrlEncoded
    @POST("UploadLabClientImage")
    fun uploadLabClientImage(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //file i.e sound ,image viewing...
    @FormUrlEncoded
    @POST("GetClientUploadedFile")
    fun getClientUploadedFile(@FieldMap params:Map<String,String>): Call<GetClientUploadedResponse?>?

    //Get all test code while patient registration...PatientRegistrationActivity
    @FormUrlEncoded
    @POST("GetTestCode")
    fun getTestCode(@FieldMap params:Map<String,String>): Call<GetTestCodeResponse?>?

    //Patient registration....PatientRegistrationActivity
    @FormUrlEncoded
    @POST("ContactInformationForCC_New_API")
    fun contactinformationforccNewApi(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //Get all drug for PrescriptionActivity..
    @GET("GetDrugList")
    fun getDrugList(): Call<GetDrugResponse?>?

    //Get all DoseMaster for PrescriptionActivity..
    @GET("GetDoseMasterlist")
    fun getDoseMasterlist(): Call<GetDoseMasterResponse?>?

    //Get all GetEMRCheckboxlist for for VitalsActivity
    @GET("GetEMRCheckboxList")
    fun getEMRCheckboxList(): Call<GetEMRCheckBoxListResponse?>?

    //Get all GetEMRCheckboxlist for for VitalsActivity
    @GET("GetAllergyList")
    fun getAllergyList(): Call<GetAllergyListResponse?>?

    //Getvital surgery list...fir VitalsActivity
    @FormUrlEncoded
    @POST("GetVitalSurgeryList")
    fun getVitalSurgeryList(@FieldMap params:Map<String,String>): Call<VitalSurgeryListResponse?>?

    //Insert vital details patient in...VitalsActivity
    @FormUrlEncoded
    @POST("InsertVitalDetailsofpatient")
    fun insertVitalDetailsofpatient(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //Insert vital allergies in...VitalsActivity
    @FormUrlEncoded
    @POST("InsertVitalAllergies")
    fun insertVitalAllergies(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //Insert vital history in...VitalsActivity
    @FormUrlEncoded
    @POST("InsertVitalHistory")
    fun insertVitalHistory(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //Insert prescription in PrescriptionActivity.
    @FormUrlEncoded
    @POST("InsertEMRTreatmentDetailsofPaitent")
    fun insertEMRTreatmentDetailsofPaitent(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //View old prescription in OldPrescActivity.
    @FormUrlEncoded
    @POST("GetEMRTreatmentlist")
    fun getEMRTreatmentlist(@FieldMap params:Map<String,String>): Call<GetEMRTreatmentResponse?>?

    //Insert report in ReportActivity.
    @FormUrlEncoded
    @POST("GetReportDownload")
    fun getReportDownload(@FieldMap params:Map<String,String>): Call<ReportResponse?>?

    //getPatientVital in PrescriptionActivity.
    @FormUrlEncoded
    @POST("GetPatientVitalDetails")
    fun getPatientVitalDetails(@FieldMap params:Map<String,String>): Call<GetPatientVitalResponse?>?

    //Insert Walk In distance...
    @FormUrlEncoded
    @POST("InsertWalkDistanceOfCollectionBoy")
    fun insertWalkDistanceOfCollectionBoy(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //getDistanceof phelbo....
    @FormUrlEncoded
    @POST("GetDistanceSheetOfCollectionBoy")
    fun getDistanceSheetOfCollectionBoy(@FieldMap params:Map<String,String>): Call<GetWalkDistanceResponse?>?

    //getDistanceof phelbo....
    @FormUrlEncoded
    @POST("GetComplaintsExaminationHistoryAllergiesVitalPatientDetails")
    fun getComplaintsExaminationHistoryAllergiesVitalPatientDetails(@FieldMap params:Map<String,String>): Call<GetComplaintsAllergiesResponse?>?

    //GetTestResultdetails reportsFragment....
    @FormUrlEncoded
    @POST("GetTestResultdetails")
    fun getTestResultdetails(@FieldMap params:Map<String,String>): Call<ReportDetailsResponse?>?

    //GetVaccineList viewVaccinationFragment.....
    @FormUrlEncoded
    @POST("GetVaccineList")
    fun getVaccineList(@FieldMap params:Map<String,String>): Call<VaccinationResponse?>?

    //AddNew Vaccination addVaccinationFragment..
    @FormUrlEncoded
    @POST("Insertupdatevaccinemaster")
    fun insertupdatevaccinemaster(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //gat vaccine list patient in vaccinationFragment.
    @FormUrlEncoded
    @POST("GetVaccineList_patient")
    fun getVaccineList_patient(@FieldMap params:Map<String,String>): Call<VaccinationPatientResponse?>?

    //getRefCCAndDr.. for Patient Registration
    @FormUrlEncoded
    @POST("Getref_cc")
    fun getref_cc(@FieldMap params:Map<String,String>): Call<GetRefCCResponse?>?

    //Insert Walk In distance...
    @FormUrlEncoded
    @POST("Insertupdatedrugmaster")
    fun insertupdatedrugmaster(@FieldMap params:Map<String,String>): Call<AddDrugResponse?>?

    //InsertUpdateVaccination_patient..for EnterDetailsVaccinationFragment
    @FormUrlEncoded
    @POST("InsertUpdateVaccination_patient")
    fun insertUpdateVaccinationPatient(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //PatientAppointmentRemainder..for ReminderBroadCast
    @FormUrlEncoded
    @POST("PatientAppointmentRemainder")
    fun patientAppointmentRemainder(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //Daily Cash Register....
    @FormUrlEncoded
    @POST("dailycashRegister")
    fun dailyCashRegister(@FieldMap params:Map<String,String>): Call<DailyCashRegisterResponse?>?

    //Daily Cash Register Rec....
    @FormUrlEncoded
    @POST("dailycashRegisterr_rec")
    fun dailycashRegisterr_rec(@FieldMap params:Map<String,String>): Call<DailyCashRegisterResponse?>?

    //Daily Cash Register....
    @FormUrlEncoded
    @POST("dailycashRegistersummary")
    fun dailyCashSummary(@FieldMap params:Map<String,String>): Call<DailyCashRegisterResponse?>?

    //Daily Cash Register....
    @FormUrlEncoded
    @POST("dailycashRegistersummary_monthwise")
    fun dailyCashSummaryMonthlyWise(@FieldMap params:Map<String,String>): Call<DailyCashRegisterResponse?>?

    //Otp request....
    @FormUrlEncoded
    @POST("VerifypatientByOtp")
    fun verifyPatientByOtp(@FieldMap params:Map<String,String>): Call<OtpResponse?>?

    //Getlab_superadmin....
    @FormUrlEncoded
    @POST("Getlab_superadmin")
    fun getLabSuperAdmin(@FieldMap params:Map<String,String>): Call<LabNameResponse?>?

    @FormUrlEncoded
    @POST("Insertupdatedpayment")
    fun insertUpdatedPayment(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //It is for payment of patient registration invoice
    @FormUrlEncoded
    @POST("patientInvoice_New_API")
    fun patientInvoiceNewAPI(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //It is for collection center payment success..
    @FormUrlEncoded
    @POST("Insertupdateclientpaymentdetail")
    fun insertUpdateClientPaymentDetail(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    @FormUrlEncoded
    @POST("Insertupdatedadvance_service")
    fun insertUpdatedAdvanceService(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    @FormUrlEncoded
    @POST("Insertupdatedadvance")
    fun insertUpdatedAdvance(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    //Get all selected test of patient registration...ViewTestFragment.
    @FormUrlEncoded
    @POST("viewTestCode")
    fun viewTestCode(@FieldMap params:Map<String,String>): Call<GetTestCodeResponse?>?

    @FormUrlEncoded
    @POST("delete_image")
    fun deleteImage(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    @FormUrlEncoded
    @POST("UploadprofileImage")
    fun uploadProfileImage(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    @FormUrlEncoded
    @POST("Get_LabDetail")
    fun getLabDetail(@FieldMap params:Map<String,String>): Call<GetLabDetailsResponse?>?

    @FormUrlEncoded
    @POST("sendmsgbylab")
    fun sendMsgByLab(@FieldMap params:Map<String,String>): Call<CommonResponse?>?

    @FormUrlEncoded
    @POST("Get_servicename")
    fun getServiceName(@FieldMap params:Map<String,String>): Call<GetServiceResponse?>?

    @FormUrlEncoded
    @POST("Get_complain")
    fun getComplain(@FieldMap params:Map<String,String>): Call<GetComplainResponse?>?

    @FormUrlEncoded
    @POST("Get_diagnosis")
    fun getDiagnosis(@FieldMap params:Map<String,String>): Call<GetDiagnosisResponse?>?

    @FormUrlEncoded
    @POST("Get_PROV_diagnosis")
    fun getProvDiagnosis(@FieldMap params:Map<String,String>): Call<GetProvisionalDiagnosisResponse?>?

    @FormUrlEncoded
    @POST("Get_sysexam")
    fun getSysExams(@FieldMap params:Map<String,String>): Call<GetSysExamsResponse?>?


}
