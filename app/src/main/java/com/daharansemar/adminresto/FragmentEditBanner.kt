package com.daharansemar.adminresto

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daharansemar.adminresto.adapter.BannerAdapter
import com.daharansemar.adminresto.dataClass.ImageData
import com.daharansemar.adminresto.databinding.FragmentEditBannerBinding
import com.daharansemar.adminresto.repository.BannerInterface
import com.daharansemar.adminresto.util.Utilization.loadingDialog
import com.daharansemar.adminresto.util.Utilization.selectImage
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class FragmentEditBanner : Fragment(), BannerInterface {

    private var reqCode = 0

    private lateinit var binding: FragmentEditBannerBinding

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var adapter: BannerAdapter

    private lateinit var arrayImageTambah: ArrayList<Pair<ImageData, ByteArray?>>

    private lateinit var arrayImageHapus:ArrayList<String>
    private lateinit var loadingdialog:AlertDialog

    private var currentImageData:ImageData?=null



    private val viewModel: MainViewModel by viewModels()
    private lateinit var mContext:Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        resultLauncher = resultLauncer()
        adapter = BannerAdapter(this)

        loadingdialog = loadingDialog(requireActivity())
        arrayImageTambah = arrayListOf()

        arrayImageHapus = arrayListOf()

        binding = FragmentEditBannerBinding.inflate(inflater)

        binding.rvBanner.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvBanner.adapter = adapter

        binding.tambahBanner.setOnClickListener {
            selectImage(resultLauncher, mContext){
                reqCode=it
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

     getData()
    }
    private fun getData(){

        viewModel.getImageData {

            it.forEach { data ->
                arrayImageTambah.add(Pair(data, null))
            }



            adapter.differ.submitList(it.sortedBy { data->data.time })

        }

    }

    private fun resultLauncer(): ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


            if (result.resultCode == Activity.RESULT_OK) {


                if (reqCode == 10) {
                    val extras = result!!.data?.extras
                    val bitmap = extras!!.get("data") as Bitmap




                    setImagetoByteArray(bitmap)


                } else if (reqCode == 20) {
                    try {

                        val path = result!!.data?.data
                        val inputStream = context?.contentResolver!!.openInputStream(path!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null) {


                            setImagetoByteArray(bitmap)


                        }


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    private fun setImagetoByteArray(bitmap: Bitmap) {

        loadingdialog.show()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val imageData = currentImageData?:newBanner()

      viewModel.insertImageData(imageData,data){succed->
          currentImageData = if(succed){
              getData()
              loadingdialog.dismiss()
              toastMaker(mContext, "berhasil tambah poster").show()
              adapter.notifyDataSetChanged()
              null

          }else{
              toastMaker(mContext, "gagal tambah poster").show()
              loadingdialog.dismiss()
              null
          }
      }

    }

    override fun editImageData(imageData: ImageData) {

        currentImageData = imageData

        selectImage(resultLauncher, mContext){
            reqCode=it
        }

    }

    private fun newBanner():ImageData{
        return ImageData(id= UUID.randomUUID().toString(), time = Timestamp.now())
    }

    override fun deleteImageData(id: String) {
        loadingdialog.show()
        viewModel.removeImageData(id){
            if(it){
                loadingdialog.dismiss()
                getData()
                toastMaker(mContext,"poster berhasil dihapus").show()

            }else{
                loadingdialog.dismiss()
                getData()
                toastMaker(mContext,"poster gagal dihapus").show()
            }
        }



    }


}