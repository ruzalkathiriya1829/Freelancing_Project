package com.example.fuelfix.Fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
import android.provider.Settings.ACTION_DATE_SETTINGS
import android.provider.Settings.ACTION_DISPLAY_SETTINGS
import android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.provider.Settings.ACTION_WIRELESS_SETTINGS
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fuelfix.R
import com.example.fuelfix.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }

    private fun initView() {

        binding.imgBack.setOnClickListener {

            activity?.onBackPressed()

        }

        // Handle wireless settings button
        binding.wirelessSettings.setOnClickListener {
            val i = Intent(ACTION_WIRELESS_SETTINGS)
            startActivity(i)
        }

        // Handle wifi settings button
        binding.wifiSettings.setOnClickListener {
            val i = Intent(ACTION_WIFI_SETTINGS)
            startActivity(i)
        }

        // Handle bluetooth settings button
        binding.bluetoothSettings.setOnClickListener {
            val i = Intent(ACTION_BLUETOOTH_SETTINGS)
            startActivity(i)
        }

        // Handle date settings button
        binding.dateSettings.setOnClickListener {
            val i = Intent(ACTION_DATE_SETTINGS)
            startActivity(i)
        }

        // Handle input method settings button
        binding.inputMethodSettings.setOnClickListener {
            val i = Intent(ACTION_INPUT_METHOD_SETTINGS)
            startActivity(i)
        }

        // Handle display settings button
        binding.displaySettings.setOnClickListener {
            val i = Intent(ACTION_DISPLAY_SETTINGS)
            startActivity(i)
        }

        // Handle location settings button
        binding.locationSettings.setOnClickListener {
            val i = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(i)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}