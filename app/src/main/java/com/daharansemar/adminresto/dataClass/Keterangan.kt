package com.daharansemar.adminresto.dataClass

data class Keterangan (
    var id:String?=null,
    var keterangan:String="",
    var tipe:String="",
    var aktif: Boolean = false
        )