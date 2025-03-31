package com.example.fuelfix.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fuelfix.Activity.StationDetailsActivity
import com.example.fuelfix.ModalClass.StationModel
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ItemStationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class StationAdapter(private val context: Context, private val stationList: MutableList<StationModel>) :
    RecyclerView.Adapter<StationAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemStationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgLike: ImageView = binding.imgHeart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val station = stationList[position]

        // Load image using Glide
        Glide.with(context)
            .load(station.imageUrl)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .into(holder.binding.imgPetrolStation)

        // Set station details
        holder.binding.txtPetrolStationName.text = station.name
        holder.binding.txtPetrolStationDistance.text = station.distance
        holder.binding.txtReview.text = station.rating

        // Set like button
        val heartIcon = if (station.like == 1) R.drawable.fillheart else R.drawable.heart
        holder.imgLike.setImageResource(heartIcon)

        // Handle Like Button Click
        holder.imgLike.setOnClickListener {
            station.like = if (station.like == 1) 0 else 1 // Toggle Like
            updateLikeStatusInFirebase(station.stationId, station.like)
            notifyItemChanged(position) // Update UI immediately
        }

        // Handle Click Event to Open Station Details
        holder.itemView.setOnClickListener {
            val intent = Intent(context, StationDetailsActivity::class.java)
            intent.putExtra("stationId", station.stationId)
            intent.putExtra("name", station.name)
            intent.putExtra("distance", station.distance)
            intent.putExtra("rating", station.rating)
            intent.putExtra("imageUrl", station.imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = stationList.size

    // ✅ Update Firebase Like Status
    private fun updateLikeStatusInFirebase(stationId: String, newLikeStatus: Int) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return

        val userLikesRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId)
            .child("LikedStations")
            .child(stationId)

        if (newLikeStatus == 1) {
            // Store the liked station under the user's node
            userLikesRef.setValue(true)
        } else {
            // Remove it from the liked list
            userLikesRef.removeValue()
        }
    }

}
