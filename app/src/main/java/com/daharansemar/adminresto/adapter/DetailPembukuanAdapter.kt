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
import com.daharansemar.adminresto.dataClass.CashFlow
import com.daharansemar.adminresto.dataClass.Kategori
import com.daharansemar.adminresto.databinding.ViewholderDatapembukuanBinding
import com.daharansemar.adminresto.databinding.ViewholderPembukuanBinding
import com.daharansemar.adminresto.util.DiffUtilRepo
import com.daharansemar.adminresto.util.Utilization
import com.daharansemar.adminresto.util.Utilization.formatRupiah

class DetailPembukuanAdapter(private val cashflow:Boolean, private val callback:(Pair<String,Any>)->Unit):RecyclerView.Adapter<DetailPembukuanAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Any>()
    val differ = AsyncListDiffer(this, diffUtilRepo)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailPembukuanAdapter.MyViewHolder {
        val binding = ViewholderDatapembukuanBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailPembukuanAdapter.MyViewHolder, position: Int) {
        val item = differ.currentList[position]

        if(cashflow){
            cashFlowAdapter(holder, item as CashFlow)
        }else{
            kategoriAdapter(holder, item as Kategori)
        }



    }



    override fun getItemCount(): Int = differ.currentList.size

    private fun kategoriAdapter(holder: MyViewHolder, item:Kategori){
        holder.jumlahTv.visibility = View.GONE
        val param = holder.keteranganTv.layoutParams as ConstraintLayout.LayoutParams

        param.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

        holder.keteranganTv.layoutParams = param

        holder.keteranganTv.text = item.nama

        holder.itemView.setOnLongClickListener {
            callback.invoke(Pair("tag",item))

            true
        }


    }

    private fun cashFlowAdapter(holder:MyViewHolder, item:CashFlow){

        with(holder){

            with(item){

                keteranganTv.text = keterangan
                jumlahTv.text =formatRupiah(jumlah)

                itemView.setOnLongClickListener {

                    callback.invoke(Pair(id, jumlah))

                    true
                }

            }

        }

    }

    inner class MyViewHolder(val binding: ViewholderDatapembukuanBinding):RecyclerView.ViewHolder(binding.root){

        val keteranganTv = binding.tvKeterangan
        val jumlahTv = binding.tvJumlah




    }

}