package com.daharansemar.adminresto

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.OverScroller
import android.widget.Scroller
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ScrollingView
import androidx.core.view.contains
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daharansemar.adminresto.adapter.AdapterMenu
import com.daharansemar.adminresto.dataClass.Menu
import com.daharansemar.adminresto.databinding.DialogPilihanMenuBinding
import com.daharansemar.adminresto.databinding.FragmentListMenuBinding
import com.daharansemar.adminresto.util.Utilization
import com.daharansemar.adminresto.util.Utilization.customDialog
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel
import com.google.android.material.button.MaterialButton


class FragmentListMenu : Fragment() {
    private lateinit var binding: FragmentListMenuBinding

    private var util = Utilization

    private val viewModel: MainViewModel by viewModels()

    private lateinit var menuArray: ArrayList<String>

    private lateinit var arrayRv: ArrayList<RecyclerView>

    private lateinit var allMenuArray: List<Menu>

    private lateinit var rvHasilSearch: RecyclerView
    private lateinit var layoutHasilSearch: LinearLayout

    private lateinit var arrayRv1:ArrayList<Pair<LinearLayout, RecyclerView>>

    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arrayRv1 = arrayListOf()
        arrayRv = arrayListOf()
        binding = FragmentListMenuBinding.inflate(inflater)
        //init recyclerv

        initSearchLayout()
        binding.tambahMenu.setOnClickListener {

            var tipe = 0

            val binding = DialogPilihanMenuBinding.inflate(layoutInflater)

            val dialog = customDialog(requireContext(), binding.root)

            binding.goToMakanan.setOnClickListener {
                tipe = 1
                goToTambahMenu(tipe)

                dialog.dismiss()
            }
            binding.goToMinuman.setOnClickListener {
                tipe = 2
                goToTambahMenu(tipe)

                dialog.dismiss()
            }
            binding.goToPaket.setOnClickListener {
                tipe = 3
                goToTambahMenu(tipe)

                dialog.dismiss()
            }
            binding.closeDialog.setOnClickListener { dialog.dismiss() }


            dialog.show()


        }

        getData(false)



