package com.daharansemar.adminresto.dataClass


import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Pesanan (
    var id:String? = null,
    var item:List<Cart> ?= null,
    var username:String?=null,
    var idPelanggan:String?=null,
    var alamat: Alamat?=null,
    var status:String?=null,
    var totalHarga:Double?=null,
    var catatan:String?=null,
    var buktiTF:String?=null,
    var tanggal:Timestamp=Timestamp.now(),
    var expanded:Boolean = false,
        ):Parcelable