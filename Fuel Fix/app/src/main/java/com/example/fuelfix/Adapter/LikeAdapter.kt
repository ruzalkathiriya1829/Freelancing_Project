package com.example.fuelfix.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fuelfix.ModalClass.StationModel
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ItemStationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LikeAdapter(
    private var context: Context,
    private var likedStations: MutableList<StationModel>,
    private val showEmptyMessage: () -> Unit // Callback function to show empty message
) : RecyclerView.Adapter<LikeAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemStationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemStationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = likedStations.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val station = likedStations[position]

        Glide.with(context)
            .load(station.imageUrl)
            .placeholder(R.drawable.loading) // Placeholder image
            .error(R.drawable.loading) // Error image
            .into(holder.binding.imgPetrolStation)

        holder.binding.txtPetrolStationName.text = station.name
        holder.binding.txtPetrolStationDistance.text = station.distance
        holder.binding.txtReview.text = station.rating

        val heartIcon = if (station.like == 1) R.drawable.fillheart else R.drawable.heart
        holder.binding.imgHeart.setImageResource(heartIcon)

        // Click listener for heart icon (to remove from liked)
        holder.binding.imgHeart.setOnClickListener {
            station.like = if (station.like == 1) 0 else 1 // Toggle like

            // Update Firebase
            updateLikeStatusInFirebase(station.stationId, station.like)

            if (station.like == 0) {
                likedStations.removeAt(position)
                notifyItemRemoved(position)

                // If no stations are left, show the empty message
                if (likedStations.isEmpty()) {
                    showEmptyMessage()
                }
            }
        }
    }

    private fun updateLikeStatusInFirebase(stationId: String, newLikeStatus: Int) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return

        val userLikesRef = FirebaseDatabase.getInstance().reference
            .child("LikedPetrolStationTb")
            .child(userId)
            .child("LikedStations")
            .child(stationId)

        if (newLikeStatus == 1) {
            // Add to Firebase liked stations
            userLikesRef.setValue(true)
                .addOnSuccessListener {
                    Log.d("LikeStatus", "Station $stationId liked successfully!")
                }
                .addOnFailureListener { e ->
                    Log.e("LikeStatus", "Failed to like station: ${e.message}")
                }
        } else {
            // Remove from Firebase liked stations
            userLikesRef.removeValue()
                .addOnSuccessListener {
                    Log.d("LikeStatus", "Station $stationId removed from liked successfully!")
                }
                .addOnFailureListener { e ->
                    Log.e("LikeStatus", "Failed to remove station: ${e.message}")
                }
        }
    }
}