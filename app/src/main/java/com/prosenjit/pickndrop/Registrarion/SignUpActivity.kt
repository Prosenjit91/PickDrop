package com.prosenjit.pickndrop.Registrarion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.prosenjit.pickndrop .NetworkCall.*
import com.prosenjit.pickndrop.Base
import com.prosenjit.pickndrop.NetworkCall.NetworkUrlRequest
import com.prosenjit.pickndrop.R
import kotlinx.android.synthetic.main.layout_register.*
import kotlinx.android.synthetic.main.layout_register.avi_progress
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import android.view.View.GONE
import com.google.android.gms.tasks.TaskExecutors
import javax.xml.datatype.DatatypeConstants.SECONDS
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import android.view.View.GONE
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import douglasspgyn.com.github.circularcountdown.CircularCountdown
import douglasspgyn.com.github.circularcountdown.listener.CircularListener
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.graphics.Paint





class SignUpActivity : Base(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_register)
        ChangeStausBarColour(this@SignUpActivity, R.color.colorwhite)

        tv_signin.setOnClickListener {
            Log.e("signin Onclick","True")
            GoToSignIn()
        }
        btnRegister.setOnClickListener {
            Log.e("signin Onclick","True")
            CheckValidation()
        }
        tvResend.setOnClickListener {
            resendVerificationCode(phoneNo)
        }
        edtPhone.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charecter: CharSequence?, p1: Int, p2: Int, p3: Int) {
              if(edtPhone.text!!.length==10){
                  sendVerificationCode("+91"+edtPhone.text.toString())
              }else{
                  if(lnOtpView.visibility==View.VISIBLE){
                      lnOtpView.visibility==View.GONE
                  }
                  otp_view.setText("")

              }
            }
        })
    }
    fun GoToSignIn(){
        val  intent= Intent(this@SignUpActivity, SiginInActivity::class.java).apply {  }
        startActivity(intent)
        finish()
    }

    private fun CheckValidation() {
        if(edtName.text!!.isEmpty()){
            edtName.setError("Please Enter name")
            errorShake(edtName)
        }else if(edtPhone.text!!.isEmpty()){
            edtName.setError("Please Enter Ph. No.")
            errorShake(edtPhone)
        }else if(edtPhone.text!!.length!==10){
            edtPhone.setError("Ph. No. not valid")
            errorShake(edtPhone)
        }else if(edtPassword.text!!.isEmpty()){
            edtPassword.setError("Please Enter Password")
            errorShake(edtPassword)
        }else{
            btnRegister.isEnabled=false
            createUser()
        }
    }

    private fun createUser() {
        hideKeyboard()
        aviProgressVisible(avi_progress,"BallPulseIndicator")
         val parameter = JSONObject()
         try {
             parameter.put("name",edtName.text.toString().trim())
             parameter.put("mobile",edtPhone.text.toString().trim())
             parameter.put("password",edtPassword.text.toString().trim())
             parameter.put("confirm_password",edtPassword.text.toString().trim())
             parameter.put("user_type","1")
         } catch (e: JSONException) {
             // TODO Auto-generated catch block
             e.printStackTrace()
         }
        NetworkUrlRequest(WebUrls.user_SignUp_URL,parameter,METHOD_POST,object : CustomResponse.Listener<String>{
            override fun onSuccessResponse(response: String) {
                runOnUiThread {
                    aviProgressInVisible(avi_progress)
                    btnRegister.isEnabled=true
                }
                try {

                    val signup_obj = JSONObject(response)
                    Log.e("signup_obj",""+signup_obj)
                    val error=signup_obj.getString(ERROR_NAME)
                    Log.e("error",error)
                    val message=signup_obj.getString(RESULT_MSG)
                    if(error.equals(ERROR_SUSSESS)){
                        Log.e("If True","Call")
                        runOnUiThread {
                            Toast.makeText(this@SignUpActivity,"Signup successfully",Toast.LENGTH_LONG).show()
                            val handler = Handler()
                            handler.postDelayed({
                                val intent = Intent(this@SignUpActivity, SiginInActivity::class.java)
                                startActivity(intent)
                                finish()

                            }, 2000)
                        }


                    }else{
                        runOnUiThread {
                            showErrorAlert(this@SignUpActivity,"SignUp Failed","Something going wrong")
                        }

                    }

                }catch (e:Exception){
                    Log.e("excep","Call")
                }

            }

            override fun onError(response: String) {
                btnRegister.isEnabled=true
                showErrorAlert(this@SignUpActivity,"SignUp Failed",response)
            }
        })
    }
    private fun sendVerificationCode(number: String) {
        phoneNo=number
        edtPhone.isEnabled=false
        edtPassword.isEnabled=false
        if(lnOtpView.visibility==View.GONE){
            lnOtpView.visibility= View.VISIBLE
        }
        tv_otpresult.text="Verifying Otp..."
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,
            30,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallBack
        )
        if(circularCountdown.visibility==View.GONE){
            circularCountdown.visibility==View.VISIBLE
        }
        circularCountdown.create(1, 30, CircularCountdown.TYPE_SECOND)
            .listener(object : CircularListener {
                override fun onTick(progress: Int) {

                }

                override fun onFinish(newCycle: Boolean, cycleCount: Int) {
                  circularCountdown.stop()
                    if(tvResend.visibility==View.GONE) {
                        tvResend.visibility = View.VISIBLE
                    }
                    tvResend.setPaintFlags(tvResend.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
                }
            })
            .start()
    }
    private fun resendVerificationCode(phoneNumber: String) {
        tvResend.visibility=View.GONE
        phoneNo=phoneNumber
        edtPhone.isEnabled=false
        edtPassword.isEnabled=false
        if(lnOtpView.visibility==View.GONE){
            lnOtpView.visibility= View.VISIBLE
        }
        tv_otpresult.text="Verifying Otp..."
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            30,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallBack
        )
        if(circularCountdown.visibility==View.GONE){
            circularCountdown.visibility==View.VISIBLE
        }
        circularCountdown.create(1, 30, CircularCountdown.TYPE_SECOND)
            .listener(object : CircularListener {
                override fun onTick(progress: Int) {

                }

                override fun onFinish(newCycle: Boolean, cycleCount: Int) {
                    circularCountdown.stop()
                    if(tvResend.visibility==View.GONE) {
                        tvResend.visibility = View.VISIBLE
                    }
                    tvResend.setPaintFlags(tvResend.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
                }
            })
            .start()
    }
    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            Log.e("onCodeSent##",""+s)
            verificationId = s
            resendingToken=forceResendingToken
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            Log.e("code##",""+code)
            if (code != null) {
                edtPhone.isEnabled=true
                edtPassword.isEnabled=true
                circularCountdown.stop()
                circularCountdown.visibility=View.GONE
                tv_otpresult.text="Verified Successfully"
                otp_view.setText(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("onVerificationFailed##",""+e)
            edtPhone.isEnabled=true
            edtPassword.isEnabled=false
            circularCountdown.stop()
            circularCountdown.visibility=View.GONE
            tv_otpresult.text="Verification Faild"
            tv_otpresult.setTextColor(resources.getColor(R.color.colorred))

        }
    }

}