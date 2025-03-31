package com.example.fuelfix.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fuelfix.Adapter.LikeAdapter
import com.example.fuelfix.ModalClass.StationModel
import com.example.fuelfix.databinding.FragmentLikeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LikeFragment : Fragment() {

    lateinit var binding: FragmentLikeBinding
    private lateinit var likedStationList: ArrayList<StationModel>
    private lateinit var likedAdapter: LikeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLikeBinding.inflate(layoutInflater)

        initView()
        fetchLikedPetrolStations()

        // Inflate the layout for this fragment
        return binding.root

    }

    private fun initView() {
        likedStationList = ArrayList()
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        likedAdapter = LikeAdapter(requireContext(), likedStationList) {
            showEmptyMessage() // Callback function to show empty message
        }

        binding.rcvLikePetrolStations.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvLikePetrolStations.adapter = likedAdapter
    }

    fun showEmptyMessage() {
        binding.txtEmptyMessage.visibility = View.VISIBLE
        binding.rcvLikePetrolStations.visibility = View.GONE
    }


    private fun fetchLikedPetrolStations() {
        val userId = auth.currentUser?.uid ?: return

        val likedStationsRef = firebaseDatabase.reference
            .child("Users")  // Verify if this is the correct path in your Firebase
            .child(userId)
            .child("LikedStations")

        likedStationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                likedStationList.clear()

                if (!snapshot.exists()) {
                    showEmptyMessage()
                    return
                }

                val likedStationIds = snapshot.children.mapNotNull { it.key } // Collect all liked station IDs

                // Now fetch all stations in a single query
                val stationRef = firebaseDatabase.reference.child("PetrolStationsTb")
                stationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(stationSnapshot: DataSnapshot) {
                        likedStationList.clear() // Clear list before adding new data

                        for (stationId in likedStationIds) {
                            val stationData = stationSnapshot.child(stationId)
                            if (!stationData.exists()) continue

                            val name = stationData.child("name").getValue(String::class.java) ?: ""
                            val distance = stationData.child("distance").getValue(String::class.java) ?: ""
                            val rating = stationData.child("rating").getValue(String::class.java) ?: ""
                            val imageUrl = stationData.child("imageUrl").getValue(String::class.java) ?: ""

                            likedStationList.add(
                                StationModel(
                                    stationId = stationId,
                                    name = name,
                                    distance = distance,
                                    rating = rating,
                                    like = 1, // Since it's liked
                                    imageUrl = imageUrl
                                )
                            )
                        }

                        // Update UI
                        if (likedStationList.isEmpty()) {
                            showEmptyMessage()
                        } else {
                            binding.txtEmptyMessage.visibility = View.GONE
                            binding.rcvLikePetrolStations.visibility = View.VISIBLE
                        }

                        likedAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TAG", "Error fetching stations: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Error fetching liked stations: ${error.message}")
            }
        })
    }


}