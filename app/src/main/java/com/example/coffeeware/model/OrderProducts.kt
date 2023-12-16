package com.example.coffeeware.Model

import android.os.Parcelable
import com.example.coffeeware.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderProducts(
    var id: String = "",
    var order: String = "",
    var product: String = ""
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDataBase().push().key ?: ""
    }
}