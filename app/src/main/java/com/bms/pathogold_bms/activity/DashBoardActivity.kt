package com.bms.pathogold_bms.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.bms.pathogold_bms.BaseActivity
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.fragment.CheckSlotFragment
import com.bms.pathogold_bms.model.CommonResponse
import com.bms.pathogold_bms.model.chat.FireBaseBO
import com.bms.pathogold_bms.model.emrcheckboxlist.GetEMRCheckBoxListBO
import com.bms.pathogold_bms.model.emrcheckboxlist.GetEMRCheckBoxListResponse
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListBO
import com.bms.pathogold_bms.model.getallergylist.GetAllergyListResponse
import com.bms.pathogold_bms.model.getcomplain.GetComplainBO
import com.bms.pathogold_bms.model.getcomplain.GetComplainResponse
import com.bms.pathogold_bms.model.getconsultation.ConsultationResponse
import com.bms.pathogold_bms.model.getdiagnosis.GetDiagnosisBO
import com.bms.pathogold_bms.model.getdiagnosis.GetDiagnosisResponse
import com.bms.pathogold_bms.model.getdosemaster.GetDoseMasterResponse
import com.bms.pathogold_bms.model.getdrug.GetDrugResponse
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.getprovisionaldiagnosis.GetProvDiagnosisBO
import com.bms.pathogold_bms.model.getprovisionaldiagnosis.GetProvisionalDiagnosisResponse
import com.bms.pathogold_bms.model.getrefcc.GetRefCCBO
import com.bms.pathogold_bms.model.getrefcc.GetRefCCResponse
import com.bms.pathogold_bms.model.getsysexams.GetSysExamsBO
import com.bms.pathogold_bms.model.getsysexams.GetSysExamsResponse
import com.bms.pathogold_bms.model.lab_super_admin.LabNameBO
import com.bms.pathogold_bms.model.lab_super_admin.LabNameResponse
import com.bms.pathogold_bms.model.login.LoginBO
import com.bms.pathogold_bms.model.login.LoginResponse
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeResponse
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import com.bms.pathogold_bms.model.vitalsurgerlylist.VitalSurgeryListBO
import com.bms.pathogold_bms.model.vitalsurgerlylist.VitalSurgeryListResponse
import com.bms.pathogold_bms.services.ApiRequestHelper
import com.bms.pathogold_bms.services.LocationService
import com.bms.pathogold_bms.services.PatientAppointmentReminderBroadcast
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods
import com.bms.pathogold_bms.utility.ConfigUrl
import com.bms.pathogold_bms.view_models.GetAllPatientViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import de.hdodenhof.circleimageview.CircleImageView
import io.paperdb.Paper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DashBoardActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    var toolbar: Toolbar? = null

    private val PERMISSION_ID = 21

    private val TAG = "DashBoardActivity2"
    var drawer: DrawerLayout? = null

    private val getAllergyListMap: MutableMap<String, ArrayList<GetAllergyListBO>> = LinkedHashMap()
    private val getSysExamsMap: MutableMap<String, ArrayList<GetSysExamsBO>> = LinkedHashMap()
    private var allergyTemp: ArrayList<GetAllergyListBO>? = null
    private var sysExamsTemp: ArrayList<GetSysExamsBO>? = null
    private var getRefCCList: ArrayList<GetRefCCBO>? = ArrayList()
    private var getRefDrList: ArrayList<GetRefCCBO>? = ArrayList()
    private var getLabNameArrayList = ArrayList<LabNameBO>()

    lateinit var navController: NavController


    //GetAllPatientViewModel...
    private lateinit var getAllPatientViewModel: GetAllPatientViewModel

    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 101
    private val GALLERY = 102
    private var mCurrentPhotoPath: String? = null
    private var profilePicBase64String: String? = null
    private var profilePicBitMap: Bitmap? = null

    //ImageView declaration...
    private var ivProfilePic: CircleImageView? = null

    private var firebaseFirestore: FirebaseFirestore? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()

        //basic intialisation..
        initViews()

        getAllPatientViewModel = ViewModelProvider(this@DashBoardActivity)[GetAllPatientViewModel::class.java] // init view model

        // Initialize the Update Manager with the Activity and the Update Mode
        //mUpdateManager = UpdateManager.Builder(this).mode(UpdateManagerConstant.FLEXIBLE)
        // Call start() to check for updates and install them
        //mUpdateManager?.start()

        //Getting the Navigation Controller
        navController = Navigation.findNavController(this, R.id.fragment)
        val navGraph: NavGraph = navController.navInflater.inflate(R.navigation.navigation_graph)

        //autologout api..
        if(CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Phlebotomist) ||
            CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Administrator) ||
            CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)){

            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todaysDate = df.format(c)

            // Format of sharedpref date is CommonMethods.getPrefrence(mContext,AllKeys.TODAYS_DATE)"yyyy-MM-dd"
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.TODAYS_DATE).equals(todaysDate, false)) {
                //do nothing api are hit already.....
                loadAndSaveDefaultData(navGraph, AllKeys.ACTIVE)
            } else {
                if(CommonMethods.getPrefrence(mContext!!,AllKeys.LOGIN_VIA).equals(AllKeys.VIA_USERNAME_PASSWORD)){
                    autoLogoutChecking(navGraph)
                }else if(CommonMethods.getPrefrence(mContext!!,AllKeys.LOGIN_VIA).equals(AllKeys.VIA_OTP)){
                    autoLogoutCheckingViaOtp(navGraph)
                }

            }
        }else{
            loadAndSaveDefaultData(navGraph, AllKeys.ACTIVE)
        }

        /*mUpdateManager!!.addUpdateInfoListener(object : UpdateInfoListener {
            override fun onReceiveVersionCode(code: Int) {
                // You can get the available version code of the apk in Google Play
                // Do something here
                if(BuildConfig.VERSION_CODE==code){
                    Toast.makeText(mContext!!, "App is uptodate!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(mContext!!, "Please update the app!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onReceiveStalenessDays(days: Int) {
                // Number of days passed since the user was notified of an update through the Google Play
                // If the user hasn't notified this will return -1 as days
                // You can decide the type of update you want to call
            }
        })*/
    }

    //Setting Up the back button
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    override val activityLayout: Int
        get() = R.layout.activity_dash_board2

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        toolbar?.textAlignment = TEXT_ALIGNMENT_CENTER
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawerLayout)
        val toggle = ActionBarDrawerToggle(this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)

        val navigationView = findViewById<NavigationView>(R.id.navView)
        navigationView.menu.clear()

        //Set Menu Dynamically Based On User Login...
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Patient)) {
            navigationView.inflateMenu(R.menu.patient_menu_drawer)
        } else if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Phlebotomist)
        ) {
            navigationView.inflateMenu(R.menu.phelbo_menu_drawer)
        } else if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Lab_Technician) || CommonMethods.getPrefrence(mContext!!,
                AllKeys.USERTYPE).equals(AllKeys.Administrator)
        ) {
            navigationView.inflateMenu(R.menu.lab_menu_drawer)
        } else if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Consultant)
        ) {
            navigationView.inflateMenu(R.menu.consultant_menu_drawer)
        } else if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.SuperAdmin)
        ) {
            navigationView.inflateMenu(R.menu.super_admin_menu_drawer)
        } else if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Reference_Doctor)
        ) {
            navigationView.inflateMenu(R.menu.reference_dr_menu_drawer)
        } else if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Support)
        ) {
            navigationView.inflateMenu(R.menu.support_menu_drawer)
        } else if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.CollectionCenter)
        ) {
            navigationView.inflateMenu(R.menu.collection_center_menu_drawer)
        }

        navigationView.setNavigationItemSelectedListener(this)

        val header: View = navigationView.getHeaderView(0)
        val tvName = header.findViewById<View>(R.id.tvName) as TextView
        ivProfilePic = header.findViewById<View>(R.id.iv_profile_pic) as CircleImageView?

        tvName.text =CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)+"\n Lab:- "+CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME_C)

        val profileId =
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
                CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID).toString()
            } else {
                CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()
            }
        Glide.with(mContext!!).load(CommonMethods.getPrefrence(mContext!!, AllKeys.PATH).toString())
            //.placeholder(R.drawable.ic_user)
            .into(ivProfilePic!!)
        ivProfilePic?.setOnClickListener {
            showPictureDialog()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Support)) {
                    toolbar?.title = CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)
                    navController.navigate(R.id.chatFragment)
                } else {
                    toolbar?.title = CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)
                    navController.navigate(R.id.dashBoardFragment2)
                }
                //clear all backstack fragment...
                //navController.popBackStack()
            }

            R.id.nav_todays_patient -> {
                toolbar?.title = CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)
                navController.navigate(R.id.dashBoardFragment2)

                //  navController.popBackStack()
            }

            R.id.nav_all_patient -> {
                getAllPatientViewModel.list.value!!.clear()
                if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
                    val bundle = Bundle()
                    bundle.putString("launch_type", AllKeys.IS_PATIENT_LIST_LAUNCH)
                    bundle.putParcelableArrayList("get_lab_name", getLabNameArrayList)
                    toolbar?.title = resources.getString(R.string.all_patient)
                    navController.navigate(R.id.getPatientListFragment, bundle)
                } else {
                    val bundle = Bundle()
                    bundle.putString("launch_type", AllKeys.IS_PATIENT_LIST_LAUNCH)
                    toolbar?.title = resources.getString(R.string.all_patient)
                    navController.navigate(R.id.getPatientListFragment, bundle)
                }
            }

            R.id.nav_recharge->{
                if(CommonMethods.isNetworkAvailable(mContext!!)){
                    checkBalanceForCollectonCenter()
                }else{
                    CommonMethods.showDialogForError(mContext!!,AllKeys.NO_INTERNET_AVAILABLE)
                }
            }

           /* R.id.nav_services -> {
                toolbar?.title = resources.getString(R.string.services)
                navController.navigate(R.id.serviceFragment)
            }

            R.id.nav_offers -> {
                CommonMethods.showDialogForError(mContext!!, "Comming soon..")
            }*/

            R.id.nav_my_profile -> {
                toolbar?.title = resources.getString(R.string.my_profile)
                navController.navigate(R.id.myProfileEditFragment)
            }

            R.id.nav_patient_registration -> {
                if (CommonMethods.getPrefrence(mContext!!, AllKeys.patientregistration)
                        .equals("true", true)
                ) {
                    toolbar?.title = resources.getString(R.string.patient_registration)
                    navController.navigate(R.id.patientRegistrationFragment)
                } else {
                    //don't have access...to this usertype
                    CommonMethods.showDialogForError(mContext!!, AllKeys.NOT_AVAILABLE)
                }
            }

            R.id.nav_appointments -> {
                val bundle = Bundle()
                bundle.putString("app_type", "new")
                toolbar?.title = resources.getString(R.string.view_slot)
                navController.navigate(R.id.checkSlotFragment2, bundle)
            }

            R.id.nav_call_to_doctor -> {
                toolbar?.title = resources.getString(R.string.call_to_dr)
                navController.navigate(R.id.callToDoctorFragment)
            }

            R.id.nav_call_to_patient -> {
                getAllPatientViewModel.list.value!!.clear()

                val bundle = Bundle()
                bundle.putString("launch_type", AllKeys.IS_VIDEO_CALL_LAUNCH)
                toolbar?.title = resources.getString(R.string.all_patient)
                navController.navigate(R.id.getPatientListFragment, bundle)

            }

            R.id.nav_chat_ -> {
                toolbar?.title = resources.getString(R.string.app_name)
                navController.popBackStack(R.id.chatDashBoardFragment, inclusive = true)
                navController.navigate(R.id.chatDashBoardFragment)
            }

            R.id.nav_daily_cash -> {
                if (CommonMethods.getPrefrence(mContext!!, AllKeys.dailycashregister).equals("true", true)) {
                    toolbar?.title = resources.getString(R.string.daily_cash_register)
                    if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
                        navController.navigate(R.id.dailyCashDashBoardFragment2)
                    } else {
                        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
                        when {
                            drawer.isDrawerOpen(GravityCompat.START) -> {
                                drawer.closeDrawer(GravityCompat.START)
                            }
                            drawer.isDrawerOpen(GravityCompat.END) -> {
                                drawer.closeDrawer(GravityCompat.END)
                            }
                        }
                        val intent = Intent(this@DashBoardActivity, DashBoardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                    //navController.navigate(R.id.dailyCashDashBoardFragment2)
                    //navController.popBackStack()
                    //navController.currentDestination
                } else {
                    CommonMethods.showDialogForError(mContext!!, "Not available.")
                }
            }

            /*R.id.nav_support -> {
                if (CommonMethods.getPrefrence(mContext!!, AllKeys.SUPPORT_TOKEN)
                        .equals(AllKeys.DNF)
                ) {
                    CommonMethods.showDialogForError(mContext!!, "Support is not available!")
                } else {
                    val fireBaseBO = FireBaseBO(
                        "BMS Support",
                        "",
                        CommonMethods.getPrefrence(mContext!!, AllKeys.SUPPORT_ID),//support id
                        "Offline",
                        CommonMethods.getPrefrence(mContext!!,
                            AllKeys.SUPPORT_TOKEN),//support token
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        CommonMethods.getPrefrence(mContext!!,
                            AllKeys.SUPPORT_TOKEN),//support token
                        "none")

                    //Store user recent chat to in database to show recent chat as per user
                    sendDataToCloudStore(fireBaseBO)

                    val intent = Intent(this, SpecificChatActivity::class.java)
                    intent.putExtra("name", "Support")
                    intent.putExtra("receiveruid",
                        CommonMethods.getPrefrence(mContext!!, AllKeys.SUPPORT_ID))
                    intent.putExtra("imageuri", "")
                    intent.putExtra("token",
                        CommonMethods.getPrefrence(mContext!!, AllKeys.SUPPORT_TOKEN))
                    intent.putExtra("firebase_bo", fireBaseBO)
                    startActivity(intent)
                }
            }*/

            R.id.nav_view_appintment -> {
                toolbar?.title = resources.getString(R.string.view_appointment)
                navController.navigate(R.id.viewAppointmentFragment)
            }

            R.id.nav_terms_and_condition -> {
                val intent = Intent(mContext, WebviewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("url", "https://birlamedisoft.com/privacy.html")
                intent.putExtra("patient_name", "Privacy Policy")
                startActivity(intent)
            }

            R.id.nav_my_distance -> {
                CommonMethods.showDialogForError(mContext!!, "Feature not available!")
                /*if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Phlebotomist)) {
                    navController.navigate(R.id.myDistanceFragment)
                } else {
                    CommonMethods.showDialogForError(mContext!!, "Feature not available!")
                }*/
            }

            R.id.nav_end_job -> {
                /*  if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                          .equals(AllKeys.Phlebotomist)
                  ) {
                      val mDialog = MaterialDialog.Builder(this@DashBoardActivity)
                          .setTitle("Status")
                          .setMessage("Are you sure want to end job?")
                          .setCancelable(false)
                          .setPositiveButton("Yes") { dialogInterface: DialogInterface, which: Int ->
                              //End Day....
                              if (CommonMethods.isNetworkAvailable(mContext!!)) {
                                  insertWalkDistanceOfLabBoy()
                                  dialogInterface.dismiss()
                              } else {
                                  dialogInterface.dismiss()
                                  CommonMethods.showDialogForError(mContext!!,
                                      AllKeys.NO_INTERNET_AVAILABLE)
                              }
                          }
                          .setNegativeButton("No") { dialogInterface: DialogInterface, which: Int ->
                              // Delete Operation
                              dialogInterface.dismiss()
                          }
                          .build()

                      // Show Dialog
                      mDialog.show()
                  } else {
                      CommonMethods.showDialogForError(mContext!!, "Feature not available!")
                  }*/
                CommonMethods.showDialogForError(mContext!!, "Feature not available!")
            }

            R.id.nav_add_view_vaccination -> {
                navController.navigate(R.id.viewVaccinationFragment)
            }

            R.id.nav_feedback -> {
                try {
                    val appStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}"))
                    appStoreIntent.setPackage("com.android.vending")
                    startActivity(appStoreIntent)
                } catch (exception: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
                }
            }

            R.id.nav_about_us -> {
                try {
                    val appStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}"))
                    appStoreIntent.setPackage("com.android.vending")
                    startActivity(appStoreIntent)
                } catch (exception: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
                }
            }

            R.id.nav_logout -> {
                val mDialog = MaterialDialog.Builder(this@DashBoardActivity)
                    .setTitle("Warning")
                    .setMessage("Are you sure want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialogInterface: DialogInterface, which: Int ->
                        dialogInterface.dismiss()
                        logoutUser()
                    }
                    .setNegativeButton("No") { dialogInterface: DialogInterface, which: Int ->
                        // Delete Operation
                        dialogInterface.dismiss()
                    }
                    .build()

                // Show Dialog
                mDialog.show()
            }
        }
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }

    private fun autoLogoutChecking(navGraph: NavGraph) {
        val params: MutableMap<String, String> = HashMap()

        params["UserId"] = CommonMethods.getPrefrence(mContext!!, AllKeys.ET_USERNAME).toString()
        params["Password"] = CommonMethods.getPrefrence(mContext!!, AllKeys.ET_PASSWORD).toString()
        params["Token"] = CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN).toString()
        params["usertype"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).toString()

        Log.e(TAG, "login: $params")

        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Checkin Status...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.login(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val loginResponse = `object` as LoginResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $loginResponse")
                if (loginResponse.ResponseCode == 200) {
                    //If 200 response code then it will hit all required api's...
                    if(loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()) == AllKeys.ACTIVE
                        || loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()) == AllKeys.SUCCESS){
                        //logout the user..user is inactive.
                        loadAndSaveDefaultData(navGraph,loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()))
                    }else if(loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()) == AllKeys.INACTIVE &&
                        CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)){
                        loadAndSaveDefaultData(navGraph,loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()))
                    }else{
                        logoutUser()
                    }
                }else{
                    logoutUser()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                CommonMethods.showDialogForError(mContext!!,AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun autoLogoutCheckingViaOtp(navGraph: NavGraph) {
        val params: MutableMap<String, String> = HashMap()

        params["mobile"] = CommonMethods.getPrefrence(mContext!!,AllKeys.MOBILE_NO).toString()
        params["Token"] = CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN).toString()
        params["usertype"] = CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).toString()

        Log.e(TAG, "login: $params")

        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Checkin Status...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.pateintLoginByMobile(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val loginResponse = `object` as LoginResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $loginResponse")
                if (loginResponse.ResponseCode == 200) {
                    //If 200 response code then it will hit all required api's...
                    if(loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()) == AllKeys.ACTIVE
                        || loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()) == AllKeys.SUCCESS){
                        //logout the user..user is inactive.
                        loadAndSaveDefaultData(navGraph,loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()))
                    }else if(loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()) == AllKeys.INACTIVE &&
                        CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.CollectionCenter)){
                        loadAndSaveDefaultData(navGraph,loginResponse.ResultArray[0].Status.uppercase(Locale.getDefault()))
                    }else{
                        logoutUser()
                    }
                }else{
                    logoutUser()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                CommonMethods.showDialogForError(mContext!!,AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun loadAndSaveDefaultData(navGraph: NavGraph, status: String) {
        //request run time permission
        requestPermissions()

        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todaysDate = df.format(c)

        // Format of sharedpref date is CommonMethods.getPrefrence(mContext,AllKeys.TODAYS_DATE)"yyyy-MM-dd"
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.TODAYS_DATE).equals(todaysDate, false)) {
            //do nothing api are hit already.....
        } else {
            //First destroy if data is present..
            Paper.book().destroy()

            Paper.init(mContext)

            if (CommonMethods.isNetworkAvailable(mContext!!)) {
                if(CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Consultant)){

                    //getAllDrugsMaster...for PrescriptionActivity
                    getAllDrugs()

                    //GetDoseMasterlist...for PrescriptionFragment
                    getDoseMaster()

                    //getAllComplaints..for PrescriptionFragment
                    getAllComplaints()

                    //getAllDiagnosis.. for PrescriptionFragment
                    getAllDiagnosis()

                    //getAllProvisionalDiagnosis.. for PrescriptionFragment
                    getProvisionalDiagnosis()

                    //getSysExams... for PrescriptionFragment..
                    getSysExams()
                }

                //getConsultationList..
                getConsultationList()

                //getAllTestList
                getTestList()

                //getEmrCheckBoxList...for VitalActivity
                getEMRCheckBoxList()

                //getAllergyList...for VitalActivity
                getAllergyList()

                //getAllSugeryList...for VitalActivity
                getSurgeryList()

                //GetRef CC and DR for PatientRegistrationActivity..
                getRefCCAndDr()

                /* if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Phlebotomist)) {
                     if (CommonMethods.getPrefrence(mContext!!, AllKeys.START_TIME).equals(AllKeys.DNF)
                         && CommonMethods.getPrefrence(mContext!!, AllKeys.ENTRY_DATE).equals(AllKeys.DNF)) {
                         //Phelbo distance api hitted already...
                     } else {
                         insertWalkDistanceOfLabBoy()
                     }
                 }*/

                //setAlarm To hit api at 8:00AM in morning..
                //setAlarmToHitApi()

                //Setting todays date...
                CommonMethods.setPreference(mContext!!, AllKeys.TODAYS_DATE, todaysDate)
            } else {
                CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
            }
        }

        if (CommonMethods.isNetworkAvailable(mContext!!)) {
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.SuperAdmin)) {
                getSuperAdminLabs()
            }
        } else {
            CommonMethods.showDialogForError(mContext!!, AllKeys.NO_INTERNET_AVAILABLE)
        }

        if(CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.CollectionCenter) &&
            status == AllKeys.INACTIVE){
            //navigate to payment gateway
            CommonMethods.showDialogForError(mContext!!,"Kindly make payment using recharge button.")
        }else{
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.dailycashregister).equals("true", true)) {
                navGraph.setStartDestination(R.id.dailyCashDashBoardFragment2)
                navController.graph = navGraph
            }else{
                navGraph.setStartDestination(R.id.dashBoardFragment2)
                navController.graph = navGraph
            }
        }

        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Lab_Technician)
            || CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Administrator)) {
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.dailycashregister).equals("true", true)) {
                navGraph.setStartDestination(R.id.dailyCashDashBoardFragment2)
                navController.graph = navGraph
            }else{
                navGraph.setStartDestination(R.id.dashBoardFragment2)
                navController.graph = navGraph
            }

        } else if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).equals(AllKeys.Support)) {
            navGraph.setStartDestination(R.id.chatFragment)
            navController.graph = navGraph
        } else {
            navGraph.setStartDestination(R.id.dashBoardFragment2)
            navController.graph = navGraph
        }

        //set total distance default..
        //CommonMethods.setPreference(mContext!!, AllKeys.TOTAL_DISTANCE, "0.0")
        /*if(CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Lab_Technician) ||
            CommonMethods.getPrefrence(mContext!!,AllKeys.USERTYPE).equals(AllKeys.Administrator)){
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.dailycashregister).equals("true", true)) {
                toolbar?.title = resources.getString(R.string.daily_cash_register)
                navController.navigate(R.id.dailyCashDashBoardFragment2)
            } else {
                toolbar?.title = CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)
                navController.navigate(R.id.dashBoardFragment2)
            }
        }else{
            toolbar?.title = CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME)
            navController.navigate(R.id.dashBoardFragment2)
        }*/
    }

    private fun getConsultationList() {
        val params: MutableMap<String, String> = HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME).toString() }!!
        params["Companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        Log.e(TAG, "getConsultationList: $params")

        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getConsultantList(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val consultationResponse = `object` as ConsultationResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $consultationResponse")
                    if (consultationResponse.ResponseCode == 200) {
                        //Calling Intent...
                        // Toast.makeText(mContext, "" + getPatientResponse.Message, Toast.LENGTH_SHORT).show()
                        Paper.book().write("consultation_list",consultationResponse.ResultArray)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getTestList() {
        val params: MutableMap<String, String> = HashMap()
        params["LabCode"] = mContext?.let { CommonMethods.getPrefrence(it, AllKeys.LABNAME) }.toString()
        Log.e(TAG, "onTestCode: $params")

        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.getTestCode(params,object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val getTestCodeResponse = `object` as GetTestCodeResponse
                progress.dismiss()
                Log.e(TAG, "onTestCode: $getTestCodeResponse")
                if (getTestCodeResponse.ResponseCode == 200) {
                    if (getTestCodeResponse.ResultArray.size > 0) {
                        Log.e(TAG, "onTestCode: "+getTestCodeResponse.ResultArray.size )
                        Paper.book().write("test_list", getTestCodeResponse.ResultArray)
                        Log.e(TAG, "onTestCode: "+Paper.book().read("test_list") )
                    }
                } else {
                    //intialise with default..
                    Paper.book().write("test_list", ArrayList<GetTestCodeBO>())
                    Toast.makeText(mContext, getTestCodeResponse.Message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                //showDialog(AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    private fun getSysExams() {
        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        val params: MutableMap<String, String> = HashMap()
        params["labcode"] = CommonMethods.getPrefrence(mContext!!,AllKeys.LABNAME).toString()
        params["companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        digiPath?.getApiRequestHelper()
            ?.getSysExams(params,object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getSysExamsResponse = `object` as GetSysExamsResponse
                    progress.dismiss()
                    if (getSysExamsResponse.ResponseCode == 200) {
                        if (getSysExamsResponse.ResultArray.size > 0) {
                            for (temp in getSysExamsResponse.ResultArray) {
                                if (getSysExamsMap.containsKey(temp.sysexam.trim())) {
                                    sysExamsTemp = getSysExamsMap[temp.sysexam.trim()]
                                    sysExamsTemp!!.add(temp)
                                } else {
                                    sysExamsTemp = ArrayList()
                                    sysExamsTemp!!.add(temp)
                                    getSysExamsMap[temp.sysexam.trim()] = sysExamsTemp!!
                                }
                            }
                            Paper.book().write("sys_exams", getSysExamsMap)
                        }
                    } else {
                        //intialise with default
                        Paper.book().write("sys_exams", getSysExamsMap)
                        Toast.makeText(mContext, getSysExamsResponse.Message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getProvisionalDiagnosis() {
        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        val params: MutableMap<String, String> = HashMap()
        params["labcode"] = CommonMethods.getPrefrence(mContext!!,AllKeys.LABNAME).toString()
        params["companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        digiPath?.getApiRequestHelper()
            ?.getProvDiagnosis(params,object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getProvDiagnosisResponse = `object` as GetProvisionalDiagnosisResponse
                    progress.dismiss()
                    if (getProvDiagnosisResponse.ResponseCode == 200) {
                        if (getProvDiagnosisResponse.ResultArray.size > 0) {
                            Paper.book().write("prov_diagnosis_master", getProvDiagnosisResponse.ResultArray)
                        }
                    } else {
                        //intialise with default
                        Paper.book().write("prov_diagnosis_master",ArrayList<GetProvDiagnosisBO>())
                        Toast.makeText(mContext, getProvDiagnosisResponse.Message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getAllDiagnosis() {
        /*val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        val params: MutableMap<String, String> = HashMap()
        params["labcode"] = CommonMethods.getPrefrence(mContext!!,AllKeys.LABNAME).toString()
        params["companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        digiPath?.getApiRequestHelper()
            ?.getDiagnosis(params,object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getDiagnosisResponse = `object` as GetDiagnosisResponse
                    //progress.dismiss()
                    if (getDiagnosisResponse.ResponseCode == 200) {
                        if (getDiagnosisResponse.ResultArray.size > 0) {
                            Paper.book().write("diagnosis_master", getDiagnosisResponse.ResultArray)
                        }
                    } else {
                        //intialise with default
                        Paper.book().write("diagnosis_master", ArrayList<GetDiagnosisBO>())
                        Toast.makeText(mContext, getDiagnosisResponse.Message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                  //  progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getAllComplaints() {
       /* val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        val params: MutableMap<String, String> = HashMap()
        params["labcode"] = CommonMethods.getPrefrence(mContext!!,AllKeys.LABNAME).toString()
        params["companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()

        digiPath?.getApiRequestHelper()
            ?.getComplain(params,object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getComplainResponse = `object` as GetComplainResponse
                  //  progress.dismiss()
                    if (getComplainResponse.ResponseCode == 200) {
                        if (getComplainResponse.ResultArray.size > 0) {
                            Paper.book().write("complains_master", getComplainResponse.ResultArray)
                        }
                    } else {
                        //intialise with default
                        Paper.book().write("complains_master", ArrayList<GetComplainBO>())
                        Toast.makeText(mContext,
                            getComplainResponse.Message,
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                 //   progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun logoutUser() {
        // Delete Operation
        CommonMethods.setPreference(mContext!!,
            AllKeys.HSC_ID,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.MOBILE_NO,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.LABNAME,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.PERSON_NAME,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.SEX,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.DOB,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.EMAIL,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.ADDRESS,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.USERTYPE,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.GCM_TOKEN,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.PATIENT_PROFILE_ID,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.USER_NAME,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.TODAYS_DATE,
            AllKeys.DNF)
        CommonMethods.setPreference(mContext!!,
            AllKeys.START_TIME,
            AllKeys.DNF)
        mContext?.let {
            CommonMethods.setPreference(it,
                AllKeys.CURRENCY_SYMBOL,
                AllKeys.DNF)
        }
        mContext?.let {
            CommonMethods.setPreference(it,
                AllKeys.dailycashregister,
                AllKeys.DNF)
        }
        mContext?.let {
            CommonMethods.setPreference(it,
                AllKeys.LOGIN_VIA,
                AllKeys.DNF)
        }
        mContext?.let {
            CommonMethods.setPreference(it,
                AllKeys.reportdetails,
                AllKeys.DNF)
        }
        mContext?.let {
            CommonMethods.setPreference(it,
                AllKeys.MERCHANT_ID_RAZORPAY,
                AllKeys.DNF)
        }
        /* CommonMethods.setPreference(mContext!!,
             AllKeys.TOTAL_DISTANCE,
             "0.0")
         CommonMethods.setPreference(mContext!!,
             AllKeys.ENTRY_DATE,
             AllKeys.DNF)

         //stop location service..
         NotificationManagerCompat.from(mContext!!).cancelAll()
         stopLocationBackgroundService()
*/
        //Destroy all paperDb data...
        Paper.book().destroy()

        Toast.makeText(mContext!!, "You are logout...Please login again", Toast.LENGTH_LONG).show()

        // check if permissions are given
        val intent = Intent(this@DashBoardActivity, LoginTypeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    fun loadCheckSlotRescheduleFragment(viewAppointmentBO: ViewAppointmentBO) {
        val checkSlotFragment = CheckSlotFragment()
        val bundle = Bundle()
        bundle.putString("app_type", "reschdeule")
        bundle.putParcelable("view_app_bo", viewAppointmentBO)
        checkSlotFragment.arguments = bundle
        loadFragment(checkSlotFragment)
        // overridePendingTransition(R.anim.activity_close_scale, R.anim.activity_open_scale);
        supportFragmentManager.popBackStack("", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            /* val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
             fragmentTransaction.replace(R.id.frame_layout, fragment)
             fragmentTransaction.commitAllowingStateLoss()*/
        }
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        /* ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission
                        .ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        */
    }

    // method to request for permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE,
            ),
            PERMISSION_ID)
    }

    // If everything is alright then
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                //Start location tressing job....
                /*if (CommonMethods.getPrefrence(mContext!!, AllKeys.START_TIME).equals(AllKeys.DNF)
                    && CommonMethods.getPrefrence(mContext!!, AllKeys.ENTRY_DATE)
                        .equals(AllKeys.DNF)
                ) {
                    openLocationServiceDialog()
                }*/
                Toast.makeText(this, "All Permsission granted...", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun openLocationServiceDialog() {
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Phlebotomist)
        ) {
            val mDialog = MaterialDialog.Builder(this@DashBoardActivity)
                .setTitle("Start Job?")
                .setMessage("Start you job to get distance.")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialogInterface: DialogInterface, which: Int ->
                    //startService..
                    startLocationService()
                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface: DialogInterface, which: Int ->
                    // Delete Operation
                    dialogInterface.dismiss()
                }
                .build()

            // Show Dialog
            mDialog.show()
        }
    }

    private fun insertWalkDistanceOfLabBoy() {
        val params: MutableMap<String, String> = HashMap()
        params["HSC_ID"] = CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()
        params["entrydate"] =
            CommonMethods.getPrefrence(mContext!!, AllKeys.ENTRY_DATE).toString()
        params["starttime"] =
            CommonMethods.getPrefrence(mContext!!, AllKeys.START_TIME).toString()
        params["endtime"] = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        params["Distance"] =
            CommonMethods.getPrefrence(mContext!!, AllKeys.TOTAL_DISTANCE).toString()
        params["Username"] =
            CommonMethods.getPrefrence(mContext!!, AllKeys.USER_NAME).toString()

        Log.e(TAG, "insertWalkDistanceOfLabBoy: $params")

        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.insertWalkDistanceOfCollectionBoy(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $commonResponse")
                    if (commonResponse.ResponseCode == 200) {
                        //make all fields to default...
                        CommonMethods.setPreference(mContext!!,
                            AllKeys.START_TIME,
                            AllKeys.DNF)
                        CommonMethods.setPreference(mContext!!,
                            AllKeys.TOTAL_DISTANCE,
                            "0.0")
                        CommonMethods.setPreference(mContext!!,
                            AllKeys.ENTRY_DATE,
                            AllKeys.DNF)
                        CommonMethods.showDialogForSuccess(mContext!!,
                            "You have to restart app to start job again.")

                        //Stop location service....
                        NotificationManagerCompat.from(mContext!!).cancelAll()
                        stopLocationBackgroundService()
                    } else {
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun startLocationService() {
        if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE)
                .equals(AllKeys.Phlebotomist, false)
        ) {
            if (isLocationEnabled()) {
                startLocationBackgroundService()
            } else {
                Toast.makeText(mContext,
                    "Please turn on your location...",
                    Toast.LENGTH_LONG)
                    .show()
                /*val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent,199)*/
                displayLocationSettingsRequest(mContext!!)
            }
        }
    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                builder.build())
        result.setResultCallback { result1 ->
            val status: Status = result1.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    startLocationBackgroundService()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    startLocationBackgroundService()
                    status.startResolutionForResult(this@DashBoardActivity, 1)
                } catch (e: IntentSender.SendIntentException) {
                    // Log.i(TAG, "PendingIntent unable to execute request.");
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    private fun getAllDrugs() {
        /*val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        digiPath?.getApiRequestHelper()
            ?.getDrugsList(object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getDrugResponse = `object` as GetDrugResponse
                    //progress.dismiss()
                    Log.e(TAG, "onSuccess: $getDrugResponse")
                    if (getDrugResponse.ResponseCode == 200) {
                        if (getDrugResponse.ResultArray.size > 0) {
                            Paper.book().write("drug_master", getDrugResponse.ResultArray)
                        }
                    } else {
                        Toast.makeText(mContext,
                            getDrugResponse.Message,
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    //progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getDoseMaster() {
        /*val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        digiPath?.getApiRequestHelper()
            ?.getDoseMasterlist(object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getDoseMaster = `object` as GetDoseMasterResponse
                   // progress.dismiss()
                    Log.e(TAG, "onSuccess: $getDoseMaster")
                    if (getDoseMaster.ResponseCode == 200) {
                        if (getDoseMaster.ResultArray.size > 0) {
                            Paper.book().write("dose_master", getDoseMaster.ResultArray)
                        }
                    } else {
                        Toast.makeText(mContext, getDoseMaster.Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                 //   progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getEMRCheckBoxList() {
       /* val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        digiPath?.getApiRequestHelper()
            ?.getEMRCheckboxList(object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getEMRCheckBoxListResponse = `object` as GetEMRCheckBoxListResponse
                    //progress.dismiss()
                    Log.e(TAG, "onSuccess: $getEMRCheckBoxListResponse")
                    if (getEMRCheckBoxListResponse.ResponseCode == 200) {
                        if (getEMRCheckBoxListResponse.ResultArray.size > 0) {
                            Paper.book().write("emr_checkbox_master",
                                getEMRCheckBoxListResponse.ResultArray)
                        }
                    } else {
                        //intiailse with default
                        Paper.book().write("emr_checkbox_master", ArrayList<GetEMRCheckBoxListBO>())
                        Toast.makeText(mContext,
                            getEMRCheckBoxListResponse.Message,
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                   // progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getAllergyList() {
      /*  val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        digiPath?.getApiRequestHelper()
            ?.getAllergyList(object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getAllergyListResponse = `object` as GetAllergyListResponse
                   // progress.dismiss()
                    Log.e(TAG, "onSuccess: $getAllergyListResponse")
                    if (getAllergyListResponse.ResponseCode == 200) {
                        if (getAllergyListResponse.ResultArray.size > 0) {
                            for (temp in getAllergyListResponse.ResultArray) {
                                if (getAllergyListMap.containsKey(temp.AllergyCategoryId.trim())) {
                                    allergyTemp = getAllergyListMap[temp.AllergyCategoryId.trim()]
                                    allergyTemp!!.add(temp)
                                } else {
                                    allergyTemp = ArrayList()
                                    allergyTemp!!.add(temp)
                                    getAllergyListMap[temp.AllergyCategoryId.trim()] =
                                        allergyTemp!!
                                }
                            }
                            Paper.book().write("allergy_master", getAllergyListMap)
                        }
                    } else {
                        //intilise with default...
                        Paper.book().write("allergy_master", getAllergyListMap)
                        Toast.makeText(mContext,
                            getAllergyListResponse.Message,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                   // progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getSurgeryList() {
        val params: MutableMap<String, String> = HashMap()
        params["Prefix"] = ""

       /* val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)*/

        digiPath?.getApiRequestHelper()
            ?.getVitalSurgeryList(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val vitalSurgeryListResponse = `object` as VitalSurgeryListResponse
                   // progress.dismiss()
                    Log.e(TAG, "onSuccess: $vitalSurgeryListResponse")
                    if (vitalSurgeryListResponse.ResponseCode == 200) {
                        if (vitalSurgeryListResponse.ResultArray.size > 0) {
                            Paper.book().write("surgery_master", vitalSurgeryListResponse.ResultArray)
                        }
                    } else {
                        //intialise with default
                        Paper.book().write("surgery_master", ArrayList<VitalSurgeryListBO>())
                        Toast.makeText(mContext,
                            vitalSurgeryListResponse.Message,
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    //progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getRefCCAndDr() {
        val params: MutableMap<String, String> = HashMap()

        params["companyid"] =  mContext?.let { CommonMethods.getPrefrence(it, AllKeys.COMPANY_ID) }.toString()
        params["labcode"] =
            CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()

        Log.e(TAG, "getRefCCAndDr: $params")
        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please wait...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)
        digiPath?.getApiRequestHelper()
            ?.getref_cc(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getRefCCResponse = `object` as GetRefCCResponse
                    progress.dismiss()
                    Log.e(TAG, "onSuccess: $getRefCCResponse")
                    if (getRefCCResponse.ResponseCode == 200) {
                        if (getRefCCResponse.ResultArray.size > 0) {
                            for (temp in getRefCCResponse.ResultArray) {
                                if (temp.Check_Flag.equals("CC", true)) {
                                    getRefCCList?.add(temp)
                                } else if (temp.Check_Flag.equals("DR", true)) {
                                    getRefDrList?.add(temp)
                                }
                            }

                            Paper.book().write("getref_cc", getRefCCList)
                            Paper.book().write("getref_dr", getRefDrList)

                            Log.e(TAG, "onSuccess: getRefCCAndDr " + getRefCCList?.size)
                            Log.e(TAG, "onSuccess: getRefCCAndDr " + getRefDrList?.size)
                        }
                    } else {
                        //intialise with default.
                        Paper.book().write("getref_cc", getRefCCList)
                        Paper.book().write("getref_dr", getRefDrList)
                        Toast.makeText(mContext,
                            getRefCCResponse.Message,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    //showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    private fun getSuperAdminLabs() {
        val params: MutableMap<String, String> = HashMap()
        params["Pno"] = CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()

        Log.e(TAG, "getSuperAdminLabs: $params")

        /*val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Please Wait....")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)
*/
        digiPath?.getApiRequestHelper()
            ?.getLabSuperAdmin(params, object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val getLabNameResponse = `object` as LabNameResponse
                    //progress.dismiss()
                    Log.e(TAG, "onSuccess: $getLabNameResponse")
                    if (getLabNameResponse.ResponseCode == 200) {
                        getLabNameArrayList = getLabNameResponse.ResultArray
                    } else {
                        Toast.makeText(mContext!!,
                            getLabNameResponse.Message,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(apiResponse: String) {
                   // progress.dismiss()
                    Log.e(TAG, "onFailure: $apiResponse")
                }
            })
    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER))
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (LocationService::class.java.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    private fun startLocationBackgroundService() {
        if (!isLocationServiceRunning()) {
            CommonMethods.setPreference(mContext!!,
                AllKeys.ENTRY_DATE,
                CommonMethods.getTodayDate("MM/dd/yyyy"))
            CommonMethods.setPreference(mContext!!,
                AllKeys.START_TIME,
                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = AllKeys.ACTION_START_LOCATION_SERVICE
            startService(intent)
        }
    }

    private fun stopLocationBackgroundService() {
        if (isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = AllKeys.ACTION_STOP_LOCATION_SERVICE
            stopService(intent)
        }
    }


    private fun setAlarmToHitApi() {
        Toast.makeText(mContext, "Trigger Hitted at alarm", Toast.LENGTH_LONG).show()
        val intent = Intent(mContext, PatientAppointmentReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0)
        val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // Set the alarm to start at 8:00 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            //set(Calendar.MINUTE, 33)
        }

        //Ring alarm on time at 8:00AM
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            // SystemClock.elapsedRealtime() + 60 * 1000,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> {
                drawer.closeDrawer(GravityCompat.START)
            }
            drawer.isDrawerOpen(GravityCompat.END) -> {
                drawer.closeDrawer(GravityCompat.END)
            }
            else -> {
                //Call close app function
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    return
                }

                doubleBackToExitPressedOnce = true
                //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

                Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }

        /*if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 3000)*/

        //Call close app function
        //closeApp()
    }

    fun closeApp() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setMessage("Do you want to Exit?")
        builder.setPositiveButton("Yes") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            finish()
        }
        builder.setNegativeButton("No") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            dialog.cancel()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun showPictureDialog() {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems =
            arrayOf("Capture photo from camera", "Select photo from gallery"/*, "Preview"*/)
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> takePhotoFromCamera()
                1 -> choosePhotoFromGallary()
            }
        }
        pictureDialog.show()
    }

    private fun takePhotoFromCamera() {
        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)*/
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create the File where the photo should go
        try {
            photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(mContext!!,
                    mContext?.packageName + ".fileprovider",
                    photoFile!!)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
            }
        } catch (ex: Exception) {
            // Error occurred while creating the File
            displayMessage(mContext!!, ex.message.toString())
        }
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = mContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val thumbnail = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            val out = ByteArrayOutputStream()
            if (BuildConfig.DEBUG && thumbnail == null) {
                error("Assertion failed")
            }
            thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 50, out)
            //val decodedImage = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
            val byteArray = out.toByteArray()
            profilePicBitMap = thumbnail
            ivProfilePic!!.setImageBitmap(thumbnail)
            profilePicBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

            //show Alert Box
            showAlertBox()

        } else if (requestCode == GALLERY && resultCode == RESULT_OK) {
            val contentURI: Uri = data?.data ?: return
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(mContext?.contentResolver, contentURI)
                //String path = saveImage(bitmap);
                val out = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 65, out)
                val byteArray = out.toByteArray()
                //val compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                profilePicBitMap = bitmap
                ivProfilePic!!.setImageBitmap(bitmap)
                profilePicBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                //show Alert Box
                showAlertBox()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlertBox() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        builder.setCancelable(false)
        builder.setMessage("Do you want to Upload Pic?")
        builder.setPositiveButton("Yes") { dialog, which -> // send data from the
            //remove file from list
            if (profilePicBase64String != null) {
                uploadProfilePicImage()
            }
        }
        builder.setNegativeButton("No") { dialog, which -> // send data from the
            // AlertDialog to the Activity
            dialog.cancel()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun uploadProfilePicImage() {
        val profileId =
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
                CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID).toString()
            } else {
                CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString()
            }
        val params: MutableMap<String, String> = HashMap()
        params["profileid"] = profileId
        params["labcode"] = CommonMethods.getPrefrence(mContext!!, AllKeys.LABNAME).toString()
        params["f"] = profilePicBase64String.toString()
        params["CompId"] = AllKeys.COMPANY_ID
        params["type"] = "profile_pic"

        Log.e(TAG, "uploadProfilePicImage: $params")

        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Uploading please wait..")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.uploadProfileImage(params,
            object : ApiRequestHelper.OnRequestComplete {
                override fun onSuccess(`object`: Any) {
                    val commonResponse = `object` as CommonResponse
                    progress.dismiss()
                    if (commonResponse.ResponseCode == 200) {
                        //showDialogForSuccess(mContext!!, "Payment Successful. \n$transactionID")
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)

                        Glide.with(mContext!!)
                            .load(ConfigUrl.IMAGE_URL + "" + profileId + "_" + CommonMethods.getPrefrence(
                                mContext!!,
                                AllKeys.LABNAME).toString() + "_image")
                            .placeholder(R.drawable.ic_user)
                            .into(ivProfilePic!!)
                    } else {
                        CommonMethods.showDialogForError(mContext!!, commonResponse.Message)
                    }
                }

                override fun onFailure(apiResponse: String) {
                    progress.dismiss()
                    // showDialog(AllKeys.SERVER_MESSAGE)
                    Log.e(TAG, "insertVaccinationPatient: $apiResponse")
                }
            })
    }

    private fun sendDataToCloudStore(firebaseBO: FireBaseBO) {
        //first insert in our recent chat to show list in Chat Fragment....
        val documentReference: DocumentReference =
            if (CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE) == AllKeys.Patient) {
                firebaseFirestore?.collection("Users")!!
                    .document(CommonMethods.getPrefrence(mContext!!, AllKeys.PATIENT_PROFILE_ID)
                        .toString())
            } else {
                firebaseFirestore?.collection("Users")!!
                    .document(CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID).toString())
            }
        val params: MutableMap<String, Any> = HashMap()

        params[AllKeys.recent_chat] = FieldValue.arrayUnion(firebaseBO)

        documentReference.set(params).addOnSuccessListener {
            // Toast.makeText(mContext, "Data on Cloud FireStore success", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "sendDataToCloudStore: \"Data on Cloud FireStore success\"")
        }.addOnFailureListener {
            Log.e(TAG, "sendDataToCloudStore: $it")
        }

        //then also add in other user chat also that they can also view who has messaged him
        addInReceiverUserDataBase(firebaseBO.uid)
    }

    private fun addInReceiverUserDataBase(receiver_uid: String) {
        val documentReference1: DocumentReference =
            firebaseFirestore?.collection("Users")!!.document(receiver_uid)
        val params1: MutableMap<String, Any> = HashMap()

        val firebaseBO1 = FireBaseBO(
            CommonMethods.getPrefrence(mContext!!, AllKeys.PERSON_NAME),
            "",
            CommonMethods.getPrefrence(mContext!!, AllKeys.HSC_ID),
            "Offline",
            CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN),
            "",
        )

        params1[AllKeys.recent_chat] = FieldValue.arrayUnion(firebaseBO1)

        documentReference1.set(params1).addOnSuccessListener {
            //Toast.makeText(mContext, "Data on Cloud FireStore success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e(TAG, "sendDataToCloudStore: $it")
        }
    }

    private fun checkBalanceForCollectonCenter(){
        val params: MutableMap<String, String> = HashMap()
        params["UserId"] = CommonMethods.getPrefrence(mContext!!, AllKeys.ET_USERNAME).toString()
        params["Password"] = CommonMethods.getPrefrence(mContext!!, AllKeys.ET_PASSWORD).toString()
        params["Token"] = CommonMethods.getPrefrence(mContext!!, AllKeys.GCM_TOKEN).toString()
        params["usertype"] = CommonMethods.getPrefrence(mContext!!, AllKeys.USERTYPE).toString()

        Log.e(TAG, "login: $params")

        val progress = ProgressDialog(this@DashBoardActivity)
        progress.setMessage("Checkin In...")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.show()
        progress.setCancelable(false)

        digiPath?.getApiRequestHelper()?.login(params, object : ApiRequestHelper.OnRequestComplete {
            override fun onSuccess(`object`: Any) {
                val loginResponse = `object` as LoginResponse
                progress.dismiss()
                Log.e(TAG, "onSuccess: $loginResponse")
                if (loginResponse.ResponseCode == 200) {
                    if(loginResponse.ResultArray[0].balance == "0"){
                        CommonMethods.showDialogForSuccess(mContext!!,"No Payment Due!")
                    }else{
                        showDialogForError(mContext!!,"Please make payment of ${loginResponse.ResultArray[0].balance}",loginResponse.ResultArray[0])
                    }

                } else {
                    CommonMethods.showDialogForError(mContext!!,loginResponse.Message)
                }
            }

            override fun onFailure(apiResponse: String) {
                progress.dismiss()
                CommonMethods.showDialogForError(mContext!!,AllKeys.SERVER_MESSAGE)
                Log.e(TAG, "onFailure: $apiResponse")
            }
        })
    }

    fun showDialogForError(context: Context, message: String, loginBO: LoginBO) {
        val mDialog: MaterialDialog = MaterialDialog.Builder(context as Activity)
            .setTitle(message) // .setMessage("Thank you for your interest in MACSI, Your application is being processed. We will get back to you in timely manner")
            .setCancelable(false)
            //.setAnimation(R.raw.success_exploration)
            .setPositiveButton("Make Payment") { dialogInterface: DialogInterface, which: Int ->
                // Delete Operation
                dialogInterface.dismiss()
                navigateToPaymentGateway(loginBO)
            } //.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
            .build()

        // Show Dialog
        mDialog.show()
    }

    private fun navigateToPaymentGateway(loginBO: LoginBO) {
        //Please consider loginBO object as getPatientList it is not a patient data..
        val getPatientListBO= GetPatientListBO(loginBO.HSC_ID
            ,loginBO.PersonName
            ,loginBO.UserName
            ,"",
            "",
            "",
            "",
            loginBO.MobileNo,"",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            loginBO.balance.replace("-",""),
            loginBO.balance.replace("-",""),
            "")

        val labNameBO=LabNameBO(loginBO.LabName,loginBO.LabName_C)

        Log.e(TAG, "navigateToPaymentGateway: $getPatientListBO")
        Log.e(TAG, "navigateToPaymentGateway: "+labNameBO )
        showPictureDialog(getPatientListBO,labNameBO,loginBO)
    }

    private fun showPictureDialog(
        getPatientListBO: GetPatientListBO,
        labNameBO: LabNameBO,
        loginBO: LoginBO
    ) {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Payment Gateway")
        val pictureDialogItems = arrayOf("RazorPay", "PayU Money"/*, "Preview"*/)
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 ->
                    if(loginBO.marchantid.isEmpty()){
                        CommonMethods.showDialogForError(mContext!!,"Payment Gateway not available!")
                    }else{
                        // Delete Operation
                        val intent = Intent(mContext!!, CheckOutActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("patient_bo", getPatientListBO)
                        intent.putExtra("labname_bo", labNameBO)
                        intent.putExtra("type", "cc_invoice")
                        intent.putExtra("isLogin","yes")
                        intent.putExtra("payment_gateway",AllKeys.RAZOR_PAY)
                        startActivity(intent)
                    }
                1 ->
                    if(loginBO.Payu_marchantid.isEmpty() || loginBO.Payu_marchantsaltid.isEmpty()){
                        CommonMethods.showDialogForError(mContext!!,"Payment Gateway not available!")
                    }else{
                        // Delete Operation
                        val intent = Intent(mContext!!, CheckOutActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("patient_bo", getPatientListBO)
                        intent.putExtra("labname_bo", labNameBO)
                        intent.putExtra("type", "cc_invoice")
                        intent.putExtra("isLogin","yes")
                        intent.putExtra("payment_gateway",AllKeys.PAYU)
                        startActivity(intent)
                    }
            }
        }
        pictureDialog.show()
    }
}