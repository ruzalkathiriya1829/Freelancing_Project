package com.example.fuelfix.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fuelfix.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var phoneNumber: String  // ✅ Declare globally

    private lateinit var etOtp: EditText
    private lateinit var btnVerifyOtp: Button
    private lateinit var tvResendOtp: TextView
    private lateinit var tvChangeNumber: TextView
    private lateinit var tvPhoneNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        auth = FirebaseAuth.getInstance()

        etOtp = findViewById(R.id.etOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
        tvResendOtp = findViewById(R.id.tvResendOtp)
        tvChangeNumber = findViewById(R.id.tvChangeNumber)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)

        // ✅ Correctly initialize phoneNumber & verificationId
        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        storedVerificationId = intent.getStringExtra("verificationId")

        tvPhoneNumber.text = phoneNumber

        // Verify OTP
        btnVerifyOtp.setOnClickListener {
            val otp = etOtp.text.toString().trim()
            if (otp.length == 6) {
                verifyOTP(otp)
            } else {
                Toast.makeText(this, "Enter valid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Resend OTP
        tvResendOtp.setOnClickListener {
            if (resendToken != null) {
                resendOTP(phoneNumber, resendToken!!)
            } else {
                Toast.makeText(this, "Please wait for OTP timeout", Toast.LENGTH_SHORT).show()
            }
        }

        // Change Phone Number
        tvChangeNumber.setOnClickListener {
            startActivity(Intent(this, PhoneAuthActivity::class.java))
            finish()
        }
    }

    private fun verifyOTP(otp: String) {
        if (phoneNumber == "+919925827865" && otp == "290604") { // ✅ Corrected test number check
            Toast.makeText(this, "Test OTP Verified!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AgeVerificationActivity::class.java))
            finish()
        } else {
            if (storedVerificationId == null) {  // ✅ Check if verification ID is null
                Toast.makeText(this, "Error: Verification ID not found", Toast.LENGTH_SHORT).show()
                return
            }
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
            signInWithCredential(credential)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "OTP Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resendOTP(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OTPVerificationActivity, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            })
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}