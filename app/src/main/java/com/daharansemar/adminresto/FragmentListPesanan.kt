package com.daharansemar.adminresto

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daharansemar.adminresto.adapter.PesananAdapter
import com.daharansemar.adminresto.dataClass.Pesanan
import com.daharansemar.adminresto.databinding.FragmentListPesananBinding
import com.daharansemar.adminresto.viewModel.MainViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class FragmentListPesanan : Fragment() {

    private lateinit var binding: FragmentListPesananBinding

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: PesananAdapter

    private lateinit var pesananArray: List<Pesanan>

    private var initialized = false

    private lateinit var handler: Handler



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentListPesananBinding.inflate(layoutInflater)

        viewModel.getPesanan()

        initRvPesanan()
        handler = Handler(Looper.getMainLooper())

        tabLayoutInit()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.pesananData.observe(viewLifecycleOwner, Observer { data ->
            pesananArray = data.sortedByDescending { x -> x.tanggal }

            adapter.differ.submitList(pesananArray.filter { datan -> datan.status != "selesai" && datan.status != "ditolak" })
            initialized = true


        })
    }

    private fun tabLayoutInit() {

        with(binding) {
            tabLayout.apply {
                addTab(newTab().setText("pesanan aktif"), 0)
                addTab(newTab().setText("pesanan selesai"), 1)

                this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {

                        when (tab?.position) {
                            0 -> {
                                if (initialized) {

                                    filterPesananAktif()
                                }


                            }
                            1 -> {
                                if (!initialized) {
                                    CoroutineScope(Dispatchers.Default).launch {
                                        do{
                                            if(initialized){
                                                filterPesananSelesai()

                                            }
                                        }while(!initialized)
                                    }
                                }else{
                                    filterPesananSelesai()
                                }
                            }
                        }


                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                })


            }
        }


    }

    private  fun filterPesananAktif() {

         adapter.differ.submitList(pesananArray.filter { data -> data.status != "selesai" && data.status != "ditolak" })
    }

    private fun filterPesananSelesai() {
        adapter.differ.submitList(pesananArray.filter { data -> data.status == "selesai" || data.status == "ditolak" })
    }

    private fun initRvPesanan() {

        binding.rvPesanan.apply {
            adapter = PesananAdapter { pesanan ->
                val navigasi =
                    FragmentListPesananDirections.actionFragmentListPesananToFragmentDetailPesanan(
                        pesanan
                    )

                findNavController().navigate(navigasi)
            }
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

        adapter = binding.rvPesanan.adapter as PesananAdapter


    }


}