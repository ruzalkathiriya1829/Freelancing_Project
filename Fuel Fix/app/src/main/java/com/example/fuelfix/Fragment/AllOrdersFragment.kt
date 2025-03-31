package com.example.fuelfix.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelfix.Adapter.OrdersAdapter
import com.example.fuelfix.ModalClass.OrderModel
import com.example.fuelfix.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AllOrdersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrdersAdapter
    private lateinit var databaseReference: DatabaseReference
    private var orderList = ArrayList<OrderModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_orders, container, false)

        recyclerView = view.findViewById(R.id.recyclerOrders)
        recyclerView.layoutManager = LinearLayoutManager(context)
        orderAdapter = OrdersAdapter(orderList)
        recyclerView.adapter = orderAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("OrdersTb")

        fetchAllOrders()

        return view
    }

    private fun fetchAllOrders() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.orderByChild("userId").equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderList.clear()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderModel::class.java)
                        if (order != null) {
                            orderList.add(order)
                        }
                    }
                    orderAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load orders", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
