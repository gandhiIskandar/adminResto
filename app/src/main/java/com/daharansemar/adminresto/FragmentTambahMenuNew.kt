package com.daharansemar.adminresto

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daharansemar.adminresto.dataClass.Kategori
import com.daharansemar.adminresto.databinding.FragmentTambahMenuNewBinding
import com.daharansemar.adminresto.util.MenuBuilder
import com.daharansemar.adminresto.util.Utilization
import com.daharansemar.adminresto.util.Utilization.disableTemp
import com.daharansemar.adminresto.util.Utilization.formatRupiah
import com.daharansemar.adminresto.util.Utilization.hideAndShowToogle
import com.daharansemar.adminresto.util.Utilization.loadingDialog
import com.daharansemar.adminresto.util.Utilization.selectImage
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

typealias mb = MenuBuilder

class FragmentTambahMenuNew : Fragment() {

    private lateinit var binding: FragmentTambahMenuNewBinding
    private val viewModel: MainViewModel by viewModels()

    private var allKategori: List<Kategori> = listOf()

    private lateinit var arrayAc: Array<AutoCompleteTextView>

    private lateinit var arrayEt: ArrayList<EditText>

    private val menuBuilder: mb by lazy {
        MenuBuilder()
    }

    private lateinit var picData: ByteArray
    private var reqCode = 0
    private val judulArray: ArrayList<String?> = arrayListOf(null, null)

    private val argument: FragmentTambahMenuNewArgs by navArgs()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var arrayKelengkapan: MutableList<String?>

    private lateinit var dialogKelengkapan: DialogKelengkapan

    private lateinit var loadingDialog: AlertDialog

    private var rekom: Boolean = false
    private var promo: Boolean = false

    private var menuKey: String? = null

    private lateinit var handler:Handler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        handler = Handler(Looper.getMainLooper())

        rekom = false
        promo = false
        loadingDialog = loadingDialog(requireActivity())

        // Inflate the layout for this fragment
        arrayKelengkapan = mutableListOf()
        arrayKelengkapan.sortBy { data -> data }

        loadingDialog.show()



        init()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.kategoriData.observe(viewLifecycleOwner, Observer<MutableList<Kategori>> {

            allKategori = it

            val databaru =
                allKategori.filter { data -> data.tipe == "kelengkapan" }.map { data -> data.nama }

            if (arrayKelengkapan.size == 0) {
                arrayKelengkapan.addAll(databaru)
            } else {
                databaru.forEach { item ->
                    if (!arrayKelengkapan.contains(item)) {
                        arrayKelengkapan.add(item)
                    }
                }
            }

            initDialog(arrayKelengkapan)


            when (argument.tipe) {
                1 -> {
                    pilihMakanan(false)
                }
                2 -> {
                    pilihMinuman(false)
                }
                3 -> {
                    pilihPaket()
                }
            }

           handler.postDelayed({
               loadingDialog.dismiss()

           },1000)
            editCondition()

        })


