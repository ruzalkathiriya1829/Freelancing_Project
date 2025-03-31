package com.example.fuelfix.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fuelfix.Adapter.OrdersAdapter
import com.example.fuelfix.ModalClass.OrderModel
import com.example.fuelfix.databinding.FragmentPendingOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PendingOrdersFragment : Fragment() {

    private lateinit var binding: FragmentPendingOrdersBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderList: ArrayList<OrderModel>
    private lateinit var orderAdapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPendingOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerPendingOrders.layoutManager = LinearLayoutManager(requireContext())
        orderList = ArrayList()
        orderAdapter = OrdersAdapter(orderList)
        binding.recyclerPendingOrders.adapter = orderAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("OrdersTb")

        fetchPendingOrders()
    }

    private fun fetchPendingOrders() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.orderByChild("paymentStatus").equalTo("Pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderList.clear()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderModel::class.java)
                        if (order != null && order.userId == currentUserId) {
                            orderList.add(order)
                        }
                    }
                    orderAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load Pending orders", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
