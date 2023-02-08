package com.daharansemar.adminresto

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.adapter.AdapterKeterangan
import com.daharansemar.adminresto.dataClass.CashFlow
import com.daharansemar.adminresto.dataClass.Keterangan
import com.daharansemar.adminresto.databinding.DialogTambahCashflowBinding
import com.daharansemar.adminresto.databinding.DialogTambahKategoriBinding
import com.daharansemar.adminresto.util.Utilization.customDialog
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.util.Utilization.untilMonth
import com.daharansemar.adminresto.viewModel.MainViewModel
import com.google.firebase.Timestamp


class DialogTambahCashFlow(private val callback: (Pair<Any, Boolean>) -> Unit) : DialogFragment() {

    /* tipe
    * 'a' -> saldo
    * 'b' -> pendapatan
    * 'c' -> pengeluaran
    * 'd' -> karyawan
    *
    *
    * */


    var tipe: Char = 'a'
   private  var keteranganData:Keterangan?=null

    private lateinit var binding: DialogTambahCashflowBinding

    private lateinit var rvKeterangan: RecyclerView

    private var topup = false

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var dataKeterangan: List<Keterangan>



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        topup = false

        binding = DialogTambahCashflowBinding.inflate(layoutInflater, null, false)

        rvKeterangan = binding.rvKeterangan

       /* adapterKeterangan = AdapterKeterangan {
            if(it.second){
                keterangan = it.first.keterangan
            }
        }
*/
//        rvKeterangan.adapter = adapterKeterangan

        rvKeterangan.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        val builder = AlertDialog.Builder(requireContext())
            .setView(binding.root)


        if (tipe == 'a') {
            spesialSaldo()
        }else if(tipe=='d'){
            binding.etJumlah.hint = "saldo"
            binding.listKaryawan.setText("kasir")
        }



        with(binding) {

            tambahKeterangan.setOnClickListener {
                dialogTambahKeterangan()
            }

            judulTambahDialog.text = tag

            dialogClose.setOnClickListener {
                dismiss()
            }
            tambahCf.setOnClickListener {
                    if(tipe!='d'){
                        callbackData()
                    }else{
                        bukaToko()
                    }



            }

        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)



        return dialog


    }

    override fun onResume() {
        super.onResume()

      getDataKeteranganAgain()


    }

    private fun spesialSaldo() {
        //dah ga kepake
        binding.rvKeterangan.visibility = View.GONE
        binding.linearbutton.visibility = View.GONE
        //end

        binding.listLayout.visibility = View.GONE

        topup = true
    }

    private fun dialogTambahKeterangan() {

        var jenis = ""

        when(tipe){
            'a'-> jenis ="saldo"
            'b' -> jenis ="omset"
            'c' -> jenis ="pengeluaran"
            'd' -> jenis ="karyawan"
        }

        val binding = DialogTambahKategoriBinding.inflate(layoutInflater, null, false)

        val dialog = customDialog(requireContext(), binding.root)

        binding.judulTambahDialog.text = if(tipe!='d')"tambah keterangan" else "tambah karyawan"

        binding.dialogClose.setOnClickListener {
            dialog.dismiss()
        }




        binding.tambahMenu.setOnClickListener {
            val keteranganny = binding.etMenu.text.toString()
            if (keteranganny.isEmpty()) {
                toastMaker(requireContext(), "keterangan masih kosong")
            } else {
                callback.invoke(
                    Pair(
                        Keterangan(
                             keterangan =keteranganny,
                           tipe = jenis
                        ),
                        false
                    )
                )

                dialog.dismiss()



            }

        }

     dialog.show()

    }


    private fun callbackData(){
        if (binding.etJumlah.text!!.isEmpty() && keteranganData!=null) {
            toastMaker(requireContext(), "Mohon lengkapi data").show()
        } else {
            callback.invoke(
                Pair(
                    CashFlow(
                        "",
                        untilMonth(Timestamp.now().toDate()),
                        binding.etJumlah.text.toString().toDouble(),
                        tipe == 'a' || tipe == 'b',
                        if (!topup) keteranganData!!.keterangan else "top up"
                    ),
                    true
                )
            )
            dismiss()
        }

    }

    private fun bukaToko(){
        if (binding.etJumlah.text.toString()=="" && keteranganData == null) {
            toastMaker(requireContext(), "Mohon lengkapi data").show()
        } else {
            callback.invoke(
                Pair(
                    CashFlow(
                        "",
                        untilMonth(Timestamp.now().toDate()),
                        binding.etJumlah.text.toString().toDouble(),
                        true,
                       "top up",
                        //nama karyawan
                    keteranganData!!.keterangan
                    ),
                    true
                )
            )
            dismiss()
        }

    }



    fun getDataKeteranganAgain(){

        viewModel.getKeterangan {

            dataKeterangan = it

            var filtered = listOf<Keterangan>()

            when (tipe) {
                'a' -> filtered = filter("saldo")
                'b' -> filtered = filter("omset")
                'c' -> filtered = filter("pengeluaran")
                'd' -> {
                    filtered = filter("karyawan")
                    binding.listKaryawan.setText("kasir")


                }
            }

            val arrayAdapter = ArrayAdapter(binding.root.context, R.layout.textajalah, filtered.map{data -> data.keterangan}.toTypedArray())

            binding.listKaryawan.setAdapter(arrayAdapter)

            binding.listKaryawan.setOnItemClickListener { _, _, position, _ ->
                keteranganData = filtered[position]

            }

            binding.hapusKeterangan.setOnClickListener {

                if(keteranganData !=null){
                    viewModel.hapusKeterangan(keteranganData!!.id!!){ kkk->
                        if(kkk){
                            toastMaker(requireContext(), "hapus keterangan berhasil").show()
                            keteranganData = null

                            getDataKeteranganAgain()

                        }else{
                            toastMaker(requireContext(), "hapus keterangan gagal").show()
                        }
                    }

                }else{
                    toastMaker(requireContext(), "pilih keterangan terlebih dahulu").show()
                }

            }

        }

    }

    private fun filter(string:String): List<Keterangan> {

        return dataKeterangan.filter{data -> data.tipe==string}

    }





}