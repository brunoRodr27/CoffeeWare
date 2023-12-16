package com.example.coffeeware.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeware.Model.Order
import com.example.coffeeware.Model.OrderProducts
import com.example.coffeeware.Model.Product
import com.example.coffeeware.databinding.FragmentAddProductBinding
import com.example.coffeeware.databinding.FragmentDetailsOrderBinding
import com.example.coffeeware.helper.FirebaseHelper
import com.example.coffeeware.ui.adapter.OrderProductsAdapter
import com.example.coffeeware.ui.adapter.SelectProductAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DetailsOrderFragment : Fragment() {

    private val args: DetailsOrderFragmentArgs by navArgs()

    private var _binding: FragmentDetailsOrderBinding? = null
    private val binding get() = _binding

    private lateinit var order: Order
    private val orderProductsList = mutableListOf<OrderProducts>()
    private val productsList = mutableListOf<Product>()

    private lateinit var orderProductsAdapter: OrderProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsOrderBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getArgs()

        getOrderProducts()
    }

    private fun getOrderProducts() {
        FirebaseHelper
            .getDataBase()
            .child("OrderProducts")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        orderProductsList.clear()

                        for (snap in snapshot.children) {
                            val orderProduct = snap.getValue(OrderProducts::class.java) as OrderProducts
                            if (orderProduct.order == order.id) orderProductsList.add(orderProduct)
                        }

                        getProducts()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro ao buscar produtos.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getProducts() {
        binding?.progressBar?.isVisible = true

        FirebaseHelper
            .getDataBase()
            .child("Product")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        productsList.clear()

                        for (item in orderProductsList) {
                            for (snap in snapshot.children) {
                                val products = snap.getValue(Product::class.java) as Product
                                if (products.id == item.product) productsList.add(products)
                            }
                        }

                        binding?.progressBar?.isVisible = false
                        binding?.textInfo?.text = ""

                        initAdapter()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro ao buscar produtos.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun initAdapter() {
        binding?.rvOrder?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvOrder?.setHasFixedSize(true)
        orderProductsAdapter = OrderProductsAdapter(productsList) {product, select ->
            optionSelect(product, select)
        }
        binding?.rvOrder?.adapter = orderProductsAdapter
    }

    private fun optionSelect(product: Product, select: Int) {

    }

    private fun getArgs() {
        args.order.let {
            if (it != null) {
                order = it

                configProduct()
            }
        }
    }

    private fun configProduct() {
        when (order.status) {
            0 -> binding?.tvStatus?.text = "Pedido aberto"
            1 -> binding?.tvStatus?.text = "Pedido em preparo"
            2 -> binding?.tvStatus?.text = "Pedido pronto"

        }
        binding?.tvTable?.text = "Mesa: " + order.table
        binding?.tvObsdesc?.text = order.obs
    }

    private fun initClicks() {
        binding?.ibClose?.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}