package com.example.coffeeware.Model

import android.os.Parcelable
import com.example.coffeeware.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    var id: String = "",
    var table: String = "",
    var obs: String = "",
    var value: Double = 0.00,
    var status: Int = 0
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDataBase().push().key ?: ""
    }
}