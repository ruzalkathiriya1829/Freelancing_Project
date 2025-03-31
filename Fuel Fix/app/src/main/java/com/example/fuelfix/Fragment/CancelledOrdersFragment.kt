package com.example.fuelfix.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelfix.Adapter.OrdersAdapter
import com.example.fuelfix.ModalClass.OrderModel
import com.example.fuelfix.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CancelledOrdersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrdersAdapter
    private val orderList = mutableListOf<OrderModel>()
    private val databaseRef = FirebaseDatabase.getInstance().getReference("OrdersTb")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cancelled_orders, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCancelledOrders)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = OrdersAdapter(orderList)
        recyclerView.adapter = adapter

        fetchCancelledOrders()

        return view
    }

    private fun fetchCancelledOrders() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        databaseRef.orderByChild("paymentStatus").equalTo("Cancelled")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderList.clear()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderModel::class.java)
                        if (order != null && order.userId == currentUserId) {
                            orderList.add(order)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load data!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
