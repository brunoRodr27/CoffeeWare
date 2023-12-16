package com.example.coffeeware.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeware.Model.Order
import com.example.coffeeware.Model.Product
import com.example.coffeeware.databinding.FragmentAddOrdersBinding
import com.example.coffeeware.helper.FirebaseHelper
import com.example.coffeeware.Model.OrderProducts
import com.example.coffeeware.ui.adapter.SelectProductAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AddOrdersFragment : Fragment() {

    private var _binding: FragmentAddOrdersBinding? = null
    private val binding get() = _binding

    private lateinit var order: Order
    private lateinit var orderProducts: OrderProducts

    private val productList = mutableListOf<Product>()
    private val productSelect = mutableListOf<String>()

    private lateinit var selectProductAdapter : SelectProductAdapter

    private var valueProds: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOrdersBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getProducts()
    }

    private fun getProducts() {
        FirebaseHelper
            .getDataBase()
            .child("Product")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        productList.clear()

                        for (snap in snapshot.children) {
                            val product = snap.getValue(Product::class.java) as Product
                            if (product.status == "T") productList.add(product)
                        }

                        productList.reverse()
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
        selectProductAdapter = SelectProductAdapter(productList) {product, select, image ->
            optionSelect(product, select, image)
        }
        binding?.rvOrder?.adapter = selectProductAdapter
    }

    private fun optionSelect(product: Product, select: Int, image: ImageView) {
        when (select) {
            SelectProductAdapter.SELECT_PROD -> {
                if (!image.isVisible) {
                    image.isVisible = true
                    valueProds += product.value
                    productSelect.add(product.id)

                } else {
                    image.isVisible = false
                    valueProds -= product.value
                    productSelect.remove(product.id)

                }

                binding?.tvValue?.setText("Valor (R$) : " + String.format("%.2f", valueProds))
            }
        }
    }

    private fun initClicks() {
        binding?.ibClose?.setOnClickListener {
            findNavController().popBackStack()
        }
        binding?.btnSave?.setOnClickListener{ validOrder() }
    }

    private fun validOrder() {
        val table = binding?.etTable?.text.toString()
        val obs = binding?.etDesc?.text.toString()
        val value = valueProds
        val productsSelectList = productSelect

        if (table.isNotEmpty()) {
            if (productsSelectList.isNotEmpty()) {

                order = Order()
                order.table = table
                order.obs = obs
                order.value = value
                order.status = 0

                val id = order.id

                for (product in productsSelectList) {
                    orderProducts = OrderProducts()
                    orderProducts.order = id
                    orderProducts.product = product

                    saveOrderProduct()
                }

                saveOrder()
            } else {
                Toast.makeText(requireContext(), "Adicione ao menos um produto.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "Adicione uma mesa.", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveOrderProduct() {
        FirebaseHelper
            .getDataBase()
            .child("OrderProducts")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(orderProducts.id)
            .setValue(orderProducts)
    }

    private fun saveOrder() {
        FirebaseHelper
            .getDataBase()
            .child("Order")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(order.id)
            .setValue(order)
            .addOnCompleteListener {order ->
                if (order.isSuccessful) {
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "Pedido salvo com sucesso.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar o pedido.", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao salvar o pedido.", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}