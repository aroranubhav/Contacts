package com.maxi.contacts.domain.model

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("number")
    val number: String = "",
    @SerializedName("email")
    val email: String? = ""
)
