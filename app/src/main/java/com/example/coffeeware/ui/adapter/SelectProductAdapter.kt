package com.example.coffeeware.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeware.Model.Product
import com.example.coffeeware.databinding.SelectProductAdapterBinding
import com.google.android.material.card.MaterialCardView

class SelectProductAdapter (
    private val productList: List<Product>,
    val productSelected: (Product, Int, ImageView) -> Unit
) : RecyclerView.Adapter<SelectProductAdapter.MyViewHolder>() {

    companion object {
        val SELECT_PROD: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            SelectProductAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = productList.size


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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

        holder.binding.cvProduct.setOnClickListener{productSelected(product, SELECT_PROD, holder.binding.ivChecked)}
    }

    inner class MyViewHolder(val binding: SelectProductAdapterBinding): RecyclerView.ViewHolder(binding.root)
}