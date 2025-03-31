package com.example.fuelfix.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fuelfix.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class AgeVerificationActivity : AppCompatActivity() {

    private lateinit var imgScanner: ImageView
    private lateinit var btnContinue: Button
    private var imageUri: Uri? = null
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age_verification)

        imgScanner = findViewById(R.id.imgScanner)
        btnContinue = findViewById(R.id.btnContinue)

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("UserAgeVerificationTb")
        storageRef = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        imgScanner.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        btnContinue.setOnClickListener {
            if (imageUri != null) {
                uploadImageToFirebase()
            } else {
                Toast.makeText(this, "Please scan your ID first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "Camera Not Supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imgScanner.setImageBitmap(imageBitmap)
            imageUri = getImageUri(imageBitmap) // Convert to URI for Firebase Storage
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "ID_Scan", null)
        return Uri.parse(path)
    }

    private fun uploadImageToFirebase() {
        val userId = auth.currentUser?.uid ?: UUID.randomUUID().toString()
        val storagePath = storageRef.reference.child("UserAgeVerificationTb/$userId.jpg")

        storagePath.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    saveDataToDatabase(userId, uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataToDatabase(userId: String, imageUrl: String) {
        val userAgeVerification = mapOf(
            "userId" to userId,
            "imageUrl" to imageUrl
        )

        databaseRef.child(userId).setValue(userAgeVerification)
            .addOnSuccessListener {
                Toast.makeText(this, "Age verification successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AgeVerificationActivity, SigninActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
            }
    }
}
