package com.example.fuelfix.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelfix.ModalClass.OrderModel
import com.example.fuelfix.R
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(private val ordersList: MutableList<OrderModel>) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = ordersList[position]

        holder.txtOrderId.text = "Order ID: ${order.orderId}"
        holder.txtUserName.text = "User: ${order.userName} (${order.userEmail})"
        holder.txtTotalAmount.text = "Total: ₹${order.totalAmount}"
        holder.txtDeliveryFee.text = "Delivery Fee: ₹${order.deliveryFee}"
        holder.txtTax.text = "Tax: ₹${order.tax}"
        holder.txtDiscount.text = "Discount: ₹${order.discount}"
        holder.txtFinalAmount.text = "Final Amount: ₹${order.finalAmount}"
        holder.txtTimestamp.text = "Ordered On: ${formatTimestamp(order.timestamp)}"

        // Show status based on payment ID & order status
        when (order.paymentStatus) {
            "Pending" -> {
                holder.txtStatus.text = "Status: Pending ❌"
                holder.txtStatus.setTextColor(holder.itemView.context.getColor(R.color.red))
            }
            "Completed" -> {
                holder.txtStatus.text = "Status: Completed ✅"
                holder.txtStatus.setTextColor(holder.itemView.context.getColor(R.color.green))
            }
            "Cancelled" -> {
                holder.txtStatus.text = "Status: Cancelled ❌"
                holder.txtStatus.setTextColor(holder.itemView.context.getColor(R.color.red))
            }
            else -> {
                holder.txtStatus.text = "Status: Unknown ❓"
                holder.txtStatus.setTextColor(holder.itemView.context.getColor(R.color.gray))
            }
        }
    }

    override fun getItemCount(): Int = ordersList.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtOrderId: TextView = itemView.findViewById(R.id.txtOrderId)
        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtTotalAmount: TextView = itemView.findViewById(R.id.txtTotalAmount)
        val txtDeliveryFee: TextView = itemView.findViewById(R.id.txtDeliveryFee)
        val txtTax: TextView = itemView.findViewById(R.id.txtTax)
        val txtDiscount: TextView = itemView.findViewById(R.id.txtDiscount)
        val txtFinalAmount: TextView = itemView.findViewById(R.id.txtFinalAmount)
        val txtTimestamp: TextView = itemView.findViewById(R.id.txtTimestamp)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
