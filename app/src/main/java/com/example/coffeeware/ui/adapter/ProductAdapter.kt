package com.example.coffeeware.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeware.Model.Product
import com.example.coffeeware.R
import com.example.coffeeware.databinding.ProductAdapterBinding

class ProductAdapter (
    private val productList: List<Product>,
    val productSelected: (Product, Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    companion object {
        val SELECT_REMOVE: Int = 1
        val SELECT_EDIT: Int = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ProductAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = productList.size

    override fun onBindViewHolder(holder: ProductAdapter.MyViewHolder, position: Int) {
        val product = productList[position]

        if (product.status == "F") {
            holder.binding.ivDisable.isVisible = true
            holder.binding.ivEnable.isVisible = false
        } else {
            holder.binding.ivDisable.isVisible = false
            holder.binding.ivEnable.isVisible = true
        }

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


        holder.binding.ibDelete.setOnClickListener{productSelected(product, SELECT_REMOVE)}
        holder.binding.ibEdit.setOnClickListener{productSelected(product, SELECT_EDIT)}
    }

    inner class MyViewHolder(val binding: ProductAdapterBinding): RecyclerView.ViewHolder(binding.root)
}