package com.daharansemar.adminresto

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daharansemar.adminresto.adapter.PembukuanAdapter
import com.daharansemar.adminresto.dataClass.CashFlow
import com.daharansemar.adminresto.dataClass.Keterangan
import com.daharansemar.adminresto.databinding.DialogTambahKategoriBinding
import com.daharansemar.adminresto.databinding.FragmentPembukuanBinding
import com.daharansemar.adminresto.databinding.LoadingDialogBinding
import com.daharansemar.adminresto.util.Utilization.customDialog
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel
import java.util.*
import kotlin.collections.ArrayList


class PembukuanFragment : Fragment() {

    private lateinit var binding: FragmentPembukuanBinding
    private val mainViewModel: MainViewModel by viewModels()


    private lateinit var arrayCashflow: ArrayList<CashFlow>

    private val bulan = arrayOf(
        "Januari", "Februari", "Maret", "April",
        "Mei", "Juni", "Juli", "Agustus", "September",
        "Oktober", "November", "Desember"
    )

    private lateinit var dialogLoading:AlertDialog

    private val dataTanggal = arrayListOf<Long>()

    private lateinit var adapterr: PembukuanAdapter


    private var prevWaktu:Int = -1


    private lateinit var arrayButtonWaktu:Array<Button>

    private lateinit var dialogTambahCashFlow: DialogTambahCashFlow

    lateinit var mContext:Context



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPembukuanBinding.inflate(layoutInflater)
        initRv()
        dialogBukaToko()
        with(binding) {
            arrayButtonWaktu = arrayOf(btnHarian, btnMingguan, btnBulanan, btnTahunan)
        }


     dialogLoading = customDialog(requireContext(), LoadingDialogBinding.inflate(layoutInflater).root)

     /*   dialogTambahCashFlow = DialogTambahCashFlow {
            mainViewModel.setPengeluaranOrPendapatan(it){ succed ->
                if(succed){
                    toastMaker(requireContext(), "Tambah cashflow berhasil").show()
                    getData()
                }else{
                    toastMaker(requireContext(), "Tambah cashflow gagal").show()

                }

            }

        }*/

