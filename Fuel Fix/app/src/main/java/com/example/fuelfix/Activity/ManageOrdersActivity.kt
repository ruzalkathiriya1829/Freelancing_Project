package com.example.fuelfix.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelfix.Adapter.AdminOrdersAdapter
import com.example.fuelfix.ModalClass.OrderModel
import com.example.fuelfix.R
import com.google.firebase.database.*

class ManageOrdersActivity : AppCompatActivity() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var ordersAdapter: AdminOrdersAdapter
    private val ordersList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_orders)

        ordersRecyclerView = findViewById(R.id.recyclerViewOrders)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchOrdersFromFirebase()
    }

    private fun fetchOrdersFromFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("OrdersTb")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ordersList.clear()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(OrderModel::class.java)
                    if (order != null) {
                        ordersList.add(order)
                    }
                }
                ordersAdapter = AdminOrdersAdapter(this@ManageOrdersActivity, ordersList)
                ordersRecyclerView.adapter = ordersAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ManageOrdersActivity,
                    "Failed to load orders: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}