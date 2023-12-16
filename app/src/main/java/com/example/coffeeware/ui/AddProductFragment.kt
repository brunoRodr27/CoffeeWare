package com.example.coffeeware.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.coffeeware.Model.Product
import com.example.coffeeware.databinding.FragmentAddProductBinding
import com.example.coffeeware.helper.FirebaseHelper
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddProductFragment : Fragment() {

    private val args: AddProductFragmentArgs by navArgs()

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding

    val GALLERY_REQUEST_CODE = 101

    private lateinit var product: Product
    private var newProduct: Boolean = true
    private var imageUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        initWatcher()

        getArgs()
    }

    private fun getArgs() {
        args.product.let {
            if (it != null) {
                product = it

                configProduct()
            }
        }
    }

    private fun configProduct() {
        newProduct = false

        binding?.tvTitle?.text = "Editar produto"

        when (product.status) {
            "T" -> binding?.sSituacao?.isChecked = true
            else -> binding?.sSituacao?.isChecked = false
        }

        if (product.image.isNotEmpty()) {
            binding?.ivProduct?.let {
                Glide.with(this)
                    .load(product.image)
                    .into(it)
            }
        }

        binding?.etName?.setText(product.name)
        binding?.etDesc?.setText(product.description)
        binding?.etValue?.setText(String.format("%.2f", product.value))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            binding?.progressBar?.isVisible = true

            val selectedImage = data?.data
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("images/${UUID.randomUUID()}")
            val uploadProduct = selectedImage?.let { imageRef.putFile(it) }

            uploadProduct?.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    binding?.ivProduct?.setImageURI(selectedImage)
                    binding?.progressBar?.isVisible = false
                }
            }

        }
    }

    private fun initClicks() {
        binding?.ibClose?.setOnClickListener{
            findNavController().popBackStack()
        }

        binding?.ivProduct?.setOnClickListener { openGallery() }

        binding?.btnSave?.setOnClickListener { validProduct() }
    }

    private fun validProduct() {
        if (binding?.progressBar?.isVisible == false) {

            val name = binding?.etName?.text.toString()
            val desc = binding?.etDesc?.text.toString()
            val value = binding?.etValue?.text.toString().trim().replace(",", ".")
            val status = if (binding?.sSituacao?.isChecked == true) "T" else "F"

            if (name.isNotEmpty()) {
                if ((value.isNotEmpty()) && (value != "0.00")) {
                    if (newProduct) product = Product()
                    product.name = name
                    product.description = desc
                    product.value = value.toDouble()
                    product.status = status
                    if (imageUrl != "") product.image = imageUrl

                    saveProduct()
                } else {
                    Toast.makeText(requireContext(), "Adicione o valor do produto.", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(requireContext(), "Adicione o nome do produto.", Toast.LENGTH_LONG).show()
            }
        }  else {
            Toast.makeText(requireContext(), "Aguarde a imagem ser importada.", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveProduct() {
        FirebaseHelper
            .getDataBase()
            .child("Product")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(product.id)
            .setValue(product)
            .addOnCompleteListener {product ->
                if (product.isSuccessful) {
                    if (newProduct) {
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Produto salvo com sucesso.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Produto atualizado com sucesso.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar o produto.", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao salvar o produto.", Toast.LENGTH_LONG).show()
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun initWatcher() {
        binding?.etValue?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString().replace(",", "")

                val formattedText = if (text.isEmpty()) {
                    "0,00"
                } else {
                    val doubleValue = text.toDouble() / 100.0
                    String.format("%.2f", doubleValue)
                }

                binding?.etValue?.removeTextChangedListener(this)
                binding?.etValue?.setText(formattedText)
                binding?.etValue?.setSelection(formattedText.length)
                binding?.etValue?.addTextChangedListener(this)
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}