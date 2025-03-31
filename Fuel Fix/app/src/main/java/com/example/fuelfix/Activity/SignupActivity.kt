package com.example.fuelfix.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.fuelfix.ModalClass.SignupModel
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ActivitySignupBinding
import com.facebook.appevents.codeless.internal.ViewHierarchy.setOnClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.Calendar
import java.util.UUID

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var signupModel: SignupModel

    var PICK_IMAGE_REQUEST = 100
    lateinit var storage: FirebaseStorage
    lateinit var bitmap: Bitmap
    var filePath: Uri? = null
    lateinit var images: String

    lateinit var storageReference: StorageReference
    lateinit var sharedPreferences: SharedPreferences

    lateinit var radioGroup: RadioGroup
    lateinit var radioButton: RadioButton
    var selectedRadioButtonId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()

        sharedPreferences = getSharedPreferences("FuelFixSharedPreferences", MODE_PRIVATE)

        initView()
    }

    private fun initView() {
        if (sharedPreferences.getBoolean("isLogin", false) == true) {
            var intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        setOnClickListener()

    }

    private fun setOnClickListener() {

        binding.imgUser.setOnClickListener {
            getImage()
        }

        binding.btnCreateAccount.setOnClickListener {

            createAccount(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())

        }

        binding.txtLogin.setOnClickListener {

            intent = Intent(this@SignupActivity, SigninActivity::class.java)
            startActivity(intent)
        }

    }

    private fun createAccount(email: String, password: String) {

        val registrationTime = Calendar.getInstance().timeInMillis

        //insert data on the real-time database

        //get value from ui
        var email = binding.edtEmail.text.toString()
        var password = binding.edtPassword.text.toString()
        var fname = binding.edtFristName.text.toString()
        var lname = binding.edtLastName.text.toString()
        var address = binding.edtAddress.text.toString()
        var mobileNo = binding.edtMobileNo.text.toString()
        var city = binding.edtCity.text.toString()

        //filePath contains the image URI
        images = filePath.toString()


        radioGroup = findViewById(R.id.rgUserType)
        radioButton = findViewById(R.id.rbUser)
        radioButton = findViewById(R.id.rbAdmin)

        val genderRadioButtonId = binding.rgGender.checkedRadioButtonId
        val gender = findViewById<RadioButton>(genderRadioButtonId)?.text.toString()

        // Get the selected radio button id
        selectedRadioButtonId = radioGroup.checkedRadioButtonId

        // Initialize userType with default value
        var userType = -1

        if (selectedRadioButtonId != -1) {
            userType = when (selectedRadioButtonId) {
                R.id.rbUser -> 0
                R.id.rbAdmin -> 1
                else -> -1 // Handle other cases if needed
            }
        }

        if (email.isEmpty()) {
            binding.txtEmail.error = "Please enter email. "
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmail.error = "Please enter valid email."
            binding.edtEmail.addTextChangedListener {
                binding.txtEmail.error = ""

            }
        } else if (password.isEmpty()) {
            binding.txtPassword.error = "Please enter password."
            binding.edtPassword.addTextChangedListener {
                binding.txtPassword.error = ""

            }
        } else if (fname.isEmpty()) {
            binding.txtFristName.error = "Please enter first name."
            binding.edtFristName.addTextChangedListener {
                binding.txtPassword.error = ""

            }
        } else if (lname.isEmpty()) {
            binding.txtLastName.error = "Please enter Last name."
            binding.edtLastName.addTextChangedListener {
                binding.txtLastName.error = ""

            }
        } else if (mobileNo.isEmpty()) {
            binding.txtMobileNo.error = "Please enter Mobile number."
            binding.edtMobileNo.addTextChangedListener {
                if (binding.edtMobileNo.text.toString().length >= 10) {
                    binding.txtMobileNo.error = "Please check mobile number."
                } else if (binding.edtMobileNo.text.toString().length == 10) {
                    binding.txtMobileNo.error = ""

                }
            }
        } else if (address.isEmpty()) {
            binding.txtAddress.error = "Please enter address."
            binding.edtAddress.addTextChangedListener {
                binding.txtAddress.error = ""

            }
        } else if (city.isEmpty()) {
            binding.txtCity.error = "Please enter city."
            binding.edtCity.addTextChangedListener {
                binding.txtCity.error = ""

            }
        } else {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please wait...")

            progressDialog.show()

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        // Dismiss dialog
                        progressDialog.dismiss()

                        //stored in shared preference
                        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                        myEdit.putBoolean("isLogin", true)
                        myEdit.putString("email", email)
                        myEdit.putString("password", password)
                        myEdit.putString("fname", fname)
                        myEdit.putString("lname", lname)
                        myEdit.putString("address", address)
                        myEdit.putString("mobileNo", mobileNo)
                        myEdit.putString("city", city)
                        myEdit.putString("gender", gender)
                        myEdit.putString("images", images)
                        myEdit.putInt("userType", userType)
                        myEdit.commit()

                        Toast.makeText(
                            this@SignupActivity,
                            "Congratulation!! Account created successfully.",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (userType == 0) {
                            // User registration, store data in UserTb
                            val userSignupModel = SignupModel(
                                email,
                                password,
                                fname,
                                lname,
                                address,
                                mobileNo,
                                city,
                                gender,
                                userType,
                                images,
                                registrationTime
                            )
                            firebaseDatabase.reference.root.child("UserTb").child(fname)
                                .setValue(userSignupModel)
                        } else if (userType == 1) {
                            // Admin registration, store data in AdminTb
                            val adminSignupModel = SignupModel(
                                email,
                                password,
                                fname,
                                lname,
                                address,
                                mobileNo,
                                city,
                                gender,
                                userType,
                                images,
                                registrationTime
                            )
                            firebaseDatabase.reference.root.child("AdminTb").child(fname)
                                .setValue(adminSignupModel)
                        }

                        val intent = Intent(
                            this@SignupActivity,
                            MainActivity::class.java
                        )
                        startActivity(intent)
                        finish()

                    } else {
                        progressDialog.dismiss()
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@SignupActivity,
                            "This Email Already used Please Use Different Email.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun getImage() {
        binding.imgUser.setOnClickListener {

            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(
                Intent.createChooser(intent, "Select Image from here..."),
                PICK_IMAGE_REQUEST
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // then set image in the image view

        if (data != null) {

            if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK) && (data != null) && (data.getData() != null)) {

                // Get the Uri of data
                filePath = data?.getData()!!
                try {

                    uploadImage()
                    // Setting image on image view using Bitmap
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    binding.imgUser.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    // Log the exception
                    e.printStackTrace()
                }
            }

        }

    }

    private fun uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref: StorageReference =
                storageReference.child("SignupImages/" + UUID.randomUUID().toString())

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath!!)
                .addOnCompleteListener {
                    ref.downloadUrl.addOnSuccessListener { images = it.toString() }
                }
                .addOnSuccessListener {
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                }

                .addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast.makeText(
                        this, "Failed to upload image. " + e.message, Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded " + progress.toInt() + "%"
                    )
                }
        }
    }
}

