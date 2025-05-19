package com.maxi.contacts.domain.model

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

/*@Stable*/
data class Contact(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("numbers")
    var numbers: ArrayList<String> = arrayListOf(),
    @SerializedName("emails")
    var emailIds: ArrayList<String> = arrayListOf()
)