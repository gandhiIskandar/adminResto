package com.daharansemar.adminresto.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewbinding.ViewBindings
import com.bumptech.glide.Glide
import com.daharansemar.adminresto.R
import com.daharansemar.adminresto.dataClass.Cart
import com.daharansemar.adminresto.databinding.ViewholderItempesananBinding
import com.daharansemar.adminresto.util.DiffUtilRepo
import com.daharansemar.adminresto.util.Utilization.formatRupiah

class ItemPesananAdapter(): RecyclerView.Adapter<ItemPesananAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Cart>()
    private lateinit var context:Context
    val differ = AsyncListDiffer(this, diffUtilRepo)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val binding = ViewholderItempesananBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_itempesanan, parent, false))

        context = parent.context

    return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        with(holder){
            val judulQty = "${currentItem.name} ${currentItem.qty}x"

            textItem.text = judulQty

            Glide.with(context).load(currentItem.foto).into(gambarItem)



            hargaItem.text = formatRupiah(currentItem.totalharga.toDouble())

        }

    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class MyViewHolder(binding:ViewholderItempesananBinding):RecyclerView.ViewHolder(binding.root){

        val gambarItem = binding.itemPesanan
        val textItem = binding.namaItem
        val hargaItem = binding.totalHargaItem


    }
}