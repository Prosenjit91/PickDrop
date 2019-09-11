package  com.prosenjit.pickndrop.Registrarion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.prosenjit.pickndrop .NetworkCall.*
import com.prosenjit.pickndrop.Base
import com.prosenjit.pickndrop.MapsActivity
import com.prosenjit.pickndrop.NetworkCall.NetworkUrlRequest
import com.prosenjit.pickndrop.R
import com.prosenjit.pickndrop.SharedPrefernce.TinyDB
import kotlinx.android.synthetic.main.layout_login.*
import kotlinx.android.synthetic.main.layout_login.avi_progress
import kotlinx.android.synthetic.main.layout_register.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject



class SiginInActivity : Base() {
     lateinit var message:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_login)
        ChangeStausBarColour(this@SiginInActivity, R.color.colorwhite)
        tv_signUp.setOnClickListener {
            Log.e("Onclick","True")
            GoToSignUp()
        }
        btnLogin.setOnClickListener {
            Log.e("Onclick","True")
            CheckValidation()
        }
    }

    private fun CheckValidation() {
        if(et_mobile.text!!.isEmpty()){
            errorShake(et_mobile)
            et_mobile.setError("Mobile No. Required")
        }else if(et_mobile.text!!.length!==10){
            errorShake(et_mobile)
            et_mobile.setError("Mobile No. Not valid")
        }else if(et_password.text!!.isEmpty()){
            errorShake(et_password)
            et_password.setError("Password Required")
        }else{
            ExuteLogin()
        }
    }

    private fun ExuteLogin() {
        hideKeyboard()
        aviProgressVisible(avi_progress,"BallPulseIndicator")
        val parameter = JSONObject()
        try {
            parameter.put("mobile",et_mobile.text.toString().trim())
            parameter.put("password",et_password.text.toString().trim())
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        NetworkUrlRequest(WebUrls.user_LogIn_URL,parameter,METHOD_POST,object : CustomResponse.Listener<String>{
            override fun onSuccessResponse(response: String) {
                btnLogin.isEnabled=true
                runOnUiThread {
                    aviProgressInVisible(avi_progress)
                }
                try {
                    val signin_obj = JSONObject(response)
                    Log.e("signin_obj",""+signin_obj)
                    val error=signin_obj.getString(ERROR_NAME)
                    try {
                        message = signin_obj.getString(RESULT_MSG)
                    }catch (e:Exception){}
                    if(error.equals(ERROR_SUSSESS)){
                        val jsonarray=signin_obj.getJSONArray("USER_DETAILS")
                        StoreShared(jsonarray);
                        runOnUiThread {
                            Toast.makeText(this@SiginInActivity,"Login successfully done",Toast.LENGTH_LONG).show()
                            val handler = Handler()
                            handler.postDelayed({
                                val intent = Intent(this@SiginInActivity, MapsActivity::class.java).apply { }
                                startActivity(intent)
                                finish()

                            }, 2000)
                        }

                    }else{
                        runOnUiThread {
                            Log.e("signin_obj", "Else")
                            showErrorAlert(this@SiginInActivity,"Login Failed",message)

                        }

                    }
                }catch (e:Exception){
                    runOnUiThread {
                        Log.e("Exception##",""+e)
                        showErrorAlert(this@SiginInActivity,"Login Failed","Something going wrong")

                    }
                }

            }

            override fun onError(response: String) {
                btnLogin.isEnabled=true
                runOnUiThread {
                    aviProgressInVisible(avi_progress)
                    showErrorAlert(this@SiginInActivity,"Login Failed",response)

                }


            }
        })
    }

    private fun StoreShared(jsonarray: JSONArray) {
        val tinyDb= TinyDB(this@SiginInActivity)
        for (i in 0..jsonarray.length()-1){
            tinyDb.putString(shared_user_id,jsonarray.getJSONObject(i).getString(shared_user_id))
            tinyDb.putString(shared_user_name,jsonarray.getJSONObject(i).getString(shared_user_name))
            tinyDb.putString(shared_user_mobile,jsonarray.getJSONObject(i).getString(shared_user_mobile))
            tinyDb.putString(shared_user_created_on,jsonarray.getJSONObject(i).getString(shared_user_created_on))
        }

    }




    fun GoToSignUp(){
        val  intent= Intent(this@SiginInActivity, SignUpActivity::class.java).apply {  }
        startActivity(intent)
        finish()
    }
}