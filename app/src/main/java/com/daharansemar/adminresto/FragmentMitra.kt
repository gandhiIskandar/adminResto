package com.daharansemar.adminresto

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daharansemar.adminresto.adapter.AdapterMitra
import com.daharansemar.adminresto.databinding.FragmentMitraBinding
import com.daharansemar.adminresto.repository.GenericInterface
import com.daharansemar.adminresto.viewModel.MainViewModel


class FragmentMitra : Fragment(), GenericInterface {

private lateinit var binding:FragmentMitraBinding
private lateinit var mContext:Context
private val viewModel : MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMitraBinding.inflate(layoutInflater)

        viewModel.getKategori()


        return binding.root
    }

    private fun init(){
        with(binding){
            rvMitra.adapter = AdapterMitra(this@FragmentMitra)
            rvMitra.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)

        }


    }

    private fun dataListener(){


        viewModel.kategoriData.observe(viewLifecycleOwner) {

            val data = it.filter { data -> data.tipe == "mitra" }

            viewModel.getAllMenu {



            }


        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun deleteData(item: Any) {

    }

    override fun editData(item: Any) {

    }

    override fun editSpesific(item: Any) {

    }
}