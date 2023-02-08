package com.daharansemar.adminresto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.R

import com.daharansemar.adminresto.databinding.ViewholderPembukuanBinding
import com.daharansemar.adminresto.util.DiffUtilRepo
import com.daharansemar.adminresto.util.Utilization.formatRupiah
import com.daharansemar.adminresto.util.Utilization.simpleDateFormatUntilMonth
import java.util.*

class PembukuanAdapter (private val callback:(Array<Long>) -> Unit): RecyclerView.Adapter<PembukuanAdapter.MyViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<MutableMap<String, Any>>()
    val differ = AsyncListDiffer(this, diffUtilRepo)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ViewholderPembukuanBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_pembukuan, parent, false)
        )

        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]



        val awal = currentItem["tanggalAwal"] as Long?
        val akhir = currentItem["tanggalAkhir"] as Long?

        val tanggal1 = simpleDateFormatUntilMonth(Date(awal!!))
        val tanggal2 = simpleDateFormatUntilMonth(Date(akhir!!))

        val tanggal = if(awal != akhir)"$tanggal1 - $tanggal2" else tanggal1

        with(holder) {
            tanggalTv.text = tanggal

            val total = currentItem["total"] as Double?

                totalTv.text = formatRupiah(total)



            btnDetail.setOnClickListener {
                callback.invoke(arrayOf(awal, akhir))
            }
        }


    }


    override fun getItemCount(): Int = differ.currentList.size

    inner class MyViewHolder(val binding: ViewholderPembukuanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tanggalTv = binding.statusTanggal
        val totalTv = binding.statusTotal
        val btnDetail = binding.expands



    }

}