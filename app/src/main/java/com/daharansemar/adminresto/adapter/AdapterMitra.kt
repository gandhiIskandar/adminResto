package com.daharansemar.adminresto.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.R
import com.daharansemar.adminresto.dataClass.Kategori
import com.daharansemar.adminresto.dataClass.Menu
import com.daharansemar.adminresto.databinding.ViewholderMitraBinding
import com.daharansemar.adminresto.repository.GenericInterface
import com.daharansemar.adminresto.util.DiffUtilRepo
import com.google.android.material.button.MaterialButton

class AdapterMitra(private val callback:GenericInterface) : RecyclerView.Adapter<AdapterMitra.MyViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<Pair<Kategori, List<Menu>>>()
    val differ = AsyncListDiffer(this, diffUtilRepo)

    private lateinit var mContext: Context

    private var oldPost = -1
    private var currentPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ViewholderMitraBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        mContext = parent.context


        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = differ.currentList[position]

        with(holder) {
            bind(item)

            checkRvView(this)

            expand.setOnClickListener {

                currentPos = adapterPosition

                if (oldPost >= 0) {
                    notifyItemChanged(oldPost)
                }

                notifyItemChanged(currentPos)

            }

            bukaTutup.setOnClickListener {
                //TODO callback untuk set buka tutup mitra
            }


        }

    }

    private fun checkRvView(holder: MyViewHolder) {
        with(holder) {
            if (currentPos == adapterPosition) {
                rv.visibility = View.VISIBLE
                expand.setImageResource(R.drawable.ic_baseline_expand_less_24)
            }else{
                rv.visibility = View.GONE
                expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    inner class MyViewHolder(private val binding: ViewholderMitraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var rv: RecyclerView = binding.rvMenu
        var textView: TextView = binding.tvNamaMitra
        var expand: ImageView = binding.expandPic
        var bukaTutup: MaterialButton = binding.btnBukaTutup


        fun bind(item: Pair<Kategori, List<Menu>>) {

            setUpRv(item.second)

            textView.text = item.first.nama


        }

        private fun setUpRv(list: List<Menu>) {
            val adapter = AdapterMenu {}

            rv.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            rv.adapter = adapter
            adapter.differ.submitList(list)
        }


    }

}