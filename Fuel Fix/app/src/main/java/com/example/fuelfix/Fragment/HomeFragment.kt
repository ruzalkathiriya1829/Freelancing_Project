package com.example.fuelfix.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.fuelfix.Activity.AdminActivity
import com.example.fuelfix.Adapter.StationAdapter
import com.example.fuelfix.Adapter.TopOffersSliderAdapter
import com.example.fuelfix.ModalClass.StationModel
import com.example.fuelfix.ModalClass.TopOffersSliderData
import com.example.fuelfix.R
import com.example.fuelfix.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: StationAdapter
    private lateinit var stationList: MutableList<StationModel>

    lateinit var topOffersSliderAdapter: TopOffersSliderAdapter
    lateinit var topOffersSliderList: ArrayList<TopOffersSliderData>

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        // Set RecyclerView with 2 columns per row
        binding.rcvPetrolStations.layoutManager = GridLayoutManager(requireContext(), 2)

        stationList = mutableListOf()
        adapter = StationAdapter(requireContext(), stationList)
        binding.rcvPetrolStations.adapter = adapter

        fetchStations()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        sharedPreferences = requireActivity().getSharedPreferences(
            "FuelFixSharedPreferences",
            AppCompatActivity.MODE_PRIVATE
        )

        val view = binding.root

        // Load Offers & Nearby Petrol Stations
        topOffersViewPager()

        // Check if user is an admin
        checkAdmin()
        SeeAllData()

        return view
    }

    private fun SeeAllData() {
        binding.txtSeeAll.setOnClickListener {
            val searchFragment = SearchFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, searchFragment) // Replace with actual container ID
            transaction.addToBackStack(null) // Allow back navigation
            transaction.commit()
        }

    }


    private fun fetchStations() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userLikesRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("LikedStations")
        val stationsRef = FirebaseDatabase.getInstance().reference.child("PetrolStationsTb")

        userLikesRef.addValueEventListener(object : ValueEventListener { // ✅ Change to addValueEventListener
            override fun onDataChange(snapshot: DataSnapshot) {
                val likedStations = mutableSetOf<String>()

                for (likedSnapshot in snapshot.children) {
                    likedSnapshot.key?.let { likedStations.add(it) }
                }

                stationsRef.addValueEventListener(object : ValueEventListener { // ✅ Listen for station changes
                    override fun onDataChange(stationSnapshot: DataSnapshot) {
                        stationList.clear()

                        for (station in stationSnapshot.children) {
                            val stationId = station.key ?: continue
                            val stationData = station.getValue(StationModel::class.java)

                            stationData?.let {
                                it.stationId = stationId
                                it.like = if (likedStations.contains(stationId)) 1 else 0 // ✅ Auto update Like status
                                stationList.add(it)
                            }
                        }
                        adapter.notifyDataSetChanged() // ✅ Auto refresh RecyclerView
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun topOffersViewPager() {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo


        if (networkInfo != null && networkInfo.isConnected) {

            topOffersSliderList = ArrayList()
            topOffersSliderList.add(

                TopOffersSliderData(
                    "Claim your discount 30% daily now!", R.drawable.topoffer
                )
            )
            topOffersSliderList.add(

                TopOffersSliderData(
                    "Claim your discount 40% daily now!", R.drawable.topoffer1
                )
            )
            topOffersSliderList.add(

                TopOffersSliderData(
                    "Claim your discount 50% daily now!", R.drawable.p3
                )
            )
            topOffersSliderList.add(

                TopOffersSliderData(
                    "Claim your discount 10% daily now!", R.drawable.p4
                )
            )
            topOffersSliderList.add(

                TopOffersSliderData(
                    "Claim your discount 20% daily now!", R.drawable.p5
                )
            )

            val viewListener: ViewPager.OnPageChangeListener =
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int, positionOffset: Float, positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {
                        val colorWhite = requireContext().resources.getColor(R.color.lightgray1)
                        val colorBrown = requireContext().resources.getColor(R.color.yellow)
                        when (binding.viewpager.currentItem) {
                            0 -> {

                                binding.iv1.setBackgroundColor(colorBrown)
                                binding.iv2.setBackgroundColor(colorWhite)
                                binding.iv3.setBackgroundColor(colorWhite)
                                binding.iv4.setBackgroundColor(colorWhite)
                                binding.iv5.setBackgroundColor(colorWhite)
                            }


                            1 -> {
                                binding.iv1.setBackgroundColor(colorWhite)
                                binding.iv2.setBackgroundColor(colorBrown)
                                binding.iv3.setBackgroundColor(colorWhite)
                                binding.iv4.setBackgroundColor(colorWhite)
                                binding.iv5.setBackgroundColor(colorWhite)
                            }

                            2 -> {
                                binding.iv1.setBackgroundColor(colorWhite)
                                binding.iv2.setBackgroundColor(colorWhite)
                                binding.iv3.setBackgroundColor(colorBrown)
                                binding.iv4.setBackgroundColor(colorWhite)
                                binding.iv5.setBackgroundColor(colorWhite)
                            }

                            3 -> {
                                binding.iv1.setBackgroundColor(colorWhite)
                                binding.iv2.setBackgroundColor(colorWhite)
                                binding.iv3.setBackgroundColor(colorWhite)
                                binding.iv4.setBackgroundColor(colorBrown)
                                binding.iv5.setBackgroundColor(colorWhite)
                            }

                            4 -> {
                                binding.iv1.setBackgroundColor(colorWhite)
                                binding.iv2.setBackgroundColor(colorWhite)
                                binding.iv3.setBackgroundColor(colorWhite)
                                binding.iv4.setBackgroundColor(colorWhite)
                                binding.iv5.setBackgroundColor(colorBrown)
                            }
                        }

                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                }

            topOffersSliderAdapter = TopOffersSliderAdapter(requireContext(), topOffersSliderList)
            binding.viewpager.adapter = topOffersSliderAdapter
            binding.viewpager.addOnPageChangeListener(viewListener)

        } else {
            Toast.makeText(
                requireContext(), "Please Check Your Internet Connection.", Toast.LENGTH_SHORT
            ).show()
        }
    }



    /** Check if User is Admin **/
    private fun checkAdmin() {

//

        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).child("role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.getValue(String::class.java)
                    Log.d("TAG", "onDataChange: ===>"+role)
//                    binding.adminButton.visibility = if (role == "admin") View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        //increment button code
        binding.imgIncrement.setOnClickListener {

            val intent = Intent(activity, AdminActivity::class.java)
            startActivity(intent)
        }
    }
}

