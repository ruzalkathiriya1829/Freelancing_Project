package com.example.fuelfix.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ItemTimeBinding

class TimeAdapter(
    private val times: List<String>,
    private val onTimeSelected: (String) -> Unit
) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    private var selectedPosition = -1

    inner class TimeViewHolder(val binding: ItemTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(time: String, position: Int) {
            binding.btnTime.text = time

            // Highlight selected time
            if (selectedPosition == position) {
                binding.btnTime.setBackgroundResource(R.drawable.border_box_yellow)
            } else {
                binding.btnTime.setBackgroundResource(R.drawable.border_box_gray)
            }

            binding.btnTime.setOnClickListener {
                selectedPosition = position
                onTimeSelected(time)
                notifyDataSetChanged() // Update UI
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val binding =
            ItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(times[position], position)
    }

    override fun getItemCount() = times.size
}
