package com.example.fuelfix.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fuelfix.Adapter.SearchStationAdapter
import com.example.fuelfix.Adapter.StationAdapter
import com.example.fuelfix.ModalClass.StationModel
import com.example.fuelfix.R
import com.example.fuelfix.databinding.FragmentSearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: SearchStationAdapter
    private var stationList = mutableListOf<StationModel>()
    private var filteredList = mutableListOf<StationModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference.child("PetrolStationsTb")

        setupRecyclerView()
        fetchAllStations()
        setupSearchListener()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rcvSearchStations.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchStationAdapter(requireContext(), filteredList)
        binding.rcvSearchStations.adapter = adapter
    }

    private fun fetchAllStations() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stationList.clear()
                filteredList.clear()

                for (stationSnapshot in snapshot.children) {
                    val station = stationSnapshot.getValue(StationModel::class.java)
                    station?.let { stationList.add(it) }
                }

                filteredList.addAll(stationList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchListener() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterStations(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterStations(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(stationList)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredList.addAll(stationList.filter {
                it.name?.lowercase()?.contains(lowerCaseQuery) == true ||
                        it.distance?.lowercase()?.contains(lowerCaseQuery) == true
            })
        }

        adapter.notifyDataSetChanged()
    }
}