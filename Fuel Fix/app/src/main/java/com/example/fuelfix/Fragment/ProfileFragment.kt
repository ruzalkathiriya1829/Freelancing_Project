package com.example.fuelfix.Fragment

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.bumptech.glide.Glide
import com.example.fuelfix.Activity.SigninActivity
import com.example.fuelfix.R
import com.example.fuelfix.databinding.FragmentProfileBinding
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        sharedPreferences = requireActivity().getSharedPreferences("FuelFixSharedPreferences", AppCompatActivity.MODE_PRIVATE)

        initView()
        getDataFromLoginProviders()

        return binding.root
    }

    private fun getDataFromLoginProviders() {
        val firebaseUser = auth.currentUser

        if (GoogleSignIn.getLastSignedInAccount(requireContext()) != null) {
            val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
            updateUI(googleSignInAccount?.displayName, googleSignInAccount?.email, googleSignInAccount?.photoUrl?.toString())
        }
//        else if (AccessToken.getCurrentAccessToken() != null) {
//            updateUI(firebaseUser?.displayName, firebaseUser?.email, firebaseUser?.photoUrl?.toString())
//        }
        else if (firebaseUser != null) {
            getProfileFromFirebase()
        }
    }

    private fun updateUI(displayName: String?, email: String?, image: String?) {
        binding.txtUsername.text = displayName ?: "User"
        binding.txtEmail.text = email ?: "No Email"

        if (!image.isNullOrBlank()) {
            Glide.with(this).load(image).into(binding.imgImage)
        } else {
            Glide.with(requireContext()).load(R.drawable.user).into(binding.imgImage)
        }
    }

    private fun getProfileFromFirebase() {
        auth.currentUser?.let { user ->
            firebaseDatabase.reference.child("usersTb").child(user.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Log.d("ProfileFragment", "User data found: $snapshot")
                            val fname = snapshot.child("fname").getValue(String::class.java) ?: "User"
                            val email = snapshot.child("email").getValue(String::class.java) ?: "No Email"

                            binding.txtUsername.text = fname
                            binding.txtEmail.text = email
                        } else {
                            Log.e("ProfileFragment", "User data not found in Firebase")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ProfileFragment", "Failed to fetch data: ${error.message}")
                        Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }



    private fun initView() {
        binding.btnLogOut.setOnClickListener {
            logout()
        }

        binding.linearSave.setOnClickListener{
            openLikedFragment()
        }

//        binding.linearSetting.setOnClickListener {
//            startActivity(Intent(requireContext(), SettingsActivity::class.java))
//        }

        binding.linearCalendar.setOnClickListener {
            openDatePicker()
        }

        binding.linearHelpSupport.setOnClickListener {
            val url = "https://support.link.com/search?q=interior+app"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "No browser found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openLikedFragment() {
        val likedFragment = LikeFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, likedFragment) // Replace with your actual FragmentContainer ID
            .addToBackStack(null)
            .commit()
    }

    private fun openDatePicker() {
        DatePickerBuilder(requireContext(), object : OnSelectDateListener {
            override fun onSelect(date: List<java.util.Calendar>) {
                Toast.makeText(requireContext(), "Date Selected: ${date[0].time}", Toast.LENGTH_SHORT).show()
            }
        })
            .pickerType(com.applandeo.materialcalendarview.CalendarView.ONE_DAY_PICKER)
            .headerColor(R.color.yellow)
            .todayLabelColor(R.color.green)
            .selectionColor(R.color.green)
            .dialogButtonsColor(R.color.blue)
            .build()
            .show()
    }

    private fun logout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logging out")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                sharedPreferences.edit().remove("isLogin").apply()
                auth.signOut()
                signOutFromGoogle()
                navigateToLoginActivity()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(context, SigninActivity::class.java))
        Toast.makeText(context, "Successfully Logged Out", Toast.LENGTH_SHORT).show()
    }

    private fun signOutFromGoogle() {
        googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful) auth.signOut()
        }
    }
}
