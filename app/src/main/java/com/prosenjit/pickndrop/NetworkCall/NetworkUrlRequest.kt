package com.prosenjit.pickndrop.NetworkCall

import android.util.Log
import com.prosenjit.pickndrop .NetworkCall.CustomResponse
import com.prosenjit.pickndrop.Application.PicknDropApplication
import com.prosenjit.pickndrop.Base
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NetworkUrlRequest( val url: String,
                         val postdata: JSONObject,
                         val callingMethod :String,
                         val mListener: CustomResponse.Listener<String>) {




    init {
        if(PicknDropApplication.hasNetwork()){
            CallhttpRequest()
        }else{

        }

        }

    @Throws(IOException::class)
    fun CallhttpRequest(

    ) {
        Log.e("Url**", url + "?" + postdata)
        val client = OkHttpClient()
        lateinit var request: Request
        if(callingMethod.equals(Base.METHOD_POST)) {
            Log.e("METHOD**", "POST")
            val MEDIA_TYPE = MediaType.parse("application/json")
            val body = RequestBody.create(MEDIA_TYPE, postdata.toString())
             request = Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
        }else if(callingMethod.equals(Base.METHOD_GET)){
            Log.e("METHOD**", "GET")
              request = Request.Builder()
                .url(url)
                .get()
                .header("Content-Type", "application/json")
                .build()
        }


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val mMessage = e.message.toString()
                Log.e("failure Response", mMessage)
                // avi_progress.hide()
                mListener.onError(mMessage)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val mMessage = response.body()!!.string()
                Log.e("success Response", mMessage)
                // avi_progress.hide()
                mListener.onSuccessResponse(mMessage)
            }
        })
    }
}



