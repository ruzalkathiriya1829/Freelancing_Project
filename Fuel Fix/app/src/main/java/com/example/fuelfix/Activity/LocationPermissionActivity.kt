package com.example.fuelfix.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fuelfix.databinding.ActivityLocationPermissionBinding

class LocationPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationPermissionBinding
    private val LOCATION_PERMISSION_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtWhileUsing.setOnClickListener {
            requestLocationPermission()
        }

        binding.txtOnlyThisTime.setOnClickListener {
            requestLocationPermission()
        }

        binding.txtDontAllow.setOnClickListener {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }

        binding.btnContinue.setOnClickListener {
            if (isLocationPermissionGranted()) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please allow location access", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}