package com.daharansemar.adminresto

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.daharansemar.adminresto.dataClass.Menu
import com.daharansemar.adminresto.databinding.EditDialogBinding
import com.daharansemar.adminresto.util.Utilization
import com.daharansemar.adminresto.util.Utilization.hideAndShowToogle
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel
import java.io.ByteArrayOutputStream
import java.io.IOException

class DialogEditMenu( private val callback:() -> Unit) : DialogFragment() {

    private lateinit var binding: EditDialogBinding

    var data: Menu? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var reqCode = 0

    private val viewModel: MainViewModel by viewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = EditDialogBinding.inflate(layoutInflater)

        resultLauncher = resultLauncer()

        val builder = AlertDialog.Builder(requireContext()).setView(binding.root)
        val dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        init(data!!)

        return dialog


    }


    private fun init(data: Menu) {


        lateinit var arrayEt: Array<EditText>



        with(binding) {

            arrayEt = arrayOf(
                valJudulMenu,
                valHargaMenu,
                valKeteranganMenu,
                valDeskripsiMenu,
                valPotonganMenu,
                valStokMenu,
            )

            Glide.with(requireContext()).load(data.foto).into(editMenuPicture) //set gambar


            arrayEt[0].setText(data.nama)
            arrayEt[1].setText(data.harga.toString())
            arrayEt[2].setText(data.deskripsi)
            arrayEt[3].setText(data.deskripsi1)
            arrayEt[5].setText(data.stok.toString())

            editMenuPicture.setOnClickListener {
                Utilization.selectImage(resultLauncher, requireContext()) {
                    reqCode = it
                }
            }



            viewModel.statusUploadGambar.observe(requireParentFragment().viewLifecycleOwner, Observer {
                if (it != null) {

                    data.nama = arrayEt[0].text.toString()
                    data.harga = arrayEt[1].text.toString().toLong()
                    data.deskripsi = arrayEt[2].text.toString()
                    data.deskripsi1 = arrayEt[3].text.toString()
                    data.stok = arrayEt[5].text.toString().toLong()
                    data.foto = it.first.toString()

                    viewModel.editMenu(data) { berhasil ->
                        if (berhasil) {
                            dismiss()

                            toastMaker(requireContext(), "Edit data berhasil").show()
                            callback.invoke()


                        } else {
                            toastMaker(requireContext(), "Edit data gagal").show()
                        }
                    }


                }
            })

            simpanData.setOnClickListener {


                if (data.promo) {
                    if (arrayEt[4].text.toString() == "") {
                        toastMaker(
                            requireContext(),
                            "isi potongan harga terlebih dahulu"
                        ).show()
                    } else {
                        data.potongan = arrayEt[4].text.toString().toLong()
                        if(viewModel.getImageData()==null){
                            data.nama = arrayEt[0].text.toString()
                            data.harga = arrayEt[1].text.toString().toLong()
                            data.deskripsi = arrayEt[2].text.toString()
                            data.deskripsi1 = arrayEt[3].text.toString()
                            data.stok = arrayEt[5].text.toString().toLong()


                            viewModel.editMenu(data) { berhasil ->
                                if (berhasil) {
                                    dismiss()

                                    toastMaker(requireContext(), "Edit data berhasil").show()
                                    callback.invoke()


                                } else {
                                    toastMaker(requireContext(), "Edit data gagal").show()
                                }
                            }
                        }else{

                            viewModel.uploadFoto(
                                viewModel.getImageData()!!,
                                data.key!!
                            )
                        }


                    }
                } else {
                    data.potongan = arrayEt[4].text.toString().toLong()
                    if(viewModel.getImageData()==null){
                        data.nama = arrayEt[0].text.toString()
                        data.harga = arrayEt[1].text.toString().toLong()
                        data.deskripsi = arrayEt[2].text.toString()
                        data.deskripsi1 = arrayEt[3].text.toString()
                        data.stok = arrayEt[5].text.toString().toLong()


                        viewModel.editMenu(data) { berhasil ->
                            if (berhasil) {
                                dismiss()

                                toastMaker(requireContext(), "Edit data berhasil").show()
                                callback.invoke()


                            } else {
                                toastMaker(requireContext(), "Edit data gagal").show()
                            }
                        }
                    }else{

                        viewModel.uploadFoto(
                            viewModel.getImageData()!!,
                            data.key!!
                        )
                    }


                }

            }


            rekomendasiCheckBox.setOnCheckedChangeListener { _, isChecked ->

                data.rekom = isChecked
            }

            promoCheckBox.setOnCheckedChangeListener { _, isChecked ->
                hideAndShowToogle(grupPotongan, isChecked)

                data.promo = isChecked

                if (!isChecked) {
                    data.potongan = 0
                }

            }


            if (data.promo) {

                promoCheckBox.isChecked = true

                grupPotongan.visibility = View.VISIBLE
                valPotonganMenu.apply {
                    visibility = View.VISIBLE
                    setText(data.potongan.toString())
                }


            }

            if (data.rekom) {
                rekomendasiCheckBox.isChecked = true
            }

        }


    }


    private fun resultLauncer(): ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


            if (result.resultCode == Activity.RESULT_OK) {


                if (reqCode == 10) {
                    val extras = result!!.data?.extras
                    val bitmap = extras!!.get("data") as Bitmap


                    Glide.with(requireContext())
                        .load(bitmap)
                        .into(binding.editMenuPicture)

                    setImagetoByteArray(bitmap)


                } else if (reqCode == 20) {
                    try {
                        Log.d("requCd", "masukk1")
                        val path = result!!.data?.data
                        val inputStream = context?.contentResolver!!.openInputStream(path!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null) {

                            Glide.with(requireContext())
                                .load(bitmap)
                                .into(binding.editMenuPicture)
                            setImagetoByteArray(bitmap)


                        }


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private fun setImagetoByteArray(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        viewModel.setImageData(data)


    }

}