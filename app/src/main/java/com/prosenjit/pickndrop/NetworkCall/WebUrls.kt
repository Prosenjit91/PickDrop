package com.prosenjit.pickndrop .NetworkCall

import androidx.core.os.HandlerCompat.postDelayed



class WebUrls {

    companion object {
        const val BASE_URL="http://13.233.207.27/pickanddrop/api/"
        const val getLatLong_URL= BASE_URL+"location/getLocationByLatLong"
        const val addLatLong_URL= BASE_URL+"location/addLocation"
        const val user_SignUp_URL= BASE_URL+"user/signup"
        const val user_LogIn_URL= BASE_URL+"user/login"
        const val PLACE_AUTOCOMPLETE_URL= "https://maps.googleapis.com/maps/api/place/autocomplete/json?"
       // https://maps.googleapis.com/maps/api/place/autocomplete/json?input=1600+Amphitheatre&key=<API_KEY>&sessiontoken=1234567890
    }
}