package com.daharansemar.adminresto

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daharansemar.adminresto.adapter.ItemPesananAdapter
import com.daharansemar.adminresto.dataClass.Pesanan
import com.daharansemar.adminresto.util.Utilization
import com.daharansemar.adminresto.databinding.FragmentDetailPesananBinding
import com.daharansemar.adminresto.databinding.ImageDialogBinding

import com.daharansemar.adminresto.viewModel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FragmentDetailPesanan : Fragment() {

    private lateinit var binding: FragmentDetailPesananBinding
    private lateinit var pesanan: Pesanan
    private val args: FragmentDetailPesananArgs by navArgs()
    private val viewModel: MainViewModel by viewModels()
    private val util = Utilization
    private lateinit var arrayImage: Array<MutableMap<Any, Any>>


    private var index = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pesanan = args.pesanan

        binding = FragmentDetailPesananBinding.inflate(layoutInflater)

        setUi()


        initItemRv()




            with(binding) {
                arrayImage =
                    arrayOf(
                        mutableMapOf("image" to detailDisiapkan, "clickable" to true, "status" to "disiapkan"),
                        mutableMapOf("image" to detailDimasak, "clickable" to true, "status" to "dimasak"),
                        mutableMapOf("image" to detailDikirim, "clickable" to true, "status" to "dikirim"),
                        mutableMapOf("image" to detailSelesai, "clickable" to true, "status" to "selesai"),
                        mutableMapOf("image" to detailDitolak, "clickable" to true, "status" to "ditolak")
                    )
            }

            detailPesananClickListener()
             statusTerakhirPesanan()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fungsi yang berguna untuk mengetahui apakah proses update berhasil atau tidak
        updateCallback()

    }

    private fun updateCallback() {
        viewModel.statusUpdate.observe(viewLifecycleOwner, Observer { sukses ->
            if (sukses) {
                Toast.makeText(
                    requireContext(),
                    "Update status pesanan berhasil",
                    Toast.LENGTH_LONG
                ).show()
                ubahStatus(index)
                if(index == 3 || index==4){
                    findNavController().popBackStack()
                }



            } else {
                Toast.makeText(
                    requireContext(),
                    "Update status pesanan gagal, mohon cek koneksi internet",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun detailPesananClickListener() {
        with(binding) {
            detailAntar.setOnClickListener { navigateToMaps() }
            detailBukti.setOnClickListener { showDialogDetailBukti(pesanan.buktiTF!!) }



            arrayImage.forEachIndexed { idx, _ ->

                with(arrayImage) {
                    val image = this[idx]["image"] as ImageView
                    image.setOnClickListener {
                        if (this[idx]["clickable"] as Boolean && pesanan.status != "ditolak" && pesanan.status != "selesai") {
                              val  status = this[idx]["status"] as String
                            konfirmasiUbahStatusPesanan(status)
                            index = idx

                        }
                    }


                }


            }


        }
    }

    private fun statusTerakhirPesanan(){
        val status = pesanan.status
        var idx =0

        when(status){
            "disiapkan" -> idx =0
            "dimasak" -> idx =1
            "dikirim" -> idx =2
            "selesai" -> idx =3
            "ditolak" -> idx =4
        }

        ubahStatus(idx)


    }

    //memunculkan dialog konfirmasi untuk mengubah status pesanan berdasarkan id
    private fun konfirmasiUbahStatusPesanan(status: String) {

        val alertBuilder = AlertDialog.Builder(requireContext())

        alertBuilder.setMessage("Ubah status pesanan menjadi \"${status}\" ?")
            .setPositiveButton("ya") { _, _ ->
                viewModel.updatePesanan(status, pesanan.id!!, pesanan.totalHarga!!)
            }
            .setNegativeButton("tidak") { dialog, _ ->

                dialog.dismiss()

            }
            .setCancelable(false)

        val alertDialog = alertBuilder.create()

        alertDialog.show()


    }

    private fun showDialogDetailBukti(urlGambar: String) {
        val binding = ImageDialogBinding.inflate(layoutInflater)
        Glide.with(requireContext()).load(urlGambar).into(binding.buktiPic)
        val dialog = util.customDialog(requireContext(), binding.root)
        dialog.show()
    }


    private fun setUi() {
        with(binding) {

            //ringkasan pembayaran
            detailid.text = pesanan.id!!.substring(0, 6)
            detailHarga.text = util.formatRupiah(pesanan.totalHarga!! - pesanan.alamat!!.ongkir!!)
            detailOngkir.text = util.formatRupiah(pesanan.alamat!!.ongkir!!)
            detailTotalsemua.text = util.formatRupiah(pesanan.totalHarga!!)
            // end ringkasan pembayaran

            //info pembeli
            valNamaDetail.text = pesanan.username!!
            valAlamatDetail.text = pesanan.alamat!!.alamat
            valNomorDetail.text = pesanan.alamat!!.nomorHp

        }
    }

    private fun navigateToMaps() {

        //uri untuk perintah google maps, untuk case ini kita menggunakan navigasi
        val uri = Uri.parse("google.navigation:q=${pesanan.alamat!!.lat},${pesanan.alamat!!.long}")

        //set action view dan masukan uri perintah ke mapintent
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)

        //intent spesifik ke google maps(berdasarkan nama package)
         mapIntent.setPackage("com.google.android.apps.maps")


        //mulai intent
        startActivity(mapIntent)
    }

    private fun initItemRv() {
        binding.pesananItem.apply {
            adapter = ItemPesananAdapter()
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        val adapter = binding.pesananItem.adapter as ItemPesananAdapter

        adapter.differ.submitList(pesanan.item)
    }

    private fun ubahStatus(index: Int) {

        with(arrayImage) {
            for (i in 0..size) {

                if (i != this.lastIndex && index != this.lastIndex) {
                    this[i].apply {
                        val image = this["image"] as ImageView
                        image.setImageResource(R.color.primary)

                        this["clickable"] = false

                    }
                } else if (i == this.lastIndex) {
                    this[i].apply {
                        val image = this["image"] as ImageView
                        image.setImageResource(R.color.red)

                        this["clickable"] = false

                    }
                }
                if (i == index) {
                    break
                }
            }
        }


    }

}