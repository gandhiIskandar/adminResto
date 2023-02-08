package com.daharansemar.adminresto.dataClass

import com.daharansemar.adminresto.util.Utilization.untilMonth
import com.google.firebase.Timestamp

data class CashFlow(
    var id:String="id",
    val tanggal :Long = untilMonth(Timestamp.now().toDate()),
    val jumlah: Double= 0.0,
    val pendapatan:Boolean = false,
    val keterangan:String = "keterangan",
    var karyawan:String?= null

)
