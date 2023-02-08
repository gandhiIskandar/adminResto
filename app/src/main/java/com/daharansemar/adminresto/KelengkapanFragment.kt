package com.daharansemar.adminresto

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.adapter.DetailPembukuanAdapter
import com.daharansemar.adminresto.dataClass.Kategori
import com.daharansemar.adminresto.databinding.DialogEditAtauHapusBinding
import com.daharansemar.adminresto.databinding.DialogTambahKategoriBinding
import com.daharansemar.adminresto.databinding.FragmentKelengkapanBinding
import com.daharansemar.adminresto.util.Utilization
import com.daharansemar.adminresto.util.Utilization.customDialog
import com.daharansemar.adminresto.util.Utilization.loadingDialog
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class KelengkapanFragment : Fragment() {


    private lateinit var binding:FragmentKelengkapanBinding
    private lateinit var arrayRv:Array<RecyclerView>

    private lateinit var arrayBtn: Array<FloatingActionButton>

    private val argument : KelengkapanFragmentArgs by navArgs()

    private val viewModel:MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKelengkapanBinding.inflate(layoutInflater)

        viewModel.getKategori()

        init()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.kategoriData.observe(viewLifecycleOwner, Observer {
            splitData(it)
        })
    }

    private fun init(){
        with(binding){
            arrayRv = arrayOf(rvMenu, rvRasa, rvMinuman, rvJenisMinuman)
            arrayBtn = arrayOf( tambahMenu, tambahRasa, tambahMinuman, tambahJenisMinuman)

        }

        arrayBtn.forEach { btn ->
            btn.setOnClickListener { showDialogTambah(btn.contentDescription.toString()) }
        }

        initRv()

        when(argument.tipe){
            1->{
                binding.grupMinuman.visibility = View.GONE
            }
            2->{
                binding.grupMakanan.visibility = View.GONE
            }
        }
    }

    private fun showDialogTambah(tipe:String){

        val dialogBinding = DialogTambahKategoriBinding.inflate(layoutInflater,null,false)

       val dialog =  customDialog(requireContext(), dialogBinding.root)

        val loading =  loadingDialog(requireActivity())
        dialog.show()

        with(dialogBinding){
            dialogClose.setOnClickListener { dialog.dismiss() }
            judulTambahDialog.text = "tambah $tipe"
            tambahMenu.setOnClickListener {

                loading.show()

                if(etMenu.text.toString()==""){
                    toastMaker(requireContext(),"data tidak boleh kosong").show()
                }else{
                    val kategori = Kategori(nama = etMenu.text.toString(), tipe = tipe)

                    viewModel.insertOrUpdateKategori(kategori){


                        if(it){
                            loading.dismiss()
                            toastMaker(requireContext(), "tambah kelengkapan berhasil").show()
                            dialog.dismiss()
                            viewModel.getKategori()
                        }else{
                            toastMaker(requireContext(), "tambah kelengkapan gagal").show()
                        }
                    }
                }
            }


        }

    }


    private fun initRv(){
        arrayRv.forEach { data ->
            data.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            data.adapter = DetailPembukuanAdapter(false){
                dialogData(it.second as Kategori)

            }
        }
    }

    private fun splitData(list:List<Kategori>){
        arrayRv.forEach { data->
            val adapter = data.adapter as DetailPembukuanAdapter
            adapter.differ.submitList(list.filter{x -> x.tipe == data.contentDescription})
        }

    }

    private fun dialogData(pair: Kategori) {
        val binding = DialogEditAtauHapusBinding.inflate(layoutInflater, null, false)

        val dialog = customDialog(requireContext(), binding.root)
        dialog.show()

        with(binding) {
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            etEdit.inputType = InputType.TYPE_CLASS_TEXT

            etEdit.setText(pair.nama)

            btnEdit.setOnClickListener {
                if (etEdit.text!!.isEmpty()) {
                    toastMaker(requireContext(), "Mohon input jumlah").show()
                } else {
                    pair.nama = etEdit.text!!.toString()

                    viewModel.insertOrUpdateKategori(pair) {
                        if (it) {
                            toastMaker(requireContext(), "Update kelengkapan berhasil")
                                .show()
                            viewModel.getKategori()
                            dialog.dismiss()
                        } else {
                           toastMaker(requireContext(), "Update kelengkapan gagal")
                                .show()
                        }
                    }
                }
            }

            btnHapus.setOnClickListener {
                viewModel.removeKategori(pair.id!!) {
                    if (it) {
                       toastMaker(requireContext(), "Hapus kelengkapan berhasil").show()
                        viewModel.getKategori()
                        dialog.dismiss()

                    } else {
                       toastMaker(requireContext(), "Hapus kelengkapan gagal").show()

                    }
                }
            }
        }


    }


}