        viewModel.judulMenu.observe(viewLifecycleOwner, Observer {

            binding.valJudulMenu.setText(it)
        })


    }


    private fun pilihMakanan(isPaket: Boolean) {
        if (!isPaket) {
            with(binding) {
                grupMinuman.visibility = View.GONE
                grupTipeMinuman.visibility = View.GONE
            }
        }
        val arrayMenu = filterData("menu")
        val arrayRasa = filterData("rasa")

        arrayAc[0].setAdapter(newAdapter(arrayMenu))
        arrayAc[1].setAdapter(newAdapter(arrayRasa))

        arrayAc[0].setOnItemClickListener { _, _, position, _ ->

            val item = "${arrayMenu[position]}*"
            CoroutineScope(Dispatchers.Default).launch {


                if (!arrayKelengkapan.any { data -> data?.last() == '*' }) {
                    arrayKelengkapan.add(item)
                } else {
                    val sameItem = arrayKelengkapan.single { data -> data?.last() == '*' }
                    arrayKelengkapan[arrayKelengkapan.indexOf(sameItem)] = item

                }

                dialogKelengkapan.insertData(arrayKelengkapan)


            }

            if (!isPaket) {
                CoroutineScope(Dispatchers.Default).launch {
                    setJudul(item)


                }
            }


        }
        arrayAc[1].setOnItemClickListener { _, _, position, _ ->

            val item = "${arrayRasa[position]}_"

            if (!isPaket) {
                CoroutineScope(Dispatchers.Default).launch {
                    setJudul(item)
                }
            }
        }
    }


    private fun pilihPaket() {
        pilihMakanan(true)
        pilihMinuman(true)
    }

    private suspend fun setJudul(text: String) =
        coroutineScope {

            if (text.last() == '*') {

                if (judulArray.any { data -> data?.last() == '*' }) {
                    val oldVal = judulArray.single { data -> data?.last() == '*' }
                    judulArray[judulArray.indexOf(oldVal)] = text


                } else {
                    judulArray.add(0, text)
                }


            } else if (text.last() == '_') {
                if (judulArray.any { data -> data?.last() == '_' }) {
                    val oldVal = judulArray.single { data -> data?.last() == '_' }
                    judulArray[judulArray.indexOf(oldVal)] = text

                } else {
                    judulArray.add(1, text)
                }

            }

            val fixedJudul = arrayListOf<String>()

            judulArray.filterNotNull().forEach {
                if (it.contains("[*_+]".toRegex())) {
                    fixedJudul.add(it.dropLast(1))
                }
            }



            viewModel.setJudul(fixedJudul.joinToString(" "))
        }


    private fun pilihMinuman(isPaket: Boolean) {
        if (!isPaket) {
            with(binding) {
                grupMenu.visibility = View.GONE
                grupRasa.visibility = View.GONE

                tvkelengkapan.visibility = View.GONE
                textInputLayout3.visibility = View.GONE
                valKeteranganMenu.setText("null")

            }
        }

        val arrayMinuman = filterData("minuman")

        arrayAc[2].setAdapter(newAdapter(arrayMinuman))
        arrayAc[3].setAdapter(newAdapter(filterData("tipe minuman")))

        arrayAc[2].setOnItemClickListener { _, _, position, _ ->

            val item = "${arrayMinuman[position]}."
            CoroutineScope(Dispatchers.Default).launch {


                if (!arrayKelengkapan.any { data -> data?.last() == '.' }) {
                    arrayKelengkapan.add(item)
                } else {
                    val sameItem = arrayKelengkapan.single { data -> data?.last() == '.' }
                    arrayKelengkapan[arrayKelengkapan.indexOf(sameItem)] = item

                }

                dialogKelengkapan.insertData(arrayKelengkapan)


            }


        }


    }


    private fun filterData(kategori: String): List<String?> {

        return allKategori.filter { data -> data.tipe == kategori }.map { data -> data.nama }

    }

    private fun collectAndInsertData() {

        loadingDialog.show()

        menuBuilder.key = menuKey ?: UUID.randomUUID().toString()



        menuBuilder.nama = arrayEt[0].text.toString()
        menuBuilder.harga = arrayEt[1].text.toString().toLong()
        menuBuilder.deskripsi = arrayEt[2].text.toString()
        menuBuilder.deskripsi1 = arrayEt[3].text.toString()
        menuBuilder.stok = arrayEt[4].text.toString().toLong()
        menuBuilder.rekom = rekom



        when (argument.tipe) {
            1 -> {
                menuBuilder.menuKey = arrayAc[0].text.toString()
                menuBuilder.rasaKey = arrayAc[1].text.toString()
                menuBuilder.tipe = "makanan"
            }
            2 -> {
                menuBuilder.minumanKey = arrayAc[2].text.toString()
                menuBuilder.tipeMinuman = arrayAc[3].text.toString()
                menuBuilder.tipe = "minuman"
            }
            else -> {
                menuBuilder.menuKey = arrayAc[0].text.toString()
                menuBuilder.rasaKey = arrayAc[1].text.toString()
                menuBuilder.minumanKey = arrayAc[2].text.toString()
                menuBuilder.tipeMinuman = arrayAc[3].text.toString()
                menuBuilder.tipe = "paket"
            }
        }

        if (promo) {
            menuBuilder.promo = true
            menuBuilder.potongan = binding.valPotonganMenu.text.toString().toLong()
        } else {
            menuBuilder.promo = false
            menuBuilder.potongan = 0L
        }

        val menu = menuBuilder.create()

        viewModel.insertMenu(menu, picData) {
            if (it) {
                loadingDialog.dismiss()
                toastMaker(requireActivity(), "tambah menu berhasil").show()
                findNavController().popBackStack()
            } else {
                loadingDialog.dismiss()
                toastMaker(requireActivity(), "tambah menu gagal").show()
            }
        }


    }


    private fun checkField(): Boolean {
        var pass = true
        arrayAc.forEachIndexed { idx, _ ->
            if (argument.tipe == 1) {
                if (idx < 2 && arrayAc[idx].text.isEmpty()) {
                    pass = false
                }

            } else if (argument.tipe == 2) {
                if (idx >= 2 && arrayAc[idx].text.isEmpty()) {
                    pass = false
                }
            } else {
                if (arrayAc[idx].text.isEmpty()) {
                    pass = false
                }
            }

        }

        arrayEt.forEach { editText ->

            if (editText.text.isEmpty()) {
                pass = false
            }

        }

        if (promo) {
            if (binding.valPotonganMenu.text!!.isEmpty()) {
                pass = false
            }
        }

        if (!::picData.isInitialized) {
            pass = false
        }

        return pass

    }

    private fun init() {

        binding = FragmentTambahMenuNewBinding.inflate(layoutInflater)

        viewModel.getKategori()

        resultLauncher = resultLauncer()

        with(binding) {
            arrayAc = arrayOf(
                autoCompleteMenu,
                autoCompleteRasa,
                autoCompleteMinuman,
                autoCompleteTipeMinuman
            )
            arrayEt = arrayListOf(
                valJudulMenu,
                valHargaMenu,
                valKeteranganMenu,
                valDeskripsiMenu,
                valStokMenu,
            )

            cardView2.setOnClickListener {

                selectImage(resultLauncher, requireContext()) {
                    reqCode = it
                }

            }

            /*  arrayEt[2].inputType = InputType.TYPE_NULL
            arrayEt[2].setOnClickListener {

               disableTemp(it)
               dialogKelengkapan.show(childFragmentManager, "Test123")
            }
          */




            btnTambahMenu.setOnClickListener {
                if (checkField()) {
                    collectAndInsertData()
                } else {
                    toastMaker(requireContext(), "lengkapi data terlebih dahulu").show()
                }
            }

            promoIv.setOnClickListener {
                disableEnable(!promo, it as ImageView)
                potonganetlayout.isEnabled = !promo
                promo = !promo

            }

            rekomIv.setOnClickListener {
                disableEnable(!rekom, it as ImageView)
                rekom = !rekom
            }

            btnTambahKlp.setOnClickListener {
                val navi = FragmentTambahMenuNewDirections.actionFragmentTambahMenuNewToKelengkapanFragment(argument.tipe)
                findNavController().navigate(navi)
            }

            var judul = ""
            when (argument.tipe) {
                1 -> judul = "makanan"
                2 -> judul = "minuman"
                3 -> judul = "paket"
            }

            btnTambahKlp.text = resources.getString(R.string.editgajelas, judul)

        }


    }

    private fun initDialog(data: List<String?>) {
        dialogKelengkapan = DialogKelengkapan {
            arrayEt[2].setText(it)
        }
        CoroutineScope(Dispatchers.Default).launch {
            dialogKelengkapan.insertData(data)
        }

    }

    private fun disableEnable(aktif: Boolean, view: ImageView) {
        if (aktif) {
            view.setImageDrawable(ColorDrawable(Color.parseColor("#FDD100")))
        } else {
            view.setImageDrawable(ColorDrawable(Color.LTGRAY))
        }
    }

    private fun newAdapter(list: List<String?>): ArrayAdapter<Any> {
        return ArrayAdapter(requireContext(), R.layout.textajalah, list)
    }

    private fun setImagetoByteArray(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        picData = data


    }

    private fun resultLauncer(): ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


            if (result.resultCode == Activity.RESULT_OK) {

                binding.tambahGambar.visibility = View.GONE


                if (reqCode == 10) {
                    val extras = result!!.data?.extras
                    val bitmap = extras!!.get("data") as Bitmap


                    Glide.with(requireContext())
                        .load(bitmap)
                        .into(binding.tambahMenuPicture)

                    setImagetoByteArray(bitmap)


                } else if (reqCode == 20) {
                    try {

                        val path = result!!.data?.data
                        val inputStream = context?.contentResolver!!.openInputStream(path!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null) {

                            Glide.with(requireContext())
                                .load(bitmap)
                                .into(binding.tambahMenuPicture)
                            setImagetoByteArray(bitmap)


                        }


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private fun editCondition() {
        if (argument.menu != null) {
            val menu = argument.menu!!

            with(binding) {
                Glide.with(requireContext()).asBitmap().load(menu.foto)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            tambahGambar.visibility = View.GONE

                            setImagetoByteArray(resource)
                            tambahMenuPicture.setImageBitmap(resource)

                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
                valJudulMenu.setText(menu.nama) // set judul
                valHargaMenu.setText(menu.harga.toInt().toString()) //set harga
                valKeteranganMenu.setText(menu.deskripsi) // set kelengkapan
                valDeskripsiMenu.setText(menu.deskripsi1)
                valStokMenu.setText(menu.stok.toString())
                menuKey = menu.key

                rekom = menu.rekom
                promo = menu.promo

                disableEnable(menu.rekom, rekomIv)

                if (menu.promo) {
                    disableEnable(true, promoIv)
                    potonganetlayout.isEnabled = true
                    valPotonganMenu.setText(menu.potongan.toInt().toString())
                }

                when (argument.tipe) {
                    1 -> {
                        arrayAc[0].setText(menu.menuKey, false)
                        arrayAc[1].setText(menu.rasaKey, false)
                    }
                    2 -> {
                        arrayAc[2].setText(menu.minumanKey, false)
                        arrayAc[3].setText(menu.tipeMinuman, false)
                    }
                    3 -> {
                        arrayAc[0].setText(menu.menuKey, false)
                        arrayAc[1].setText(menu.rasaKey, false)
                        arrayAc[2].setText(menu.minumanKey, false)
                        arrayAc[3].setText(menu.tipeMinuman, false)
                    }
                }


            }


        }
    }

}