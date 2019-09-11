package com.prosenjit.pickndrop

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.prosenjit.pickndrop .NetworkCall.CustomResponse
import com.prosenjit.pickndrop .NetworkCall.WebUrls
import com.prosenjit.pickndrop.NetworkCall.NetworkUrlRequest
import com.prosenjit.pickndrop.Registrarion.SiginInActivity
import com.prosenjit.pickndrop.SharedPrefernce.TinyDB
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONObject
import java.util.*
import android.location.LocationManager as LocationManager


class MapsActivity : Base(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {


    private var service: LocationManager? = null
    private var enabled: Boolean? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private lateinit var mMap: GoogleMap
    private var REQUEST_LOCATION_CODE = 101
    private val UPDATE_INTERVAL = 5000
    private val FASTEST_INTERVAL = 3000
    private val DISPLACEMENT = 10
     var latititude:Double = 0.0
     var longitude:Double = 0.0
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val tinyDb= TinyDB(this@MapsActivity)
        Log.e("On create**","True");
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        buildGoogleApiClient()
        createLocationRequest()
        img_setting.setOnClickListener {
            tinyDb.clear()
            val intent = Intent(this@MapsActivity, SiginInActivity::class.java).apply { }
            startActivity(intent)
            finish()
        }
        cv_drop.setOnClickListener {

            val intent = Intent(this@MapsActivity, SearchActivity::class.java).apply { }
            startActivity(intent)
            overridePendingTransition(0,0)
        }
    }
    override fun onLocationChanged(location: Location?) {
        Log.e("onLocationChanged","True"+location)
        mLastLocation = location
        //Place current location marker
        val latLng = LatLng(location!!.latitude, location.longitude)
        latititude=location.latitude
        longitude=location.longitude
        MoveCameraSetMArker(latLng)

    }

    private fun MoveCameraSetMArker(latLng: LatLng) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.draggable(false)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_pin))
        mCurrLocationMarker = mMap.addMarker(markerOptions)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f))
        setAddressName()
    }

    override fun onConnected(p0: Bundle?) {
        Log.e("onConnected**","True");
        createLocationRequest()
        startLocationUpdates()
        // Check if enabled and if not send user to the GPS settings
        FetchMarkerperiod()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e("onConnectionSuspended**","True");
        mGoogleApiClient.connect()
    }
    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("onConnectionFailed**","True"+p0.errorMessage)
        mGoogleApiClient.connect()
    }
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        Log.e("onMapReady**","True")
        SettingGoogleMap(googleMap)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this@MapsActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@MapsActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MapsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_CODE
                )
                return;
            } else {
                // Write you code here if permission already given.
                if(!isLocationEnabled(this@MapsActivity)) {
                   displayLocationSettingsRequest(this@MapsActivity)
                }
            }
        }else{
            if(!isLocationEnabled(this@MapsActivity)) {
                displayLocationSettingsRequest(this@MapsActivity)
            }
            mMap.isMyLocationEnabled = true
        }

    }
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = UPDATE_INTERVAL.toLong()
        mLocationRequest.fastestInterval = FASTEST_INTERVAL.toLong()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.smallestDisplacement = DISPLACEMENT.toFloat()
    }
    private fun startLocationUpdates() {
       // Check if permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }
    }
    private fun stopLocationUpdates() {
        // Check if permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this as com.google.android.gms.location.LocationListener
            )
        }
    }
    private fun SettingGoogleMap(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isTrafficEnabled = false
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = false
        mMap.setOnCameraIdleListener(this@MapsActivity)
        mMap.setOnCameraMoveStartedListener(this@MapsActivity)
    }

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        mGoogleApiClient.connect()
    }
    private fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          // This is new method provided in API 28
            return lm.isLocationEnabled();
        }else {
             val mode = Settings.Secure.getInt(
                 mContext.getContentResolver(), Settings.Secure.LOCATION_MODE,
                 Settings.Secure.LOCATION_MODE_OFF
             )
             return  (mode != Settings.Secure.LOCATION_MODE_OFF);
         }

    }
    private fun displayLocationSettingsRequest(context: Context) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)

        val result: PendingResult<LocationSettingsResult>
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.e("Permission", "All location settings are satisfied.")
                    startLocationUpdates()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.e("Permission", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ")

                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this@MapsActivity, 500)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("Permission", "PendingIntent unable to execute request.")
                    }

                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e("Permission", "Location settings are inadequate, and cannot be fixed here. Dialog not created.")
            }
        }
    }



    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("", "Permission has been denied by user")
                    AlertDialog.Builder(this@MapsActivity)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            finish()
                        })
                        .create()
                        .show()
                } else {
                    Log.e("", "Permission has been granted by user")
                    mMap.isMyLocationEnabled = true
                    if(!isLocationEnabled(this@MapsActivity)) {
                        displayLocationSettingsRequest(this@MapsActivity)
                    }
                    startLocationUpdates()
                }
            }
        }
    }
    private fun FetchMarkerperiod() {
        // Initialize the handler instance
        mHandler = Handler()
        mHandler.postDelayed(object : Runnable {
                override fun run() {
                    FetchLatLong()          // this method will contain your almost-finished HTTP calls
                    mHandler.postDelayed(this, 5000)
                }
            }, 5000)

    }
    private fun FetchLatLong() {
        val parameter= JSONObject()
        parameter.put("user_lat",""+latititude)
        parameter.put("user_long",""+longitude)
        NetworkUrlRequest(WebUrls.getLatLong_URL,parameter,METHOD_POST,object : CustomResponse.Listener<String>{
            override fun onSuccessResponse(response: String) {
                try {
                    val jsonObject= JSONObject(response)
                    val error=jsonObject.getString(ERROR_NAME)
                    try{
                        val message=jsonObject.getString(RESULT_MSG)
                    }catch (e:Exception){}

                    if(error.equals(Base.ERROR_SUSSESS)){
                        val jsonArray=jsonObject.getJSONArray("USERLATLONG_DETAILS")
                        Log.e("USERLATLONG_DETAILS","length**"+jsonArray.length()+"====>"+jsonArray)
                        if(jsonArray.length()>0) {
                            for (i in 0 until jsonArray.length()) {
                                val lat = jsonArray.getJSONObject(i).getString("user_lat")
                                val long = jsonArray.getJSONObject(i).getString("user_long")
                               runOnUiThread {
                                   // mMap.clear()
                                    createMarker(lat.toDouble(), long.toDouble())
                                }

                            }
                        }
                    }else{

                    }



                }catch (e:Exception){
                    Log.e("Setting Adapter ex##",""+e)

                }

            }

            override fun onError(response: String) {

            }
        })

    }
    protected fun createMarker(
        latitude: Double,
        longitude: Double
    ): Marker {

        return mMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_car_24))
        )
    }
    private fun AddLatLong() {
        val parameter= JSONObject()
        parameter.put("user_lat",""+latititude)
        parameter.put("user_long",""+longitude)
        parameter.put("user_id","5")
        NetworkUrlRequest(WebUrls.addLatLong_URL,parameter,METHOD_POST,object : CustomResponse.Listener<String>{
            override fun onSuccessResponse(response: String) {
                try {
                    val jsonObject= JSONObject(response)
                    val error=jsonObject.getString(ERROR_NAME)
                    try{
                        val message=jsonObject.getString(RESULT_MSG)
                    }catch (e:Exception){}

                    if(error.equals(ERROR_SUSSESS)){

                    }else{

                    }



                }catch (e:Exception){
                    Log.e("Setting Adapter ex##",""+e)

                }

            }

            override fun onError(response: String) {

            }
        })

    }
    @SuppressLint("MissingPermission")
    override fun onCameraIdle() {
        try {
            val latLng = mMap.getCameraPosition().target
            latititude = latLng.latitude
            longitude = latLng.longitude
            Log.e("onCameraIdle lat Long", "" + latititude + "--->" + longitude)
            imgPinUp?.visibility = View.GONE
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Position")
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_pin))
            mCurrLocationMarker = mMap.addMarker(markerOptions)
            mMap.isMyLocationEnabled = true
            setAddressName()
        }catch (ex:Exception){}

    }



    override fun onCameraMoveStarted(p0: Int) {
      //  mMap.clear()
        // display imageView
        if(mCurrLocationMarker!=null){
            mCurrLocationMarker!!.remove()
        }
        imgPinUp?.visibility = View.VISIBLE
    }
    private fun setAddressName(){
        var locAdd=""
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(latititude, longitude, 1)
            val address = addresses[0].getAddressLine(0)
            locAdd=address
        }catch (e:Exception){}

        Log.e("#####","==>"+locAdd)
       tv_pickUpLocation.text=locAdd
    }

}