        setClickListener()




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }


    private fun setClickListener(){
        arrayButtonWaktu.forEachIndexed{idx, item->

            item.setOnClickListener {
                setAktifWaktu(idx)

            }

        }

        binding.tambahCashFlow.setOnClickListener {
                dialogTambahCashFlow.show(childFragmentManager, "buka restaurant")
        }


    }

    private fun setAktifWaktu(indexWaktu: Int){

        if(prevWaktu != -1 && prevWaktu != indexWaktu){

            setNonaktif(prevWaktu)

        }
        if(prevWaktu!=indexWaktu) {

            val button = arrayButtonWaktu[indexWaktu]
            var primaryColor = 0
            var textColor = 0

            when (indexWaktu) {
                0 -> {
                    adapterr.differ.submitList(chunkedArray(1))
                }
                1 -> {
                    adapterr.differ.submitList(chunkedArray(7))
                }
                2 -> {
                    adapterr.differ.submitList(chunkedArray(30))
                }
                3 -> {
                    adapterr.differ.submitList(chunkedArray(365))
                }

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                primaryColor = mContext.getColor(R.color.primary)
                textColor = mContext.getColor(R.color.abu_tua)
            } else {
                primaryColor = mContext.resources!!.getColor(R.color.primary)
                textColor = mContext.resources!!.getColor(R.color.abu_tua)
            }


            button.setBackgroundColor(primaryColor)
            button.setTextColor(textColor)

            prevWaktu = indexWaktu
        }
    }

    private fun setNonaktif(indicator: Int) {

       val button = arrayButtonWaktu[indicator]
        var primaryColor = 0
        var textColor = 0


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            primaryColor = mContext.getColor(R.color.abu_tua)
            textColor = mContext.getColor(R.color.primary)
        } else {
            primaryColor = mContext.resources!!.getColor(R.color.abu_tua)
            textColor = mContext.resources!!.getColor(R.color.primary)
        }

        button.setBackgroundColor(primaryColor)
        button.setTextColor(textColor)


    }



    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    /* private fun showDateRangePicker(invoker: (Pair<Long, Long>) -> Unit) {

         val builder = MaterialDatePicker.Builder.dateRangePicker()
         val picker = builder.build()
         picker.apply {
             show(this@PembukuanFragment.requireActivity().supportFragmentManager, picker.toString())
             addOnNegativeButtonClickListener { dismiss() }
             addOnPositiveButtonClickListener { data ->
                 invoker.invoke(data.toKotlinPair())
             }


         }


     }*/


    private fun chunkedArray( waktu:Int): MutableList<MutableMap<String, Any>> {



        val dataPerWaktu = mutableListOf<MutableMap<String, Any>>()
        dataTanggal.chunked(waktu).forEach{ tanggalList ->



            var totalPendapatan =0.0
            var totalPengeluaran = 0.0


            val mutableMap = mutableMapOf<String, Any>()

            val awal = tanggalList[0]
            val akhir = tanggalList.last()


            //set tanggal awal dan akhir karena setiap foreach dataTanggal mengandung List
            mutableMap["tanggalAwal"] = awal
            mutableMap["tanggalAkhir"] = akhir



            //cek jika arrayPesanan apakah ada data yang tanggalnya antara tanggalAwal dan tanggalAkhir

            arrayCashflow.forEach { data ->
                val untilMonth = data.tanggal

                if (untilMonth in awal..akhir) {

                    if(data.pendapatan){
                        totalPendapatan += data.jumlah
                    }else{
                        totalPengeluaran += data.jumlah
                    }



                }

            }


            mutableMap["total"] = totalPendapatan - totalPengeluaran


            dataPerWaktu.add(mutableMap)

            dataPerWaktu.sortByDescending{ data -> data["tanggalAkhir"] as Long }


        }

        return dataPerWaktu
    }

    private fun getData(){
        mainViewModel.getCashflow(){
            arrayCashflow = it.toCollection(ArrayList())
            //ambil semua tanggal di cashflow
            arrayCashflow.forEach { data->

                if(!dataTanggal.contains(data.tanggal)){
                    dataTanggal.add(data.tanggal)
                }

            }
            //set data tanggal dari besar kecil 26,25,24,.. dan nanti array akan dipotong berdasarkan kebutuhan waktu
            dataTanggal.sortBy { data->data }



            //set awal jadikan harian
            prevWaktu = -1
            setAktifWaktu(0)

        }
    }

    /*private fun setData(data: Pair<Long, Long>) {
        if (this::arrayPesanan.isInitialized) {

            //reset hitung pendapatan

            pendapatan = 0.0

            val filtered = arrayListOf<Pesanan>()


            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            //ubah data menjadi time, tapi tanggal hanya hari bulan tahun tanpa jam
            val time1 = sdf.parse(untilMonth(Date(data.first)))!!.time
            val time2 = sdf.parse(untilMonth(Date(data.second)))!!.time

            arrayPesanan.filter{datax-> datax.status== "selesai"}.forEach { pesanan ->
                val pesananTime = sdf.parse(untilMonth(pesanan.tanggal.toDate()))!!.time

                if (pesananTime in time1..time2) {
                    filtered.add(pesanan)
                    pendapatan += pesanan.totalHarga!!
                }

            }

            adapterr.differ.submitList(filtered.sortedByDescending { t -> t.tanggal })


            binding.totalTv.text = "Pendapatan ${formatRupiah(pendapatan)}"

        }


    }*/

    private fun initRv() {

        adapterr = PembukuanAdapter{
            val nav = PembukuanFragmentDirections.actionPembukuanFragmentToFragmentDetailPembukuan(it.toLongArray())

            findNavController().navigate(nav)
        }

        binding.penghasilanRv.apply {
            adapter = adapterr
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }


    private fun setRangeDate(data: Pair<Long, Long>) {


        val tanggal1 = Date(data.first)
        val tanggal2 = Date(data.second)

        val calendar = Calendar.getInstance()

        calendar.time = tanggal1

        val hari1 = calendar.get(Calendar.DAY_OF_MONTH)
        val bulan1 = calendar.get(Calendar.MONTH)
        val bulanStr1 = bulan[bulan1]

        calendar.time = tanggal2

        val hari2 = calendar.get(Calendar.DAY_OF_MONTH)
        val bulan2 = calendar.get(Calendar.MONTH)
        val bulanStr2 = bulan[bulan1]


        val tahun = calendar.get(Calendar.YEAR)

        if (bulan1 == bulan2) {
            val text = "$hari1 - $hari2 $bulanStr2 $tahun"


        }

        // setData(data)

    }

    private fun dialogBukaToko() {
       dialogTambahCashFlow = DialogTambahCashFlow{ pair ->
            if(pair.second){
                mainViewModel.setPengeluaranOrPendapatan(pair.first as CashFlow){
                    if(it){
                        toastMaker(requireContext(), "berhasil buka restaurant").show()
                        getData()
                    }else{
                        toastMaker(requireContext(), "gagal buka restaurant").show()

                    }
                }
            }else{
                mainViewModel.setKeterangan(pair.first as Keterangan){
                    if(it){
                        toastMaker(requireContext(), "berhasil tambah nama karyawan").show()
                        dialogTambahCashFlow.getDataKeteranganAgain()
                    }else{
                        toastMaker(requireContext(), "gagal tambah nama karyawan").show()
                    }
                }
            }
        }

        dialogTambahCashFlow.tipe = 'd'





    }

}