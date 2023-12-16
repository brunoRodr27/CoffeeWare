package com.example.coffeeware.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeware.Model.Product
import com.example.coffeeware.R
import com.example.coffeeware.databinding.FragmentProductsBinding
import com.example.coffeeware.helper.FirebaseHelper
import com.example.coffeeware.helper.Functions
import com.example.coffeeware.ui.adapter.ProductAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding

    private lateinit var productAdapter : ProductAdapter

    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getProducts()
    }

    private fun initClicks() {
        binding?.ibClose?.setOnClickListener{
            findNavController().popBackStack()
        }

        binding?.fabAdd?.setOnClickListener{
            val action = ProductsFragmentDirections
                .actionProductsFragmentToAddProductFragment(null)
            findNavController().navigate(action)
        }
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
                        productList.clear()

                        for (snap in snapshot.children) {
                            val product = snap.getValue(Product::class.java) as Product
                            productList.add(product)
                        }

                        binding?.progressBar?.isVisible = false
                        binding?.textInfo?.text = ""

                        productList.reverse()
                        initAdapter()
                    } else {
                        binding?.progressBar?.isVisible = false
                        binding?.textInfo?.text = getString(R.string.product_null)
                    }

                    if (productList.size <= 0) {
                        binding?.progressBar?.isVisible = false
                        binding?.textInfo?.text = getString(R.string.product_null)
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
        productAdapter = ProductAdapter(productList) {product, select ->
            optionSelect(product, select)
        }

        binding?.rvOrder?.adapter = this.productAdapter
    }

    private fun optionSelect(product: Product, select: Int) {
        when (select) {
            ProductAdapter.SELECT_EDIT -> {
                val action = ProductsFragmentDirections
                    .actionProductsFragmentToAddProductFragment(product)
                findNavController().navigate(action)
            }
            ProductAdapter.SELECT_REMOVE -> {
                Functions(requireContext()).exibirCaixaDialogo("", getString(R.string.delete_question), { deleteProduct(product) }, {})
            }
        }
    }

    private fun deleteProduct(product: Product) {
        FirebaseHelper
            .getDataBase()
            .child("Product")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(product.id)
            .removeValue()

        productList.remove(product)
        productAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
