package com.example.fuelfix.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelfix.ModalClass.DateModel
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ItemDateBinding

class CalendarAdapter(private val dateList: List<DateModel>, private val onDateClick: (Int) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {

    private var selectedPosition = -1 // Track selected date

    inner class DateViewHolder(val binding: ItemDateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dateModel: DateModel, position: Int) {
            binding.btnDate.text = dateModel.date

            // Change background color based on selection
            if (position == selectedPosition) {
                binding.btnDate.setBackgroundResource(R.drawable.border_box_yellow)
            } else {
                binding.btnDate.setBackgroundResource(R.drawable.border_box_gray)
            }

            binding.btnDate.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onDateClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dateList[position], position)
    }

    override fun getItemCount() = dateList.size
}