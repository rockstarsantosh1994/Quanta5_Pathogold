package com.bms.pathogold_bms.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.pathogold_bms.BuildConfig
import com.bms.pathogold_bms.R
import com.bms.pathogold_bms.activity.CheckOutActivity
import com.bms.pathogold_bms.adapter.SelectedTestAdapter
import com.bms.pathogold_bms.model.getpatientlist.GetPatientListBO
import com.bms.pathogold_bms.model.testcode.GetTestCodeBO
import com.bms.pathogold_bms.model.viewappointment.ViewAppointmentBO
import com.bms.pathogold_bms.utility.AllKeys
import com.bms.pathogold_bms.utility.CommonMethods

class PatientRegistrationInvoiceDialog : DialogFragment(),SelectedTestAdapter.DeleteSelectedTest, View.OnClickListener {

    private val TAG = "PatientRegistrationInvo"

    //Business Object Declaration...
    private var getPatientListBO:GetPatientListBO?=null

    //ArrayList declaration..
    private var selectedTestArrayList=ArrayList<GetTestCodeBO>()

    //TextView declaration..
    private var tvPatientRegNo:TextView?=null
    private var tvPatientRegDate:TextView?=null
    private var tvPatientName:TextView?=null
    private var tvAge:TextView?=null
    private var tvSex:TextView?=null
    private var tvMobile:TextView?=null

    private var tvSubTotal:TextView?=null
    private var tvTax:TextView?=null
    private var tvTotal:TextView?=null
    private var tvBalanceDue:TextView?=null

    private var tvCancel:TextView?=null
    private var tvMakePayment:TextView?=null

    //EditText declaration...
    private var etDiscount:TextView?=null

    //RecyclerView declaration..
    private var rvSelectedList:RecyclerView?=null

    private var viewAppointmnetBO:ViewAppointmentBO?=null

