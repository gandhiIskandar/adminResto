package com.daharansemar.adminresto.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.daharansemar.adminresto.dataClass.*
import com.daharansemar.adminresto.repository.DataStoreRepository
import com.daharansemar.adminresto.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.*


typealias listPesanan = List<Pesanan>

class MainViewModel : ViewModel() {


    private val repo = Repository.getInstance()

    //live data block
    private val _pesananData = MutableLiveData<listPesanan>()
    val pesananData: LiveData<listPesanan>
        get() = _pesananData


    private val _dataMenu = MutableLiveData<List<Menu>?>()
    val dataMenu: LiveData<List<Menu>?>
        get() = _dataMenu


    private val _statusInsertMenu = MutableLiveData<Boolean>()
    val statusInsertMenu: LiveData<Boolean>
        get() = _statusInsertMenu


    private val _statusUploadGambar = MutableLiveData<Pair<Uri, String>>()
    val statusUploadGambar: LiveData<Pair<Uri, String>>
        get() = _statusUploadGambar


    private val _statusUpdate = MutableLiveData<Boolean>()
    val statusUpdate: LiveData<Boolean>
        get() = _statusUpdate

    private val _tipeMenu = MutableLiveData<Char>()
    val tipeMenu: LiveData<Char>
        get() = _tipeMenu

    private val _kategoriData = MutableLiveData<MutableList<Kategori>>()
    val kategoriData: LiveData<MutableList<Kategori>>
        get() = _kategoriData

    private val _kelengkapan = MutableLiveData<List<Any>>()
    val kelengkapan: LiveData<List<Any>>
        get() = _kelengkapan

    private val _judulMenu = MutableLiveData<String>()
    val judulMenu: LiveData<String>
        get() = _judulMenu


    //data biasa block


    var prevTipeMenu: Char? = null

    private var saveMenu: Kategori? = null

    private var saveMinuman: Kategori? = null

    private var imageData: ByteArray? = null


    fun setTokenDataStore(token: String, invoker: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {

            repo.storeNewToken(token) {
                invoker.invoke(it)
            }
        }
    }


    fun setJudul(data: String) {
        _judulMenu.postValue(data)
    }

    fun setSaveMinuman(data: Kategori) {
        saveMinuman = data
    }

    fun getSaveMinuman(): Kategori? {
        return saveMinuman
    }


    fun setSaveMenu(data: Kategori?) {
        saveMenu = data
    }

    fun getSaveMenu(): Kategori? {
        return saveMenu
    }

    fun setImageData(data: ByteArray) {
        imageData = data
    }

    fun getImageData(): ByteArray? {
        return imageData
    }

    fun insertOrUpdateKategori(kategori: Kategori, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertOrUpdateKategori(kategori) {
                callback.invoke(it)

            }

        }
    }

    fun removeKategori(id:String, callback: (Boolean) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeKategori(id){
                callback.invoke(it)
            }
        }
    }


    fun setTipeMenu(tipe: Char) {
        _tipeMenu.value = tipe
    }

    fun getKeterangan(list: (List<Keterangan>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getKeterangan {

                list.invoke(it)

            }
        }
    }

    fun setKeterangan(keterangan: Keterangan, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setKeterangan(keterangan) {
                callback.invoke(it)
            }
        }
    }


    fun getPesanan() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getPesanan { data ->
                _pesananData.postValue(data)
            }

        }
    }


    fun updateCashflow(pair: Pair<String, Double>, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateCashflow(pair.first, pair.second) {
                callback.invoke(it)
            }
        }
    }


    fun hapusCashflow(id: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.hapusCashflow(id) {
                callback.invoke(it)
            }
        }
    }

    fun getImageData(callback:(List<ImageData>) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getImageData{
                callback.invoke(it)
            }
        }
    }

    fun setToken(data:String, callback:(Boolean)->Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repo.setToken(data){
                callback.invoke(it)
            }
        }
    }


    fun setPengeluaranOrPendapatan(cashFlow: CashFlow, invoker: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setPengeluaranOrPendapatan(cashFlow) {
                invoker.invoke(it)

            }
        }
    }

    fun getCashflow(invoker: (List<CashFlow>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCashflow {
                invoker.invoke(it)
            }
        }
    }

    fun hapusKeterangan(id: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.hapusKeterangan(id) {
                callback.invoke(it)
            }
        }
    }


    fun setKelengkapan(kelengkapan: Kategori, tambah: Boolean) {
        _kelengkapan.value = listOf(kelengkapan, tambah)
    }


    fun updatePesanan(status: String, id: String, total: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updatePesanan(status, id, total) { sukses ->
                _statusUpdate.postValue(sukses)
            }


        }
    }


    fun getKategori() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getKategori { data ->
                _kategoriData.postValue(data)

            }
        }
    }

    fun insertMenu(menu: Menu, data:ByteArray, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {

            repo.uploadImage(data, menu.key!!) { pair ->

                menu.foto = pair!!.first.toString()
                menu.key = pair.second

                viewModelScope.launch(Dispatchers.IO) {
                    repo.insertMenu(menu) {
                        callback.invoke(it)
                    }
                }


            }


        }

    }

    fun insertImageData(imageData:ImageData, data:ByteArray, callback: (Boolean) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repo.uploadImageData(data, imageData.id!!){
                imageData.imageUrl = it.toString()

                viewModelScope.launch(Dispatchers.IO) {

                    repo.setImageData(imageData){ issukses->
                        callback.invoke(issukses)
                    }

                }
                  }
        }
    }

    fun removeImageData(id:String, callback: (Boolean) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeImageData(id){
                callback.invoke(it)
            }
        }
    }

    fun uploadFoto(baos: ByteArray, judul: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.uploadImage(baos, judul) {
                _statusUploadGambar.postValue(it)
            }

        }
    }

    fun getAllMenu(callback: (List<Menu>?) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            repo.getMenu {
                callback.invoke(it)

            }
        }
    }

    fun editMenu(data: Menu, invoker: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateMenu(data) {
                invoker.invoke(it)
            }
        }
    }

    fun hapusMenu(id:String, callback:(Boolean)->Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repo.hapusMenu(id){
                callback.invoke(it)
            }
        }
    }
}