package com.example.fuelfix.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fuelfix.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ManageStationsActivity : AppCompatActivity() {

    private lateinit var imgStationPreview: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnAddStation: Button
    private lateinit var edtStationName: EditText
    private lateinit var edtStationLocation: EditText
    private lateinit var edtStationReview: EditText
    private lateinit var edtFuelPrice: EditText

    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_stations)

        imgStationPreview = findViewById(R.id.imgStationPreview)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnAddStation = findViewById(R.id.btnAddStation)
        edtStationName = findViewById(R.id.edtStationName)
        edtStationLocation = findViewById(R.id.edtStationLocation)
        edtStationReview = findViewById(R.id.edtStationReview)
        edtFuelPrice = findViewById(R.id.edtFuelPrice)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading...")
        progressDialog.setCancelable(false)

        btnSelectImage.setOnClickListener {
            selectImage()
        }

        btnAddStation.setOnClickListener {
            uploadData()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imgStationPreview.setImageURI(imageUri)
        }
    }

    private fun uploadData() {
        val stationName = edtStationName.text.toString().trim()
        val stationLocation = edtStationLocation.text.toString().trim()
        val stationReview = edtStationReview.text.toString().trim()
        val fuelPrice = edtFuelPrice.text.toString().trim()

        // ✅ Validate Inputs
        if (stationName.isEmpty() || stationLocation.isEmpty() || stationReview.isEmpty() || fuelPrice.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Ensure Fuel Price is a valid number
        val fuelPriceValue = fuelPrice.toDoubleOrNull()
        if (fuelPriceValue == null || fuelPriceValue <= 0) {
            Toast.makeText(this, "Please enter a valid fuel price", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Disable Button & Show Progress Dialog
        btnAddStation.isEnabled = false
        progressDialog.show()

        val storageRef = FirebaseStorage.getInstance().reference.child("station_images/${System.currentTimeMillis()}.jpg")

        val uploadTask = storageRef.putFile(imageUri!!)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                saveStationData(stationName, stationLocation, stationReview, fuelPriceValue, uri.toString())
            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            btnAddStation.isEnabled = true
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveStationData(name: String, location: String, review: String, price: Double, imageUrl: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("PetrolStationsTb")
        val stationId = databaseRef.push().key ?: return

        val stationData = mapOf(
            "name" to name,
            "location" to location,
            "review" to review,
            "fuelPrice" to price.toString(),
            "imageUrl" to imageUrl
        )

        databaseRef.child(stationId).setValue(stationData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                btnAddStation.isEnabled = true
                Toast.makeText(this, "Station Added Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                btnAddStation.isEnabled = true
                Toast.makeText(this, "Failed to add station", Toast.LENGTH_SHORT).show()
            }
    }
}
