package com.prosenjit.pickndrop

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.prosenjit.pickndrop.Application.PicknDropApplication
import com.prosenjit.pickndrop.Receiver.ConnectivityReceiver
import com.prosenjit.pickndrop.Registrarion.SiginInActivity
import com.prosenjit.pickndrop.SharedPrefernce.TinyDB
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : Base(), ConnectivityReceiver.ConnectivityReceiverListener {
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        PicknDropApplication.getInstance().setConnectivityListener(this)
        val handler = Handler()
        handler.postDelayed({
            val tinyDb= TinyDB(this@SplashActivity)
            if(tinyDb.getString(shared_user_id).isEmpty()) {
                val intent = Intent(this@SplashActivity, SiginInActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this@SplashActivity, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 3000)

    }

}
