package com.prosenjit.pickndrop

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.PhoneAuthProvider
import com.wang.avi.AVLoadingIndicatorView

open class Base :AppCompatActivity(){
    fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
    fun ChangeStausBarColour(activity: Activity, color: Int){
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(activity, color)
        }
    }
    fun errorShake(target: View){
        YoYo.with(Techniques.Shake)
            .duration(3000)
            .playOn(target)
    }
    companion object {
        val METHOD_GET="1"
        val METHOD_POST="2"
        val ERROR_NAME="ERROR"
        val ERROR_SUSSESS="0"
        val ERROR_FAILED="1"
        val RESULT_MSG="MESSAGE"
        val shared_user_name="user_name"
        val shared_user_id="user_id"
        val shared_user_mobile="user_mobile"
        val shared_user_address="user_address"
        val shared_user_created_on="user_created_on"
        val PLACE_PICKER_REQUEST=821
        val AUTOCOMPLETE_REQUEST_CODE=522
        lateinit var  verificationId:String
        lateinit var resendingToken: PhoneAuthProvider.ForceResendingToken
        lateinit var phoneNo:String
        fun aviProgressVisible(avLoadingIndicatorView: AVLoadingIndicatorView, indicator_name: String) {
            if(avLoadingIndicatorView.visibility==View.GONE) {
                avLoadingIndicatorView.visibility = View.VISIBLE
                avLoadingIndicatorView.setIndicator(indicator_name)
            }
        }

        fun aviProgressInVisible(avLoadingIndicatorView: AVLoadingIndicatorView) {
            if(avLoadingIndicatorView.visibility==View.VISIBLE) {
                avLoadingIndicatorView.visibility = View.GONE
            }
        }
    }
    fun showErrorAlert(context: Context,title:String,message:String){
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
               dialog.dismiss()
            })
            .create()
            .show()
    }
}