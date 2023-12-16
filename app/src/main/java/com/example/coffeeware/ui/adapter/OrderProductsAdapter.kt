package com.example.coffeeware.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeware.Model.Product
import com.example.coffeeware.databinding.OrderProductsAdapterBinding

class OrderProductsAdapter (
    private val productList: List<Product>,
    val productSelected: (Product, Int) -> Unit
) : RecyclerView.Adapter<OrderProductsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderProductsAdapter.MyViewHolder {
        return MyViewHolder(
            OrderProductsAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderProductsAdapter.MyViewHolder, position: Int) {
        val product = productList[position]

        holder.binding.tvName.text = product.name
        holder.binding.tvDesc.text = product.description
        holder.binding.tvValue.text = "R$ " + product.value.toString().replace(".", ",")

        if (product.image.isNotEmpty()) {
            holder.binding?.ivProduct?.let {
                Glide.with(it)
                    .load(product.image)
                    .into(it)
            }
        }
    }

    override fun getItemCount() = productList.size

    inner class MyViewHolder(val binding: OrderProductsAdapterBinding): RecyclerView.ViewHolder(binding.root)
}