package com.daharansemar.adminresto.dataClass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    var key: String? = null,
    var name: String? = null,
    var foto: String? = null,
    var harga: String? = null,
    var qty: Int = 0,
    var totalharga: Long = 0L,
    var desc1: String? = null,
    var desc2:String?=null,
    var potongan:Long=0L,
    var stokk:Long=0L
) : Parcelable

