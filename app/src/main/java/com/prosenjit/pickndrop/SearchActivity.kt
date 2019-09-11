package com.prosenjit.pickndrop
import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.custom_search_toolbar.*
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.libraries.places.api.model.Place
import java.util.*
import java.util.Arrays.asList
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.libraries.places.widget.Autocomplete
import android.content.Intent
import android.content.res.Resources
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.prosenjit.pickndrop.NetworkCall.CustomResponse
import com.prosenjit.pickndrop.NetworkCall.NetworkUrlRequest
import com.prosenjit.pickndrop.NetworkCall.WebUrls
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener
import org.json.JSONObject


class SearchActivity : Base() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
       /* Places.initialize(this@SearchActivity, "AIzaSyDMiMgNayRuRApkNq1O1hb93lbaZMHQoUc")
        autoCompleteEditText.setOnClickListener { view ->
            val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME)
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry("IN")
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }*/

        autoCompleteEditText.setOnPlaceSelectedListener(object :OnPlaceSelectedListener{
            override fun onPlaceSelected(place: com.seatgeek.placesautocomplete.model.Place) {
                Log.e( "","Place: " + place.description + ", " + place.place_id)
            }

        })
        FetchPlaceAutoComplete()
    }
    private fun FetchPlaceAutoComplete() {
        val parameter= JSONObject()
        parameter.put("input","shyambazar")
        parameter.put("key","AIzaSyC8FH0zMLb06c4uzH8ZMJ8Uu6ABvUTxQEc")
        parameter.put("types","locality")
        parameter.put("location","22.978624,87.747803")
        NetworkUrlRequest(WebUrls.PLACE_AUTOCOMPLETE_URL,parameter,METHOD_POST,object : CustomResponse.Listener<String>{
            override fun onSuccessResponse(response: String) {
                try {
                    val place_auto_obj = JSONObject(response)
                    Log.e("place_auto_obj",""+place_auto_obj)
                }catch (e:Exception){
                    Log.e("ex fetching place##",""+e)

                }

            }

            override fun onError(response: String) {
                Log.e("onError fetching place#",""+response)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Base.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.e( "","Place: " + place.name + ", " + place.id)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.e("", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.e("RESULT_CANCELED", "True")
            }
        }
    }


}
