package com.example.fuelfix.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.fuelfix.databinding.ActivityLocationSelectionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class LocationSelectionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityLocationSelectionBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var selectedLatLng: LatLng? = null
    private var userEmail: String? = null // Nullable to prevent crashes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Fetch user email from Firebase Authentication
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        userEmail = firebaseUser?.email

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "User email not found!", Toast.LENGTH_SHORT).show()
            finish() // Close activity if email is missing
            return
        }

        // Initialize Map
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        // Initialize location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnContinue.setOnClickListener {
            if (selectedLatLng == null) {
                Toast.makeText(this, "Please select a valid location!", Toast.LENGTH_SHORT).show()
            } else {
                saveLocationToFirebase(selectedLatLng!!.latitude, selectedLatLng!!.longitude)
                intent = Intent(this@LocationSelectionActivity,DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        googleMap.isMyLocationEnabled = true

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))

                // Set initial location
                selectedLatLng = userLatLng
                updateLocationInfo(userLatLng)
            }
        }

        // Capture new location when map is moved
        googleMap.setOnCameraIdleListener {
            val center = googleMap.cameraPosition.target
            selectedLatLng = center
            updateLocationInfo(center)
        }
    }

    private fun updateLocationInfo(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addressList != null && addressList.isNotEmpty()) {
            val address = addressList[0].getAddressLine(0)
            binding.txtLocationAddress.text = address
        } else {
            binding.txtLocationAddress.text = "Location not found"
        }
    }

    private fun saveLocationToFirebase(latitude: Double, longitude: Double) {
        val sanitizedEmail = userEmail!!.replace(".", "_") // Replace '.' in email for Firebase key
        val databaseRef = FirebaseDatabase.getInstance().getReference("usersLocationTb").child(sanitizedEmail)

        val locationData = mapOf("latitude" to latitude, "longitude" to longitude)

        databaseRef.setValue(locationData).addOnSuccessListener {
            Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to Save Location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}