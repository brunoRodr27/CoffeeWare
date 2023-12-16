package com.example.coffeeware.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeware.Model.Order
import com.example.coffeeware.Model.Product
import com.example.coffeeware.R
import com.example.coffeeware.databinding.OrderAdapterBinding

class OrderAdapter (
    private val orderList: List<Order>,
    val orderSelected: (Order, Int) -> Unit
) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    companion object {
        val SELECT_BACK: Int = 1
        val SELECT_DETAL: Int = 2
        val SELECT_DELETE: Int = 3
        val SELECT_NEXT: Int = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.MyViewHolder {
        return MyViewHolder(
            OrderAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderAdapter.MyViewHolder, position: Int) {
        val order = orderList[position]

        when (order.status) {
            0 -> {
                holder.binding.ibBack.isVisible = false
                holder.binding.ibNext.setOnClickListener{orderSelected(order, SELECT_NEXT)}
            }
            1 -> {
                holder.binding.ibBack.setOnClickListener{orderSelected(order, SELECT_BACK)}
                holder.binding.ibNext.setOnClickListener{orderSelected(order, SELECT_NEXT)}
            }
            2 -> {
                holder.binding.ibBack.setOnClickListener{orderSelected(order, SELECT_BACK)}
                holder.binding.ibNext.setOnClickListener{orderSelected(order, SELECT_NEXT)}
            }
        }

        holder.binding.ibDelete.setOnClickListener{orderSelected(order, SELECT_DELETE)}
        holder.binding.ibDetail.setOnClickListener{orderSelected(order, SELECT_DETAL)}

        holder.binding.tvTable.text = "Mesa: " + order.table
        holder.binding.tvValue.setText("Valor (R$) : " + String.format("%.2f", order.value))
        holder.binding.tvDesc.text = order.obs
    }

    override fun getItemCount() = orderList.size

    inner class MyViewHolder(val binding: OrderAdapterBinding): RecyclerView.ViewHolder(binding.root)

}