package com.example.fuelfix.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fuelfix.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var etPhoneNumber: EditText
    private lateinit var btnGetOtp: Button
    private lateinit var btnGuestUser: Button
    private lateinit var checkBoxTerms: CheckBox
    private lateinit var auth: FirebaseAuth
    lateinit var verificationId: String
    lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnGetOtp = findViewById(R.id.btnGetOtp)
        btnGuestUser = findViewById(R.id.btnGuestUser)
        checkBoxTerms = findViewById(R.id.checkBoxTerms)
        auth = FirebaseAuth.getInstance()

        btnGetOtp.setOnClickListener {
            val phoneNumber = "+91" + etPhoneNumber.text.toString().trim()

            if (phoneNumber.length != 13) {
                Toast.makeText(this, "Enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!checkBoxTerms.isChecked) {
                Toast.makeText(this, "You must agree to Terms & Privacy Policy", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendVerificationCode(phoneNumber)
        }

        btnGuestUser.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        if (phoneNumber == "+919925827865") {  // Check if it's a test number
            val intent = Intent(this, OTPVerificationActivity::class.java)
            intent.putExtra("verificationId", "test_verification_id") // Fake verification ID
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
        } else {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }



    private val  mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)

                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                val code = phoneAuthCredential.smsCode

                if (code != null) {

                    val intent = Intent(this@PhoneAuthActivity, OTPVerificationActivity::class.java)
                    intent.putExtra("verificationId", verificationId)  // Pass verificationId
                    intent.putExtra("phoneNumber", phoneNumber)
                    startActivity(intent)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // displaying error message with firebase exception.
                Toast.makeText(this@PhoneAuthActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

//    private fun sendOTP(phoneNumber: String) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNumber)
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(this)
//            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                    signInWithCredential(credential)
//                }
//
//                override fun onVerificationFailed(e: FirebaseException) {
//                    Toast.makeText(applicationContext, "OTP Verification Failed: ${e.message}", Toast.LENGTH_LONG).show()
//                }
//
//                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
//                    super.onCodeSent(verificationId, token)
//                    val intent = Intent(this@PhoneAuthActivity, OTPVerificationActivity::class.java)
//                    intent.putExtra("verificationId", verificationId)  // Pass verificationId
//                    intent.putExtra("phoneNumber", phoneNumber)
//                    startActivity(intent)
//                }
//            })
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Auto Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}