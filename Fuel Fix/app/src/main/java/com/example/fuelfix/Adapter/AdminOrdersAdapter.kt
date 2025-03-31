package com.example.fuelfix.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelfix.ModalClass.OrderModel
import com.example.fuelfix.R
import com.google.firebase.database.FirebaseDatabase

class AdminOrdersAdapter(private val context: Context, private val ordersList: List<OrderModel>) :
    RecyclerView.Adapter<AdminOrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.txtOrderId)
        val userName: TextView = itemView.findViewById(R.id.txtUserName)
        val amount: TextView = itemView.findViewById(R.id.txtAmount)
        val paymentStatus: Spinner = itemView.findViewById(R.id.spinnerPaymentStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = ordersList[position]
        holder.orderId.text = "Order ID: ${order.orderId}"
        holder.userName.text = "User: ${order.userName}"
        holder.amount.text = "₹${order.finalAmount}"

        val statusOptions = arrayOf("Pending", "Completed", "Cancelled")
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.paymentStatus.adapter = adapter

        val currentStatusIndex = statusOptions.indexOf(order.paymentStatus)
        if (currentStatusIndex != -1) {
            holder.paymentStatus.setSelection(currentStatusIndex)
        }

        holder.paymentStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val newStatus = statusOptions[pos]
                if (newStatus != order.paymentStatus) {
                    updatePaymentStatus(order.orderId, newStatus)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun getItemCount() = ordersList.size

    private fun updatePaymentStatus(orderId: String, newStatus: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("OrdersTb").child(orderId)
        databaseRef.child("paymentStatus").setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(context, "Status Updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show()
            }
    }
}