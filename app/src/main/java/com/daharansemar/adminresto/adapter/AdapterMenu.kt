package com.daharansemar.adminresto.adapter

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daharansemar.adminresto.dataClass.Menu
import com.daharansemar.adminresto.databinding.ViewholderMenuBinding
import com.daharansemar.adminresto.util.DiffUtilRepo
import com.daharansemar.adminresto.util.Utilization.formatRupiah
import com.google.android.material.button.MaterialButton

class AdapterMenu( private val invoker: (Pair<Menu,Boolean>) -> Unit) :
    RecyclerView.Adapter<AdapterMenu.MyViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<Menu>()
    val differ = AsyncListDiffer(this, diffUtilRepo)

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ViewholderMenuBinding.inflate(LayoutInflater.from(parent.context))

        context = parent.context

        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = differ.currentList[position]

        Glide.with(context).load(item.foto).into(holder.image)


        with(holder) {

            if(item.tipe == "minuman"){
                kelengkapan.visibility = View.GONE
            }

            editBtn.setOnClickListener {
                invoker.invoke(Pair(item,true))
            }
            remove.setOnClickListener {
                invoker.invoke(Pair(item,false))
            }

            nama.text = item.nama
            kelengkapan.text = item.deskripsi
            deskripsi.text = item.deskripsi1
            hargaTv.text = formatRupiah(item.harga.toDouble())

           if(item.stok==0L){

               Log.d("duniaGaib", item.nama.toString())

               stokHabisView.visibility = View.VISIBLE
               val matrix = ColorMatrix()
               matrix.setSaturation(0F)
               image.colorFilter = ColorMatrixColorFilter(matrix)
           }

            if (item.promo) {
                diskonFun(
                    arrayListOf(
                        hargaTv,
                        promoText
                    ),
                    (item.harga - item.potongan).toDouble()
                )
            }


        }


    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class MyViewHolder(binding: ViewholderMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {


        var nama: TextView
        var kelengkapan: TextView
        var deskripsi: TextView
        var stokHabisView: TextView
        var image: ImageView
        var editBtn: ImageView
        var promoText: TextView
        var hargaTv: TextView
        var remove:ImageView

        init {
            with(binding) {
                nama = judulMenu
                hargaTv = hargaMenu
                kelengkapan = isianMenu
                deskripsi = descMenu
                stokHabisView = stokhabis
                image = gambarMenu
                editBtn = btnEditMenu
                promoText = hargaMenupromo
                remove = btnHapusMenu
            }
        }


        fun diskonFun(list: List<View>, harga: Double) {
            val text = list[0] as TextView
            val promoText = list[1] as TextView

            text.textSize = 13f
            text.paintFlags = text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            promoText.text = formatRupiah(harga)
            promoText.visibility = View.VISIBLE
        }

    }


}