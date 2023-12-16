package com.example.coffeeware.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeware.Model.Order
import com.example.coffeeware.R
import com.example.coffeeware.databinding.FinishOrderAdapterBinding

class FinishOrderAdapter (
    private val orderList: List<Order>,
    val orderSelect: (Order, Int) -> Unit
) : RecyclerView.Adapter<FinishOrderAdapter.MyViewHolder>() {

    companion object {
        val SELECT_CONFIRM: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            FinishOrderAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount()  = orderList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val order = orderList[position]

        holder.binding.tvTable.text = "Mesa: " + order.table
        holder.binding.tvValue.text = "Valor (R$) : " + String.format("%.2f", order.value)

        holder.binding.ibConfirm.setOnClickListener{orderSelect(order, FinishOrderAdapter.SELECT_CONFIRM)}
    }

    inner class MyViewHolder(val binding: FinishOrderAdapterBinding): RecyclerView.ViewHolder(binding.root)
}