package com.example.yadro_test_3.aidl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val avatar: String? = null
) : Parcelable
