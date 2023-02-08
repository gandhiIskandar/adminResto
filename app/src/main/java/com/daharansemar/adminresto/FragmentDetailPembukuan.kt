package com.daharansemar.adminresto

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.adapter.DetailPembukuanAdapter
import com.daharansemar.adminresto.dataClass.CashFlow
import com.daharansemar.adminresto.dataClass.Keterangan
import com.daharansemar.adminresto.databinding.DialogEditAtauHapusBinding
import com.daharansemar.adminresto.databinding.FragmentDetailPembukuanBinding
import com.daharansemar.adminresto.databinding.FragmentDetailPesananBinding
import com.daharansemar.adminresto.util.Utilization.customDialog
import com.daharansemar.adminresto.util.Utilization.formatRupiah
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel


class FragmentDetailPembukuan : Fragment() {

    private val argument: FragmentDetailPembukuanArgs by navArgs()
    private lateinit var binding: FragmentDetailPembukuanBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var arrayRv: Array<RecyclerView>

    private lateinit var dialog: DialogTambahCashFlow

    private var nama: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailPembukuanBinding.inflate(inflater)

        with(binding) {
            arrayRv = arrayOf(
                rvSaldo, rvOmset, rvPengeluaran
            )


            dialog = DialogTambahCashFlow { data ->

                if (data.second) {
                    viewModel.setPengeluaranOrPendapatan(data.first as CashFlow) {
                        if (it) {
                            toastMaker(requireContext(), "tambah cashflow berhasil").show()
                            getData()

                        } else {
                            toastMaker(requireContext(), "tambah cashflow gagal").show()
                        }
                    }
                } else {
                    viewModel.setKeterangan(data.first as Keterangan) {
                        if (it) {
                            toastMaker(requireContext(), "tambah keterangan berhasil").show()

                            dialog.getDataKeteranganAgain()

                        } else {
                            toastMaker(requireContext(), "tambah keterangan gagal").show()
                        }
                    }
                }

            }


            tambahSaldo.setOnClickListener {
                dialog.tipe = 'a'
                dialog.show(childFragmentManager, "top up saldo")

            }

            tambahPengeluaran.setOnClickListener {
                dialog.tipe = 'c'
                dialog.show(childFragmentManager, "tambah pengeluaran")

            }

            tambahPendapatan.setOnClickListener {
                dialog.tipe = 'b'
                dialog.show(childFragmentManager, "tambah omset")

            }

        }

        harianOrNot()

        initRv()


        getData()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getData() {

        lateinit var data: List<CashFlow>

        viewModel.getCashflow {
            arrayRv.forEachIndexed { index, _ ->
                var fix = listOf<CashFlow>()

                data =
                    it.filter { x -> x.tanggal in argument.rangeTanggal[0]..argument.rangeTanggal[1] }



                calculate(data)

                when (index) {

                    0 -> {
                        fix = data.filter { item -> item.keterangan == "top up" }
                    }
                    1 -> {
                        fix = data.filter { item -> item.keterangan != "top up" && item.pendapatan }
                    }
                    2 -> {
                        fix = data.filter { item -> !item.pendapatan }
                    }

                }

                splitData(index, fix)

            }


            //get nama karyawan
            if (data.any { cf -> cf.karyawan != null } && argument.rangeTanggal[0]==argument.rangeTanggal[1]) {

                nama = data.single { cf -> cf.karyawan != null }.karyawan!!
            }

            binding.namaKaryawan.text = nama
            //end get nama karyawan


        }

    }

    private fun initRv() {
        arrayRv.forEach { it ->
            it.adapter = DetailPembukuanAdapter(true) { pair ->
                dialogData(pair)
            }
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun calculate(data: List<CashFlow>) {
        var a = 0.0


        data.forEach {
            if (it.pendapatan) {
                a += it.jumlah
            } else {
                a -= it.jumlah
            }
        }

        binding.tvPendapatan.text = formatRupiah(a)
    }

    private fun splitData(index: Int, data: List<CashFlow>) {

        val adapter = arrayRv[index].adapter as DetailPembukuanAdapter

        adapter.differ.submitList(data)


    }

    private fun dialogData(pair: Pair<String, Any>) {
        val binding = DialogEditAtauHapusBinding.inflate(layoutInflater, null, false)

        val dialog = customDialog(requireContext(), binding.root)
        dialog.show()

        with(binding) {
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            val jumlahnya = pair.second as Double

            etEdit.setText(jumlahnya.toInt().toString())

            btnEdit.setOnClickListener {
                if (etEdit.text!!.isEmpty()) {
                    toastMaker(requireContext(), "Mohon input jumlah").show()
                } else {
                    viewModel.updateCashflow(Pair(pair.first, etEdit.text.toString().toDouble())) {
                        if (it) {
                            toastMaker(requireContext(), "Update cashflow berhasil").show()
                            getData()
                            dialog.dismiss()
                        } else {
                            toastMaker(requireContext(), "Update cashflow gagal").show()
                        }
                    }
                }
            }

            btnHapus.setOnClickListener {
                viewModel.hapusCashflow(pair.first) {
                    if (it) {
                        toastMaker(requireContext(), "Hapus cashflow berhasil").show()
                        getData()
                        dialog.dismiss()

                    } else {
                        toastMaker(requireContext(), "Hapus cashflow gagal").show()

                    }
                }
            }
        }


    }

    private fun harianOrNot() {
        if (argument.rangeTanggal[0] != argument.rangeTanggal[1]) {
            binding.kasirSegment.visibility = View.GONE

        }
    }

}