    private var tvFinalBalanceDue:Int=0

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_patient_registration_invoice_dialog, container, false)

        //basic intialisation..
        initViews(view)

        val arguments = arguments
        if (BuildConfig.DEBUG && arguments == null) {
            error("Assertion failed")
        }

        if(getArguments()?.getParcelableArrayList<GetTestCodeBO>("test_list")!=null){
            selectedTestArrayList.addAll(arguments?.getParcelableArrayList("test_list")!!)

            val selectedTestAdapter = context?.let { SelectedTestAdapter(it, selectedTestArrayList, this, View.GONE) }
            rvSelectedList?.adapter=selectedTestAdapter
        }
        viewAppointmnetBO=arguments?.getParcelable("appointment_details")
        getPatientListBO=getArguments()?.getParcelable("patient_bo")
        if(getPatientListBO!=null){
            setDataToTextView()
        }
        return view
    }

    private fun initViews(view: View?) {
        //TextView binding...
        tvPatientRegNo=view?.findViewById(R.id.tv_patient_reg_no)
        tvPatientRegDate=view?.findViewById(R.id.tv_patient_reg_date)
        tvPatientName=view?.findViewById(R.id.tv_patient_name)
        tvAge=view?.findViewById(R.id.tv_age_value)
        tvSex=view?.findViewById(R.id.tv_sex)
        tvMobile=view?.findViewById(R.id.tv_patient_mobile_value)

        tvSubTotal=view?.findViewById(R.id.tv_subtotal)
        tvTax=view?.findViewById(R.id.tv_tax)
        tvTotal=view?.findViewById(R.id.tv_total)
        tvBalanceDue=view?.findViewById(R.id.tv_balance_due)

        tvCancel=view?.findViewById(R.id.tv_cancel)
        tvMakePayment=view?.findViewById(R.id.tv_make_payment)

        //EditText binding...
        etDiscount=view?.findViewById(R.id.et_discount)

        //RecyclerView binding...
        rvSelectedList=view?.findViewById(R.id.rv_selected_test)
        val linearLayoutManager= LinearLayoutManager(context)
        rvSelectedList?.layoutManager=linearLayoutManager

        //Click listeners..
        tvCancel?.setOnClickListener(this)
        tvMakePayment?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_cancel-> dismiss()

            R.id.tv_make_payment->{
                when {
                    tvBalanceDue?.text.toString().toInt()<0 -> {
                        CommonMethods.showDialogForError(requireContext(),"Invalid Payment!")
                    }
                    tvBalanceDue?.text.toString().toInt()==0 -> {
                        CommonMethods.showDialogForError(requireContext(),"Payment Already Done!")
                    }
                    else -> {
                        showPictureDialog()
                    }
                }
            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Payment Gateway")
        val pictureDialogItems =
            arrayOf("RazorPay", "PayU Money"/*, "Preview"*/)
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 ->
                    if(CommonMethods.getPrefrence(requireContext(), AllKeys.MERCHANT_ID_RAZORPAY).equals(AllKeys.DNF)){
                    CommonMethods.showDialogForError(requireContext(),"Internal Server Error!")
                }else{
                    val intent = Intent(context, CheckOutActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("patient_bo", getPatientListBO)
                    intent.putExtra("selected_test",selectedTestArrayList)
                    intent.putExtra("final_amount",tvBalanceDue?.text.toString())
                    intent.putExtra("total",tvTotal?.text.toString())
                    intent.putExtra("discount",etDiscount?.text.toString())
                    intent.putExtra("type","patient_registration_invoice")
                    intent.putExtra("payment_gateway",AllKeys.RAZOR_PAY)
                    startActivity(intent)
                    dismiss()
                }
                1 ->
                    if(CommonMethods.getPrefrence(requireContext(), AllKeys.PAYU_MERCHANT_KEY).equals(AllKeys.DNF) ||
                        CommonMethods.getPrefrence(requireContext(), AllKeys.PAYU_MERCHANT_SALT).equals(AllKeys.DNF)){
                    CommonMethods.showDialogForError(requireContext(),"Internal Server Error!")
                }else{
                    val intent = Intent(context, CheckOutActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("patient_bo", getPatientListBO)
                    intent.putExtra("selected_test",selectedTestArrayList)
                    intent.putExtra("final_amount",tvBalanceDue?.text.toString())
                    intent.putExtra("total",tvTotal?.text.toString())
                    intent.putExtra("discount",etDiscount?.text.toString())
                    intent.putExtra("type","patient_registration_invoice")
                    intent.putExtra("payment_gateway",AllKeys.PAYU)
                    startActivity(intent)
                    dismiss()
                }
            }
        }
        pictureDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToTextView() {
        tvPatientRegNo?.text=getPatientListBO?.regno
        tvPatientRegDate?.text= CommonMethods.getTodayDate("dd, MMM yyyy")
        tvPatientName?.text = getPatientListBO?.PatientName
        tvAge?.text = getPatientListBO?.age+" "+getPatientListBO?.MDY
        tvSex?.text= getPatientListBO?.sex
        tvMobile?.text = getPatientListBO?.PatientPhoneNo
        etDiscount?.text="0"
        var sum = 0
        for (temp in selectedTestArrayList) {
            //Total of all selected test rate..
            sum += temp.rate.toInt()
        }
        if(viewAppointmnetBO==null){
            tvBalanceDue?.text= sum.toString()
            tvFinalBalanceDue=sum
        }else{
            tvBalanceDue?.text= (sum - viewAppointmnetBO?.Advance.toString().toInt()).toString()
            tvFinalBalanceDue=(sum - viewAppointmnetBO?.Advance.toString().toInt())
        }
        tvSubTotal?.text= sum.toString()
        tvTotal?.text=tvSubTotal?.text.toString()

        etDiscount?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try{
                    if(etDiscount?.text.toString().isNotEmpty()){
                        var discount = 0
                        discount = (tvFinalBalanceDue/ 100)* etDiscount?.text.toString().toInt()
                        if(discount==0){
                            tvBalanceDue?.text= tvFinalBalanceDue.toString()
                        }else{
                            tvBalanceDue?.text= (tvFinalBalanceDue-discount).toString()
                        }
                    }else{
                        etDiscount?.text="0"
                        tvTotal?.text=tvSubTotal?.text.toString()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onDeleteTest(position: Int) {
        Log.e(TAG, "do nothing" )
    }
}