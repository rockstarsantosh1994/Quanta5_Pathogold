package com.bms.pathogold_bms.services;

import android.text.Html;

import androidx.annotation.NonNull;

import com.bms.pathogold_bms.BuildConfig;
import com.bms.pathogold_bms.model.CommonResponse;
import com.bms.pathogold_bms.model.adddrug.AddDrugResponse;
import com.bms.pathogold_bms.model.all_labs_details.GetLabDetailsResponse;
import com.bms.pathogold_bms.model.dailycashregister.DailyCashRegisterResponse;
import com.bms.pathogold_bms.model.emrcheckboxlist.GetEMRCheckBoxListResponse;
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListResponse;
import com.bms.pathogold_bms.model.getclientuploadedfile.GetClientUploadedResponse;
import com.bms.pathogold_bms.model.getcomplain.GetComplainResponse;
import com.bms.pathogold_bms.model.getcomplaintsallergies.GetComplaintsAllergiesResponse;
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse;
import com.bms.pathogold_bms.model.getdiagnosis.GetDiagnosisResponse;
import com.bms.pathogold_bms.model.getdosemaster.GetDoseMasterResponse;
import com.bms.pathogold_bms.model.getdrug.GetDrugResponse;
import com.bms.pathogold_bms.model.getemrtreatment.GetEMRTreatmentResponse;
import com.bms.pathogold_bms.model.getpatient.GetPatientResponse;
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListResponse;
import com.bms.pathogold_bms.model.getpatientvital.GetPatientVitalResponse;
import com.bms.pathogold_bms.model.getprovisionaldiagnosis.GetProvisionalDiagnosisResponse;
import com.bms.pathogold_bms.model.getrefcc.GetRefCCResponse;
import com.bms.pathogold_bms.model.getslots.GetSlotsResponse;
import com.bms.pathogold_bms.model.getsysexams.GetSysExamsResponse;
import com.bms.pathogold_bms.model.getwalkdistance.GetWalkDistanceResponse;
import com.bms.pathogold_bms.model.lab_super_admin.LabNameResponse;
import com.bms.pathogold_bms.model.login.LoginResponse;
import com.bms.pathogold_bms.model.otp.OtpResponse;
import com.bms.pathogold_bms.model.patienttestlist.GetPatientTestListResponse;
import com.bms.pathogold_bms.model.report.ReportResponse;
import com.bms.pathogold_bms.model.reportdetails.ReportDetailsResponse;
import com.bms.pathogold_bms.model.service.GetServiceResponse;
import com.bms.pathogold_bms.model.testcode.GetTestCodeResponse;
import com.bms.pathogold_bms.model.vaccination.VaccinationResponse;
import com.bms.pathogold_bms.model.vaccination_patient.VaccinationPatientResponse;
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentResponse;
import com.bms.pathogold_bms.model.vitalsurgerlylist.VitalSurgeryListResponse;
import com.bms.pathogold_bms.utility.AllKeys;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRequestHelper {

    public interface OnRequestComplete {
        void onSuccess(Object object);

        void onFailure(String apiResponse);
    }


    private static ApiRequestHelper instance;
    private DigiPath application;
    private DigiPathServices digiPathServices;
    static Gson gson;

    public void login(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<LoginResponse> call = digiPathServices.login(params);
        get_login_api(onRequestComplete, call);
    }

    public void pateintLoginByMobile(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<LoginResponse> call = digiPathServices.pateintLoginByMobile(params);
        get_login_api(onRequestComplete, call);
    }

    private void get_login_api(final OnRequestComplete onRequestComplete, Call<LoginResponse> call) {
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getSlots(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetSlotsResponse> call = digiPathServices.getSlots(params);
        get_slots_api(onRequestComplete, call);
    }

    private void get_slots_api(final OnRequestComplete onRequestComplete, Call<GetSlotsResponse> call) {
        call.enqueue(new Callback<GetSlotsResponse>() {
            @Override
            public void onResponse(Call<GetSlotsResponse> call, Response<GetSlotsResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetSlotsResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void patientBookAppointment(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.patientBookAppointment(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void labBoyProfileUpdate(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.labBoyProfileUpdate(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void updateViewAppointment(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.updateViewAppointment(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void patientAppointmentConfirm(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.patientAppointmentConfirm(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void patientAppointmentCancel(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.patientAppointmentCancel(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void patientAppointmentComplete(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.patientAppointmentComplete(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void uploadLabClientImage(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.uploadLabClientImage(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void contactInformationForCCNewAPI(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.contactinformationforccNewApi(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void patientAppointmentCancelAndReschedule(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.patientAppointmentCancelAndReschedule(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertVitalDetailsofpatient(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertVitalDetailsofpatient(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertVitalAllergies(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertVitalAllergies(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertVitalHistory(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertVitalHistory(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertEMRTreatmentDetailsofPaitent(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertEMRTreatmentDetailsofPaitent(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertWalkDistanceOfCollectionBoy(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertWalkDistanceOfCollectionBoy(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    private void get_patient_book_appointment_api(final OnRequestComplete onRequestComplete, Call<CommonResponse> call) {
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getPatientByMobileNo(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetPatientResponse> call = digiPathServices.getPatientByMobileNo(params);
        get_patient_by_mobile_api(onRequestComplete, call);
    }

    private void get_patient_by_mobile_api(final OnRequestComplete onRequestComplete, Call<GetPatientResponse> call) {
        call.enqueue(new Callback<GetPatientResponse>() {
            @Override
            public void onResponse(Call<GetPatientResponse> call, Response<GetPatientResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetPatientResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void viewAppointmentAccepted(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<ViewAppointmentResponse> call = digiPathServices.viewAppointmentAccepted(params);
        get_view_appointment_accepted_api(onRequestComplete, call);
    }

    public void viewAppointmentAll(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<ViewAppointmentResponse> call = digiPathServices.viewAppointmentAll(params);
        get_view_appointment_accepted_api(onRequestComplete, call);
    }

    public void viewAppointmentPending(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<ViewAppointmentResponse> call = digiPathServices.viewAppointmentPending(params);
        get_view_appointment_accepted_api(onRequestComplete, call);
    }

    private void get_view_appointment_accepted_api(final OnRequestComplete onRequestComplete, Call<ViewAppointmentResponse> call) {
        call.enqueue(new Callback<ViewAppointmentResponse>() {
            @Override
            public void onResponse(Call<ViewAppointmentResponse> call, Response<ViewAppointmentResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ViewAppointmentResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getPhleboList(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<ConsultationResponse> call = digiPathServices.getPhleboList(params);
        get_consultation_list_api(onRequestComplete, call);
    }

    public void getConsultantList(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<ConsultationResponse> call = digiPathServices.getConsultantList(params);
        get_consultation_list_api(onRequestComplete, call);
    }

    private void get_consultation_list_api(final OnRequestComplete onRequestComplete, Call<ConsultationResponse> call) {
        call.enqueue(new Callback<ConsultationResponse>() {
            @Override
            public void onResponse(Call<ConsultationResponse> call, Response<ConsultationResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ConsultationResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getPatientList(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetPatientListResponse> call = digiPathServices.getPatientList(params);
        get_patient_list_api(onRequestComplete, call);
    }

    private void get_patient_list_api(final OnRequestComplete onRequestComplete, Call<GetPatientListResponse> call) {
        call.enqueue(new Callback<GetPatientListResponse>() {
            @Override
            public void onResponse(Call<GetPatientListResponse> call, Response<GetPatientListResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetPatientListResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getPatientTestList(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetPatientTestListResponse> call = digiPathServices.getPatientTestList(params);
        get_patient_test_list_api(onRequestComplete, call);
    }


    private void get_patient_test_list_api(final OnRequestComplete onRequestComplete, Call<GetPatientTestListResponse> call) {
        call.enqueue(new Callback<GetPatientTestListResponse>() {
            @Override
            public void onResponse(Call<GetPatientTestListResponse> call, Response<GetPatientTestListResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetPatientTestListResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getTestCode(Map<String, String> params,final OnRequestComplete onRequestComplete) {
        Call<GetTestCodeResponse> call = digiPathServices.getTestCode(params);
        get_test_code_api(onRequestComplete, call);
    }

    public void viewTestCode(Map<String, String> params,final OnRequestComplete onRequestComplete) {
        Call<GetTestCodeResponse> call = digiPathServices.viewTestCode(params);
        get_test_code_api(onRequestComplete, call);
    }

    private void get_test_code_api(final OnRequestComplete onRequestComplete, Call<GetTestCodeResponse> call) {
        call.enqueue(new Callback<GetTestCodeResponse>() {
            @Override
            public void onResponse(Call<GetTestCodeResponse> call, Response<GetTestCodeResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetTestCodeResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getClientUploadedFile(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetClientUploadedResponse> call = digiPathServices.getClientUploadedFile(params);
        get_client_uploaded_api(onRequestComplete, call);
    }

    private void get_client_uploaded_api(final OnRequestComplete onRequestComplete, Call<GetClientUploadedResponse> call) {
        call.enqueue(new Callback<GetClientUploadedResponse>() {
            @Override
            public void onResponse(Call<GetClientUploadedResponse> call, Response<GetClientUploadedResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetClientUploadedResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getDrugsList( final OnRequestComplete onRequestComplete) {
        Call<GetDrugResponse> call = digiPathServices.getDrugList();
        get_drugs_list_api(onRequestComplete, call);
    }

    private void get_drugs_list_api(final OnRequestComplete onRequestComplete, Call<GetDrugResponse> call) {
        call.enqueue(new Callback<GetDrugResponse>() {
            @Override
            public void onResponse(Call<GetDrugResponse> call, Response<GetDrugResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetDrugResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getDoseMasterlist( final OnRequestComplete onRequestComplete) {
        Call<GetDoseMasterResponse> call = digiPathServices.getDoseMasterlist();
        get_dose_master_api(onRequestComplete, call);
    }

    private void get_dose_master_api(final OnRequestComplete onRequestComplete, Call<GetDoseMasterResponse> call) {
        call.enqueue(new Callback<GetDoseMasterResponse>() {
            @Override
            public void onResponse(Call<GetDoseMasterResponse> call, Response<GetDoseMasterResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetDoseMasterResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getEMRCheckboxList( final OnRequestComplete onRequestComplete) {
        Call<GetEMRCheckBoxListResponse> call = digiPathServices.getEMRCheckboxList();
        get_emr_checkbox_lis_api(onRequestComplete, call);
    }

    private void get_emr_checkbox_lis_api(final OnRequestComplete onRequestComplete, Call<GetEMRCheckBoxListResponse> call) {
        call.enqueue(new Callback<GetEMRCheckBoxListResponse>() {
            @Override
            public void onResponse(Call<GetEMRCheckBoxListResponse> call, Response<GetEMRCheckBoxListResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetEMRCheckBoxListResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getAllergyList( final OnRequestComplete onRequestComplete) {
        Call<GetAllergyListResponse> call = digiPathServices.getAllergyList();
        get_allergy_list_api(onRequestComplete, call);
    }

    private void get_allergy_list_api(final OnRequestComplete onRequestComplete, Call<GetAllergyListResponse> call) {
        call.enqueue(new Callback<GetAllergyListResponse>() {
            @Override
            public void onResponse(Call<GetAllergyListResponse> call, Response<GetAllergyListResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetAllergyListResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getVitalSurgeryList( Map<String, String> params,final OnRequestComplete onRequestComplete) {
        Call<VitalSurgeryListResponse> call = digiPathServices.getVitalSurgeryList(params);
        get_vital_surgery_api(onRequestComplete, call);
    }

    private void get_vital_surgery_api(final OnRequestComplete onRequestComplete, Call<VitalSurgeryListResponse> call) {
        call.enqueue(new Callback<VitalSurgeryListResponse>() {
            @Override
            public void onResponse(Call<VitalSurgeryListResponse> call, Response<VitalSurgeryListResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<VitalSurgeryListResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getReportDownload(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<ReportResponse> call = digiPathServices.getReportDownload(params);
        get_reports_download_api(onRequestComplete, call);
    }

    private void get_reports_download_api(final OnRequestComplete onRequestComplete, Call<ReportResponse> call) {
        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getPatientVitalDetails(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetPatientVitalResponse> call = digiPathServices.getPatientVitalDetails(params);
        get_patient_vital_details_api(onRequestComplete, call);
    }

    private void get_patient_vital_details_api(final OnRequestComplete onRequestComplete, Call<GetPatientVitalResponse> call) {
        call.enqueue(new Callback<GetPatientVitalResponse>() {
            @Override
            public void onResponse(Call<GetPatientVitalResponse> call, Response<GetPatientVitalResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetPatientVitalResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getEMRTreatmentlist(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetEMRTreatmentResponse> call = digiPathServices.getEMRTreatmentlist(params);
        get_emr_treatement_api(onRequestComplete, call);
    }

    private void get_emr_treatement_api(final OnRequestComplete onRequestComplete, Call<GetEMRTreatmentResponse> call) {
        call.enqueue(new Callback<GetEMRTreatmentResponse>() {
            @Override
            public void onResponse(Call<GetEMRTreatmentResponse> call, Response<GetEMRTreatmentResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetEMRTreatmentResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getDistanceSheetOfCollectionBoy(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetWalkDistanceResponse> call = digiPathServices.getDistanceSheetOfCollectionBoy(params);
        get_distance_sheet_api(onRequestComplete, call);
    }

    private void get_distance_sheet_api(final OnRequestComplete onRequestComplete, Call<GetWalkDistanceResponse> call) {
        call.enqueue(new Callback<GetWalkDistanceResponse>() {
            @Override
            public void onResponse(Call<GetWalkDistanceResponse> call, Response<GetWalkDistanceResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetWalkDistanceResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getComplaintsExaminationHistoryAllergiesVitalPatientDetails(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetComplaintsAllergiesResponse> call = digiPathServices.getComplaintsExaminationHistoryAllergiesVitalPatientDetails(params);
        get_complaints_allergies_api(onRequestComplete, call);
    }

    private void get_complaints_allergies_api(final OnRequestComplete onRequestComplete, Call<GetComplaintsAllergiesResponse> call) {
        call.enqueue(new Callback<GetComplaintsAllergiesResponse>() {
            @Override
            public void onResponse(Call<GetComplaintsAllergiesResponse> call, Response<GetComplaintsAllergiesResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetComplaintsAllergiesResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getTestResultdetails(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<ReportDetailsResponse> call = digiPathServices.getTestResultdetails(params);
        get_reports_details_api(onRequestComplete, call);
    }

    private void get_reports_details_api(final OnRequestComplete onRequestComplete, Call<ReportDetailsResponse> call) {
        call.enqueue(new Callback<ReportDetailsResponse>() {
            @Override
            public void onResponse(Call<ReportDetailsResponse> call, Response<ReportDetailsResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReportDetailsResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getVaccineList(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<VaccinationResponse> call = digiPathServices.getVaccineList(params);
        get_view_vaccination_api(onRequestComplete, call);
    }

    private void get_view_vaccination_api(final OnRequestComplete onRequestComplete, Call<VaccinationResponse> call) {
        call.enqueue(new Callback<VaccinationResponse>() {
            @Override
            public void onResponse(Call<VaccinationResponse> call, Response<VaccinationResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<VaccinationResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void insertupdatevaccinemaster(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertupdatevaccinemaster(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertUpdateVaccinationPatient(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertUpdateVaccinationPatient(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertupdatedrugmaster(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<AddDrugResponse> call = digiPathServices.insertupdatedrugmaster(params);
        get_add_drug_response_api(onRequestComplete, call);
    }

    public void patientAppointmentRemainder(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.patientAppointmentRemainder(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertUpdatedPayment(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertUpdatedPayment(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void patientInvoiceNewAPI(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.patientInvoiceNewAPI(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertUpdatedAdvanceService(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertUpdatedAdvanceService(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertUpdateClientPaymentDetail(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertUpdateClientPaymentDetail(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void insertUpdatedAdvance(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.insertUpdatedAdvance(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void deleteImage(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.deleteImage(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void uploadProfileImage(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.uploadProfileImage(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    public void sendMsgByLab(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<CommonResponse> call = digiPathServices.sendMsgByLab(params);
        get_patient_book_appointment_api(onRequestComplete, call);
    }

    private void get_add_drug_response_api(final OnRequestComplete onRequestComplete, Call<AddDrugResponse> call) {
        call.enqueue(new Callback<AddDrugResponse>() {
            @Override
            public void onResponse(Call<AddDrugResponse> call, Response<AddDrugResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddDrugResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getVaccineList_patient(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<VaccinationPatientResponse> call = digiPathServices.getVaccineList_patient(params);
        get_vaccine_list_patient_api(onRequestComplete, call);
    }

    private void get_vaccine_list_patient_api(final OnRequestComplete onRequestComplete, Call<VaccinationPatientResponse> call) {
        call.enqueue(new Callback<VaccinationPatientResponse>() {
            @Override
            public void onResponse(Call<VaccinationPatientResponse> call, Response<VaccinationPatientResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<VaccinationPatientResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getref_cc(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetRefCCResponse> call = digiPathServices.getref_cc(params);
        getref_cc_api(onRequestComplete, call);
    }

    private void getref_cc_api(final OnRequestComplete onRequestComplete, Call<GetRefCCResponse> call) {
        call.enqueue(new Callback<GetRefCCResponse>() {
            @Override
            public void onResponse(Call<GetRefCCResponse> call, Response<GetRefCCResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetRefCCResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void dailyCashRegister(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<DailyCashRegisterResponse> call = digiPathServices.dailyCashRegister(params);
        get_daily_cash_register_api(onRequestComplete, call);
    }

    public void dailycashRegisterr_rec(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<DailyCashRegisterResponse> call = digiPathServices.dailycashRegisterr_rec(params);
        get_daily_cash_register_api(onRequestComplete, call);
    }


    private void get_daily_cash_register_api(final OnRequestComplete onRequestComplete, Call<DailyCashRegisterResponse> call) {
        call.enqueue(new Callback<DailyCashRegisterResponse>() {
            @Override
            public void onResponse(Call<DailyCashRegisterResponse> call, Response<DailyCashRegisterResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DailyCashRegisterResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void dailyCashSummary(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<DailyCashRegisterResponse> call = digiPathServices.dailyCashSummary(params);
        get_daily_cash_register_api(onRequestComplete, call);
    }

    public void dailyCashSummaryMonthlyWise(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<DailyCashRegisterResponse> call = digiPathServices.dailyCashSummaryMonthlyWise(params);
        get_daily_cash_register_api(onRequestComplete, call);
    }

    public void verifyPatientByOtp(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<OtpResponse> call = digiPathServices.verifyPatientByOtp(params);
        get_verifiy_patient_by_otp_api(onRequestComplete, call);
    }

    private void get_verifiy_patient_by_otp_api(final OnRequestComplete onRequestComplete, Call<OtpResponse> call) {
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getLabSuperAdmin(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<LabNameResponse> call = digiPathServices.getLabSuperAdmin(params);
        get_lab_name_response_api(onRequestComplete, call);
    }

    private void get_lab_name_response_api(final OnRequestComplete onRequestComplete, Call<LabNameResponse> call) {
        call.enqueue(new Callback<LabNameResponse>() {
            @Override
            public void onResponse(Call<LabNameResponse> call, Response<LabNameResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LabNameResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getLabDetail(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetLabDetailsResponse> call = digiPathServices.getLabDetail(params);
        get_lab_details_api(onRequestComplete, call);
    }

    private void get_lab_details_api(final OnRequestComplete onRequestComplete, Call<GetLabDetailsResponse> call) {
        call.enqueue(new Callback<GetLabDetailsResponse>() {
            @Override
            public void onResponse(Call<GetLabDetailsResponse> call, Response<GetLabDetailsResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetLabDetailsResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getServiceName(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetServiceResponse> call = digiPathServices.getServiceName(params);
        get_service_response_api(onRequestComplete, call);
    }

    private void get_service_response_api(final OnRequestComplete onRequestComplete, @NonNull Call<GetServiceResponse> call) {
        call.enqueue(new Callback<GetServiceResponse>() {
            @Override
            public void onResponse(Call<GetServiceResponse> call, Response<GetServiceResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetServiceResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getComplain(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetComplainResponse> call = digiPathServices.getComplain(params);
        get_complains_api(onRequestComplete, call);
    }

    private void get_complains_api(final OnRequestComplete onRequestComplete, @NonNull Call<GetComplainResponse> call) {
        call.enqueue(new Callback<GetComplainResponse>() {
            @Override
            public void onResponse(Call<GetComplainResponse> call, Response<GetComplainResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetComplainResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getDiagnosis(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetDiagnosisResponse> call = digiPathServices.getDiagnosis(params);
        get_diagnosis_api(onRequestComplete, call);
    }

    private void get_diagnosis_api(final OnRequestComplete onRequestComplete, @NonNull Call<GetDiagnosisResponse> call) {
        call.enqueue(new Callback<GetDiagnosisResponse>() {
            @Override
            public void onResponse(Call<GetDiagnosisResponse> call, Response<GetDiagnosisResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetDiagnosisResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getProvDiagnosis(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetProvisionalDiagnosisResponse> call = digiPathServices.getProvDiagnosis(params);
        get_prov_diagnosis_api(onRequestComplete, call);
    }

    private void get_prov_diagnosis_api(final OnRequestComplete onRequestComplete, @NonNull Call<GetProvisionalDiagnosisResponse> call) {
        call.enqueue(new Callback<GetProvisionalDiagnosisResponse>() {
            @Override
            public void onResponse(Call<GetProvisionalDiagnosisResponse> call, Response<GetProvisionalDiagnosisResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetProvisionalDiagnosisResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public void getSysExams(Map<String, String> params, final OnRequestComplete onRequestComplete) {
        Call<GetSysExamsResponse> call = digiPathServices.getSysExams(params);
        get_sys_exams_api(onRequestComplete, call);
    }

    private void get_sys_exams_api(final OnRequestComplete onRequestComplete, @NonNull Call<GetSysExamsResponse> call) {
        call.enqueue(new Callback<GetSysExamsResponse>() {
            @Override
            public void onResponse(Call<GetSysExamsResponse> call, Response<GetSysExamsResponse> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                } else {
                    try {
                        onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()) + "");
                    } catch (IOException e) {
                        onRequestComplete.onFailure(AllKeys.Companion.getUNPROPER_RESPONSE());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetSysExamsResponse> call, Throwable t) {
                handle_fail_response(t, onRequestComplete);
            }
        });
    }

    public static synchronized ApiRequestHelper init(DigiPath application) {
        if (null == instance) {
            instance = new ApiRequestHelper();
            instance.setApplication(application);

            gson = new GsonBuilder()
                    .setLenient()
                    .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
//                    .setExclusionStrategies(new ExclusionStrategy() {
//                        @Override
//                        public boolean shouldSkipField(FieldAttributes f) {
//                            return f.getDeclaringClass().equals(RealmObject.class);
//                        }
//
//                        @Override
//                        public boolean shouldSkipClass(Class<?> clazz) {
//                            return false;
//                        }
//                    })
                    .create();
            instance.createRestAdapter();
        }
        return instance;
    }

    private void handle_fail_response(Throwable t, OnRequestComplete onRequestComplete) {
        if (t.getMessage() != null) {
            if (t.getMessage().contains("Unable to resolve host")) {
                onRequestComplete.onFailure("No Internet Connection!");
            } else {
                onRequestComplete.onFailure(Html.fromHtml(t.getLocalizedMessage()) + "");
            }
        } else
            onRequestComplete.onFailure("Something went wrong. Please, Try gain later.");
    }

    private static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }

    public static class StringAdapter extends TypeAdapter<String> {
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }

    /**
     * REST Adapter Configuration
     */
    private void createRestAdapter() {
//        ObjectMapper objectMapper = new ObjectMapper();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.writeTimeout(5, TimeUnit.MINUTES);
        httpClient.readTimeout(5, TimeUnit.MINUTES);
        httpClient.connectTimeout(5, TimeUnit.MINUTES);
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.interceptors().add(logging);

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(BuildConfig.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.client(httpClient.build()).build();
        digiPathServices = retrofit.create(DigiPathServices.class);
    }

    /**
     * End REST Adapter Configuration
     */

    public DigiPath getApplication() {
        return application;
    }

    public void setApplication(DigiPath application) {
        this.application = application;
    }

}
