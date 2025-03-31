package com.example.fuelfix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelfix.ModalClass.OrderModel
import com.example.fuelfix.R

class OrderAdapter(private val orderList: List<OrderModel>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.txtOrderId)
        val tvTotalAmount: TextView = itemView.findViewById(R.id.txtTotalAmount)
        //val tvPaymentId: TextView = itemView.findViewById(R.id.)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.tvOrderId.text = "Order ID: ${order.orderId}"
        holder.tvTotalAmount.text = "Total: ₹${order.finalAmount}"
        //holder.tvPaymentId.text = "Payment ID: ${order.paymentId}"
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}