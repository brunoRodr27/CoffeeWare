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
import com.example.coffeeware.databinding.FragmentReadyOrdersBinding
import com.example.coffeeware.helper.FirebaseHelper
import com.example.coffeeware.helper.Functions
import com.example.coffeeware.ui.adapter.OrderAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ReadyOrdersFragment : Fragment() {

    private var _binding: FragmentReadyOrdersBinding? = null
    private val binding get() = _binding

    private lateinit var orderAdapter: OrderAdapter

    private val orderList = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReadyOrdersBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getOrders()

    }

    private fun initClicks() {
        binding?.fabAdd?.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_finishOrderFragment)
        }
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
                            if (order.status == 2)  orderList.add(order)
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
        orderAdapter = OrderAdapter(orderList) {order, select ->
            optionSelect(order, select)
        }
        binding?.rvOrder?.adapter = orderAdapter
    }

    private fun optionSelect(order: Order, select: Int) {
        when (select) {
            OrderAdapter.SELECT_BACK -> {
                order.status = 1
                updateTask(order)
            }
            OrderAdapter.SELECT_DETAL -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToDetailsOrderFragment(order)
                findNavController().navigate(action)
            }
            OrderAdapter.SELECT_DELETE -> {
                Functions(requireContext()).exibirCaixaDialogo("", getString(R.string.delete_question), { deleteOrder(order) }, {})
            }
            OrderAdapter.SELECT_NEXT -> {

                Functions(requireContext()).exibirCaixaDialogo("", "Deseja finalizar o pedido?",
                    { order.status = 3
                     updateTask(order) }, {})
            }
        }
    }

    private fun updateTask(order: Order) {
        FirebaseHelper
            .getDataBase()
            .child("Order")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(order.id)
            .setValue(order)
            .addOnCompleteListener {order ->
                if (order.isSuccessful) {
                    Toast.makeText(requireContext(), "Pedido atualizado com sucesso.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar o pedido.", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                binding?.progressBar?.isVisible = false
                Toast.makeText(requireContext(), "Erro ao salvar o pedido.", Toast.LENGTH_LONG).show()
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
        orderAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}