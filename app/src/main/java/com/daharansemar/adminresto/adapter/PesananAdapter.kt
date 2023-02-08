package com.daharansemar.adminresto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.dataClass.Pesanan
import com.daharansemar.adminresto.databinding.PesananViewholderBinding
import com.daharansemar.adminresto.util.DiffUtilRepo
import com.daharansemar.adminresto.util.Utilization.formatRupiah
import com.daharansemar.adminresto.util.Utilization.simpleDateFormat


class PesananAdapter(private val invoker: (Pesanan) -> Unit) :
    RecyclerView.Adapter<PesananAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Pesanan>()
    val differ = AsyncListDiffer(this, diffUtilRepo)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PesananViewholderBinding.inflate(LayoutInflater.from(parent.context))

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        with(holder) {

            alamat.text = currentItem.alamat?.alamat
            harga.text = formatRupiah(currentItem.totalHarga!!)
            tanggal.text = simpleDateFormat(currentItem.tanggal.toDate())

            hargaItem.text = formatRupiah(currentItem.totalHarga!!-currentItem.alamat!!.ongkir!!)
            ongkir.text = formatRupiah(currentItem.alamat!!.ongkir!!)
            total.text = formatRupiah(currentItem.totalHarga!!)
            detailId.text = currentItem.id

            check.setOnClickListener {
                invoker.invoke(currentItem)
            }
        }


    }

    override fun getItemCount(): Int = differ.currentList.size

    class MyViewHolder(binding: PesananViewholderBinding) : RecyclerView.ViewHolder(binding.root) {

        val alamat = binding.statusAlamat
        val tanggal = binding.statusTanggal
        val harga = binding.statusTotal
        val check = binding.expands
        val hargaItem = binding.detailHarga
        val ongkir = binding.detailOngkir
        val total = binding.detailTotalsemua
        val detailId = binding.detailid


    }
}