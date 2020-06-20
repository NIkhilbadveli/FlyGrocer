package com.titos.flygrocer

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import java.util.jar.Manifest

class TrackingFragment : Fragment() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var locationManager: LocationManager
    private val MIN_TIME = 1000
    private val MIN_DISTANCE = 1 //minimum distance the location needs to be changed to get an update
    private lateinit var mapMarker: Marker
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutView = inflater.inflate(R.layout.fragment_track, container, false)
        dbRef = FirebaseDatabase.getInstance().reference.child("driverData")

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapTracking) as SupportMapFragment
        mapFragment.getMapAsync{
            googleMap = it
            val sydney = LatLng(-34.0, 151.0)
            mapMarker = it.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            it.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

        //Location updates - we have to use this in delivery app
        locationManager = activity?.getSystemService(Activity.LOCATION_SERVICE) as LocationManager

        getLocationUpdates()

        changeLocation()

        return layoutView
    }

    private fun changeLocation() {
        dbRef.child("testUser/location").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val lat = p0.child("latitude").value.toString().toDouble()
                    val long = p0.child("longitude").value.toString().toDouble()
                    val newLocation = LatLng(lat, long)
                    mapMarker.position = newLocation
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
                }
            }

        })
    }

    private fun getLocationUpdates() {
        val permissionGranted = (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)

        if (locationManager!= null){
            //https://stackoverflow.com/a/10311891 use this answer to implement dialog if location is not enabled in driver app
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val netGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (permissionGranted){
                if (gpsEnabled)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME.toLong(), MIN_DISTANCE.toFloat(), locationListener)
                else if (netGpsEnabled)
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME.toLong(), MIN_DISTANCE.toFloat(), locationListener)
                else
                    Toast.makeText(requireContext(), "Location is not enabled!", Toast.LENGTH_SHORT).show()
            }
            else
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates()
            }
            else
                Toast.makeText(requireContext(), "Please accept location permission", Toast.LENGTH_SHORT).show()
        }
    }

    private val locationListener = object : LocationListener{
        override fun onLocationChanged(location: Location?) {
            if (location!=null){
                dbRef.child("testUser/location").child("latitude").setValue(location.latitude)
                dbRef.child("testUser/location").child("longitude").setValue(location.longitude)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }
    }
}