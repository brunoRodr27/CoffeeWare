package com.example.coffeeware.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.coffeeware.R
import com.example.coffeeware.databinding.FragmentHomeBinding
import com.example.coffeeware.helper.Functions
import com.example.coffeeware.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClick()

        configTabLayout()
    }

    private fun configTabLayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding?.viewPager?.adapter = adapter

        adapter.addFragment(OpenOrdersFragment(), "Pedidos abertos")
        adapter.addFragment(PreparingOrdersFragment(), "Em preparo")
        adapter.addFragment(ReadyOrdersFragment(), "Pedidos prontos")

        binding?.viewPager?.offscreenPageLimit = adapter.itemCount

        binding?.let {
            TabLayoutMediator(
                binding!!.tabs, it.viewPager
            ) { tab, position ->
                tab.text = adapter.getTitle(position)
            }.attach()
        }
    }

    private fun initClick() {
        binding?.ibSignout?.setOnClickListener {
            Functions(requireContext()).exibirCaixaDialogo("", getString(R.string.signout_question), { signout() }, {})
        }

        binding?.ibProducts?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productsFragment)
        }
    }

    private fun signout() {
        auth.signOut()
        findNavController().navigate(R.id.action_homeFragment_to_navigation6)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}