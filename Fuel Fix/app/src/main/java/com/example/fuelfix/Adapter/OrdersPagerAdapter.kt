package com.example.fuelfix.Fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OrdersPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4 // 4 tabs (All, Pending, Completed, Cancelled)

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllOrdersFragment()
            1 -> PendingOrdersFragment()
            2 -> CompletedOrdersFragment()
            3 -> CancelledOrdersFragment()
            else -> Fragment()
        }
    }
}
