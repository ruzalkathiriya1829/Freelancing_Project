package com.example.fuelfix.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fuelfix.ModalClass.StationModel
import com.example.fuelfix.R

class SearchStationAdapter(
    private val context: Context,
    private var stationList: MutableList<StationModel>
) : RecyclerView.Adapter<SearchStationAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_search_station, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = stationList[position]
        holder.bind(station, context)
    }

    override fun getItemCount(): Int = stationList.size

    class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtStationName: TextView = itemView.findViewById(R.id.txtPetrolStationName)
        private val txtStationLocation: TextView = itemView.findViewById(R.id.txtPetrolStationDistance)
        private val imgStation: ImageView = itemView.findViewById(R.id.imgPetrolStation) // Ensure this ImageView exists

        fun bind(station: StationModel, context: Context) {
            txtStationName.text = station.name
            txtStationLocation.text = station.distance

            // Load image using Glide
            if (!station.imageUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(station.imageUrl)
                    .placeholder(R.drawable.p1) // Add a default placeholder
                    .error(R.drawable.loading) // Add an error image
                    .into(imgStation)
            } else {
                imgStation.setImageResource(R.drawable.p1) // Use placeholder if no image
            }
        }
    }

    // Method to update the list dynamically
    fun updateList(newList: List<StationModel>) {
        stationList.clear()
        stationList.addAll(newList)
        notifyDataSetChanged()
    }
}