package com.daharansemar.adminresto

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.daharansemar.adminresto.adapter.AdapterTambahMenu
import com.daharansemar.adminresto.databinding.DialogKelengkapanBinding
import com.daharansemar.adminresto.util.Utilization.toastMaker
import kotlinx.coroutines.coroutineScope
import java.util.*

class DialogKelengkapan(private val callback: (String) -> Unit) : DialogFragment() {

    private lateinit var binding: DialogKelengkapanBinding
    private lateinit var adapter: AdapterTambahMenu

    private lateinit var listData: List<String?>
    private var kelengkapan: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = DialogKelengkapanBinding.inflate(layoutInflater)


        init()

        val builder = AlertDialog.Builder(requireContext()).setView(binding.root)

        val dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        return dialog

    }

    private fun init() {

        adapter = AdapterTambahMenu { text ->
            kelengkapan = text
            binding.textView2.text = text

        }
        binding.closeKelengkapan.setOnClickListener {
            dismiss()
        }
        binding.rvKelengkapan.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvKelengkapan.adapter = adapter

        binding.rvKelengkapan.setHasFixedSize(true)

        binding.simpanKelengkapan.setOnClickListener {
            if (kelengkapan != "") {
                callback.invoke(kelengkapan)
                dismiss()

            } else {
                toastMaker(requireContext(), "kelengkapan masih kosong").show()
            }
        }
        adapter.differ.submitList(listData)

    }

    suspend fun insertData(list: List<String?>) {
        coroutineScope {
            listData = list


        }

    }


}