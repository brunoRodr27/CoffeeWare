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
import com.example.coffeeware.Model.Order
import com.example.coffeeware.R
import com.example.coffeeware.databinding.FragmentFinishOrderBinding
import com.example.coffeeware.helper.FirebaseHelper
import com.example.coffeeware.helper.Functions
import com.example.coffeeware.ui.adapter.FinishOrderAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FinishOrderFragment : Fragment() {

    private var _binding: FragmentFinishOrderBinding? = null
    private val binding get() = _binding

    private val orderList = mutableListOf<Order>()
    private lateinit var finishOrderAdapter: FinishOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishOrderBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getOrders()
    }

    private fun getOrders() {
        binding?.progressBar?.isVisible = true

        FirebaseHelper
            .getDataBase()
            .child("Order")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        orderList.clear()

                        for (snap in snapshot.children) {
                            val order = snap.getValue(Order::class.java) as Order
                            if (order.status == 3)  orderList.add(order)
                        }

                        binding?.progressBar?.isVisible = false
                        binding?.textInfo?.text = ""

                        orderList.reverse()
                        initAdapter()
                    } else {
                        binding?.progressBar?.isVisible = false
                        binding?.textInfo?.text = getString(R.string.orders_null)
                    }

                    if (orderList.size <= 0) {
                        binding?.progressBar?.isVisible = false
                        binding?.textInfo?.text = getString(R.string.orders_null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro ao buscar pedidos.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun initAdapter() {
        binding?.rvOrder?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvOrder?.setHasFixedSize(true)
        finishOrderAdapter = FinishOrderAdapter(orderList) {order, select ->
            optionSelect(order, select)
        }
        binding?.rvOrder?.adapter = this.finishOrderAdapter
    }

    private fun optionSelect(order: Order, select: Int) {
        when (select) {
            FinishOrderAdapter.SELECT_CONFIRM -> {
                Functions(requireContext()).exibirCaixaDialogo("", "Pagamento realizado?", { deleteOrder(order) }, {})
            }
        }
    }

    private fun deleteOrder(order: Order) {
        FirebaseHelper
            .getDataBase()
            .child("Order")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(order.id)
            .removeValue()

        orderList.remove(order)
        finishOrderAdapter.notifyDataSetChanged()
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