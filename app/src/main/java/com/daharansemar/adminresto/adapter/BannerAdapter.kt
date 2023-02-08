package com.daharansemar.adminresto.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daharansemar.adminresto.dataClass.ImageData
import com.daharansemar.adminresto.databinding.ViewholderBannerBinding


import com.daharansemar.adminresto.repository.BannerInterface
import com.daharansemar.adminresto.util.DiffUtilRepo

class BannerAdapter(private val callback: BannerInterface) :
    RecyclerView.Adapter<BannerAdapter.MyViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<ImageData>()
    val differ = AsyncListDiffer(this, diffUtilRepo)

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ViewholderBannerBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        mContext = parent.context


        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = differ.currentList[position]


        with(holder) {


                    setImage(item.imageUrl)

            hapusBanner.setOnClickListener {

        callback.deleteImageData(item.id!!)


            }

            editBanner.setOnClickListener {
                callback.editImageData(item)
            }


        }


    }


    inner class MyViewHolder(private val binding: ViewholderBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val btnTambah = binding.imageView6
        val picture = binding.gambar
        val btnGroup = binding.buttonGroup
        val editBanner = binding.editBanner
        val hapusBanner = binding.hapusBanner

        fun setImage(data: Any) {



            Glide.with(mContext).load(
                if (data is Bitmap) {
                    data as Bitmap
                } else {
                    data as String
                }
            ).into(picture)

            btnGroup.visibility = View.VISIBLE

        }


    }

    override fun getItemCount(): Int = differ.currentList.size



}