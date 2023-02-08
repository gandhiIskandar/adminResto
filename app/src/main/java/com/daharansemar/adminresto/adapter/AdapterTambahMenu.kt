package com.daharansemar.adminresto.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.R
import com.daharansemar.adminresto.dataClass.Kategori
import com.daharansemar.adminresto.databinding.PlaceholderTambahmenuBinding
import com.daharansemar.adminresto.util.DiffUtilRepo
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.coroutineScope

class AdapterTambahMenu(private val invoker: (String) -> Unit) :
    RecyclerView.Adapter<AdapterTambahMenu.MyViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<String>()
    var differ = AsyncListDiffer(this, diffUtilRepo)

    private var arrayString = arrayListOf<String>()


    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PlaceholderTambahmenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)




        context = parent.context


        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var currentItem = differ.currentList[position]

        if(currentItem.last()=='*'||currentItem.last()=='.'){
            currentItem = currentItem.dropLast(1)
        }

        holder.toogleButtonKelengkapan(arrayString.contains(currentItem))


        holder.button.text = currentItem

        holder.button.setOnClickListener {

            if(arrayString.contains(currentItem)){
                arrayString.remove(currentItem)
            }else{
                arrayString.add(currentItem)
            }

            val kelengkapan = arrayString.joinToString(" + ")
            invoker.invoke(kelengkapan)

            notifyItemChanged(position)

        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class MyViewHolder(binding: PlaceholderTambahmenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val button = binding.pilihButton
        fun toogleButtonKelengkapan(toogle: Boolean) {

            var primaryColor = 0
            var textColor = 0

            if (toogle) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    primaryColor = context.getColor(R.color.primary)
                    textColor = context.getColor(R.color.primary)
                } else {
                    primaryColor = context.resources!!.getColor(R.color.primary)
                    textColor = context.resources!!.getColor(R.color.primary)
                }
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    primaryColor = Color.LTGRAY
                    textColor = context.getColor(R.color.abu_tua)
                } else {
                    primaryColor = Color.LTGRAY
                    textColor = context.resources!!.getColor(R.color.abu_tua)
                }

            }

          //  button.strokeColor = ColorStateList.valueOf(primaryColor)
            button.backgroundTintList = ColorStateList.valueOf(primaryColor)
        //    button.setTextColor(textColor)


        }
    }


}