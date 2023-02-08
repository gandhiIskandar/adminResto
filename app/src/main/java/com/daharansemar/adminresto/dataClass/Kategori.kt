package com.daharansemar.adminresto.dataClass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kategori (
    var id:String?=null,
    var nama:String?=null,
    var tipe:String?=null,
    var buka:Boolean=false
        ):Parcelable