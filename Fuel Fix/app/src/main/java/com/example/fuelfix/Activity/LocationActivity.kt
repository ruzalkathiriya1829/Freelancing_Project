package com.example.fuelfix.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.fuelfix.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase

class LocationActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var btnEnableLocation: Button
    lateinit var btnEnterLocationManually: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        btnEnableLocation = findViewById(R.id.btnEnableLocation)
        btnEnterLocationManually = findViewById(R.id.btnEnterLocationManually)

        btnEnableLocation.setOnClickListener {
            getCurrentLocation()
        }

        btnEnterLocationManually.setOnClickListener {
            startActivity(Intent(this@LocationActivity, LocationSelectionActivity::class.java))
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                saveLocationToFirebase(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLocationToFirebase(latitude: Double, longitude: Double) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child("user_location")
        val locationData = mapOf("latitude" to latitude, "longitude" to longitude)

        databaseRef.setValue(locationData).addOnSuccessListener {
            Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to Save Location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}