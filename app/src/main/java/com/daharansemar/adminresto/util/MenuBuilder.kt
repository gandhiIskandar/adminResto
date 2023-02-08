package com.daharansemar.adminresto.util

import com.daharansemar.adminresto.dataClass.Menu
typealias strn = String?
typealias l = Long

class MenuBuilder {
    var key: strn = null
    var nama: strn = null
    var harga: l = 0L
    var tipe: strn = null
    var menuKey: strn = null
    var rasaKey: strn = null
    var foto: strn = null
    var deskripsi: strn = null //kelengkapan menu
    var deskripsi1: strn = null
    var terjual: l = 0L // default awal 0L
    var promo: Boolean = false
    var rekom: Boolean = false
    var potongan: l = 0L
    var tipeMinuman: strn = null
    var stok: l = 0L // default awal 0L
    var minumanKey:String?=null

    fun create() = Menu(
        key,
        nama,
        harga,
        tipe,
        foto,
        deskripsi,
        menuKey,
        rasaKey,
        terjual,
        deskripsi1,
        promo,
        rekom,
        potongan,
       tipeMinuman =  tipeMinuman,
       minumanKey =  minumanKey,
         stok = stok
    )


}