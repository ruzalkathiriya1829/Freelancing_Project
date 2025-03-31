package com.example.fuelfix.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fuelfix.databinding.ActivitySigninBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loadingBar: ProgressDialog

    private val GOOGLE_SIGN_IN_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("FuelFixSharedPreferences", MODE_PRIVATE)
        loadingBar = ProgressDialog(this)
        loadingBar.setMessage("Please wait...")
        loadingBar.setCancelable(false)

        // Google Sign-In Options (Replace with your Firebase Web Client ID)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1017695695566-p1j4pf2hjd2018jbt6sqi1e1294ptd7k.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Handle Google Sign-In Click
        binding.imgGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Handle Email & Password Login Click
        binding.btnLogin.setOnClickListener {
            emailPasswordLogin()
        }

        // Navigate to Signup Activity
        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.txtForgotPassword.setOnClickListener{
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }
    }

    // Google Sign-In Process
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("Google SignIn", "SignIn Failed: ${e.statusCode}")
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        loadingBar.show()

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            loadingBar.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(this, "Logged in as ${account.email}", Toast.LENGTH_SHORT).show()
                saveLoginData(account.email!!)
                navigateToNextScreen()
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Email & Password Login
    private fun emailPasswordLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate Email Format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        loadingBar.show()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            loadingBar.dismiss()
            if (task.isSuccessful) {
                saveLoginData(email)
                navigateToNextScreen()
            } else {
                val errorMessage = task.exception?.message ?: "Unknown error"
                Log.e("LoginError", "Login Failed: $errorMessage")
                Toast.makeText(this, "Login Failed: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save Login Data in Firebase Database
    private fun saveLoginData(email: String) {
        val loginData = hashMapOf(
            "email" to email,
            "loginTime" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance().reference.child("UserLoginDataTb").push().setValue(loginData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SigninActivity", "Login data saved successfully")
                } else {
                    Log.e("SigninActivity", "Failed to save login data: ${task.exception}")
                }
            }
    }

    // Navigate to the next screen after login
    private fun navigateToNextScreen() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, LocationActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, AgeVerificationActivity::class.java))
            finish()
        }
    }
}