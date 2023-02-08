package com.daharansemar.adminresto.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.R
import com.daharansemar.adminresto.dataClass.Keterangan
import com.daharansemar.adminresto.databinding.ViewholderKeteranganBinding
import com.daharansemar.adminresto.util.DiffUtilRepo

class AdapterKeterangan(private val callback:(Pair<Keterangan,Boolean>)->Unit):RecyclerView.Adapter<AdapterKeterangan.MyViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<Keterangan>()
    val differ = AsyncListDiffer(this, diffUtilRepo)

    private var prevIndex= -1

    private lateinit var mContext:Context





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ViewholderKeteranganBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return MyViewHolder(binding)


         }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = differ.currentList[position]
        val keterangan = item.keterangan
        val aktif = item.aktif




        holder.backgroundAktif(aktif)

        holder.textNya.text = keterangan

        holder.itemView.setOnClickListener {

            if (prevIndex >= 0) {


                differ.currentList[prevIndex].aktif = false
                Log.d("tester",prevIndex.toString())
                notifyItemChanged(prevIndex)

            }

            differ.currentList[position].aktif = true
            notifyItemChanged(position)
            prevIndex = holder.adapterPosition



            callback.invoke(Pair(item, true))


        }

        holder.itemView.setOnLongClickListener {

            callback.invoke(Pair(item, false))
            true
        }

    }

    inner class MyViewHolder(binding:ViewholderKeteranganBinding):RecyclerView.ViewHolder(binding.root){

       private val backgroundnya = binding.parentnya
        val textNya = binding.textKeterangan


        fun backgroundAktif(aktif:Boolean){
            var primaryColor:Drawable? = null


            primaryColor = if(aktif) {
                AppCompatResources.getDrawable(mContext, R.drawable.backgroundgajelas)
            }else{
                AppCompatResources.getDrawable(mContext, R.drawable.background_abu)
            }

            backgroundnya.background = primaryColor
        }


}

    fun resetPrevIndex(){
        prevIndex =-1
    }
}