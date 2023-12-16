package com.example.coffeeware.Model

import android.os.Parcelable
import com.example.coffeeware.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var value: Double = 0.00,
    var status: String = "",
    var image: String = ""
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDataBase().push().key ?: ""
    }
}