        return binding.root
    }

    private fun goToTambahMenu(tipe: Int) {
        val navi = FragmentListMenuDirections.actionFragmentListMenuToFragmentTambahMenuNew(tipe)
        findNavController().navigate(navi)

    }

    private fun konfirmasihapusMenu(menu: Menu) {

        val alertBuilder = AlertDialog.Builder(mContext)

        alertBuilder.setMessage("hapus menu \"${menu.nama}\" ?")
            .setPositiveButton("ya") { dialog, _ ->
                viewModel.hapusMenu(menu.key!!){
                    if(it){
                        toastMaker(requireContext(), "hapus menu berhasil").show()
                        getData(true)
                    }else{
                        toastMaker(requireContext(), "hapus menu gagal").show()

                    }

                    dialog.dismiss()
                }
            }
            .setNegativeButton("tidak") { dialog, _ ->

                dialog.dismiss()

            }
            .setCancelable(false)

        val alertDialog = alertBuilder.create()

        alertDialog.show()


    }


    private fun addViewGroup(rv: RecyclerView, menu: String): LinearLayout {

        val linearLayout = LinearLayout(mContext)
        linearLayout.orientation = LinearLayout.VERTICAL
        val vgParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        vgParam.width = LinearLayout.LayoutParams.MATCH_PARENT
        vgParam.height = LinearLayout.LayoutParams.WRAP_CONTENT

        linearLayout.layoutParams = vgParam


        val tv = TextView(mContext)

        val tf = ResourcesCompat.getFont(mContext, R.font.centurygothic)

        tv.setTypeface(tf, Typeface.BOLD)
        linearLayout.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10))

        tv.text = menu

        tv.textSize = 18f
        tv.setTextColor(Color.BLACK)

        linearLayout.addView(
            tv
        )
        linearLayout.addView(
            rv
        )




        return linearLayout

    }

    private fun newRv(): RecyclerView {
        val rv = RecyclerView(mContext)

        val rvParam = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

        rv.layoutParams = rvParam
        rv.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

        val adapter = AdapterMenu {
           if(it.second) {
                var tipe = 0

                when (it.first.tipe) {
                    "makanan" -> tipe = 1
                    "minuman" -> tipe = 2
                    "paket" -> tipe = 3
                }

                val navi =
                    FragmentListMenuDirections.actionFragmentListMenuToFragmentTambahMenuNew(tipe)
                navi.menu = it.first
                findNavController().navigate(navi)
            }else{
                konfirmasihapusMenu(it.first)
            }
        }


        rv.adapter = adapter




        rv.isNestedScrollingEnabled = false
        rv.overScrollMode = View.OVER_SCROLL_NEVER

        return rv
    }


    private fun dpToPx(dp: Int): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun generateRvMakanan() {

        menuArray.sortedBy { x -> x }.forEach { data ->

            val rv = newRv()

            rv.contentDescription = data

            arrayRv.add(rv)


            val linearLayout = addViewGroup(rv, data)

            binding.linearUtam.addView(
                linearLayout
            )


        }

    }

    private fun generateRvMinuman() {
        val rv = newRv()

        rv.contentDescription = "minuman"

       val linear = addViewGroup(rv, "minuman")



        arrayRv1.add(Pair(linear, rv))

    }

    private fun updateRvData() {
        arrayRv.forEach { data ->

            val adapter = data.adapter as AdapterMenu

            adapter.differ.submitList(allMenuArray.filter { x -> x.menuKey == data.contentDescription && x.stok>0 })

            val linear = data.parent as LinearLayout

            if(binding.linearUtam.contains(linear) && !allMenuArray.any{x -> x.menuKey == data.contentDescription && x.stok>0 }){
                binding.linearUtam.removeView(linear)
            }


        }

    }

    private fun updateRvDataNonMakanan(){

        arrayRv1.forEach { data->

            var condition = !allMenuArray.any { x -> x.tipe == data.second.contentDescription && x.stok>0 }
            var list = allMenuArray.filter{x -> x.tipe == data.second.contentDescription && x.stok>0}

            if(data.second.contentDescription == "stok habis"){
                condition = !allMenuArray.any { x -> x.stok <= 0 }
                list = allMenuArray.filter{x -> x.stok <= 0}

            }

                val adapter = data.second.adapter as AdapterMenu
                adapter.differ.submitList(list)



                if(!binding.linearUtam.contains(data.first) && adapter.differ.currentList.size>0){
                    binding.linearUtam.addView(data.first)
                }else if(binding.linearUtam.contains(data.first) && condition){

                    binding.linearUtam.removeView(data.first)
                }



        }

    }

    private fun generateRvPaket() {
        val rv = newRv()

        rv.contentDescription = "paket"

        val linear = addViewGroup(rv, "paket")

        arrayRv1.add(Pair(linear, rv))

    }

    private fun generateRvStokHabis(){

        val rv= newRv()

        rv.contentDescription = "stok habis"

        val linear = addViewGroup(rv, rv.contentDescription.toString())

        arrayRv1.add(Pair(linear, rv))

    }


    private fun getData(update:Boolean) {
        viewModel.getAllMenu {

            if (it != null) {

                menuArray = arrayListOf()

                allMenuArray = it

                allMenuArray.forEach { data ->

                    if (!menuArray.contains(data.menuKey)) {
                        if (data.menuKey != null) {
                            menuArray.add(data.menuKey!!)
                        }

                    }

                }

             if(!update) {
                 generateRvMakanan()
                 generateRvMinuman()
                 generateRvPaket()
                 generateRvStokHabis()
                 updateRvData()
                 updateRvDataNonMakanan()
                 searchViewListener()
             }else{
                 updateRvData()
                 updateRvDataNonMakanan()
             }


            } else {
                util.toastMaker(mContext, "Gagal mengambil data").show()
            }
        }

    }

    private fun initSearchLayout() {
        rvHasilSearch = newRv()
        layoutHasilSearch = addViewGroup(rvHasilSearch, "hasil pencarian")
        layoutHasilSearch.visibility = View.GONE

        binding.linearUtam.addView(
            layoutHasilSearch
        )
    }


    private fun searchViewListener() {

        val adapter = rvHasilSearch.adapter as AdapterMenu

        val listener: androidx.appcompat.widget.SearchView.OnQueryTextListener =
            object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText != "") {

                        layoutHasilSearch.visibility = View.VISIBLE

                        adapter.differ.submitList(allMenuArray.filter { data ->
                            data.nama!!.contains(
                                newText!!,
                                true
                            )
                        })

                        val tv = layoutHasilSearch.getChildAt(0) as TextView

                        tv.text = resources.getString(R.string.hasil_pencarian, newText)

                        if (arrayRv.size !=0) {
                            arrayRv.forEach { rv ->
                                val linear = rv.parent as LinearLayout
                                linear.visibility = View.GONE
                            }
                        }

                        if(arrayRv1.size != 0){
                            arrayRv1.forEach { rv ->
                                val linear = rv.first
                                linear.visibility = View.GONE
                            }

                        }


                    } else {

                        if(arrayRv.size != 0){
                            arrayRv.forEach { rv ->
                                val linear = rv.parent as LinearLayout
                                linear.visibility = View.VISIBLE
                            }

                        }

                        if(arrayRv1.size != 0){
                            arrayRv1.forEach { rv ->
                                val linear = rv.first
                                linear.visibility = View.VISIBLE
                            }

                        }


                        layoutHasilSearch.visibility = View.GONE
                    }


                    return true
                }
            }

        binding.menuSearch.setOnQueryTextListener(listener)
    }
}

