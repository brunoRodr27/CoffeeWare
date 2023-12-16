package com.example.coffeeware.ui.auth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.coffeeware.R
import com.example.coffeeware.databinding.FragmentRecoverBinding
import com.example.coffeeware.databinding.FragmentRegisterBinding
import com.example.coffeeware.helper.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverFragment : Fragment() {

    private var _binding: FragmentRecoverBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecoverBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClicks()

    }

    private fun initClicks() {
        binding?.btnRecover?.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val email = binding?.etEmail?.text.toString().trim()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        
        if (email.isNotEmpty()) {
            binding?.progressBar?.isVisible = true

            recoverAccountUset(email)
        } else {
            Toast.makeText(requireContext(), "Informe seu E-mail", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recoverAccountUset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { account ->
                if (account.isSuccessful) {
                    Toast.makeText(requireContext(), "Pronto, acabamos de enviar um link para seu e-mail", Toast.LENGTH_SHORT).show()
                    binding?.progressBar?.isVisible = false
                } else {
                    Toast.makeText(requireContext(), FirebaseHelper.validError(account.exception?.message ?: ""), Toast.LENGTH_SHORT).show()
                    binding?.progressBar?.isVisible = false
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}